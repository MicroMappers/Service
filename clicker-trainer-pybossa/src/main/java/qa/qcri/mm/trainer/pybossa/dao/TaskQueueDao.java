package qa.qcri.mm.trainer.pybossa.dao;


import java.util.List;

import qa.qcri.mm.trainer.pybossa.entity.TaskQueue;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/25/13
 * Time: 7:29 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskQueueDao extends AbstractDao<TaskQueue, String>  {

    void createTaskQueue(TaskQueue taskQueue);
    void updateTaskQueue(TaskQueue taskQueue);
    List<TaskQueue> findTaskQueue(Long taskID, Long clientAppID, Long documentID);
    List<TaskQueue> findTaskQueueByDocument(Long clientAppID, Long documentID);
    List<TaskQueue> findTaskQueueByStatus(String column,Integer status);
    List<TaskQueue> findTaskQueueSetByStatus(Long clientAppID, Integer status);
    List<TaskQueue> findTaskQueueByTaskID(Long clientAppID, Long taskID);
    void deleteTaskQueue(Long taskQueueID);
    List<TaskQueue> findLatestTaskQueue(Long clientAppID)  ;
	List<TaskQueue> findTaskQueueByClientAppId(Long clientAppID);
}
