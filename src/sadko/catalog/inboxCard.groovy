package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.KeyStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Logger
import java.util.regex.Matcher

@Field final Logger logger = Logger.getLogger("") //todo off in web

def version = 0.1



interface iCard {}

enum MappingTypeUrl {
    resolution("resolution", "InboxResol"),
    letter("letter", "InboxLetter"),

    private def name;
    private def url;

    MappingTypeUrl(String name, String url) {
        this.name = name
        this.url = url
    }

    static Map<String, String> getMapFields() {
        def name = values()*.name
        def decs = values()*.url
        return [name, decs].transpose().collectEntries();
    }
}

class InboxCard {
    Resol Card
    Resolution Resolution
    Letter Letter
    def Guid
}

class Resol implements iCard{        // appeal

    def prepareAddress
    String Guid                     //[OK] Уникальный идентификатор резолюции todo check New field -> GuidSadko
    // Заявитель
    String CitizenName              //[OK] Имя заявителя -> LastName
    String CitizenSurname           //[OK] Фамилия заявителя -> FirstName
    String CitizenPatronymic        //[OK] Отчество заявителя -> MiddleName
    String CitizenAddress           //[OK] Почтовый адрес заявителя -> oldaddr todo check собрать из дома [house2]+[street2]+room
                                    // Список домов title -> номер дома
                                    // def houses = utils.find('Location$house', [title: '2'])
                                    // Список домов contains -> улица или метод поподания
                                    // for(def house: houses){
                                    //      def street = house.stid
                                    //      try {
                                    //            def isFind = street.title.contains("Аристово")
                                    //          if(isFind){
                                    //                return house.UUID
                                    //          }
                                    //      } catch (Exception e) {}
                                    //   return null
                                    //}
    String CitizenAddressPost       //[OK] Индекс почтового адреса заявителя -> indexAddr
    String CitizenAddressAreaId     //[OK] ID района по почтовому адресу заявителя -> справочник CitizenAddArea [regionAp]
    String CitizenPhone             //[OK] Телефон заявителя -> phoneNumber
    String CitizenEmail             //[OK] E-Mail заявителя -> email
    String CitizenSocialStatusId    //[Х] ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    String CitizenBenefitId         //[Х] ID льготный состав гражданина -> справочник CitizenBenefit todo не используют
    String CitizenAnswerSendTypeId  //[OK] Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    String LetterTypeId             //[OK] ID типа обращения -> справочник LetterTypes [typeAp]
    String DocumentTypeId           //[OK] ID вида обращения -> справочник DocumentTypes [viewAp]
    String CorrespondentId          //[OK] ID корреспондента -> справочник Correspondents [reporter]
    String LetterNumber             //[OK] Номер сопроводительного письма -> MessageNumber
    String ControlOrgSendDate       //[OK] Дата отправки из организации -> MessageDate
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSD
    String DeliveryTypeId           //[OK] Тип доставки -> справочник DeliveryTypes [deliveryType]
    String ConsiderationFormId      //[Х] Форма рассмотрения -> справочник ConsiderationF todo не используют
    String ReceivedFrom             //[OK] Поступило из -> todo fromAp справочник Место поступления или receivedfrom (строка) !!!!!!!!!!!!
    String RegistrationNumber       // Регистрационный номер -> todo New field -> RegNumSD
    String RegistrationDate         //[OK] Дата регистрации -> registerDate
    String PreviousCardsCount       // Количество предыдущих обращений todo New field -> PreviousCardsCountSadko todo не используют
    String DocSheetNumber           // Количество листов документа  todo New field -> DocSheetNumberSadko todo не используют
    String DocCopyNumber            // Количество листов приложения todo New field -> DocCopyNumberSadko todo не используют
    String ConcernedCitizensNumber  // Количество заинтересованных todo New field -> ConcernedCitizensNumberSadko todo не используют
    String Message                  //[OK] Текст обращения -> descrip
    ArrayList Files                 // Файлы -> Павет документов [docpack]
                                    // Прикрепление файла к объекту*/
                                    // def attachedFile = utils.attachFile(obj, fileName, contentType, description, data)
                                    // def str = new String(base64.decodeBase64())
                                    // def attachedFile = utils.attachFile(utils.get(obj.docpack.UUID[0]), "Hello4.txt", '', "Hello", str.getBytes())
}

