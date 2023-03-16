package sadko.catalog

import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.SecureRandom
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web

@Field final JsonSlurper jsonSlurper = new JsonSlurper()
@Field final String LOG_PREFIX = "[САДКО: синхронизации справочников] "
@Field final String SADCO_CATALOG = "SadkoCatalog" + '$'

final String baseUrl = utils.get('root', [:]).urlSadko + "api/Dir/"
final String connectUrl = utils.get('root', [:]).urlSadko + "connect/token"
final String urlConnectParam = utils.get('root', [:]).authSadko


class GetAddControlMeasuresData {
    public String = ""
    private static Map data = [
            "Не определено"             : "Нет", //todo check field
            "До выполнения рекомендаций": null,
            "До принятия решения"       : null,
            "До принятия мер"           : null,
    ]

    static Map getData() {
        return data
    }
}

class GetCitizenAddressAreasData {
    public String = "Район обращения"
    private static Map data = [
            "Не определено"   : "Интернет приемная", //todo check field
            "Калуга"          : "empty",
            "Обнинск"         : "empty",
            "Бабынинский"     : "empty",
            "Барятинский"     : "empty",
            "Малоярославецкий": "empty",
            "Тарусский"       : "empty",
            "Хвастовичский"   : "empty",
            "Мещовский"       : "empty",
            "Медынский"       : "empty",
            "Перемышльский"   : "empty",
            "Ферзиковский"    : "empty",
            "Дзержинский"     : "empty",
            "Износковский"    : "empty",
            "Жиздринский"     : "empty",
            "Кировский"       : "empty",
            "Ульяновский"     : "empty",
            "Боровский"       : "empty",
            "Козельский"      : "empty",
            "Мосальский"      : "empty",
            "Спас-Деменский"  : "empty",
            "Думиничский"     : "empty",
            "Жуковский"       : "empty",
            "Куйбышевский"    : "empty",
            "Сухиничский"     : "empty",
            "Юхновский"       : "empty",
            "Людиновский"     : "empty",
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
            "Электронная почта"                             : "empty",
            "Почта России"                                  : "empty",
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
            "Ветеран военной службы"            : "empty",
            "Ветеран государственной службы"    : "empty",
            "Участник ВОВ"                      : "empty",
            "Инвалид в/с"                       : "инв. в/с",
            "Инвалид по общему  заболеванию	": null,
            "Воин-интернацианалист"             : "empty",
            "Пострадавший от стихийных бедствий": "пострадавший от стихийных бедствий",
            "Мать-одиночка"                     : "мать-одиночка",
            "Инвалид с детства"                 : "инв. с детства",
            "Репрессированный"                  : "empty",
            "Вынужденный переселенец"           : "вынужденный переселенец",
            "Ликвидатор"                        : null,
            "Сирота"                            : "empty",
            "Малолетний узник"                  : "малолетний узник",
            "Опекун"                            : "empty",
            "Ветеран труда"                     : "empty",
            "Труженик тыла"                     : "empty",
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
            "Приём в режиме конференц-связи": "empty",
            "Мобильная приёмная президента" : "empty",
            "Выездной приём"                : "empty",
            "Личный прием"                  : "empty",
    ]

    static Map getData() {
        return data
    }
}

