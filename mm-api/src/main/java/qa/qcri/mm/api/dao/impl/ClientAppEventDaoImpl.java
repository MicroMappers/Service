package qa.qcri.mm.api.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.ClientAppEventDao;
import qa.qcri.mm.api.entity.ClientAppEvent;

@Repository
public class ClientAppEventDaoImpl extends AbstractDaoImpl<ClientAppEvent, String> implements ClientAppEventDao {

    protected ClientAppEventDaoImpl(){
        super(ClientAppEvent.class);
    }

    @Override
	public void update(ClientAppEvent clientAppEvent) {
    	if(clientAppEvent.getCreated() == null ){
    		clientAppEvent.setCreated(new Date());
    	}
		saveOrUpdate(clientAppEvent);
	}
}
