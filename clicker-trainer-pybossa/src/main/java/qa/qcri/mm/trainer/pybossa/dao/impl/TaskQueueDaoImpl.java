package qa.qcri.mm.trainer.pybossa.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.TaskQueueDao;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueue;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/25/13
 * Time: 8:10 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TaskQueueDaoImpl extends AbstractDaoImpl<TaskQueue, String> implements TaskQueueDao {

    protected TaskQueueDaoImpl(){
        super(TaskQueue.class);
    }

    @Override
    public void createTaskQueue(TaskQueue taskQueue) {
    	Date date = new Date();
    	if(taskQueue.getTaskQueueID() == null || taskQueue.getCreated() == null){
			taskQueue.setCreated(date);
    	}
    	taskQueue.setUpdated(date);
        save(taskQueue);
    }
    
    @Override
    public void updateTaskQueue(TaskQueue taskQueue) {
    	Date date = new Date();
    	if(taskQueue.getTaskQueueID() == null || taskQueue.getCreated() == null){
			taskQueue.setCreated(date);
    	}
    		taskQueue.setUpdated(date);
        saveOrUpdate(taskQueue);
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
    public Long findTaskQueueCountByStatus(String column, Integer status) {
        return findCountByCriteria(Restrictions.eq(column, status));  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override
    public List<TaskQueue> findTaskQueueByClientAppId(Long clientAppID) {
        return findByCriteria(Restrictions.eq("clientAppID", clientAppID));
    }

    @Override
    public List<TaskQueue> findTaskQueueSetByStatus(Long clientAppID, Integer status) {
        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("status", status)));
    }
    
    @Override
    public Long findTaskQueueSetCountByStatus(Long clientAppID, Integer status) {
        return findCountByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("status", status)));
    }

    @Override
    public List<TaskQueue> findTaskQueueByTaskID(Long clientAppID, Long taskID) {
        return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("clientAppID", clientAppID))
                .add(Restrictions.eq("taskID", taskID)));
    }

    @Override
    public void deleteTaskQueue(Long taskQueueID) {
        List<TaskQueue> taskQueues = findByCriteria(Restrictions.eq("taskQueueID", taskQueueID));
        if(taskQueues.size() > 0){
            delete(taskQueues.get(0));
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueue> findLatestTaskQueue(Long clientAppID) {
        Criterion criterion = Restrictions.eq("clientAppID",clientAppID);
        return getMaxOrderByCriteria(criterion, "updated");
    }
}
