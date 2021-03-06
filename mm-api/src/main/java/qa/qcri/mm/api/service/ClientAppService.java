package qa.qcri.mm.api.service;



import java.util.List;

import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.template.ClientAppModel;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/19/13
 * Time: 11:59 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientAppService {

    ClientApp findClientAppByID(String columnName, Long id);
    ClientApp findClientAppByCriteria(String columnName, String value);
    List<ClientApp> getAllClientAppByClientID(Long clientID);
    List<ClientApp> findClientAppByStatus(Integer status);
    List<ClientApp> getAllClientAppByCrisisID(Long crisisID);
    List<ClientApp> findClientAppByAppType(String columnName, Integer typeID);
    void updateClientAppByShortName(String shortName, Integer status);
    ClientApp getClientAppByCrisisAndAttribute(Long crisisID, Long attributeID);
    void deactivateClientAppByCrisis(Long crisisID) throws Exception;
    void deactivateClientAppByAttribute(Long crisisID, Long attributeID) throws Exception;
    List<ClientAppModel> getAllActiveClientApps();
    String enableForClientAppStatusByCrisisID(Long crisisID, Integer status);
    List<ClientApp> getClientAppByPlatformAppID(Long platformAppID);
    List<ClientApp> getAllClientApp();
	ClientApp getClientAppById(Long id);
	List<ClientApp> getAvailableClientApp();
	ClientAppModel updateClientApp(ClientAppModel model);
}
