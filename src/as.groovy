/*-----------------------
    ГИС ЖКХ getState
    (загрузка содержания обращений)
    ver. 2.0
    2021-08-03
------------------------*/
import javax.xml.soap.*
import java.nio.charset.Charset
import javax.xml.transform.TransformerFactory
import javax.xml.transform.Transformer
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder
import org.w3c.dom.Document
import java.util.zip.GZIPInputStream
import org.xml.sax.InputSource
import javax.xml.transform.OutputKeys
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.LocalDate
//import javax.mail.internet.MimeUtility
import groovy.json.JsonBuilder



// Константы
appNameSpace = 'app'
baseNameSpace = 'base'

root = utils.get('root', [:])
debug = utils.containsAttribute(root, 'gishcsDebug')? root.gishcsDebug : true
orgPPAGUID = utils.containsAttribute(root, 'gishcsOrgGUID')? root.gishcsOrgGUID : 'a113a6f6-4a6e-4238-8554-831f73d38958'
gishcsAuth = utils.containsAttribute(root, 'gishcsAuth')? root.gishcsAuth.bytes.encodeBase64().toString() : 'sit:xw{p&&Ee3b9r8?amJv*]'.bytes.encodeBase64().toString()
sHost = utils.containsAttribute(root, 'gishcsHost')? root.gishcsHost : 'http://sit02.dom.test.gosuslugi.ru:10082'
gishcsUseCryptoService = utils.containsAttribute(root, 'gishcsUseCrypt')? root.gishcsUseCrypt : false
gishcsSignService = utils.containsAttribute(root, 'gishcsSignServ')? root.gishcsSignServ : 'http://172.21.1.2:8081/sign'

if (!debug) logger.info('Старт скрипта getState сервиса обмена сообщениями с ГИС ЖКХ')

def config = [:]
config.orgPPAGUID = orgPPAGUID
config.gishcsAuth = gishcsAuth
config.sHost = sHost
config.gishcsUseCryptoService

final service = 'ext-bus-appeals-service/services/AppealsAsync'

def stComplete = utils.findFirst('status',['code':'complete'])
def stProgress = utils.findFirst('status',['code':'progress'])
def stError = utils.findFirst('status',['code':'error'])
def stCanceled = utils.findFirst('status',['code':'canceled'])

// Проверяем готовность крипто туннеля РТК
def signError = checkService(sHost, gishcsSignService, gishcsUseCryptoService)

def exportAppeal = utils.find('Integration$exportAppeal', ['status' : op.not(stComplete, stCanceled)])

