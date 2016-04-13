package qa.qcri.mm.trainer.pybossa.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import qa.qcri.mm.trainer.pybossa.entity.Client;
import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppAnswer;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppSource;
import qa.qcri.mm.trainer.pybossa.entity.Crisis;
import qa.qcri.mm.trainer.pybossa.entity.MarkerStyle;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplateTyphoonRuby;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueue;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueueResponse;
import qa.qcri.mm.trainer.pybossa.entity.TyphoonRubyTextGeoClicker;
import qa.qcri.mm.trainer.pybossa.format.impl.CVSRemoteFileFormatter;
import qa.qcri.mm.trainer.pybossa.format.impl.GeoJsonOutputModel;
import qa.qcri.mm.trainer.pybossa.format.impl.MicroMapperPybossaFormatter;
import qa.qcri.mm.trainer.pybossa.format.impl.MicromapperInput;
import qa.qcri.mm.trainer.pybossa.service.ClientAppResponseService;
import qa.qcri.mm.trainer.pybossa.service.ClientAppService;
import qa.qcri.mm.trainer.pybossa.service.ClientAppSourceService;
import qa.qcri.mm.trainer.pybossa.service.ClientService;
import qa.qcri.mm.trainer.pybossa.service.ExternalCustomService;
import qa.qcri.mm.trainer.pybossa.service.MicroMapperWorker;
import qa.qcri.mm.trainer.pybossa.service.PusherService;
import qa.qcri.mm.trainer.pybossa.service.ReportProductService;
import qa.qcri.mm.trainer.pybossa.service.ReportTemplateService;
import qa.qcri.mm.trainer.pybossa.service.TaskQueueService;
import qa.qcri.mm.trainer.pybossa.store.StatusCodeType;
import qa.qcri.mm.trainer.pybossa.store.URLPrefixCode;
import qa.qcri.mm.trainer.pybossa.store.UserAccount;

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

    private final PybossaCommunicator pybossaCommunicator = new PybossaCommunicator();
    private final JSONParser parser = new JSONParser();
    private final MicroMapperPybossaFormatter pybossaFormatter = new MicroMapperPybossaFormatter();


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


    public void setClassVariable() throws Exception{
        client = clientService.findClientByCriteria("name", UserAccount.MIROMAPPER_USER_NAME);
        if(client != null){
            PYBOSSA_API_TASK_PUBLSIH_URL = client.getHostURL() + URLPrefixCode.TASK_PUBLISH + client.getHostAPIKey();
            PYBOSSA_API_TASK_BASE_URL  = client.getHostURL() + URLPrefixCode.TASK_INFO;
            PYBOSSA_API_TASK_RUN_BASE_URL =  client.getHostURL() + URLPrefixCode.TASKRUN_INFO;
            MAX_PENDING_QUEUE_SIZE = client.getQueueSize();

        }

    }

    public void setClassVariableByUserName(String userName) throws Exception{
        client = clientService.findClientByCriteria("name", userName);

        if(client != null){
            PYBOSSA_API_TASK_PUBLSIH_URL = client.getHostURL() + URLPrefixCode.TASK_PUBLISH + client.getHostAPIKey();
            PYBOSSA_API_TASK_BASE_URL  = client.getHostURL() + URLPrefixCode.TASK_INFO;
            PYBOSSA_API_TASK_RUN_BASE_URL =  client.getHostURL() + URLPrefixCode.TASKRUN_INFO;
            MAX_PENDING_QUEUE_SIZE = client.getQueueSize();
        }

    }
    
    
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
   								System.out.println("------------------------------------------------------");
   								System.out.println("TaskQueueId: " + taskQueueResponse.getTaskQueueID()
   										+ " Processed.....");
   								System.out.println(responseArray);
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
					System.out.println("------------------------------------------------------");
					System.out.println("TaskQueueId: " + taskQueueResponse.getTaskQueueID() + "Not Processed.....");
					System.out.println(responseArray);
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
								System.out.println("------------------------------------------------------");
								System.out.println("TaskQueueId: " + taskQueueResponse.getTaskQueueID()
										+ " Processed.....");
								System.out.println(responseObject);
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

    @Override
    public void processTaskPublish() throws Exception{
        System.out.println("processTaskPublish is starting");
        setClassVariable();

        
        if(client == null){
            System.out.println(   "client IS NULL");
            return;
        }

        List<ClientApp> appList = clientAppService.getAllClientAppByClientIDAndStatus(client.getClientID(), StatusCodeType.MICROMAPPER_ONLY );

        if(appList.size() > 0){
            for(int i=0; i < appList.size(); i++){

                ClientApp currentClientApp =  appList.get(i);
                System.out.println(   "clientApp processTaskPublish currentClientApp : " +  currentClientApp.getShortName());
                List<ClientAppSource> datasources = clientAppSourceService.getClientAppSourceByStatus(currentClientApp.getClientAppID(), StatusCodeType.EXTERNAL_DATA_SOURCE_ACTIVE);
                System.out.println(   "clientApp processTaskPublish datasources : " +  datasources.size());
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
                            this.publishToPybossa(currentClientApp, micromapperInputList , source.getClientAppSourceID());
                        }

                        clientAppSourceService.updateClientAppSourceStatus(source.getClientAppSourceID(), StatusCodeType.EXTERNAL_DATA_SOURCE_USED);

                    }
                }

                this.searchUpdateNextAvailableAppSource(currentClientApp.getClientAppID());

            }
        }

    }

    @Override
    public void processTaskImport() throws Exception{    	   	
        logger.info("Data import is starting");
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
            if(clientApp.getAppType() != 4 && clientApp.getAppType() != 5){
            	continue;
            }
           

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
									boolean isProcessed = this.processTaskQueueImport(clientApp, taskQueue, taskID, geoJsonOutputModels);
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
                    System.out.println("taskID :" + taskID);
                    //String taskQueryURL = PYBOSSA_API_TASK_BASE_URL + clientApp.getPlatformAppID() + "&id=" + taskID;
                    //String inputData = pybossaCommunicator.sendGet(taskQueryURL);
                    try {

                      //  boolean isFound = pybossaFormatter.isTaskStatusCompleted(inputData);
                        boolean isFound = true;
                        if(isFound){
                            processTaskQueueImport(clientApp,taskQueue,taskID, geoJsonOutputModels) ;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }


    }

    @Override
    public void processTaskExport() throws Exception {
        reportProductService.generateCVSReportForGeoClicker();
        reportProductService.generateMapBoxTemplateForAerialClicker();

    }

    private boolean processTaskQueueImport(ClientApp clientApp,TaskQueue taskQueue, Long taskID, List<GeoJsonOutputModel> geoJsonOutputModels) throws Exception {
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

    private void publishToPybossa(ClientApp currentClientApp, List<MicromapperInput> micromapperInputList, Long clientAppSourceID){
        try {
            List<String> aidr_data = pybossaFormatter.assemblePybossaTaskPublishForm(micromapperInputList, currentClientApp);

            for (String temp : aidr_data) {

                String response = pybossaCommunicator.sendPostGet(temp, PYBOSSA_API_TASK_PUBLSIH_URL) ;

                if(!response.startsWith(StatusCodeType.EXCEPTION_STRING)){

                    addToTaskQueue(response, currentClientApp.getClientAppID(), StatusCodeType.TASK_PUBLISHED, clientAppSourceID) ;
                }
                else{
                    addToTaskQueue(temp, currentClientApp.getClientAppID(), StatusCodeType.Task_NOT_PUBLISHED, clientAppSourceID) ;
                }
            }
            // data is consumed. need to mark as completed not to process anymore.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void addToTaskQueue(String inputData, Long clientAppID, Integer status, Long clientAppSourceID){
        try {
            Object obj = parser.parse(inputData);
            JSONObject jsonObject = (JSONObject) obj;

            System.out.println("addToTaskQueue : " + inputData);

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

    private void updateTaskQueue(TaskQueue taskQueue){
        taskQueueService.updateTaskQueue(taskQueue);

    }

    private void searchUpdateNextAvailableAppSource(Long clientAppID){
        List<ClientAppSource> sourceList =  clientAppSourceService.getClientAppSourceByStatus(clientAppID, StatusCodeType.EXTERNAL_DATA_SOURCE_UPLOADED);

        System.out.println("searchUpdateNextAvailableAppSource : " +sourceList.size());

        if(sourceList.size() > 0){
            System.out.println("searchUpdateNextAvailableAppSource2 : " + sourceList.get(0).getClientAppSourceID());
            clientAppSourceService.updateClientAppSourceStatus(sourceList.get(0).getClientAppSourceID(), StatusCodeType.EXTERNAL_DATA_SOURCE_ACTIVE);
        }
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
}
