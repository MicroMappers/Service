package qa.qcri.mm.trainer.pybossa.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/23/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientAppCreateWorker {
    void doCreateApp() throws Exception;
    void doAppUpdate(Project remoteProject, ClientApp clientApp, JSONObject attribute, JSONArray labelModel) throws Exception;
    void doAppTemplateUpdate(ClientApp clientApp, Long nominalAttributeID) throws Exception;
    void doAppDelete() throws Exception;
}
