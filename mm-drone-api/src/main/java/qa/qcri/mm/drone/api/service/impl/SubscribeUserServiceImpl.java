package qa.qcri.mm.drone.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.drone.api.dao.SubscribeUserDao;
import qa.qcri.mm.drone.api.entity.SubscribeUser;
import qa.qcri.mm.drone.api.service.SubscribeUserService;
import qa.qcri.mm.drone.api.store.SubscribeFrequency;

@Service("subscribeUserService")
@Transactional
public class SubscribeUserServiceImpl implements SubscribeUserService {
	
	@Autowired
	private SubscribeUserDao subscribeUserDao;

	@Override
	public void subscribeUser(String name, String email, String preference) {
		SubscribeUser subscribeUser = new SubscribeUser(name, email, SubscribeFrequency.valueOf(preference));
		subscribeUserDao.save(subscribeUser);
	}
	
	@Override
	public List<SubscribeUser> getSubscribedUsers(SubscribeFrequency subscribeFrequency){
		return subscribeUserDao.getSubscribedUsers(subscribeFrequency);
    }
	
}
