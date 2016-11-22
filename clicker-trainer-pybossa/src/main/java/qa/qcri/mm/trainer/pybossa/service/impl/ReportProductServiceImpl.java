package qa.qcri.mm.trainer.pybossa.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import au.com.bytecode.opencsv.CSVWriter;
import qa.qcri.mm.trainer.pybossa.custom.MAPBoxAerialClickerFileFormat;
import qa.qcri.mm.trainer.pybossa.dao.ImageMetaDataDao;
import qa.qcri.mm.trainer.pybossa.dao.TaskQueueResponseDao;
import qa.qcri.mm.trainer.pybossa.entity.Client;
import qa.qcri.mm.trainer.pybossa.entity.ClientApp;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppEvent;
import qa.qcri.mm.trainer.pybossa.entity.ClientAppSource;
import qa.qcri.mm.trainer.pybossa.entity.ImageMetaData;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplate;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueue;
import qa.qcri.mm.trainer.pybossa.entity.TaskQueueResponse;
import qa.qcri.mm.trainer.pybossa.format.impl.CSVRemoteFileFormatter;
import qa.qcri.mm.trainer.pybossa.format.impl.GeoJsonOutputModel;
import qa.qcri.mm.trainer.pybossa.format.impl.MicromapperInput;
import qa.qcri.mm.trainer.pybossa.service.ClientAppEventService;
import qa.qcri.mm.trainer.pybossa.service.ClientAppService;
import qa.qcri.mm.trainer.pybossa.service.ClientAppSourceService;
import qa.qcri.mm.trainer.pybossa.service.ClientService;
import qa.qcri.mm.trainer.pybossa.service.ReportProductService;
import qa.qcri.mm.trainer.pybossa.service.ReportTemplateService;
import qa.qcri.mm.trainer.pybossa.service.TaskQueueService;
import qa.qcri.mm.trainer.pybossa.store.LookupCode;
import qa.qcri.mm.trainer.pybossa.store.PybossaConf;
import qa.qcri.mm.trainer.pybossa.store.StatusCodeType;
import qa.qcri.mm.trainer.pybossa.store.UserAccount;
import qa.qcri.mm.trainer.pybossa.util.DateTimeConverter;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 11/22/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("reportProductService")
@Transactional(readOnly = false)
public class ReportProductServiceImpl implements ReportProductService {

	protected static Logger logger = Logger.getLogger(ReportProductServiceImpl.class);

	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientAppService clientAppService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ClientAppSourceService clientAppSourceService;

	@Autowired
	private ClientAppEventService clientAppEventService;

	@Autowired
	private ImageMetaDataDao imageMetaDataDao;

	@Autowired
	private TaskQueueResponseDao taskQueueResponseDao;

	@Autowired
	private TaskQueueService taskQueueService;

	private CSVRemoteFileFormatter cvsRemoteFileFormatter;

	private Client client;

	private JSONParser parser = new JSONParser();

	public void setClassVariable() throws Exception{
		client = clientService.findClientByCriteria("name", UserAccount.MIROMAPPER_USER_NAME);
	}

	@Override
	public void generateReportTemplateFromExternalSource() throws Exception {
		List<ClientAppSource> datasources = clientAppSourceService.getClientAppSourceWithStatusCode(StatusCodeType.EXTERNAL_DATA_SOURCE_TO_GEO_READY_REPORT);

		for(int j=0; j < datasources.size(); j++){

			String url = datasources.get(j).getSourceURL();
			Long clientAppID = datasources.get(j).getClientAppID();

			if(!cvsRemoteFileFormatter.doesSourcerExist(url)){
				return;
			}

			List<MicromapperInput> micromapperInputList = cvsRemoteFileFormatter.getInputDataForReportTemplate(url);
			this.generateReportTemplate(micromapperInputList,clientAppID );
		}

	}

