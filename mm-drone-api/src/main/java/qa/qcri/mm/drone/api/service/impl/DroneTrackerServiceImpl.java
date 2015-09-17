package qa.qcri.mm.drone.api.service.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import qa.qcri.mm.drone.api.dao.DroneReportDao;
import qa.qcri.mm.drone.api.dao.DroneTrackerDao;
import qa.qcri.mm.drone.api.entity.DroneTracker;
import qa.qcri.mm.drone.api.entity.SubscribeUser;
import qa.qcri.mm.drone.api.service.DroneTrackerService;
import qa.qcri.mm.drone.api.service.SubscribeUserService;
import qa.qcri.mm.drone.api.service.UserTokenService;
import qa.qcri.mm.drone.api.store.LookUp;
import qa.qcri.mm.drone.api.store.SubscribeFrequency;
import qa.qcri.mm.drone.api.util.GISUtil;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/23/14
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("droneTrackerService")
@Transactional(readOnly = true)
public class DroneTrackerServiceImpl implements DroneTrackerService {
    @Autowired
    DroneTrackerDao droneTrackerDao;

    @Autowired
    DroneReportDao droneReportDao;

    @Autowired
    UserTokenService userTokenService;
    
    @Autowired
    private SubscribeUserService subscribeUserService;

    @Override
    public JSONArray getAllApprovedDroneGeoData() {
        List<DroneTracker> drones =  droneTrackerDao.getallApprovedData();     
        return reformatDroneJson(drones);  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    @Override
    public JSONArray getAllApprovedDroneGeoDataAfterID(Long id) {
        //System.out.println("id : " + id);
        List<DroneTracker> drones =  droneTrackerDao.getallApprovedDataAfterID(id);
        //System.out.println("drones : " + drones.size());
        return reformatDroneJson(drones);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DroneTracker> getAllPendingDroneGeoData(String token) {

        return droneTrackerDao.getallPendingData();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    @Transactional(readOnly = false)
    public void saveUserMappingRequest(String geoJson) {
        JSONObject jObj = getJsonObject(geoJson);
        if(jObj != null){
            GISUtil gisUtil = new GISUtil();
            JSONObject info = (JSONObject)jObj.get("info");
            String url = (String) info.get("url");
            String key = getReserverLookupKey((JSONObject) jObj.get("features"));
            String displayName = gisUtil.getDisplayNameWithReverseLookUp(key) ;
            DroneTracker droneTracker =    new DroneTracker(geoJson,url, displayName );
            droneTrackerDao.save(droneTracker);
            notifySubscribeUsers(droneTracker);            
        }
    }

    @Override
    @Transactional(readOnly = false)
    public int updateUserMappingRequest(String geoJson) {
        JSONObject jObj = getJsonObject(geoJson);
        int returnValue = LookUp.REQUEST_INPUT_DATA_INVALID;
        System.out.println("updateUserMappingRequest : " + geoJson );

        if(jObj != null){
            GISUtil gisUtil = new GISUtil();
            JSONObject info = (JSONObject)jObj.get("info");
            String requestEmail = (String) info.get("email");

            Long id = (Long)jObj.get("id");


            System.out.println("info : " + info.toJSONString() );

            List<DroneTracker> drones = droneTrackerDao.findDroneTrackerByID(id);
            System.out.println("id : " + id );
            System.out.println("size : " + drones.size() );
            if(drones.size() > 0){
                DroneTracker oldDrone = drones.get(0);
                String oldDroneInfo = oldDrone.getGeojson();
                JSONObject oldJobj = getJsonObject(oldDroneInfo);
                JSONObject oldInfo = (JSONObject)oldJobj.get("info");
                String oldEmail =  (String) oldInfo.get("email");
                if(oldEmail.equalsIgnoreCase(requestEmail)){
                    // delete & save
                    droneTrackerDao.deleteDroneTracker(oldDrone.getId());
                    saveUserMappingRequest(geoJson);
                    returnValue = LookUp.REQUEST_SUCCESS;
                }
                else{
                    returnValue = LookUp.REQUEST_INPUT_DATA_MISMATCH;
                }
            }
            else{
                returnValue = LookUp.REQUEST_INPUT_DATA_NOT_FOUND;
            }
        }

        return returnValue;
    }

    @Override
    @Transactional(readOnly = false)
    public int deleteUserMappingRequest(String email, Long id) {
        int returnValue = LookUp.REQUEST_INPUT_DATA_INVALID;
        if(!email.isEmpty() && email != null && id != null){
            List<DroneTracker> drones = droneTrackerDao.findDroneTrackerByID(id);
            if(drones.size() > 0){
                DroneTracker drone = drones.get(0);
                String geoJson = drone.getGeojson();
                JSONObject jObj = getJsonObject(geoJson);
                JSONObject info = (JSONObject)jObj.get("info");
                String oldEmail =  (String) info.get("email");
                if(oldEmail.equalsIgnoreCase(email)){
                    droneTrackerDao.deleteDroneTracker(drone.getId());
                    returnValue = LookUp.REQUEST_SUCCESS;
                }
                else{
                    returnValue = LookUp.REQUEST_INPUT_DATA_MISMATCH;
                }

            }
            else{
                returnValue = LookUp.REQUEST_INPUT_DATA_NOT_FOUND;
            }
        }
        else{
            returnValue = LookUp.REQUEST_INPUT_DATA_INVALID;
        }
        return returnValue;
    }

    private String getReserverLookupKey(JSONObject features) {
        //lat=16.63897422279177&lon=122.0280850999999
        JSONObject geometry = (JSONObject)features.get("geometry");
        JSONArray coordinates = (JSONArray)geometry.get("coordinates");
        String key = "lat="+coordinates.get(1)+"&lon=" + coordinates.get(0);
        return key;
    }

    private String getLatLng(JSONObject features){
        JSONObject geometry = (JSONObject)features.get("geometry");
        JSONArray coordinates = (JSONArray)geometry.get("coordinates");
       // String lng = (String)coordinates.get(0);
       // String lat = (String)coordinates.get(1);
        String key = coordinates.get(1)+"," + coordinates.get(0);
        return key;
    }

    private JSONObject getJsonObject(String geoJson){
        JSONObject obj = null;
        try{
            JSONParser parser = new JSONParser();
            obj = (JSONObject) parser.parse(geoJson);
        }
        catch(Exception e){
            System.out.println("getJsonObject :" + e.getMessage());
        }

        return obj;

    }


    private JSONArray reformatDroneJson(List<DroneTracker> drones ){
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        for(DroneTracker d : drones){
            try{

                JSONObject obj = (JSONObject) parser.parse(d.getGeojson());
                JSONObject temp = new JSONObject();
                temp.put("features", obj.get("features"));
                temp.put("info", getMapinfoWindowJson((JSONObject)obj.get("info"), d));
                temp.put("vote", droneReportDao.getReportCount(d.getId()));
                jsonArray.add(temp) ;

            }
            catch(Exception e){
                System.out.println("reformatDroneJson :" + e.getMessage());
            }
        }

        return jsonArray;
    }


    private JSONObject getMapinfoWindowJson(JSONObject jsonObject, DroneTracker d){
        JSONObject obj = new JSONObject();

        obj.put("url", jsonObject.get("url"));
        obj.put("created",DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT).format(d.getCreated()) );
        obj.put("id", d.getId());
        obj.put("displayName", d.getDisplayName()) ;
       // obj.put("title", jsonObject.get("title"));

        return obj;
    }
    

	@Override
	@Async
    public void notifySubscribeUsers(DroneTracker droneTracker){
		
		List<String> emails = new ArrayList<String>();
    	List<SubscribeUser> subscribedUsers = subscribeUserService.getSubscribedUsers(SubscribeFrequency.IMAGERY_ADDED);
    	if(subscribedUsers != null && !subscribedUsers.isEmpty()){
    		for(SubscribeUser subscribeUser : subscribedUsers){
    			emails.add(subscribeUser.getEmail());
    		}
    	}
    	if(!emails.isEmpty()){
    		Map<String, Object> variables = new HashMap<>();
    		variables.put("location", droneTracker.getDisplayName());
    		variables.put("created_at", new Date());
    		sendMail(emails, variables, "imagery_added.html");
    	}    	
    }
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Async
	public void sendMail( List<String> recipientEmails, Map<String, Object> variables, String templateName)
	 {
		
	  try {
		// Prepare the evaluation context
		  final Context ctx = new Context();	  
		  ctx.setVariables(variables);
		  
		  final MimeMessage mimeMessage = mailSender.createMimeMessage();
		  final MimeMessageHelper message =
		      new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
		  message.setSubject("UAViators Map Updated");
		  message.setFrom("info.uaviators@gmail.com");	  
		  message.setBcc(recipientEmails.toArray(new String[0]));
		 
		  
		  final String htmlContent = this.templateEngine.process(templateName, ctx);
		  message.setText(htmlContent, true); 
		  System.out.println(htmlContent);
		 
		  // Send mail
		  this.mailSender.send(mimeMessage);
		  
		} catch (MailException e) {		
			e.printStackTrace();
		} catch (MessagingException e) {		
			e.printStackTrace();
		}
	 
	}
	
	@Override
	@Scheduled(cron="0 0 0 1/1 * ?")
    public void dailySuscribedMail()
    {
		List<String> emails = new ArrayList<String>();
    	List<SubscribeUser> subscribedUsers = subscribeUserService.getSubscribedUsers(SubscribeFrequency.DAILY);
    	if(subscribedUsers != null && !subscribedUsers.isEmpty()){
    		for(SubscribeUser subscribeUser : subscribedUsers){
    			emails.add(subscribeUser.getEmail());
    		}
    		
    		Calendar calendar = Calendar.getInstance();
        	calendar.add(Calendar.DATE, -1);
        	calendar.set(Calendar.HOUR_OF_DAY, 0);
        	calendar.set(Calendar.MINUTE, 0);
        	calendar.set(Calendar.SECOND, 0);
        	calendar.set(Calendar.MILLISECOND, 0);
        	Date fromDate = calendar.getTime();

        	calendar.add(Calendar.DATE, 1);
        	Date toDate = calendar.getTime();

        	Criterion criterion = Restrictions.between("created", fromDate, toDate);
        	List<DroneTracker> droneTrackers = droneTrackerDao.findByCriteriaByOrder(criterion, new String[]{"created"}, null);
        	if(!emails.isEmpty() && !droneTrackers.isEmpty()){
        		Map<String, Object> variables = new HashMap<>();
        		List<Map<String, Object>> droneData = new ArrayList<>();
        		for(DroneTracker droneTracker : droneTrackers){
        			Map<String, Object> tempMap = new HashMap<>();
        			tempMap.put("location", droneTracker.getDisplayName());
        			tempMap.put("created_at", droneTracker.getCreated());
        			droneData.add(tempMap);
        		}
        		variables.put("drones", droneData);
        		sendMail(emails, variables, "daily.html");
        	}
    	}   	
    	
    }
	
	@Override
	@Scheduled(cron="0 1 0 ? * SUN")
    public void weeklySuscribedMail()
    {
		List<String> emails = new ArrayList<String>();
    	List<SubscribeUser> subscribedUsers = subscribeUserService.getSubscribedUsers(SubscribeFrequency.WEEKLY);
    	if(subscribedUsers != null && !subscribedUsers.isEmpty()){
    		for(SubscribeUser subscribeUser : subscribedUsers){
    			emails.add(subscribeUser.getEmail());
    		}
    		
    		// get last week range
    		Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
        	c.set(Calendar.MINUTE, 0);
        	c.set(Calendar.SECOND, 0);
        	c.set(Calendar.MILLISECOND, 0);
            
            int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
            c.add(Calendar.DATE, -i - 7);
            Date fromDate = c.getTime();
            c.add(Calendar.DATE, 7);
            Date toDate = c.getTime();

        	Criterion criterion = Restrictions.between("created", fromDate, toDate);
        	List<DroneTracker> droneTrackers = droneTrackerDao.findByCriteriaByOrder(criterion, new String[]{"created"}, null);
        	if(!emails.isEmpty() && !droneTrackers.isEmpty()){
        		Map<String, Object> variables = new HashMap<>();
        		List<Map<String, Object>> droneData = new ArrayList<>();
        		for(DroneTracker droneTracker : droneTrackers){
        			Map<String, Object> tempMap = new HashMap<>();
        			tempMap.put("location", droneTracker.getDisplayName());
        			tempMap.put("created_at", droneTracker.getCreated());
        			droneData.add(tempMap);
        		}
        		variables.put("drones", droneData);
        		sendMail(emails, variables, "weekly.html");
        	}
    	}   	
    	
    }
	
}
