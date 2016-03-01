package qa.qcri.mm.api.service;


import java.util.List;

import qa.qcri.mm.api.entity.TaskQueue;
import qa.qcri.mm.api.entity.TaskQueueResponse;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/25/13
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskQueueService {

    List<TaskQueue> getTaskQueueSet(Long taskID, Long clientAppID, Long documentID);
    List<TaskQueue> getTaskQueueByDocument(Long clientAppID, Long documentID);
    List<TaskQueue> getTaskQueueByClientAppStatus(Long clientAppID, Integer status);
    List<TaskQueue> getTaskQueueByClientApp(Long clientAppID);
    List<TaskQueue> getTotalNumberOfTaskQueue(Long clientAppID);
    List<TaskQueueResponse> getTaskQueueResponseByClientApp(String shortName);
	List<TaskQueue> getAll();
	Long getTaskQueueCountByClientAppId(Long clientAppID);
	Long getTaskQueueCountByClientAppIdAndStatus(Long clientAppID, Integer status);

}
