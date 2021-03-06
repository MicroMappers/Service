package qa.qcri.mm.api.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.ClientAppDao;
import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.store.StatusCodeType;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/18/13
 * Time: 6:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ClientAppDaoImpl extends AbstractDaoImpl<ClientApp, String> implements ClientAppDao {

    protected ClientAppDaoImpl(){
        super(ClientApp.class);
    }

    @Override
    public ClientApp findClientAppByID(String columnName, Long id) {
        ClientApp appCfg = findByCriterionID(Restrictions.eq(columnName, id));
        return appCfg;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ClientApp findClientAppByCriteria(String columnName, String value) {
        ClientApp appCfg = findByCriterionID(Restrictions.eq(columnName, value));
        return appCfg;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ClientApp> findAllClientApp(Long clientID) {
        List<ClientApp> clientAppList =  findByCriteria(Restrictions.eq("clientID", clientID));

        return clientAppList;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ClientApp> findAllClientAppByCrisisID(Long crisisID) {
        return findByCriteria(Restrictions.eq("crisisID", crisisID));  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ClientApp> findAllClientAppByStatus(Integer status) {
        return findByCriteria(Restrictions.eq("status", status));  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override
    public ClientApp getClientAppById(Long id) {
    	ClientApp clientApp = null;
        List<ClientApp> findByCriteria = findByCriteria(Restrictions.eq("id", id));
        if(!findByCriteria.isEmpty()) {
        	clientApp = findByCriteria.get(0);
        }
        return clientApp;
    }

    @Override
    public List<ClientApp> findClientAppByPlatFormID(Long platformAppID) {
        return findByCriteria(Restrictions.eq("platformAppID", platformAppID));  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ClientApp> findClientAppByCrisisAndAttribute(Long crisisID, Long attributeID) {

        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("crisisID",crisisID))
                .add(Restrictions.eq("nominalAttributeID",attributeID)));
    }

    @Override
    public void updateClientAppStatus(ClientApp clientApp, Integer status) {
        //To change body of implemented methods use File | Settings | File Templates.
        ClientApp thisClientApp = findClientAppByID("clientAppID", clientApp.getClientAppID());

        if(thisClientApp!= null){
            thisClientApp.setStatus(status);
            if(thisClientApp.getCreated() == null){
            	thisClientApp.setCreated(new Date());
            }
            saveOrUpdate(thisClientApp);
        }

    }

    @Override
    public List<ClientApp> getAllActiveClientApp() {
        return findByCriteria(Restrictions.disjunction()
                .add(Restrictions.eq("status", StatusCodeType.AIDR_ONLY))
                .add(Restrictions.eq("status", StatusCodeType.MICROMAPPER_ONLY))
                .add(Restrictions.eq("status", StatusCodeType.AIDR_MICROMAPPER_BOTH)));
    }
    
    @Override
    public List<ClientApp> getAvailableClientApp() {
        return findByCriteria(Restrictions.disjunction()
                .add(Restrictions.eq("status", StatusCodeType.AIDR_ONLY))
                .add(Restrictions.eq("status", StatusCodeType.MICROMAPPER_ONLY))
                .add(Restrictions.eq("status", StatusCodeType.AIDR_MICROMAPPER_BOTH))
                .add(Restrictions.eq("status", StatusCodeType.CLIENT_APP_PENDING)));
    }


    @Override
    public List<ClientApp> findClientAppByAppType(String columnName, Integer typeID) {

        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("appType",typeID))
                .add(Restrictions.not(Restrictions.eq("status",0))));
    }


    @Override
    public List<ClientApp> findAllClientApps() {

        return getAll();
    }

	@Override
	public void update(ClientApp clientApp) {
		if(clientApp.getCreated() == null){
			clientApp.setCreated(new Date());
		}
		saveOrUpdate(clientApp);
		
	}


}
