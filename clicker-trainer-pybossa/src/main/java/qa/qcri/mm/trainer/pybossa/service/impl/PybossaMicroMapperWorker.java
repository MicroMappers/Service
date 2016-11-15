package qa.qcri.mm.trainer.pybossa.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.trainer.pybossa.dao.CrisisDao;
import qa.qcri.mm.trainer.pybossa.dao.MarkerStyleDao;
import qa.qcri.mm.trainer.pybossa.dao.ReportTemplateTyphoonRubyDao;
import qa.qcri.mm.trainer.pybossa.dao.TaskQueueResponseDao;
import qa.qcri.mm.trainer.pybossa.dao.TyphoonRubyTextGeoClickerDao;
import qa.qcri.mm.trainer.pybossa.entity.*;
import qa.qcri.mm.trainer.pybossa.format.impl.*;
import qa.qcri.mm.trainer.pybossa.service.*;
import qa.qcri.mm.trainer.pybossa.store.LookupCode;
import qa.qcri.mm.trainer.pybossa.store.StatusCodeType;
import qa.qcri.mm.trainer.pybossa.store.URLPrefixCode;
import qa.qcri.mm.trainer.pybossa.store.UserAccount;
import qa.qcri.mm.trainer.pybossa.util.DataFormatValidator;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/18/13
 * Time: 1:19 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("pybossaMicroMapperWorker")
@Transactional(readOnly = false)
public class PybossaMicroMapperWorker implements MicroMapperWorker {

    protected static Logger logger = Logger.getLogger(PybossaMicroMapperWorker.class);

    private Client client;
    private final CVSRemoteFileFormatter cvsRemoteFileFormatter = new CVSRemoteFileFormatter();
    private int MAX_PENDING_QUEUE_SIZE = 50;
    private final int MAX_IMPORT_PROCESS_QUEUE_SIZE = 300;
    private String PYBOSSA_API_TASK_PUBLSIH_URL;

    private String PYBOSSA_API_TASK_RUN_BASE_URL;
    private String PYBOSSA_API_TASK_BASE_URL;

    private String AIDR_API_URL;
    private String AIDR_ASSIGNED_TASK_CLEAN_UP_URL;
    private String AIDR_TASK_ANSWER_URL;
    private String PYBOSSA_TASK_DELETE_URL;
    private String AIDR_NOMINAL_ATTRIBUTE_LABEL_URL;


    private final PybossaCommunicator pybossaCommunicator = new PybossaCommunicator();
    private final JSONParser parser = new JSONParser();
    private final MicroMapperPybossaFormatter pybossaFormatter = new MicroMapperPybossaFormatter();
    private final TextClickerPybossaFormatter textPybossaFormatter = new TextClickerPybossaFormatter();


    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientAppService clientAppService;

    @Autowired
    private TaskQueueService taskQueueService;

    @Autowired
    private ClientAppSourceService clientAppSourceService;

    @Autowired
    private ClientAppResponseService clientAppResponseService;

    @Autowired
    private ReportTemplateService reportTemplateService;

    @Autowired
    private ReportProductService reportProductService;

    @Autowired
    private ExternalCustomService externalCustomService;

    @Autowired
    private MarkerStyleDao markerStyleDao;
    
    @Autowired
    private TaskQueueResponseDao taskQueueResponseDao;
    
    @Autowired
    private TyphoonRubyTextGeoClickerDao typhoonRubyTextGeoClickerDao;
    
    @Autowired
    private ReportTemplateTyphoonRubyDao reportTemplateTyphoonRubyDao;

    @Autowired
    private CrisisDao crisisDao;
    
    @Autowired
    private PusherService pusherService;

    @Autowired
    private TranslationService translationService;

    public void setClassVariable() throws Exception{
        client = clientService.findClientByCriteria("name", UserAccount.MIROMAPPER_USER_NAME);
        if(client != null){
            PYBOSSA_API_TASK_PUBLSIH_URL = client.getHostURL() + URLPrefixCode.TASK_PUBLISH + client.getHostAPIKey();
            PYBOSSA_API_TASK_BASE_URL  = client.getHostURL() + URLPrefixCode.TASK_INFO;
            PYBOSSA_API_TASK_RUN_BASE_URL =  client.getHostURL() + URLPrefixCode.TASKRUN_INFO;
            MAX_PENDING_QUEUE_SIZE = client.getQueueSize();

        }

    }

