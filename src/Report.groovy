import groovy.json.JsonSlurper

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import groovy.transform.Field
import java.util.logging.Logger

Logger logger = Logger.getLogger("")

@Field final String DATE_FORMAT = "dd.MM.yyyy"
@Field final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"
@Field final String URL = "" //TODO insert url report rtk

def jsonSlurper = new JsonSlurper()

enum MappingReportt{
    accost_no("accost_no","title"),
    instruction_date("instruction_date","prikazDate"),
    icnt("icnt","prikazNum"),
    creator_fio("creator_fio","prikazIsp"),
    instruction_num("instruction_num","prikazNumbers"),
    act_num("act_num","actNumbers"),
    inspection_date("inspection_date","aktDate"),
    acnt("acnt","aktNum"),
    prescr_date("prescr_date","predpisDate"),
    pcnt("pcnt","predpisNum"),
    protocol_date("protocol_date","protokolDate"),
    dcnt("dcnt","protkolNum"),
    instruction_date2("instruction_date2","prkazPPDate"),
    icnt2("icnt2","prkazPPNum"),
    inspection_date2("inspection_date2","aktPPDate"),
    acnt2("acnt2","aktPPNum"),
    prescr_date2("prescr_date2","predpisPPDate"),
    pcnt2("pcnt2","predpisPPNum"),
    protocol_date2("protocol_date2","protokolPPDate"),
    dcnt2("dcnt2","protokolPPNum"),

    private def name;
    private def desc;

    MappingReport(String name, String desc) {
        this.name = name
        this.desc = desc
    }

    static Map<String, String> getMappingFields() {
        def name = values()*.name
        def decs = values()*.desc
        return [ name, decs ].transpose().collectEntries();
    }

}

class ReportGZHII {
    String accost_no //displayname -> Номер обращения [datatype -> string]      ::       title
    Date instruction_date //displayname -> дата приказа [datatype -> date]      ::       prikazDate
    Float icnt //displayname -> количество приказов [datatype -> float]     ::      prikazNum
    String creator_fio //displayname -> исполнитель из приказа [datatype -> string]     ::      prikazIsp
    String instruction_num //displayname -> Номера приказов [datatype -> string]        ::  [NEW FIELD] prikazNumbers
    String act_num //displayname -> Номера актов [datatype -> string]       ::  [NEW FIELD] actNumbers
    Date inspection_date //displayname -> дата акта [datatype -> date]      ::      aktDate
    Float acnt //displayname -> количество актов проверки [datatype -> float]       ::      aktNum
    Date prescr_date //displayname -> дата предписания [datatype -> date]       ::      predpisDate
    Float pcnt //displayname -> количество предписаний [datatype -> float]      ::      predpisNum
    Date protocol_date //displayname -> дата протокола [datatype -> date]       ::      protokolDate
    Float dcnt //displayname -> количество протоколов [datatype -> float]       ::      protkolNum
    Date instruction_date2 //displayname -> дата приказа ПП [datatype -> date]      ::      prkazPPDate
    Float icnt2 //displayname -> количество приказов ПП [datatype -> float]     ::      prkazPPNum
    Date inspection_date2 //displayname -> дата акта ПП [datatype -> date]      ::      aktPPDate
    Float acnt2 //displayname -> количество актов ПП [datatype -> float]        ::      aktPPNum
    Date prescr_date2 //displayname -> дата предписания ПП [datatype -> date]       ::      predpisPPDate
    Float pcnt2 //displayname -> количество предписаний ПП [datatype -> float]      ::      predpisPPNum
    Date protocol_date2 //displayname -> дата протокола ПП [datatype -> date]       ::      protokolPPDate
    Float dcnt2 //displayname -> количество протоколов ПП [datatype -> float]       ::      protokolPPNum
}

// reading data from url or change url from constant
//String url = System.getenv("urlReport")
//def json = jsonSlurper.parseText(new URL(url).text)

//write to [test]-output.json
//new File("output.json").write(JsonOutput.prettyPrint(new URL(url).text))

//reading data from [test]-output.json
def json = jsonSlurper.parse(new File("output.json"))

