package qa.qcri.mm.trainer.pybossa.service;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/22/13
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReportTemplateService {
    void saveReportItem(ReportTemplate reportTemplate);
    List<ReportTemplate> getReportTemplateByClientApp(Long clientAppID, Integer status);
    void updateReportItem(ReportTemplate reportTemplate);
    List<ReportTemplate> getReportTemplateSearchByTwittID(String field, String value);
	List<ReportTemplate> getReportTemplatesByUrl(String url);
    List<ReportTemplate> getReportTemplateWithUniqueKey(String uniqueKey);
}
