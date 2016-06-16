package qa.qcri.mm.trainer.pybossa.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import qa.qcri.mm.trainer.pybossa.dao.TaskTranslationDao;
import qa.qcri.mm.trainer.pybossa.entity.TaskTranslation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author dan landy
 *
 */
@Repository
public class TaskTranslationDaoImpl extends AbstractDaoImpl<TaskTranslation, String> implements TaskTranslationDao {

    protected TaskTranslationDaoImpl(){
        super(TaskTranslation.class);
    }

	@Override
	public TaskTranslation findTranslationByID(Long translationId) {
		TaskTranslation translation = findByCriterionID(Restrictions.eq("translationId", translationId));
        return translation;  
	}


    public TaskTranslation findTranslationByTaskID(Long taskId) {
        TaskTranslation translation = findByCriterionID(Restrictions.eq("taskId", taskId));
        return translation;
    }

    public List<TaskTranslation> findAllTranslationsByTaskID(Long taskId) {
        List<TaskTranslation> translations = findByCriteria(Restrictions.eq("taskId", taskId));
        return translations;
    }

    public List<TaskTranslation> findAllTranslationsByClientAppIdAndStatus(Long clientAppId, String status, Integer count) {
        Map map = new HashMap();
        map.put("clientAppId", clientAppId);
        map.put("status", status);
        List<TaskTranslation> list = findByCriteria(Restrictions.allEq(map), count);
        return list;
    }

    public List<TaskTranslation> findAllTranslations() {
        Criteria criteria = getCurrentSession().createCriteria(TaskTranslation.class);
        return criteria.list();

    }

	@Override
	public void createTaskTranslation(TaskTranslation taskTranslation) {
		if(taskTranslation.getCreated() == null){
			taskTranslation.setCreated(new Date());
		}
		save(taskTranslation);
	}

	@Override
	public void saveOrUpdateTaskTranslation( TaskTranslation taskTranslation) {
		if(taskTranslation.getCreated() == null){
			taskTranslation.setCreated(new Date());
		}
		saveOrUpdate(taskTranslation);
	}

    @Override
    public int countAllTranslationsByOrderID(Integer orderId) {
        List<TaskTranslation> translations = findByCriteria(Restrictions.eq("twbOrderId", new Long(orderId)));
        return translations.size();
    }

    @Override
    public int countAllTranslationsByDateAndStatus(Date fromDate, Date toDate, String status) {

        List<TaskTranslation> translations = findByCriteria(Restrictions.conjunction()
                .add(Restrictions.between("created", fromDate, toDate))
                .add(Restrictions.eq("status", status)));
        return translations.size();
    }
}
