package sadko.catalog

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/**
 * First version disable SSL certificate
 */
class UnsafeTrustManager extends X509ExtendedTrustManager {
    @Override
    void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

    }

    @Override
    void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

    }

    @Override
    void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

    }

    @Override
    void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

    }

    @Override
    void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0]
    }
}

SSLSocketFactory getSSLSocketFactory() {
    def sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, new TrustManager[]{new UnsafeTrustManager()}, null)
    def sslSocketFactory = sslContext.getSocketFactory()
    return sslSocketFactory;
}

//todo ========================================

/**
 * Second version disable SSL certificate
 */
def sc = SSLContext.getInstance("SSL")
def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
hostnameVerifier = [verify: { hostname, session -> true }] as HostnameVerifier
HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

//def url = ""
//def firstRequest = (HttpsURLConnection) new URL(url).openConnection()
//firstRequest.setSSLSocketFactory(getSSLSocketFactory())
//if(firstRequest.responseCode == 200){
//    println(firstRequest.inputStream.text)
//    logger.error(firstRequest.inputStream.text)
//}


//def url = ""
//def secondRequest = new URL(url).openConnection() as HttpURLConnection
//if(secondRequest.responseCode == 200){
//    println(secondRequest.inputStream.text)
//    logger.error(secondRequest.inputStream.text)
//}

println('Finish')


