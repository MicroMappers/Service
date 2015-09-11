package qa.qcri.mm.drone.api.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.drone.api.dao.SubscribeUserDao;
import qa.qcri.mm.drone.api.entity.SubscribeUser;
import qa.qcri.mm.drone.api.store.LookUp;
import qa.qcri.mm.drone.api.store.SubscribeFrequency;

@Repository
public class SubscribeUserDaoImpl extends AbstractDaoImpl<SubscribeUser, String> implements SubscribeUserDao {

	protected SubscribeUserDaoImpl() {
		super(SubscribeUser.class);
	}
	
	@Override
	public List<SubscribeUser> getSubscribedUsers(SubscribeFrequency subscribeFrequency){
    	Criterion criterion = Restrictions.eq("status", LookUp.DRONE_VIDEO_APPROVED);
    	Criteria criteria = getCurrentSession().createCriteria(SubscribeUser.class);
        criteria.add(Restrictions.eq("subscribeFrequency", subscribeFrequency));
        return criteria.list();
    }

}
