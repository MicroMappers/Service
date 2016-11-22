package qa.qcri.mm.trainer.pybossa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.trainer.pybossa.dao.ClientAppEventDao;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppEvent;
import qa.qcri.mm.trainer.pybossa.service.ClientAppEventService;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 12/5/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("clientAppEventService")
@Transactional(readOnly = true)
public class ClientAppEventServiceImpl implements ClientAppEventService {
    @Autowired
    ClientAppEventDao clientAppEventDao;

    @Override
    public ClientAppEvent getNextSequenceClientAppEvent(Long clientAppID) {
        ClientAppEvent nextRunner = null;
        List<ClientAppEvent> clientAppEvents =  clientAppEventDao.getClientAppEventByClientAPP(clientAppID);

        for (ClientAppEvent clientAppEvent : clientAppEvents) {

            Long eventID = clientAppEvent.getEventID();

            List<ClientAppEvent> events =  clientAppEventDao.getClientAppEventByEvent(eventID);
            nextRunner = getNextSequenceClientApp(events, clientAppEvent) ;
            if(nextRunner != null){
                break;
            }
		}

        return nextRunner;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private ClientAppEvent getNextSequenceClientApp(List<ClientAppEvent> eventList, ClientAppEvent currentAppEvent){
        ClientAppEvent nextEventApp = null;
        
		for (ClientAppEvent clientAppEvent : eventList) {
		
		    if(!clientAppEvent.getClientAppEventID().equals(currentAppEvent.getClientAppEventID())){
		        if(clientAppEvent.getSequence() > currentAppEvent.getSequence() ){
		            // yes, it is next sequence!
		            nextEventApp = clientAppEvent;
		        }
		    }
		
		}
        return nextEventApp;
    }
}