exportAppeal.each() { _exportAppeal ->
    try {
        if (signError) {
            utils.edit(_exportAppeal, ['status':stError,'faultString': signError])
            return  // переходим к следующему элементму each()
        }

        def messageUUID = UUID.randomUUID().toString()
        def messageGUID = _exportAppeal.ResponceGUID    // GUID обрабатываемого ответа
        println("Выполнение запроса getState ГИС ЖКХ. Request. MessageGUID = " + messageGUID)
        def surl = new URL("${sHost}//${service}")

        //println("Request messageUUID = " + messageUUID)
        SOAPMessage soapRequest = createSoapGISMessage(messageUUID, 'getState')

        // BODY
        SOAPPart soapPart = soapRequest.getSOAPPart()
        SOAPEnvelope envelope = soapPart.getEnvelope()
        SOAPBody soapBody = envelope.getBody()
        SOAPElement bodyStateRequest = soapBody.addChildElement('getStateRequest', baseNameSpace)
        SOAPElement bodyMessageGUID = bodyStateRequest.addChildElement('MessageGUID', baseNameSpace)
        bodyMessageGUID.addTextNode(messageGUID)

        // сохранение
        if (soapRequest.saveRequired()) soapRequest.saveChanges()

        //println("\nSOAP Request:\n" + getSOAPAsString(soapRequest))

        println("\n... Connect to ${surl} \n")
        // Cоздаем соединение
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance()
        SOAPConnection connection = soapConnFactory.createConnection()

        //Отправка сообщения
        SOAPMessage reply = connection.call(soapRequest, surl)
        //Закрываем соединение
        connection.close()
        // ------------------------------------------

        // Unzip ответа, если необходимо
        SOAPMessage soapResponce = getAndUnzipSOAPMessage(reply)

        // Полученный ответ (Debug)
        println("\nsoapResponce:\n" + getSOAPAsString(soapResponce))

        // ------- Анализ ответа  -------
        SOAPPart responceSOAPPart = soapResponce.getSOAPPart()
        SOAPEnvelope responceEnvelope = responceSOAPPart.getEnvelope()
        SOAPBody responceBody = responceEnvelope.getBody()

        // Проверка на возврат ошибки <env:Fault>
        SOAPBodyElement fault = getChildElements(responceBody, 'ChildElement', 'Fault')[0]
        if (fault) {
            // ------- ошибка в полученном ответе ---------
            def faultstring = getChildElements( getChildElements(fault, 'ChildElement', 'faultstring')[0] )
            println("Сервер ГИС ЖКХ вернул текст ошибки faultstring: " + faultstring)
            utils.edit(_exportAppeal, ['status':stError,'faultString':faultstring])
        } else {
            // ------- Разбор сообщения -------
            def stateResult = [:]
            // Получение структуры getStateResult
            SOAPBodyElement getStateResult = getChildElements(responceBody, 'ChildElement', 'getStateResult')[0]

            // RequestState (Статус обработки сообщения в асинхронном обмене) (1 - получено; 2 - в обработке; 3 - обработано)
            stateResult.requestState = getChildElements( getChildElements(getStateResult, 'ChildElement', 'RequestState')[0] )[0]
            println('requestState = ' + stateResult.requestState)

            switch (stateResult.requestState) {
                case ['1','2']: // получено или в обработке
                    utils.edit(_exportAppeal.UUID, ['status':stProgress, 'faultString':''])
                    break
                case '3': // обработано
                    // Описание ошибки, если есть
                    stateResult.errorMessage = [:]
                    SOAPBodyElement errorMessage = getChildElements(getStateResult, 'ChildElement', 'ErrorMessage')[0]
                    stateResult.errorMessage.errorCode = getChildElements( getChildElements(errorMessage, 'ChildElement', 'ErrorCode')[0] )[0]  // INT002012
                    stateResult.errorMessage.description = getChildElements( getChildElements(errorMessage, 'ChildElement', 'Description')[0] )[0]  // Нет объектов для экспорта
                    //println("ErrorMessage.ErrorCode = ${stateResult.errorMessage.errorCode}\tErrorMessage.Description = ${stateResult.errorMessage.description}")

                    // Получение структуры ExportAppealResult (само обращение)
                    stateResult.exportAppeal = []

                    def appeal = [:]

                    SOAPBodyElement[] exportAppealResult = getChildElements(getStateResult, 'ChildElement', 'ExportAppealResult')
                    exportAppealResult.each { it ->

                        // AppealGUID (Идентификатор обращения в системе ГИС ЖКХ)
                        appeal.appealGUID = getChildElements( getChildElements(it, 'ChildElement', 'AppealGUID')[0] )[0]

                        // ParentAppealGUID (необязательное)
                        appeal.parentAppealGUID = getChildElements( getChildElements(it, 'ChildElement', 'ParentAppealGUID')[0] )[0]

                        // ApplicantInfo (Заявитель обращения)
                        SOAPBodyElement applicantInfo = getChildElements(it, 'ChildElement', 'ApplicantInfo')[0]

                        // orgRootEntityGUID - Идентификатор корневой сущности организации в реестре организаций ГИС ЖКХ
                        SOAPBodyElement org = getChildElements(applicantInfo, 'ChildElement', 'Org')[0]
                        appeal.orgRootEntityGUID = getChildElements( getChildElements(org, 'ChildElement', 'orgRootEntityGUID')[0] )[0]

                        def _person = [:]
                        // Person (расширение) Физическое лицо
                        SOAPBodyElement person = getChildElements(applicantInfo, 'ChildElement', 'Person')[0]
                        // Surname Фамилия
                        _person.surname = getChildElements( getChildElements(person, 'ChildElement', 'Surname')[0] )[0]
                        // FirstName Имя
                        _person.firstName = getChildElements( getChildElements(person, 'ChildElement', 'FirstName')[0] )[0]
                        // Patronymic Отчество (необязательное)
                        _person.patronymic = getChildElements( getChildElements(person, 'ChildElement', 'Patronymic')[0] )[0]
                        appeal.person = _person
                        //println("Surname: ${appeal.person.surname}\tFirstName: ${appeal.person.firstName}\tPatronymic: ${appeal.person.patronymic}")

                        // PostAddress (Почтовый адрес) (необязательное)
                        appeal.postAddress = getChildElements( getChildElements(applicantInfo, 'ChildElement', 'PostAddress')[0] )[0]
                        //println("PostAddress = ${appeal.postAddress}")

                        // ApartmentNumber (Номер помещения) (необязательное)
                        appeal.apartmentNumber = getChildElements( getChildElements(applicantInfo, 'ChildElement', 'ApartmentNumber')[0] )[0]

                        // Email  (Электронная почта) (необязательное)
                        appeal.email = getChildElements( getChildElements(applicantInfo, 'ChildElement', 'Email')[0] )[0]

                        // PhoneNumber (Номер телефона) (необязательное)
                        appeal.phoneNumber = getChildElements( getChildElements(applicantInfo, 'ChildElement', 'PhoneNumber')[0] )[0]

                        // AppealNumber (Номер обращения)
                        appeal.appealNumber = getChildElements( getChildElements(it, 'ChildElement', 'AppealNumber')[0] )[0]

                        // AppealCreateDate (Дата создания обращения)
                        appeal.appealCreateDate = getChildElements( getChildElements(it, 'ChildElement', 'AppealCreateDate')[0] )[0]

                        // AppealTopic Тема обращения (НСИ 220)
                        _topic = [:]
                        SOAPBodyElement appealTopic = getChildElements(it, 'ChildElement', 'AppealTopic')[0]
                        // Code (Код записи справочника)
                        _topic.code = getChildElements( getChildElements(appealTopic, 'ChildElement', 'Code')[0] )[0]
                        // GUID (Идентификатор записи в соответствующем справочнике ГИС ЖКХ)
                        _topic.guid = getChildElements( getChildElements(appealTopic, 'ChildElement', 'GUID')[0] )[0]
                        // Name  (Значение) (необязательное)
                        _topic.name = getChildElements( getChildElements(appealTopic, 'ChildElement', 'Name')[0] )[0]
                        appeal.topic = _topic

                        // AnotherTopic (Другая тема обращения)
                        appeal.anotherTopic = getChildElements( getChildElements(it, 'ChildElement', 'AnotherTopic')[0] )[0]

                        // FIASHouseGuid (Глобальный уникальный идентификатор дома по ФИАС) (необязательное)
                        appeal.fiasHouseGuid = getChildElements( getChildElements(it, 'ChildElement', 'FIASHouseGuid')[0] )[0]
                        //println('FIASHouseGuid = ' + appeal.fiasHouseGuid)

                        // OKTMO
                        _oktmo = [:]
                        SOAPBodyElement oktmo = getChildElements(it, 'ChildElement', 'OKTMO')[0]
                        // code (Код по ОКТМО)
                        _oktmo.code = getChildElements( getChildElements(oktmo, 'ChildElement', 'code')[0] )[0]
                        // name (Полное наименование)
                        _oktmo.name = getChildElements( getChildElements(oktmo, 'ChildElement', 'name')[0] )[0]
                        appeal.oktmo = _oktmo
                        //println("ОКТМО> code: ${appeal.oktmo.code} \tname: ${appeal.oktmo.name}")

                        // AppealText (Текст обращения)
                        appeal.appealText = getChildElements( getChildElements(it, 'ChildElement', 'AppealText')[0] )[0]

                        // Attachment (Прикрепленные файлы) (необязательное)
                        def _attachment = []
                        def attachment = getChildElements(it, 'ChildElement', 'Attachment')
                        println("Всего вложений - " + attachment.size())
                        attachment.each {value ->
                            def _fl = [:]
                            // Name (Наименование вложения)
                            _fl.name = getChildElements( getChildElements(value, 'ChildElement', 'Name')[0] )[0]
                            // Description (Описание вложения)
                            _fl.description = getChildElements( getChildElements(value, 'ChildElement', 'Description')[0] )[0]
                            // Attachment (Вложение) - для наум
                            SOAPBodyElement _att = getChildElements(value, 'ChildElement', 'Attachment')[0]
                            // AttachmentGUID (Идентификатор сохраненного вложения)
                            _fl.attachmentGUID = getChildElements( getChildElements(_att, 'ChildElement', 'AttachmentGUID')[0] )[0]
                            // AttachmentHASH (Хэш-тег вложения по алгоритму ГОСТ в binhex
                            //_fl.attachmentHASH = getChildElements( getChildElements(value, 'ChildElement', 'AttachmentHASH')[0] )[0]
                            _attachment.add(_fl)
                        }
                        appeal.attachment = _attachment
                        _attachment = null

                        // AppealStatus (Статус обращения) Send - Отправлено/Получено; Executed - Исполнено; Withdrawn - Отозвано
                        appeal.appealStatus = getChildElements( getChildElements(it, 'ChildElement', 'AppealStatus')[0] )[0]

                        // Redirected 	boolean (Перенаправлено в другую организацию) (необязательное)
                        appeal.redirected = (getChildElements( getChildElements(it, 'ChildElement', 'AnswererGUID')[0] )[0] != null)

                        // RedirectedIsNotHCS 	boolean (Перенаправлено в организацию, не зарегистрированную в ГИС ЖКХ) (необязательное)
                        def _redirectedIsNotHCS = getChildElements( getChildElements(it, 'ChildElement', 'RedirectedIsNotHCS')[0] )[0]
                        appeal.redirectedIsNotHCS = (_redirectedIsNotHCS && _redirectedIsNotHCS.toLowerCase() == 'true')

                        // RolledOver 	boolean (Продлен срок рассмотрения) (необязательное)
                        def _rolledOver = getChildElements( getChildElements(it, 'ChildElement', 'RolledOver')[0] )[0]
                        appeal.rolledOver = (_rolledOver && _rolledOver.toLowerCase() == 'true')

                        // AnswerIsNotRequired 	boolean (Ответ на обращение не требуется) (необязательное)
                        def _answerIsNotRequired = getChildElements( getChildElements(it, 'ChildElement', 'AnswerIsNotRequired')[0] )[0]
                        appeal.answerIsNotRequired = (_answerIsNotRequired && _answerIsNotRequired.toLowerCase() == 'true')

                        // AppealWithdrawn 	anyType (ограничение) (Обращение отозвано заявителем) (необязательное)
                        SOAPBodyElement appealWithdrawn = getChildElements(it, 'ChildElement', 'AppealWithdrawn')[0]
                        // Comment (Комментарий заявителя) (необязательное)
                        //appeal.comment = getChildElements( getChildElements(appealWithdrawn, 'ChildElement', 'Comment')[0] )[0]
                        //println('Comment = ' + appeal.comment)

                        // save appeal
                        stateResult.exportAppeal.add(appeal)
                        appeal = [:]
                    }
                    break
            }

            // Занесении в АИС ЖКХ полученных данных разобранного ответа
            if (stateResult.errorMessage.errorCode) {
                // есть код ошибки
                println("Ответ ${_exportAppeal.ResponceGUID}: Код = ${stateResult.errorMessage.errorCode} с расшифровкой ${stateResult.errorMessage.description}")
                def _edit = ['faultString':stateResult.errorMessage.description]
                switch(stateResult.errorMessage.errorCode) {
                    case 'INT002012':
                        _edit.status = stComplete
                        break
                    default:
                        _edit.status = stError
                        break
                }
                utils.edit(_exportAppeal, _edit)
            }
            else {
                // обрабатываем все обращения, содержащиеся в ответе
                utils.edit( _exportAppeal, [status : stProgress, 'faultString' : ''])
                def countNewAppeal = 0
                def stCurrent = stComplete  // итоговый статус для обрабатываемого сообщения
                def processError    // для сохранения ошибки процесса

                stateResult.exportAppeal.each() { appeal ->
                    //println("appealGUID=${appeal.appealGUID}\tappealText=${appeal.appealText}\tfiasHouseGuid=${appeal.fiasHouseGuid}\t")
                    def _double = utils.findFirst('appeal', ['gishcsGUID':appeal.appealGUID])
                    if ( _double ) {    // Уже загрузили. Пропускаем
                        // Увеличиваем счетчик обработанных, если соданы этим (для сообщения о количестве обращений в пакете)
                        if (_exportAppeal.creationDate < _double.creationDate) {
                            countNewAppeal ++
                            println("Пропускаем импорт Обращения ${appeal.appealGUID}, т.к. уже загружено ранее")
                        }
                        else {
                            println("Пропускаем импорт Обращения ${appeal.appealGUID}, т.к. принадлежит другой сессии импорта")
                        }
                        // Пропускаем сообщение, если по ряду условие не требуется его импорт
                    } else if (appeal.appealStatus == 'Send' && !appeal.redirected && !appeal.redirectedIsNotHCS) {
                        def newAppeal =[:]
                        newAppeal.state = 'registered'
                        newAppeal.fromAp = utils.findFirst('entryPlace',['code':'en4'])  // Место поступления
                        newAppeal.deliveryType = utils.findFirst('deliveryType',['code':'dev8'])  // Тип доставки
                        newAppeal.gishcsGUID = appeal.appealGUID
                        newAppeal.MessageNumber = appeal.appealNumber
                        newAppeal.typeAp = utils.findFirst('appealType',['code':'t3']).UUID // Заявление
                        newAppeal.viewAp = utils.findFirst('viewAp',['code':'view1']).UUID // Индивидуальное
                        // Если указан parentAppealGUID, то «Очередное», иначе «Первичное»)
                        newAppeal.categoryAp = utils.findFirst('CategoryAp',['code': ((appeal.parentAppealGUID) ? 'cat3' : 'cat1')]).UUID

                        // Дата регистрации обращения
                        //newAppeal.registerDate = stringToDate(appeal.appealCreateDate, true)

                        // Дата отправки из организации . Берем из сообщения ГИС ЖКХ?
                        newAppeal.MessageDate = stringToDate(appeal.appealCreateDate, true)

                        // Почтовый адрес заявителя (Организация)
                        //??? Куда сохранять
                        //appeal.postAddress

                        // ОКТМО - Код муниципального образования
                        // ??? В АИС ГЖИ ОКТМО нет
                        //appeal.oktmo.code     Код по ОКТМО        // пример - 29701000
                        //appeal.oktmo.name     Полное наименование // пример - город Калуга
                        if (appeal.oktmo) {    // ОКТМО (обязательное)
                            newAppeal.city = transformOKTMOName(appeal.oktmo.name)
                        }

                        // regionAp Регион обращения
                        if (appeal.oktmo && appeal.oktmo.name) {
                            String[] _reg = appeal.oktmo.name.split(' ')
                            def regionAp
                            if (_reg.size() > 1) {
                                regionAp = utils.find('regionAp',['title': op.orEq(_reg)], sp.ignoreCase())
                            } else {
                                regionAp = utils.find('regionAp',['title': appeal.oktmo.name], sp.ignoreCase())
                            }

                            if (regionAp.size() > 0) newAppeal.regionAp = regionAp[0]
                        }

                        // Поиск адреса дома по ФИАС
                        def house
                        if (appeal.fiasHouseGuid) {
                            //
                            newAppeal.fiasHouse = appeal.fiasHouseGuid
                            house = utils.findFirst('Location$house', ['fias' : appeal.fiasHouseGuid])
                        }

                        if (house) {
                            newAppeal.house2 = house.UUID
                            if (house.stid) newAppeal.street2 = house.stid
                            if (house.distname) newAppeal.city = house.distname
                        }
                        else {
                            def msg = "При выполнении метода getState ГИС ЖКХ в локальном справочнике 'house' не найден дом. Код ФИАС: ${appeal.fiasHouseGuid}. Код ОКТМО: ${appeal.oktmo.code} Наименование муниципального образования: ${appeal.oktmo.name}"
                            println(msg)
                            logger.error(msg)
                            // После успешной зарузки обращения будет добавлен запроса на загрузку данных дома по коду ФИАС (appeal.fiasHouseGuid)
                        }

                        // Комната
                        if (appeal.apartmentNumber) newAppeal.room = appeal.apartmentNumber

                        // Электронная почта
                        if (appeal.email) newAppeal.email = appeal.email

                        // Способ ответа
                        newAppeal.ansType = utils.findFirst('ansWay', ['code' : (appeal.email) ? 'ans3' : 'ans2'])

                        // Телефон
                        if (appeal.phoneNumber) newAppeal.phoneNumber = appeal.phoneNumber

                        // ФИО
                        if (appeal.person.patronymic) newAppeal.FirstName = appeal.person.surname   // !!! В ГЖИ имя и фамилия перепутаны
                        if (appeal.person.firstName) newAppeal.LastName = appeal.person.firstName   // !!! В ГЖИ имя и фамилия перепутаны
                        if (appeal.person.patronymic) newAppeal.MiddleName = appeal.person.patronymic

                        if (!(appeal.orgRootEntityGUID || appeal.person.patronymic)) {
                            newAppeal.FirstName = 'Не указано'
                        }

                        // Ответ на обращение не требуется appeal.AnswerIsNotRequired
                        // В АИС ГЖИ такого нет. Возможно это в справочник "Способ ответа" (ansType) - сейчас такой вариант отсутствует

                        // Описание проблемы
                        if (appeal.appealText) newAppeal.descrip = appeal.appealText.replaceAll("\\<.*?\\>", "")

                        // Тема обращения
                        // Значение по умолчанию, если тематика не найдена
                        def _themes = utils.find('themeInv',['code':'no']) // Другая тема. Возможно надо согласовать новое значение в справочнике типа "Другое"?

                        if (appeal.topic?.code) {
                            // appeal.topic.code - Тема обращения (НСИ 220) // !!! при проверке код какой-то странный, не из классификатора, поиск отключаю
                            // хардкорю соответствие кодов тематик обращений %((( АИС ГЖИ и ГИС ЖКХ
                            println("Тематика и тема обращения: topic.code = ${appeal.topic.code}")
                            String[] _th = appeal.topic.code.split('\\.')

                            if (_th.size()>0) {
                                switch(_th[0]) {
                                    case '1':
                                        _themes = utils.find('themeInv',['code': '12'])
                                        break
                                    case '2':
                                        _themes = utils.find('themeInv',['code': op.orEq('3','17','18')])
                                        break
                                    case '3':
                                        _themes = utils.find('themeInv',['code': '10'])
                                        break
                                    case '5':
                                        _themes = utils.find('themeInv',['code': op.orEq('1','4','7','8','9')])
                                        break
                                    case '6':
                                        _themes = utils.find('themeInv',['code': op.orEq('2','11','13','14')])
                                        break
                                    case '7':
                                        _themes = utils.find('themeInv',['code': '6'])
                                        break
                                    case '8':
                                        _themes = utils.find('themeInv',['code': '5'])
                                        break
                                    case '9':
                                        _themes = utils.find('themeInv',['code': '12'])
                                        break
                                    case '10':
                                        _themes = utils.find('themeInv',['code': '6'])
                                        break
                                        // default:
                                        //     _themes = utils.find('themeInv',['code': 'no'])
                                        // break
                                }
                            }

                            if (appeal.topic.name) newAppeal.theme = appeal.topic.name  // Значение темы обращения (необязательное)
                        }
                        else if (appeal.anotherTopic) {
                            //appeal.anotherTopic - Другая тема обращения
                            newAppeal.theme = appeal.anotherTopic
                        }
                        else {
                            println('В обращении не указана тема (тематика)')
                        }
                        newAppeal.themes = _themes  // обязательное

                        // тип проблемы "Другое", т.к. справочники не синхронизированы
                        newAppeal.typeproblems = utils.findFirst('typeproblems',['code':'probl17']).UUID

                        // docpack <- appeal.attachment[]
                        // Вывод информации о присоединенных файлах
                        appeal.attachment.each {value ->
                            println("Attachment: ${value.name}")
                            println("AttachmentGUID: ${value.attachmentGUID}")
                        }
                        def needSave = true
                        if (appeal.attachment.size() > 0) {
                            appeal.attachment.each { value ->
                                if (needSave) {
                                    println("Формируем запрос на загрузку файла ${value.name} GUID: ${value.attachmentGUID}")
                                    GisFile f = new GisFile (value.attachmentGUID, config)
                                    // Доступные атрибуты файла:
                                    // f.getResponseCode(), f.getName(), f.getContentType(), f.getFileGUID(), f.getLength()
                                    // f.isCompleted(), f.getError(), f.getResponse() <- byte[]  данные считанного файла

                                    if (f.isCompleted() && f.getResponseCode() == 200) {
                                        value.file = f
                                    } else {
                                        // ошибка при получении файла. Текущее Обращение надо пропустить
                                        stCurrent = stProgress  // в дальнейшем считывание Обращения повторится. Типы ошибок не обрабатываю
                                        if (f.error) processError = f.error
                                        needSave =  false
                                    }
                                }
                            }
                        }

                        // Обращение от юр.лица. Ищем id юрлица (для appealComp.legalentity)
                        if (appeal.orgRootEntityGUID) {
                            def org = getLegalentity(appeal.orgRootEntityGUID)
                            if (org.error) {
                                stCurrent = stProgress  // в дальнейшем считывание Обращения повторится
                                processError = org.error
                                needSave =  false
                            } else {
                                newAppeal.legalentity = org.legalentity
                            }
                        }

                        // Сохраняем сформированное обращение
                        if (needSave) {
                            newAppeal.autoCreate = true
                            logger.debug("Обращению будет присвоен номер: ${newAppeal.number}")
                            newAppeal.number = getNewAppealNumber()     // newAppealNumber

                            def _cr
                            def tr1 = {
                                _cr = utils.create(appeal.orgRootEntityGUID ? 'appeal$appealComp' : 'appeal$appeal', newAppeal)
                            }
                            api.tx.call(tr1)
                            println("Скрипт метода getState сервиса ГИС ЖКХ создал обращение с Регистрационный номером ${_cr.title} (number: ${_cr.number})")

                            // Сохраняем полученные файлы (если есть и все успешно загружено)
                            docpack = utils.create('documents$docpack', ['appeal': _cr.UUID, 'title': "Пакет документов в рамках обращения ${_cr.title}"])
                            println("Скрипт метода getState сервиса ГИС ЖКХ создал обращение с Пакет документов ${docpack.title}")

                            // Добавляем запрос на загрузку данных о доме по коду ФИАС
                            if (!house && appeal.fiasHouseGuid) {
                                def _r = (String) new JsonBuilder( ['fiasHouseGuid': appeal.fiasHouseGuid] )
                                def _newGet = utils.create('Integration$getData',[
                                        'action':'exportBriefBasicHouse',
                                        'appeal':_cr,
                                        'requestData':_r
                                ])
                                def msg = "Создан запрос к ГИС ЖКХ ${_newGet.UUID} на получение данных о доме по ФИАС ${appeal.fiasHouseGuid}"
                                println(msg)
                                logger.info(msg)
                            }

                            if (appeal.attachment.size() > 0) {
                                //def docpack = utils.findFirst('documents$docpack', ['appeal':_cr.UUID])
                                // if (!docpack) {
                                //     docpack = utils.create('documents$docpack', ['appeal': _cr.UUID, 'title': "Пакет документов в рамках обращения ${_cr.title}"])
                                // }
                                appeal.attachment.each { value ->
                                    value.file
                                    utils.attachFile( docpack, value.name, value.file.getContentType(), '', value.file.getResponse() )
                                }
                            }

                            //newAppealNumber++
                        }

                        countNewAppeal++
                    }
                }
                // Изменяем статус у обработанного асинхранного запроса
                processError = (processError) ? '. Получена не критическая ошибка: ' + processError : ''
                utils.edit(_exportAppeal, ['status': stCurrent, 'faultString': "Обработано обращений: ${countNewAppeal}${processError}"])
            }
        }

    } catch(Exception e) {
        println(e.getMessage())
        logger.error('Ошибка при выполнении асинхронного запроса к ГИС ЖКХ скрипта getState')
        logger.error(e.getMessage())
        if (_exportAppeal) {
            def _edit = [:]
            _edit.status = stError
            _edit.faultString = e.getMessage()
            utils.edit(_exportAppeal, _edit)
        }
    }

}

