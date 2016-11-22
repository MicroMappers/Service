package qa.qcri.mm.trainer.pybossa.format.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import qa.qcri.mm.trainer.pybossa.dao.ImageMetaDataDao;
import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entity.ImageMetaData;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueue;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueueResponse;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;
import qa.qcri.mm.trainer.pybossa.service.ClientAppResponseService;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/10/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class VanuatuDataOutputProcessor extends DataProcessor {

    @Autowired
    ImageMetaDataDao imageMetaDataDao;

    protected static Logger logger = Logger.getLogger(VanuatuDataOutputProcessor.class);
    
    public VanuatuDataOutputProcessor(ClientApp clientApp) {
        super(clientApp);
    }

    public void setImageMetaDataDao(ImageMetaDataDao imageMetaDataDao) {
        this.imageMetaDataDao = imageMetaDataDao;
    }

    @Override
    public TaskQueueResponse process(List<TaskRun> datasource, TaskQueue taskQueue) throws Exception {
        if(this.clientApp == null)
            return null;

        this.datasource = datasource;
        this.taskQueue = taskQueue;

        TaskQueueResponse taskQueueResponse = null;

        try{

           // JSONArray array = (JSONArray) parser.parse(this.datasource) ;
            JSONArray taskQueueResJsonArray = new JSONArray();
            if(this.datasource.size() > 0) {
               // Iterator itr= array.iterator();



                String tweetID = null;
                String imgURL = this.datasource.get(0).getInfo().getString("imgurl"); 
                                
                //String bounds = this.getStringValueFromInfoJson(array, "geo");
                String bounds = "[125.00587463378906, 11.241715102754723, 125.00553131103516, 11.241378366973036]";

                JSONObject finalProperties = new JSONObject();
                finalProperties.put("imgURL", imgURL);
                finalProperties.put("bounds", bounds);
                finalProperties.put("taskid", this.taskQueue.getTaskID());

                JSONObject features = this.getFeature(imgURL);

                JSONArray locations  =  new JSONArray();
                
                for (TaskRun taskRun : this.datasource) {
                	org.json.JSONObject info = taskRun.getInfo();
                    JSONArray loc = (JSONArray)info.get("loc");
                    this.getProperties(loc, info, locations) ;
				}

                finalProperties.put("features", locations);

                logger.info("ans: " + finalProperties.toJSONString() );

                features.put("properties", finalProperties) ;

                logger.info("ans: " + features.toJSONString() );

                if(locations.size() > 0){
                    taskQueueResJsonArray.add(features)  ;
                }

                taskQueueResponse = new TaskQueueResponse(this.taskQueue.getTaskQueueID(), taskQueueResJsonArray.toJSONString(), tweetID);

            }
        }
        catch(Exception e){
            logger.info("Exception while processing Vanatu Data : " + e) ;
            taskQueueResponse = null;
        }
        return taskQueueResponse;
    }

    @Override
    public List<TaskQueueResponse> generateMapOuput(List<TaskQueue> taskQueues, ClientAppResponseService clientAppResponseService) throws Exception {
        List<TaskQueueResponse> responses = new ArrayList<TaskQueueResponse>();

        for(TaskQueue taskQ : taskQueues){
            List<TaskQueueResponse> taskQueueResponse = clientAppResponseService.getTaskQueueResponse(taskQ.getTaskQueueID());
            if(taskQueueResponse.size() > 0){
                TaskQueueResponse thisTaskResponse = taskQueueResponse.get(0);
                String infoOutput = thisTaskResponse.getResponse();

                if(infoOutput!= null && !infoOutput.isEmpty()){
                    responses.add(thisTaskResponse) ;
                }
            }
        }
        return responses;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private JSONObject getFeature(String imgURL){
        List<ImageMetaData> imageMetaDataList = imageMetaDataDao.findImageMetaDataByImageURL(imgURL);
        JSONObject features = new JSONObject();


        if(imageMetaDataList.size() > 0){
            features.put("type", "Feature") ;

            JSONObject geometry = new JSONObject();

            geometry.put("type", "Point") ;
            JSONArray latlng = new JSONArray();

            ImageMetaData aData = imageMetaDataList.get(0);
            
            latlng.add(Double.valueOf(aData.getLng())) ;            
            latlng.add(Double.valueOf(aData.getLat())) ;
            

            geometry.put("coordinates", latlng) ;

            features.put("geometry", geometry) ;

        }
        System.out.println("***** : " + features.toJSONString());
        return features;

    }

    private JSONArray getProperties(JSONArray loc, org.json.JSONObject info, JSONArray locations){

    	try{
    		if(!loc.isEmpty() && loc.size() > 0){
    			Iterator itr= loc.iterator();
    			while(itr.hasNext()){
    				JSONObject featureJsonObj = (JSONObject)itr.next();
    				JSONObject layer = (JSONObject)featureJsonObj.get("layer");
    				String layerType = (String)featureJsonObj.get("layerType");

    				JSONObject properties  =  (JSONObject)layer.get("properties");

    				properties.put("layerType",layerType);

    				if(style.size() > 0){
    					JSONObject theStyleTemplate = (JSONObject)parser.parse(style.get(0).getStyle()) ;

    					JSONArray styles = (JSONArray)theStyleTemplate.get("style");
    					for(int i=0; i < styles.size(); i++){
    						JSONObject aStyle  = (JSONObject)styles.get(i);
    						String lable_code = (String)aStyle.get("label_code");
    						if(lable_code.equalsIgnoreCase(layerType)){
    							properties.put("label",layerType);
    							properties.put("style", aStyle);
    						}
    					}

    				}

    				properties.put("userID", info.get("user_id"));

    				layer.remove("bounds");
    				layer.remove("taskid");
    				layer.remove("imgURL");

    				locations.add(layer) ;
    			}
    		}
    	}
    	catch (Exception e){
    		logger.info("Excpetion while getting Properties - " + e.getMessage());
    	}

    	return locations;
    }
}
