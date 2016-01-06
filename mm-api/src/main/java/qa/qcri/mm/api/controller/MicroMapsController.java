package qa.qcri.mm.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.dao.CrisisDao;
import qa.qcri.mm.api.entity.Crisis;
import qa.qcri.mm.api.service.GeoService;
import qa.qcri.mm.api.service.MicroMapsService;
import qa.qcri.mm.api.store.URLReference;
import qa.qcri.mm.api.util.DataFileUtil;

/**
 * Created with IntelliJ IDEA. User: jlucas Date: 4/22/15 Time: 10:23 PM To
 * change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/micromaps")
public class MicroMapsController {
	protected static Logger logger = Logger.getLogger("MicroMapsController");

	@Autowired
	MicroMapsService microMapsService;

	@Autowired
	GeoService geoService;

	@Autowired
	CrisisDao crisisDao;

	@RequestMapping(value = "/clear/temp-data", method = RequestMethod.GET)
	public String clearAllTempFiles() throws Exception {
		String fileName = URLReference.GEOJSON_HOME + "app";
		File f = new File(fileName);
		if (f.exists()) {
			File[] listFiles = f.listFiles();
			for (File file : listFiles) {
				System.out.println(file.delete());
			}

			fileName = URLReference.GEOJSON_HOME + "app/download";
			f = new File(fileName);
			listFiles = f.listFiles();
			for (File file : listFiles) {
				System.out.println(file.delete());
			}
		}
		return "Success";
	}

	@RequestMapping(value = "/JSONP/download/geojson/id/{id}", method = RequestMethod.GET)
	public String downloadGeojson(@PathVariable("id") long id) throws Exception {
		String path = URLReference.GEOJSON_HOME + "app/download/";
		String dwonloadPath = "app/download/";

		File pathDir = new File(path);
		pathDir.mkdirs();

		List<Crisis> crisises = microMapsService.findCrisisByClientAppID(id);
		if (crisises != null && !crisises.isEmpty()) {
			Crisis crisis = crisises.get(0);
			String displayName = crisis.getDisplayName().replace(' ', '_');
			String jsonFileName = path + displayName + "_" + id + ".json";
			String zipFileName = path + displayName + "_" + id + ".zip";
			dwonloadPath += displayName + "_" + id + ".zip";

			File zipFile = new File(zipFileName);
			boolean zipExist = zipFile.exists();
			System.out.println(zipExist);
			if (!zipExist || (zipExist && crisis.getActivationEnd() == null)) {
				String geoClickerData = microMapsService.getGeoClickerDataForDownload(id);
				DataFileUtil.createAfile(geoClickerData, jsonFileName);
				microMapsService.createZip(zipFileName, jsonFileName, id + ".json");
			}
		}
		return "jsonp({\"dwonloadPath\" : \"" + dwonloadPath + "\"});";
	}

	@RequestMapping(value = "/JSON/crisis", method = RequestMethod.GET)
	public String getAllCrisis() throws Exception {
		return microMapsService.getAllCrisisJSONP().toJSONString();
	}

	@RequestMapping(value = "/JSONP/crisis", method = RequestMethod.GET)
	public String getAllCrisisJSONP() throws Exception {
		return "jsonp(" + microMapsService.getAllCrisisJSONP().toJSONString() + ");";
	}

	@RequestMapping(value = "/JSONP/crisis/apps/id/{id}", method = RequestMethod.GET)
	public String getAppsByCrisisJSONP(@PathVariable("id") long id) throws Exception {
		return microMapsService.getGeoClickerByCrisis(id);
	}

	@RequestMapping(value = "/JSON/text/id/{id}", method = RequestMethod.GET)
	public String getAllTextJSON(@PathVariable("id") long id) throws Exception {
		return microMapsService.getGeoClickerByClientApp(id);
	}

	@RequestMapping(value = "/JSONP/text/id/{id}", method = RequestMethod.GET)
	public String getAllTextJSONP(@PathVariable("id") long id) throws Exception {
		return "jsonp(" + microMapsService.getGeoClickerByClientApp(id) + ");";
	}

	@RequestMapping(value = "/JSON/image/id/{id}", method = RequestMethod.GET)
	public String getAllImageJSON(@PathVariable("id") long id) throws Exception {
		return microMapsService.getGeoClickerByClientApp(id);
	}

	@RequestMapping(value = "/JSONP/image/id/{id}", method = RequestMethod.GET)
	public String getAllImageJSONP(@PathVariable("id") long id) throws Exception {
		return "jsonp(" + microMapsService.getGeoClickerByClientApp(id) + ");";
	}

	@RequestMapping(value = "/JSON/aerial/id/{id}", method = RequestMethod.GET)
	public String getAllAerialJSON(@PathVariable("id") long id) throws Exception {
		return microMapsService.getGeoClickerByClientApp(id);
	}

	@RequestMapping(value = "/JSONP/aerial/id/{id}", method = RequestMethod.GET)
	public String getAllAerialJSONP(@PathVariable("id") long id) throws Exception {
		return "jsonp(" + microMapsService.getGeoClickerByClientApp(id) + ");";
	}

	@RequestMapping(value = "/JSONP/geojson/id/{id}/createdDate/{createdDate}", method = RequestMethod.GET)
	public String getGeojsonAfterTaskQueue(@PathVariable("id") long id, @PathVariable("createdDate") long createdDate)
			throws Exception {
		return "jsonp(" + microMapsService.getGeoClickerByClientAppAndAfterCreatedDate(id, createdDate) + ");";
	}

	@RequestMapping(value = "/file/video/id/{id}", method = RequestMethod.GET)
	public String getAllVideoJSONP(@PathVariable("id") long id) throws Exception {

		String output = "";

		try { // / just for testing now
			URL oracle = new URL("http://ec2-54-148-39-119.us-west-2.compute.amazonaws.com/videoClicker.json");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				// System.out.println(inputLine);
				output = output + inputLine;
			}

			in.close();

		} catch (IOException ex) {
			ex.printStackTrace(); // for now, simply output it.
		}

		return output;
	}

	@RequestMapping(value = "/file/image/id/{id}", method = RequestMethod.GET)
	public String getJSONPImageFile(@PathVariable("id") long id) throws Exception {

		String output = "";

		try { // / just for testing now
			URL oracle = new URL("http://ec2-54-148-39-119.us-west-2.compute.amazonaws.com/imageClicker.json");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				// System.out.println(inputLine);
				output = output + inputLine;
			}

			in.close();

		} catch (IOException ex) {
			ex.printStackTrace(); // for now, simply output it.
		}

		return output;
	}

	@RequestMapping(value = "/kml/text/id/{id}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getTextClickerKML(@PathVariable("id") long id) throws Exception {
		String output = microMapsService.generateTextClickerKML(id);
		return output;
	}

	@RequestMapping(value = "/kml/image/id/{id}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getImageClickerKML(@PathVariable("id") long id) throws Exception {
		return microMapsService.generateImageClickerKML(id);
	}

	@RequestMapping(value = "/kml/aerial/id/{id}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getAerialClickerKML(@PathVariable("id") long id) throws Exception {
		return microMapsService.generateAericalClickerKML(id);
	}

}
