package qa.qcri.mm.api.service;

import java.util.List;

import qa.qcri.mm.api.entity.MarkerStyle;

public interface MarkerStyleService {
	List<MarkerStyle> findByAppType(String appType);
    MarkerStyle findByClientAppID(long clientAppID);
	void update(MarkerStyle markerStyle);
	void save(MarkerStyle markerStyle);
}
