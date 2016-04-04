package qa.qcri.mm.api.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.CrisisService;
import qa.qcri.mm.api.template.CrisisModel;

@RestController
@RequestMapping("/crisis")
public class CrisisController {

	private final Logger logger = Logger.getLogger(CrisisController.class);
	
	@Autowired
	private CrisisService crisisService;
	
	@Value("${managerAPI}")
	private String managerAPI;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<CrisisModel> getAllCrisis() throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
        String jsonResponse = "";
        List<CrisisModel> crisisModels = new ArrayList<>();        
        String url = managerAPI + "/public/collection/list?micromappersEnabled=true";
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
    		JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);
    		
    		for(Object object : jsonArray) {
    			JSONObject jsonObject = (JSONObject) object;
    			CrisisModel crisisModel = new CrisisModel();
    			crisisModel.setCrisisID((Long)jsonObject.get("collectionId"));
    			crisisModel.setDisplayName((String)jsonObject.get("collectionName"));
    			crisisModels.add(crisisModel);
    		}
    		
        }catch (Exception ex) {
        	logger.error("Error in fetching/parsing response", ex);
        	return null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
		
		return crisisModels;
	}
}
