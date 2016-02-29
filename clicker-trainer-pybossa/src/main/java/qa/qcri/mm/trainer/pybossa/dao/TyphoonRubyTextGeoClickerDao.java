package qa.qcri.mm.trainer.pybossa.dao;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entity.TyphoonRubyTextGeoClicker;

/**
 * 
 * @author Aman
 *
 */
public interface TyphoonRubyTextGeoClickerDao extends AbstractDao<TyphoonRubyTextGeoClicker, String> {

	List<TyphoonRubyTextGeoClicker> getTyphoonRubyTextGeoClickerByTaskId(Long taskId);

    
}
