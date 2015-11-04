package qa.qcri.mm.trainer.pybossa.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.TyphoonRubyTextGeoClickerDao;
import qa.qcri.mm.trainer.pybossa.entity.TyphoonRubyTextGeoClicker;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/20/13
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TyphoonRubyTextGeoClikcerDaoImpl extends AbstractDaoImpl<TyphoonRubyTextGeoClicker, String> implements TyphoonRubyTextGeoClickerDao {

    protected TyphoonRubyTextGeoClikcerDaoImpl(){
        super(TyphoonRubyTextGeoClicker.class);
    }
    
    @Override
    public List<TyphoonRubyTextGeoClicker> getTyphoonRubyTextGeoClickerByTaskId(Long taskId) {
       return  findByCriteria(Restrictions.eq("taskId", taskId));
    }

}
