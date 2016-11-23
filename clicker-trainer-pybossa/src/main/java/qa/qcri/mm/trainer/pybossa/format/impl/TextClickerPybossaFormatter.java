package qa.qcri.mm.trainer.pybossa.format.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppAnswer;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueueResponse;
import qa.qcri.mm.trainer.pybossa.entity.TaskTranslation;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;
import qa.qcri.mm.trainer.pybossa.service.ReportTemplateService;
import qa.qcri.mm.trainer.pybossa.service.TranslationService;
import qa.qcri.mm.trainer.pybossa.store.LookupCode;
import qa.qcri.mm.trainer.pybossa.store.PybossaConf;
import qa.qcri.mm.trainer.pybossa.util.DataFormatValidator;
import qa.qcri.mm.trainer.pybossa.util.JsonSorter;
import qa.qcri.mm.trainer.pybossa.util.StreamConverter;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/17/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextClickerPybossaFormatter {

    private boolean translateRequired = false;

    @Autowired
    TranslationService translationService;

    public TextClickerPybossaFormatter(){}

    public boolean getTranslateRequired() {
        return translateRequired;
    }

    public void setTranslateRequired(boolean translateRequired) {
        this.translateRequired = translateRequired;
    }

    public Project assmeblePybossaAppCreationForm(String name, String shortName, String description) throws Exception{

        Project project = new Project();
    	project .setName(name);
    	project.setShortName(shortName);
    	project.setDescription(description);
    	project.setAllow_anonymous_contributors(Boolean.TRUE);
    	project.setLongTasks(0);
    	project.setHidden(0);
    	project.setFeatured(Boolean.FALSE);
    	project.setContacted(Boolean.FALSE);
    	project.setUser(1);
    	project.setTimeEstimate(0);
    	project.setTimeLimit(0);
    	project.setCalibrationFrac(0D);
    	project.setBoltCourseId(0);
    	project.setInfo(new org.json.JSONObject());
    	project.setCategory(45);
    	return project;
    }

    public Long getAppID(String jsonApp, JSONParser parser) throws Exception{
        Long appID = null;
        JSONArray array = (JSONArray) parser.parse(jsonApp);
        Iterator itr= array.iterator();

        while(itr.hasNext()){
            JSONObject featureJsonObj = (JSONObject)itr.next();
            appID = (Long)featureJsonObj.get("id");
        }

        return appID;
    }

    public List<TaskRun> buildTaskOutputForAIDR(Long taskQueueId, List<TaskRun> taskRuns, JSONParser parser, ClientApp clientApp, ClientAppAnswer clientAppAnswer) throws Exception{

    	List<TaskRun> outTaskRuns = new ArrayList<TaskRun>();
    	
    	if(taskRuns.size() > 0){
    		TaskRun oneFeatureJsonObj = taskRuns.get(0);
    		String finalAnswer = this.getAnswerResponse(clientApp, taskRuns, parser, clientAppAnswer, taskQueueId);

    		if(finalAnswer != null) {
    			
    			org.json.JSONObject infoJson =  this.buildInfoJson(oneFeatureJsonObj.getInfo(), finalAnswer, clientApp );
    			oneFeatureJsonObj.setInfo(infoJson);
    			outTaskRuns.add(oneFeatureJsonObj);
    			
    			return outTaskRuns;
    		}
    	}
    	return null  ;
    }


    private org.json.JSONObject buildInfoJson(org.json.JSONObject infoJson,  String finalAnswer, ClientApp clientApp){

    	org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("documentID", infoJson.get("documentID"));
        obj.put("category", finalAnswer);
        obj.put("aidrID", infoJson.get("aidrID"));
        obj.put("crisisID", clientApp.getCrisisID());
        obj.put("attributeID", clientApp.getNominalAttributeID());

        return obj;
    }

    public String getAnswerResponse(ClientApp clientApp, List<TaskRun> pybossaResult, JSONParser parser, ClientAppAnswer clientAppAnswer, Long taskQueueID) throws Exception{

    	Map<String, Integer> questionMapWithResponseCount = getQuestion( clientAppAnswer,  parser);

    	org.json.JSONObject info = null;
    	String answer = null;
    	
    	for (TaskRun taskRun : pybossaResult) {

    		info = taskRun.getInfo();
    		answer = this.getUserAnswer(info);

    		if(answer != null){
    			if(questionMapWithResponseCount.containsKey(answer)){
    				questionMapWithResponseCount.put(answer, questionMapWithResponseCount.get(answer) + 1);
    			}
    		}
    	}

    	String finalAnswer = null;
    	int cutoffSize = this.getCutOffNumber(pybossaResult.size(), clientApp.getTaskRunsPerTask(), clientAppAnswer)  ;
    	
    	for (String question : questionMapWithResponseCount.keySet()) {
    		if(questionMapWithResponseCount.get(question) >= cutoffSize){
    			finalAnswer =  question;
    			if(finalAnswer.equalsIgnoreCase(LookupCode.ANSWER_NOT_ENGLISH)){
    				handleTranslationItem(taskQueueID, answer, info, clientAppAnswer, cutoffSize);
    			}
    		}
    	}

    	return  finalAnswer;
    }

    private void handleTranslationItem(Long taskQueueID,String answer, org.json.JSONObject info, ClientAppAnswer clientAppAnswer, int cutOffSize){

        try{
            String tweetID = String.valueOf(info.get("tweetid"));
            String tweet = (String)info.get("tweet");
            if(tweet == null){
                tweet = (String)info.get("text");
            }
            String author= (String)info.get("author");
            String lat= (String)info.get("lat");
            String lng= (String)info.get("lon");
            String url= (String)info.get("url");
            String created = (String)info.get("timestamp");

            Long taskID;
            if(info.get("taskid") == null){
                taskID =  taskQueueID;
            }
            else{
                taskID = (Long)info.get("taskid");
            }


            if(taskQueueID!=null && taskID!=null && tweetID!=null && (tweet!=null && !tweet.isEmpty())) {
                System.out.println("handleTranslationItem :" + taskQueueID);
                this.setTranslateRequired(true);
                createTaskTranslation(taskID, tweetID, tweet, author, lat, lng, url, taskQueueID, created, clientAppAnswer);

            }
        }
        catch(Exception e){
            System.out.println("handleTranslationItem- exception :" + e.getMessage());
            this.setTranslateRequired(false);
        }

    }

    private void createTaskTranslation(Long taskID, String tweetID, String tweet, String author, String lat, String lon, String url, Long taskQueueID, String created, ClientAppAnswer clientAppAnswer){

        TaskTranslation extTrans = translationService.findByTaskId(taskID);
        if (extTrans != null ) {
            return;
        }

        System.out.println("createTaskTranslation is called : " + taskQueueID);

        TaskTranslation translation = new TaskTranslation(taskID, clientAppAnswer.getClientAppID(), tweetID, author, lat, lon, url, taskQueueID, tweet, TaskTranslation.STATUS_NEW);
        translationService.createTranslation(translation);


    }

    public TaskQueueResponse getTaskQueueResponse(ClientApp clientApp, List<TaskRun> pybossaResult, JSONParser parser, Long taskQueueID, ClientAppAnswer clientAppAnswer, ReportTemplateService rtpService) throws Exception{
    	
    	if(clientAppAnswer == null){
    		return null;
    	}

    	JSONObject responseJSON = new JSONObject();

    	Map<String, Integer> correctAnswers = getQuestion(clientAppAnswer, parser);
    	Map<String, Integer> activeAnswers = this.getActiveAnswerKey( clientAppAnswer,  parser);

    	//JSONArray array = (JSONArray) parser.parse(pybossaResult) ;
    	int cutOffSize =  getCutOffNumber(pybossaResult.size(),  clientApp.getTaskRunsPerTask(), clientAppAnswer) ;

    	//Iterator itr= array.iterator();
    	String answer = null;
    	for (TaskRun taskRun : pybossaResult) {

    		org.json.JSONObject info = taskRun.getInfo();
    		answer = this.getUserAnswer(info);

    		if(answer!=null && !clientApp.getAppType().equals(LookupCode.APP_MAP) ){
    			if(correctAnswers.containsKey(answer)){
    				Integer responses = correctAnswers.get(answer);
    				responses++;
    				correctAnswers.put(answer, responses);
    				handleItemAboveCutOff(taskQueueID,responses, answer, info, 
    						clientAppAnswer, rtpService, cutOffSize, activeAnswers);
    			}
    		}
    	}

    	String responseJsonString = "";

    	for (String question : correctAnswers.keySet()) {
    		responseJSON.put(question, correctAnswers.get(question));
    	}
    	responseJsonString = responseJSON.toJSONString();

    	TaskQueueResponse taskQueueResponse = new TaskQueueResponse(taskQueueID, responseJsonString, "");
    	return  taskQueueResponse;
    }

    private boolean handleItemAboveCutOff(Long taskQueueID,int responseCount, String answer, org.json.JSONObject info, ClientAppAnswer clientAppAnswer, ReportTemplateService reportTemplateService, int cutOffSize, Map<String, Integer> activeAnswers){
        // MAKE SURE TO MODIFY TEMPLATE HTML  Standize OUTPUT FORMAT
        boolean processed = false;
        try{

            String tweetID ;
            String tweet;
            String author= "";
            String lat= "";
            String lng= "";
            String url= "";
            String created = "";
            Long taskID = taskQueueID;

            if(responseCount >= cutOffSize){

                Long tid = (Long)info.get("tweetid");
                tweetID = tid + "";
                if(info.get("tweet") == null){

                    tweet = (String)info.get("text");
                    author= "";
                    lat= "";
                    lng= "";
                    url= "";
                    created = "";
                }
                else{

                    tweet = (String)info.get("tweet");

                    if(info.get("author") != null){
                        author= (String)info.get("author");
                    }

                    if(info.get("lat") != null){
                        lat= (String)info.get("lat");
                    }

                    if(info.get("lon") != null){
                        lng= (String)info.get("lon");
                    }

                    if(info.get("url") != null){
                        url= (String)info.get("url");
                    }

                    created = (String)info.get("timestamp");
                    taskID = (Long)info.get("taskid");
                }

                
                if(activeAnswers.containsKey(answer)){
                    if(taskQueueID!=null && taskID!=null && tweetID!=null && (tweet!=null && !tweet.isEmpty())){
                        ReportTemplate template = new ReportTemplate(taskQueueID,taskID,tweetID,tweet,author,lat,lng,url,created, answer, LookupCode.TEMPLATE_IS_READY_FOR_EXPORT, clientAppAnswer.getClientAppID());
                        reportTemplateService.saveReportItem(template);
                        processed = true;
                    }
                }

            }
        }
        catch(Exception e){
            System.out.println("handleItemAboveCutOff exception");
            System.out.println("exception e :" + e.toString());
        }
        return processed;
    }

    private String getUserAnswer(org.json.JSONObject info){
        String answer = null;
        if(info.get("category")!=null) {
            answer = (String)info.get("category");
        }

        return answer;
    }

    private Map<String, Integer> getQuestion(ClientAppAnswer clientAppAnswer, JSONParser parser) throws ParseException {
        JSONArray questionArrary =   (JSONArray) parser.parse(clientAppAnswer.getAnswer()) ;
        Map<String, Integer> questionMapWithResponseCount = new HashMap<String, Integer>(questionArrary.size());

        for(int i=0; i< questionArrary.size(); i++){
        	
            JSONObject obj = (JSONObject)questionArrary.get(i);
            questionMapWithResponseCount.put((String)obj.get("qa"), 0);
        }
        return questionMapWithResponseCount;
    }

    private Map<String, Integer> getActiveAnswerKey(ClientAppAnswer clientAppAnswer, JSONParser parser) throws ParseException {

        String answerKey =   clientAppAnswer.getActiveAnswerKey();
        System.out.println("getActiveAnswerKey : " + answerKey);
        if(answerKey== null){
            answerKey =   clientAppAnswer.getAnswer();
        }

        JSONArray questionArrary =   (JSONArray) parser.parse(answerKey) ;
        Map<String, Integer> questionMapWithResponseCount = new HashMap<String, Integer>(questionArrary.size());

        for(int i=0; i< questionArrary.size(); i++){
        	
            JSONObject obj = (JSONObject)questionArrary.get(i);
            questionMapWithResponseCount.put((String)obj.get("qa"), 0);
            // questions[i] =   (String)obj.get("qa");
        }
        return questionMapWithResponseCount;
    }

    public int getCutOffNumber(int responseSize, int maxResponseSize, ClientAppAnswer clientAppAnswer){

        int cutOffSize =    clientAppAnswer.getVoteCutOff();

        if(responseSize > maxResponseSize){
            cutOffSize = (int)Math.round(maxResponseSize * 0.5);
        }

        return cutOffSize;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    /*public List<String> assemblePybossaTaskPublishForm(String inputData, ClientApp clientApp) throws Exception {

        List<String> outputFormatData = new ArrayList<String>();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inputData);

        JSONArray jsonObject = (JSONArray) obj;
        Iterator itr= jsonObject.iterator();

        while(itr.hasNext()){
            JSONObject featureJsonObj = (JSONObject)itr.next();
            JSONObject info = assemblePybossaInfoFormat(featureJsonObj, parser, clientApp) ;

            JSONObject tasks = new JSONObject();

            tasks.put("info", info);
            tasks.put("n_answers", clientApp.getTaskRunsPerTask());
            tasks.put("quorum", clientApp.getQuorum());
            tasks.put("calibration", new Integer(0));
            tasks.put("project_id", clientApp.getPlatformAppID());
            tasks.put("priority_0", new Integer(0));

            outputFormatData.add(tasks.toJSONString());

        }

        return outputFormatData;
    }*/

    public List<Task> assemblePybossaTaskPublishFormWithIndex(String inputData, ClientApp clientApp, int indexStart, int indexEnd) throws Exception {

        List<Task> outputFormatData = new ArrayList<Task>();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inputData);

        JSONArray jsonObject = (JSONArray) obj;

        for(int i = indexStart; i < indexEnd; i++){
            JSONObject featureJsonObj = (JSONObject)jsonObject.get(i);
            org.json.JSONObject info = assemblePybossaInfoFormat(featureJsonObj, parser, clientApp) ;

            Task task = new Task();
            task.setInfo(info);
            task.setnAnswers(clientApp.getTaskRunsPerTask());
            task.setQuorum(clientApp.getQuorum());
            task.setCalibration( new Integer(0));
            Project project = new Project();
            project.setId(clientApp.getPlatformAppID().intValue());
            task.setProject(project);
            task.setState(PybossaConf.TASK_STATUS_ONGOING);
            task.setPriority0(new Double(0));
            outputFormatData.add(task);
            
        }

        return outputFormatData;
    }

    public void publicTaskTranslationTaskPublishForm(String inputData, ClientApp clientApp, int indexStart, int indexEnd) throws Exception{
        try{
            List<TaskTranslation> outputFormatData = new ArrayList<TaskTranslation>();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(inputData);

            JSONArray jsonObject = (JSONArray) obj;

            for(int i = indexStart; i < indexEnd; i++){
                JSONObject featureJsonObj = (JSONObject)jsonObject.get(i);
                JSONObject data = (JSONObject) parser.parse((String) featureJsonObj.get("data"));

                Long documentID =  (Long)featureJsonObj.get("documentID");
                JSONObject usr =  (JSONObject)data.get("user");
                String userName = (String)usr.get("name");
                String tweetTxt = (String)data.get("text");
                String tweetID =  String.valueOf(data.get("id"));

                TaskTranslation task = new TaskTranslation(clientApp.getClientAppID(),documentID, tweetID, userName, tweetTxt, TaskTranslation.STATUS_NEW);
                translationService.createTranslation(task);

            }
        }
        catch(Exception e){
            System.out.println("publicTaskTranslationTaskPublishForm : " + e);
        }
    }

    public String generateClientAppTemplateLabel(JSONArray labelModel){
        JSONArray sortedLabelModel = JsonSorter.sortJsonByKey(labelModel, "norminalLabelCode");
        StringBuffer displayLabel = new StringBuffer();
        Iterator itr= sortedLabelModel.iterator();
        //logger.debug("sortedLabelModel : " + sortedLabelModel);
        while(itr.hasNext()){

            JSONObject featureJsonObj = (JSONObject)itr.next();
            String labelName = (String)featureJsonObj.get("name");
            String lableCode = (String)featureJsonObj.get("norminalLabelCode");
            String description = (String)featureJsonObj.get("description");
            Long norminalLabelID = (Long) featureJsonObj.get("norminalLabelID");

            displayLabel.append("<label class='radio' name='nominalLabel'><strong>")  ;
            displayLabel.append("<input name='nominalLabel' type='radio' value='");
            displayLabel.append(lableCode) ;
            displayLabel.append("'>") ;
            displayLabel.append(labelName) ;
            displayLabel.append("</strong>")  ;
            if(!description.isEmpty()){
                displayLabel.append("&nbsp;&nbsp;")  ;
                displayLabel.append("<font color='#999999' size=-1>")  ;
                displayLabel.append(description) ;
                displayLabel.append("</font>")  ;
            }
            displayLabel.append("</label>")  ;
        }

        return displayLabel.toString();

    }

    public Project updateApp(Project remoteProject, ClientApp clientApp,JSONObject attribute, JSONArray labelModel, Long categoryID) throws Exception {
       
    	InputStream templateIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("html/template.html");
        String templateString = StreamConverter.convertStreamToString(templateIS) ;

        templateString = templateString.replace("TEMPLATE:SHORTNAME", clientApp.getShortName());

        String attributeDisplay = (String)attribute.get("name") ;

        attributeDisplay =  attributeDisplay +" " + (String)attribute.get("description") ;
        templateString = templateString.replace("TEMPLATE:FORATTRIBUTEAIDR", attributeDisplay);

        templateString = templateString.replace("TEMPLATE:FORLABELSFROMAIDR", this.generateClientAppTemplateLabel(labelModel) );

        InputStream tutorialIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("html/tutorial.html");
        String tutorialString = StreamConverter.convertStreamToString(tutorialIS) ;

        tutorialString = tutorialString.replace("TEMPLATE:SHORTNAME", clientApp.getShortName());
        tutorialString = tutorialString.replace("TEMPLATE:NAME", clientApp.getName());

        InputStream longDescIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("html/long_description.html");
        String longDescString = StreamConverter.convertStreamToString(longDescIS) ;

        org.json.JSONObject appInfo = new org.json.JSONObject();

        appInfo.put("task_presenter", templateString);

        appInfo.put("tutorial", tutorialString);
        appInfo.put("thumbnail", "http://i.imgur.com/lgZAWIc.png");

        remoteProject.setInfo(appInfo);
        remoteProject.setLong_description(longDescString);
        remoteProject.setName(clientApp.getName());
        remoteProject.setShortName(clientApp.getShortName());
        remoteProject.setDescription(clientApp.getShortName());
        remoteProject.setAllow_anonymous_contributors(Boolean.TRUE);
        remoteProject.setHidden(0);
        remoteProject.setCategory(categoryID.intValue());
        remoteProject.setFeatured(Boolean.FALSE);

        return  remoteProject;
    }

    private org.json.JSONObject assemblePybossaInfoFormat(JSONObject featureJsonObj, JSONParser parser, ClientApp clientApp) throws Exception{

        JSONObject data = (JSONObject) parser.parse((String) featureJsonObj.get("data"));

        Long documentID =  (Long)featureJsonObj.get("documentID");
        Long crisisID =   clientApp.getCrisisID();
        JSONObject usr =  (JSONObject)data.get("user");
        String userName = (String)usr.get("name");
        Long userID = (Long)usr.get("id");
        String tweetTxt = (String)data.get("text");
        String createdAt = (String)data.get("created_at");
        Long tweetID =  (Long)data.get("id");

        org.json.JSONObject pybossaData = new org.json.JSONObject();
        pybossaData.put("question","please tag it.");
        pybossaData.put("userName",userName);
        pybossaData.put("tweetid",tweetID);
        pybossaData.put("userID",userID.toString());
        pybossaData.put("text",tweetTxt);
        pybossaData.put("createdAt",createdAt);
        pybossaData.put("documentID",documentID);
        pybossaData.put("crisisID",crisisID);
        pybossaData.put("aidrID",clientApp.getClient().getAidrUserID());

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
                //logger.debug("featureJsonObj : " +  featureJsonObj);
                String status = (String)featureJsonObj.get("state") ;
                //logger.debug("status : "  + status);
                if(status.equalsIgnoreCase("completed"))
                {
                    isCompleted = true;
                }
            }

        }
        return isCompleted;
    }

    public void setTranslationService(TranslationService service) {
        translationService = service;
    }

}
