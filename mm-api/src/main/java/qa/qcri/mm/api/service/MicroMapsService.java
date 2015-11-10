package qa.qcri.mm.api.service;

import java.util.List;

import org.json.simple.JSONArray;

import qa.qcri.mm.api.template.CrisisGISModel;
import qa.qcri.mm.api.template.MicroMapsCrisisModel;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 4/22/15
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MicroMapsService {

    List<MicroMapsCrisisModel> getAllCries();
    List<CrisisGISModel> getAllCrisis() throws Exception;
    JSONArray getAllCrisisJSONP() throws Exception;
    String getGeoClickerByClientApp(Long clientAppID) throws Exception;
    String generateTextClickerKML(Long clientAppID) throws Exception;
    String generateImageClickerKML(Long clientAppID) throws Exception;
    String generateAericalClickerKML(Long clientAppID) throws Exception;
    String getGeoClickerByCrisis(Long crisisID) throws Exception;
	String getGeoClickerByClientAppAndAfterCreatedDate(Long clientAppID, Long createdDate) throws Exception;

}
