package qa.qcri.mm.trainer.pybossa.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.TaskRunPybossaDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;

/**
 * User: Kushal
 */
@Repository
public class TaskRunPybossaDaoImpl extends AbstractDaoImplForPybossa<TaskRun, Integer> implements TaskRunPybossaDao {

    protected TaskRunPybossaDaoImpl(){
        super(TaskRun.class);
    }

	@Override
	public List<TaskRun> getTaskRunByIdandProjectId(Integer taskID, Integer projectId) {
		return findByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("id",taskID))
                .add(Restrictions.eq("project.id", projectId)));
	}
    
}