class Letter implements iCard{
    def prepareAddress
    String CitizenName              // Имя заявителя -> LastName
    String CitizenSurname           // Фамилия заявителя -> FirstName
    String CitizenPatronymic        // Отчество заявителя -> MiddleName
    String CitizenAddress           // Почтовый адрес заявителя -> oldaddr todo check собрать из дома [house2]+[street2]+room
                                    // Список домов title -> номер дома
                                    // def houses = utils.find('Location$house', [title: '2'])
                                    // Список домов contains -> улица или метод поподания
                                    // for(def house: houses){
                                    //      def street = house.stid
                                    //      try {
                                    //            def isFind = street.title.contains("Аристово")
                                    //          if(isFind){
                                    //                return house.UUID
                                    //          }
                                    //      } catch (Exception e) {}
                                    //   return null
                                    //}
    String CitizenAddressPost       // Индекс почтового адреса заявителя -> indexAddr
    String CitizenSocialStatusId    // ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    String CitizenAnswerSendTypeId  // Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    String CitizenPhone             // Телефон заявителя -> phoneNumber
    String CitizenEmail             // E-Mail заявителя -> email
    String LetterNumber             // Номер сопроводительного письма -> MessageNumber
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSadko
    String DeliveryTypeId           // Тип доставки -> справочник DeliveryTypes [deliveryType]
    String Message                  // Текст обращения -> descrip
    ArrayList Files                 // Файлы -> Павет документов [docpack]
                                    // Прикрепление файла к объекту*/
                                    // def attachedFile = utils.attachFile(obj, fileName, contentType, description, data)
                                    // def str = new String(base64.decodeBase64())
                                    // def attachedFile = utils.attachFile(utils.get(obj.docpack.UUID[0]), "Hello4.txt", '', "Hello", str.getBytes())
}

class Resolution {                  // appeal -> resolution
    String Guid                     // Уникальный идентификатор резолюции todo New field -> GuidSadko
    String CreatedTime              // Дата поручения todo New field -> CreatedTimeSadko
    User Author                     // Автор -> who
    User Executor                   // Исполнитель -> that
    String DecisionId               // ID Решения по резолюции -> справочник Decisions todo не используют
    String ResolutionText           // Текст резолюции -> answer
    String ControlDate              // Дата контроля todo New field -> ControlDateSadko
    ArrayList Themes                // Список тем вопросов todo ??????
    ArrayList Files                 // Список файлов       todo ??????
}

class User {
    String Guid
    String LastName
    String FirstName
    String MiddleName
    String FIO
}

class Theme {
    String Code                     // Код по тематическому классификатору обращений
    String Name                     // Название вопроса
    String Annotation               // Анотация к вопросу
}

class FileData {
    String Name                     // Имя файла
    String Guid                     // Уникальный идентификатор файла
    String Data                     // Закодированное в строку base64 содержимое файла
}

class ConnectSADKO {
    String access_token
    int expires_in
    String token_type
    String scope
}

class Inbox {
    String Guid
    String Created
    String Type

    Inbox(String guid, String created, String type) {
        Guid = guid
        Created = created
        Type = type
    }
}

class PrepareAddress {
    static Map<String, String> sliceAddress(String address) {
        def slice = address.split(',')
        Map<String, String> map = new HashMap<>()
        for (def val : slice) {
            def item = val.trim()
            switch (item) {
                case { it.contains('обл.') }:
                    map.put('обл', item.substring('обл.'.size(), item.size()))
                    break
                case { it.contains('р-н.') }:
                    map.put('р-н', item.substring('р-н.'.size(), item.size()))
                    break
                    ////////////Населеный пункт//////////////
                case { it.contains('г.') }:
                    map.put('г', item.substring('г.'.size(), item.size()))
                    break
                case { it.contains('д.') && item.indexOf('.') == item.size() - 1 }:
                    map.put('дер', item.substring(0, item.indexOf('.') - 'д.'.size()))
                    break
                    //////////////////////////
                    ////////////УЛИЦА//////////////
                case { it.contains('ул.') }:
                    map.put('ул', item.substring('ул.'.size(), item.size()))
                    break
                case { it.contains('пер.') }:
                    map.put('пер', item.substring('пер.'.size(), item.size()))
                    break
                case { it.contains('пл.') }:
                    map.put('пл', item.substring('пл.'.size(), item.size()))
                    break
                case { it.contains('пр.') }:
                    map.put('пр', item.substring('пр.'.size(), item.size()))
                    break
                    //////////////////////////
                case { it.contains('д.') }:
                    map.put('д', item.substring('д.'.size(), item.size()))
                    break
                case { it.contains('кв.') }:
                    map.put('кв', item.substring('кв.'.size(), item.size()))
                    break
            }
        }
        return map
    }