return "Всего обработано ответов: ${exportAppeal.size()}"


// --------- Functions ---------
// Создание SOAP запроска к ГИС ЖКХ (соощение и HEADER)
public SOAPMessage createSoapGISMessage(String messageID, String soapAction, String  gisService = 'AppealsAsync') throws SOAPException {
    String  headRequestHeader
    MessageFactory messageFactory = MessageFactory.newInstance()
    SOAPMessage soapMessage = messageFactory.createMessage()
    soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true")
    soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8")

    SOAPPart soapPart = soapMessage.getSOAPPart()
    SOAPEnvelope envelope = soapPart.getEnvelope()

    switch (gisService) {
        case 'AppealsAsync':
            headRequestHeader = 'RequestHeader'
            envelope.addNamespaceDeclaration(baseNameSpace, 'http://dom.gosuslugi.ru/schema/integration/base/')
            envelope.addNamespaceDeclaration(appNameSpace, 'http://dom.gosuslugi.ru/schema/integration/appeals/')
            envelope.addNamespaceDeclaration('nsi', 'http://dom.gosuslugi.ru/schema/integration/nsi-base/')
            envelope.addNamespaceDeclaration('xd', 'http://www.w3.org/2000/09/xmldsig#')
            break
        case 'OrgRegistryCommonAsync':
            headRequestHeader = 'ISRequestHeader'
            envelope.addNamespaceDeclaration(baseNameSpace, 'http://dom.gosuslugi.ru/schema/integration/base/')
            envelope.addNamespaceDeclaration('org', 'http://dom.gosuslugi.ru/schema/integration/organizations-registry-common/')
            envelope.addNamespaceDeclaration('org1', 'http://dom.gosuslugi.ru/schema/integration/organizations-base//')
            envelope.addNamespaceDeclaration('org2', 'http://dom.gosuslugi.ru/schema/integration/organizations-registry-base/')
            envelope.addNamespaceDeclaration('xd', 'http://www.w3.org/2000/09/xmldsig#')
            break
        case 'HomeManagementAsync':
            headRequestHeader = 'RequestHeader'
            envelope.addNamespaceDeclaration(baseNameSpace, 'http://dom.gosuslugi.ru/schema/integration/base/')
            // if (soapAction != 'getState') {
            envelope.addNamespaceDeclaration(housNameSpace, 'http://dom.gosuslugi.ru/schema/integration/house-management/')
            envelope.addNamespaceDeclaration('xd', 'http://www.w3.org/2000/09/xmldsig#')
            // }
            break
    }

    // HEADER
    SOAPHeader soapHeader = envelope.getHeader()
    SOAPElement requestHeader = soapHeader.addChildElement(headRequestHeader, baseNameSpace)
    SOAPElement headerDate = requestHeader.addChildElement('Date', baseNameSpace)
    headerDate.addTextNode( formatDateTime(new Date(), true) )
    SOAPElement messageGUID = requestHeader.addChildElement('MessageGUID', baseNameSpace)
    messageGUID.addTextNode(messageID)

    if (headRequestHeader == 'RequestHeader') {
        SOAPElement headerOrgPPA = requestHeader.addChildElement('orgPPAGUID', baseNameSpace)
        headerOrgPPA.addTextNode(orgPPAGUID)
        // Новый блок, для тестового контура не нужен //<base:IsOperatorSignature>true</base:IsOperatorSignature>
        SOAPElement isOperatorSignature = requestHeader.addChildElement('IsOperatorSignature', baseNameSpace)
        isOperatorSignature.addTextNode("true")
    }

    soapMessage = createSoapGISHeader(soapMessage, soapAction)

    return soapMessage
}


