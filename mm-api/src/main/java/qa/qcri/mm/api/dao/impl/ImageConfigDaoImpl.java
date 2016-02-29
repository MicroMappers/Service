package qa.qcri.mm.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.ImageConfigDao;
import qa.qcri.mm.api.entity.ImageConfig;

@Repository
public class ImageConfigDaoImpl extends AbstractDaoImpl<ImageConfig, Long> implements ImageConfigDao {

    protected ImageConfigDaoImpl(){
        super(ImageConfig.class);
    }

    @Override
    public ImageConfig getByClientAppId(Long clientAppId) {
		return findByCriterionID(Restrictions.eq("clientApp.id", clientAppId));
    	
    }
}
