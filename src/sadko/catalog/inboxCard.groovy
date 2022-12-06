package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.logging.Logger
import java.util.regex.Matcher

@Field final Logger logger = Logger.getLogger("") //todo off in web

@Field final JsonSlurper jsonSlurper = new JsonSlurper()




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
}

class Resol {                       // appeal

    String Guid                     // Уникальный идентификатор резолюции todo check New field -> GuidSadko
    // Заявитель
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
    int CitizenAddressAreaId        // ID района по почтовому адресу заявителя -> справочник CitizenAddArea [regionAp]
    String CitizenPhone             // Телефон заявителя -> phoneNumber
    String CitizenEmail             // E-Mail заявителя -> email
    String CitizenSocialStatusId    // ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    String CitizenBenefitId         // ID льготный состав гражданина -> справочник CitizenBenefit todo не используют
    String CitizenAnswerSendTypeId  // Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    String LetterTypeId             // ID типа обращения -> справочник LetterTypes [typeAp]
    String DocumentTypeId           // ID вида обращения -> справочник DocumentTypes [viewAp]
    String CorrespondentId          // ID корреспондента -> справочник Correspondents [reporter]
    String LetterNumber             // Номер сопроводительного письма -> MessageNumber
    String ControlOrgSendDate       // Дата отправки из организации -> MessageDate
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSadko
    String DeliveryTypeId           // Тип доставки -> справочник DeliveryTypes [deliveryType]
    String ConsiderationFormId      // Форма рассмотрения -> справочник ConsiderationF todo не используют
    String ReceivedFrom
    // Поступило из -> todo fromAp справочник Место поступления или receivedfrom (строка) !!!!!!!!!!!!
    String RegistrationNumber       // Регистрационный номер -> todo New field -> RegistrationNumberSadko
    String RegistrationDate         // Дата регистрации -> registerDate
    String PreviousCardsCount       // Количество предыдущих обращений todo New field -> PreviousCardsCountSadko
    String DocSheetNumber           // Количество листов документа  todo New field -> DocSheetNumberSadko
    String DocCopyNumber            // Количество листов приложения todo New field -> DocCopyNumberSadko
    String ConcernedCitizensNumber  // Количество заинтересованных todo New field -> ConcernedCitizensNumberSadko
    String Message                  // Текст обращения -> descrip
    ArrayList Files                 // Файлы -> Павет документов [docpack]
                                    // Прикрепление файла к объекту*/
                                    // def attachedFile = utils.attachFile(obj, fileName, contentType, description, data)
                                    // def str = new String(base64.decodeBase64())
                                    // def attachedFile = utils.attachFile(utils.get(obj.docpack.UUID[0]), "Hello4.txt", '', "Hello", str.getBytes())
}

class Letter {
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

    static boolean checkMatchAddress(String city, String address, String valueDb) {
        def isContainsMain = valueDb.contains(city.trim())
        if (isContainsMain) return isContainsMain
        def isContains = sliceContainsAddress(address, valueDb)
        return isContains
    }

