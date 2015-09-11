package qa.qcri.mm.drone.api.dao;

import java.util.List;

import qa.qcri.mm.drone.api.entity.SubscribeUser;
import qa.qcri.mm.drone.api.store.SubscribeFrequency;

public interface SubscribeUserDao extends AbstractDao<SubscribeUser, String>  {

	List<SubscribeUser> getSubscribedUsers(SubscribeFrequency subscribeFrequency);
		
}
