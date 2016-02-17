package qa.qcri.mm.api.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import qa.qcri.mm.api.dao.NewsImageDao;
import qa.qcri.mm.api.entity.NewsImage;
import qa.qcri.mm.api.service.ClientAppSourceService;
import qa.qcri.mm.api.service.NewsImageService;
import au.com.bytecode.opencsv.CSVReader;

@Service("newsImageService")
public class NewsImageServiceImpl implements NewsImageService {

	@Autowired
    private ClientAppSourceService clientAppSourceService;
	
	@Autowired
	private NewsImageDao newsImageDao;
	
	private static boolean running = false;
	
	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	@SuppressWarnings("static-access")
	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	@Async
	public void pull(Long clientAppID) {
		while(running){
            try {
                String fileURL =  sendGet("http://data.gdeltproject.org/micromappers/lastupdate.txt");
                System.out.println("Fetching Data.....");

                if(fileURL.indexOf(".mmic.txt") > -1){
                	boolean dublicateFound = clientAppSourceService.addExternalDataSouceWithClientAppID(fileURL, clientAppID);
                	
                	if(!dublicateFound) {
	                	// Saving file data into database
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
	                	System.out.println("Saving NewsImages :" +newsImages.size());
	                	newsImageDao.saveAll(newsImages);
	                	System.out.println("Saved.");
	                	csvReader.close();
                	}
                }
                Thread.sleep(900000);
                //Thread.sleep(10000);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	private String sendGet(String url) {
        HttpURLConnection con = null;
        StringBuffer response = new StringBuffer();
        try {
            URL connectionURL = new URL(url);
            con = (HttpURLConnection) connectionURL.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                if(inputLine.indexOf(".mmic.txt") > -1){
                    response.append(inputLine);
                }
            }
            in.close();
        }catch (Exception ex) {
            System.out.println("ex Code sendGet: " + ex + " : sendGet url = " + url);
            System.out.println("[errror on sendGet ]" + url);
        }
        return response.toString();
    }
}
