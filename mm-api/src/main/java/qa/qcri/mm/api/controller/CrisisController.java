package qa.qcri.mm.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.service.CrisisService;
import qa.qcri.mm.api.template.CrisisModel;

@RestController
@RequestMapping("/crisis")
public class CrisisController {

	@Autowired
	private CrisisService crisisService;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<CrisisModel> getAllCrisis() throws Exception {
		List<Crisis> allCrisis = crisisService.getAllCrisis();
		List<CrisisModel> crisisModels = new ArrayList<>();
		for(Crisis crisis : allCrisis) {
			CrisisModel crisisModel = new CrisisModel(crisis.getId(),
					crisis.getCrisisID(), crisis.getClientAppID(),
					crisis.getDisplayName(), crisis.getDescription(),
					crisis.getActivationStart(), crisis.getActivationEnd(),
					crisis.getClickerType(), crisis.getBounds());
			crisisModels.add(crisisModel);
		}
		return crisisModels;
	}
}
