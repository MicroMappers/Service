package qa.qcri.mm.api.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.PamReport;
import qa.qcri.mm.api.entity.TaskQueueResponse;
import qa.qcri.mm.api.service.SkyeyeReportService;
import qa.qcri.mm.api.service.VanuatuReportService;

/**
 * Created with IntelliJ IDEA. User: jlucas Date: 12/13/14 Time: 6:40 AM To
 * change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("rest/pam")
public class VanuatuReportController {

	protected static Logger logger = Logger.getLogger("pamController");

	@Autowired
	SkyeyeReportService skyeyeReportService;

	@Autowired
	VanuatuReportService vanuatuReportService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/jsonp/reports/byID/{id}", method = RequestMethod.GET)
	public String getPamReportByID(@PathVariable("id") Long id) {
		PamReport report = vanuatuReportService.getPamRecordByID(id);
		JSONObject object = new JSONObject();

		object.put("id", report.getId());
		object.put("lat", report.getLat());
		object.put("lng", report.getLng());
		object.put("damageType", report.getDamageType());
		object.put("imageurl", report.getImgurl());
		object.put("geo", report.getGeo());

		return "jsonp(" + object.toJSONString() + ");";

	}

	@RequestMapping(value = "/reports/{source}", method = RequestMethod.GET)
	public List<TaskQueueResponse> getResponseReport(@PathVariable("source") String source) {
		System.out.print("source : " + source);
		return skyeyeReportService.getSummerydDataSetForReport(source);

	}

	@RequestMapping(value = "/jsonp/reports/{source}", method = RequestMethod.GET)
	public String getJSONPSummeryReport(@PathVariable("source") String source) {
		return "jsonp(" + skyeyeReportService.getJSONSummerydDataSetForReport(source).toJSONString() + ");";
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String getTester() {
		return "{\"status\":\"working\"}";
	}

	@RequestMapping(value = "/KML/reports/resources/{source}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getKMLResources(@PathVariable("source") String source) {
		return skyeyeReportService.getKMLSummeryDataSetByResources(source);

	}

	@RequestMapping(value = "/KML/reports/{source}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getKMLAll(@PathVariable("source") String source) {
		return skyeyeReportService.getKMLSummeryDataSetForReport(source);

	}

	@RequestMapping(value = "/KML/reports/blue/{source}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getKMLBlue(@PathVariable("source") String source) {
		return skyeyeReportService.getKMLSummeryDataSetByLayerType(source, "polyline");

	}

	@RequestMapping(value = "/KML/reports/red/{source}", method = RequestMethod.GET, produces = { "application/xml" })
	public String getKMLRed(@PathVariable("source") String source) {
		return skyeyeReportService.getKMLSummeryDataSetByLayerType(source, "polyline2");
	}
}
