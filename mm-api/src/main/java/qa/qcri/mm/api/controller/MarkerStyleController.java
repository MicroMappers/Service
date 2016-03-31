package qa.qcri.mm.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.MarkerStyle;
import qa.qcri.mm.api.service.MarkerStyleService;

@RestController
@RequestMapping("/marker_style")
public class MarkerStyleController {
    
	@Autowired
    private MarkerStyleService markerStyleService;
    
	@RequestMapping(value = "/{id}", method={RequestMethod.GET})
    public MarkerStyle getMarkerStyle(@PathVariable("id") long id) {
		MarkerStyle markerStyle = markerStyleService.findByClientAppID(id);
		return markerStyle;
    }
	
	@RequestMapping(method={RequestMethod.POST})
    public void saveMarkerStyle(@RequestBody MarkerStyle markerStyle) {
    	if(markerStyle != null) {
    		markerStyleService.save(markerStyle);
    	}
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/{id}", method={RequestMethod.PUT})
    public void updateMarkerStyle(@PathVariable("id") long id, @RequestBody MarkerStyle markerStyle) {
    	if(markerStyle != null) {
    		markerStyleService.update(markerStyle);
    	}
    }
}
