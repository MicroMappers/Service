package qa.qcri.mm.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.CrisisDao;
import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.service.CrisisService;

@Service("crisisService")
@Transactional(readOnly = true)
public class CrisisServiceImpl implements CrisisService{

	@Autowired
	private CrisisDao crisisDao;
	
	@Override
	public List<Crisis> getAllCrisis() {
		return crisisDao.getAllCrisis();
	}
	
	@Override
    public List<Crisis> findCrisisByCrisisID(Long crisisID) {
        return crisisDao.findCrisisByCrisisID(crisisID);
    }
	
	@Override
    public List<Crisis> findCrisisByClientAppID(Long clientAppID) {
        return crisisDao.findCrisisByClientAppID(clientAppID);
    }
	
}
