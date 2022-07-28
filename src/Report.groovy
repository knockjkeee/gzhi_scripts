import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
String url = System.getenv("urlReport")
def json = jsonSlurper.parseText(new URL(url).text)
def list = new ArrayList()
def report = new ReportGZHI();

if (json["error"] == 0) {
    list = json["fields"].collect()

}

list.size().println()

println("Result")

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