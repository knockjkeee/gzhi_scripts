package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web

@Field final JsonSlurper jsonSlurper = new JsonSlurper()



enum MappingTypeUrl{
    resolution("resolution","InboxResol"),
    letter("letter","InboxLetter"),

    private def name;
    private def url;

    MappingTypeUrl(String name, String url) {
        this.name = name
        this.url = url
    }

    static Map<String, String> getMapFields() {
        def name = values()*.name
        def decs = values()*.url
        return [ name, decs ].transpose().collectEntries();
    }
}

class InboxCard {
    Card Card
    Resolution Resolution
    Card Letter
}

class Card {
    String Guid
    String CitizenName
    String CitizenSurname
    String CitizenPatronymic
    String CitizenAddress
    String CitizenAddressPost
    int CitizenAddressAreaId //
    String CitizenPhone
    String CitizenEmail
    int CitizenSocialStatusId
    int CitizenBenefitId
    int CitizenAnswerSendTypeId
    int LetterTypeId
    int DocumentTypeId
    int CorrespondentId //
    String LetterNumber
    String ControlOrgSendDate
    String ReceiveDate
    int DeliveryTypeId
    int ConsiderationFormId//
    String ReceivedFrom
    String RegistrationNumber
    String RegistrationDate
    int PreviousCardsCount
    String DocSheetNumber
    String DocCopyNumber
    int ConcernedCitizensNumber
    String Message
    ArrayList Files
}

class Resolution {
    String Guid
    String CreatedTime
    Author Author
    Author Executor
    int DecisionId
    String ResolutionText
    String ControlDate
    ArrayList Themes
    ArrayList Files
}

class Author {
    String Guid
    String LastName
    String FirstName
    String MiddleName
    String FIO
}

class Theme {
    String Code
    String Name
    String Annotation
}

class FileData {
    String Name
    String Guid
    String Data
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
    }else{
        logger.error("${LOG_PREFIX} Ошибка в запросе при получении обращения, код ошибки: ${response.responseCode}, guid: ${guid}")
    }
    return card
}

prepareSSLConnection()
def response = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(response)

if (response.responseCode == 200) {
    ConnectSADKO connect = jsonSlurper.parseText(response.inputStream.text) as ConnectSADKO
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token
        def data = loadInboxData(authorization)
        def urlFields = MappingTypeUrl.getMapFields()
        data.each { inbox ->
            InboxCard card = appealProcessing(baseUrl + urlFields.get(inbox.Type) + "/" + inbox.Guid, authorization, inbox.Guid)
            if (card != null){

            }
            logger.info("${LOG_PREFIX} Обращение, c атрибутами: тип - ${inbox.Type}, guid - ${inbox.Guid}, загружено")
        }
    } else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
} else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${response.responseCode}, ошибка: ${response.errorStream.text}")
}