import groovy.json.JsonSlurper

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import groovy.transform.Field

@Field final String DATE_FORMAT = "dd.MM.yyyy"
@Field final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"

def jsonSlurper = new JsonSlurper()

class ReportGZHI {
    String accost_no //displayname -> Номер обращения [datatype -> string]
    Date instruction_date //displayname -> дата приказа [datatype -> date]
    Float icnt //displayname -> количество приказов [datatype -> float]
    String creator_fio //displayname -> исполнитель из приказа [datatype -> string]
    String instruction_num //displayname -> Номера приказов [datatype -> string]
    String act_num //displayname -> Номера актов [datatype -> string]
    Date inspection_date //displayname -> дата акта [datatype -> date]
    Float acnt //displayname -> количество актов проверки [datatype -> float]
    Date prescr_date //displayname -> дата предписания [datatype -> date]
    Float pcnt //displayname -> количество предписаний [datatype -> float]
    Date protocol_date //displayname -> дата протокола [datatype -> date]
    Float dcnt //displayname -> количество протоколов [datatype -> float]
    Date instruction_date2 //displayname -> дата приказа ПП [datatype -> date]
    Float icnt2 //displayname -> количество приказов ПП [datatype -> float]
    Date inspection_date2 //displayname -> дата акта ПП [datatype -> date]
    Float acnt2 //displayname -> количество актов ПП [datatype -> float]
    Date prescr_date2 //displayname -> дата предписания ПП [datatype -> date]
    Float pcnt2 //displayname -> количество предписаний ПП [datatype -> float]
    Date protocol_date2 //displayname -> дата протокола ПП [datatype -> date]
    Float dcnt2 //displayname -> количество протоколов ПП [datatype -> float]
}

// reading data from url
//String url = System.getenv("urlReport")
//def json = jsonSlurper.parseText(new URL(url).text)

//write to [test]-output.json
//new File("output.json").write(JsonOutput.prettyPrint(new URL(url).text))

//reading data from [test]-output.json
def json = jsonSlurper.parse(new File("output.json"))

if (json["error"] == 0) {
    ArrayList data = json["fields"].collect()
    ArrayList<ReportGZHI> result = parseToObject(data)
    println(result[1].toString())
//    logger.error(result.size.toString())
}

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

private ArrayList<ReportGZHI> prepareResultReport(Collection data) {
    ArrayList<ReportGZHI> prepareList = new ArrayList()
    def prepareDataCountItems = data[0]["data"] as ArrayList
    for (int i = 0; i < prepareDataCountItems.size(); i++) {
        prepareList.add(new ReportGZHI())
    }
    return prepareList
}