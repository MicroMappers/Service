package qa.qcri.mm.api.service;

import org.json.simple.JSONArray;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/11/14
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientAppSourceService {

    boolean addExternalDataSouceWithClientAppID(String fileURL, Long clientAppID);
    void addExternalDataSourceWithClassifiedData(String fileURL, Long crisisID);

    void addExternalDataSouceWithAppType(String fileURL, Integer appType);
    void addExternalDataSouceWithPlatFormInd(String fileURL, Long micromappersID);

    void handleMapBoxDataSource(String jsonString);
    void handleMapBoxGistDataSource(String url);
	JSONArray getBoundsByLatLng(double[] coorindates);
}