	@Override
	public void generateCSVReportForImageGeoClicker() throws Exception{
		logger.info("ReportProductServiceImpl.generateCSVReportForImageGeoClicker Starts");
		setClassVariable();

		if(client == null){
			return;
		}

		List<ClientApp> appList = clientAppService.getAllClientAppByClientID(client.getClientID() );
		for (ClientApp clientApp : appList) {

			ClientAppEvent targetClientApp = clientAppEventService.getNextSequenceClientAppEvent(clientApp.getClientAppID());

			if(targetClientApp != null ){
				List<ReportTemplate> templateList =  reportTemplateService.getReportTemplateByClientApp(clientApp.getClientAppID(), StatusCodeType.TEMPLATE_IS_READY_FOR_EXPORT);

				if(templateList.size() > StatusCodeType.MAX_CSV_ROW_QUEUE_SIZE ){

					List<ReportTemplate> reportTemplateList = new ArrayList<ReportTemplate>();
					CSVRemoteFileFormatter formatter = new CSVRemoteFileFormatter();

					String fileName = PybossaConf.DEFAULT_TRAINER_FILE_PATH + DateTimeConverter.reformattedCurrentDateForFileName() + clientApp.getShortName() + "export.csv";
					CSVWriter writer = formatter.instanceToOutput(fileName);

					for (ReportTemplate reportTemplate : templateList) {

						String ans = reportTemplate.getAnswer();

						if(!ans.equalsIgnoreCase("none"))  {
							formatter.addToCSVOuputFile(generateOutputData(reportTemplate),writer);
							reportTemplate.setStatus(StatusCodeType.TEMPLATE_EXPORTED);
							reportTemplateList.add(reportTemplate);
						}
					}
					if(!CollectionUtils.isEmpty(reportTemplateList)){
						reportTemplateService.updateReportItem(reportTemplateList);
					}

					formatter.finalizeCSVOutputFile(writer);

					ClientAppSource appSource = new ClientAppSource(targetClientApp.getClientAppID(), StatusCodeType.EXTERNAL_DATA_SOURCE_ACTIVE, fileName);
					clientAppSourceService.insertNewClientAppSource(appSource);
				}
			}
		}
		logger.info("ReportProductServiceImpl.generateCSVReportForImageGeoClicker Ends");
	}


	@Override
	public void generateCSVReportForTextGeoClicker() throws Exception{
		logger.info("ReportProductServiceImpl.generateCSVReportForTextGeoClicker Starts");
		setClassVariable();
		if(client == null){
			return;
		}

		List<ReportTemplate> temps =  reportTemplateService.getReportTemplateWithUniqueKey("clientAppID");

		Iterator itr= temps.iterator();

		while(itr.hasNext()){

			Long clientAppID = (long)itr.next();
			List<ReportTemplate> templateList =  reportTemplateService.getReportTemplateByClientApp(clientAppID, LookupCode.TEMPLATE_IS_READY_FOR_EXPORT);

			if(templateList.size() > LookupCode.MIN_REPORT_TEMPLATE_EXPORT_SIZE){

				ClientApp clientApp = clientAppService.findClientAppByID(clientAppID);
				String sTemp = reformatFileName(clientApp.getShortName()) ;

				String fileName = PybossaConf.DEFAULT_TRAINER_FILE_PATH + sTemp;

				CSVRemoteFileFormatter formatter = new CSVRemoteFileFormatter();
				List<ReportTemplate> reportTemplateUpdateList = new ArrayList<ReportTemplate>();

				CSVWriter writer = formatter.instanceToOutput(fileName);

				for (ReportTemplate reportTemplate : templateList) {

					formatter.addToCSVOuputFile(generateOutputData(reportTemplate),writer);
					reportTemplate.setStatus(LookupCode.TEMPLATE_EXPORTED);
					reportTemplateUpdateList.add(reportTemplate);
				}

				reportTemplateService.updateReportItem(reportTemplateUpdateList);

				formatter.finalizeCSVOutputFile(writer);
				ClientAppEvent targetClientApp = clientAppEventService.getNextSequenceClientAppEvent(clientApp.getClientAppID());
				if(targetClientApp != null ){
					ClientAppSource appSource = new ClientAppSource(targetClientApp.getClientAppID(), LookupCode.EXTERNAL_DATA_SOURCE_ACTIVE, fileName);
					clientAppSourceService.insertNewClientAppSource(appSource);
				}
			}
		}
		logger.info("ReportProductServiceImpl.generateCSVReportForTextGeoClicker Ends");
	}

	@Override
	public void generateGeoJsonForESRI(List<GeoJsonOutputModel> geoJsonOutputModels) throws Exception {
		//To change body of implemented methods use File | Settings | File Templates.
		JSONParser parser = new JSONParser();

		JSONArray jsonArray ;
		FileWriter fw ;
		BufferedWriter bw;

		if(geoJsonOutputModels.size() > 0){
			CSVRemoteFileFormatter formatter = new CSVRemoteFileFormatter();

			String fileName = PybossaConf.DEFAULT_TRAINER_GEO_FILE_PATH + DateTimeConverter.reformattedCurrentDateForFileName()+"export.geojson";
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
				jsonArray = new JSONArray();
			}
			else{
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write("");
				bw.close();

				Object obj = parser.parse(new FileReader(fileName));
				jsonArray = (JSONArray) obj;
			}


			for(GeoJsonOutputModel item : geoJsonOutputModels) {
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("info", (JSONObject)parser.parse(item.getGeoJsonInfo()));

				jsonObject.put("features", (JSONObject)parser.parse(item.getGeoJson()));
				jsonArray.add(jsonObject);
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(jsonArray.toJSONString());
			bw.close();
		}

	}

