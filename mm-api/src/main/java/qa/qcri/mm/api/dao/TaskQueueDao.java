package qa.qcri.mm.api.dao;




import java.util.HashMap;
import java.util.List;

import qa.qcri.mm.api.entity.TaskQueue;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/25/13
 * Time: 7:29 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskQueueDao extends AbstractDao<TaskQueue, Long>  {

    List<TaskQueue> findTaskQueue(Long taskID, Long clientAppID, Long documentID);
    List<TaskQueue> findTaskQueueByDocument(Long clientAppID, Long documentID);
    List<TaskQueue> findTaskQueueByStatus(String column, Integer status);
    List<TaskQueue> findTaskQueueSetByStatus(Long clientAppID, Integer status);
    List<TaskQueue> findTaskQueueSetByclientApp(Long clientAppID);
    List<TaskQueue> findLatestTaskQueue(Long clientAppID);
    Long getTotalTaskInQueueByclientAppId(Long clientAppID);
	Long getTaskQueueCountByclientAppAndStatus(Long clientAppID, Integer status);
	List<Object> getTotalTaskInQueue();
	List<Object> getTotalTaskInQueueByStatus(Integer status);
}
