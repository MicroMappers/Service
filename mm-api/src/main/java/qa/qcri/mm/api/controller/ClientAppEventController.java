package qa.qcri.mm.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.entity.ClientAppEvent;
import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.service.ClientAppEventService;
import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.service.CrisisService;
import qa.qcri.mm.api.util.CommonUtil;

@RestController
@RequestMapping("/rest/client_app_event")
public class ClientAppEventController {
    
	@Autowired
    private ClientAppEventService clientAppEventService;
	
	@Autowired
    private ClientAppService clientAppService;
	
	@Autowired
    private CrisisService crisisService;
    
	@RequestMapping(value = "/{id}/getClientEvents", method={RequestMethod.GET})
    public List<ClientAppEvent> getClientAppEvents(@PathVariable("id") long id) {
		List<ClientAppEvent> events = new ArrayList<>();
		List<Crisis> crList = crisisService.findCrisisByClientAppID(id);
		if(crList != null && crList.size() > 0) {
			List<Crisis> crisisList = crisisService.findCrisisByCrisisID(crList.get(0).getCrisisID());
			for(Crisis crisis : crisisList) {
				ClientAppEvent clientAppEvent = clientAppEventService.getClientAppEvent(crisis.getClientAppID());
				if(clientAppEvent != null) {
					events.add(clientAppEvent);
				}
			}
		}
		return events;
    }
	
	@RequestMapping(value = "/{id}/getOtherClientApp", method={RequestMethod.GET})
    public List<ClientApp> getOtherClientApp(@PathVariable("id") long id) {
		List<ClientApp> clientApps = new ArrayList<>();
		List<Crisis> crList = crisisService.findCrisisByClientAppID(id);
		if(crList != null && crList.size() > 0) {
			List<Crisis> crisisList = crisisService.findCrisisByCrisisID(crList.get(0).getCrisisID());
			for(Crisis crisis : crisisList) {
				ClientApp clientApp = clientAppService.getClientAppById(crisis.getClientAppID());
				if(clientApp != null) {
					clientApp.setClient(null);
					clientApps.add(clientApp);
				}
			}
		}
		return clientApps;
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/generateEvents", method={RequestMethod.POST})
    public Map<String, Object> generateEvents(@RequestBody String requestData) throws ParseException {
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(requestData);
		Long clientId = (Long) jsonObject.get("clientId");
		Long geoClientAppId = (Long) jsonObject.get("geoClientAppId");
		
		
		ClientApp clientApp = clientAppService.getClientAppById(clientId);
		ClientAppEvent event = new ClientAppEvent(null, clientApp.getName(), clientApp.getClientAppID(), 1, 0L, new Date());
		clientAppEventService.save(event);
		event = clientAppEventService.getClientAppEvent(clientId);
		event.setEventID(event.getClientAppEventID());
		clientAppEventService.save(event);
		
		ClientApp geoClientApp = clientAppService.getClientAppById(geoClientAppId);
		ClientAppEvent geoEvent = new ClientAppEvent(null, geoClientApp.getName(), geoClientApp.getClientAppID(), 2, event.getClientAppEventID(), new Date());
		clientAppEventService.save(geoEvent);
		
		return CommonUtil.returnSuccess("Events Generated Successfully.", null);
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method={RequestMethod.POST})
    public void saveMarkerStyle(@RequestBody ClientAppEvent markerStyle) {
    	if(markerStyle != null) {
    		clientAppEventService.save(markerStyle);
    	}
    }
}
