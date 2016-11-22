package qa.qcri.mm.trainer.pybossa.service;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.format.impl.GeoJsonOutputModel;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/22/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReportProductService {

    void generateReportTemplateFromExternalSource() throws Exception;
    void generateCSVReportForTextGeoClicker() throws Exception;
    void generateCSVReportForImageGeoClicker() throws Exception;
    void generateGeoJsonForESRI(List<GeoJsonOutputModel> geoJsonOutputModels) throws Exception;
    void generateMapBoxTemplateForAerialClicker() throws Exception;
    void generateGeoJsonForClientApp(ClientApp clientApp) throws Exception;
}
