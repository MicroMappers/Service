package qa.qcri.mm.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.ClientApp;
import qa.qcri.mm.api.entity.ClientAppAnswer;
import qa.qcri.mm.api.entity.ClientAppDeployment;
import qa.qcri.mm.api.service.ClientAppAnswerService;
import qa.qcri.mm.api.service.ClientAppDeploymentService;
import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.template.ClientAppDeploymentModel;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/16/14
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/deployment")
public class ClientAppDeploymentController {

    @Autowired
    ClientAppDeploymentService clientAppDeploymentService;

    @Autowired
    ClientAppAnswerService appAnswerService;

    @Autowired
    ClientAppService appService;

    @RequestMapping(value = "/active/type/{typeID}", method = RequestMethod.GET)
    public ClientAppDeploymentModel getActiveByType(@PathVariable("typeID") Integer typeID){
        ClientAppDeployment deploy=  clientAppDeploymentService.getActiveDeploymentForAppType(typeID);
        if(deploy != null){
            ClientAppAnswer cAns = appAnswerService.getClientAppAnswer(deploy.getClientAppID()) ;
            ClientApp cApp = appService.findClientAppByID("clientAppID", deploy.getClientAppID());
            if(cAns != null && cApp != null){
                ClientAppDeploymentModel aModel = new ClientAppDeploymentModel(deploy.getDeploymentID(), deploy.getClientAppID(), cAns.getAnswer(), cApp.getName(), cApp.getAppType() );
                return aModel;
            }
        }
        return null;
    }
    
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public List<ClientAppDeploymentModel> getActive(){
        List<ClientAppDeployment> cDeploys = clientAppDeploymentService.getActiveDeployment();

        if(cDeploys != null && cDeploys.size() > 0){
            List<ClientAppDeploymentModel> models = new ArrayList<ClientAppDeploymentModel>();
            for(ClientAppDeployment c : cDeploys) {
                ClientAppAnswer cAns = appAnswerService.getClientAppAnswer(c.getClientAppID()) ;
                ClientApp cApp = appService.findClientAppByID("clientAppID", c.getClientAppID());
                if(cAns != null && cApp != null){
                    ClientAppDeploymentModel aModel = new ClientAppDeploymentModel(c.getDeploymentID(), c.getClientAppID(), cAns.getAnswer(), cApp.getName(), cApp.getAppType());
                    models.add(aModel)  ;
                }
            }
            return models;
        }

        return null ;
    }
    
    @RequestMapping(value = "/active/mobile", method = RequestMethod.GET)
    public List<ClientAppDeploymentModel> getMobileActive(){
        List<ClientAppDeployment> cDeploys = clientAppDeploymentService.getMobileActiveDeployment();
        if(cDeploys != null && cDeploys.size() > 0){
            List<ClientAppDeploymentModel> models = new ArrayList<ClientAppDeploymentModel>();
            for(ClientAppDeployment c : cDeploys) {
                ClientAppAnswer cAns = appAnswerService.getClientAppAnswer(c.getClientAppID()) ;
                ClientApp cApp = appService.findClientAppByID("clientAppID", c.getClientAppID());
                if(cAns != null && cApp != null){
                    ClientAppDeploymentModel aModel = new ClientAppDeploymentModel(c.getDeploymentID(), cApp.getPlatformAppID(), cAns.getAnswer(), cApp.getName(), cApp.getAppType());
                    models.add(aModel)  ;
                }
            }
            return models;
        }

        return null ;
    }

}
