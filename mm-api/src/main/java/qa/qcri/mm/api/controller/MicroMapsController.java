package qa.qcri.mm.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import qa.qcri.mm.api.dao.CrisisDao;
import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.service.GeoService;
import qa.qcri.mm.api.service.MicroMapsService;
import qa.qcri.mm.api.store.URLReference;
import qa.qcri.mm.api.util.DataFileUtil;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 4/22/15
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/micromaps")
@Component
public class MicroMapsController {
    protected static Logger logger = Logger.getLogger("MicroMapsController");

    @Autowired
    MicroMapsService microMapsService;

    @Autowired
    GeoService geoService;  
    
    @Autowired
    CrisisDao crisisDao;
    
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/clear/temp-data")
    public String clearAllTempFiles() throws Exception {    	
    	String fileName = URLReference.GEOJSON_HOME + "app";
    	File f = new File(fileName);
    	if(f.exists()){
    		File[] listFiles = f.listFiles();
    		for(File file : listFiles){
    			System.out.println(file.delete());
    		}    
    		
    		fileName = URLReference.GEOJSON_HOME + "app/download";
    		f = new File(fileName);
    		listFiles = f.listFiles();
    		for(File file : listFiles){
    			System.out.println(file.delete());
    		}
    	}    	
        return "Success";
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/download/geojson/id/{id}")
    public String downloadGeojson(@PathParam("id") long id) throws Exception {
    	String path = URLReference.GEOJSON_HOME + "app/download/";
    	String dwonloadPath = "app/download/";
    	
    	File pathDir = new File(path);
    	pathDir.mkdirs();
    	
    	List<Crisis> crisises = microMapsService.findCrisisByClientAppID(id);
    	if(crisises != null && !crisises.isEmpty()){
    		Crisis crisis = crisises.get(0);
    		String displayName = crisis.getDisplayName().replace(' ', '_');    		
    		String jsonFileName = path + displayName + "_" + id + ".json";
        	String zipFileName = path + displayName + "_" + id + ".zip";
        	dwonloadPath +=  displayName + "_" + id + ".zip";
        	
    		File zipFile = new File(zipFileName);    		
    		boolean zipExist = zipFile.exists();
    		System.out.println(zipExist);
        	if(!zipExist || (zipExist && crisis.getActivationEnd() == null )){
        		String geoClickerData = microMapsService.getGeoClickerDataForDownload(id);        		
        		DataFileUtil.createAfile(geoClickerData, jsonFileName);        		
        		microMapsService.createZip(zipFileName, jsonFileName, id + ".json");        		
        	}
    	}    
    	return "jsonp({\"dwonloadPath\" : \"" + dwonloadPath+ "\"});";
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSON/crisis")
    public String getAllCrisis() throws Exception {
        return microMapsService.getAllCrisisJSONP().toJSONString();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/crisis")
    public String getAllCrisisJSONP() throws Exception {
        return "jsonp(" + microMapsService.getAllCrisisJSONP().toJSONString() + ");";
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/crisis/apps/id/{id}")
    public String getAppsByCrisisJSONP(@PathParam("id") long id) throws Exception {

        return microMapsService.getGeoClickerByCrisis(id);
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSON/text/id/{id}")
    public String getAllTextJSON(@PathParam("id") long id) throws Exception {

        return microMapsService.getGeoClickerByClientApp(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/text/id/{id}")
    public String getAllTextJSONP(@PathParam("id") long id) throws Exception {

        return "jsonp(" + microMapsService.getGeoClickerByClientApp(id) + ");";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSON/image/id/{id}")
    public String getAllImageJSON(@PathParam("id") long id) throws Exception {

        return microMapsService.getGeoClickerByClientApp(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/image/id/{id}")
    public String getAllImageJSONP(@PathParam("id") long id) throws Exception {

        return "jsonp(" + microMapsService.getGeoClickerByClientApp(id) + ");";
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSON/aerial/id/{id}")
    public String getAllAerialJSON(@PathParam("id") long id) throws Exception {

        return microMapsService.getGeoClickerByClientApp(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/aerial/id/{id}")
    public String getAllAerialJSONP(@PathParam("id") long id) throws Exception {

        return "jsonp(" +microMapsService.getGeoClickerByClientApp(id) + ");";
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/JSONP/geojson/id/{id}/createdDate/{createdDate}")
    public String getGeojsonAfterTaskQueue(@PathParam("id") long id, @PathParam("createdDate") long createdDate) throws Exception {
        return "jsonp(" + microMapsService.getGeoClickerByClientAppAndAfterCreatedDate(id, createdDate) + ");";
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/file/video/id/{id}")
    public String getAllVideoJSONP(@PathParam("id") long id) throws Exception {

        String output = "";

        try {       /// just for testing now
            URL oracle = new URL("http://ec2-54-148-39-119.us-west-2.compute.amazonaws.com/videoClicker.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                output = output +  inputLine;
            }

            in.close();

        }
        catch(IOException ex) {
            ex.printStackTrace(); // for now, simply output it.
        }

        return output;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/file/image/id/{id}")
    public String getJSONPImageFile(@PathParam("id") long id) throws Exception {

        String output = "";

        try {      /// just for testing now
            URL oracle = new URL("http://ec2-54-148-39-119.us-west-2.compute.amazonaws.com/imageClicker.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                output = output +  inputLine;
            }

            in.close();

        }
        catch(IOException ex) {
            ex.printStackTrace(); // for now, simply output it.
        }

        return output;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Path("/kml/text/id/{id}")
    public String getTextClickerKML(@PathParam("id") long id) throws Exception {

        String output = microMapsService.generateTextClickerKML(id);
        return output;
    }

    @GET
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.WILDCARD})
    @Path("/kml/image/id/{id}")
    public String getImageClickerKML(@PathParam("id") long id) throws Exception {

        return microMapsService.generateImageClickerKML(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.WILDCARD})
    @Path("/kml/aerial/id/{id}")
    public String getAerialClickerKML(@PathParam("id") long id) throws Exception {

        return microMapsService.generateAericalClickerKML(id);
    }


}
