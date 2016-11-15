package qa.qcri.mm.trainer.pybossa.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.ReportTemplateDao;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/22/13
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ReportTemplateDaoImpl extends AbstractDaoImpl<ReportTemplate, String> implements ReportTemplateDao {

    protected ReportTemplateDaoImpl(){
        super(ReportTemplate.class);
    }

    @Override
    public void saveReportItem(ReportTemplate reportTemplate) {
        Long templateCount =  findCountByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("taskQueueID",reportTemplate.getTaskQueueID()))
                .add(Restrictions.eq("answer", reportTemplate.getAnswer())));

        if(templateCount == null || templateCount == 0) {
        	if(reportTemplate.getCreated() == null){
        		reportTemplate.setCreated(new Date().toString());
        	}
            save(reportTemplate);
        }
    }

    @Override
    public void updateReportItem(ReportTemplate reportTemplate) {
        //To change body of implemented methods use File | Settings | File Templates.
        ReportTemplate reportItem = findByCriterionID(Restrictions.eq("reportTemplateID", reportTemplate.getReportTemplateID()));
        if(reportItem != null){
            reportItem.setStatus(reportTemplate.getStatus());
            if(reportItem.getCreated() == null){
            	reportItem.setCreated(new Date().toString());
        	}
            saveOrUpdate(reportItem);
        }
    }


    @Override
    public List<ReportTemplate> getReportTemplateByClientApp(Long clientAppID, Integer status) {
        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID",clientAppID))
                .add(Restrictions.eq("status", status)));

    }

    @Override
    public List<ReportTemplate> getReportTemplateSearchBy(String field, String value) {
        return findByCriteria(Restrictions.eq(field, value));

    }

    @Override
    public List<ReportTemplate> getReportTemplateWithUniqueKey(String field, Integer value, String uniqueKey ) {
        return findUniqueByCriteria(Restrictions.eq(field, value), uniqueKey);

    }
}
