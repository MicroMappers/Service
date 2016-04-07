package qa.qcri.mm.api.controller;


import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.ClientAppSourceService;
import qa.qcri.mm.api.store.CodeLookUp;
import qa.qcri.mm.api.store.StatusCodeType;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/12/14
 * Time: 9:28 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/rest/source")
public class ClientAppSourceController {

    protected static Logger logger = Logger.getLogger("ClientAppSourceController");

    @Autowired
    private ClientAppSourceService clientAppSourceService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response saveAppSource(@RequestBody String data){
        String returnValue = StatusCodeType.RETURN_SUCCESS;

        logger.info("saveAppSource : " + data );

        try{
            JSONParser parser = new JSONParser();
            JSONArray objs = (JSONArray)parser.parse(data);
            for(Object a : objs){
                JSONObject obj = (JSONObject)a;
                String fileURL = (String)obj.get("fileURL");
                Long appID = (Long)obj.get("appID");

                logger.info("fileURL : " + fileURL );
                logger.info("appID : " + appID );

                clientAppSourceService.addExternalDataSouceWithClientAppID(fileURL, appID);
            }
        }
        catch(Exception e){
            returnValue = StatusCodeType.RETURN_FAIL;
            logger.error("saveAppSource got exception : ", e);
        }

        return Response.status(CodeLookUp.APP_REQUEST_SUCCESS).entity(returnValue).build();
    }

    @RequestMapping(value = "/aidr/push", method = RequestMethod.POST)
    public Response saveAIDRSource(@RequestBody String data){
        String returnValue = StatusCodeType.RETURN_SUCCESS;

        System.out.println("saveAppSource : " + data );

        try{
            JSONParser parser = new JSONParser();
            JSONArray objs = (JSONArray)parser.parse(data);
            for(Object a : objs){
                JSONObject obj = (JSONObject)a;
                String fileURL = (String)obj.get("fileURL");
                Long platformID = (Long)obj.get("platformID");
                System.out.println("fileURL : " + fileURL );

                clientAppSourceService.addExternalDataSourceWithClassifiedData(fileURL, platformID);
            }
        }
        catch(Exception e){
            returnValue = StatusCodeType.RETURN_FAIL;
            logger.error("saveAppSource got exception : " + e);
            System.out.println("saveAppSource excpetion : " + e );
        }

        return Response.status(CodeLookUp.APP_REQUEST_SUCCESS).entity(returnValue).build();
    }

    @RequestMapping(value = "/mapbox/push", method = RequestMethod.POST)
    public Response saveMapBoxSource(@RequestBody String data){
        String returnValue = StatusCodeType.RETURN_SUCCESS;

        System.out.println("saveMapBoxSource : " + data );

        try{
            clientAppSourceService.handleMapBoxDataSource(data);
        }
        catch(Exception e){
            returnValue = StatusCodeType.RETURN_FAIL;
            logger.error("saveAppSource got exception : " + e);
            System.out.println("saveAppSource excpetion : " + e );
        }

        return Response.status(CodeLookUp.APP_REQUEST_SUCCESS).entity(returnValue).build();
    }

    @RequestMapping(value = "/gist/push", method = RequestMethod.POST)
    public Response saveMapBoxGist(@RequestBody String data){

        String returnValue = StatusCodeType.RETURN_SUCCESS;
        System.out.println("saveMapBoxGist : " + data );

        try{
            clientAppSourceService.handleMapBoxGistDataSource(data);
        }
        catch(Exception e){
            returnValue = StatusCodeType.RETURN_FAIL;
            logger.error("saveAppSource got exception : " + e);
            System.out.println("saveAppSource excpetion : " + e );
        }

        return Response.status(CodeLookUp.APP_REQUEST_SUCCESS).entity(returnValue).build();
    }

}