import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

class ReportGZHI {
    String accost_no //displayname -> Номер обращения [datatype -> string]
    String instruction_date //displayname -> дата приказа [datatype -> date]
    Float icnt //displayname -> количество приказов [datatype -> float]
    String creator_fio //displayname -> исполнитель из приказа [datatype -> string]
    String instruction_num //displayname -> Номера приказов [datatype -> string]
    String act_num //displayname -> Номера актов [datatype -> string]
    String inspection_date //displayname -> дата акта [datatype -> date]
    Float acnt //displayname -> количество актов проверки [datatype -> float]
    String prescr_date //displayname -> дата предписания [datatype -> date]
    Float pcnt //displayname -> количество предписаний [datatype -> float]
    String protocol_date //displayname -> дата протокола [datatype -> date]
    Float dcnt //displayname -> количество протоколов [datatype -> float]
    String instruction_date2 //displayname -> дата приказа ПП [datatype -> date]
    Float icnt2 //displayname -> количество приказов ПП [datatype -> float]
    String inspection_date2 //displayname -> дата акта ПП [datatype -> date]
    Float acnt2 //displayname -> количество актов ПП [datatype -> float]
    String prescr_date2 //displayname -> дата предписания ПП [datatype -> date]
    Float pcnt2 //displayname -> количество предписаний ПП [datatype -> float]
    String protocol_date2 //displayname -> дата протокола ПП [datatype -> date]
    Float dcnt2 //displayname -> количество протоколов ПП [datatype -> float]
}

// reading data from url
String url = System.getenv("urlReport")
def json = jsonSlurper.parseText(new URL(url).text)

//write to [test]-output.json
//new File("output.json").write(JsonOutput.prettyPrint(new URL(url).text))

//reading data from [test]-output.json
//def json = jsonSlurper.parse(new File("output.json"))

if (json["error"] == 0) {
    ArrayList data = json["fields"].collect()
    ArrayList<ReportGZHI> result = parseToObject(data)
    println(result[1].toString())
//    logger.error(result.size.toString())
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
        ReportGZHI reportGZHI = data[j]
        LinkedHashMap<String, Object> properties = reportGZHI.properties
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