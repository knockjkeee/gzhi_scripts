package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.security.KeyStore
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web

@Field final JsonSlurper jsonSlurper = new JsonSlurper()



class GetAddControlMeasuresData {
    public String = ""
    private static Map data = [
            "Не определено"             : "",
            "До выполнения рекомендаций": "",
            "До принятия решения"       : "",
            "До принятия мер"           : "",
    ]

    static Map getData() {
        return data
    }
}

class GetCitizenAddressAreasData {
    public String = "Район обращения"
    private static Map data = [
            "Не определено"   : "Интернет приемная", //todo check field
            "Калуга"          : "",
            "Обнинск"         : "",
            "Бабынинский"     : "",
            "Барятинский"     : "",
            "Малоярославецкий": "",
            "Тарусский"       : "",
            "Хвастовичский"   : "",
            "Мещовский"       : "",
            "Медынский"       : "",
            "Перемышльский"   : "",
            "Ферзиковский"    : "",
            "Дзержинский"     : "",
            "Износковский"    : "",
            "Жиздринский"     : "",
            "Кировский"       : "",
            "Ульяновский"     : "",
            "Боровский"       : "",
            "Козельский"      : "",
            "Мосальский"      : "",
            "Спас-Деменский"  : "",
            "Думиничский"     : "",
            "Жуковский"       : "",
            "Куйбышевский"    : "",
            "Сухиничский"     : "",
            "Юхновский"       : "",
            "Людиновский"     : "",
            "Другие регионы"  : "Из других субъектов РФ" //todo check field
    ]

    static Map getData() {
        return data
    }
}

class GetCitizenAnswerSendTypesData {
    public String name = "Способ ответа"
    private static Map data = [
            "Не определено"                                 : "Нет", //todo check field
            "Электронная почта"                             : "",
            "Почта России"                                  : "",
            "Комбинированный вариант (Почта России + email)": "Почта России + электронная почта" //todo check field
    ]

    static Map getData() {
        return data
    }
}

class GetCitizenBenefitsData {
    public String name = "Льготный состав"
    private static Map data = [
            "Не определено"                     : "Нет", //todo check field
            "Многодетная семья"                 : "многодетная семья",
            "Ветеран военной службы"            : "",
            "Ветеран государственной службы"    : "",
            "Участник ВОВ"                      : "",
            "Инвалид в/с"                       : "инв. в/с",
            "Инвалид по общему  заболеванию	": null,
            "Воин-интернацианалист"             : "",
            "Пострадавший от стихийных бедствий": "пострадавший от стихийных бедствий",
            "Мать-одиночка"                     : "мать-одиночка",
            "Инвалид с детства"                 : "инв. с детства",
            "Репрессированный"                  : "",
            "Вынужденный переселенец"           : "вынужденный переселенец",
            "Ликвидатор"                        : null,
            "Сирота"                            : "",
            "Малолетний узник"                  : "малолетний узник",
            "Опекун"                            : "",
            "Ветеран труда"                     : "",
            "Труженик тыла"                     : "",
            "ИВОВ"                              : "ИВОВ",
            "Инвалид труда"                     : null,
    ]

    static Map getData() {
        return data
    }
}

class GetCitizenSocialStatusesData {
    public String name = "Социальный статус"
    private static Map data = [
            "Не определено"                     : "Нет", //todo check field
            "Беженец"                           : "беженец",
            "Без определенного места жительства": "без определенного место жительства",
            "Безработный"                       : "безработный",
            "Военнослужащий"                    : "военнослужащий",
            "Временно не работает"              : "временно не работает",
            "Вынужденный переселенец"           : "вынужденный переселенец",
            "Домохозяйка"                       : "домохозяйка",
            "Другие"                            : "другие",
            "Заключенный"                       : "заключенный",
            "Общественно-политический деятель"  : "общественно-логический деятель",
            "Пенсионер - МВД"                   : "пенсионер - МВД",
            "Пенсионер - МО"                    : "пенсионер - МО",
            "Пенсионер - ФСБ"                   : "пенсионер - ФСБ",
            "Пенсионер"                         : "пенсионер",
            "Предприниматель"                   : "предприниматель",
            "Рабочий"                           : "рабочий",
            "Рабочий с/х"                       : "рабочий с/х",
            "Священнослужитель"                 : "священнослужитель",
            "Служащий"                          : "служащий",
            "Студент"                           : "студент",
            "Творческая и научная интеллигенция": "творческая и научная интеллигенция",
            "Учащийся"                          : "учащийся",
            "Фермер"                            : "фермер",
    ]

    static Map getData() {
        return data
    }
}

