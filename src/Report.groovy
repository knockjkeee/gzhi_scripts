import groovy.json.JsonSlurper

import java.util.stream.Collectors

def jsonSlurper = new JsonSlurper()

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
    def collect = result.stream().filter(e -> e.inspection_date != null && e.prescr_date != null).collect(Collectors.toList())
    println("Result")
}

private static ArrayList<ReportGZHI> parseToObject(Collection data) {
    ArrayList<ReportGZHI> resultReport = prepareResultReport(data)
    for (int i = 0; i < data.size(); i++) {
        def nameField = data[i]["name"]
        ArrayList dataObject = data[i]["data"] as ArrayList
        updateProperties(resultReport, nameField, dataObject)
    }
    return resultReport
}

private static void updateProperties(ArrayList data, nameField, ArrayList dataObject) {
    for (int j = 0; j < data.size(); j++) {
        var reportGZHI = data[j] as ReportGZHI
        var properties = reportGZHI.properties as LinkedHashMap<String, Object>
        for (Map.Entry<String, Object> objectEntry : properties.entrySet()) {
            if (objectEntry.key.equals(nameField)) {
                reportGZHI.setProperty(objectEntry.key, dataObject[j]);
            }
        }
    }
}

private static ArrayList<ReportGZHI> prepareResultReport(Collection data) {
    ArrayList<ReportGZHI> prepareList = new ArrayList()
    def prepareDataCountItems = data[0]["data"] as ArrayList
    for (int i = 0; i < prepareDataCountItems.size(); i++) {
        prepareList.add(new ReportGZHI())
    }
    return prepareList
}

class ReportGZHI {
    var accost_no //displayname -> Номер обращения [datatype -> string]
    var instruction_date //displayname -> дата приказа [datatype -> date]
    var icnt //displayname -> количество приказов [datatype -> float]
    var creator_fio //displayname -> исполнитель из приказа [datatype -> string]
    var instruction_num //displayname -> Номера приказов [datatype -> string]
    var act_num //displayname -> Номера актов [datatype -> string]
    var inspection_date //displayname -> дата акта [datatype -> date]
    var acnt //displayname -> количество актов проверки [datatype -> float]
    var prescr_date //displayname -> дата предписания [datatype -> date]
    var pcnt //displayname -> количество предписаний [datatype -> float]
    var protocol_date //displayname -> дата протокола [datatype -> date]
    var dcnt //displayname -> количество протоколов [datatype -> float]
    var instruction_date2 //displayname -> дата приказа ПП [datatype -> date]
    var icnt2 //displayname -> количество приказов ПП [datatype -> float]
    var inspection_date2 //displayname -> дата акта ПП [datatype -> date]
    var acnt2 //displayname -> количество актов ПП [datatype -> float]
    var prescr_date2 //displayname -> дата предписания ПП [datatype -> date]
    var pcnt2 //displayname -> количество предписаний ПП [datatype -> float]
    var protocol_date2 //displayname -> дата протокола ПП [datatype -> date]
    var dcnt2 //displayname -> количество протоколов ПП [datatype -> float]
}