    public void setClassVariablePerClient(Client theClient) throws Exception{
        boolean resetVariable = false;

        if(client != null){
            if(!client.getClientID().equals(theClient.getClientID())){
                client = theClient;
                resetVariable = true;
            }
        }
        else{
            client = theClient;
            resetVariable = true;
        }

        if(resetVariable){
            AIDR_API_URL =  client.getAidrHostURL() + URLPrefixCode.ASSINGN_TASK + LookupCode.SYSTEM_USER_NAME + "/";
            AIDR_ASSIGNED_TASK_CLEAN_UP_URL = client.getAidrHostURL()  + URLPrefixCode.AIDR_TASKASSIGNMENT_REVERT + LookupCode.SYSTEM_USER_NAME + "/";
            PYBOSSA_API_TASK_PUBLSIH_URL = client.getHostURL() + URLPrefixCode.TASK_PUBLISH + client.getHostAPIKey();
            AIDR_TASK_ANSWER_URL = client.getAidrHostURL() + URLPrefixCode.TASK_ANSWER_SAVE;
            PYBOSSA_API_TASK_BASE_URL  = client.getHostURL() + URLPrefixCode.TASK_INFO;
            PYBOSSA_API_TASK_RUN_BASE_URL =  client.getHostURL() + URLPrefixCode.TASKRUN_INFO;
            MAX_PENDING_QUEUE_SIZE = client.getQueueSize();

            PYBOSSA_TASK_DELETE_URL = client.getHostURL() + URLPrefixCode.PYBOSSA_TASK_DELETE;

            AIDR_NOMINAL_ATTRIBUTE_LABEL_URL = client.getAidrHostURL() + URLPrefixCode.AIDR_NOMINAL_ATTRIBUTE_LABEL;

        }

    }


    /** Import process  starts**/
    @Override
    public void processTaskImport() throws Exception{
        logger.info("Data import is starting");
        this.processTaskRunImportForText();
        this.processTaskRunImportForNonText();
        // 1. processs text import
        // 2. process non text import

    }


    public void processTaskRunImportForText() throws Exception{
        System.out.println("processTaskRunImportForText : Start : " + new Date());

        List<ClientApp> clientAppList = clientAppService.findClientAppByStatus(LookupCode.AIDR_ONLY);
        Iterator itr= clientAppList.iterator();

        textPybossaFormatter.setTranslationService(translationService);

        while(itr.hasNext()){
            ClientApp clientApp = (ClientApp)itr.next();
            this.setClassVariablePerClient(clientApp.getClient());
            this.processQueueImportForText(clientApp);

            if(clientApp.getTcProjectId() != null ){
                this.processTranslations(clientApp);
            }
        }
    }