class GetConsiderationFormsData {
    public String name = "Форма рассмотрения"
    private static Map data = [
            "Не определено"                 : "Нет", //todo check field
            "Приём в режиме конференц-связи": "",
            "Мобильная приёмная президента" : "",
            "Выездной приём"                : "",
            "Личный прием"                  : "",
    ]

    static Map getData() {
        return data
    }
}

class GetCorrespondentsData {
    public String name = "Корреспондент"
    private static Map data = [
            "Не определено"                                                                                      : "Нет", //todo check field
            "Областные ходатайства"                                                                              : "",
            "Областные организации"                                                                              : "",
            "От граждан"                                                                                         : "",
            "Администрация Президента Российской Федерации"                                                      : "",
            "Государственная Дума Российской Федерации"                                                          : "",
            "Министерства и ведомства"                                                                           : "",
            "Правительство РФ"                                                                                   : "",
            "Федеральное собрание РФ	"                                                                        : "",
            "Депутаты областной Думы"                                                                            : "",
            "Представитель Президента  РФ"                                                                       : "",
            "Прокуратура Калужской области"                                                                      : "",
            "Редакции газет и журналов"                                                                          : "",
            "Уполном.по правам  человека в Кал.обл."                                                             : "Полном.предст.Презид. РФ в Кал. обл.",
            "Упол.РФ при  Европ. суде"                                                                           : "",
            "Аппарат Уполномоченного по правам человека в Российской Федерации"                                  : "Аппарат уполномоченного по правам человека в РФ",
            "Общественная Палата Российской Федерации"                                                           : "",
            "Полном.предст.Презид. РФ в Кал.обл."                                                                : null,
            "Приемная Президента РФ в Калужской обл."                                                            : "",
            "Аппарат Правительства Российской Федерации"                                                         : "",
            "Аппарат полномочного представителя Президента Российской Федерации в Центральном федеральном округе": "Аппарат полномочного предст. Президента РФ в Центральном округе",
            "Совет Федерации Федерального Собрания Российской Федерации	"                                     : "",
            "Всероссийская политической партии «Единая Россия»"                                                  : null,
            "Министерство строительства и жилищно-коммунального хозяйства Российской Федерации"                  : "Министерство строительства и ЖКХ РФ",
            "Министерство здравоохранения Российской Федерации"                                                  : "Министерство Здравоохранения РФ",
            "Министерство природных ресурсов и экологии Российской Федерации	"                                : "Министерство природных ресурсов и экологии РФ",
            "Министерство труда и социальной защиты Российской Федерации"                                        : "Министерство труда и социальной защиты РФ",
            "Министерство экономического развития Российской Федерации"                                          : "Министерство экономического развития РФ",
            "Министерство обороны Российской Федерации"                                                          : "",
            "Администрация Губернатора Московской области"                                                       : "",
            "Администрация Губернатора Калужской области"                                                        : "",
            "Городская Дума города Калуги"                                                                       : "",
            "Законодательное Собрание Калужской области"                                                         : "",
            "Иностранный гражданин"                                                                              : "",
            "министерство природных ресурсов и экологии Калужской области"                                       : null,
            "Министерство экономического развития Калужской области"                                             : null,
            "Министерство дорожного хозяйства Калужской области"                                                 : null,
            "Министерство спорта Калужской области"                                                              : null,
            "Министерство строительства и жилищно-коммунального хозяйства Калужской области"                     : null,
            "министерство сельского хозяйства Калужской области"                                                 : null,
            "Главное управление МЧС России по Калужской области"                                                 : null,
            "Управление Роспотребнадзора по Калужской области"                                                   : null,
            "Управление Росприроднадзора по Калужской области"                                                   : null,
            "Управление Россельхознадзора по Калужской области"                                                  : null,
            "Управление Ростехнадзора по Калужской области"                                                      : null,
            "Государственная жилищная инспекция Калужской области"                                               : null,
            "null_1"                                                                                             : "Уполном. по правам человека в Калужской области", //todo check field
            "null_2"                                                                                             : "Законодательное собрание области" //todo check field
    ]

    static Map getData() {
        return data
    }
}

class GetDecisionsData {
    public String name = ""
    private static Map data = [
            "Не определено"                                 : "Нет", //todo check field
            "Рассмотреть по компетенции"                    : "",
            "Рассмотреть с запросом документов и материалов": "",
            "Направить по компетенции"                      : "",
            "Направить по компетенции с запросом результата": "",
            "Запрос информации"                             : "",
    ]

    static Map getData() {
        return data
    }
}