    def static matchAddress(String predicate, String address, boolean isString = false, boolean isHome = false) {

        def preparePattern = isString ? "(\\s|\\.)([А-Я][а-я]+)" : "(\\.|\\s?)\\s*?(\\d+[.*?]*)"
//    def preparePatternRevers = isString ? "([\\wА-Яа-я\\-]+)(\\s|\\.)?" : "(\\d+[.*?]*)\\s*?"
        def preparePatternRevers = isString ? "[A-Я][a-я]+(\\s|\\.)" : "(\\d+[.*?]*)\\s*?"

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

def prepareRequestPOST(HttpsURLConnection response) {
    byte[] postData = urlConnectParam.getBytes(Charset.forName("utf-8"));
    response.setDoOutput(true);
    response.setInstanceFollowRedirects(false);
    response.setRequestMethod("POST");
    response.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    response.setRequestProperty("charset", "utf-8");
    response.setRequestProperty("Content-Length", Integer.toString(postData.length))
    response.setUseCaches(false);
    def outStream = response.getOutputStream()
    outStream.write(postData)
    outStream.close()
}

def checkAddress(String address) {
    def response = ["curl", "-X", "POST", "-H", "Content-Type: application/json", "-H", TOKEN, "-H", SECRET, "-d", ["\"" + address + "\""], "https://cleaner.dadata.ru/api/v1/clean/address"].execute().text
    return response
}

HttpsURLConnection prepareConnectWithToken(String url, String token) {
    def response = (HttpsURLConnection) new URL(url).openConnection()
    response.setRequestProperty("Authorization", token);
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
        logger.error("${LOG_PREFIX} Ошибка в запросе при получении обращения, код ошибки: ${response.responseCode}, guid: ${guid}")
    }
    return card
}

def prepareToDb(InboxCard card) {
    if (card.Card != null) {
        //println(card.Card.CitizenAddress)
        pushResolToDb(card.Card, card.Resolution)
    }
    if (card.Letter != null) {
        //println(card.Letter.CitizenAddress)
        pushLetterToDb(card.Letter)
    }
}

def pushLetterToDb(Letter letter) {

}

def pushResolToDb(Resol resol, Resolution resolution) {
    def obj = ""

}

//
//def encoded = "Hello World".bytes.encodeBase64().toString()
//assert encoded == "SGVsbG8gV29ybGQ="
//def decoded = new String("SGVsbG8gV29ybGQ=".decodeBase64())
//assert decoded == "Hello World"
//

//def address = "Россия, Октябрьский , Псков, Город Кондово Проспект Октябрьский Д 36 к. 1 литера А 203 квар почтовый индекс 180000"
def address = "3. Россия, г Калуга, ул Салтыкова-Щедрина, д 72, кв 15"
def parse = jsonSlurper.parseText(checkAddress(address))
def houseData, house_fias_id, homeData, cityData
if (parse instanceof ArrayList) {
    if (parse[0].house.isInteger()) {
        houseData = parse[0].house as Integer
    }
    house_fias_id = parse[0].house_fias_id
    cityData = parse[0].city
    if (parse[0].flat.isInteger()) {
        homeData = parse[0].flat as Integer
    }

}


//def address = "Иванково деревня,  Качурина, Дом. 14, Дом. 14"
def house = PrepareAddress.matchAddress("(д|Д|дом|Дом|ДОМ)", address)
def home = PrepareAddress.matchAddress("(кв|КВ|квартира|Квартира|квар|Квар)", address, false, true)
def city = PrepareAddress.matchAddress("(Город|город|г|гор|Гор|деревня|Деревня|дер|Дер|д|Д|c|C|село|Село)", address, true)
def isMatchAddress = PrepareAddress.checkMatchAddress(city as String, address, "р-н.Октябрьский, г.Кондрово,") // value = street.title


prepareSSLConnection()
def response = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(response)

if (response.responseCode == 200) {
    ConnectSADKO connect = jsonSlurper.parseText(response.inputStream.text) as ConnectSADKO
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token
        def data = loadInboxData(authorization)
        def urlFields = MappingTypeUrl.getMapFields()
        def count = 0
        data?.each { inbox ->
            if (count > 0) return false
            InboxCard card = appealProcessing(baseUrl + urlFields.get(inbox.Type) + "/" + inbox.Guid, authorization, inbox.Guid)
            if (card != null) {
                prepareToDb(card)
            }
            logger.info("${LOG_PREFIX} Обращение, c атрибутами: тип - ${inbox.Type}, guid - ${inbox.Guid}, загружено")
            count++
        }
    } else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
} else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${response.responseCode}, ошибка: ${response.errorStream.text}")
}


// example prepare address
//def address = "обл.Калужская, р-н.Дзержинский, г.Кондрово, ул.Пушкина, д.728, кв.170, Додо д."


//
//def objj = utils.find('appeal', [title: 'Т-8162-22'])
////return obj.house2.region.UUID
////return obj.street2
////return objj.house2.UUID
//
//
////Район обращения -> obj.house2.region.UUID title -> район
//def res = utils.find('regionAp', [title: 'Бабынинский'])
//
//
//Список домов title -> номер дома
//def houses = utils.find('Location$house', [title: '2'])
////return houses[0].stid
//
////Список домов contains -> улица
//for(def house: houses){
//    def street = house.stid
//    try {
//        def isFind = street.title.contains("Аристово")
//        if(isFind){
//            return house.UUID
//        }
//    } catch (Exception e) {
//    }
//
//
//    //return false
//}
