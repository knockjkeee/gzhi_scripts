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

@JsonPropertyOrder(['Card', 'Resolution', 'LetterDetail', 'СonsiderationResultGzi'])
class OutBoxCard {
    Card Card
    ResolutionOut Resolution
    LetterDetail LetterDetail
    СonsiderationResultGzi СonsiderationResultGzi

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
    @JsonProperty("СonsiderationResultGzi")
    СonsiderationResultGzi getConsiderationResults() {
        return СonsiderationResultGzi
    }

    OutBoxCard(Card card, ResolutionOut resolution, LetterDetail letterDetail, СonsiderationResultGzi сonsiderationResultGzi) {
        Card = card
        Resolution = resolution
        LetterDetail = letterDetail
        СonsiderationResultGzi = сonsiderationResultGzi
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
    int CitizenAddressAreaId     //[OK] ID района по почтовому адресу заявителя -> справочник CitizenAddArea [regionAp]
    String CitizenPhone             //[OK] Телефон заявителя -> phoneNumber
    String CitizenEmail             //[OK] E-Mail заявителя -> email
    int CitizenSocialStatusId    //[Х] ID социальный статус гражданина -> справочник CitizenSocStat todo не используют
    int CitizenBenefitId         //[Х] ID льготный состав гражданина -> справочник CitizenBenefit todo не используют
    int CitizenAnswerSendTypeId  //[OK] Желаемый способ ответа гражданину -> спарвочник CitizenAnSeTy [ansType]
    int LetterTypeId             //[OK] ID типа обращения -> справочник LetterTypes [typeAp]
    int DocumentTypeId           //[OK] ID вида обращения -> справочник DocumentTypes [viewAp]
    int CorrespondentId          //[OK] ID корреспондента -> справочник Correspondents [reporter]
    String LetterNumber             //[OK] Номер сопроводительного письма -> MessageNumber
    String ControlOrgSendDate       //[OK] Дата отправки из организации -> MessageDate
    String ReceiveDate              // Дата поступления todo New field -> ReceiveDateSadko ??? registerDate
    int DeliveryTypeId           //[OK] Тип доставки -> справочник DeliveryTypes [deliveryType]
    int ConsiderationFormId      //[Х] Форма рассмотрения -> справочник ConsiderationF todo не используют
    String ReceivedFrom             //[OK] Поступило из -> todo fromAp справочник Место поступления или receivedfrom (строка) !!!!!!!!!!!!
    String RegistrationNumber       // Регистрационный номер -> todo New field -> RegistrationNumberSadko
    String RegistrationDate         //[OK] Дата регистрации -> registerDate
    int PreviousCardsCount       // Количество предыдущих обращений todo New field -> PreviousCardsCountSadko
    int DocSheetNumber           // Количество листов документа  todo New field -> DocSheetNumberSadko
    int DocCopyNumber            // Количество листов приложения todo New field -> DocCopyNumberSadko
    int ConcernedCitizensNumber  // Количество заинтересованных todo New field -> ConcernedCitizensNumberSadko
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
    int getCitizenAddressAreaId() {
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
    int getCitizenSocialStatusId() {
        return CitizenSocialStatusId
    }
    @JsonProperty("CitizenBenefitId")
    int getCitizenBenefitId() {
        return CitizenBenefitId
    }
    @JsonProperty("CitizenAnswerSendTypeId")
    int getCitizenAnswerSendTypeId() {
        return CitizenAnswerSendTypeId
    }
    @JsonProperty("LetterTypeId")
    int getLetterTypeId() {
        return LetterTypeId
    }
    @JsonProperty("DocumentTypeId")
    int getDocumentTypeId() {
        return DocumentTypeId
    }
    @JsonProperty("CorrespondentId")
    int getCorrespondentId() {
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
    int getDeliveryTypeId() {
        return DeliveryTypeId
    }
    @JsonProperty("ConsiderationFormId")
    int getConsiderationFormId() {
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
    int getPreviousCardsCount() {
        return PreviousCardsCount
    }
    @JsonProperty("DocSheetNumber")
    int getDocSheetNumber() {
        return DocSheetNumber
    }
    @JsonProperty("DocCopyNumber")
    int getDocCopyNumber() {
        return DocCopyNumber
    }
    @JsonProperty("ConcernedCitizensNumber")
    int getConcernedCitizensNumber() {
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
    int DecisionId               // ID Решения по резолюции -> справочник Decisions todo не используют
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
    int getDecisionId() {
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
    int SendTypeId
    String EmployeeSended
    String TextAnswer
    String Status
    int StatusId
    @JsonProperty("Closed")
    String getClosed() {
        return Closed
    }
    @JsonProperty("Sended")
    String getSended() {
        return Sended
    }
    @JsonProperty("SendTypeId")
    int getSendTypeId() {
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
    int getStatusId() {
        return StatusId
    }
}

@JsonPropertyOrder(['Code', 'ResultId', 'InspectionTypeId', 'AddControlMeasureId', 'TakenMeasuresId', 'ResponseDate', 'HeadSign'])
class СonsiderationResultGzi {
    String Code
    String ResultId
    String InspectionTypeId
    String AddControlMeasureId
    String TakenMeasuresId
    String ResponseDate
    boolean HeadSign

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
    boolean getHeadSign() {
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
//        card.Guid = '8DAB527A-A951-4996-995F-C7CD3AFF84C8'
        card.CitizenName = 'Г.'
        card.CitizenSurname = 'Чернова'
        card.CitizenPatronymic = 'А.'
        card.CitizenAddress = 'обл.Калужская, р-н.Дзержинский, г.Кондрово, ул.Пушкина, д.728, кв.170'
        card.CitizenAddressPost = '249833'
        card.CitizenAddressAreaId = 12
        card.CitizenPhone = '+7 (900) 123-456-89'
        card.CitizenEmail = 'bronsquall@gemx.com'
        card.CitizenSocialStatusId = 0
        card.CitizenBenefitId = 0
        card.CitizenAnswerSendTypeId = 1
        card.LetterTypeId = 2
        card.DocumentTypeId = 1
        card.CorrespondentId = 4
        card.LetterNumber = 'Ч-гжи-40/4772/3-18'
        card.ReceiveDate = '2018-11-28T00:00:00'
        card.DeliveryTypeId = 2
        card.ConsiderationFormId = 0
        card.RegistrationNumber = 'Ч-гжи-40/4772/3-18'
        card.RegistrationDate = '2018-11-28T00:00:00'
        card.PreviousCardsCount = 3
        card.ConcernedCitizensNumber = 1
        card.Message = 'Низкая температура ГВС'

        FileDataOut file = new FileDataOut()
        file.Guid = 'D6CD574B-D06F-4486-B671-CEC2183860EC'
        file.Name = 'Обращение.pdf'
        file.Data = 'EAA33(base64)...=='
        card.files.add(file)

        ResolutionOut resolution = new ResolutionOut()
        resolution.Guid = '7C126804-4C9C-4063-8A5A-865C899F8A63'
        resolution.CreatedTime = '2022-11-29T12:45:40'

        UserOut user = new UserOut()
        user.Guid = 'AB172B74-45DC-486F-B44B-0CFEFE4EA251'
        user.LastName = 'Дулишкович'
        user.FirstName = 'А'
        user.MiddleName = 'D'
        user.FIO = 'Дулишкович А.В.'

        resolution.Author = user
        resolution.executor = user

        resolution.DecisionId = 3
        resolution.ResolutionText = 'Направляется ответ на ваш запрос'
        resolution.ControlDate = '2022-11-28T00:00:00'

        ThemeOut theme = new ThemeOut()
        theme.Code = '0005.0005.0056.1147'
        theme.Name = 'Коммунально-бытовое хозяйство и предоставление услуг в условиях рынка'
        resolution.themes.add(theme)

        FileDataOut file2 = new FileDataOut()
        file2.Guid = '61E1F898-3E84-4611-BD80-4089AEFBBBA0'
        file2.Name = 'Акт.pdf'
        file2.Data = 'EAA33(base64)...=='
        resolution.files.add(file2)

        LetterDetail letterDetail = new LetterDetail()

        letterDetail.Closed = '2022-11-29T00:00:00'
        letterDetail.Sended = '2022-11-29T10:30:00'
        letterDetail.SendTypeId = 1
        letterDetail.EmployeeSended = 'Семёнова Мария Петровна'
        letterDetail.TextAnswer = 'Рассмотрев ваше заявление...'
        letterDetail.Status = 'Дан ответ автору'
        letterDetail.StatusId = 8

        СonsiderationResultGzi considerationResults = new СonsiderationResultGzi()

        considerationResults.Code = '0005.0005.0056.1147'
        considerationResults.ResultId = 1
        considerationResults.InspectionTypeId = 1
        considerationResults.AddControlMeasureId = 1
        considerationResults.ResponseDate = '2022-11-29T00:00:00'
        considerationResults.headSign = false


        OutBoxCard outBoxCard = new OutBoxCard(card, resolution, letterDetail, considerationResults)
        ObjectWriter mapper = new ObjectMapper().writer();;
        String json = mapper.writeValueAsString(outBoxCard);

        String print = mapper.withDefaultPrettyPrinter().writeValueAsString(outBoxCard);

        def con = prepareConnectWithToken(baseUrl + 'OutboxResol', authorization)
        prepareRequestPOST(con, json)
        println('UpLoad is completed')
    }else {
        logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
    }
}else {
    logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}")
}