class GetCorrespondentsData {
    public String name = "Корреспондент"
    private static Map data = [
            "Не определено"                                                                                      : "Нет", //todo check field
            "Областные ходатайства"                                                                              : "empty",
            "Областные организации"                                                                              : "empty",
            "От граждан"                                                                                         : "empty",
            "Администрация Президента Российской Федерации"                                                      : "empty",
            "Государственная Дума Российской Федерации"                                                          : "empty",
            "Министерства и ведомства"                                                                           : "empty",
            "Правительство РФ"                                                                                   : "empty",
            "Федеральное собрание РФ	"                                                                        : "empty",
            "Депутаты областной Думы"                                                                            : "empty",
            "Представитель Президента  РФ"                                                                       : "empty",
            "Прокуратура Калужской области"                                                                      : "empty",
            "Редакции газет и журналов"                                                                          : "empty",
            "Уполном.по правам  человека в Кал.обл."                                                             : "Полном.предст.Презид. РФ в Кал. обл.",
            "Упол.РФ при  Европ. суде"                                                                           : "empty",
            "Аппарат Уполномоченного по правам человека в Российской Федерации"                                  : "Аппарат уполномоченного по правам человека в РФ",
            "Общественная Палата Российской Федерации"                                                           : "empty",
            "Полном.предст.Презид. РФ в Кал.обл."                                                                : null,
            "Приемная Президента РФ в Калужской обл."                                                            : "empty",
            "Аппарат Правительства Российской Федерации"                                                         : "empty",
            "Аппарат полномочного представителя Президента Российской Федерации в Центральном федеральном округе": "Аппарат полномочного предст. Президента РФ в Центральном округе",
            "Совет Федерации Федерального Собрания Российской Федерации	"                                     : "empty",
            "Всероссийская политической партии «Единая Россия»"                                                  : null,
            "Министерство строительства и жилищно-коммунального хозяйства Российской Федерации"                  : "Министерство строительства и ЖКХ РФ",
            "Министерство здравоохранения Российской Федерации"                                                  : "Министерство Здравоохранения РФ",
            "Министерство природных ресурсов и экологии Российской Федерации	"                                : "Министерство природных ресурсов и экологии РФ",
            "Министерство труда и социальной защиты Российской Федерации"                                        : "Министерство труда и социальной защиты РФ",
            "Министерство экономического развития Российской Федерации"                                          : "Министерство экономического развития РФ",
            "Министерство обороны Российской Федерации"                                                          : "empty",
            "Администрация Губернатора Московской области"                                                       : "empty",
            "Администрация Губернатора Калужской области"                                                        : "empty",
            "Городская Дума города Калуги"                                                                       : "empty",
            "Законодательное Собрание Калужской области"                                                         : "empty",
            "Иностранный гражданин"                                                                              : "empty",
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
            "Рассмотреть по компетенции"                    : null,
            "Рассмотреть с запросом документов и материалов": null,
            "Направить по компетенции"                      : null,
            "Направить по компетенции с запросом результата": null,
            "Запрос информации"                             : null,
    ]

    static Map getData() {
        return data
    }
}

