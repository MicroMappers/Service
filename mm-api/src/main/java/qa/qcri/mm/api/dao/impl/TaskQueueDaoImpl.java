package qa.qcri.mm.api.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.TaskQueueDao;
import qa.qcri.mm.api.entity.TaskQueue;
import qa.qcri.mm.api.store.StatusCodeType;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/25/13
 * Time: 8:10 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TaskQueueDaoImpl extends AbstractDaoImpl<TaskQueue, Long> implements TaskQueueDao {

    protected TaskQueueDaoImpl(){
        super(TaskQueue.class);
    }


    @Override
    public List<TaskQueue> findTaskQueue(Long taskID, Long clientAppID, Long documentID) {

        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("taskID",taskID))
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("documentID", documentID)));
                  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueue> findTaskQueueByDocument(Long clientAppID, Long documentID) {
        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("documentID", documentID)));
    }

    @Override
    public List<TaskQueue> findTaskQueueByStatus(String column, Integer status) {
        return findByCriteria(Restrictions.eq(column, status));  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueue> findTaskQueueSetByStatus(Long clientAppID, Integer status) {
        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("status", status)));
    }

    @Override
    public List<TaskQueue> findTaskQueueSetByclientApp(Long clientAppID) {
        return findByCriteria(Restrictions.eq("clientAppID", clientAppID));
    }
    
    /* (non-Javadoc)
     * total task in queue by clientappID where status is not TASK_ABANDONED
     */
    @Override
    public Long getTotalTaskInQueueByclientAppId(Long clientAppID) {
    	Criteria criteria = getCurrentSession().createCriteria(TaskQueue.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.ne("status", StatusCodeType.TASK_ABANDONED)));
		Long result = (Long) criteria.uniqueResult();
		return result;
    }
    
    @Override
    public List<Object> getTotalTaskInQueue() {
    	Criteria criteria = getCurrentSession().createCriteria(TaskQueue.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("clientAppID"))
				.add(Projections.rowCount())
				);
		criteria.add(Restrictions.ne("status", StatusCodeType.TASK_ABANDONED));
		List<Object> result = criteria.list();
		return result;
    }
    
    @Override
    public List<Object> getTotalTaskInQueueByStatus(Integer status) {
    	Criteria criteria = getCurrentSession().createCriteria(TaskQueue.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("clientAppID"))
				.add(Projections.rowCount())
				);
		criteria.add(Restrictions.eq("status", status));
		List<Object> result = criteria.list();
		return result;
    }
    
    @Override
    public Long getTaskQueueCountByclientAppAndStatus(Long clientAppID, Integer status) {
    	Criteria criteria = getCurrentSession().createCriteria(TaskQueue.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("status", status)));
		Long result = (Long) criteria.uniqueResult();
		return result;
    }
    
    @Override
    public List<TaskQueue> findLatestTaskQueue(Long clientAppID) {
        Criterion criterion = Restrictions.eq("clientAppID",clientAppID);
        return getMaxOrderByCriteria(criterion, "updated");
    }
    
    @Override
    public List<TaskQueue> getAll() {
        return getAll();
    }

}