    static String getStreet(Map<String, String> map) {
        def street = ['ул', 'пр', 'пл', 'пер']
        for (def val : street) {
            if (map[val] != null) return map[val]
        }
        return null
    }

    static String getLocality(Map<String, String> map) {
        def street = ['г', 'дер']
        for (def val : street) {
            if (map[val] != null) return map[val]
        }
        return null
    }

    static boolean sliceContainsAddressDep(String address, String value) {
        def split = address.split(', ')
        for (def item : split) {
            def strings = item.split('\\.')
            if (strings == null || strings.size() == 0) return false
            if (strings.size() > 1) {
                if (value.contains(strings[1].trim())) {
                    return true
                }
            } else {
                if (value.contains(strings[0].trim())) {
                    return true
                }
            }
        }
        return false
    }

    static boolean sliceContainsAddress(String address, String value) {
        def split = address.split(' ')
        for (def item : split) {
            if (item.size() < 4) continue
            def strings = item.split(',')
            if (strings == null || strings.size() == 0) return false
            if (strings.size() > 1) {
                if (value.contains(strings[1].trim())) {
                    return true
                }
            } else {
                if (value.contains(strings[0].trim())) {
                    return true
                }
            }
        }
        return false
    }

    static boolean checkMatchAddress(String street, String address, String valueDb) {
        def isContainsMain = street == null ? false :valueDb.contains(street.trim())
        if (isContainsMain) return isContainsMain
        def isContains = sliceContainsAddress(address, valueDb)
        return isContains
    }

    def static matchAddress(String predicate, String address, boolean isString = false, boolean isHome = false) {
        //([А-Я][а-я]+(-?[А-Я][а-я]+)?)
        def preparePattern = isString ? "(\\s|\\.)([А-Я][а-я]+)" : "(\\.|\\s?)\\s*?(\\d+[.*?]*)"
//        def preparePattern = isString ? "(\\s|\\.)([А-Я][а-я]+(-?[А-Я][а-я]+)?)" : "(\\.|\\s?)\\s*?(\\d+[.*?]*)"
//    def preparePatternRevers = isString ? "([\\wА-Яа-я\\-]+)(\\s|\\.)?" : "(\\d+[.*?]*)\\s*?"
        def preparePatternRevers = isString ? "[A-Я][a-я]+(\\s|\\.)" : "(\\d+[.*?]*)\\s*?"
//        def preparePatternRevers = isString ? "([А-Я][а-я]+(-?[А-Я][а-я]+)?)(\\s|\\.)" : "(\\d+[.*?]*)\\s*?"

        def patternFirstNum = "\\s(\\d+[.*?]*)"
//    def patternFirstNum = "[1-9]\\d*"
        def pattern = "${predicate}${preparePattern}"
        def patternRevers = "${preparePatternRevers}${predicate}"

        def matches = (address =~ pattern)
        if (matches.size() > 0) {
            if (isString) {
                return checkMatchString(matches)
            } else {
                return checkMatchNumber(matches)
            }
        } else {
            def matchesRevers = (address =~ patternRevers)
            if (matchesRevers.size() > 0) {
                if (isString) {
                    return checkMatchString(matchesRevers, true)
                } else {
                    return checkMatchNumber(matchesRevers)
                }
            } else {
                def matchesFirstNum = (address =~ patternFirstNum)
                if (!isString && matchesFirstNum.size() > 0) {
                    String res = isHome ? matchesFirstNum[0][matchesFirstNum[0].size() - 1] : matchesFirstNum[0][0]
                    if (res.isInteger()) {
                        def number = res as Integer
                        return number
                    }
                }
            }
        }
    }