// Формирование mime заголовка SOAP сообщения
public SOAPMessage createSoapGISHeader(SOAPMessage soapMessage, String soapAction) throws SOAPException {
    MimeHeaders headers = soapMessage.getMimeHeaders()
    headers.addHeader('Accept-Encoding', 'gzip,deflate')
    headers.addHeader('Content-Type', 'text/xml')     // с этим стало давать ошибку 'Invalid Content-Type:text/html. Is this an error message instead of a SOAP response?"'
    headers.addHeader('charset', 'utf-8')
    headers.addHeader('SOAPAction', 'urn:' + soapAction)
    if (!gishcsUseCryptoService) {
        // запрос к тестовому серверу (без шифрования)
        headers.addHeader('X-Client-Cert-Fingerprint', 'd070a0bf700010e358fca4b99e2058fc77890d01')
        // Authentification
        headers.addHeader('Authorization', 'Basic ' + gishcsAuth)
    }

    return soapMessage
}


// type - тип возвращаемого элемента. type = 'TextNode' (default), 'ChildElement'
// name - имя возвращаемого элемента
private static def getChildElements(SOAPBodyElement element, String type='TextNode', name = '') {
    def result = []
    if (element) {
        Iterator<SOAPBodyElement> ackRequest = element.getChildElements()

        switch (type) {
            case 'TextNode':
                while (ackRequest.hasNext()) {
                    def child = ackRequest.next()
                    result.add( child.getTextContent() )
                }
                break
            case 'ChildElement':
                while (ackRequest.hasNext()) {
                    def child = ackRequest.next()
                    if ((child instanceof SOAPElement) && child.getNodeName().contains(name)) {
                        result.add( child )
                    }
                }
                break
        }

    }
    return result
}