class GetDeliveryTypesData {
    public String name = "Тип доставки"
    private static Map data = [
            "Не определено"        : "Нет", //todo check field
            "МЭДО"                 : "",
            "Эл. почта"            : "Электронная почта",
            "Портал КО"            : "",
            "Личный приём"         : "Личный прием",
            "Бумажный носитель"    : "",
            "Эл. почта - соц. сеть": null,
            "null_0"               : "Call-центр 112",
            "null_1"               : "ГИС ЖКХ",
            "null_2"               : "Интернет приемная",
            "null_3"               : "Мобильное приложение",
            "null_4"               : "Наручно",
            "null_5"               : "ОДС инспекции",
            "null_6"               : "Портал Госуслуги",
            "null_7"               : "Почта России",
            "null_8"               : "Телеграф",
            "null_9"               : "Факс",
            "null_10"              : "Фельдъегерская или спец связь"
    ]

    static Map getData() {
        return data
    }
}

class GetDocumentTypesData {
    public String name = "Вид обращения"
    private static Map data = [
            "Не определено"        : "Нет", //todo check field
            "Индивидуальное"       : "",
            "Коллективное"         : "",
            "Коллективно-повторное": "",
            "Повторное"            : "",
    ]

    static Map getData() {
        return data
    }
}

class GetInspectionTypesData {
    public String name = ""
    private static Map data = [
            "Не определено"     : "Нет", //todo check field
            "комиссионно"       : null,
            "с выездом на место": null,
    ]

    static Map getData() {
        return data
    }
}

class GetLetterTypesData {
    public String name = "Тип обращения"
    private static Map data = [
            "Не определено" : "Нет", //todo check field
            "Предложение"   : "",
            "Заявление"     : "",
            "Жалоба"        : "",
            "Запрос"        : "",
            "Запрос по 8-ФЗ": "",
            "Госуслуги"     : "",
            "Не обращение"  : "",
            "null1"         : "Заявление о внесении изменений в реестр",
    ]

    static Map getData() {
        return data
    }
}

class GetResultsData {
    public String name = ""
    private static Map data = [
            "Не определено": "Нет", //todo check field
            "Поддержано"   : null,
            "Не поддержано": null,
            "Разъяснено"   : null,
    ]

    static Map getData() {
        return data
    }
}

class GetSstuStatusesData {
    public String name = ""
    private static Map data = [
            "Не определено"              : "Нет",  //todo check field
            "Не поступило"               : null,
            "Не зарегистрировано"        : null,
            "Находится на рассмотрении"  : null,
            "Рассмотрено. Разъяснено"    : null,
            "Рассмотрено. Поддержано"    : null,
            "Рассмотрено. Не поддержано" : null,
            "Направлено по компетенции"  : null,
            "Дан ответ автору"           : null,
            "Оставлено без ответа автору": null,
            "Рассмотрение продлено"      : null,
    ]

    static Map getData() {
        return data
    }
}

class GetTakenMeasuresData {
    public String name = ""
    private static Map data = [
            "Не определено"  : "Нет",  //todo check field
            "принятые меры 1": null,
            "принятые меры 2": null,
    ]

    static Map getData() {
        return data
    }
}

enum Catalog {
    GetAddControlMeasures("Справочник Дополнительные меры контроля", GetAddControlMeasuresData.getData()),
    GetCitizenAddressAreas("Справочник районов", GetCitizenAddressAreasData.getData()),
    GetCitizenAnswerSendTypes("Справочник способов ответа гражданину", GetCitizenAnswerSendTypesData.getData()),
    GetCitizenBenefits("Справочник льготного состава", GetCitizenBenefitsData.getData()),
    GetCitizenSocialStatuses("Справочник социальных статусов", GetCitizenSocialStatusesData.getData()),
    GetConsiderationForms("Справочник формы рассмотрения", GetConsiderationFormsData.getData()),
    GetCorrespondents("Справочник корреспондентов", GetCorrespondentsData.getData()),
    GetDecisions("Справочник решений по резолюции", GetDecisionsData.getData()),
    GetDeliveryTypes("Справочник типов доставки", GetDeliveryTypesData.getData()),
    GetDocumentTypes("Справочник видов обращения", GetDocumentTypesData.getData()),
    GetInspectionTypes("Справочник Типы проверки", GetInspectionTypesData.getData()),
    GetLetterTypes("Справочник типов обращения", GetLetterTypesData.getData()),
    GetResults("Справочник Результат рассмотрения", GetResultsData.getData()),
    GetSstuStatuses("Справочник Статус обращения (ССТУ)", GetSstuStatusesData.getData()),
    GetTakenMeasures("Справочник Принятые меры", GetTakenMeasuresData.getData()),

    private def desc;
    private Map data;

    Catalog(String desc, Map data) {
        this.desc = desc
        this.data = data
    }
}

class TrustHostnameVerifier implements HostnameVerifier {
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

def parseData(String response, Catalog item) {
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

        parseData(response.inputStream.text, item)
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
