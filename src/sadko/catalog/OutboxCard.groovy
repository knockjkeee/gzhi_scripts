package sadko.catalog

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.Field
import jdk.nashorn.internal.ir.ObjectNode

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManagerFactory
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web



class TrustHostnameVerifiero implements HostnameVerifier {
    @Override
    boolean verify(String hostname, SSLSession session) {
        return hostname == session.peerHost
    }
}

class ConnectSADKOo {
    String access_token
    int expires_in
    String token_type
    String scope
}

@JsonPropertyOrder(['Card', 'Resolution', 'LetterDetail', 'ConsiderationResults'])
class OutBoxCard {
    Card Card
    ResolutionOut Resolution
    LetterDetail LetterDetail
    ConsiderationResults ConsiderationResults

    @JsonProperty("Card")
    Card getCard() {
        return Card
    }
    @JsonProperty("Resolution")
    ResolutionOut getResolution() {
        return Resolution
    }
    @JsonProperty("LetterDetail")
    LetterDetail getLetterDetail() {
        return LetterDetail
    }
    @JsonProperty("ConsiderationResults")
    ConsiderationResults getConsiderationResults() {
        return ConsiderationResults
    }

    OutBoxCard(Card card, ResolutionOut resolution, LetterDetail letterDetail, ConsiderationResults considerationResults) {
        Card = card
        Resolution = resolution
        LetterDetail = letterDetail
        ConsiderationResults = considerationResults
    }
}

@JsonPropertyOrder(['Guid', 'CitizenName', 'CitizenSurname', 'CitizenPatronymic', 'CitizenAddress', 'CitizenAddressPost', 'CitizenAddressAreaId', 'CitizenPhone', 'CitizenEmail', 'CitizenSocialStatusId', 'CitizenBenefitId', 'CitizenAnswerSendTypeId', 'LetterTypeId', 'DocumentTypeId', 'CorrespondentId', 'LetterNumber', 'ControlOrgSendDate', 'ReceiveDate', 'DeliveryTypeId', 'ConsiderationFormId', 'ReceivedFrom', 'RegistrationNumber', 'RegistrationDate', 'PreviousCardsCount', 'DocSheetNumber', 'DocCopyNumber', 'ConcernedCitizensNumber', 'Message', 'Files'])
class Card {                        // appeal
    String Guid                     //[OK] Уникальный идентификатор резолюции todo check New field -> GuidSadko
    // Заявитель
    String CitizenName              //[OK] Имя заявителя -> LastName
    String CitizenSurname           //[OK] Фамилия заявителя -> FirstName
    String CitizenPatronymic        //[OK] Отчество заявителя -> MiddleName
    String CitizenAddress           //[OK] Почтовый адрес заявителя -> oldaddr todo check собрать из дома [house2]+[street2]+room
    String CitizenAddressPost       //[OK] Индекс почтового адреса заявителя -> indexAddr
    String CitizenAddressAreaId     //[OK] ID района по почтовому адресу заявителя -> справочник CitizenAddArea [regionAp]
    String CitizenPhone             //[OK] Телефон заявителя -> phoneNumber
    String CitizenEmail             //[OK] E-Mail заявителя -> email
    String CitizenSocialStatusId    //[Х] ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    String CitizenBenefitId         //[Х] ID льготный состав гражданина -> справочник CitizenBenefit todo не используют
    String CitizenAnswerSendTypeId  //[OK] Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    String LetterTypeId             //[OK] ID типа обращения -> справочник LetterTypes [typeAp]
    String DocumentTypeId           //[OK] ID вида обращения -> справочник DocumentTypes [viewAp]
    String CorrespondentId          //[OK] ID корреспондента -> справочник Correspondents [reporter]
    String LetterNumber             //[OK] Номер сопроводительного письма -> MessageNumber
    String ControlOrgSendDate       //[OK] Дата отправки из организации -> MessageDate
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSadko ??? registerDate
    String DeliveryTypeId           //[OK] Тип доставки -> справочник DeliveryTypes [deliveryType]
    String ConsiderationFormId      //[Х] Форма рассмотрения -> справочник ConsiderationF todo не используют
    String ReceivedFrom             //[OK] Поступило из -> todo fromAp справочник Место поступления или receivedfrom (строка) !!!!!!!!!!!!
    String RegistrationNumber       // Регистрационный номер -> todo New field -> RegistrationNumberSadko
    String RegistrationDate         //[OK] Дата регистрации -> registerDate
    String PreviousCardsCount       // Количество предыдущих обращений todo New field -> PreviousCardsCountSadko
    String DocSheetNumber           // Количество листов документа  todo New field -> DocSheetNumberSadko
    String DocCopyNumber            // Количество листов приложения todo New field -> DocCopyNumberSadko
    String ConcernedCitizensNumber  // Количество заинтересованных todo New field -> ConcernedCitizensNumberSadko
    String Message                  //[OK] Текст обращения -> descrip
    ArrayList<FileDataOut> Files = new ArrayList<>()             // Файлы -> Павет документов [docpack]

