package qa.qcri.mm.api.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.GeoService;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 1/19/14
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("rest/geo")
public class GeoController {
    protected static Logger logger = Logger.getLogger("GeoController");

    @Autowired
    GeoService geoService;

    @RequestMapping(value = "/JSON/geoMap/qdate/{lastupdated}", method = RequestMethod.GET)
    public String getMapGeoJSONBasedOnDate(@PathVariable("lastupdated") String lastupdated) {
        ///System.out.print("updated : " + lastupdated);
        String requestedDate = null;
        String returnValue = "";


        try {
            Date queryDate = null;
            if(!lastupdated.isEmpty() && lastupdated!= null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //String dateInString = "2014-01-26 13:44:48";
                if(requestedDate != null){
                    try{
                        queryDate = sdf.parse(lastupdated);
                    }
                    catch(Exception e){
                        queryDate = null;
                    }
                }
            }

            returnValue =  geoService.getGeoJsonOuputJSON(queryDate);

        } catch (Exception e) {
            System.out.println("Exception getMapGeoJSONBasedOnDate: " + e.getMessage());
           // e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return returnValue;
    }

    @RequestMapping(value = "/JSON/geoMap", method = RequestMethod.GET)
    public String getMapGeoJSON() {
        String returnValue = "";
        try {

            returnValue =  geoService.getGeoJsonOuputJSON(null);

        } catch (Exception e) {
            System.out.println("Exception getMapGeoJSON : " + e.getMessage());
           // e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return returnValue;
    }
    
    @RequestMapping(value = "/JSONP/geoMap/qdate/{lastupdated}", method = RequestMethod.GET)
    public String getMapGeoJSONPBasedOnDate(@PathVariable("lastupdated") String lastupdated) {

        String returnValue = "";
        try {
            Date queryDate = null;
            if(!lastupdated.isEmpty() && lastupdated!= null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //String dateInString = "2014-01-26 13:44:48";
                queryDate = sdf.parse(lastupdated);
            }

            returnValue =  geoService.getGeoJsonOuputJSONP(queryDate);

        } catch (Exception e) {
            System.out.println("Exception getMapGeoJSONPBasedOnDate : " + e.getMessage());
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return returnValue;
    }

    @RequestMapping(value = "/JSONP/geoMap", method = RequestMethod.GET)
    public String getMapGeoJSONP() {

        String returnValue = "";
        try {

            returnValue =  geoService.getGeoJsonOuputJSONP(null);

        } catch (Exception e) {
            System.out.println("Exception getMapGeoJSONP : " + e.getMessage());
        }

        return returnValue;
    }

}
