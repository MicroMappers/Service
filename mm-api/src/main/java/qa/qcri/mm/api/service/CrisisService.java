package qa.qcri.mm.api.service;

import java.util.List;

import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.template.ClientAppModel;

public interface CrisisService {

	public List<Crisis> getAllCrisis();

	List<Crisis> findCrisisByCrisisID(Long crisisID);

	List<Crisis> findCrisisByClientAppID(Long clientAppID);
	
	Crisis createCrisisForClientApp(ClientAppModel model);

}