private ArrayList<ReportGZHI> parseToObject(Collection data) {
    ArrayList<ReportGZHI> resultReport = prepareResultReport(data)
    for (int i = 0; i < data.size(); i++) {
        def nameField = data[i]["name"]
        def datatype = data[i]["datatype"]
        ArrayList dataObject = data[i]["data"] as ArrayList
        updateProperties(resultReport, nameField, dataObject, datatype)
    }
    return resultReport
}

private ArrayList<ReportGZHI> prepareResultReport(Collection data) {
    ArrayList<ReportGZHI> prepareList = new ArrayList()
    def prepareDataCountItems = data[0]["data"] as ArrayList
    for (int i = 0; i < prepareDataCountItems.size(); i++) {
        prepareList.add(new ReportGZHI())
    }
    return prepareList
}

private void updateProperties(ArrayList data, nameField, ArrayList dataObject, datatype) {
    for (int j = 0; j < data.size(); j++) {
        ReportGZHI reportGZHI = data[j]
        LinkedHashMap<String, Object> properties = reportGZHI.properties
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
                    reportGZHI.setProperty(objectEntry.key, parseToDate);
                } else {
                    reportGZHI.setProperty(objectEntry.key, dataObject[j]);
                }
            }
        }
    }
}

private Date parseDateFromString(obj) {
    Date.parse(DATE_FORMAT, LocalDate.parse(obj.toString()).format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString())
}

private Date parseDateTimeFromString(obj) {
    Date.parse(DATE_TIME_FORMAT, LocalDateTime.parse(obj.toString().replaceAll("\\s", "T")).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toString())
}

private void updateReportInDB(ArrayList<ReportGZHI> list) {
    def mapping = MappingReport.getMappingFields()
    for (ReportGZHI reportGZHI : list) {
//        if (reportGZHI.accost_no != null) {
//            def obj = utils.find('appeal', [title: reportGZHI.accost_no])[0]
//            if (obj != null){
//                def uuid = obj.UUID
//                Map<Object, Object> updateData = new HashMap<>()
//                LinkedHashMap<String, Object> properties  = reportGZHI.properties
//                for (Map.Entry<String, Object> map: properties.entrySet() ) {
//                    if (map.value != null) {
//                        for (Map.Entry<String, Object> props : mapping.entrySet()) {
//                            if (props.key == map.key) {
//                                if (props.key == map.key) {
//                                    if(props.key == "prikazIsp") {
//                                        updatePrikazIspFIO(reportGZHI, obj)
//                                    }else {
//                                        updateData.put(props.value, map.value)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                utils.edit(uuid, updateData)
//                logger.info(obj.title +" "+ uuid + " updated from rtk report")
//            }
//
//            //testRun(list, mapping)
//
//        }
    }
}

//private void updatePrikazIspFIO(ReportGZHI reportGZHI, obj) {
//    def splitFIO = reportGZHI.creator_fio.split(" ")
//    Map<Object, Object> updateFIO = new HashMap<>()
//    updateFIO.put("lastName", splitFIO[0])
//    updateFIO.put("firstName", splitFIO[1])
//    updateFIO.put("middleName", splitFIO[2])
//    utils.edit(obj.prikazIsp.UUID, updateFIO)
//}

private void testRun(ArrayList<ReportGZHI> list, Map<String, String> mapping) {
    def report = list[15]
    Map<Object, Object> updateData = new HashMap<>()
    LinkedHashMap<String, Object> properties = report.properties
    for (Map.Entry<String, Object> map : properties.entrySet()) {
        if (map.value != null) {
            for (Map.Entry<String, Object> props : mapping.entrySet()) {
                if (props.key == map.key) {
                    updateData.put(props.value, map.value)
                }
            }
        }
    }
    println(updateData)
    println(mapping)
}

//def json = jsonSlurper.parseText(new URL(URL).text)
if (json["error"] == 0) {
    ArrayList data = json["fields"].collect()
    ArrayList<ReportGZHI> result = parseToObject(data)
    updateReportInDB(result)
    //return result.size
    logger.info("Всего обработано: " + result.size.toString() + " обращений")
}else{
    logger.error("Ошибки при чтении данных с сервиса РТК,  " + json["error"])
}