package qa.qcri.mm.trainer.pybossa.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.TaskPybossaDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;

/**
 * User: kushal
 */
@Repository
public class TaskPybossaDaoImpl extends AbstractDaoImplForPybossa<Task, Integer> implements TaskPybossaDao {

    protected TaskPybossaDaoImpl(){
        super(Task.class);
    }
    
    @Override
    public Task getTaskByIdandProjectId(Integer id, Integer projectId) {
        return findByCriterionID(Restrictions.conjunction()
                .add(Restrictions.eq("id",id))
                .add(Restrictions.eq("project.id", projectId)));
    }

	@Override
	public Long getTaskCountByIdProjectIdAndState(Integer id, Integer projectId, String state) {
		return findCountByCriteria(Restrictions.conjunction()
                .add(Restrictions.eq("id",id))
                .add(Restrictions.eq("project.id", projectId))
                .add(Restrictions.eq("state", state)));
	}
}