    @JsonProperty("Guid")
    String getGuid() {
        return Guid
    }
    @JsonProperty("CitizenName")
    String getCitizenName() {
        return CitizenName
    }
    @JsonProperty("CitizenSurname")
    String getCitizenSurname() {
        return CitizenSurname
    }
    @JsonProperty("CitizenPatronymic")
    String getCitizenPatronymic() {
        return CitizenPatronymic
    }
    @JsonProperty("CitizenAddress")
    String getCitizenAddress() {
        return CitizenAddress
    }
    @JsonProperty("CitizenAddressPost")
    String getCitizenAddressPost() {
        return CitizenAddressPost
    }
    @JsonProperty("CitizenAddressAreaId")
    String getCitizenAddressAreaId() {
        return CitizenAddressAreaId
    }
    @JsonProperty("CitizenPhone")
    String getCitizenPhone() {
        return CitizenPhone
    }
    @JsonProperty("CitizenEmail")
    String getCitizenEmail() {
        return CitizenEmail
    }
    @JsonProperty("CitizenSocialStatusId")
    String getCitizenSocialStatusId() {
        return CitizenSocialStatusId
    }
    @JsonProperty("CitizenBenefitId")
    String getCitizenBenefitId() {
        return CitizenBenefitId
    }
    @JsonProperty("CitizenAnswerSendTypeId")
    String getCitizenAnswerSendTypeId() {
        return CitizenAnswerSendTypeId
    }
    @JsonProperty("LetterTypeId")
    String getLetterTypeId() {
        return LetterTypeId
    }
    @JsonProperty("DocumentTypeId")
    String getDocumentTypeId() {
        return DocumentTypeId
    }
    @JsonProperty("CorrespondentId")
    String getCorrespondentId() {
        return CorrespondentId
    }
    @JsonProperty("LetterNumber")
    String getLetterNumber() {
        return LetterNumber
    }
    @JsonProperty("ControlOrgSendDate")
    String getControlOrgSendDate() {
        return ControlOrgSendDate
    }
    @JsonProperty("ReceiveDate")
    String getReceiveDate() {
        return ReceiveDate
    }
    @JsonProperty("DeliveryTypeId")
    String getDeliveryTypeId() {
        return DeliveryTypeId
    }
    @JsonProperty("ConsiderationFormId")
    String getConsiderationFormId() {
        return ConsiderationFormId
    }
    @JsonProperty("ReceivedFrom")
    String getReceivedFrom() {
        return ReceivedFrom
    }
    @JsonProperty("RegistrationNumber")
    String getRegistrationNumber() {
        return RegistrationNumber
    }
    @JsonProperty("RegistrationDate")
    String getRegistrationDate() {
        return RegistrationDate
    }
    @JsonProperty("PreviousCardsCount")
    String getPreviousCardsCount() {
        return PreviousCardsCount
    }
    @JsonProperty("DocSheetNumber")
    String getDocSheetNumber() {
        return DocSheetNumber
    }
    @JsonProperty("DocCopyNumber")
    String getDocCopyNumber() {
        return DocCopyNumber
    }
    @JsonProperty("ConcernedCitizensNumber")
    String getConcernedCitizensNumber() {
        return ConcernedCitizensNumber
    }
    @JsonProperty("Message")
    String getMessage() {
        return Message
    }
    @JsonProperty("Files")
    ArrayList<FileDataOut> getFiles() {
        return Files
    }
}

