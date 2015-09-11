package qa.qcri.mm.drone.api.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import qa.qcri.mm.drone.api.service.SubscribeUserService;

@Path("/user")  
@Component
public class SubscribeUserController {
	protected static Logger logger = Logger.getLogger("SubscribeUserController");
	
	@Autowired
    private SubscribeUserService subscribeUserService;
	

	@POST
    @Produces( MediaType.APPLICATION_JSON )
	@Path("/subscribe")
	public String subscribeUser(@FormParam("name") String name, @FormParam("email") String email, @FormParam("preference") String preference){		
		subscribeUserService.subscribeUser(name, email, preference);
		return "{\"status\": \"sucess\"}";
	}    
	
}
