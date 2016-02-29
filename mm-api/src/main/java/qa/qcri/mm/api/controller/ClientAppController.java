package qa.qcri.mm.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.entity.TaskQueue;
import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.service.TaskQueueService;
import qa.qcri.mm.api.store.StatusCodeType;
import qa.qcri.mm.api.template.ClientAppModel;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 2/21/14
 * Time: 1:44 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/clientapp")
public class ClientAppController {
    
	@Autowired
    private ClientAppService clientAppService;
    
	@Autowired
	private TaskQueueService taskQueueService;
    
    @RequestMapping(value = "/allactive", method = RequestMethod.GET)
    public List<ClientAppModel> getAllActive(){
        return clientAppService.getAllActiveClientApps();
    }
    
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<ClientAppModel> getAll(){
    	List<ClientApp> appList = clientAppService.getAllClientApp();
    	List<ClientAppModel> aList = new ArrayList<ClientAppModel>();
    	
    	for (ClientApp clientApp : appList) {
    		List<TaskQueue> taskQueues = taskQueueService.getTaskQueueByClientApp(clientApp.getClientAppID());
    		Integer totalTask = taskQueues.size();
			Integer availableTask = getAvailableTaskCount(taskQueues);
			ClientAppModel model = new ClientAppModel(clientApp.getClientAppID(),
					clientApp.getPlatformAppID(), clientApp.getCrisisID(), clientApp.getName(),
					clientApp.getShortName(), clientApp.getAppType(), clientApp.getStatus(), availableTask, totalTask);
            aList.add(model);
        }
    	return aList;
    }
    
    private int getAvailableTaskCount(List<TaskQueue> taskList) {
    	int availableTask = 0;
    	for (TaskQueue taskQueue : taskList) {
			if(taskQueue.getStatus() == StatusCodeType.TASK_LIFECYCLE_COMPLETED) {
				availableTask++;
			}
		}
    	return availableTask;
    }
    
}
