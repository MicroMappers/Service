package qa.qcri.mm.trainer.pybossa.format.impl;

import java.util.List;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import qa.qcri.mm.trainer.pybossa.dao.MarkerStyleDao;
import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entity.MarkerStyle;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueue;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueueResponse;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.TaskRun;
import qa.qcri.mm.trainer.pybossa.service.ClientAppResponseService;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/10/14
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataProcessor extends Object {

    @Autowired
    protected MarkerStyleDao markerStyleDao;

    protected ClientApp clientApp;
    protected TaskQueue taskQueue;
    protected List<TaskRun> datasource;
    protected JSONParser parser;
    protected List<MarkerStyle> style;

    public DataProcessor(){
        throw new AssertionError();
    }
    public DataProcessor(ClientApp clientApp){
        this.parser = new JSONParser();
        this.clientApp = clientApp;
    }

    public void setMarkerStyleDao(MarkerStyleDao markerStyleDao) {
        this.markerStyleDao = markerStyleDao;
        this.style = markerStyleDao.findByClientAppID(this.clientApp.getClientAppID());
    }

    public ClientApp getClientApp(){
        return this.clientApp;
    }

    public abstract TaskQueueResponse process(List<TaskRun> datasource, TaskQueue taskQueue) throws Exception;

    public abstract List<TaskQueueResponse> generateMapOuput(List<TaskQueue> taskQueues, ClientAppResponseService clientAppResponseService) throws Exception;

}
