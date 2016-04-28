package qa.qcri.mm.api.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.TaskQueueResponseDao;
import qa.qcri.mm.api.entity.TaskQueueResponse;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 1/18/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TaskQueueResponseDaoImpl extends AbstractDaoImpl<TaskQueueResponse, String> implements TaskQueueResponseDao {

    protected TaskQueueResponseDaoImpl(){
        super(TaskQueueResponse.class);
    }

    @Override
    public List<TaskQueueResponse> getTaskQueueResponseByTaskQueueID(Long taskQueueID) {
        return findByCriteria(Restrictions.eq("taskQueueID", taskQueueID));  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override
    public List<TaskQueueResponse> getTaskQueueResponseByClientAppIDAndStatus(Long clientAppID, Integer statusCodeType) {
    	
    	return getTaskQueueResponseByClientAppIDStatusAndCreated(clientAppID, statusCodeType, null);
    }
    
    @Override
    public List<TaskQueueResponse> getTaskQueueResponseByClientAppIDStatusAndCreated(Long clientAppID, Integer statusCodeType, Long createdDate) {
    	
    	Query query = null;
    	
    	if(createdDate!=null){
    		//Don't change java.sql to java.util
    		java.sql.Date date = new java.sql.Date(createdDate);
    		query = getCurrentSession().createSQLQuery("SELECT * FROM task_queue_response tqr JOIN task_queue tq ON tqr.task_queue_id = tq.id where tq.client_app_id = ?  and tq.status = ? and tqr.created >= ?").addEntity(TaskQueueResponse.class);
    		query.setParameter(2, date);
    	}
    	else{
    		query = getCurrentSession().createSQLQuery("SELECT * FROM task_queue_response tqr JOIN task_queue tq ON tqr.task_queue_id = tq.id where tq.client_app_id = ?  and tq.status = ?").addEntity(TaskQueueResponse.class);
    	}
    	query.setParameter(0, clientAppID);
    	query.setParameter(1, statusCodeType);
    	query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    	return query.list();
    }

    @Override
    public List<TaskQueueResponse> getTaskQueueResponseByTaskQueueIDBasedOnLastUpdate(Long taskQueueID, Date updated) {

        if(updated == null){
            return findByCriteria(Restrictions.conjunction()
                    .add(Restrictions.eq("taskQueueID", taskQueueID)));
        }
        else{
            return findByCriteria(Restrictions.conjunction()
                    .add(Restrictions.gt("created",updated))
                    .add(Restrictions.eq("taskQueueID", taskQueueID)));
        }

    }
}