    def static checkMatchNumber(Matcher matches) {
        def number = null
        matches[0].each { String val ->
            if (val.isInteger()) {
                number = val as Integer
            }
        }
        return number
    }

    def static checkMatchString(Matcher matches, boolean isRevers = false) {
        def list = matches[0] as ArrayList
        String res = isRevers && list.size() > 1 ? list[0].split(" ")[0] : list[list.size() - 1]
        return res
    }
}

class TrustHostnameVerifierInbox implements HostnameVerifier {
    @Override
    boolean verify(String hostname, SSLSession session) {
        return hostname == session.peerHost
    }
}

def prepareSSLConnection() {
    def context = SSLContext.getInstance('SSL')
    def tks = KeyStore.getInstance(KeyStore.defaultType);
    def tmf = TrustManagerFactory.getInstance('SunX509')
    new File(PATH).withInputStream { stream ->
        tks.load(stream, PASSWORD.toCharArray())
    }
    tmf.init(tks)
    context.init(null, tmf.trustManagers, null)
    HttpsURLConnection.defaultSSLSocketFactory = context.socketFactory
    HttpsURLConnection.setDefaultHostnameVerifier(new TrustHostnameVerifierInbox())
}

def prepareRequestPOST(HttpsURLConnection response, String data, boolean isConnect = false) {
    byte[] postData = data.getBytes(Charset.forName("utf-8"));
    response.setDoOutput(true);
    response.setInstanceFollowRedirects(false);
    response.setRequestMethod("POST");
    if (isConnect){
        response.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    }else{
        response.setRequestProperty("Content-Type", "application/json-patch+json")
    }
    response.setRequestProperty("charset", "utf-8");
    response.setRequestProperty("Content-Length", Integer.toString(postData.length))
    response.setUseCaches(false);
    def outStream = response.getOutputStream()
    outStream.write(postData)
    outStream.close()
}

HttpsURLConnection prepareConnectWithToken(String url, String token) {
    def response = (HttpsURLConnection) new URL(url).openConnection()
    response.setRequestProperty("Authorization", token);
    return response
}

def checkAddressByDadata(String address, int index ) {
    def response = ["curl", "-X", "POST", "-H", "Content-Type: application/json", "-H", TOKEN[index], "-H", SECRET[index], "-d", ["\"" + address + "\""], "https://cleaner.dadata.ru/api/v1/clean/address"].execute().text
    return response
}

List loadInboxData(String token) {
    def response = prepareConnectWithToken(baseUrl + "Inbox", token)
    List<Inbox> result = new ArrayList<>();
    if (response.responseCode == 200) {
        def text = response.inputStream.text
        result = prepareInboxObjects(jsonSlurper.parseText(text))
        if (result.size() > 0) {
            logger.info("${LOG_PREFIX} На обработку поступили новые обращения в количестве ${result.size()}  шт.")
            return result
        }
        logger.info("${LOG_PREFIX} Новых обращений НЕТ")
    } else {
        logger.error("${LOG_PREFIX} Ошибка в запросе при загружке списка обращений, код ошибки: ${response.responseCode}, ошибка: ${response.errorStream.text}")
    }
    return result
}

List<Inbox> prepareInboxObjects(List list) {
    if (list == null) return new ArrayList<Inbox>();
    List<Inbox> result = new ArrayList<>()
    list.each { def item ->
        Inbox obj = new Inbox(item.Guid, item.Created, item.Type)
        result.add(obj)
    }
    return result
}

InboxCard appealProcessing(String url, String token, String guid) {
    InboxCard card
    def response = prepareConnectWithToken(url, token)
    if (response.responseCode == 200) {
        def text = jsonSlurper.parseText(response.inputStream.text)
        card = text as InboxCard
        return card
    } else {
        logger.error("${LOG_PREFIX} Ошибка в запросе при получении обращения, код ошибки: ${response.responseCode}, guid: ${guid}, ошибка: ${response?.errorStream?.text}")
    }
    return card
}

private Date parseDateTimeFromString(obj) {
    return Date.parse(DATE_TIME_FORMAT, LocalDateTime.parse(obj.toString().replaceAll("\\s", "T")).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toString())
}
private Date parseDateFromString(obj) {
    return Date.parse(DATE_FORMAT, LocalDate.parse(obj.toString()).format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString())
}

