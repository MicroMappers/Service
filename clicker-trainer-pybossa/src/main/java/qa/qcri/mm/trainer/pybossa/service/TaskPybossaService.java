package qa.qcri.mm.trainer.pybossa.service;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;

public interface TaskPybossaService {

	Task getTaskByIdandProjectId(Integer taskID, Integer projectId);
	Long  getTaskCountByIdProjectIdAndState(Integer taskID, Integer projectId, String state);
	List<Task> persist(List<Task> task);
}
