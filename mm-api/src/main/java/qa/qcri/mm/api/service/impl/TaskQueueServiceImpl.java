package qa.qcri.mm.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.TaskQueueDao;
import qa.qcri.mm.api.dao.TaskQueueResponseDao;
import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.entity.TaskQueue;
import qa.qcri.mm.api.entity.TaskQueueResponse;
import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.service.TaskQueueService;
import qa.qcri.mm.api.store.StatusCodeType;

@Service("taskStatusLookUpService")
@Transactional(readOnly = true)
public class TaskQueueServiceImpl implements TaskQueueService {

    @Autowired
    private TaskQueueDao taskQueueDao;

    @Autowired
    private TaskQueueResponseDao taskQueueResponseDao;

    @Autowired
    private ClientAppService clientAppService;

    @Override
    public List<TaskQueue> getTaskQueueSet(Long taskID, Long clientAppID, Long documentID) {
        return taskQueueDao.findTaskQueue(taskID,clientAppID, documentID );  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueue> getTaskQueueByDocument(Long clientAppID, Long documentID) {
        return taskQueueDao.findTaskQueueByDocument(clientAppID, documentID );  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueue> getTaskQueueByClientAppStatus(Long clientAppID, Integer status) {
        return taskQueueDao.findTaskQueueSetByStatus(clientAppID, status);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueue> getTaskQueueByClientApp(Long clientAppID) {
        return taskQueueDao.findTaskQueueSetByclientApp(clientAppID);
    }
    
    @Override
    public Long getTotalTaskInQueueByclientAppId(Long clientAppID) {
        return taskQueueDao.getTotalTaskInQueueByclientAppId(clientAppID);
    }
    
    @Override
    public Long getTaskQueueCountByClientAppIdAndStatus(Long clientAppID, Integer status) {
        return taskQueueDao.getTaskQueueCountByclientAppAndStatus(clientAppID, status);
    }

    @Override
    public Long getTotalNumberOfTaskQueue(Long clientAppID) {
        return taskQueueDao.getTotalTaskInQueueByclientAppId(clientAppID);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TaskQueueResponse> getTaskQueueResponseByClientApp(String shortName){
        ClientApp app = clientAppService.findClientAppByCriteria("shortName", shortName);

        List<TaskQueue> taskQueues =  this.getTaskQueueByClientAppStatus(app.getClientAppID(), StatusCodeType.TASK_LIFECYCLE_COMPLETED);

        List<TaskQueueResponse> responses = new ArrayList<TaskQueueResponse>();

        for(TaskQueue taskQ : taskQueues){
            List<TaskQueueResponse> taskQueueResponse = taskQueueResponseDao.getTaskQueueResponseByTaskQueueID(taskQ.getTaskQueueID());
            if(taskQueueResponse.size() > 0){
                TaskQueueResponse thisTaskResponse = taskQueueResponse.get(0);
                String infoOutput = thisTaskResponse.getResponse();

                if(infoOutput!= null && !infoOutput.isEmpty()){
                    responses.add(thisTaskResponse) ;
                }
            }
        }
        return responses;
    }

    @Override
    public List<TaskQueue> getAll() {
        return taskQueueDao.getAll();
    }

}
