package qa.qcri.mm.trainer.pybossa.format.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
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
public class SkyEyeDataOutputProcessor extends DataProcessor {

	protected static Logger logger = Logger.getLogger(SkyEyeDataOutputProcessor.class);
	 
    public SkyEyeDataOutputProcessor(ClientApp clientApp) {
        super(clientApp);
    }

    @Override
    public TaskQueueResponse process(List<TaskRun> datasource, TaskQueue taskQueue) throws Exception {
    	if(this.clientApp == null)
    		return null;

    	this.datasource = datasource;
    	this.taskQueue = taskQueue;

    	TaskQueueResponse taskQueueResponse = null;

    	try{

    		JSONArray taskQueueResJsonArray = new JSONArray();
    		if(this.datasource.size() > 0) {
    			JSONArray locations  =  new JSONArray();

    			JSONObject finalProperties = new JSONObject();
    			String imgURL = this.datasource.get(0).getInfo().getString("imgurl");
    			finalProperties.put("imgURL", imgURL);

    			//JSONArray bounds = (JSONArray)parser.parse(this.getStringValueFromInfoJson(array, "geo"));
    			JSONArray bounds = (JSONArray)parser.parse("[125.00587463378906, 11.241715102754723, 125.00553131103516, 11.241378366973036]");

    			finalProperties.put("bounds", bounds);
    			finalProperties.put("taskid", this.taskQueue.getTaskID());

    			JSONObject features = this.getFeature(bounds);

    			for (TaskRun taskRun : this.datasource) {
    				org.json.JSONObject info = taskRun.getInfo();
    				JSONArray loc = (JSONArray)info.get("loc");
    				this.getProperties(loc, info, locations, taskRun) ;
    			}

    			finalProperties.put("features", locations);

    			features.put("properties", finalProperties) ;

    			if(locations.size() > 0){
    				taskQueueResJsonArray.add(features)  ;
    			}

    			taskQueueResponse = new TaskQueueResponse(this.taskQueue.getTaskQueueID(), taskQueueResJsonArray.toJSONString(), null);
    		}
    	}
    	catch(Exception e){
    		logger.error("Exception while processing data for SkyEyeDataOutputProcessor"+ e.getMessage());
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

    private JSONArray getProperties(JSONArray loc, org.json.JSONObject info, JSONArray locations, TaskRun taskRun){

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

                    properties.put("userID",  taskRun.getUser().getId());

                    layer.remove("bounds");
                    layer.remove("taskid");
                    layer.remove("imgURL");

                    locations.add(layer) ;
                }
            }

        }
        catch (Exception e){
            logger.error("Excpetion while gettingProperties form Json - " + e.getMessage());
        }

        return locations;
    }

    private JSONObject getFeature(JSONArray aBounds){
        JSONObject features = new JSONObject();

        features.put("type", "Feature") ;

        JSONObject geometry = new JSONObject();

        geometry.put("type", "Point") ;
        JSONArray latlng = new JSONArray();

        double lat1 = Double.parseDouble((String)aBounds.get(3));
        double lat2 = Double.parseDouble((String)aBounds.get(1));

        double lng1 = Double.parseDouble((String)aBounds.get(2));
        double lng2 = Double.parseDouble((String)aBounds.get(0));

        double cLat =  (lat1 + lat2) / 2;
        double cLng =  (lng1 + lng2) / 2;

        latlng.add(cLng) ;
        latlng.add(cLat) ;        

        geometry.put("coordinates", latlng) ;

        features.put("geometry", geometry) ;

        logger.info("***** : " + features.toJSONString());
        return features;

    }

}
