package qa.qcri.mm.trainer.pybossa.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.trainer.pybossa.dao.ReportTemplateDao;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;
import qa.qcri.mm.trainer.pybossa.service.ReportTemplateService;
import qa.qcri.mm.trainer.pybossa.store.LookupCode;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/22/13
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("reportTemplateService")
@Transactional(readOnly = false)
public class ReportTemplateServiceImpl implements ReportTemplateService {

    private static Logger logger = Logger.getLogger(ReportTemplateServiceImpl.class);

    @Autowired
    private ReportTemplateDao reportTemplateDao;

    @Override
    @Transactional(readOnly = false, propagation= Propagation.REQUIRES_NEW)
    public void saveReportItem(ReportTemplate reportTemplate) {

        try{
                reportTemplateDao.saveReportItem(reportTemplate);

        }
        catch(Exception ex){
            logger.error("saveReportItem exception");
            logger.error(ex.getMessage());
            System.out.println("saveReportItem exception : " + ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }

        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportTemplate> getReportTemplateWithUniqueKey(String uniqueKey) {
        return reportTemplateDao.getReportTemplateWithUniqueKey("status", LookupCode.TEMPLATE_IS_READY_FOR_EXPORT, uniqueKey);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportTemplate> getReportTemplateByClientApp(Long clientAppID, Integer status) {
        return reportTemplateDao.getReportTemplateByClientApp(clientAppID, status);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Transactional(readOnly = false)
    public void updateReportItem(ReportTemplate reportTemplate) {
        reportTemplateDao.updateReportItem(reportTemplate);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportTemplate> getReportTemplateSearchByTwittID(String field, String value) {
        return reportTemplateDao.getReportTemplateSearchBy(field, value);  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override
    public List<ReportTemplate> getReportTemplatesByUrl(String url) {
        return reportTemplateDao.getReportTemplateSearchBy("url", url); 
    }

    private boolean isNumeric(String data){
        boolean returnValue = false;
        try{
            Long tweetID = Long.parseLong(data);
            returnValue = true;
        }
        catch(Exception e){
            System.out.println("isNumeric exception on TweetID: " + data);
        }

        return returnValue;
    }
}
