package qa.qcri.mm.trainer.pybossa.dao;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 9/30/14
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskPybossaDao extends AbstractDao<Task, Integer>{

	Task getTaskByIdandProjectId(Integer id, Integer projectId);
	Long getTaskCountByIdProjectIdAndState(Integer taskID, Integer projectId, String state);
}