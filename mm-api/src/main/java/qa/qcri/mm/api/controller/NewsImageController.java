package qa.qcri.mm.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.NewsImageService;

@RestController
@RequestMapping("/newsimage")
public class NewsImageController {
	
	@Autowired
	private NewsImageService newsImageService;
	
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
	public String startGdeltPull(@PathVariable("id") long clientAppID) throws Exception {
		newsImageService.setRunning(true);
		newsImageService.pull(clientAppID);
		return "Success";
	}
	
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
	public String endGdeltPull(@PathVariable("id") long clientAppID) throws Exception {
		newsImageService.setRunning(false);
		return "Success";
	}

}
