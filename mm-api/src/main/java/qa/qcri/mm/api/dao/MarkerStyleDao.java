package qa.qcri.mm.api.dao;

import java.util.List;

import qa.qcri.mm.api.entity.MarkerStyle;

/**
 * Created by jlucas on 5/19/15.
 */
public interface MarkerStyleDao extends AbstractDao<MarkerStyle, String>  {

    List<MarkerStyle> findByAppType(String appType);
    List<MarkerStyle> findByClientAppID(long clientAppID);
	void update(MarkerStyle markerStyle);

}