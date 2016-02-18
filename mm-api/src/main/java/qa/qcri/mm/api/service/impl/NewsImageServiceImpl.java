package qa.qcri.mm.api.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import qa.qcri.mm.api.dao.NewsImageDao;
import qa.qcri.mm.api.entity.NewsImage;
import qa.qcri.mm.api.service.ClientAppSourceService;
import qa.qcri.mm.api.service.NewsImageService;
import au.com.bytecode.opencsv.CSVReader;

@Service("newsImageService")
public class NewsImageServiceImpl implements NewsImageService {

	protected static Logger logger = Logger.getLogger(NewsImageService.class);
	
	@Autowired
    private ClientAppSourceService clientAppSourceService;
	
	@Autowired
	private NewsImageDao newsImageDao;
	
	@Value("${gdelt.url}")
	private String gdeltURL;
	
	@Value("${gdelt.file.extension}")
	private String gdeltFileExtension;
	
	private static boolean gdeltPullStatus = false;
	
	@Override
	public boolean getGdeltPullStatus() {
		return gdeltPullStatus;
	}

	@SuppressWarnings("static-access")
	@Override
	public void stopFetchingDataFromGdelt(Long clientAppID) {
		this.gdeltPullStatus = false;
	}

	@SuppressWarnings("static-access")
	@Override
	@Async
	public void startFetchingDataFromGdelt(Long clientAppID) {
		this.gdeltPullStatus = true;
		
		while(gdeltPullStatus){
            try {
            	String fileURL =  sendGet(gdeltURL);

                if(fileURL.indexOf(gdeltFileExtension) > -1){
                	boolean duplicateFound = clientAppSourceService.addExternalDataSouceWithClientAppID(fileURL, clientAppID);
                	if(!duplicateFound) {
                		List<NewsImage> newsImages = readDataFromFile(fileURL, clientAppID);
	                	newsImageDao.saveAll(newsImages);
                	}
                }
                Thread.sleep(900000);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Error in fetching gdelt data", e);
            } catch (Exception e) {
                logger.warn("Error in fetching gdelt data", e);
            }
        }
	}
	
	// Fetching data from file and parse data in to list of NewsImage
	private List<NewsImage> readDataFromFile(String fileURL, Long clientAppID) throws MalformedURLException, IOException {
    	InputStream input = new URL(fileURL).openStream();
    	Reader reader = new InputStreamReader(input, "UTF-8");
    	CSVReader csvReader = new CSVReader(reader);
    	String[] row = null;
    	List<NewsImage> newsImages = new ArrayList<>();
    	while((row = csvReader.readNext()) != null) {
    		if(row[1].indexOf("http") > -1) {
    		 NewsImage newsImage = new NewsImage(row[0], row[1], row[6], row[2], row[3], row[4], row[5], row[7]);
    		 newsImage.setClientAppID(clientAppID);
    	     newsImages.add(newsImage);
    		}
    	}
    	csvReader.close();
    	return newsImages;
	}
	
	private String sendGet(String url) {
        HttpURLConnection con = null;
        StringBuffer response = new StringBuffer();
        try {
            URL connectionURL = new URL(url);
            con = (HttpURLConnection) connectionURL.openConnection();

            con.setRequestMethod(HttpMethod.GET.name());
            con.setRequestProperty(HttpHeaders.USER_AGENT, "Mozilla/5.0");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.indexOf(gdeltFileExtension) > -1){
                    response.append(inputLine);
                }
            }
            in.close();
        }catch (Exception ex) {
            logger.warn("Error in sending request for url = " + url, ex);
        }
        return response.toString();
    }
}
