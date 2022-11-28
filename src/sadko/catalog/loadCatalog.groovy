package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.security.KeyStore
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web




enum Catalog {
    GetAddControlMeasures("Справочник Дополнительные меры контроля"),
    GetCitizenAddressAreas("Справочник районов"),
    GetCitizenAnswerSendTypes("Справочник способов ответа гражданину"),
    GetCitizenBenefits("Справочник льготного состава"),
    GetCitizenSocialStatuses("Справочник социальных статусов"),
    GetConsiderationForms("Справочник формы рассмотрения"),
    GetCorrespondents("Справочник корреспондентов"),
    GetDecisions("Справочник решений по резолюции"),
    GetDeliveryTypes("Справочник типов доставки"),
    GetDocumentTypes("Справочник видов обращения"),
    GetInspectionTypes("Справочник Типы проверки"),
    GetLetterTypes("Справочник типов обращения"),
    GetResults("Справочник Результат рассмотрения"),
    GetSstuStatuses("Справочник Статус обращения (ССТУ)"),
    GetTakenMeasures("Справочник Принятые меры"),

    private def desc;

    Catalog(String desc) {
        this.desc = desc
    }
}

class TrustHostnameVerifier implements HostnameVerifier {
    @Override
    boolean verify(String hostname, SSLSession session) {
        return hostname == session.peerHost
    }
}

def prepareSSLConnection(){
    def context = SSLContext.getInstance('SSL')
    def tks = KeyStore.getInstance(KeyStore.defaultType);
    def tmf = TrustManagerFactory.getInstance('SunX509')
    new File (PATH).withInputStream { stream ->
        tks.load(stream, PASSWORD.toCharArray())
    }
    tmf.init(tks)
    context.init(null, tmf.trustManagers, null)
    HttpsURLConnection.defaultSSLSocketFactory = context.socketFactory
    HttpsURLConnection.setDefaultHostnameVerifier(new TrustHostnameVerifier())
}


def updateDataToDb(ArrayList data, Catalog item) {
    def catalogName = item.desc + " [ " + item.name() + " ]"
    for (def val in data) {
        def id = val.Id
        def name = val.Name
        Map<Object, Object> updateData = new HashMap<>()
        updateData.put("itemId", id)
        updateData.put("itemName", id)

//        println(name)
//        dataCSV.add(name)

        //////// CREATE OBJ DB //////
//        def obj
//        try {
//            obj = utils.find('???$' + catalogName, [itemId: itemId])[0]
//        } catch (Exception e) {
//            logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Справочники:" + catalogName + " \", id обьекта: " + id + " ошибка: " + e.message)
//        }
//        if (obj == null) {
//            updateData.put("title", name)
//            utils.create('???$' + catalogName, updateData)
//            logger.info(LOG_PREFIX + "Обьект в таблице \"Справочники:" + catalogName + " \" создан, ID записи: " + id)
//
//        } else {
//            utils.edit(obj.UUID, updateData)
//            logger.info(LOG_PREFIX + "Обьект в таблице \"Справочники:" + catalogName + " \" обновлен, ID записи: " + id)
//
//        }
    }
}

def loadData(String response, Catalog item) {
    def catalogName = item.desc + " [ " + item.name() + " ]"
    ArrayList data
    try {
        data = jsonSlurper.parseText(response) as ArrayList
    } catch (Exception e) {
        logger.error(LOG_PREFIX + "Ошибка в получении данных из справочника " + catalogName + ", ошибка: " + e.message)
    }
    if (data.size() > 0) {
        updateDataToDb(data, item)
//        updateDataToDb(data, item, dataCSV)
    } else {
        logger.error(LOG_PREFIX + "Полученный массив из справочника " + catalogName + ", пустой.")
    }
}

def loadCatalog(Catalog item) {
    def catalogName = item.desc + " [ " + item.name() + " ]"
    def url = baseUrl + item.name()
    def response = (HttpsURLConnection) new URL(url).openConnection()
    if (response.responseCode == 200) {
//        dataCSV.add(" ")
//        dataCSV.add(item.desc)
//        println(LOG_PREFIX  + catalogName + " загружен")

        loadData(response.inputStream.text, item)
//        loadData(response.inputStream.text, item, dataCSV)
    } else {
        logger.error(LOG_PREFIX + "Ошибка в запросе по справочнику: " + catalogName + ", код ошибки: " + response.responseCode + ", ошибка: " + response.errorStream.text)
    }
    logger.info(LOG_PREFIX + catalogName + " загружен")
}

//////// CREATE CATALOG CSV //////
//def createCatalogCSV (dataCSV){
//    def file = new File('/Users/knockjkeee/IdeaProjects/gzhiKaluga/src/sadko/catalog/Catalog.csv')
//    file.createNewFile()
//    dataCSV.forEach(item -> file.append(item + "\n"))
//}
//def dataCSV = []


prepareSSLConnection()
def values = Catalog.values()
for (def item in values) {
//    loadCatalog(item, dataCSV)
    loadCatalog(item)
}
//createCatalogCSV(dataCSV)
println('Finish')
