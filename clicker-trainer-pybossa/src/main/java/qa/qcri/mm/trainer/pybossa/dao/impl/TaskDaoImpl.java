package qa.qcri.mm.trainer.pybossa.dao.impl;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.TaskDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Task;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 9/30/14
 * Time: 8:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TaskDaoImpl extends AbstractDaoImplForPybossa<Task, String> implements TaskDao {

    protected TaskDaoImpl(){
        super(Task.class);
    }

}