public SOAPMessage getAndUnzipSOAPMessage(SOAPMessage message) throws Exception {
    SOAPMessage result
    MimeHeaders mimeHeaders = message.getMimeHeaders()
    String[] header = mimeHeaders.getHeader('Content-Encoding')
    String contentEoncoding = (header != null && header.length > 0) ? header[0].toString() : 'NONE'
    println("Content-Encoding: " + contentEoncoding)
    if (contentEoncoding.equalsIgnoreCase("GZIP")) {
        println("SOAP Message in GZIP")
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        message.writeTo(out)
        byte[] zipBytes = out.toByteArray()
        String gZipString= getGZIP(zipBytes)
        //println("UnZip string:\n"+ gZipString)
        result = getSOAPMessageFromString(gZipString)
        //println("SOAP Message Object:\n" + getSOAPAsString(result))
    } else {
        result = message
    }
    return result
}


private String getGZIP(byte[] zipBytes) {
    try {
        GZIPInputStream gzipInput = new GZIPInputStream( new ByteArrayInputStream(zipBytes) )
        return gzipInput.text
    } catch (IOException e) {
        throw new UncheckedIOException("Error while decompression.", e)
    }
}


private String getSOAPAsString(SOAPMessage soapMsg) throws SOAPException, IOException {
    StringWriter sw = new StringWriter()
    TransformerFactory transformerFactory = TransformerFactory.newInstance()
    Transformer transformer = transformerFactory.newTransformer()
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    transformer.transform(
            new DOMSource(soapMsg.getSOAPPart()),
            new StreamResult(sw))
    String strMsg = new String(sw.toString())
    //println("Soap XML:\n"+ strMsg)
    return strMsg
}


private SOAPMessage getSOAPMessageFromString(String xml) throws SOAPException, IOException {
    MessageFactory factory = MessageFactory.newInstance()
    SOAPMessage message = factory.createMessage(
            new MimeHeaders(),
            new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))))
    return message
}


private static String formatDateTime(Date date, boolean zoned) {
    if (zoned) {
        def dtz = ZoneId.systemDefault().toString()
        if (dtz == 'Etc/UTC') {
            dtz = 'Europe/Moscow'
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of( dtz )).format(date.toInstant())
        //return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()).format(date.toInstant())
    } else {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
    }
}


private static String formatDate(Date date, boolean zoned) {
    if (zoned) {
        def dtz = ZoneId.systemDefault().toString()
        if (dtz == 'Etc/UTC') {
            dtz = 'Europe/Moscow'
        }
        return DateTimeFormatter.ISO_OFFSET_DATE.withZone(ZoneId.of( dtz )).format(date.toInstant())
        //return DateTimeFormatter.ISO_OFFSET_DATE.withZone(ZoneId.systemDefault()).format(date.toInstant())
    } else {
        return new SimpleDateFormat("yyyy-MM-dd").format(date)
    }
}


private static Date stringToDate(String stringDate, boolean zoned = false) {
    DateTimeFormatter formatter = zoned ?
            DateTimeFormatter.ISO_OFFSET_DATE.withZone(ZoneId.systemDefault()) :
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    LocalDate localDate = LocalDate.parse(stringDate, formatter)
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
}


public static boolean pingHost(String host, String port, int timeout) {
    try {
        Socket socket = new Socket()
        socket.connect(new InetSocketAddress(host, port.toInteger()), timeout);
        return true;
    } catch (IOException e) {
        return false
    }
}

