import groovy.json.JsonSlurper
import groovy.transform.Field

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Logger

//Logger logger = Logger.getLogger("") //todo off in web
def version = "0.2"
/*
Подготовка объектов
*/
@Field final String DATE_FORMAT = "dd.MM.yyyy"
@Field final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"
@Field final String LOG_PREFIX = "[Скрипт синхронизации данных из инспекции] "

def isAllPeriod = false;
def date = LocalDateTime.now()
def timeParam = isAllPeriod
        ? "&d1=2020-01-01&d2=" + date.date //todo java8 .date
        : "&d1=" + date.minusDays(60).date + "&d2=" + date.date
String URL = "http://92.50.248.64/insp/restapi/gkh.php?user=test1&pass=pass2&appl=1&action=select&object=docs_pers" + timeParam
def jsonSlurper = new JsonSlurper()

/*
Маппинг Полей класса и полей БД
*/
enum MappingInspectDocs{
    doc_id("doc_id","DocId"),
    doc_no("doc_no","DocNo"),
    doc_date("doc_date","DocDate"),
    doc_type("doc_type","DocType"),
    status("status","DocStatus"),
    accost_id("accost_id","AccostId"),
    accost_no("accost_no","accostNumber"),
    ext_id("ext_id","extId"),
    fileids("fileids","fileids"),
    inserted_by("inserted_by","InsertedBy"),
    note1("note1","note1"),
    person_guid("person_guid","inspector"), //Todo ссылка на сотрудника по id
    sbj_org_id("sbj_org_id","legalentity"), //Todo ссылка на УК

    private def name;
    private def desc;

    MappingInspectDocs(String name, String desc) {
        this.name = name
        this.desc = desc
    }

    static Map<String, String> getMappingFields() {
        def name = values()*.name
        def decs = values()*.desc
        return [ name, decs ].transpose().collectEntries();
    }
}

/*
Базовый класс импорта полей
*/
class InspectDocs {
    int doc_id //displayname -> ID документа [datatype -> int]              :: DocId
    String doc_no //displayname -> Номер документа [datatype -> string]     :: DocNo
    Date doc_date //displayname -> Дата документа [datatype -> date]        :: DocDate
    String doc_type //displayname -> Тип документа [datatype -> string]     :: DocType
    String status //displayname -> Статус [datatype -> string]              :: DocStatus
    int accost_id //displayname -> Код обращения [datatype -> int]          :: AccostId
    String accost_no //displayname -> Титул обращения [datatype -> string]  :: accostNumber
    String ext_id //displayname -> ext_id [datatype -> string]              :: extId
    String fileids //displayname -> Список ID файлов [datatype -> string]   :: fileids
    String inserted_by //displayname -> Автор [datatype -> string]          :: InsertedBy
    String note1 //displayname -> Примечание 1 [datatype -> string]         :: note1
    String person_guid //displayname -> person_guid [datatype -> string]    :: //todo person_guid -> inspector
    String obj_id //obj_id -> id дома [datatype -> string]                  :: //todo id -> RelationClass$HouseObjReport with doc_id
    int sbj_org_id //displayname -> id УК[datatype -> int]                  :: legalentity
}

/*
Маппинг обьектов InspectDocs
*/
private ArrayList<InspectDocs> parseToObject(Collection data) {
    ArrayList<InspectDocs> resultReport = prepareResultReport(data)
    for (int i = 0; i < data.size(); i++) {
        def name = data[i]["name"]
        def datatype = data[i]["datatype"]
        ArrayList dataObject = data[i]["data"] as ArrayList
        updateProperties(resultReport, name, dataObject, datatype)
    }
    return resultReport
}

/*
Иницилизация финального списка обьектов InspectDocs по размеру ответа из сервиса РТК
*/
private ArrayList<InspectDocs> prepareResultReport(Collection data) {
    ArrayList<InspectDocs> prepareList = new ArrayList()
    def prepareDataCountItems = data[0]["data"] as ArrayList
    for (int i = 0; i < prepareDataCountItems.size(); i++) {
        prepareList.add(new InspectDocs())
    }
    return prepareList
}

/*
Сеттинг атрибутов из сервиса РТК к финальному обьекту InspectDocs
 */
