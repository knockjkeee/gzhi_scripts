package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.logging.Logger

Logger logger = Logger.getLogger("") //todo off in web

enum Catalog {
    GetAddControlMeasures,
    GetCitizenAddressAreas,
    GetCitizenAnswerSendTypes,
    GetCitizenBenefits,
    GetCitizenSocialStatuses,
    GetConsiderationForms,
    GetCorrespondents,
    GetDecisions,
    GetDeliveryTypes,
    GetDocumentTypes,
    GetInspectionTypes,
    GetLetterTypes,
    GetResults,
    GetSstuStatuses,
    GetTakenMeasures,
}

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

@Field final JsonSlurper jsonSlurper = new JsonSlurper()
@Field final String baseUrl = ""
@Field final String LOG_PREFIX = "[САДКО: синхронизации справочников. ] "

SSLSocketFactory getSSLSocketFactory() {
    def sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, new TrustManager[]{new UnsafeTrustManager()}, null)
    def sslSocketFactory = sslContext.getSocketFactory()
    return sslSocketFactory
}

def updateDataToDb(ArrayList data, String catalogName) {

    for (def item in data) {
        def id = item.Id
        def name = item.Name
        Map<Object, Object> updateData = new HashMap<>()
        updateData.put("itemId", id)
        updateData.put("itemName", id)

        def obj
        try {
            obj = utils.find('???$' + catalogName, [itemId: itemId])[0]
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Справочники:" + catalogName + " \", id обьекта: " + id + " ошибка: " + e.message)
        }
        if (obj == null) {
            updateData.put("title", name)
            utils.create('???$' + catalogName, updateData)
            logger.info(LOG_PREFIX + "Обьект в таблице \"Справочники:" + catalogName + " \" создан, ID записи: " + id)

        } else {
            utils.edit(obj.UUID, updateData)
            logger.info(LOG_PREFIX + "Обьект в таблице \"Справочники:" + catalogName + " \" обновлен, ID записи: " + id)

        }
    }
}

def loadData(String response, String catalogName) {
    ArrayList data
    try {
        data = jsonSlurper.parseText(response) as ArrayList
    } catch (Exception e) {
        logger.error(LOG_PREFIX + "Ошибка в получении данных из справочника " + catalogName + ", ошибка: " + e.message)
    }
    if (data.size() > 0) {
        updateDataToDb(data, catalogName)
    } else {
        logger.error(LOG_PREFIX + "Полученный массив из справочника " + catalogName + ", пустой.")
    }
}

def loadCatalog(String catalogName) {
    def url = baseUrl + catalogName
    def response = (HttpsURLConnection) new URL(url).openConnection()
    response.setSSLSocketFactory(getSSLSocketFactory())
    if (response.responseCode == 200) {
        loadData(response.inputStream.text, catalogName)
    } else {
        logger.error(LOG_PREFIX + "Ошибка в запросе по справочнику: " + catalogName + ", код ошибки: " + response.responseCode + ", ошибка: " + response.errorStream.text)
    }
    logger.info(LOG_PREFIX + "Справочник: " + catalogName + " загружен")
}

def values = Catalog.values()
for (def item in values) {
    loadCatalog(item.name())
}

println('Finish')
