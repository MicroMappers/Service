package qa.qcri.mm.api.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.MarkerStyleDao;
import qa.qcri.mm.api.entity.MarkerStyle;

/**
 * Created by jlucas on 5/19/15.
 */
@Repository
public class MarkerStyleDaoImpl extends AbstractDaoImpl<MarkerStyle, String> implements MarkerStyleDao {

    protected MarkerStyleDaoImpl(){
        super(MarkerStyle.class);
    }


    @Override
    public List<MarkerStyle> findByAppType(String appType) {

        return findByCriteria(Restrictions.eq("appType", appType));
    }

    @Override
    public List<MarkerStyle> findByClientAppID(long clientAppID) {
        return findByCriteria(Restrictions.eq("clientAppID", clientAppID));
    }

    @Override
	public void update(MarkerStyle markerStyle) {
		saveOrUpdate(markerStyle);
	}
}