private void updateProperties(ArrayList data, nameField, ArrayList dataObject, datatype) {
    for (int j = 0; j < data.size(); j++) {
        InspectDocs inspectDocs = data[j]
        LinkedHashMap<String, Object> properties = inspectDocs.properties
        for (Map.Entry<String, Object> objectEntry : properties.entrySet()) {
            if (objectEntry.key == nameField) {
                if (datatype == "date" && dataObject[j] != null) {
                    Date parseToDate;
                    def obj = dataObject[j]
                    if (obj.toString().length() == 10) {
                        parseToDate = parseDateFromString(obj)
                    } else {
                        parseToDate = parseDateTimeFromString(obj)
                    }
                    inspectDocs.setProperty(objectEntry.key, parseToDate);
                } else {
                    if ( objectEntry.key == "person_guid" && dataObject[j] == null ){
                        continue
                    }
                    if(dataObject[j] == null) continue
                    inspectDocs.setProperty(objectEntry.key, dataObject[j]);
                }
            }
        }
    }
}

/*
Перевод из строки в дату
 */
private Date parseDateFromString(obj) {
    Date.parse(DATE_FORMAT, LocalDate.parse(obj.toString()).format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString())
}

/*
Перевод из строки в дату и время
 */
private Date parseDateTimeFromString(obj) {
    Date.parse(DATE_TIME_FORMAT, LocalDateTime.parse(obj.toString().replaceAll("\\s", "T")).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toString())
}


/*
Обновление или создание информации в результате инспекции
 */
private void updateInspectDocsInDB(ArrayList<InspectDocs> data) {
    def mapping = MappingInspectDocs.getMappingFields()
    for (InspectDocs inspectDocs : data) {
        if (inspectDocs.doc_id != 0) { //TODO проверка поля инспектора

            Map<Object, Object> updateData = new HashMap<>()
            prepareUpdateData(inspectDocs, mapping, updateData)

            // insert/update objReport
            def obj;
            try {
                obj = utils.find('objreport$objreport', [DocId: inspectDocs.doc_id, title: inspectDocs.doc_no, accostNumber: inspectDocs.accost_no])[0]
            } catch (Exception e) {
                logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Отчетность\": " + inspectDocs.doc_id + ", ошибка: " + e.message)
            }  //TODO поиск обьекта инспектор в бд
            if (obj == null){
                utils.create('objreport$objreport', updateData);
                logger.info(LOG_PREFIX + "Обьект в таблице \"Отчетность\"  создан, ID записи: " + inspectDocs.doc_id)
                obj = utils.find('objreport$objreport', [DocId: inspectDocs.doc_id])[0]
            }else{
                utils.edit(obj.UUID, updateData)
                logger.info(LOG_PREFIX + "Обьект в таблице \"Отчетность\"  обновлен, ID записи: " + inspectDocs.doc_id)
            }

            // insert data to RealAppealRep
            if (inspectDocs.accost_no != null) {
                updateRealAppealRep(updateData, inspectDocs, obj)
            }

            // insert data to HouseObjReport
            if (inspectDocs.obj_id != null) {
                updateHouseObjReport(updateData, inspectDocs, obj)
            }

        }
    }
}

/*
Подготовить обьект для БД
 */
private void prepareUpdateData(InspectDocs inspectDocs, Map<String, String> mapping, HashMap<Object, Object> updateData) {
    LinkedHashMap<String, Object> properties = inspectDocs.properties
    for (Map.Entry<String, Object> map : properties.entrySet()) {
        if (map.value != null) {
            for (Map.Entry<String, Object> props : mapping.entrySet()) {
                if (props.key != map.key) continue
                if (map.key == MappingInspectDocs.person_guid.name() && inspectDocs.person_guid != null) {
                    def empl
                    try {
                        empl = utils.find('employee', [guid: inspectDocs.person_guid])[0]
                    } catch (Exception e) {
                        logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Сотрудник\": " + inspectDocs.doc_id + ", ошибка: " + e.message)
                    }
                    if (empl != null) {
                        updateData.put(props.value, empl)
                    }else{
                        logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Сотрудник\": " + inspectDocs.doc_id + ", обьект не создан в базе данных")
                    }
                } else if (map.key == MappingInspectDocs.sbj_org_id.name()){
                    def uk
                    try {
                        uk = utils.find('legalentity$legalentity', [pdid:inspectDocs.sbj_org_id])[0]
                    } catch (Exception e) {
                        logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Управляющая компания\": " + inspectDocs.doc_id + ", ошибка: " + e.message)
                    }
                    if (uk != null) {
                        updateData.put(props.value, uk)
                    }else{
                        logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Управляющая компания\": " + inspectDocs.doc_id + ", обьект не создан в базе данных")
                    }
                }else {
                    updateData.put(props.value, map.value)
                }
            }
        }
    }
    updateData.put("title", inspectDocs.doc_no)
}

