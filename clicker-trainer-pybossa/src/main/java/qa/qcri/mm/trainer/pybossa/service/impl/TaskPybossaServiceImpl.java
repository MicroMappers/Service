package qa.qcri.mm.trainer.pybossa.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.trainer.pybossa.dao.TaskDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;
import qa.qcri.mm.trainer.pybossa.service.TaskPybossaService;

@Service("TaskPybossaService")
@Transactional(readOnly = true)
public class TaskPybossaServiceImpl implements TaskPybossaService {

    private static Logger logger = Logger.getLogger(TaskPybossaServiceImpl.class);

    @Autowired
    private TaskDao taskDao;


    @Override
    public Task getTaskByIdandProjectId(Integer taskID, Integer projectId) {
        return taskDao.getTaskByIdandProjectId(taskID, projectId); 
    }


	@Override
	public List<Task> persist(List<Task> tasks) {
		for (Task task : tasks) {
			String dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
			task.setCreated(dateTime);
			taskDao.save(task);
		}
		return tasks;
	}


	@Override
	public Long getTaskCountByIdProjectIdAndState(Integer taskID, Integer projectId, String state) {
		return taskDao.getTaskCountByIdProjectIdAndState(taskID, projectId, state); 
	}
}