@JsonPropertyOrder(['Guid', 'CreatedTime', 'Author', 'Executor', 'DecisionId', 'ResolutionText', 'ControlDate', 'Themes', 'Files'])
class ResolutionOut {               // appeal -> resolution
    String Guid                     // Уникальный идентификатор резолюции todo New field -> GuidSadko
    String CreatedTime              // Дата поручения todo New field -> CreatedTimeSadko
    UserOut Author                  // Автор -> who
    UserOut Executor                // Исполнитель -> that
    String DecisionId               // ID Решения по резолюции -> справочник Decisions todo не используют
    String ResolutionText           // Текст резолюции -> answer
    String ControlDate              // Дата контроля todo New field -> ControlDateSadko
    ArrayList<ThemeOut> Themes = new ArrayList<>()            // Список тем вопросов todo ??????
    ArrayList<FileDataOut> Files = new ArrayList<>()    // Список файлов       todo ??????
    @JsonProperty("Guid")
    String getGuid() {
        return Guid
    }
    @JsonProperty("CreatedTime")
    String getCreatedTime() {
        return CreatedTime
    }
    @JsonProperty("Author")
    UserOut getAuthor() {
        return Author
    }
    @JsonProperty("Executor")
    UserOut getExecutor() {
        return Executor
    }
    @JsonProperty("DecisionId")
    String getDecisionId() {
        return DecisionId
    }
    @JsonProperty("ResolutionText")
    String getResolutionText() {
        return ResolutionText
    }
    @JsonProperty("ControlDate")
    String getControlDate() {
        return ControlDate
    }
    @JsonProperty("Themes")
    ArrayList<ThemeOut> getThemes() {
        return Themes
    }
    @JsonProperty("Files")
    ArrayList<FileDataOut> getFiles() {
        return Files
    }
}

@JsonPropertyOrder(['Name', 'Guid', 'Data'])
class FileDataOut {
    String Name                     // Имя файла
    String Guid                     // Уникальный идентификатор файла
    String Data                     // Закодированное в строку base64 содержимое файла
    @JsonProperty("Name")
    String getName() {
        return Name
    }
    @JsonProperty("Guid")
    String getGuid() {
        return Guid
    }
    @JsonProperty("Data")
    String getData() {
        return Data
    }
}

@JsonPropertyOrder(['Code', 'Name', 'Annotation'])
class ThemeOut {
    String Code                     // Код по тематическому классификатору обращений
    String Name                     // Название вопроса
    String Annotation               // Анотация к вопросу
    @JsonProperty("Code")
    String getCode() {
        return Code
    }
    @JsonProperty("Name")
    String getName() {
        return Name
    }
    @JsonProperty("Annotation")
    String getAnnotation() {
        return Annotation
    }
}

@JsonPropertyOrder(['Closed', 'Sended', 'SendTypeId', 'EmployeeSended', 'TextAnswer', 'Status', 'StatusId'])
class LetterDetail{
    String Closed
    String Sended
    String SendTypeId
    String EmployeeSended
    String TextAnswer
    String Status
    String StatusId
    @JsonProperty("Closed")
    String getClosed() {
        return Closed
    }
    @JsonProperty("Sended")
    String getSended() {
        return Sended
    }
    @JsonProperty("SendTypeId")
    String getSendTypeId() {
        return SendTypeId
    }
    @JsonProperty("EmployeeSended")
    String getEmployeeSended() {
        return EmployeeSended
    }
    @JsonProperty("TextAnswer")
    String getTextAnswer() {
        return TextAnswer
    }
    @JsonProperty("Status")
    String getStatus() {
        return Status
    }
    @JsonProperty("StatusId")
    String getStatusId() {
        return StatusId
    }
}

