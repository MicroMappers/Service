package qa.qcri.mm.trainer.pybossa.service;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;

public interface TaskPybossaService {

	Task getTaskByIdandProjectId(Long taskID, Long projectId);
	List<Task> persist(List<Task> task);
}