def getCatalogItem(String catalogName, String directoryName, String id){
    def catalog = utils.find('SadkoCatalog$' + catalogName, [itemId:id])[0]
    String itemName = catalog.itemMap == 'empty' ? catalog.itemName : catalog.itemMap
    def directoryItem = utils.find(directoryName, [title: itemName])[0]
    return directoryItem
}

String checkFieldOnNull(String item){
    return item == null ? "Текст отсутсвует" : item
}

def GetObjHouse(String address){
    //Первый этап поиска с помощью Dadata.ru
    def parse = jsonSlurper.parseText(checkAddressByDadata(address, 0))
    if (parse instanceof Map){
        parse = jsonSlurper.parseText(checkAddressByDadata(address, 1))
    }
    def houseData, house_fias_id, homeData, cityData
    if (parse instanceof ArrayList) {
        if (parse[0].house != null){
            String house = parse[0].house
            if(house.isInteger()) {
                houseData = house as Integer
            }
        }
        house_fias_id = parse[0].house_fias_id
        cityData = parse[0].city
        if (parse[0].flat != null){
            String flat = parse[0].flat
            if(flat.isInteger()) {
                homeData = flat as Integer
            }
        }
    }
    if (house_fias_id != null) {
        def obj = utils.find('Location$house', [fias: house_fias_id])[0]
        if (obj != null) return [obj, house_fias_id, homeData]
    }

    //Второй этап поиска с помощью регулярный выражений
    def house = PrepareAddress.matchAddress("(д|Д|дом|Дом|ДОМ)", address)
    def home = PrepareAddress.matchAddress("(кв|КВ|квартира|Квартира|квар|Квар)", address, false, true)
    def street = PrepareAddress.matchAddress("(ул|Ул|улица|Улица|у|У|УЛИЦА|пр|Пр|Проспект|проспект|Просп|просп|пер|Пер|переулок|Переулок)", address, true)
    def city = PrepareAddress.matchAddress("(Город|город|г|гор|Гор|деревня|Деревня|дер|Дер|д|Д|c|C|село|Село)", address, true)

    if (house != null){
        def houses = utils.find('Location$house', [title: house])
        if (houses == null) return null
        for(def val: houses){
            def str = val.stid
            try {
                def isFind = PrepareAddress.checkMatchAddress(street != null ? street as String : city as String, address, str)
                if(isFind){
                    return  [val, val.fias, house]
                }
            } catch (Exception ignored) {}
        }
    }
    return null
}

boolean prepareToDb(InboxCard card) {
    if (card.Card != null || card.Letter != null){
        def obj = pushToMediumTable(card.Card != null ? card.Card : card.Letter)
        def appeal = createAppeal(card.Card != null ? card.Card : card.Letter)
        if (appeal[0] != null){
            Map<Object, Object> updateData = new HashMap<>()
            if (obj.Appeal == null){
                updateData.put('Appeal', appeal[0])
            }
            if (appeal[1]){
                updateData.put('Status', utils.find('SadkoStatus', [code:'code2']))
            }else{
                updateData.put('Status', utils.find('SadkoStatus', [code:'code5']))
            }
            utils.edit(obj.UUID, updateData)
        }
        attachmentFiles(card.Card != null ? card.Card : card.Letter, appeal[0])
        return true
    }
    return false
}

def attachmentFiles(iCard card, obj){
    def closure = {
        utils.get(obj.docpack.UUID[0])
    }
    def dockpack = api.tx.call(closure)
    if (obj.docpack.UUID[0] == null) return
    if (card.Files.size() > 0){
        for (def item : card.Files){
            byte[] file = Base64.decoder.decode(item.Data);
            def attachedFiles = {
                utils.attachFile(dockpack, item.Name, '', '', file)
            }
            def attachedFile = api.tx.call(attachedFiles)
            if (attachedFile != null){
                logger.info("${LOG_PREFIX} Файл с именем ${item.Name} прикреплен к обращению ${obj.title} ${attachedFile.UUID}")
                Map<Object, Object> updateData = new HashMap<>()
                updateData.put('markInSadko', 'да')
                obj = utils.edit(attachedFile.UUID, updateData)
            }else{
                logger.error("${LOG_PREFIX} Файл с именем ${item.Name} не прикрепился к обращению ${obj.title}")
            }
        }
    }
}

