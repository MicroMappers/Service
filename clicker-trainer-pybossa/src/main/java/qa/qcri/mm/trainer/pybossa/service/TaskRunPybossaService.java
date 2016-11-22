package qa.qcri.mm.trainer.pybossa.service;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;

public interface TaskRunPybossaService {
	
	public List<TaskRun> getTaskRunByIdandProjectId(Integer taskID, Integer projectId);
}
