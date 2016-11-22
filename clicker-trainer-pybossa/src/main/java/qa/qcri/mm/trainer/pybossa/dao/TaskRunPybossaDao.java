package qa.qcri.mm.trainer.pybossa.dao;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;

public interface TaskRunPybossaDao extends AbstractDao<TaskRun, Integer>{
	
	List<TaskRun> getTaskRunByIdandProjectId(Integer taskID, Integer projectId);
}