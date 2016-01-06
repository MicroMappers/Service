package qa.qcri.mm.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.service.PartnerAppSourceService;
import qa.qcri.mm.api.store.StatusCodeType;
import qa.qcri.mm.api.util.DataFormatValidator;

@RestController
@RequestMapping("/partnerAppSource")
public class PartnerAppSourceController {
	protected static Logger logger = Logger.getLogger("PartnerAppSourceController");

	@Autowired
	PartnerAppSourceService partnerAppSourceService;

	@RequestMapping(value = "/pushAppSource", method = RequestMethod.POST)
	public String pushAppSource(@RequestBody String data){

		String returnValue = StatusCodeType.RETURN_SUCCESS;
		try {
			if(data!=null && DataFormatValidator.isValidateJson(data)){
				JSONParser parser = new JSONParser();
				JSONObject config = (JSONObject) parser.parse(data);

				String importURL = (String)config.get("IMPORT_URL");

				Long crisisId = (long)config.get("CRISIS_ID");

				Long recordsCount = config.get("NUMBER_OF_RECORDS_PER_VOLUME") != null ? (long)config.get("NUMBER_OF_RECORDS_PER_VOLUME") : 1500L;

				String crisisCode = (String)config.get("CRISIS_CODE");

				String fileLocation = (String)config.get("FILE_LOCATION");

				if(!StringUtils.isEmpty(importURL)  || crisisId!=null || recordsCount!=null
						|| !StringUtils.isEmpty(crisisCode) || !StringUtils.isEmpty(fileLocation) ){

					partnerAppSourceService.pushAppSource(importURL, crisisId, recordsCount, crisisCode, fileLocation);
				}
				else{
					returnValue = StatusCodeType.RETURN_FAIL;
					logger.error("Properties are unassigned or not defined properly in configuration file: " + config);
				}
			}
		}catch(Exception e){
			returnValue = StatusCodeType.RETURN_FAIL;
			logger.error("Error in pushAppSource : " + e);
		}
		return returnValue;
		//return Response.status(CodeLookUp.APP_REQUEST_SUCCESS).entity(returnValue).build();
	}
}
