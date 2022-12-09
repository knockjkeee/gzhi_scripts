package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManagerFactory
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web




class TrustHostnameVerifiero implements HostnameVerifier {
    @Override
    boolean verify(String hostname, SSLSession session) {
        return hostname == session.peerHost
    }
}

class ConnectSADKOo {
    String access_token
    int expires_in
    String token_type
    String scope
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
    HttpsURLConnection.setDefaultHostnameVerifier(new TrustHostnameVerifiero())
}

HttpsURLConnection prepareConnectWithToken(String url, String token) {
    def response = (HttpsURLConnection) new URL(url).openConnection()
    response.setRequestProperty("Authorization", token);
    return response
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

prepareSSLConnection()
def connection = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(connection, urlConnectParam, true)


if (connection.responseCode == 200) {
    ConnectSADKOo connect = jsonSlurper.parseText(connection.inputStream.text) as ConnectSADKOo
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token
        logger.error("TOKEN -> " + authorization)
//        def values = Catalog.values()
//        for (def item in values) {
//            loadCatalog(item, authorization)
//        }
        println('UpLoad is completed')
    }else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
}else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}")
}