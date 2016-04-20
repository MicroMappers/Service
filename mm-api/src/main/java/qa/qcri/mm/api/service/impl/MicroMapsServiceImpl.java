package qa.qcri.mm.api.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.CrisisDao;
import qa.qcri.mm.api.dao.MarkerStyleDao;
import qa.qcri.mm.api.dao.TaskQueueResponseDao;
import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.entity.ClientAppAnswer;
import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.entity.MarkerStyle;
import qa.qcri.mm.api.entity.TaskQueue;
import qa.qcri.mm.api.entity.TaskQueueResponse;
import qa.qcri.mm.api.service.ClientAppAnswerService;
import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.service.MicroMapsService;
import qa.qcri.mm.api.service.TaskQueueService;
import qa.qcri.mm.api.store.StatusCodeType;
import qa.qcri.mm.api.store.URLReference;
import qa.qcri.mm.api.template.AerialClickerKMLModel;
import qa.qcri.mm.api.template.CrisisGISModel;
import qa.qcri.mm.api.template.ImageClickerKMLModel;
import qa.qcri.mm.api.template.MicroMapsCrisisModel;
import qa.qcri.mm.api.template.TextClickerKMLModel;
import qa.qcri.mm.api.util.DataFileUtil;


/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 4/22/15
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("microMapsService")
@Transactional(readOnly = true)
public class MicroMapsServiceImpl implements MicroMapsService {

    @Autowired
    ClientAppService clientAppService;

    @Autowired
    ClientAppAnswerService clientAppAnswerService;

    @Autowired
    TaskQueueResponseDao taskQueueResponseDao;

    @Autowired
    CrisisDao crisisDao;

    @Autowired
    MarkerStyleDao markerStyleDao;

    @Autowired
    TaskQueueService taskQueueService;

    private final JSONParser parser = new JSONParser();

    @Override
    public List<MicroMapsCrisisModel> getAllCries() {
        List<MicroMapsCrisisModel> models = new ArrayList<MicroMapsCrisisModel>();
        List<ClientApp> clientAppList = clientAppService.getAllClientApp();

        for(ClientApp c : clientAppList){

            String appType = getClientAppType(c.getAppType());
            int appStatus = getClientAppStatus(c.getStatus());
            JSONArray category = this.getClientAppCategory(c.getClientAppID());
            String geoJsonLink = "";
            String kmlLink="";

            models.add(new MicroMapsCrisisModel(c.getName(),c.getCreated().toString(), appType, geoJsonLink, kmlLink,category, appStatus ));
        }

        return models;
    }

    @Override
    public List<CrisisGISModel> getAllCrisis() throws Exception {

        List<Crisis> crisises = crisisDao.getAllCrisis();
        List<CrisisGISModel> models = new ArrayList<CrisisGISModel>();

        String filePath = "http://aidr-prod.qcri.org/data/trainer/";

        for(Crisis c : crisises){
            String geoJson = filePath + File.separator + c.getClientAppID() + ".json";
            String kml = filePath + File.separator + c.getClientAppID() + ".kml";
            MarkerStyle aStyle = this.getClientAppMarkerStyle(c);
            JSONObject aStyleJson = (JSONObject)parser.parse(aStyle.getStyle());
            CrisisGISModel aModel = new CrisisGISModel(c.getClientAppID(),c.getDisplayName(), c.getClickerType(), c.getActivationStart(),
                    c.getActivationEnd(),geoJson, kml, aStyleJson);

            models.add(aModel);
        }
        return models;
    }

