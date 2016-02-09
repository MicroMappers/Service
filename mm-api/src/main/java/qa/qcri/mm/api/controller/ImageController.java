package qa.qcri.mm.api.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.SlicedImageService;

@RestController
@RequestMapping("/image")
public class ImageController {
    protected static Logger logger = Logger.getLogger("ImageController");

    @Autowired
    SlicedImageService slicedImageService;
    
    @RequestMapping(value = "/slice/{clientAppId}", method = RequestMethod.GET)
    public void sliceImage(@PathVariable("clientAppId") Long clientAppId) {
    	slicedImageService.sliceImage(clientAppId);
    	
    }

}
