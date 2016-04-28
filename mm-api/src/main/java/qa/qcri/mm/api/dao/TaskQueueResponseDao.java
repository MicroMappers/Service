package qa.qcri.mm.api.dao;

import java.util.Date;
import java.util.List;

import qa.qcri.mm.api.entity.TaskQueueResponse;
import qa.qcri.mm.api.store.StatusCodeType;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 1/18/14
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskQueueResponseDao {

    List<TaskQueueResponse> getTaskQueueResponseByTaskQueueID(Long taskQueueID);
    List<TaskQueueResponse> getTaskQueueResponseByTaskQueueIDBasedOnLastUpdate(Long taskQueueID, Date updated);
	List<TaskQueueResponse> getTaskQueueResponseByClientAppIDAndStatus(Long clientAppID, Integer statusCodeType);
	List<TaskQueueResponse> getTaskQueueResponseByClientAppIDStatusAndCreated(Long clientAppID, Integer statusCodeType, Long createdDate);
}