    @SuppressWarnings("unchecked")
	@Override
    public JSONArray getAllCrisisJSONP() throws Exception {

        List<Crisis> crisises = crisisDao.getAllCrisis();
        List<MarkerStyle> allMarkerStyle = markerStyleDao.getAllMarkerStyle();
        Map<Long, MarkerStyle> markerStyleMapByClientAppId = new HashMap<>();
        for(MarkerStyle markerStyle : allMarkerStyle){
        	markerStyleMapByClientAppId.put(markerStyle.getClientAppID(), markerStyle);
        }
        
        JSONArray models = new JSONArray();

        for(Crisis c : crisises){
            MarkerStyle aStyle = markerStyleMapByClientAppId.get(c.getClientAppID()); //this.getClientAppMarkerStyle(c);

            JSONObject aObject = new JSONObject();
            aObject.put("clientAppID",c.getClientAppID()) ;
            aObject.put("crisisID",c.getCrisisID()) ;
            aObject.put("name",c.getDisplayName()) ;
            aObject.put("type",c.getClickerType()) ;
            aObject.put("bounds", c.getBounds());
            aObject.put("activationStart",c.getActivationStart().toString()) ;
            if(c.getActivationEnd() == null){
                aObject.put("activationEnd","") ;
                aObject.put("status", 1);
            }
            else{
                aObject.put("activationEnd",c.getActivationEnd().toString()) ;
                aObject.put("status", 0);
            }
            JSONObject aStyleJson = null;
            if(aStyle != null) {
            	aStyleJson = (JSONObject)parser.parse(aStyle.getStyle());
            }
            aObject.put("style",aStyleJson) ;
            models.add(aObject);
        }
        return models;
    }
    
    @Override
    public String getGeoClickerByClientAppAndAfterCreatedDate(Long clientAppID,Long createdDate) throws Exception{

        //List<Crisis> crisises = crisisDao.findCrisisByClientAppID(clientAppID);
        //ClientApp clientApp = clientAppService.findClientAppByID("clientAppID", clientAppID);

        JSONObject geoClickerOutput = new JSONObject();
        JSONArray features = new JSONArray();

        List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientAppID, StatusCodeType.TASK_LIFECYCLE_COMPLETED);

        for(TaskQueue t: taskQueueList){

            List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());

