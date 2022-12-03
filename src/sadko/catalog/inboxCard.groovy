package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.logging.Logger

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
    int CitizenSocialStatusId       // ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    int CitizenBenefitId            // ID льготный состав гражданина -> справочник CitizenBenefit todo не используют
    int CitizenAnswerSendTypeId     // Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    int LetterTypeId                // ID типа обращения -> справочник LetterTypes [typeAp]
    int DocumentTypeId              // ID вида обращения -> справочник DocumentTypes [viewAp]
    int CorrespondentId             // ID корреспондента -> справочник Correspondents [reporter]
    String LetterNumber             // Номер сопроводительного письма -> MessageNumber
    String ControlOrgSendDate       // Дата отправки из организации -> MessageDate
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSadko
    int DeliveryTypeId              // Тип доставки -> справочник DeliveryTypes [deliveryType]
    int ConsiderationFormId         // Форма рассмотрения -> справочник ConsiderationF todo не используют
    String ReceivedFrom             // Поступило из -> todo fromAp справочник Место поступления или receivedfrom (строка) !!!!!!!!!!!!
    String RegistrationNumber       // Регистрационный номер -> todo New field -> RegistrationNumberSadko
    String RegistrationDate         // Дата регистрации -> registerDate
    int PreviousCardsCount          // Количество предыдущих обращений todo New field -> PreviousCardsCountSadko
    String DocSheetNumber           // Количество листов документа  todo New field -> DocSheetNumberSadko
    String DocCopyNumber            // Количество листов приложения todo New field -> DocCopyNumberSadko
    int ConcernedCitizensNumber     // Количество заинтересованных todo New field -> ConcernedCitizensNumberSadko
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
    int CitizenSocialStatusId       // ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    int CitizenAnswerSendTypeId     // Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    String CitizenPhone             // Телефон заявителя -> phoneNumber
    String CitizenEmail             // E-Mail заявителя -> email
    String LetterNumber             // Номер сопроводительного письма -> MessageNumber
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSadko
    int DeliveryTypeId              // Тип доставки -> справочник DeliveryTypes [deliveryType]
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
    int DecisionId                  // ID Решения по резолюции -> справочник Decisions todo не используют
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
    static Map<String, String> sliceAddres(String addres) {
        def slice = addres.split(',')
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

    static boolean sliceContainsAddres(String address, String value) {
        def split = address.split(', ')
        for (def item : split) {
            def strings = item.split('\\.')
            if (strings == null || strings.size() == 0) return false
            if (strings.size() > 1) {
                if (value.contains(strings[1])) {
                    return true
                }
            } else {
                if (value.contains(strings[0])) {
                    return true
                }
            }
        }
        return false
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

    static boolean checkMatchAddress(Map<String, String> map, String address, String value) {
        def isContains = sliceContainsAddres(address, value)
        if (isContains) return isContains
        def locally = getLocality(map)
        if (locally == null) return false
        return value.contains(locally)
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
        pushResolToDb(card.Card, card.Resolution)
    }
    if (card.Letter != null) {
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
def address = "обл.Калужская, р-н.Дзержинский, г.Кондрово, ул.Пушкина, д.728, кв.170, Додо д."
def mapAddress = PrepareAddress.sliceAddres(address)
def streetName = PrepareAddress.getStreet(mapAddress)
def houseNumber = mapAddress.get('д')
def isMatch = PrepareAddress.checkMatchAddress(mapAddress, address, "р-н.Дзержинский, г.Кондрово,") // value = street.title


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