// Получение объекта класса legalentity по его orgRootEntityGUID в ГИС ЖКХ
//      org.legalentity при org.error = null
private getLegalentity (String orgRootEntityGUID) {
    def org = [:]
    def res
    def metaCls = 'legalentity$legalentity'
    def flt = [:]

    def _fnd = utils.find(metaCls, ['gishcsOrgGUID': orgRootEntityGUID])
    // Если по gishcsOrgGUID не найдено или найдено > 1, ищем по реквизитам
    if (_fnd.size() != 1) {
        // Получение реквизитов организации из ГИС ЖКХ
        org = gisOrgByOrgRootGUID(orgRootEntityGUID)
        if (!org.error) {
            if (org.ogrn) flt.ogrn = org.ogrn
            if (org.inn) flt.inn = org.inn
            if (org.kpp) flt.kpp = org.kpp
            flt.removalDate = op.isNull()
            _fnd = utils.find(metaCls, flt)

            // Если по реквизитам не найдено или найдено > 1, ищем по полному наименованию
            if (_fnd.size() != 1) _fnd = utils.find(metaCls, ['fullname': org.fullName], sp.ignoreCase())
        }
    }

    if (_fnd.size() == 1) {
        // Если найдено
        res = _fnd[0]
        flt = [:]
        // Проверка наличия GUID ГИС ЖКХ и если нужно его сохранение
        if (res.gishcsOrgGUID != orgRootEntityGUID) flt.gishcsOrgGUID = orgRootEntityGUID
        // Проверка короткого наименования, т.к. в АИС ГЖИ встречаются '-'
        if (org?.shortName && res.title != org?.shortName && org?.shortName != '-') flt.title = org.shortName
        // При необходимости сохранение изменений
        if ( !flt.isEmpty() ) {
            utils.edit(res , flt)
            println("Дополнена информация по организации ${org.fullName}")
        }
    } else if (!org.error) {
        // Не найдено ни одним способом и нет ошибки, добавляем новую организацию
        if (org.shortName) {     // необзязательное
            flt.title = org.shortName
        }
        else {
            flt.title = org.fullName
        }
        flt.fullname = org.fullName
        flt.gishcsOrgGUID = orgRootEntityGUID
        if (org.address && org.address != '-') flt.addressfact = org.address
        // org.okopf
        // org.fiasHouse
        flt.removalDate = null
        res = utils.create(metaCls, flt)
        println("Добавлена новая организация ${org.fullName}")
    }
    org.legalentity = res

    return org
}


// Реквизиты организации из ГИС ЖКХ по ее OrgRootEntinyGUID
private gisOrgByOrgRootGUID (String orgID) {
    //constant
    final versionOrg = '10.0.2.1'
    final serviceOrg = 'ext-bus-org-registry-common-service/services/OrgRegistryCommonAsync'

    def org = [:]

    org.orgRootEntityGUID = orgID
    org.error = null

    try {
        //def surl = new URL('http://sit02.dom.test.gosuslugi.ru:10082/ext-bus-org-registry-common-service/services/OrgRegistryCommonAsync?wsdl')
        def surl = new URL("${sHost}//${serviceOrg}")

        //Cоздаем соединение
        bl1: { // Анонимный блок кода для изоляции переменных
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance()
            SOAPConnection connection = soapConnFactory.createConnection()
            SOAPMessage soapRequest = createSoapGISMessage( UUID.randomUUID().toString(), 'exportOrgRegistry', 'OrgRegistryCommonAsync' )

            // BODY
            SOAPPart soapPart = soapRequest.getSOAPPart()
            SOAPEnvelope envelope = soapPart.getEnvelope()
            SOAPBody soapBody = envelope.getBody()
            SOAPElement orgRequest = soapBody.addChildElement('exportOrgRegistryRequest', 'org')
            orgRequest.setAttribute('Id','signed-data-container')
            orgRequest.setAttribute("base:version", versionOrg)
            SOAPElement searchCriteria = orgRequest.addChildElement('SearchCriteria', 'org')
            SOAPElement orgRootEntityGUID = searchCriteria.addChildElement('orgRootEntityGUID', 'org2')
            orgRootEntityGUID.addTextNode(orgID)

            //сохранение
            if (soapRequest.saveRequired()) soapRequest.saveChanges()

            // Подпись запроса сервисом РТК
            if (gishcsUseCryptoService) {
                def signed = getSignSOAP(soapRequest)
                if (signed.error) {
                    println(signed.error)
                    logger.error(signed.error)

                    org.error = signed.error
                } else {
                    // Замена запроса подписанным xml
                    soapRequest = createSoapGISHeader(signed.soapRequest, 'exportOrgRegistry')
                    if (soapRequest.saveRequired()) soapRequest.saveChanges()
                }
            }

            if (!org.error) {
                println("\nSOAP Request:\n" + getSOAPAsString(soapRequest))
                println("\n... Connect to ${surl} \n")

                //Отправка сообщения
                SOAPMessage reply = connection.call(soapRequest, surl)
                //Закрываем соединение
                connection.close()
                // ------------------------------------------

                // Unzip ответа, если необходимо
                SOAPMessage soapResponce = getAndUnzipSOAPMessage(reply)

                // Полученный ответ
                println("\nsoapResponce:\n" + getSOAPAsString(soapResponce))

                // ------- Разбор сообщения -------
                SOAPPart responceSOAPPart = soapResponce.getSOAPPart()
                SOAPEnvelope responceEnvelope = responceSOAPPart.getEnvelope()
                SOAPBody responceBody = responceEnvelope.getBody()

                // Проверка InvalidRequest (Fault)
                SOAPBodyElement fault = getChildElements(responceBody, 'ChildElement', 'Fault')[0]
                if (fault) {
                    // ------- ошибка в полученном ответе -------
                    org.error = getChildElements( getChildElements(fault, 'ChildElement', 'faultstring')[0] )
                    println("Сервер ГИС ЖКХ вернул текст ошибки faultstring: " + org.error)
                } else {
                    // Ответ без ошибки
                    // get 'AckRequest'
                    SOAPBodyElement ackRequest = getChildElements(responceBody, 'ChildElement', 'AckRequest')[0]
                    // get 'Ack'
                    SOAPBodyElement ack = getChildElements(ackRequest, 'ChildElement', 'Ack')[0]
                    // get MessageGUID
                    SOAPBodyElement guid = getChildElements(ack, 'ChildElement', 'MessageGUID')[0]
                    // get MessageGUID.textNode
                    org.requestMessageGUID = getChildElements(guid)[0]
                    println("requestMessageGUID = ${org.requestMessageGUID}")
                }
            }
        }

        if (!org.error) {
            // Получение данных о организации (запрос данных данных по requestMessageGUID)
            // Результат выполнения асинхронный, поэтому может появиться не сразу
            //  делам цикл с задержкой в 1 сек и количеством повторений не более 100 раз
            SOAPMessage soapRequest = createSoapGISMessage( UUID.randomUUID().toString(), 'getState', 'OrgRegistryCommonAsync' )

            // BODY
            SOAPPart soapPart = soapRequest.getSOAPPart()
            SOAPEnvelope envelope = soapPart.getEnvelope()
            SOAPBody soapBody = envelope.getBody()
            SOAPElement stateRequest = soapBody.addChildElement('getStateRequest', baseNameSpace)
            SOAPElement messageGUID = stateRequest.addChildElement('MessageGUID', baseNameSpace)
            messageGUID.addTextNode(org.requestMessageGUID)

            //сохранение
            if (soapRequest.saveRequired()) soapRequest.saveChanges()

            println("\nSOAP Request:\n" + getSOAPAsString(soapRequest))
            println("\n... Connect to ${surl} \n")
            // Cоздаем соединение
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance()
            SOAPConnection connection = soapConnFactory.createConnection()

            def count = 0
            while (count < 100) {
                sleep(1000) // пауза для ожидания обработки сообщения на стороне сервера
                count ++

                //Отправка сообщения
                SOAPMessage reply = connection.call(soapRequest, surl)
                //Закрываем соединение
                connection.close()
                // ------------------------------------------
                // Unzip ответа, если необходимо
                SOAPMessage soapResponce = getAndUnzipSOAPMessage(reply)

                // Полученный ответ
                println("\nsoapResponce:\n" + getSOAPAsString(soapResponce))

                // ------- Разбор сообщения -------
                SOAPPart responceSOAPPart = soapResponce.getSOAPPart()
                SOAPEnvelope responceEnvelope = responceSOAPPart.getEnvelope()
                SOAPBody responceBody = responceEnvelope.getBody()

                // Проверка InvalidRequest (Fault)
                SOAPBodyElement fault = getChildElements(responceBody, 'ChildElement', 'Fault')[0]
                if (fault) {
                    // ------- ошибка в полученном ответе -------
                    org.error = getChildElements( getChildElements(fault, 'ChildElement', 'faultstring')[0] )
                    println("Сервер ГИС ЖКХ вернул текст ошибки faultstring: " + org.error)
                    break
                } else {
                    // Разбираем ответ
                    // get 'getStateResult'
                    SOAPBodyElement getStateResult = getChildElements(responceBody, 'ChildElement', 'getStateResult')[0]

                    // Проверка на возврат кода ошибки
                    SOAPBodyElement errorMessage = getChildElements(getStateResult, 'ChildElement', 'ErrorMessage')[0]
                    if (errorMessage) {
                        String errorCode = getChildElements( getChildElements(errorMessage, 'ChildElement', 'ErrorCode')[0] )[0]
                        org.error = getChildElements( getChildElements(errorMessage, 'ChildElement', 'Description')[0] )[0]
                        println("Сервер ГИС ЖКХ вернул ошибку. ErrorCode: ${errorCode} Description: ${org.error}")
                        break
                    }
                    // RequestState (Статус обработки сообщения в асинхронном обмене) (1- получено; 2 - в обработке; 3 - обработано)
                    String requestState = getChildElements( getChildElements(getStateResult, 'ChildElement', 'RequestState')[0] )[0]
                    println('requestState = ' + requestState)

                    if (requestState.toInteger() == 3) {
                        // Ответ без ошибки. Разбираем полученные данные об организации
                        // секция exportOrgRegistryResult
                        SOAPBodyElement exportOrgRegistryResult = getChildElements(getStateResult, 'ChildElement', 'exportOrgRegistryResult')[0]
                        SOAPBodyElement orgVersion = getChildElements(exportOrgRegistryResult, 'ChildElement', 'OrgVersion')[0]
                        // секция Legal
                        SOAPBodyElement legal = getChildElements(orgVersion, 'ChildElement', 'Legal')[0]
                        org.shortName = getChildElements( getChildElements(legal, 'ChildElement', 'ShortName')[0] )[0]
                        org.fullName = getChildElements( getChildElements(legal, 'ChildElement', 'FullName')[0] )[0]
                        org.ogrn = getChildElements( getChildElements(legal, 'ChildElement', 'OGRN')[0] )[0]
                        org.inn = getChildElements( getChildElements(legal, 'ChildElement', 'INN')[0] )[0]
                        org.kpp = getChildElements( getChildElements(legal, 'ChildElement', 'KPP')[0] )[0]
                        org.okopf = getChildElements( getChildElements(legal, 'ChildElement', 'OKOPF')[0] )[0]
                        org.address = getChildElements( getChildElements(legal, 'ChildElement', 'Address')[0] )[0]
                        // FIASHouseGuid
                        org.fiasHouse = getChildElements( getChildElements(legal, 'ChildElement', 'FIASHouseGuid')[0] )[0]
                        break
                    }
                }
            }
        }

    } catch(Exception e) {
        println( e.getMessage() )
        org.error = e.getMessage()
    }

    return org
}


