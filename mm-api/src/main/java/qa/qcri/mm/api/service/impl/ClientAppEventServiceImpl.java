package qa.qcri.mm.api.service.impl;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.ClientAppEventDao;
import qa.qcri.mm.api.entity.ClientAppEvent;
import qa.qcri.mm.api.service.ClientAppEventService;


@Service
@Transactional
public class ClientAppEventServiceImpl implements ClientAppEventService{

    protected static Logger logger = Logger.getLogger("ClientAppEventService");

    @Autowired
    ClientAppEventDao clientAppEventDao;
	
	@Override
    public void update(ClientAppEvent clientAppEvent) {
		clientAppEventDao.update(clientAppEvent);
    }
	
	@Override
    public void save(ClientAppEvent clientAppEvent) {
		clientAppEventDao.saveOrUpdate(clientAppEvent);
    }
	
	@Override
    public ClientAppEvent getClientAppEvent(Long clientAppId) {
		return clientAppEventDao.findByCriterionID(Restrictions.eq("clientAppID", clientAppId));
    }
}
