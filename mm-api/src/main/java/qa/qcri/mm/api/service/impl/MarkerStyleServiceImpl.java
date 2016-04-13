package qa.qcri.mm.api.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.MarkerStyleDao;
import qa.qcri.mm.api.entity.MarkerStyle;
import qa.qcri.mm.api.service.MarkerStyleService;

@Service("markerStyleService")
@Transactional
public class MarkerStyleServiceImpl implements MarkerStyleService{

    protected static Logger logger = Logger.getLogger("markerStyleService");

    @Autowired
    MarkerStyleDao markerStyleDao;

	@Override
	public List<MarkerStyle> findByAppType(String appType) {
		return markerStyleDao.findByAppType(appType);
	}

	@Override
	public MarkerStyle findByClientAppID(long clientAppID) {
		List<MarkerStyle> markerStyles = markerStyleDao.findByClientAppID(clientAppID);
		if(markerStyles.size() > 0)
            return markerStyles.get(0);
        return null;
	}
	
	@Override
    public void update(MarkerStyle markerStyle) {
		markerStyleDao.update(markerStyle);
    }
	
	@Override
    public void save(MarkerStyle markerStyle) {
		markerStyleDao.create(markerStyle);
    }
}
