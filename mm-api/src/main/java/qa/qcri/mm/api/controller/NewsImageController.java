package qa.qcri.mm.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.NewsImageService;
import qa.qcri.mm.api.util.MessageConstants;

@RestController
@RequestMapping("/newsimage")
public class NewsImageController {
	
	@Autowired
	private NewsImageService newsImageService;
	
	@RequestMapping(value = "/start/{id}", method = RequestMethod.POST)
	public String startGdeltPull(@PathVariable("id") long clientAppID) throws Exception {
		if(newsImageService.getGdeltPullStatus()){
			return MessageConstants.SERVICE_ALREADY_RUNNING;
		}
		newsImageService.startFetchingDataFromGdelt(clientAppID);
		return MessageConstants.SUCCESS;
	}
	
	@RequestMapping(value = "/stop/{id}", method = RequestMethod.POST)
	public String endGdeltPull(@PathVariable("id") long clientAppID) throws Exception {
		newsImageService.stopFetchingDataFromGdelt(clientAppID);
		return MessageConstants.SUCCESS;
	}

}
