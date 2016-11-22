package qa.qcri.mm.trainer.pybossa.format.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.StringUtils;

import qa.qcri.mm.trainer.pybossa.dao.TyphoonRubyTextGeoClickerDao;
import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppAnswer;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppSource;
import qa.qcri.mm.trainer.pybossa.entity.Crisis;
import qa.qcri.mm.trainer.pybossa.entity.MarkerStyle;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueueResponse;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;
import qa.qcri.mm.trainer.pybossa.service.ReportTemplateService;
import qa.qcri.mm.trainer.pybossa.service.impl.PybossaCommunicator;
import qa.qcri.mm.trainer.pybossa.store.PybossaConf;
import qa.qcri.mm.trainer.pybossa.store.StatusCodeType;
import qa.qcri.mm.trainer.pybossa.util.CVSFileDataSourceSearch;
import qa.qcri.mm.trainer.pybossa.util.DataFormatValidator;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/17/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class MicroMapperPybossaFormatter {
	protected static Logger logger = Logger.getLogger(MicroMapperPybossaFormatter.class);

	private final PybossaCommunicator pybossaCommunicator = new PybossaCommunicator();


	public MicroMapperPybossaFormatter(){}

	public List<Task> assemblePybossaTaskPublishForm( List<MicromapperInput> inputSources, ClientApp clientApp) throws Exception {

		List<Task> outputFormatData = new ArrayList<Task>();

		for (MicromapperInput micromapperInput : inputSources) {
			org.json.JSONObject info = assemblePybossaInfoFormat(micromapperInput, clientApp) ;
			if(info != null) {
				Task task = new Task();
				task.setInfo(info);
				task.setCalibration(new Integer(0));
				task.setnAnswers(clientApp.getTaskRunsPerTask());
				task.setQuorum(clientApp.getQuorum());
				Project project = new Project();
				project.setId(clientApp.getPlatformAppID().intValue());
				task.setProject(project);
				task.setState(PybossaConf.TASK_STATUS_ONGOING);
				task.setPriority0(new Double(0));
				outputFormatData.add(task);
			}
		}
		return outputFormatData;
	}

	private org.json.JSONObject assemblePybossaInfoFormat(MicromapperInput micromapperInput, ClientApp clientApp) throws Exception{


		org.json.JSONObject pybossaData = new org.json.JSONObject();
		pybossaData.put("question","please tag it.");

		if(clientApp.getAppType() == StatusCodeType.APP_MAP ){
			pybossaData = createGeoClickerInfo(pybossaData, micromapperInput);
			return  pybossaData;
		}


		if(clientApp.getAppType() == StatusCodeType.APP_AERIAL){
			pybossaData = createAerialClickerInfo(pybossaData, micromapperInput);
			return  pybossaData;
		}

		if(clientApp.getAppType() == StatusCodeType.APP_3W){
			pybossaData = create3WClickerInfo(pybossaData, micromapperInput);
			return  pybossaData;
		}
		// otherwise, process no geoclicker
		pybossaData = createNonGeoClickerInfo(pybossaData, micromapperInput);

		return pybossaData;
	}

	private org.json.JSONObject createNonGeoClickerInfo(org.json.JSONObject pybossaData, MicromapperInput micromapperInput ){
		//logger.warn(micromapperInput);
		if(micromapperInput.getDocumentID() != null && micromapperInput.getAnswer()!=null){
			logger.info("parsing document ID");
			try {
				pybossaData.put("tweet_category",micromapperInput.getAnswer());
				long docID = Long.parseLong(micromapperInput.getDocumentID()) ;
				pybossaData.put("documentID", docID);
			} catch (NumberFormatException e) {
				return null;
			}
		}


		pybossaData.put("author",micromapperInput.getAuthor());
		pybossaData.put("tweetid",micromapperInput.getTweetID());
		pybossaData.put("userID",micromapperInput.getAuthor());
		pybossaData.put("tweet",micromapperInput.getTweet());
		pybossaData.put("timestamp",micromapperInput.getCreated());
		pybossaData.put("lat",micromapperInput.getLat());
		pybossaData.put("lon",micromapperInput.getLng());
		pybossaData.put("url",micromapperInput.getUrl());
		pybossaData.put("imgurl",micromapperInput.getUrl());

		return pybossaData;

	}

	private org.json.JSONObject createGeoClickerInfo(org.json.JSONObject pybossaData, MicromapperInput micromapperInput ){

		pybossaData.put("author",micromapperInput.getAuthor());
		pybossaData.put("tweetid",micromapperInput.getTweetID());
		pybossaData.put("userID",micromapperInput.getAuthor());
		pybossaData.put("tweet",micromapperInput.getTweet());
		pybossaData.put("timestamp",micromapperInput.getCreated());
		pybossaData.put("lat",micromapperInput.getLat());
		pybossaData.put("lon",micromapperInput.getLng());
		pybossaData.put("url",micromapperInput.getUrl());
		pybossaData.put("imgurl",micromapperInput.getUrl());
		pybossaData.put("category",micromapperInput.getAnswer());

		return pybossaData;
	}

	private org.json.JSONObject createAerialClickerInfo(org.json.JSONObject pybossaData, MicromapperInput micromapperInput ){

		pybossaData.put("url",micromapperInput.getUrl());
		pybossaData.put("imgurl",micromapperInput.getUrl());
		pybossaData.put("geo",micromapperInput.getGeo());
		pybossaData.put("mediaSize",micromapperInput.getMediaSize());
		pybossaData.put("category",micromapperInput.getUrl());
		pybossaData.put("mediasource",micromapperInput.getMediasSource());

		return pybossaData;
	}

	private org.json.JSONObject create3WClickerInfo(org.json.JSONObject pybossaData, MicromapperInput micromapperInput ){

		pybossaData.put("glide",micromapperInput.getGlide());
		pybossaData.put("link",micromapperInput.getLink());
		pybossaData.put("where",micromapperInput.getWhere());
		pybossaData.put("who",micromapperInput.getWho());
		pybossaData.put("langcode",micromapperInput.getLang());

		return pybossaData;
	}

	public boolean isTaskStatusCompleted(String data) throws Exception{
		/// will do later for importing process
		boolean isCompleted = false;
		if(DataFormatValidator.isValidateJson(data)){
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(data);
			JSONArray jsonObject = (JSONArray) obj;

			Iterator itr= jsonObject.iterator();

			while(itr.hasNext()){
				JSONObject featureJsonObj = (JSONObject)itr.next();
				//  logger.debug("featureJsonObj : " +  featureJsonObj);
				String status = (String)featureJsonObj.get("state") ;
				//  logger.debug("status : "  + status);
				if(status.equalsIgnoreCase(PybossaConf.TASK_STATUS_COMPLETED))
				{
					isCompleted = true;
				}
			}

		}
		return isCompleted;
	}

	public TaskQueueResponse getAnswerResponseForVideo(ClientApp clientApp, List<TaskRun> pybossaResult, JSONParser parser, Long taskQueueID, ClientAppAnswer clientAppAnswer, ReportTemplateService rtpService) throws Exception{
		JSONObject responseJSON = new JSONObject();
		//        JSONArray array = (JSONArray) parser.parse(pybossaResult) ;
		//        Iterator itr= array.iterator();
		String answer = null;

		org.json.JSONObject infoToSave = null;
		HashMap<Integer, Integer> severeTimeStamp = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> mildTimeStamp = new HashMap<Integer, Integer>();

		for (TaskRun taskRun : pybossaResult) {

			org.json.JSONObject info = taskRun.getInfo();

			if(info.get("damage")!=null) {
				if(info.get("damage").toString().isEmpty()){
					answer = "";
				}
				else{
					String answerInfo = (String)info.get("damage");
					JSONObject answers = (JSONObject)parser.parse(answerInfo);
					String severeAnswer = (String)answers.get("severe");
					String mildAnswer = (String)answers.get("mild");
					if(severeAnswer!=null){
						severeTimeStamp = addToTimeStampList(severeTimeStamp, severeAnswer);
					}
					if(mildAnswer != null){
						mildTimeStamp = addToTimeStampList(mildTimeStamp, mildAnswer);
					}

					for (Map.Entry entry : mildTimeStamp.entrySet()) {
						if((Integer)entry.getValue() >= clientAppAnswer.getVoteCutOff()) {
							infoToSave = info;
						}
					}

					for (Map.Entry entry : severeTimeStamp.entrySet()) {
						if((Integer)entry.getValue() >= clientAppAnswer.getVoteCutOff()) {
							infoToSave = info;
						}
					}
				}
			}
		}

		severeTimeStamp = removeItemBelowCutOffForVideo(severeTimeStamp, clientAppAnswer);
		mildTimeStamp =   removeItemBelowCutOffForVideo(mildTimeStamp, clientAppAnswer);

		if( severeTimeStamp.size() > 0){

			responseJSON.put("severe", StringUtils.collectionToCommaDelimitedString(severeTimeStamp.keySet()));

		}
		else{
			responseJSON.put("severe", "");
		}
		if( mildTimeStamp.size() > 0){
			responseJSON.put("mild", StringUtils.collectionToCommaDelimitedString(mildTimeStamp.keySet()));

		}
		else{
			responseJSON.put("mild", "");
		}

		handleItemAboveCutOffForVideo(taskQueueID, severeTimeStamp, mildTimeStamp, infoToSave, clientAppAnswer, rtpService);

		TaskQueueResponse taskQueueResponse = new TaskQueueResponse(taskQueueID, responseJSON.toJSONString(), "");
		return  taskQueueResponse;
	}

	public TaskQueueResponse getAnswerResponse(ClientApp clientApp, List<TaskRun> pybossaResult, JSONParser parser, Long taskQueueID, ClientAppAnswer clientAppAnswer, ReportTemplateService rtpService) throws Exception{
		if(clientAppAnswer == null){
			return null;
		}

		JSONObject responseJSON = new JSONObject();

		String[] acceptableAnswers = getAcceptableAnswers(clientAppAnswer,  parser);

		if(acceptableAnswers == null) {
			//System.out.println("active answer key is null. No validation is required");
			return null;
		}

		int[] responses = new int[acceptableAnswers.length];

		String userAnswer = null;

		int cutoffSize = getCutOffNumber(pybossaResult.size(), clientApp.getTaskRunsPerTask(), clientAppAnswer)  ;

		for (TaskRun taskRun : pybossaResult) {

			org.json.JSONObject info = taskRun.getInfo();

			//Long taskID = (Long) featureJsonObj.get("id");

			userAnswer = this.getUserAnswer(info, clientApp);

			if(userAnswer!=null ){
				for(int i=0; i < acceptableAnswers.length; i++ ){
					if(acceptableAnswers[i].trim().equalsIgnoreCase(userAnswer.trim())){
						responses[i] = responses[i] + 1;
						handleItemAboveCutOff(taskQueueID,responses[i], userAnswer, info, clientAppAnswer, rtpService, cutoffSize);
					}
				}
			}
		}

		String responseJsonString = "";

		for(int i=0; i < acceptableAnswers.length; i++ ){
			responseJSON.put(acceptableAnswers[i], responses[i]);
		}
		responseJsonString = responseJSON.toJSONString();

		TaskQueueResponse taskQueueResponse = new TaskQueueResponse(taskQueueID, responseJsonString, "");
		return  taskQueueResponse;
	}

	Map<Long, JSONObject> responseArrayMap = new HashMap<>();

	public String getCategoryFromJson(Long taskid) throws ParseException{
		if (responseArrayMap.isEmpty()) {
			JSONParser parser = new JSONParser();
			String jsonData = pybossaCommunicator
					.sendGet("http://maps.micromappers.org/data/esri/nepal/nepalText1.json");
			jsonData = jsonData.substring(6, jsonData.length() - 2);
			JSONArray responseArray = (JSONArray) parser.parse(jsonData);
			for (int j = 0; j < responseArray.size(); j++) {
				try {
					JSONObject object = (JSONObject) responseArray.get(j);
					String infoStringObject = (String) object.get("info");
					JSONObject infoObject = (JSONObject) parser.parse(infoStringObject);
					Long taskId = (Long) infoObject.get("taskid");
					responseArrayMap.put(taskId, object);
					logger.info(j);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}   	
		JSONObject jsonObject = responseArrayMap.get(taskid);
		if(jsonObject == null){
			return null;
		}
		return (String) jsonObject.get("answer");
	}

	public TaskQueueResponse getAnswerResponseForGeo(List<TaskRun> pybossaResult, JSONParser parser, Long taskQueueID, ClientApp clientApp,Crisis crisis, 
			MarkerStyle markerStyle, ClientAppSource appSource, ReportTemplateService reportTemplateService, TyphoonRubyTextGeoClickerDao typhoonRubyTextGeoClickerDao) throws Exception{
		// boolean noLocationFound  = isContainNoLocationInfo(pybossaResult,  parser);
		JSONArray locations  =  new JSONArray();
		String uniqueIDString = null;

		for (TaskRun taskRun : pybossaResult) {

			org.json.JSONObject info = taskRun.getInfo();

			String locValue = info.get("loc") == null? null : info.get("loc").toString();
			if(locValue != null && !locValue.equalsIgnoreCase(PybossaConf.TASK_QUEUE_GEO_INFO_NOT_FOUND) && crisis != null){
				if(DataFormatValidator.isValidateJson(locValue)) {
					JSONObject loc = (JSONObject)info.get("loc");
					String locType = (String)loc.get("type");

					if(locType.equalsIgnoreCase(PybossaConf.GEOJSON_TYPE_FEATURE_COLLECTION)){
						JSONArray features = (JSONArray)loc.get("features");

						for(int i= 0; i < features.size(); i++  ){
							JSONObject aFeature = (JSONObject)features.get(i);
							JSONObject properties = (JSONObject)aFeature.get("properties");

							if(info.get("category") == null && appSource != null){
								String[] row =   CVSFileDataSourceSearch.search((String)info.get("tweetid"),appSource.getSourceURL() );
								if(row != null){
									info.put("author", row[2] );
									info.put("timestamp", row[6] );
									info.put("category", row[7] );
								}
							}

							properties.put("author", info.get("author") )   ;
							properties.put("category", info.get("category") ) ;
							properties.put("timestamp", info.get("timestamp") ) ;
							properties.put("tweet", info.get("tweet") )   ;
							properties.put("url", info.get("url") )   ;
							properties.put("tweetid", info.get("tweetid") )   ;
							properties.put("taskid", info.get("taskid") )   ;
							properties.put("crisis_name", crisis.getDisplayName() )   ;
							properties.put("crisis_type", crisis.getClickerType() )   ;

							String category = info.get("category") != null ? info.get("category").toString():"";
							JSONObject mStyle = getMarkerStyleForClientApp(markerStyle,parser, category);
							if(mStyle != null){
								properties.put("style", mStyle );
								locations.add(aFeature) ;
							}                            
							uniqueIDString  = String.valueOf(info.get("tweetid"));
						}

					}
					else{ // Image GeoClicker process

						logger.info("image geo clicker process ::: " + info);
						JSONObject geoLoc = (JSONObject)info.get("loc");
						JSONObject properties = (JSONObject)geoLoc.get("properties");
						properties.put("tweet", info.get("tweet") )   ;
						properties.put("author", info.get("author") )   ;
						properties.put("url", info.get("url") )   ;
						properties.put("timestamp", info.get("timestamp") )   ;
						properties.put("tweetid", info.get("tweetid") )   ;
						properties.put("taskid", info.get("taskid") )   ;
						properties.put("crisis_name", crisis.getDisplayName() )   ;
						properties.put("crisis_type", crisis.getClickerType() )   ;

						uniqueIDString = String.valueOf(info.get("url") );

						String category = null;
						List<ReportTemplate> reportTemplatesByUrl = reportTemplateService
								.getReportTemplatesByUrl(uniqueIDString);
						if (reportTemplatesByUrl != null && !reportTemplatesByUrl.isEmpty()) {
							for (ReportTemplate reportTemplate : reportTemplatesByUrl) {
								String answer = reportTemplate.getAnswer();
								if (answer != null && !answer.equalsIgnoreCase("none")) {
									if (category == null || (category != null && answer.equalsIgnoreCase("severe"))) {
										category = answer;
									}
								}
							}
						}
						if(category != null){
							category = category.trim().toLowerCase();
							info.put("category", category);
						} else {
							category = "mild";
						}
						properties.put("category", category) ;
						JSONObject mStyle = getMarkerStyleForClientApp(markerStyle, parser, category);

						if(mStyle != null){
							properties.put("style", mStyle );
							locations.add(geoLoc);
						}
					}
				}
			}
		}

		logger.info("locations ::: " + locations);

		TaskQueueResponse taskQueueResponse = null;
		if(!locations.isEmpty()){
			taskQueueResponse = new TaskQueueResponse(taskQueueID, locations.toJSONString(), uniqueIDString);
			logger.info("TaskQueueResponse : " + taskQueueResponse) ;
		}
		return  taskQueueResponse;
	}

	public TaskQueueResponse getAnswerResponseForAerial(List<TaskRun> pybossaResult, JSONParser parser, Long taskQueueID, ClientApp clientApp) throws Exception{
		JSONArray locations  =  new JSONArray();
		for (TaskRun taskRun : pybossaResult) {

			org.json.JSONObject info = taskRun.getInfo();
			String locValue = info.get("loc").toString();
			if( !locValue.isEmpty() && !locValue.equalsIgnoreCase(PybossaConf.TASK_QUEUE_GEO_INFO_NOT_FOUND)){
				locations.add(info.get("loc"))   ;
			}
		}
		TaskQueueResponse taskQueueResponse = new TaskQueueResponse(taskQueueID, locations.toJSONString(), null);

		return  taskQueueResponse;
	}

	private HashMap<Integer, Integer> addToTimeStampList(HashMap<Integer, Integer> timeStampList, String answerValue){

		String[] answerAry = answerValue.split(",");
		for(int i=0; i< answerAry.length; i++){
			if(!answerAry[i].toString().isEmpty()){
				Integer sec = Double.valueOf(answerAry[i]).intValue();
				if(!timeStampList.containsKey(sec)) {
					if(sec > 0){
						Integer secAddOne = sec + 1;
						if(!timeStampList.containsKey(secAddOne)){
							if(sec > 1){
								Integer secSubtractOne = sec - 1;
								if(!timeStampList.containsKey(secSubtractOne)){
									timeStampList.put(sec, 1);
								}
								else{
									Integer value = timeStampList.get(secSubtractOne) ;
									timeStampList.put(secSubtractOne, value + 1);
								}
							}
						}
						else{
							Integer value = timeStampList.get(secAddOne) ;
							timeStampList.put(secAddOne, value + 1);
						}
					}
				}
				else{
					Integer value = timeStampList.get(sec) ;
					timeStampList.put(sec, value + 1);
				}
			}
		}
		return timeStampList;
	}

	private String getUserAnswer(org.json.JSONObject info, ClientApp clientApp){

		String answer = null;
		// tweet
		if(info.get("category")!=null) {
			answer = (String)info.get("category");
		}
		// image , video
		if(info.get("damage")!=null && clientApp.getAppType() == StatusCodeType.APP_IMAGE) {
			answer = (String)info.get("damage");
		}

		// geo
		if(info.get("loc")!=null) {
			answer = (String)info.get("loc");
		}

		return answer;
	}

	private String[] getAcceptableAnswers(ClientAppAnswer clientAppAnswer, JSONParser parser) throws ParseException {
		String answerKey =  clientAppAnswer.getAnswer();
		if(clientAppAnswer.getActiveAnswerKey() != null){
			answerKey =  clientAppAnswer.getActiveAnswerKey();
		}

		JSONArray questionArrary =   (JSONArray) parser.parse(answerKey) ;

		int questionSize =  questionArrary.size();
		String[] questions = new String[questionSize];

		for(int i=0; i< questionSize; i++){
			JSONObject obj = (JSONObject)questionArrary.get(i);
			questions[i] =   (String)obj.get("qa");
		}

		return questions;
	}

	private void handleItemAboveCutOff(Long taskQueueID,int responseCount, String answer, org.json.JSONObject info, ClientAppAnswer clientAppAnswer, ReportTemplateService reportTemplateService, int cutOffSize){
		// MAKE SURE TO MODIFY TEMPLATE HTML  Standize OUTPUT FORMAT
		if(responseCount >= cutOffSize){
			String tweetID = (String)info.get("tweetid");
			String tweet = (String)info.get("tweet");
			String author= (String)info.get("author");
			String lat= (String)info.get("lat");
			String lng= (String)info.get("lon");
			String url= (String)info.get("url");
			String created = (String)info.get("timestamp");
			Long taskID = (Long)info.get("taskid");

			if(taskQueueID!=null && taskID!=null && tweetID!=null && (tweet!=null && !tweet.isEmpty())){
				ReportTemplate template = new ReportTemplate(taskQueueID,taskID,tweetID,tweet,author,lat,lng,url,created, answer, StatusCodeType.TEMPLATE_IS_READY_FOR_EXPORT, clientAppAnswer.getClientAppID());
				reportTemplateService.saveReportItem(template);
			}
			// save to output
		}
	}

	private void handleItemAboveCutOffForVideo(Long taskQueueID, HashMap<Integer, Integer> severeTimeStamp, HashMap<Integer, Integer> mildTimeStamp, org.json.JSONObject info, ClientAppAnswer clientAppAnswer, ReportTemplateService reportTemplateService){

		if(info == null){
			return;
		}
		String responseKeyWord = null;
		String tweetID = (String)info.get("tweetid");
		String tweet = (String)info.get("tweet");
		String author= (String)info.get("author");
		String lat= (String)info.get("lat");
		String lng= (String)info.get("lon");
		String url= (String)info.get("url");
		String created = (String)info.get("timestamp");
		Long taskID = (Long)info.get("taskid");

		List<Integer> unsortAnswerList = new ArrayList<Integer>();

		if(!severeTimeStamp.isEmpty() )  {
			for (Map.Entry entry : severeTimeStamp.entrySet()) {
				Integer value = (Integer)entry.getKey();
				if (!unsortAnswerList.contains(value))
					unsortAnswerList.add(value);
				responseKeyWord = PybossaConf.VIDEO_CLICKER_RESPONSE_SEVERE;
			}
		}

		if(!mildTimeStamp.isEmpty() )  {
			for (Map.Entry entry : mildTimeStamp.entrySet()) {
				Integer value = (Integer)entry.getKey();
				if (!unsortAnswerList.contains(value))
					unsortAnswerList.add(value);
				responseKeyWord = responseKeyWord + ":" +PybossaConf.VIDEO_CLICKER_RESPONSE_MILD;
			}
		}

		if(unsortAnswerList.size() > 1){
			Collections.sort(unsortAnswerList);
			String timeStampResponseValue = StringUtils.collectionToDelimitedString(unsortAnswerList,"-");
			timeStampResponseValue = timeStampResponseValue + "-" + responseKeyWord ;
			ReportTemplate template = new ReportTemplate(taskQueueID,taskID,tweetID,tweet,author,lat,lng,url,created, timeStampResponseValue , StatusCodeType.TEMPLATE_IS_READY_FOR_EXPORT, clientAppAnswer.getClientAppID());
			reportTemplateService.saveReportItem(template);
		}
	}

	private HashMap<Integer, Integer> removeItemBelowCutOffForVideo(HashMap<Integer, Integer> timeStampList, ClientAppAnswer clientAppAnswer ){

		for(Iterator<Map.Entry<Integer,Integer>> it=timeStampList.entrySet().iterator();it.hasNext();){
			Map.Entry<Integer, Integer> entry = it.next();
			if (entry.getValue() < clientAppAnswer.getVoteCutOff()) {
				it.remove();
			}
		}

		return timeStampList;
	}

	private boolean isContainNoLocationInfo(List<TaskRun> pybossaResult, JSONParser parser) throws ParseException {
		boolean found = false;
		for (TaskRun taskRun : pybossaResult) {
			org.json.JSONObject info = taskRun.getInfo();
			if(info.get("loc") != null){
				String locValue = info.get("loc").toString();
				if(locValue.equalsIgnoreCase(PybossaConf.TASK_QUEUE_GEO_INFO_NOT_FOUND)){
					found = true;
				}
			}
		}
		return  found;
	}

	private int getCutOffNumber(int responseSize, int maxResponseSize, ClientAppAnswer clientAppAnswer){

		int cutOffSize =    clientAppAnswer.getVoteCutOff();

		if(responseSize > maxResponseSize){
			cutOffSize = (int)Math.round(maxResponseSize * 0.5);
		}

		return cutOffSize;
	}

	private JSONObject getMarkerStyleForClientApp(MarkerStyle markerStyle, JSONParser parser, String answer){

		JSONObject selectedStyle = null;
		try {
			if(markerStyle != null){
				JSONObject mJson = (JSONObject)parser.parse(markerStyle.getStyle());
				JSONArray mStyles = (JSONArray)mJson.get("style");
				for(Object a : mStyles) {
					JSONObject aStyle = (JSONObject)a;

					if(aStyle.get("label_code").toString().equalsIgnoreCase(answer)){
						selectedStyle = aStyle;
					}
				}
			}
		} catch (ParseException e) {
			logger.warn("Parse Exception : " + e.getMessage());
		}

		return selectedStyle;
	}


}
