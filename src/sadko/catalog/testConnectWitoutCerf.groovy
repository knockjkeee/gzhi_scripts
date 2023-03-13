package sadko.catalog

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom


def sc = SSLContext.getInstance("SSL")
def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
hostnameVerifier = [verify: { hostname, session -> true }] as HostnameVerifier
HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

def url = "https://62.148.156.120:59456/api/Dir/GetCitizenAddressAreas"
def secondRequest = new URL(url).openConnection() as HttpURLConnection

return secondRequest.responseCode
return secondRequest.inputStream

if(secondRequest.responseCode == 200){
    println(secondRequest.inputStream.text)
    logger.error(secondRequest.inputStream.text)
}

