package qa.qcri.mm.api.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.service.CrisisService;
import qa.qcri.mm.api.service.TaskQueueService;
import qa.qcri.mm.api.store.StatusCodeType;
import qa.qcri.mm.api.template.ClientAppModel;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 2/21/14
 * Time: 1:44 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/clientapp")
public class ClientAppController {
    
	@Autowired
    private ClientAppService clientAppService;
    
	@Autowired
	private TaskQueueService taskQueueService;
	
	@Autowired
	private CrisisService crisisService;
	
	@Value("${taggerAPI}")
	private String taggerAPI;
    
    @RequestMapping(value = "/allactive", method = RequestMethod.GET)
    public List<ClientAppModel> getAllActive(){
        return clientAppService.getAllActiveClientApps();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public List<ClientAppModel> getAll(){
    	List<ClientApp> appList = clientAppService.getAvailableClientApp();
    	List<ClientAppModel> aList = new ArrayList<ClientAppModel>();
    	Map<Long, Long> totalTaskInQueue = taskQueueService.getTotalTaskInQueueMapWithClientAppId();
    	Map<Long, Long> totalTaskAvailableInQueue = taskQueueService.getTotalTaskInQueueByStatusMapWithClientAppId(StatusCodeType.TASK_LIFECYCLE_COMPLETED);
    	
    	for (ClientApp clientApp : appList) {
    		Long clientAppID = clientApp.getClientAppID();
    		Long totalTask = totalTaskInQueue.containsKey(clientAppID) ? totalTaskInQueue.get(clientAppID) : 0L ;
			Long availableTask = totalTaskAvailableInQueue.containsKey(clientAppID) ? totalTaskAvailableInQueue.get(clientAppID) : 0L ;
			ClientAppModel model = new ClientAppModel(clientAppID,
					clientApp.getPlatformAppID(), clientApp.getCrisisID(), clientApp.getName(),
					clientApp.getShortName(), clientApp.getAppType(), clientApp.getStatus(), availableTask, totalTask);
            aList.add(model);
        }
    	return aList;
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ClientAppModel getClientApp(@PathVariable("id") long id){
    	ClientApp clientApp = clientAppService.getClientAppById(id);
    	List<Crisis> crisis = crisisService.findCrisisByClientAppID(clientApp.getClientAppID());
    	String crisisName = null;
    	Long crisisID = null;
    	if(!crisis.isEmpty()){
    		crisisName = crisis.get(0).getDisplayName();
    		crisisID = crisis.get(0).getCrisisID();
    	}
    	String classifierName = null;
		if (clientApp.getNominalAttributeID() != null) {
			classifierName = getClassifierName(clientApp.getNominalAttributeID());
		}
    	ClientAppModel model = new ClientAppModel(clientApp.getClientAppID(),
				clientApp.getPlatformAppID(), crisisID, crisisName, classifierName, clientApp.getName(),
				clientApp.getShortName(), clientApp.getAppType(), clientApp.getStatus(), null, null,
				clientApp.getTaskRunsPerTask(), clientApp.getIsCustom(), clientApp.getTcProjectID());
        return model;
    }
    
    public String getClassifierName(Long attributeId) {
		HttpClient httpClient = new DefaultHttpClient();
        String jsonResponse = "";
        String classifierName = null;
        String url = taggerAPI + "rest/attribute/" + attributeId;
        try {
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();

            if ( responseCode == 200 || responseCode == 204) {
                if(response.getEntity()!=null){
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader((response.getEntity().getContent())));

                    String output;
                    while ((output = br.readLine()) != null) {
                    	jsonResponse = jsonResponse + output;
                    }
                }
            }
            else{
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            JSONParser parser = new JSONParser();
    		JSONObject nominalAttribute = (JSONObject) parser.parse(jsonResponse);
    		classifierName = (String) nominalAttribute.get("name");
        }catch (Exception ex) {
        	return null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
		
        return classifierName;
    }
}
