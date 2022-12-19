package sadko.catalog

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import groovy.json.JsonSlurper
import groovy.transform.Field

import javax.net.ssl.*
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.logging.Logger

@Field final Logger logger = Logger.getLogger("") //todo off in web

def ver = 0.1


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
    СonsiderationResultGzi getСonsiderationResultGzi() {
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
    String CreatedTime              // Дата поручения todo New field -> CreatedTimeSadko  //todo важное поле
    UserOut Author                  // Автор -> who     //todo важное поле
    UserOut Executor                // Исполнитель -> that      //todo важное поле
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
    String Closed           //todo важное поле
    String Sended           //todo важное поле
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

def getCatalogItem(String catalogName, String name){
    if (name == null) return 0
    def itemName = utils.find('SadkoCatalog$' + catalogName, [itemName:name])[0]
    if (itemName != null) return itemName.itemId
    def itemMap = utils.find('SadkoCatalog$' + catalogName, [itemMap:name])[0]
    return itemMap != null ? itemMap.itemId : 0
}

String getStringFromDate(Date date){
    if (date == null) return null
    return date.format("yyyy-MM-dd'T'HH:mm:ss")
}

def addFiles (ArrayList<FileDataOut> files, obj){
    if (obj != null){
        byte[] data = utils.readFileContent(obj)
        def encode = Base64.encoder.encode(data)
        def content = new String(encode)
        FileDataOut file = new FileDataOut()
        file.Guid = UUID.randomUUID().toString()
        file.Name = obj.title
        file.Data = content
        files.add(file)
    }
}

private void prepareСonsiderationResult(СonsiderationResultGzi considerationResults) {
    //considerationResults.Code = '0005.0005.0056.1147'
    considerationResults.ResultId = 0
    considerationResults.InspectionTypeId = 0
    considerationResults.AddControlMeasureId = 0
    considerationResults.ResponseDate = getStringFromDate(subject.dateanswer)
    considerationResults.headSign = true
}

private void prepareLetterDetail(LetterDetail letterDetail) {
    letterDetail.Closed = getStringFromDate(subject.dateanswer)     //todo важное поле
    letterDetail.Sended = getStringFromDate(subject.dateanswer)     //todo важное поле
    letterDetail.SendTypeId = getCatalogItem('CitizenAnSeTy', subject.ansType != null ? subject.ansType.title : null)
    letterDetail.EmployeeSended = subject.maddening != null ? subject.maddening.title : null
    letterDetail.TextAnswer = subject.Results
    letterDetail.Status = 'Направлен ответ за подписью руководителя высшего исполнительного органа государственной власти субъекта РФ и его заместителей'
    letterDetail.StatusId = 0
}

UserOut getDefaultUser(empl){
    UserOut user = new UserOut()
    user.Guid = 'AB172B74-45DC-486F-B44B-0CFEFE4EA251'              //todo id взят из примера
    user.LastName = empl.lastName
    user.FirstName = empl.firstName
    user.MiddleName = empl.middleName
    user.FIO = empl.title
    return user
}

private void prepareResolution(ResolutionOut resolution) {
    boolean isFind = true
    def guid
    def sub
    while (isFind){
        guid = UUID.randomUUID().toString()
        def closure = {
            utils.find('SadkoObj$SadkoResolut', [GUID:guid])[0]
        }
        sub = api.tx.call(closure)

        if (sub == null){
            isFind = false
        }else{
            sleep(1000)
        }
    }
    Map<Object, Object> updateData = new HashMap<>()
    updateData.put('GUID', guid)
    updateData.put('appeal', subject)
    updateData.put('title', subject.title)
    utils.create('SadkoObj$SadkoResolut', updateData);

//    resolution.Guid = subject.UUID + '-' + UUID.randomUUID().toString().split('-')[0]                  //todo random поле
    resolution.Guid = guid
    resolution.CreatedTime = getStringFromDate(new Date())          //todo важное поле

    def empl = utils.find('employee', [UUID:'employee$73903'])[0]
    def sadkoAppeal = utils.find('SadkoObj$SadkoAppeal', [Appeal:subject])[0]

    if (sadkoAppeal.ResAuthorGuid == null){
        UserOut user = getDefaultUser(empl)
        resolution.Author = user                                        //todo важное поле
        resolution.executor = user                                      //todo важное поле
    }else {
        if (sadkoAppeal.ResExecGuid != null){
            UserOut author = new UserOut()
            author.Guid = sadkoAppeal.ResExecGuid
            author.MiddleName = sadkoAppeal.ResExecMidN
            author.LastName = sadkoAppeal.ResExecLastN
            author.FirstName = sadkoAppeal.ResExecFirsN
            author.FIO = sadkoAppeal.ResExecFIO
            resolution.Author = author
        }else {
            UserOut author = getDefaultUser(empl)
            resolution.Author = author
        }
        UserOut executor = new UserOut()
        executor.Guid = sadkoAppeal.ResAuthorGuid
        executor.MiddleName = sadkoAppeal.ResAuthorMidN
        executor.LastName = sadkoAppeal.ResAuthorLastN
        executor.FirstName = sadkoAppeal.ResAuthorFirsN
        executor.FIO = sadkoAppeal.ResAuthorFIO
        resolution.Executor = executor
    }

//    resolution.DecisionId = 3
//    resolution.ResolutionText = 'Направляется ответ на ваш запрос'
    resolution.ResolutionText = 'Ответ на Ваше поручение'
    //resolution.ControlDate = '2022-11-28T00:00:00'

//    ThemeOut theme = new ThemeOut()
//    theme.Code = '0005.0005.0056.1147'
//    theme.Name = 'Коммунально-бытовое хозяйство и предоставление услуг в условиях рынка'
//    resolution.themes.add(theme)

    def docpack = subject.docpack[0]
    def file1Sadko = docpack.file1Sadko[0]
    def file2Sadko = docpack.file2Sadko[0]

    addFiles(resolution.files, file1Sadko)
    addFiles(resolution.files, file2Sadko)

    if (resolution.files.size() > 0){
        resolution.ResolutionText = 'Направляется ответ на ваш запрос'
    }

}

private void prepareCard(Card card) { //todo важных полей нет
    card.Guid = subject.GuidSadko
    card.CitizenName = subject.LastName
    card.CitizenSurname = subject.FirstName
    card.CitizenPatronymic = subject.MiddleName
    card.CitizenAddress = subject.address
    card.CitizenAddressPost = subject.indexAddr
    card.CitizenAddressAreaId = getCatalogItem('CitizenAddArea', subject.regionAp != null ? subject.regionAp.title : null)
    card.CitizenPhone = subject.phoneNumber
    card.CitizenEmail = subject.email
    card.CitizenSocialStatusId = 0
    card.CitizenBenefitId = 0
    card.CitizenAnswerSendTypeId = getCatalogItem('CitizenAnSeTy', subject.ansType != null ? subject.ansType.title : null)
    card.LetterTypeId = getCatalogItem('LetterTypes', subject.typeAp != null ? subject.typeAp.title : null)
    card.DocumentTypeId = getCatalogItem('DocumentTypes', subject.viewAp != null ? subject.viewAp.title : null)
    card.CorrespondentId = getCatalogItem('Correspondents', subject.reporter != null ? subject.reporter.title : null)
    card.LetterNumber = subject.MessageNumber
    card.ReceiveDate = subject.ReceiveDateSD
    card.DeliveryTypeId = getCatalogItem('DeliveryTypes', subject.deliveryType != null ? subject.deliveryType.title : null)
    card.ConsiderationFormId = 0
    card.ReceivedFrom = subject.fromAp
    card.RegistrationNumber = subject.RegNumSD
    card.RegistrationDate = getStringFromDate(subject.registerDate)
    card.Message = subject.descrip
    def response = subject.response[0]
    def intresponse = subject.intresponse[0]

    addFiles(card.files, response)
    addFiles(card.files, intresponse)
}

def initScript(){
    prepareSSLConnection()
    def connection = (HttpsURLConnection) new URL(connectUrl).openConnection()
    prepareRequestPOST(connection, urlConnectParam, true)

    if (connection.responseCode == 200) {
        ConnectSADKOo connect = jsonSlurper.parseText(connection.inputStream.text) as ConnectSADKOo
        if (connect.access_token != null) {
            def authorization = connect.token_type + " " + connect.access_token

            Card card = new Card();
            prepareCard(card)

            ResolutionOut resolution = new ResolutionOut()
            prepareResolution(resolution)

            LetterDetail letterDetail = new LetterDetail()
            prepareLetterDetail(letterDetail)

            СonsiderationResultGzi considerationResults = new СonsiderationResultGzi()
            prepareСonsiderationResult(considerationResults)

            OutBoxCard outBoxCard = new OutBoxCard(card, resolution, letterDetail, considerationResults)
            ObjectWriter mapper = new ObjectMapper().writer();;
            String json = mapper.writeValueAsString(outBoxCard);
            String printJson = mapper.withDefaultPrettyPrinter().writeValueAsString(outBoxCard);

            def con = prepareConnectWithToken(baseUrl + 'OutboxResol', authorization)
            prepareRequestPOST(con, json)
            if (con.responseCode == 200){
                def text = con.inputStream.text
                logger.info("${LOG_PREFIX} Ответ в сторону Садко направлен, ответ от сервера: ${text}, сформированный запрос:\n${printJson}")
                def sadkoView = utils.find('SadkoObj$SadkoAppeal', [Appeal:subject])[0]
                Map<Object, Object> updateData = new HashMap<>()
                updateData.put('Status', utils.find('SadkoStatus', [code:'code3']))
                utils.edit(sadkoView.UUID, updateData)

            }else{
                logger.error("${LOG_PREFIX} Ошибка в процедуре направления ответа  в сторону Садко, код ошибки: ${con.responseCode}, ошибка: ${connection?.errorStream?.text}")
            }
        }else {
            logger.error("${LOG_PREFIX} Токен отсутствует, дальнейшая загрузка прерывается")
        }
    }else {
        logger.error("${LOG_PREFIX} Ошибка в запросе при получении токена, код ошибки: ${connection.responseCode}, ошибка: ${connection?.errorStream?.text}")
    }
}

/**
 *  Проверку на Тип обращения
 */
if (subject.state == 'Responsesent' && subject.fromAp.title == 'ГИС САДКО.ОГ'){
    initScript()
}