package qa.qcri.mm.trainer.pybossa.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.trainer.pybossa.dao.TaskRunPybossaDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;
import qa.qcri.mm.trainer.pybossa.service.TaskRunPybossaService;

@Service("TaskRunPybossaService")
@Transactional(readOnly = true)
public class TaskRunPybossaServiceImpl implements TaskRunPybossaService {

    private static Logger logger = Logger.getLogger(TaskRunPybossaServiceImpl.class);

    @Autowired
    private TaskRunPybossaDao taskRunPybossaDao;
    
    @Override
    public List<TaskRun> getTaskRunByIdandProjectId(Integer taskID, Integer projectId) {
        return taskRunPybossaDao.getTaskRunByIdandProjectId(taskID, projectId); 
    }
}