            if(responses.size() > 0 ){
                if(!responses.get(0).getResponse().equalsIgnoreCase("{}") && !responses.get(0).getResponse().equalsIgnoreCase("[]")){
                	TaskQueueResponse taskQueueResponse = responses.get(0);                	
                	if(taskQueueResponse.getCreated().compareTo(new Date(createdDate)) >= 0){
                		JSONArray eachFeatureArrary = (JSONArray)parser.parse(taskQueueResponse.getResponse());
                        for(Object a : eachFeatureArrary){
                            features.add(a);
                        }
                	}               
                }
            }

        }

        geoClickerOutput.put("type", "FeatureCollection");
        geoClickerOutput.put("features", features);
        
        return geoClickerOutput.toJSONString();     
    }
    
    @Override
    public List<Crisis> findCrisisByClientAppID(Long clientAppID){
    	return crisisDao.findCrisisByClientAppID(clientAppID);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public String getGeoClickerDataForDownload(Long clientAppID) throws Exception{

        JSONObject geoClickerOutput = new JSONObject();
        JSONArray features = new JSONArray();
        List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientAppID, StatusCodeType.TASK_LIFECYCLE_COMPLETED);
        
        List<Crisis> crisises = crisisDao.findCrisisByClientAppID(clientAppID);
        JSONArray bounds = null;
        if(crisises != null && !crisises.isEmpty()){
        	Crisis crisis = crisises.get(0);
        	bounds = (JSONArray)parser.parse(crisis.getBounds());
        }
        
        for(TaskQueue t: taskQueueList){
            List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());
            if(responses.size() > 0 ){
                if(!responses.get(0).getResponse().equalsIgnoreCase("{}") && !responses.get(0).getResponse().equalsIgnoreCase("[]")){
                    JSONArray eachFeatureArrary = (JSONArray)parser.parse(responses.get(0).getResponse());
                    for(Object a : eachFeatureArrary){
                    	JSONObject jsonObject = (JSONObject) a;
                    	
                    	if(jsonObject != null && jsonObject.get("properties") != null 
                    			&& jsonObject.get("geometry") != null && bounds != null){
                    		
                    		JSONObject geometryObject = (JSONObject) jsonObject.get("geometry");
                        	JSONArray coordinates = (JSONArray) geometryObject.get("coordinates");                        	
                        	
                    		if( ((Number)coordinates.get(0)).doubleValue() >= ((Number)bounds.get(0)).doubleValue() 
                    				&& ((Number)coordinates.get(0)).doubleValue() <= ((Number)bounds.get(2)).doubleValue()
                    				&& ((Number)coordinates.get(1)).doubleValue() >= ((Number)bounds.get(1)).doubleValue() 
                    				&& ((Number)coordinates.get(1)).doubleValue() <= ((Number)bounds.get(3)).doubleValue()){
                    			
                    			features.add(a);
                    		
                    		}
                    	}
                    }
                }
            }
        }
        geoClickerOutput.put("developedBy", "Qatar Computing Research Institute");
        geoClickerOutput.put("type", "FeatureCollection");
        geoClickerOutput.put("features", features);
        return geoClickerOutput.toJSONString();     
    }

    @Override
    public String getGeoClickerByClientApp(Long clientAppID) throws Exception{

        System.out.println("clientAppID : " + clientAppID);

        List<Crisis> crisises = crisisDao.findCrisisByClientAppID(clientAppID);
        ClientApp clientApp = clientAppService.findClientAppByID("clientAppID", clientAppID);

        String fileName = URLReference.GEOJSON_HOME + "app" + File.separator + clientApp.getClientAppID() + ".json";

        JSONObject geoClickerOutput = new JSONObject();

        if(!DataFileUtil.doesFileExist(fileName)) {
            JSONArray features = new JSONArray();

            System.out.println("crisis :" + clientApp.getName());

            List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientAppID, StatusCodeType.TASK_LIFECYCLE_COMPLETED);

            System.out.println("taskQueueList :" + taskQueueList.size());

            for(TaskQueue t: taskQueueList){

                List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());

                if(responses.size() > 0 ){
                    if(!responses.get(0).getResponse().equalsIgnoreCase("{}") && !responses.get(0).getResponse().equalsIgnoreCase("[]")){
                        JSONArray eachFeatureArrary = (JSONArray)parser.parse(responses.get(0).getResponse());
                        for(Object a : eachFeatureArrary){
                            features.add(a);
                        }

                    }
                }

            }
            
            geoClickerOutput.put("developedBy", "Qatar Computing Research Institute");
            geoClickerOutput.put("type", "FeatureCollection");
            geoClickerOutput.put("features", features);            
            //System.out.println(geoClickerOutput.toJSONString());
            
            // if crisis is archived
            if(crisises != null && !crisises.isEmpty()){
            	Crisis crisis = crisises.get(0);
            	if(crisis.getActivationEnd() != null){
            		DataFileUtil.createAfile(geoClickerOutput.toJSONString(), fileName);
            	}
            }
            
            return geoClickerOutput.toJSONString();
        }
        return DataFileUtil.getDataFileContent(fileName);        
    }
    
    @Override
    public boolean createZip(String zipFileName, String filePath, String fileName){
    	byte[] buffer = new byte[1024];
    	try {
			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			ZipEntry ze= new ZipEntry(fileName);
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(filePath);
   
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
			zos.closeEntry();
			zos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }

    @Override
    public String getGeoClickerByCrisis(Long crisisID) throws Exception{
        JSONObject geoClickerOutput = new JSONObject();
        List<Crisis> crisises = crisisDao.findCrisisByCrisisID(crisisID);

        if(crisises.size() == 0) {
            return geoClickerOutput.toJSONString();
        }

        String fileName = URLReference.GEOJSON_HOME + "crisis" + File.separator + crisises.get(0).getCrisisID() + ".json";

        if(!DataFileUtil.doesFileExist(fileName)) {
            JSONArray features = new JSONArray();

            for (Crisis c : crisises) {
                List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(c.getClientAppID(), StatusCodeType.TASK_LIFECYCLE_COMPLETED);

                for (TaskQueue t : taskQueueList) {

                    List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());

                    if (responses.size() > 0) {
                        if (!responses.get(0).getResponse().equalsIgnoreCase("{}") && !responses.get(0).getResponse().equalsIgnoreCase("[]")) {
                            JSONArray eachFeatureArrary = (JSONArray) parser.parse(responses.get(0).getResponse());
                            for (Object a : eachFeatureArrary) {
                                features.add(a);
                            }

                        }
                    }

                }
            }

            geoClickerOutput.put("type", "FeatureCollection");
            geoClickerOutput.put("features", features);

            System.out.println(geoClickerOutput.toJSONString());
            DataFileUtil.createAfile(geoClickerOutput.toJSONString(), fileName);
        }

        String content = DataFileUtil.getDataFileContent(fileName);

        return "jsonp(" + content + ");";
    }


    @Override
    public String generateTextClickerKML(Long clientAppID) throws Exception{
        List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientAppID, StatusCodeType.TASK_LIFECYCLE_COMPLETED);
        TextClickerKMLModel model = new TextClickerKMLModel();
        model.setParser(parser);
        model.setKmlText(new StringBuffer());
        model.buildHeader();
        for(TaskQueue t: taskQueueList){
            List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());
            model.buildKMLBody(responses);
        }
        model.buildFooter();
        return model.getKmlText().toString();

    }

    @Override
    public String generateImageClickerKML(Long clientAppID) throws Exception{
        List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientAppID, StatusCodeType.TASK_LIFECYCLE_COMPLETED);
        ImageClickerKMLModel model = new ImageClickerKMLModel();
        model.setParser(parser);
        model.setKmlText(new StringBuffer());
        model.buildHeader();
        for(TaskQueue t: taskQueueList){
            List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());
            model.buildKMLBody(responses);
        }
        model.buildFooter();
        return model.getKmlText().toString();

    }

    @Override
    public String generateAericalClickerKML(Long clientAppID) throws Exception{
        List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientAppID, StatusCodeType.TASK_LIFECYCLE_COMPLETED);
        AerialClickerKMLModel model = new AerialClickerKMLModel();
        model.setParser(parser);
        model.setKmlText(new StringBuffer());
        model.buildHeader();
        for(TaskQueue t: taskQueueList){
            List<TaskQueueResponse> responses = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(t.getTaskQueueID());
            model.buildKMLBody(responses);
        }
        model.buildFooter();
        return model.getKmlText().toString();

    }

    private void generateGEOJSONToLocal(String fileContent, String fileName, long refreshInMinute){

        try{
            if(!DataFileUtil.doesFileExist(fileName)){
                DataFileUtil.createAfile(fileContent, fileName);
            }
            else{
                if(DataFileUtil.doesFileExist(fileName) && DataFileUtil.isUpdateRequired(fileName, refreshInMinute)){
                    File deletedFile = new File(fileName);
                    deletedFile.delete();
                    DataFileUtil.createAfile(fileContent, fileName);
                }
            }

        }
        catch(Exception e){
            System.out.print("Exception :" + e);
        }

    }

    private MarkerStyle getClientAppMarkerStyle(Crisis c){
        List<MarkerStyle> styles = markerStyleDao.findByClientAppID(c.getClientAppID().longValue());

        if(styles.isEmpty()){
            styles = markerStyleDao.findByAppType(c.getClickerType());
        }

        if(styles.size() > 0){
            return styles.get(0);
        }

        return null;
    }

    private JSONArray getClientAppCategory(Long clientAppID){
        ClientAppAnswer answer =  clientAppAnswerService.getClientAppAnswer(clientAppID);
        String ans = answer.getAnswerMarkerInfo();

        JSONArray jsonArray = null;

        JSONParser parser = new JSONParser();

        try {

            jsonArray =  (JSONArray)parser.parse(ans);

        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return jsonArray;
    }

    private String getClientAppType (int type){
        String appType = null;
        switch (type) {
            case 1:  appType = "Text";
                break;
            case 2:  appType = "Image";
                break;
            case 3:  appType = "Video";
                break;
            case 4:  appType = "Geo";
                break;
            case 5:  appType = "Aerial";
                break;
            case 6:  appType = "3W";
                break;

        }

        return appType;
    }

    private int getClientAppStatus (int status){
        int appStatus = 0;
        switch (status) {
            case 1:  appStatus = 1;
                break;
            case 2:  appStatus = 1;
                break;
            case 5:  appStatus = 1;
                break;
            default:  appStatus = 0;
                break;
        }

        return appStatus;
    }
}