@JsonPropertyOrder(['Code', 'ResultId', 'InspectionTypeId', 'AddControlMeasureId', 'TakenMeasuresId', 'ResponseDate', 'HeadSign'])
class ConsiderationResults {
    String Code
    String ResultId
    String InspectionTypeId
    String AddControlMeasureId
    String TakenMeasuresId
    String ResponseDate
    String HeadSign

    @JsonProperty("Code")
    String getCode() {
        return Code
    }
    @JsonProperty("ResultId")
    String getResultId() {
        return ResultId
    }
    @JsonProperty("InspectionTypeId")
    String getInspectionTypeId() {
        return InspectionTypeId
    }
    @JsonProperty("AddControlMeasureId")
    String getAddControlMeasureId() {
        return AddControlMeasureId
    }
    @JsonProperty("TakenMeasuresId")
    String getTakenMeasuresId() {
        return TakenMeasuresId
    }
    @JsonProperty("ResponseDate")
    String getResponseDate() {
        return ResponseDate
    }
    @JsonProperty("HeadSign")
    String getHeadSign() {
        return HeadSign
    }
}

@JsonPropertyOrder(['Guid', 'LastName', 'FirstName', 'MiddleName', 'FIO'])
class UserOut {
    String Guid
    String LastName
    String FirstName
    String MiddleName
    String FIO

    @JsonProperty("Guid")
    String getGuid() {
        return Guid
    }
    @JsonProperty("LastName")
    String getLastName() {
        return LastName
    }
    @JsonProperty("FirstName")
    String getFirstName() {
        return FirstName
    }
    @JsonProperty("MiddleName")
    String getMiddleName() {
        return MiddleName
    }
    @JsonProperty("FIO")
    String getFIO() {
        return FIO
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
    HttpsURLConnection.setDefaultHostnameVerifier(new TrustHostnameVerifiero())
}

HttpsURLConnection prepareConnectWithToken(String url, String token) {
    def response = (HttpsURLConnection) new URL(url).openConnection()
    response.setRequestProperty("Authorization", token);
    return response
}

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

prepareSSLConnection()
def connection = (HttpsURLConnection) new URL(connectUrl).openConnection()
prepareRequestPOST(connection, urlConnectParam, true)


if (connection.responseCode == 200) {
    ConnectSADKOo connect = jsonSlurper.parseText(connection.inputStream.text) as ConnectSADKOo
    if (connect.access_token != null) {
        def authorization = connect.token_type + " " + connect.access_token

        Card card = new Card();
        card.Guid = '8DAB527A-A951-4996-995F-C7CD3AFF84C8'
        card.CitizenAddress = 'Asdasdsad'
        card.CitizenPhone = '1232132'
        ResolutionOut resolution = new ResolutionOut()
        resolution.ResolutionText = 'Aasdasd'
        LetterDetail letterDetail = new LetterDetail()
        ConsiderationResults considerationResults = new ConsiderationResults()
        OutBoxCard outBoxCard = new OutBoxCard(card, resolution, letterDetail, considerationResults)

        ObjectWriter mapper = new ObjectMapper().writer();;
        String json = mapper.writeValueAsString(outBoxCard);
       // String json = mapper.withDefaultPrettyPrinter().writeValueAsString(outBoxCard);

        def print = JsonOutput.prettyPrint(json)


//        ObjectMapper om = new ObjectMapper();
//
//        ObjectNode root = om.createObjectNode() as ObjectNode;
//        root.set("A", card);
//        root.set("B", resolution);
//
//        String json2 = om.writeValueAsString(root);
//
//        def print2 = JsonOutput.prettyPrint(json2)

        logger.error("TOKEN -> " + authorization)
        logger.error("JSON -> " + print)
//        def values = Catalog.values()
//        for (def item in values) {
//            loadCatalog(item, authorization)
//        }
//        def con = prepareConnectWithToken(baseUrl + 'OutboxResol', authorization)
//        prepareRequestPOST(con, json)
//        println('UpLoad is completed')
    }else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
}else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}")
}