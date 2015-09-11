package qa.qcri.mm.drone.api.service;

import java.util.List;

import qa.qcri.mm.drone.api.entity.SubscribeUser;
import qa.qcri.mm.drone.api.store.SubscribeFrequency;

public interface SubscribeUserService {
	public void subscribeUser(String name, String email, String preference);

	List<SubscribeUser> getSubscribedUsers(SubscribeFrequency subscribeFrequency);
}