// Проверка доступности крипто срвиса подписи и хоста ГИС ЖКХ
private static String checkService(String gisHost, signService = '', gishcsUseCryptoService = false) {
    String res
    def timeout = 200
    def r = /^(.*):\/\/([A-Za-z0-9\-\.]+):([0-9]+)\/?(.*)/

    def res1 = (gisHost =~ r).findAll()[0]
    def ready1 = pingHost(res1[2], res1[3], timeout) ? null : " Хост ГИС ЖКХ (туннель): False"
    def ready2

    if (gishcsUseCryptoService) {
        def res2 = (signService =~ r).findAll()[0]
        ready2 = pingHost(res2[2], res2[3], timeout) ? null : " Сервис подписи: False"
    }

    if (ready1 || ready2) {
        res = 'Доступность сервисов.' + (ready1? ready1:'') + (ready2? ready2:'')
    }
    return res
}


// Функция подписи запроса сервисом РТК
//  должна быть определнs глобвальнst переменные:
//      gishcsSignService = 'http://172.21.1.2:8081/sign' - адрес сервиса
//
public def getSignSOAP(SOAPMessage soapRequest) {
    String msg = 'Сервис подписи сообщения для ГИС ЖКХ вернул ошибку '
    String xml = getSOAPAsString( soapRequest )
    def result = [:]
    //
    if (gishcsSignService) {
        try {
            println("Подключение к сервису подписи ${gishcsSignService}")
            URL surl = new URL("${gishcsSignService}")
            HttpURLConnection connection = (HttpURLConnection) surl.openConnection()
            connection.setRequestMethod("POST")
            connection.setDoOutput(true)
            connection.setDoInput(true)
            connection.setUseCaches(false)
            connection.setRequestProperty('Content-Type', 'text/plain')
            connection.setRequestProperty('charset', 'utf-8')
            connection.setRequestProperty('Content-length', xml.length().toString())

            //println("\nSOAP Request:\n" + xml)
            // Отправка данных
            DataOutputStream dataOutputStream = new DataOutputStream( connection.getOutputStream() )
            try {
                dataOutputStream.writeBytes(xml)
            } finally {
                dataOutputStream.close()
            }

            // Проверка соединения
            def responseCode = connection.getResponseCode()
            //println( "Сервис подписи вернул код ответа: ${responseCode}" )

            if (responseCode == 200) {
                // Чтение ответа
                // В виде двоичного потока, чтобы не искажать подпись
                def Integer currentLength = connection.getContentLength()
                def InputStream raw = connection.getInputStream()
                def InputStream inputStream = new BufferedInputStream(raw)
                def byte[] currentBuffer = new byte[currentLength]
                def int bytesRead = 0
                def Integer offset = 0
                while (offset < currentLength) {
                    bytesRead = inputStream.read(currentBuffer, offset, currentBuffer.length - offset)
                    if (bytesRead == -1)
                        break
                    offset += bytesRead
                }
                inputStream.close()
                raw.close()
                String readBuffer = new String(currentBuffer, Charset.forName('UTF-8'))

                // Ответ
                // println("Ответ сервиса подписи: ${readBuffer}" )
                result.soapRequest = getSOAPMessageFromString( readBuffer )
            } else if (responseCode == 500) {
                // Ошибка. Результат в json
                result.error = " ${msg} код: ${responseCode}"
                if (connection.getContentType() == 'application/json') {
                    def data = connection.getErrorStream()
                    if (data) {
                        // не разбираю, json содержит $, что вызывает ошибку в методе разбора json
                        // result.error = "${result.error}. Ответ:\n'${data}'"
                        def _e = "${result.error}. Ответ:\n'${data}'"
                        logger.error(_e)
                    }
                }
            } else {
                // Другая ошибка
                result.error = connection.getResponseMessage()
            }
            connection.disconnect()

        } catch(Exception e) {
            result.error = "${msg}: ${e.getMessage()}"
        }
    } else {
        result.error = 'Адрес сервиса подписи сообщений для ГИС ЖКХ не определен'
    }

    return result
}

// Получение следующего номера обращения
def int getNewAppealNumber() {
    def newAppealNumber = 1
    if (utils.count('appeal',[metaClass:['appeal','appealComp','appealAtt'], 'number':op.isNotNull()]) != 0) {
        def maxDate = api.db.query("SELECT max(creationDate) FROM appeal WHERE case_id in ('appeal', 'appealComp', 'appealAtt')").list()[0]

        if(new Date().format('yy') == maxDate.format('yy')) {
            newAppealNumber = utils.findFirst('appeal',[creationDate:maxDate]).number.plus(1)
        }
    }
    return newAppealNumber
}