/*
Обновить обьект в связи обращения и объекта отчетности
 */
private void updateRealAppealRep(HashMap<Object, Object> updateData, InspectDocs inspectDocs, obj) {
    if (updateData.size() > 0) {
        def rel
        try {
            rel = utils.find('RelationClass$RelAppealRep', [DocsId: inspectDocs.doc_id, title: inspectDocs.doc_no, TitleAppeal:inspectDocs.accost_no])[0]
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Связь обращения и объекта отчетности\": " + inspectDocs.doc_id + ", ошибка: " + e.message)
        }

        def appeal
        try {
            appeal = utils.find('appeal', [title: inspectDocs.accost_no])[0]
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Обращение\": " + inspectDocs.accost_no + ", ошибка: " + e.message)
        }

        Map<Object, Object> updateDataRelAppealRep = new HashMap<>()
        updateDataRelAppealRep.put('DocsId', inspectDocs.doc_id)
        updateDataRelAppealRep.put('TitleAppeal', inspectDocs.accost_no)
        updateDataRelAppealRep.put('ObjReport', obj)
        updateDataRelAppealRep.put("title", inspectDocs.doc_no)
        if (appeal != null)  updateDataRelAppealRep.put('Appeal', appeal)
        if (rel == null) {
            utils.create('RelationClass$RelAppealRep', updateDataRelAppealRep);
            logger.info(LOG_PREFIX + "Запись в обьекте \"Связь обращения и объекта отчетности\"  создана, ID записи: " + inspectDocs.doc_id)
        } else {
            utils.edit(rel.UUID, updateDataRelAppealRep)
            logger.info(LOG_PREFIX + "Запись в обьекте \"Связь обращения и объекта отчетности\"  обновлена, ID записи: " + inspectDocs.doc_id)
        }
    }
}

/*
Обновить обьект в связи дома и объекта отчетности
 */
private void updateHouseObjReport(HashMap<Object, Object> updateData, InspectDocs inspectDocs, obj) {
    if (updateData.size() > 0) {

        def split = inspectDocs.obj_id.split(',')
        for (def item in split) {

            def rel
            try {
                rel = utils.find('RelationClass$HouseObjReport', [DocsId: inspectDocs.doc_id, title: inspectDocs.doc_no, HouseId:item, TitleAppeal:inspectDocs.accost_no])[0]
            } catch (Exception e) {
                logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Связь дома и объекта отчетности\" doc_id: " + inspectDocs.doc_id + ", house_id: " + item + ", ошибка: " + e.message)
                return
            }
            def house
            try {
                house = utils.find('Location$house', [hoid: item])[0]
            } catch(Exception e) {
                logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Дом\" house_id: " + item + ", ошибка: " + e.message)
            }

            Map<Object, Object> updateDataRelAppealRep = new HashMap<>()
            updateDataRelAppealRep.put('DocsId', inspectDocs.doc_id)
            updateDataRelAppealRep.put('ObjReport', obj)
            updateDataRelAppealRep.put('TitleAppeal', inspectDocs.accost_no)
            updateDataRelAppealRep.put("title", inspectDocs.doc_no)
            updateDataRelAppealRep.put("HouseId", item)
            if (house != null)  {
                updateDataRelAppealRep.put('house', house)
            }else{
                logger.error(LOG_PREFIX + "Ошибка поиска обьекта в таблице \"Дом\" doc_id: " + inspectDocs.doc_id + ", house_id: " + item + ", обьект не создан в базе данных")
            }
            if (rel == null) {
                utils.create('RelationClass$HouseObjReport', updateDataRelAppealRep);
                logger.info(LOG_PREFIX + "Запись в обьекте \"Связь дома и объекта отчетности\"  создана, doc_id: " + inspectDocs.doc_id + ", house_id: " + item)
            } else {
                utils.edit(rel.UUID, updateDataRelAppealRep)
                logger.info(LOG_PREFIX + "Запись в обьекте \"Связь дома и объекта отчетности\"  обновлена, doc_id: " + inspectDocs.doc_id + ", house_id: " + item)
            }
        }
    }
}


def json = jsonSlurper.parseText(new URL(URL).text)
if (json["error"] == 0) {
    ArrayList data = json["fields"].collect()
    ArrayList<InspectDocs> result = parseToObject(data)
    updateInspectDocsInDB(result)
    logger.info(LOG_PREFIX + "Всего обработано: " + result.size.toString() + " записей")
} else {
    logger.info(LOG_PREFIX + "Ошибки при чтении данных с сервиса инспекции,  " + json["error"])
}