    public void processTaskRunImportForNonText() throws Exception{
        setClassVariable();

        if(client == null){
            return;
        }

        List<ClientApp> appList = clientAppService.getAllClientAppByClientID(client.getClientID() );
        List<GeoJsonOutputModel> geoJsonOutputModels =  new ArrayList<GeoJsonOutputModel>();
        Iterator itr= appList.iterator();
        while(itr.hasNext()){
            ClientApp clientApp = (ClientApp)itr.next();

            List<Crisis> crises = crisisDao.getClientAppCrisisDetail(clientApp.getClientAppID());

            if(clientApp.getStatus().equals(StatusCodeType.MICROMAPPER_ONLY)){
                List<TaskQueue> taskQueues =  taskQueueService.getTaskQueueByClientAppStatus(clientApp.getClientAppID(),StatusCodeType.TASK_PUBLISHED);
                if(taskQueues != null ){

                    int queueSize =   MAX_IMPORT_PROCESS_QUEUE_SIZE;
                    //if(taskQueues.size() < MAX_IMPORT_PROCESS_QUEUE_SIZE)
                    {
                        queueSize =  taskQueues.size();
                    }

                    logger.info("TaskQueueSize: "+ queueSize);

                    Date processStartTime = new Date();

                    for(int i=0; i < queueSize; i++){
                        TaskQueue taskQueue = taskQueues.get(i);
                        Long taskID =  taskQueue.getTaskID();

                        String taskQueryURL = PYBOSSA_API_TASK_BASE_URL + clientApp.getPlatformAppID() + "&id=" + taskID;
                        String inputData = pybossaCommunicator.sendGet(taskQueryURL);
                        try {

                            if (inputData != null) {
                                boolean isFound = pybossaFormatter.isTaskStatusCompleted(inputData);
                                logger.info("isFound: "+isFound);
                                if (isFound) {
                                    boolean isProcessed = this.processQueueImportForNonText(clientApp, taskQueue, taskID, geoJsonOutputModels);
                                    if(isProcessed){
                                        // Some new data has been processed
                                        if(crises != null && !crises.isEmpty()){
                                            Crisis crisis = crises.get(0);
                                            pusherService.triggerNotification(crisis.getCrisisID(), clientApp.getClientAppID(), crisis.getClickerType(), crisis.getDisplayName(), processStartTime.getTime());
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("processTaskImport: " + e, e);
                        }
                    }
                    // Map data export
                    try {
                        reportProductService.generateGeoJsonForClientApp(clientApp.getClientAppID());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }
        }
    }

    private boolean processQueueImportForNonText(ClientApp clientApp,TaskQueue taskQueue, Long taskID, List<GeoJsonOutputModel> geoJsonOutputModels) throws Exception {
        boolean isProcessed = false;
        String PYBOSSA_API_TASK_RUN = PYBOSSA_API_TASK_RUN_BASE_URL + clientApp.getPlatformAppID() + "&task_id=" + taskID;

        String importResult = pybossaCommunicator.sendGet(PYBOSSA_API_TASK_RUN) ;

        if(!importResult.isEmpty() && importResult.length() > StatusCodeType.RESPONSE_MIN_LENGTH  ){

            TaskQueueResponse taskQueueResponse = null;
            ClientAppAnswer clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());

            if(clientApp.getAppType().equals(StatusCodeType.APP_VIDEO)){
                taskQueueResponse = pybossaFormatter.getAnswerResponseForVideo(clientApp, importResult, parser, taskQueue.getTaskQueueID(), clientAppAnswer,reportTemplateService);

            }
            else if(clientApp.getAppType().equals(StatusCodeType.APP_MAP))
            {
                logger.info("processing image : "+ clientApp.getClientAppID());
                Crisis c = this.getCrisisDetail(clientApp);
                MarkerStyle style = this.getMarkerStyleForClientApp(clientApp, c);
                ClientAppSource clientAppSource = clientAppSourceService.getClientAppSourceByClientAppID(taskQueue.getClientAppSourceID());
                taskQueueResponse = pybossaFormatter.getAnswerResponseForGeo(importResult, parser, taskQueue.getTaskQueueID(), clientApp, c, style, clientAppSource, reportTemplateService, typhoonRubyTextGeoClickerDao);
            }
            else if(clientApp.getAppType().equals(StatusCodeType.APP_AERIAL))
            {

                if(clientApp.getIsCustom()){
                    taskQueueResponse = externalCustomService.getAnswerResponse(clientApp,importResult,taskQueue);

                }
                else{
                    // expected standard
                    taskQueueResponse = pybossaFormatter.getAnswerResponseForAerial(importResult, parser, taskQueue.getTaskQueueID(), clientApp);
                }

            }
            else{
                taskQueueResponse = pybossaFormatter.getAnswerResponse(clientApp, importResult, parser, taskQueue.getTaskQueueID(), clientAppAnswer, reportTemplateService);
            }

            if(taskQueueResponse != null && !taskQueueResponse.getResponse().equals("[]")){
                clientAppResponseService.processTaskQueueResponse(taskQueueResponse);
                taskQueue.setStatus(StatusCodeType.TASK_LIFECYCLE_COMPLETED);
                updateTaskQueue(taskQueue);
                isProcessed = true;
                logger.info("TaskQueueResponse Created");
            }
        }
        return isProcessed;
    }

    public void processQueueImportForText(ClientApp clientApp){
        //http://crowdcrafting.org/api/task?app_id=749&limit=10&state=completed
        //http://localhost:5000/api/task?app_id=176&id=6109
        List<TaskQueue> taskQueues =  taskQueueService.getTaskQueueByClientAppStatus(clientApp.getClientAppID(), LookupCode.TASK_PUBLISHED);

        if(taskQueues != null ){
            int max_loop_size = taskQueues.size();

            for(int i=0; i < max_loop_size; i++){
                TaskQueue taskQueue = taskQueues.get(i);
                //if(!this.isExpiredTaskQueue(taskQueue)){
                Long taskID =  taskQueue.getTaskID();
                //System.out.println("taskID :" + taskID);
                String taskQueryURL = PYBOSSA_API_TASK_BASE_URL + clientApp.getPlatformAppID() + "&id=" + taskID;
                String inputData = pybossaCommunicator.sendGet(taskQueryURL);

                try {
                    boolean isFound = pybossaFormatter.isTaskStatusCompleted(inputData);

                    if(isFound){

                        this.processQueueResponseImportForText(clientApp, taskQueue, taskID);
                    }

                } catch (Exception e) {
                    System.out.println("Error for processTaskRunPerClientAppImport: " + clientApp.getShortName());
                    System.out.println("Error for processTaskRunPerClientAppImport: taskID" + taskID);

                }
                // }
            }
        }
    }

    private void processQueueResponseImportForText(ClientApp clientApp, TaskQueue taskQueue, Long taskID) throws Exception {
        String PYBOSSA_API_TASK_RUN = PYBOSSA_API_TASK_RUN_BASE_URL + clientApp.getPlatformAppID() + "&task_id=" + taskID;
        String importResult = pybossaCommunicator.sendGet(PYBOSSA_API_TASK_RUN) ;

        ClientAppAnswer clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());

        if(clientAppAnswer == null){
            int cutOffValue = LookupCode.MAX_VOTE_CUT_OFF_VALUE;
            String AIDR_NOMINAL_ATTRIBUTE_LABEL_URL_PER_APP = AIDR_NOMINAL_ATTRIBUTE_LABEL_URL + clientApp.getCrisisID() + "/" + clientApp.getNominalAttributeID();
            String answerSet = pybossaCommunicator.sendGet(AIDR_NOMINAL_ATTRIBUTE_LABEL_URL_PER_APP) ;

            if(clientApp.getTaskRunsPerTask() < LookupCode.MAX_VOTE_CUT_OFF_VALUE){
                cutOffValue = LookupCode.MIN_VOTE_CUT_OFF_VALUE;
            }

            clientAppResponseService.saveClientAppAnswer(clientApp.getClientAppID(), answerSet, cutOffValue);
            clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());
        }

        String pybossaResult = importResult;

        if(DataFormatValidator.isValidateJson(importResult)){

            pybossaResult = textPybossaFormatter.buildTaskOutputForAIDR(taskQueue.getTaskQueueID(), importResult, parser, clientApp, clientAppAnswer);

            int responseCode =  LookupCode.HTTP_OK;

            if(pybossaResult != null && !textPybossaFormatter.getTranslateRequired()){
                responseCode = pybossaCommunicator.sendPost(pybossaResult, AIDR_TASK_ANSWER_URL);
            }

            if(responseCode == LookupCode.HTTP_OK ||responseCode == LookupCode.HTTP_OK_NO_CONTENT || textPybossaFormatter.getTranslateRequired() ){
                TaskQueueResponse taskQueueResponse = textPybossaFormatter.getTaskQueueResponse(clientApp,
                        importResult, parser, taskQueue.getTaskQueueID(),
                        clientAppAnswer, reportTemplateService);

                if(textPybossaFormatter.getTranslateRequired()){
                    textPybossaFormatter.setTranslateRequired(false);
                    taskQueueResponse.setTaskInfo(pybossaResult);
                }
                taskQueue.setStatus(LookupCode.TASK_LIFECYCLE_COMPLETED);
                taskQueueService.updateTaskQueue(taskQueue);
                clientAppResponseService.processTaskQueueResponse(taskQueueResponse);

            }

        }
    }

    private void processTranslations(ClientApp clientApp) throws Exception {
        translationService.processTranslations(clientApp);
    }

    /** Import process  ends**/


   /** Publishing process  starts**/

    @Override
    public void processTaskPublish() throws Exception{
        logger.info("processTaskPublish is starting");

        /// 1. text publish
        /// 2. non text publish
        this.processTaskPublishForText();
        this.processTaskPublishForNonText();
    }

    public void processTaskPublishForNonText() throws Exception{
        setClassVariable();

        if(client == null){
            logger.info("client IS NULL");
            return;
        }

        List<ClientApp> appList = clientAppService.getAllClientAppByClientIDAndStatus(client.getClientID(), StatusCodeType.MICROMAPPER_ONLY);

        for(int i=0; i < appList.size(); i++){

            ClientApp currentClientApp =  appList.get(i);
            logger.info("clientApp processTaskPublish currentClientApp : " + currentClientApp.getShortName());
            List<ClientAppSource> datasources = clientAppSourceService.getClientAppSourceByStatus(currentClientApp.getClientAppID(), StatusCodeType.EXTERNAL_DATA_SOURCE_ACTIVE);
            logger.info("clientApp processTaskPublish datasources : " + datasources.size());
            for(int j=0; j < datasources.size(); j++){

                List<MicromapperInput> micromapperInputList = null;
                String url = datasources.get(j).getSourceURL();

                if(!cvsRemoteFileFormatter.doesSourcerExist(url)){
                    continue;
                }

                if(currentClientApp.getAppType() == StatusCodeType.APP_MAP){
                    micromapperInputList = cvsRemoteFileFormatter.getGeoClickerInputData(url);
                }
                else{
                    if(currentClientApp.getAppType() == StatusCodeType.APP_AERIAL){
                        micromapperInputList = cvsRemoteFileFormatter.getAerialClickerInputData(url);
                    }
                    else if(currentClientApp.getAppType() == StatusCodeType.APP_3W){
                        micromapperInputList = cvsRemoteFileFormatter.get3WClickerInputData(url);
                    }
                    else{
                        micromapperInputList = cvsRemoteFileFormatter.getClickerInputData(url);
                    }
                }

                if(micromapperInputList != null){
                    ClientAppSource source = datasources.get(j);

                    if(micromapperInputList.size() > 0) {
                        this.processNonTextPushing(currentClientApp, micromapperInputList , source.getClientAppSourceID());
                    }

                    clientAppSourceService.updateClientAppSourceStatus(source.getClientAppSourceID(), StatusCodeType.EXTERNAL_DATA_SOURCE_USED);

                }
            }

            this.searchUpdateNextAvailableAppSource(currentClientApp.getClientAppID());

        }
    }

    private void processNonTextPushing(ClientApp currentClientApp, List<MicromapperInput> micromapperInputList, Long clientAppSourceID) throws Exception{
        try {
            List<String> aidr_data = pybossaFormatter.assemblePybossaTaskPublishForm(micromapperInputList, currentClientApp);

            for (String temp : aidr_data) {

                String response = pybossaCommunicator.sendPostGet(temp, PYBOSSA_API_TASK_PUBLSIH_URL) ;

                if(!response.startsWith(StatusCodeType.EXCEPTION_STRING)){

                    generateToTaskQueueForNonText(response, currentClientApp.getClientAppID(), StatusCodeType.TASK_PUBLISHED, clientAppSourceID) ;
                }
                else{
                    generateToTaskQueueForNonText(temp, currentClientApp.getClientAppID(), StatusCodeType.Task_NOT_PUBLISHED, clientAppSourceID) ;
                }
            }
        // data is consumed. need to mark as completed not to process anymore.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void processTaskPublishForText() throws Exception{
        System.out.println("processTaskPublish : Start : " + new Date());
        List<ClientApp> appList = clientAppService.findClientAppByStatus(LookupCode.AIDR_ONLY)  ;

        if(appList != null) {
            for (int index = 0; index < appList.size() ; index++){

                this.setClassVariablePerClient(appList.get(index).getClient());

                int pushTaskNumber = calculateMinNumber(appList.get(index));
                if( pushTaskNumber > 0 ){

                    String api = AIDR_API_URL + appList.get(index).getCrisisID() + "/" + pushTaskNumber;

                    logger.info("Send request :: " + api);
                    String inputData = pybossaCommunicator.sendGet(api);

                    if(DataFormatValidator.isValidateJson(inputData)){
                        try {
                            this.processTextPushing(appList.get(index), inputData, pushTaskNumber)  ;

                        } catch (Exception e) {
                            logger.warn("error in publishing data", e);
                        }
                    }
                }
            }
        }


    }

    public void processTextPushing(ClientApp currentClientApp, String inputData, int pushTaskNumber){
        try{
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(inputData);
            JSONArray jsonObject = (JSONArray) obj;
            int inputDataSize =  jsonObject.size();

            int itemsPerApp = inputDataSize;
            int itemIndexStart = 0;
            int itemIndexEnd = itemsPerApp;

            if(itemIndexStart < itemIndexEnd) {

                // if translate is required, let's go directly
                if(currentClientApp.getTcProjectId() != null){
                    textPybossaFormatter.setTranslationService(translationService);
                    textPybossaFormatter.publicTaskTranslationTaskPublishForm(
                            inputData, currentClientApp, itemIndexStart, itemIndexEnd);
                }
                else{
                    List<String> aidrData = textPybossaFormatter.assemblePybossaTaskPublishFormWithIndex(
                            inputData, currentClientApp, itemIndexStart, itemIndexEnd);
                    int itemLoopEnd = itemIndexEnd - itemIndexStart;
                    for(int i = 0; i < itemLoopEnd; i++){
                        String taskToPublish = aidrData.get(i);
                        String response = pybossaCommunicator.sendPostGet(taskToPublish, PYBOSSA_API_TASK_PUBLSIH_URL) ;
                        if(!response.startsWith("Exception") && !response.contains("exception_cls")){
                            addTextPublishingToTaskQueue(response, currentClientApp.getClientAppID(), LookupCode.TASK_PUBLISHED) ;
                        }
                        else{
                            addTextPublishingToTaskQueue(taskToPublish, currentClientApp.getClientAppID(), LookupCode.Task_NOT_PUBLISHED) ;
                        }
                    }
                    itemIndexStart = itemIndexEnd;
                    itemIndexEnd = itemIndexEnd + itemsPerApp;
                    if(itemIndexEnd > inputDataSize && itemIndexStart < inputDataSize){
                        itemIndexEnd = itemIndexStart + (inputDataSize - itemIndexStart);
                    }
                }

            }

        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
    }

    private void addTextPublishingToTaskQueue(String inputData, Long clientAppID, Integer status){

        try {
            Object obj = parser.parse(inputData);

            JSONObject jsonObject = (JSONObject) obj;

            Long taskID  = (Long)jsonObject.get("id");
            JSONObject info = (JSONObject)jsonObject.get("info");
            Long documentID = (Long)info.get("documentID");

            if(status.equals(LookupCode.Task_NOT_PUBLISHED)){
                pybossaCommunicator.sendGet(AIDR_ASSIGNED_TASK_CLEAN_UP_URL+ documentID) ;
            }
            else{
                TaskQueue taskQueue = new TaskQueue(taskID, clientAppID, documentID, status);
                taskQueueService.createTaskQueue(taskQueue);
            }

        } catch (ParseException e) {
            logger.error("Error parsing: " + inputData);
            logger.error(e.getMessage());
        }
    }

    private void generateToTaskQueueForNonText(String inputData, Long clientAppID, Integer status, Long clientAppSourceID){
        try {
            Object obj = parser.parse(inputData);
            JSONObject jsonObject = (JSONObject) obj;

            logger.info("addToTaskQueue : " + inputData);

            Long taskID  = (Long)jsonObject.get("id");
            JSONObject info = (JSONObject)jsonObject.get("info");
            Long documentID = (Long)info.get("documentID");

            TaskQueue taskQueue = new TaskQueue(taskID, clientAppID, documentID, status);
            // mostly micromapper will have outside source. AIDR will have docID
            taskQueue.setClientAppSourceID(clientAppSourceID);

            taskQueueService.createTaskQueue(taskQueue);


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void searchUpdateNextAvailableAppSource(Long clientAppID){
        List<ClientAppSource> sourceList =  clientAppSourceService.getClientAppSourceByStatus(clientAppID, StatusCodeType.EXTERNAL_DATA_SOURCE_UPLOADED);

        logger.info("searchUpdateNextAvailableAppSource : " + sourceList.size());

        if(sourceList.size() > 0){
            logger.info("searchUpdateNextAvailableAppSource2 : " + sourceList.get(0).getClientAppSourceID());
            clientAppSourceService.updateClientAppSourceStatus(sourceList.get(0).getClientAppSourceID(), StatusCodeType.EXTERNAL_DATA_SOURCE_ACTIVE);
        }
    }

    /** Publishing process  end**/



    /** export process  start**/
    @Override
    public void processTaskExport() throws Exception {
        reportProductService.generateCVSReportForImageGeoClicker();
        reportProductService.generateCVSReportForTextGeoClicker();
        reportProductService.generateMapBoxTemplateForAerialClicker();

    }
    /** export process  end**/

    private void updateTaskQueue(TaskQueue taskQueue){
        taskQueueService.updateTaskQueue(taskQueue);
    }

    public JSONObject getMarkerStyleForRubyClicker(MarkerStyle markerStyle, String answer){
        JSONObject selectedStyle = null;
        try {
            if(markerStyle != null){
                JSONObject mJson = (JSONObject)parser.parse(markerStyle.getStyle());
                JSONArray mStyles = (JSONArray)mJson.get("style");
                for(Object a : mStyles) {
                    JSONObject aStyle = (JSONObject)a;
                    String label_code = (String)aStyle.get("label_code");
                    if(label_code.equals(answer.trim())){
                        selectedStyle = aStyle;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedStyle;
    }

    private MarkerStyle getMarkerStyleForClientApp(ClientApp clientApp, Crisis c){

        MarkerStyle selectedStyle = null;
        try {
            List<MarkerStyle> styleTemplate = markerStyleDao.findByClientAppID(clientApp.getClientAppID().longValue()) ;
            if(styleTemplate.isEmpty() && c != null){
                styleTemplate = markerStyleDao.findByAppType(c.getClickerType());
            }

            if(styleTemplate.size() > 0){
                return  styleTemplate.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedStyle;
    }

    private Crisis getCrisisDetail(ClientApp clientApp){
        List<Crisis> cList = crisisDao.getClientAppCrisisDetail(clientApp.getClientAppID());

        if(cList.size() > 0) {
            return cList.get(0);
        }
        return null;
    }

    /*** util class ***/

    @SuppressWarnings("unchecked")
    @Override
    public void processTyphoonRubyTextClikcer() throws ParseException{
        List<MarkerStyle> markerStyles = markerStyleDao.findByClientAppID(79);
        MarkerStyle markerStyle = markerStyles.get(0);

        List<TaskQueue> taskQueses = taskQueueService.getTaskQueueByClientAppId(79L);

        for(TaskQueue taskQueue : taskQueses){

            boolean isProcessed = false;
            List<TaskQueueResponse> taskQueueResponses = taskQueueResponseDao.getTaskQueueResponse(taskQueue.getTaskQueueID());
            for(TaskQueueResponse taskQueueResponse : taskQueueResponses){
                if(taskQueueResponse != null && !taskQueueResponse.getResponse().equals("") && !taskQueueResponse.getResponse().equals("{}")){
                    List<TyphoonRubyTextGeoClicker> typhoonRubyTextGeoClickers = typhoonRubyTextGeoClickerDao.getTyphoonRubyTextGeoClickerByTaskId(taskQueue.getTaskID());
                    if(typhoonRubyTextGeoClickers != null && !typhoonRubyTextGeoClickers.isEmpty()){
                        TyphoonRubyTextGeoClicker typhoonRubyTextGeoClicker = typhoonRubyTextGeoClickers.get(0);

                        String answer = null;

                        String data = taskQueueResponse.getTaskInfo();

                        if(data != null && !data.isEmpty() && !data.equalsIgnoreCase("null")){
                            try {
                                long tweetId = Long.parseLong(data);
                                List<ReportTemplateTyphoonRuby> reportTemplateTyphoonRubys = reportTemplateTyphoonRubyDao.getReportTemplateTyphoonRubyByTweetId(tweetId);
                                if(reportTemplateTyphoonRubys != null && !reportTemplateTyphoonRubys.isEmpty()){
                                    ReportTemplateTyphoonRuby reportTemplateTyphoonRuby = reportTemplateTyphoonRubys.get(0);
                                    answer = reportTemplateTyphoonRuby.getAnswer();
                                }
                            } catch (NumberFormatException e) {
                                answer = data;
                            }
                        }

                        if(answer != null && !answer.isEmpty()){
                            JSONArray responseArray = new JSONArray();

                            JSONObject responseObject = (JSONObject)parser.parse(taskQueueResponse.getResponse());

                            JSONObject propertyObject = new JSONObject();

                            JSONObject markerStyleForResponse = getMarkerStyleForRubyClicker(markerStyle, answer);
                            if(markerStyleForResponse == null){
                                continue;
                            }

    						/*JSONArray responseArray = (JSONArray)parser.parse(taskQueueResponse.getResponse());

    						JSONObject responseObject = (JSONObject) responseArray.get(0);

    						JSONObject propertyObject = (JSONObject) responseObject.get("properties");
    						if(propertyObject == null){
    							continue;
    						}*/

                            propertyObject.put("category", answer);
                            propertyObject.put("crisis_type", "Text");
                            propertyObject.put("crisis_name", "Typhoon Ruby");
                            propertyObject.put("tweetid", typhoonRubyTextGeoClicker.getFinalTweetID());
                            propertyObject.put("tweet", typhoonRubyTextGeoClicker.getTweet());
                            propertyObject.put("taskid", typhoonRubyTextGeoClicker.getTaskId());


                            propertyObject.put("style", markerStyleForResponse );

                            if (markerStyleForResponse != null) {
                                responseObject.put("properties", propertyObject);
                                responseArray.remove(0);
                                responseArray.add(responseObject);
                                logger.info("------------------------------------------------------");
                                logger.info("TaskQueueId: " + taskQueueResponse.getTaskQueueID()
                                        + " Processed.....");
                                logger.info(responseArray);
                                taskQueueResponse.setResponse(responseArray.toJSONString());
                                clientAppResponseService.processTaskQueueResponse(taskQueueResponse);
                                isProcessed = true;
                            }
                        }

                    }
                }

                if(!isProcessed && taskQueueResponse != null && !taskQueueResponse.getResponse().equals("")){
                    JSONArray responseArray = new JSONArray();
                    JSONObject responseObject = (JSONObject)parser.parse(taskQueueResponse.getResponse());
                    responseArray.add(responseObject);
                    logger.info("------------------------------------------------------");
                    logger.info("TaskQueueId: " + taskQueueResponse.getTaskQueueID() + "Not Processed.....");
                    logger.info(responseArray);
                    taskQueueResponse.setResponse(responseArray.toJSONString());
                    //clientAppResponseService.processTaskQueueResponse(taskQueueResponse);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processTyphoonRubyImageClikcer() throws ParseException{
        //List<TaskQueueResponse> taskQueueResponses = clientAppResponseService.getTaskQueueResponseByClientApp("mm_rubyimagegeoclicker");
        List<TaskQueueResponse> taskQueueResponses = taskQueueResponseDao.getAll();

        List<MarkerStyle> findByAppType = markerStyleDao.findByAppType("Image");
        MarkerStyle markerStyle = findByAppType.get(0);

        for(TaskQueueResponse taskQueueResponse : taskQueueResponses){
            if(taskQueueResponse.getTaskQueueID() > 130604){
                continue;
            }
            String response = taskQueueResponse.getResponse();
            if(response != null && !response.equals("") && !response.equals("{}")){
                JSONObject responseObject = (JSONObject)parser.parse(response);
                if(responseObject.get("properties") == null){
                    List<ReportTemplate> reportTemplates = reportTemplateService.getReportTemplateSearchByTwittID("tweetID", taskQueueResponse.getTaskInfo());
                    if(reportTemplates != null && !reportTemplates.isEmpty()){
                        for(ReportTemplate reportTemplate : reportTemplates){
                            if(reportTemplate.getAnswer() == null || reportTemplate.getAnswer().isEmpty() || reportTemplate.getAnswer().equalsIgnoreCase("none")){
                                continue;
                            }
                            JSONObject propertyObject = new JSONObject();
                            propertyObject.put("category", reportTemplate.getAnswer());
                            propertyObject.put("crisis_type", "Image");
                            propertyObject.put("crisis_name", "Typhoon Ruby");
                            propertyObject.put("tweetid", reportTemplate.getTweetID());
                            propertyObject.put("url", reportTemplate.getUrl());

                            JSONObject markerStyleForResponse = getMarkerStyleForRubyClicker(markerStyle, reportTemplate.getAnswer());
                            propertyObject.put("style", markerStyleForResponse );

                            if (markerStyleForResponse != null) {
                                responseObject.put("properties", propertyObject);
                                logger.info("------------------------------------------------------");
                                logger.info("TaskQueueId: " + taskQueueResponse.getTaskQueueID()
                                        + " Processed.....");
                                logger.info(responseObject);
                                taskQueueResponse.setResponse(responseObject.toJSONString());
                                clientAppResponseService.processTaskQueueResponse(taskQueueResponse);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    private int calculateMinNumber(ClientApp obj){
        int min = MAX_PENDING_QUEUE_SIZE;

        if(obj.getTcProjectId()== null){
            int currentPendingTask =  taskQueueService.getCountTaskQueueByStatusAndClientApp(obj.getClientAppID(), LookupCode.AIDR_ONLY).intValue();
            int numQueue = MAX_PENDING_QUEUE_SIZE - currentPendingTask;
            if(numQueue < 0) {
                min =  0;
            }
            else{
                min =  numQueue;
            }
        }
        else{
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY ){
                min = 1000 ;
                List<TaskTranslation> taskTranslations = translationService.findAllTranslationsByClientAppIdAndStatus(obj.getClientAppID(), TaskTranslation.STATUS_IN_PROGRESS, min);

                if(taskTranslations.size() > 0){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                    String rightNow = sdf.format(new Date());
                    String recordDate = sdf.format(taskTranslations.get(0).getCreated())   ;
                    if(rightNow.equalsIgnoreCase(recordDate)){
                        min = 1000 - taskTranslations.size();
                    }
                }
                else{
                    min = 1000;
                }


            } else {
                min = 0;
            }
        }

        return min;

    }

    @Override
    public void processTaskImportOnDemand(String shortName) throws Exception{
        setClassVariable();
        if(client == null){
            return;
        }

        List<GeoJsonOutputModel> geoJsonOutputModels =  new ArrayList<GeoJsonOutputModel>();

        ClientApp clientApp = clientAppService.findClientAppByCriteria("shortName", shortName);

        List<TaskQueue> taskQueues =  taskQueueService.getTaskQueueByClientAppStatus(clientApp.getClientAppID(),StatusCodeType.TASK_PUBLISHED);

        if(taskQueues != null ){
            int queueSize = 1;
            int MAX_TO_PROCESS = MAX_IMPORT_PROCESS_QUEUE_SIZE;
            queueSize  = MAX_TO_PROCESS;
            if(taskQueues.size() < MAX_TO_PROCESS)
            {
                queueSize =  taskQueues.size();
            }

            for(int i=0; i < queueSize; i++){
                TaskQueue taskQueue = taskQueues.get(i);
                Long taskID =  taskQueue.getTaskID();
                logger.info("taskID :" + taskID);
                //String taskQueryURL = PYBOSSA_API_TASK_BASE_URL + clientApp.getPlatformAppID() + "&id=" + taskID;
                //String inputData = pybossaCommunicator.sendGet(taskQueryURL);
                try {

                    //  boolean isFound = pybossaFormatter.isTaskStatusCompleted(inputData);
                    boolean isFound = true;
                    if(isFound){
                        processQueueImportForNonText(clientApp, taskQueue, taskID, geoJsonOutputModels) ;
                    }

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }


    }
}