def createAppeal(iCard card){
    boolean isCorrectlyAddress = false
    Map<Object, Object> updateData = new HashMap<>()
    if (card instanceof Resol) {
        updateData.put('GuidSadko', card.Guid)
        updateData.put('RegNumSD', card.RegistrationNumber)
        updateData.put('typeAp', getCatalogItem('LetterTypes', 'appealType', card.LetterTypeId))
        updateData.put('viewAp', getCatalogItem('DocumentTypes', 'viewAp', card.DocumentTypeId))
        updateData.put('reporter', getCatalogItem('Correspondents', 'reporter', card.CorrespondentId))
        if (card.ControlOrgSendDate != null) {
            updateData.put('MessageDate', parseDateTimeFromString(card.ControlOrgSendDate))
        }
        if (card.ControlOrgSendDate != null) {
            updateData.put('registerDate', parseDateTimeFromString(card.RegistrationDate))
        }
    }
    updateData.put('LastName', card.CitizenName)
    updateData.put('FirstName', card.CitizenSurname)
    updateData.put('MiddleName', card.CitizenPatronymic)
    updateData.put('phoneNumber', card.CitizenPhone)
    updateData.put('email', card.CitizenEmail)
    updateData.put('state', 'registered')

    def house = GetObjHouse(card.CitizenAddress)
    if (house != null){
        isCorrectlyAddress =  true
        updateData.put('house2', house[0])
        updateData.put('street2', house[0].stid)
        if (card.CitizenAddressPost) updateData.put('indexAddr', card.CitizenAddressPost.trim())
        updateData.put('regionAp', house[0].region)
        updateData.put('organization', house[0].pdid)
        if (house[0].pdid ) updateData.put('typepd', house[0].pdid.pdtype)

        //updateData.put('fiasHouse', house[1])
        updateData.put('room', house[2])
    }

    updateData.put('ansType', getCatalogItem('CitizenAnSeTy', 'ansWay', card.CitizenAnswerSendTypeId))
    updateData.put('deliveryType', getCatalogItem('DeliveryTypes', 'deliveryType', card.DeliveryTypeId))
    updateData.put('MessageNumber', card.LetterNumber)
    updateData.put('descrip', checkFieldOnNull(card.Message))
    updateData.put('fromAp', utils.find('entryPlace', [code: 'en14']))  //TODO Базовое значение -> ГИС САДКО.ОГ
    updateData.put('themes', utils.find('themeInv', [code: 'no']))      //TODO значения нет в САДКО

    updateData.put('ReceiveDateSD', card.ReceiveDate)

    def closure = {
        utils.create('appeal$appeal', updateData);
    }
    def obj = api.tx.call(closure)


    if (obj != null){
        logger.info("${LOG_PREFIX} Обьект в таблице \"Обращения\", \"InboxResol\" создан, ID записи: ${obj.UUID}, Номер: ${obj.title}")
    }else{
        logger.info("${LOG_PREFIX} Обьект в таблице \"Обращения\", \"InboxResol\" не создан, GUID записи: ${card.Guid}")
    }
    return [obj, isCorrectlyAddress]
}

