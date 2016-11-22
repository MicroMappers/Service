package qa.qcri.mm.trainer.pybossa.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import qa.qcri.mm.trainer.pybossa.entity.TaskTranslation;
import qa.qcri.mm.trainer.pybossa.entity.TyphoonRubyTextGeoClicker;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;
import qa.qcri.mm.trainer.pybossa.format.impl.CSVRemoteFileFormatter;
import qa.qcri.mm.trainer.pybossa.format.impl.GeoJsonOutputModel;
import qa.qcri.mm.trainer.pybossa.format.impl.MicroMapperPybossaFormatter;
import qa.qcri.mm.trainer.pybossa.format.impl.MicromapperInput;
import qa.qcri.mm.trainer.pybossa.format.impl.TextClickerPybossaFormatter;
import qa.qcri.mm.trainer.pybossa.service.ClientAppResponseService;
import qa.qcri.mm.trainer.pybossa.service.ClientAppService;
import qa.qcri.mm.trainer.pybossa.service.ClientAppSourceService;
import qa.qcri.mm.trainer.pybossa.service.ClientService;
import qa.qcri.mm.trainer.pybossa.service.ExternalCustomService;
import qa.qcri.mm.trainer.pybossa.service.MicroMapperWorker;
import qa.qcri.mm.trainer.pybossa.service.PusherService;
import qa.qcri.mm.trainer.pybossa.service.ReportProductService;
import qa.qcri.mm.trainer.pybossa.service.ReportTemplateService;
import qa.qcri.mm.trainer.pybossa.service.TaskPybossaService;
import qa.qcri.mm.trainer.pybossa.service.TaskQueueService;
import qa.qcri.mm.trainer.pybossa.service.TaskRunPybossaService;
import qa.qcri.mm.trainer.pybossa.service.TranslationService;
import qa.qcri.mm.trainer.pybossa.store.LookupCode;
import qa.qcri.mm.trainer.pybossa.store.PybossaConf;
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
	private final CSVRemoteFileFormatter cvsRemoteFileFormatter = new CSVRemoteFileFormatter();
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

	@Autowired
	private TaskPybossaService taskPybossaService;

	@Autowired
	private TaskRunPybossaService taskRunPybossaService;

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

		this.processTaskRunImportForText();
		this.processTaskRunImportForNonText();
	}


	public void processTaskRunImportForText() throws Exception{
		logger.info("ProcessTaskRunImportForText : Start : " + new Date());

		textPybossaFormatter.setTranslationService(translationService);
		List<ClientApp> clientAppList = clientAppService.findClientAppByStatus(LookupCode.AIDR_ONLY);

		for (ClientApp clientApp : clientAppList) {
			this.setClassVariablePerClient(clientApp.getClient());
			this.processQueueImportForText(clientApp);

			if(clientApp.getTcProjectId() != null ){
				this.processTranslations(clientApp);
			}
		}
		logger.info("ProcessTaskRunImportForText : End : " + new Date());
	}

	public void processTaskRunImportForNonText() throws Exception{
		logger.info("ProcessTaskRunImportForNonText : Start : " + new Date());
		setClassVariable();

		if(client == null){
			return;
		}
		List<GeoJsonOutputModel> geoJsonOutputModels =  new ArrayList<GeoJsonOutputModel>();

		List<ClientApp> clientAppList = clientAppService.getAllClientAppByClientID(client.getClientID() );

		for (ClientApp clientApp : clientAppList) {

			List<Crisis> crises = crisisDao.getClientAppCrisisDetail(clientApp.getClientAppID());

			if(clientApp.getStatus().equals(StatusCodeType.MICROMAPPER_ONLY)){
				List<TaskQueue> taskQueues =  taskQueueService.getTaskQueueByClientAppStatus(clientApp.getClientAppID(), StatusCodeType.TASK_PUBLISHED);
				if(taskQueues != null ){

					int queueSize =   MAX_IMPORT_PROCESS_QUEUE_SIZE;
					//if(taskQueues.size() < MAX_IMPORT_PROCESS_QUEUE_SIZE)
					{
						queueSize =  taskQueues.size();
					}

					logger.info("TaskQueueSize: "+ queueSize);

					Date processStartTime = new Date();
					ClientAppAnswer clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());

					for (TaskQueue taskQueue : taskQueues) {

						Long taskID =  taskQueue.getTaskID();
						Long taskCount = taskPybossaService.getTaskCountByIdProjectIdAndState(taskID.intValue(), clientApp.getPlatformAppID().intValue(), PybossaConf.TASK_STATUS_COMPLETED);
						try {
							Crisis crisis = null;
							if (taskCount != null && taskCount > 0) {

								if(crises != null && !crises.isEmpty()){
									crisis = crises.get(0);
								}
								boolean isProcessed = this.processQueueImportForNonText(clientApp, crisis, taskQueue, clientAppAnswer, geoJsonOutputModels);
								if(isProcessed){
									// Some new data has been processed
									if(crisis != null){
										pusherService.triggerNotification(crisis.getCrisisID(), clientApp.getClientAppID(), crisis.getClickerType(), crisis.getDisplayName(), processStartTime.getTime());
									}
								}
							}
						} catch (Exception e) {
							logger.error("processTaskImport: " + e, e);
						}
					}

					// Map data export
					try {
						reportProductService.generateGeoJsonForClientApp(clientApp);
					} catch (Exception e) {
						logger.error("Exception while generating Geo Json for clientAppId: "+clientApp.getClientAppID(), e);
					}
				}
			}
		}
		logger.info("ProcessTaskRunImportForNonText : End : " + new Date());
	}

	private boolean processQueueImportForNonText(ClientApp clientApp, Crisis crisis, TaskQueue taskQueue, ClientAppAnswer clientAppAnswer, List<GeoJsonOutputModel> geoJsonOutputModels) throws Exception {
		boolean isProcessed = false;

		List<TaskRun> importedTaskRuns = taskRunPybossaService.getTaskRunByIdandProjectId(taskQueue.getTaskID().intValue(), clientApp.getPlatformAppID().intValue());

		// if(!importedTaskRuns.isEmpty() && importResult.length() > StatusCodeType.RESPONSE_MIN_LENGTH  ){
		if(!CollectionUtils.isEmpty(importedTaskRuns)){

			TaskQueueResponse taskQueueResponse = null;

			if(clientApp.getAppType().equals(StatusCodeType.APP_VIDEO)){
				taskQueueResponse = pybossaFormatter.getAnswerResponseForVideo(clientApp, importedTaskRuns, parser, taskQueue.getTaskQueueID(), clientAppAnswer,reportTemplateService);

			}
			else if(clientApp.getAppType().equals(StatusCodeType.APP_MAP))
			{
				logger.info("processing image for clientAppId : "+ clientApp.getClientAppID());
				MarkerStyle style = this.getMarkerStyleForClientApp(clientApp, crisis);
				ClientAppSource clientAppSource = clientAppSourceService.getClientAppSourceByID(taskQueue.getClientAppSourceID());
				taskQueueResponse = pybossaFormatter.getAnswerResponseForGeo(importedTaskRuns, parser, taskQueue.getTaskQueueID(), clientApp, crisis, style, clientAppSource, reportTemplateService, typhoonRubyTextGeoClickerDao);
			}
			else if(clientApp.getAppType().equals(StatusCodeType.APP_AERIAL))
			{

				if(clientApp.getIsCustom()){
					taskQueueResponse = externalCustomService.getAnswerResponse(clientApp, importedTaskRuns,taskQueue);

				}
				else{
					// expected standard
					taskQueueResponse = pybossaFormatter.getAnswerResponseForAerial(importedTaskRuns, parser, taskQueue.getTaskQueueID(), clientApp);
				}

			}
			else{
				taskQueueResponse = pybossaFormatter.getAnswerResponse(clientApp, importedTaskRuns, parser, taskQueue.getTaskQueueID(), clientAppAnswer, reportTemplateService);
			}

			if(taskQueueResponse != null && !taskQueueResponse.getResponse().equals("[]")){
				clientAppResponseService.processTaskQueueResponse(taskQueueResponse);
				taskQueue.setStatus(StatusCodeType.TASK_LIFECYCLE_COMPLETED);
				updateTaskQueue(taskQueue);
				isProcessed = true;
				logger.info("TaskQueueResponse Created for taskID: "+taskQueue.getTaskID());
			}
		}
		return isProcessed;
	}

	public void processQueueImportForText(ClientApp clientApp){
		//http://crowdcrafting.org/api/task?app_id=749&limit=10&state=completed
		//http://localhost:5000/api/task?app_id=176&id=6109
		logger.info("ProcessTaskRunImportForText.processQueueImportForText Starts for clientAppID : "+ clientApp.getClientAppID());

		ClientAppAnswer clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());
		if(clientAppAnswer == null){
			int cutOffValue = LookupCode.MAX_VOTE_CUT_OFF_VALUE;
			String AIDR_NOMINAL_ATTRIBUTE_LABEL_URL_PER_APP = AIDR_NOMINAL_ATTRIBUTE_LABEL_URL + clientApp.getCrisisID() + "/" + clientApp.getNominalAttributeID();
			String answerSet = pybossaCommunicator.sendGet(AIDR_NOMINAL_ATTRIBUTE_LABEL_URL_PER_APP) ;

			if(clientApp.getTaskRunsPerTask() < LookupCode.MAX_VOTE_CUT_OFF_VALUE){
				cutOffValue = LookupCode.MIN_VOTE_CUT_OFF_VALUE;
			}
			clientAppAnswer = clientAppResponseService.saveClientAppAnswer(clientApp.getClientAppID(), answerSet, cutOffValue);
			//clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());
		}

		List<TaskQueue> taskQueues =  taskQueueService.getTaskQueueByClientAppStatus(clientApp.getClientAppID(), LookupCode.TASK_PUBLISHED);

		if(taskQueues != null ){
			for (TaskQueue taskQueue : taskQueues) {

				//if(!this.isExpiredTaskQueue(taskQueue)){
				Long taskID =  taskQueue.getTaskID();

				//If count=0 => task Not completed. If count=1 => Task Completed
				Long taskCount = taskPybossaService.getTaskCountByIdProjectIdAndState(taskID.intValue(), clientApp.getPlatformAppID().intValue(), PybossaConf.TASK_STATUS_COMPLETED);

				try {
					if(taskCount > 0)
					{
						this.processQueueResponseImportForText(clientApp, taskQueue, taskID, clientAppAnswer);
					}

				} catch (Exception e) {
					logger.error("Error in  processQueueResponseImportForText: " + clientApp.getShortName());
					logger.error("Error in processQueueResponseImportForText: taskID" + taskID);
				}

			}
		}
		logger.info("ProcessTaskRunImportForText.processQueueImportForText Ends for clientAppID : "+ clientApp.getClientAppID());
	}

	private void processQueueResponseImportForText(ClientApp clientApp, TaskQueue taskQueue, Long taskID, ClientAppAnswer clientAppAnswer) throws Exception {
		List<TaskRun> importedTaskRuns = taskRunPybossaService.getTaskRunByIdandProjectId(taskID.intValue(), clientApp.getPlatformAppID().intValue());
		if(importedTaskRuns != null){

			List<TaskRun> pybossaResult = textPybossaFormatter.buildTaskOutputForAIDR(taskQueue.getTaskQueueID(), importedTaskRuns, parser, clientApp, clientAppAnswer);
			ObjectMapper mapper = new ObjectMapper();
			String pybossaResultAsString = mapper.writeValueAsString(pybossaResult);

			int responseCode =  LookupCode.HTTP_OK;

			if(pybossaResult != null && !textPybossaFormatter.getTranslateRequired()){
				responseCode = pybossaCommunicator.sendPost(pybossaResultAsString, AIDR_TASK_ANSWER_URL);
			}

			if(responseCode == LookupCode.HTTP_OK ||responseCode == LookupCode.HTTP_OK_NO_CONTENT || textPybossaFormatter.getTranslateRequired() ){
				TaskQueueResponse taskQueueResponse = textPybossaFormatter.getTaskQueueResponse(clientApp, importedTaskRuns, parser, taskQueue.getTaskQueueID(),
						clientAppAnswer, reportTemplateService);

				if(textPybossaFormatter.getTranslateRequired()){
					textPybossaFormatter.setTranslateRequired(false);
					taskQueueResponse.setTaskInfo(pybossaResultAsString);
				}
				taskQueue.setStatus(LookupCode.TASK_LIFECYCLE_COMPLETED);
				taskQueueService.updateTaskQueue(taskQueue);
				clientAppResponseService.processTaskQueueResponse(taskQueueResponse);
			}

		}
	}

	private void processTranslations(ClientApp clientApp) throws Exception {
		logger.info("ProcessTaskRunImportForText.processTranslations Starts for clientAppID : "+ clientApp.getClientAppID());
		translationService.processTranslations(clientApp);
		logger.info("ProcessTaskRunImportForText.processTranslations Ends for clientAppID : "+ clientApp.getClientAppID());
	}

	/** Import process  ends**/


	/** Publishing process  starts**/

	@Override
	public void processTaskPublish() throws Exception{

		this.processTaskPublishForText();
		this.processTaskPublishForNonText();
	}

	public void processTaskPublishForNonText() throws Exception{
		logger.info("ProcessTaskPublishForNonText : Start : " + new Date());

		setClassVariable();

		if(client == null){
			logger.info("client IS NULL");
			return;
		}

		List<ClientApp> clientAppList = clientAppService.getAllClientAppByClientIDAndStatus(client.getClientID(), StatusCodeType.MICROMAPPER_ONLY);
		for (ClientApp clientApp : clientAppList) {

			logger.info("clientApp processTaskPublish currentClientApp : " + clientApp.getShortName());
			List<ClientAppSource> clientAppSourceList = clientAppSourceService.getClientAppSourceByStatus(clientApp.getClientAppID(), StatusCodeType.EXTERNAL_DATA_SOURCE_ACTIVE);
			logger.info("clientApp processTaskPublish datasources : " + clientAppSourceList.size());

			for (ClientAppSource clientAppSource : clientAppSourceList) {
				List<MicromapperInput> micromapperInputList = null;
				String url = clientAppSource.getSourceURL();

				if(!cvsRemoteFileFormatter.doesSourcerExist(url)){
					continue;
				}

				if(clientApp.getAppType() == StatusCodeType.APP_MAP){
					micromapperInputList = cvsRemoteFileFormatter.getGeoClickerInputData(url);
				}
				else{
					if(clientApp.getAppType() == StatusCodeType.APP_AERIAL){
						micromapperInputList = cvsRemoteFileFormatter.getAerialClickerInputData(url);
					}
					else if(clientApp.getAppType() == StatusCodeType.APP_3W){
						micromapperInputList = cvsRemoteFileFormatter.get3WClickerInputData(url);
					}
					else{
						micromapperInputList = cvsRemoteFileFormatter.getClickerInputData(url);
					}
				}

				if(micromapperInputList != null){
					if(micromapperInputList.size() > 0) {
						this.processNonTextPushing(clientApp, micromapperInputList , clientAppSource.getClientAppSourceID());
					}
					clientAppSourceService.updateClientAppSourceStatus(clientAppSource.getClientAppSourceID(), StatusCodeType.EXTERNAL_DATA_SOURCE_USED);
				}
			}

			this.searchUpdateNextAvailableAppSource(clientApp.getClientAppID());
		}
		logger.info("ProcessTaskPublishForNonText : End : " + new Date());
	}

	private void processNonTextPushing(ClientApp currentClientApp, List<MicromapperInput> micromapperInputList, Long clientAppSourceID) throws Exception{
		try {
			List<Task> aidr_data = pybossaFormatter.assemblePybossaTaskPublishForm(micromapperInputList, currentClientApp);

			List<Task> tasksPublished = null;
			try{
				tasksPublished = taskPybossaService.persist(aidr_data);
			}catch(Exception e){
				logger.error("Exception while persisting tasks to pybossa db", e);
			}

			if(tasksPublished != null && !CollectionUtils.isEmpty(tasksPublished)){
				generateToTaskQueueForNonText(tasksPublished, currentClientApp.getClientAppID(), StatusCodeType.TASK_PUBLISHED, clientAppSourceID) ;
			}else{
				generateToTaskQueueForNonText(aidr_data, currentClientApp.getClientAppID(), StatusCodeType.Task_NOT_PUBLISHED, clientAppSourceID) ;
			}

			// data is consumed. need to mark as completed not to process anymore.
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void processTaskPublishForText() throws Exception{
		logger.info("ProcessTaskPublishForText : Start : " + new Date());

		List<ClientApp> clientAppList = clientAppService.findClientAppByStatus(LookupCode.AIDR_ONLY)  ;

		if(clientAppList != null) {
			for (ClientApp clientApp : clientAppList) {

				this.setClassVariablePerClient(clientApp.getClient());
				int pushTaskNumber = calculateMinNumber(clientApp);

				if( pushTaskNumber > 0 ){

					String api = AIDR_API_URL + clientApp.getCrisisID() + "/" + pushTaskNumber;

					logger.info("Send request :: " + api);
					String inputData = pybossaCommunicator.sendGet(api);

					if(DataFormatValidator.isValidateJson(inputData)){
						try {
							this.processTextPushing(clientApp, inputData, pushTaskNumber)  ;

						} catch (Exception e) {
							logger.warn("Error in publishing data", e);
						}
					}
				}
			}
		}
		logger.info("ProcessTaskPublishForText : End : " + new Date());
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
					List<Task> aidrData = textPybossaFormatter.assemblePybossaTaskPublishFormWithIndex(
							inputData, currentClientApp, itemIndexStart, itemIndexEnd);

					List<Task> tasksPublished = null;
					try{
						tasksPublished = taskPybossaService.persist(aidrData);
					}catch(Exception e){
						logger.error("Exception while persisting tasks to pybossa db", e);

					}

					if(tasksPublished != null && !CollectionUtils.isEmpty(tasksPublished)){
						addTextPublishingToTaskQueue(tasksPublished, currentClientApp.getClientAppID(), LookupCode.TASK_PUBLISHED) ;
					}else{
						addTextPublishingToTaskQueue(aidrData, currentClientApp.getClientAppID(), LookupCode.Task_NOT_PUBLISHED) ;
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
			logger.error("Excpetion in processTextPushing",  e);
		}
	}

	private void addTextPublishingToTaskQueue(List<Task> tasks, Long clientAppID, Integer status){

		List<TaskQueue> taskQueues = new ArrayList<TaskQueue>();
		logger.info("addToTaskQueue : " + tasks);

		for (Task task : tasks) {
			org.json.JSONObject info = task.getInfo();
			Long documentID = (Long)info.get("documentID");

			if(status.equals(LookupCode.Task_NOT_PUBLISHED)){
				pybossaCommunicator.sendGet(AIDR_ASSIGNED_TASK_CLEAN_UP_URL+ documentID) ;
			}
			else{
				TaskQueue taskQueue = new TaskQueue(task.getId().longValue(), clientAppID, documentID, status);
				taskQueues.add(taskQueue);
			}
		}

		if(!CollectionUtils.isEmpty(taskQueues)){
			for (TaskQueue taskQueue : taskQueues) {
				taskQueueService.createTaskQueue(taskQueue);
			}
		}
	}

	private void generateToTaskQueueForNonText(List<Task> tasks, Long clientAppID, Integer status, Long clientAppSourceID){
		logger.info("GenerateToTaskQueueForNonText : " + tasks);

		List<TaskQueue> taskQueues = new ArrayList<TaskQueue>();

		for (Task task : tasks) {
			org.json.JSONObject info = task.getInfo();
			Long documentID = (Long)info.get("documentID");

			TaskQueue taskQueue = new TaskQueue(task.getId().longValue(), clientAppID, documentID, status);
			taskQueue.setClientAppSourceID(clientAppSourceID);
			taskQueues.add(taskQueue);
		}

		if(!CollectionUtils.isEmpty(taskQueues)){
			for (TaskQueue taskQueue : taskQueues) {
				taskQueueService.createTaskQueue(taskQueue);
			}
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
		logger.info("PybossaMicromapperWorker.processTaskExport Starts : "+new Date());
		reportProductService.generateCSVReportForImageGeoClicker();
		reportProductService.generateCSVReportForTextGeoClicker();
		reportProductService.generateMapBoxTemplateForAerialClicker();
		logger.info("PybossaMicromapperWorker.processTaskExport Ends"  + new Date());
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
			logger.error("Exception while parsing json for getMarkerStyleForRubyClicker");
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
			logger.error("Exception in getMarkerStyleForClientApp for clientAppID: "+clientApp.getClientAppID(), e);
		}

		return selectedStyle;
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
			ClientAppAnswer clientAppAnswer = clientAppResponseService.getClientAppAnswer(clientApp.getClientAppID());

			for(int i=0; i < queueSize; i++){
				TaskQueue taskQueue = taskQueues.get(i);
				Long taskID =  taskQueue.getTaskID();
				logger.info("taskID :" + taskID);
				try {
					List<Crisis> crises = crisisDao.getClientAppCrisisDetail(clientApp.getClientAppID());
					Crisis  crisis = null;
					if(!CollectionUtils.isEmpty(crises)){
						crisis = crises.get(0);
					}
					boolean isFound = true;
					if(isFound){
						processQueueImportForNonText(clientApp, crisis, taskQueue, clientAppAnswer, geoJsonOutputModels) ;
					}

				} catch (Exception e) {
					logger.error("Exception in processTaskImportOnDemand for "+shortName, e);
				}
			}
		}
	}
}

