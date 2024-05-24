import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.LocalDate

//@Field final Logger logger = Logger.getLogger("") //todo off in web

def version = 0.1

@Field final JsonSlurper jsonSlurper = new JsonSlurper()
@Field final String DATE_FORMAT = "dd.MM.yyyy"
@Field final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"
@Field final String LOG_PREFIX = "[САДКО: Inbox обращения] "
@Field final def SECRET = "X-Secret: d98cb729b0b1a32186379f061d1bf051d87c3b45"
@Field final def TOKEN = "Authorization: Token 935721f39df2623052614e726d02c03f196d9b36"


final String baseUrl = utils.get('root', [:]).urlSadko + "api/ExchangeGzi/"
final String connectUrl = utils.get('root', [:]).urlSadko + "connect/token"
final String urlConnectParam = utils.get('root', [:]).authSadko


class ConnectSADKO {
    String access_token
    int expires_in
    String token_type
    String scope


    @Override
    public String toString() {
        return "ConnectSADKO{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", token_type='" + token_type + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
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


    @Override
    public String toString() {
        return "Inbox{" +
                "Guid='" + Guid + '\'' +
                ", Created='" + Created + '\'' +
                ", Type='" + Type + '\'' +
                '}';
    }
}


def prepareSSLConnection() {
    def sc = SSLContext.getInstance("SSL")
    def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
    sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
    def hostnameVerifier = [verify: { hostname, session -> true }] as HostnameVerifier
    HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
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

List loadInboxData(String token, baseUrl) {
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

prepareSSLConnection()
def connection = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(connection, urlConnectParam, true)


if (connection.responseCode == 200) {
    ConnectSADKO connect = jsonSlurper.parseText(connection.inputStream.text) as ConnectSADKO
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token
        def data = loadInboxData(authorization, baseUrl)


        return connect.access_token
    } else {
        def errorText = "${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается"
        logger.error(errorText)
    }
} else {
    def errorText = "${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}"
    logger.error(errorText)
}