def pushToMediumTable(iCard card){
    Map<Object, Object> updateData = new HashMap<>()
    if (card instanceof Resol){
        updateData.put('Guid', card.Guid)
    }
    updateData.put('CitizenName', card.CitizenName)
    updateData.put('CitizenSurname', card.CitizenSurname)
    updateData.put('CitizenPatrony', card.CitizenPatronymic)
    updateData.put('CitizenAddress', card.CitizenAddress)
    updateData.put('CitizenPhone', card.CitizenPhone)
    updateData.put('CitizenEmail', card.CitizenEmail)
    updateData.put('Status', utils.find('SadkoStatus', [code:'code1']))
    String codeName = card instanceof Resol ? 'InboxResol': 'InboxLetter'
    updateData.put('typeAppeal', utils.find('SadkoTypeApp', [code:codeName]))
    updateData.put('Files', card.Files.size())
    if (card.ReceiveDate != null){
        updateData.put('ReceiveDate', parseDateTimeFromString(card.ReceiveDate))
    }
    updateData.put('Message', card.Message)
    updateData.put('title', card.CitizenName + " " + card.CitizenSurname + " " + card.CitizenPatronymic)

    def obj
    try {
        if (card instanceof Resol) {
            obj = utils.find('SadkoObj$SadkoAppeal', [Guid: card.Guid, typeAppeal: utils.find('SadkoTypeApp', [code:codeName])])[0]
        }else{
            obj = utils.find('SadkoObj$SadkoAppeal', [CitizenName: card.CitizenName, CitizenSurname: card.CitizenSurname,'CitizenPatrony': card.CitizenPatronymic, typeAppeal: utils.find('SadkoTypeApp', [code:codeName])])[0]
        }
    } catch (Exception e) {
        if (card instanceof Resol) {
            logger.error("${LOG_PREFIX} Ошибка поиска обьекта в таблице \"Садко Обращения\":, guid - ${card.Guid}, ошибка: ${e.message}")
        }else{
            logger.error("${LOG_PREFIX} Ошибка поиска обьекта в таблице \"Садко Обращения\", \"InboxLetter\", Адрес записи: ${card.CitizenAddress}, ошибка: ${e.message}")
        }
    }

    if (obj == null){
        def closure = {
            utils.create('SadkoObj$SadkoAppeal', updateData);
        }
        obj = api.tx.call(closure)
        if (card instanceof Resol) {
            logger.info("${LOG_PREFIX} Обьект в таблице \"Садко Обращения\", \"InboxResol\" создан, ID записи: ${card.Guid}")
        }else{
            logger.info("${LOG_PREFIX} Обьект в таблице \"Садко Обращения\", \"InboxLetter\"  создан, Адрес записи: ${card.CitizenAddress}")
        }
    }else{
        def closure = {
            utils.edit(obj.UUID, updateData)
        }
        obj = api.tx.call(closure)
        if (card instanceof Resol) {
            logger.info("${LOG_PREFIX} Обьект в таблице \"Садко Обращения\" , \"InboxResol\" обновлен, ID записи: ${card.Guid}")
        }else{
            logger.info("${LOG_PREFIX} Обьект в таблице \"Садко Обращения\", \"InboxLetter\" обновлен, Адрес записи:${card.CitizenAddress}")
        }
    }
    return obj
}


//def addr = 'обл.Калужская, г.Калуга, ул.Суворова, д.89'
//def parse = jsonSlurper.parseText(checkAddressByDadata(addr, 0))

prepareSSLConnection()
def connection = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(connection, urlConnectParam, true)

if (connection.responseCode == 200) {
    ConnectSADKO connect = jsonSlurper.parseText(connection.inputStream.text) as ConnectSADKO
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token
        def data = loadInboxData(authorization)
        def urlFields = MappingTypeUrl.getMapFields()
        def count = 0
        def guidList = []
        data?.each { inbox ->
            InboxCard card = appealProcessing(baseUrl + urlFields.get(inbox.Type) + "/" + inbox.Guid, authorization, inbox.Guid)
            if (card != null) {
                card.Guid = inbox.Guid
                boolean isLoad = prepareToDb(card)
                if (isLoad){
                    guidList.add("\"" + inbox.Guid + "\"")
                    logger.info("${LOG_PREFIX} Обращение, c атрибутами: тип - ${inbox.Type}, guid - ${inbox.Guid}, загружено")
                }
            }
            count++
        }
        def con = prepareConnectWithToken(baseUrl + 'InboxProcessingConfirmation', authorization)
        prepareRequestPOST(con, guidList.toString())
        if (con.responseCode == 200){
            def result = jsonSlurper.parseText(con.inputStream.text)
            logger.info("${LOG_PREFIX} Процедура подтверждения обработки обращений завершилась успешно: ${result}/${guidList.size()}")
        }else{
            logger.error("${LOG_PREFIX} Ошибка в запросе при подтверждении обработки обращений, код ошибки: ${con.responseCode}, ошибка: ${con?.errorStream?.text}")
        }
    } else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
} else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}")
}
