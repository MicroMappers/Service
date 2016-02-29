package qa.qcri.mm.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.Client;
import qa.qcri.mm.api.service.ClientService;

/**
 * Created with IntelliJ IDEA.
 * User: jilucas
 * Date: 9/11/13
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("rest/client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @RequestMapping(value = "/id/{clientid}", method = RequestMethod.GET)
    public Client getCrisisByID(@PathVariable("clientid") Long clientid){
        return clientService.findClientbyID("clientID", clientid) ;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String getPing(){
        return "Hello World from Client!" ;
    }
}
