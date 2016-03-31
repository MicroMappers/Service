package qa.qcri.mm.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.ClientAppAnswer;
import qa.qcri.mm.api.service.ClientAppAnswerService;
import qa.qcri.mm.api.service.CrisisService;
import qa.qcri.mm.api.service.TaskQueueService;

@RestController
@RequestMapping("/clientapp_answer")
public class ClientAppAnswerController {
    
	@Autowired
    private ClientAppAnswerService clientAppAnswerService;
    
	@Autowired
	private TaskQueueService taskQueueService;
	
	@Autowired
	private CrisisService crisisService;
	
	@Value("${taggerAPI}")
	private String taggerAPI;
    
	@RequestMapping(value = "/{id}", method={RequestMethod.GET})
    public ClientAppAnswer updateClickerDetails(@PathVariable("id") long id) {
		ClientAppAnswer clientAppAnswer = clientAppAnswerService.getClientAppAnswer(id);
		return clientAppAnswer;
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method={RequestMethod.PUT})
    public void updateClickerDetails(@RequestBody ClientAppAnswer clientAppAnswer) {
    	if(clientAppAnswer != null) {
    		clientAppAnswerService.update(clientAppAnswer);
    	}
    }
}