class GetDeliveryTypesData {
    public String name = "Тип доставки"
    private static Map data = [
            "Не определено"        : "Нет", //todo check field
            "МЭДО"                 : "empty",
            "Эл. почта"            : "Электронная почта",
            "Портал КО"            : "empty",
            "Личный приём"         : "Личный прием",
            "Бумажный носитель"    : "empty",
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
            "Индивидуальное"       : "empty",
            "Коллективное"         : "empty",
            "Коллективно-повторное": "empty",
            "Повторное"            : "empty",
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
            "Предложение"   : "empty",
            "Заявление"     : "empty",
            "Жалоба"        : "empty",
            "Запрос"        : "empty",
            "Запрос по 8-ФЗ": "empty",
            "Госуслуги"     : "empty",
            "Не обращение"  : "empty",
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
            "Поддержано, в том числе меры приняты"   : null,
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

/**
 * Перечисление справочников
 */
enum Catalog {
    GetAddControlMeasures("ControlMeasure","Справочник Дополнительные меры контроля", GetAddControlMeasuresData.getData()),
    GetCitizenAddressAreas("CitizenAddArea","Справочник районов", GetCitizenAddressAreasData.getData()),
    GetCitizenAnswerSendTypes("CitizenAnSeTy","Справочник способов ответа гражданину", GetCitizenAnswerSendTypesData.getData()),
    GetCitizenBenefits("CitizenBenefit","Справочник льготного состава", GetCitizenBenefitsData.getData()),
    GetCitizenSocialStatuses("CitizenSocStat","Справочник социальных статусов", GetCitizenSocialStatusesData.getData()),
    GetConsiderationForms("ConsiderationF","Справочник формы рассмотрения", GetConsiderationFormsData.getData()),
    GetCorrespondents("Correspondents","Справочник корреспондентов", GetCorrespondentsData.getData()),
    GetDecisions("Decisions","Справочник решений по резолюции", GetDecisionsData.getData()),
    GetDeliveryTypes("DeliveryTypes","Справочник типов доставки", GetDeliveryTypesData.getData()),
    GetDocumentTypes("DocumentTypes","Справочник видов обращения", GetDocumentTypesData.getData()),
    GetInspectionTypes("InspectionType","Справочник Типы проверки", GetInspectionTypesData.getData()),
    GetLetterTypes("LetterTypes","Справочник типов обращения", GetLetterTypesData.getData()),
    GetResults("Results","Справочник Результат рассмотрения", GetResultsData.getData()),
    GetSstuStatuses("SstuStatuses","Справочник Статус обращения (ССТУ)", GetSstuStatusesData.getData()),
    GetTakenMeasures("TakenMeasures","Справочник Принятые меры", GetTakenMeasuresData.getData()),

    private def tName
    private def desc;
    private Map data;

    Catalog(String tName, String desc, Map data) {
        this.tName = tName
        this.desc = desc
        this.data = data
    }
}

/**
 * Класс проверки host соединения
 */
class TrustHostnameVerifier implements HostnameVerifier {
    @Override
    boolean verify(String hostname, SSLSession session) {
        return hostname == session.peerHost
    }
}

/**
 * Обьект для получения токена от Садко
 */
class ConnectSADKOc {
    String access_token
    int expires_in
    String token_type
    String scope
}

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

/**
 * Проброска токена в header
 */
HttpsURLConnection prepareConnectWithToken(String url, String token) {
    def response = (HttpsURLConnection) new URL(url).openConnection()
    response.setRequestProperty("Authorization", token);
    return response
}

/**
 * Подготовка POST запроса
 */
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

/**
 * Обновление даннызх в базе naumen
 */
def updateDataToDb(ArrayList data, Catalog item) {
    def catalogName = item.desc + " [ " + item.tName + " ]"
    for (def val in data) {
        def id = val.Id
        def name = val.Name
        Map<Object, Object> updateData = new HashMap<>()
        updateData.put("itemId", id)
        updateData.put("itemName", name)
        updateData.put("itemMap", item.data.get(name))

        ////// CREATE OBJ DB //////
        def obj
        try {
            obj = utils.find(SADCO_CATALOG + item.tName, [itemId: id])[0]
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Справочники:" + catalogName + " \", id обьекта: " + id + " ошибка: " + e.message)
        }
        if (obj == null) {
            updateData.put("title", name)
            utils.create(SADCO_CATALOG + item.tName, updateData)
            logger.info(LOG_PREFIX + "Обьект в таблице \"Справочники:" + catalogName + " \" создан, ID записи: " + id)

        } else {
            utils.edit(obj.UUID, updateData)
            logger.info(LOG_PREFIX + "Обьект в таблице \"Справочники:" + catalogName + " \" обновлен, ID записи: " + id)

        }
    }
}

/**
 * Парсинг данных из справочников Садко
 */
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
    } else {
        logger.error(LOG_PREFIX + "Полученный массив из справочника " + catalogName + ", пустой.")
    }
}

/**
 * Загрука каталогов от Садко
 */
def loadCatalog(Catalog item, token, baseUrl) {
    def catalogName = item.desc + " [ " + item.name() + " ]"
    def url = baseUrl + item.name()
    def response = prepareConnectWithToken(url, token)
    if (response.responseCode == 200) {
        parseData(response.inputStream.text, item)
    } else {
        logger.error(LOG_PREFIX + "Ошибка в запросе по справочнику: " + catalogName + ", код ошибки: " + response.responseCode + ", ошибка: " + response?.errorStream?.text)
    }
    logger.info(LOG_PREFIX + catalogName + " загружен")
}

/**
 * Entry point script
 */
prepareSSLConnection()
def connection = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(connection, urlConnectParam, true)

if (connection.responseCode == 200) {
    ConnectSADKOc connect = jsonSlurper.parseText(connection.inputStream.text) as ConnectSADKOc
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token
        def values = Catalog.values()
        for (def item in values) {
            loadCatalog(item, authorization, baseUrl)
        }
        println('Load is completed')
    }else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
}else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}")
}


