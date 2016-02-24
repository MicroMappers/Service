package qa.qcri.mm.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.ClientAppService;
import qa.qcri.mm.api.template.ClientAppModel;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 2/21/14
 * Time: 1:44 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("rest/clientapp")
public class ClientAppController {
    @Autowired
    private ClientAppService clientAppService;
    
    @RequestMapping(value = "/allactive", method = RequestMethod.GET)
    public List<ClientAppModel> getAllActive(){
        return clientAppService.getAllActiveClientApps();
    }
}
