package sadko.catalog

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom



/**
 * Подготовка SSL соединения
 */
def prepareSSLConnection() {
    def sc = SSLContext.getInstance("SSL")
    def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
    sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
    def hostnameVerifier = [verify: { hostname, session -> true }] as HostnameVerifier
    HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
}

//def sc = SSLContext.getInstance("SSL")
//def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
//sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
//hostnameVerifier = [verify: { hostname, session -> true }] as HostnameVerifier
//HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
//HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

prepareSSLConnection()

def url = "https://10.6.1.45:443/connect/token"
String urlConnectParam = "client_id=gis_gzi&client_secret=2sIFwd3bxmKGnMekVkky&grant_type=client_credentials"
def secondRequest = new URL(url).openConnection() as HttpURLConnection

byte[] postData = urlConnectParam.getBytes(Charset.forName("utf-8"));
secondRequest.setDoOutput(true);
secondRequest.setInstanceFollowRedirects(false);
secondRequest.setRequestMethod("POST");
    secondRequest.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
secondRequest.setRequestProperty("charset", "utf-8");
secondRequest.setRequestProperty("Content-Length", Integer.toString(postData.length))
secondRequest.setUseCaches(false);
def outStream = secondRequest.getOutputStream()
outStream.write(postData)
outStream.close()


return secondRequest.responseCode
return secondRequest.inputStream

if(secondRequest.responseCode == 200){
    println(secondRequest.inputStream.text)
    logger.error(secondRequest.inputStream.text)
}

