package qa.qcri.mm.drone.api.dao.impl;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.drone.api.dao.SubscribeUserDao;
import qa.qcri.mm.drone.api.entity.SubscribeUser;

@Repository
public class SubscribeUserDaoImpl extends AbstractDaoImpl<SubscribeUser, String> implements SubscribeUserDao {

	protected SubscribeUserDaoImpl() {
		super(SubscribeUser.class);
	}

}
