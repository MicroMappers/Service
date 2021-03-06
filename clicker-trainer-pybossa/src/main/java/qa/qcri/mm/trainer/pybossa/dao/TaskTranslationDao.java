package qa.qcri.mm.trainer.pybossa.dao;

import qa.qcri.mm.trainer.pybossa.entity.TaskTranslation;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author dan landy
 *
 */
public interface TaskTranslationDao extends AbstractDao<TaskTranslation, String>  {

	TaskTranslation findTranslationByID(Long translationId);
    TaskTranslation findTranslationByTaskID(Long taskId);
    List<TaskTranslation> findAllTranslationsByClientAppIdAndStatus(Long clientAppId, String status, Integer count);
    List<TaskTranslation> findAllTranslations();
    List<TaskTranslation> findAllTranslationsByTaskID(Long taskId);
    void createTaskTranslation(TaskTranslation taskTranslation);
    void saveOrUpdateTaskTranslation(TaskTranslation taskTranslation);
    int countAllTranslationsByOrderID(Integer orderId);
    int countAllTranslationsByDateAndStatus(Date fromDate, Date toDate, String status);
}