	@Override
	public void generateMapBoxTemplateForAerialClicker() throws Exception {
		logger.info("ReportProductServiceImpl.generateMapBoxTemplateForAerialClicker Starts");

		List<ImageMetaData> imageMetaDataList = imageMetaDataDao.getMapBoxDataTile(StatusCodeType.MAPBOX_TILE_IMPORTED, 1000);
		String fileJsonName = PybossaConf.DEFAULT_TRAINER_FILE_PATH + DateTimeConverter.reformattedCurrentDateForFileName() + "MapBoxExport.json";

		if(imageMetaDataList.size() > 0){
			JSONArray jsonArray = MAPBoxAerialClickerFileFormat.createAerialClickerData(imageMetaDataList);
			try {
				//String fileJsonName ="/Users/jlucas/Documents/ConservationDrones/TEST/export2.json";

				if(jsonArray.size() == 0){
					return;
				}
				File fileJson = new File(fileJsonName);

				FileWriter geoWriter = new FileWriter(fileJson);

				geoWriter.write(jsonArray.toJSONString());
				geoWriter.flush();
				geoWriter.close();

			} catch (IOException e) {
				logger.error("Exception in generateMapBoxTemplateForAerialClicker", e);
			} finally {
				if(imageMetaDataList.size() > 0){
					for(ImageMetaData imageMetaData : imageMetaDataList) {
						imageMetaDataDao.updateExportedData(imageMetaData);
					}
					// hard-coded id. bad code!!! willl be updated.
					ClientAppSource appSource = new ClientAppSource(Long.valueOf(257), StatusCodeType.MAPBOX_TILE_EXPORTED, fileJsonName);
					clientAppSourceService.insertNewClientAppSource(appSource);
				}
			}
		}
		logger.info("ReportProductServiceImpl.generateMapBoxTemplateForAerialClicker Ends");
	}

	@Override
	public void generateGeoJsonForClientApp(ClientApp clientApp) throws Exception {
		logger.info("GenerateGeoJsonForClientApp Starts for ClientAppId : " + clientApp.getClientAppID());

		if(clientApp.getAppType().equals(StatusCodeType.APP_MAP)){

			JSONArray features = new JSONArray();

			List<TaskQueue> taskQueueList = taskQueueService.getTaskQueueByClientAppStatus(clientApp.getClientAppID(), StatusCodeType.TASK_LIFECYCLE_COMPLETED);

			for(TaskQueue taskQueue: taskQueueList){

				List<TaskQueueResponse> taskQueueResponses = taskQueueResponseDao.getTaskQueueResponse(taskQueue.getTaskQueueID());

				if(taskQueueResponses.size() > 0 ){
					if(!taskQueueResponses.get(0).getResponse().equalsIgnoreCase("{}") && !taskQueueResponses.get(0).getResponse().equalsIgnoreCase("[]")){
						JSONArray eachFeatureArrary = (JSONArray)parser.parse(taskQueueResponses.get(0).getResponse());
						for(Object a : eachFeatureArrary){
							features.add((JSONObject) a);
						}
					}
				}
			}

			String fileName = PybossaConf.GEOJSON_HOME + clientApp.getClientAppID() + ".json";
			File f = new File(fileName);

			if(f.exists()){
				f.delete();
			}

			JSONObject geoClickerOutput = new JSONObject();
			geoClickerOutput.put("type", "FeatureCollection");
			geoClickerOutput.put("features", features);

			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(geoClickerOutput.toJSONString());
			writer.close();
		}
		logger.info("GenerateGeoJsonForClientApp Ends for ClientAppId : " + clientApp.getClientAppID());
	}


	private String[] generateOutputData(ReportTemplate rpt){

		String[] data = new String[8];
		data[0] =   rpt.getTweetID().toString();
		data[1] =  rpt.getTweet();
		data[2] = rpt.getAuthor();
		data[3] = rpt.getLat();
		data[4] = rpt.getLon();
		data[5] = rpt.getUrl();
		data[6] = rpt.getCreated();
		data[7] = rpt.getAnswer();

		return data;
	}

	private void generateReportTemplate(List<MicromapperInput> micromapperInputList, Long clientAppID){
		long taskID = 0;
		long taskQueueID = 0;
		for(MicromapperInput ins : micromapperInputList){
			ReportTemplate template = new ReportTemplate( taskQueueID,taskID,ins.getTweetID(), ins.getTweet(),ins.getAuthor(), ins.getLat(), ins.getLng(), ins.getUrl(),ins.getCreated(),
					ins.getAnswer(), StatusCodeType.TEMPLATE_IS_READY_FOR_EXPORT, clientAppID);

			reportTemplateService.saveReportItem(template);

		}

	}

	private String reformatFileName(String shortName){
		String sTemp = DateTimeConverter.reformattedCurrentDateForFileName() + shortName ;

		if(sTemp.length() > 50){
			int iCutCount = sTemp.length() - 50;
			iCutCount = sTemp.length() - iCutCount;

			sTemp = sTemp.substring(0, iCutCount) ;

		}
		sTemp = sTemp + "export.csv";

		return sTemp;
	}
}