def static transformOKTMOName (String s) {
    //все в lower
    s = s.toLowerCase()

    // город в г в конец
    if (s.indexOf(' город' ) > -1 || s.indexOf('город ') > -1) {
        s = s.replace('город', '') + ' г'
    }

    // г. в г в конец
    if (s.indexOf('г.' ) > -1) {
        s = s.replace('г.', '') + ' г'
    }

    // "деревня" в д
    if (s.indexOf('деревня ' ) > -1 || s.indexOf(' деревня') > -1) {
        s = s.replace('деревня', '') + ' д'
    }

    // "д." (деревня)
    if (s.indexOf('д.' ) > -1) {
        s = s.replace('д.', '') + ' д'
    }

    // "село" в с
    if (s.indexOf('село ' ) > -1 || s.indexOf(' село') > -1) {
        s = s.replace('село', '') + ' с'
    }

    // "с." (село)
    if (s.indexOf('с.' ) > -1) {
        s = s.replace('с.', '') + ' с'
    }

    // убрать двойные пробелы
    while (s.indexOf('  ') > -1) {
        s = s.replaceAll('  ', ' ')
    }

    return s
}

public class GisFile {
    private String fileGUID
    private String name
    private String contentType
    private Integer length = 0
    private Integer responseCode = 0
    private boolean completed = false
    private byte[] responseBuffer = new byte[0]
    private String error
    private HttpURLConnection connection
    private String orgPPAGUID
    private String gishcsAuth
    private String sHost
    private boolean gishcsUseCryptoService

    final service = 'ext-bus-file-store-service/rest/appeals'

    public GisFile(String fileGUID, def config) {
        this.orgPPAGUID = config.orgPPAGUID
        this.gishcsAuth = config.gishcsAuth
        this.sHost = config.sHost
        this.gishcsUseCryptoService = config.gishcsUseCryptoService

        this.fileGUID = fileGUID

        try {
            // Запрос на получение сведений о файле
            println('Запрос на получение информации о файле')
            def URL surl = new URL("${this.sHost}/${service}/${this.fileGUID}")
            this.connection = (HttpURLConnection) surl.openConnection()
            this.connection.setRequestMethod("HEAD")
            this.connection.setUseCaches(false)

            // HEAD
            if (!this.gishcsUseCryptoService) {
                // запрос к тестовому серверу (без шифрования)
                this.connection.setRequestProperty('X-Client-Cert-Fingerprint', 'd070a0bf700010e358fca4b99e2058fc77890d01')
                // Authentification
                this.connection.setRequestProperty('Authorization', 'Basic ' + this.gishcsAuth)
            }
            this.connection.setRequestProperty('X-Upload-OrgPPAGUID', this.orgPPAGUID)

            // Проверка соединения
            this.responseCode = this.connection.getResponseCode()
            //println("Connection code ${this.responseCode}")
            if (this.responseCode == 200) {
                // Ответ
                def field = getField()

                //println("X-Upload-Completed = " + field['X-Upload-Completed'] )  // !!! Файл готовченко
                //this.name = field['X-Upload-Filename']
                this.name = MimeUtility.decodeText(field['X-Upload-Filename'])    // для наумен. требуется import javax.mail.internet.MimeUtility
                this.length = field['X-Upload-Length'] as Integer
                this.completed = (field['X-Upload-Completed'] == 'true')? true : false
            } else if (this.responseCode == 400) {
                def field = getField()
                this.error = field['X-Upload-Error']
            } else {
                this.error = this.connection.getResponseMessage()
            }
            this.connection.disconnect()

            // Получение файла
            println('Запрос на получение содержания файла')
            if (this.completed) {
                // Запрос на выгрузку файла
                def currentStart = 0
                def currentEnd = 0
                def maxPart = 5242880

                while (true) {
                    currentEnd = ((currentStart + maxPart) > this.length) ? (this.length - 1) : (currentStart + maxPart - 1)


                    // Создаем соединение
                    surl = new URL("${this.sHost}/${service}/${this.fileGUID}?getfile")
                    this.connection = (HttpURLConnection) surl.openConnection()
                    this.connection.setRequestMethod("GET")
                    this.connection.setUseCaches(false)

                    // HEAD
                    if (!this.gishcsUseCryptoService) {
                        // запрос к тестовому серверу (без шифрования)
                        this.connection.setRequestProperty('X-Client-Cert-Fingerprint', 'd070a0bf700010e358fca4b99e2058fc77890d01')
                        // Authentification
                        this.connection.setRequestProperty('Authorization', 'Basic ' + this.gishcsAuth)
                    }
                    this.connection.setRequestProperty('X-Upload-OrgPPAGUID', this.orgPPAGUID)

                    // еще часть файла (при размере больше 5Мб)
                    if (this.length > maxPart) {
                        // println("Get Range: bytes=${currentStart}-${currentEnd}")
                        this.connection.setRequestProperty('Range', "bytes=${currentStart}-${currentEnd}")    //5МБ Range: bytes=0-5242879
                    }

                    // Проверка соединения
                    this.responseCode = this.connection.getResponseCode()
                    Integer currentLength = this.connection.getContentLength()
                    // println("GisFile=>Connection code ${this.responseCode}")
                    // println('GisFile=>ResponseMessage: ' + this.connection.getResponseMessage() )
                    // println('GisFile=>Длина файла: ' + currentLength)

                    if (this.responseCode == 200) {
                        // Ответ
                        this.contentType = this.connection.getContentType()
                        //println(contentType)

                        def InputStream raw = this.connection.getInputStream()
                        def InputStream inp = new BufferedInputStream(raw)
                        def byte[] currentBuffer = new byte[currentLength]
                        def int bytesRead = 0
                        def Integer offset = 0
                        while (offset < currentLength) {
                            bytesRead = inp.read(currentBuffer, offset, currentBuffer.length - offset)
                            if (bytesRead == -1) break;
                            offset += bytesRead
                        }
                        inp.close()

                        // Сбор данных разных parts
                        def ByteArrayOutputStream outStream = new ByteArrayOutputStream()
                        outStream.write(this.responseBuffer)
                        outStream.write(currentBuffer)
                        this.responseBuffer = outStream.toByteArray()

                    }
                    else if (this.responseCode == 400) {
                        def field = getField()
                        this.error = field['X-Upload-Error']
                        // println("GisFile=>X-Upload-Error: ${this.error}")
                        break
                    }
                    this.connection.disconnect()

                    if (currentEnd >= (this.length-1)) break
                    currentStart = currentStart + currentLength
                }
            }
            else {
                if (!this.error) {
                    this.error = 'X-Upload-Error: Файл не готов'
                }
            }

        } catch(Exception e) {
            this.error = e.getMessage()
        }
    }

    private def getField() {
        def field = [:]
        def i = 1

        println("Разбор заголовка:")
        while (true) {
            this.connection.getHeaderFieldKey(i)
            if (!this.connection.getHeaderFieldKey(i)) break
            field[this.connection.getHeaderFieldKey(i)] = this.connection.getHeaderField(i)
            // println("\t${this.connection.getHeaderFieldKey(i)} = ${this.connection.getHeaderField(i)}")
            i++
        }

        return field
    }

    public String getName() {
        return this.name
    }

    public String getFileGUID() {
        return this.fileGUID
    }

    public Integer getLength() {
        return this.length
    }

    public boolean isCompleted() {
        return this.completed
    }

    public byte[] getResponse() {
        return this.responseBuffer
    }

    public String getError() {
        return this.error
    }

    public int getResponseCode() {
        return this.responseCode
    }

    public String getContentType() {
        return this.contentType
    }
}


//*
void println(msg) {
    if (debug) logger.debug("\n*** " + msg)
}
//*/