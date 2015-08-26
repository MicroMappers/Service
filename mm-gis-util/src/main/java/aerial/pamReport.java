package aerial;

import au.com.bytecode.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 4/6/15
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class pamReport {

    public static void main(String[] args)  {
        /**
       int polycount =  getKMLSummeryDataSetByLayerType("MM_Aerial_PAM", "polygon");
       System.out.println("polycount : " + polycount);
       int polycount2 =  getKMLSummeryDataSetByLayerType("MM_Aerial_PAM", "polygon2");
       System.out.println("polycount2 : " + polycount2);
       int polycount3 = getKMLSummeryDataSetByLayerType("MM_Aerial_PAM", "polygon3");
       System.out.println("polycount3 : " + polycount3);
       **/
        generateCVS();
    }

    private static void generateCVS(){

        JSONParser cParser = new JSONParser();
        try{
            //String arrayString = sendGet("http://gis.micromappers.org/MMAPI/rest/pam/reports/MM_Aerial_PAM");


            String arrayString = sendGet("http://localhost:8081/MMAPI/rest/pam/reports/MM_Aerial_PAM");
            JSONArray reportList = (JSONArray)cParser.parse(arrayString)  ;


            CSVWriter writer = new CSVWriter(new FileWriter("/Users/jlucas/Documents/imagery/pam/PAM_Report/MM_Aerial_PAM_heat_recounter11.csv", true));
       //"taskid\":603327,\"lng\":\"168.3686111111111\",\"lat\":\"-17.48222222222222\",\"imgurl\":\"http:\\/\\/aidr-prod.qcri.org\\/data\\/trainer\\/pam\\/59KKA2065_1_oblique_02042015_1315\\/slice\\/59KKA2065_1_oblique_02042015_1315 (10)_1.jpg\"
            String[] header = {"taskid", "lng","lat","imgurl", "red avg", "orange avg","blue avg", "total tracing", " total average"};
            writer.writeNext(header);


            for(int i= 0; i < reportList.size(); i++){
                JSONObject aReport = (JSONObject)reportList.get(i);
                String temp = (String)aReport.get("response");
                JSONObject obj = (JSONObject)cParser.parse(temp);
                JSONArray geo = (JSONArray)obj.get("geo");

                if(geo.size() > 0){
                    String[] data = new String[4];
                    data[0] =  String.valueOf(obj.get("taskid"));
                    data[1] =  String.valueOf(obj.get("lng"));
                    data[2] =  String.valueOf(obj.get("lat"));
                    data[3] =  (String)obj.get("imgurl");

                    processGEO(geo, data, writer);

                }
            }
            writer.close();


        }
        catch(Exception e){
            System.out.println("GetKMLSummeryDataSetForReport ERROR : " + e.toString());
        }

    }


    private static void processGEO(JSONArray geo, String[] metaData, CSVWriter writer){
        int requestedLayerTypeCount = 0;
        String[] data = new String[9];
        data[0] =  metaData[0];
        data[1] =  metaData[1];
        data[2] =  metaData[2];
        data[3] =  metaData[3];

        int red = 0;
        int blue = 0;
        int orange = 0;

        for(int i=0; i < geo.size() ; i++){


            JSONObject obj = (JSONObject) geo.get(i);
            JSONArray geoInfo = (JSONArray)obj.get("geo");
            /**
            if(obj.get("userID")== null){
                data[4] = "NULL";
            }
            else{
                data[4] =  String.valueOf(obj.get("userID"));
            }   **/


            //data[6] = geoInfo.toJSONString();
            for(int j=0; j < geoInfo.size(); j++){
                JSONObject layerObj =(JSONObject) geoInfo.get(j);

                String layterType = (String)layerObj.get("layerType");
                layterType = layterType.trim();
                JSONObject geoJsonLayer = (JSONObject)layerObj.get("layer") ;
                //data[7] = geoJsonLayer.toJSONString();
                if(layterType.equals("polygon")) {
                    //data[5] = "red";
                    red = red + 1;
                }
                if(layterType.equals("polygon2")) {
                    //data[5] = "orange";
                    orange = orange + 1;
                }
                if(layterType.equals("polygon3")) {
                  //  data[5] = "blue";
                    blue = blue + 1;
                }


            }
        }
        double redA = (double)(red/3.0);
        double orgA = (double)(orange/3.0);
        double blueA = (double)(blue/3.0);

        data[4] = redA + "";
        data[5] = orgA+ "";
        data[6] = blueA+ "";
        data[7] = red + orange+blue + "";
        double ave =  ( (redA + orgA+blueA) / 3.0);
        data[8]  = ave + "";
        // int[] ints = {red, orange, blue};
        // Arrays.sort(ints);
        // Arrays.asList(ints);

        writer.writeNext(data);
    }

    private static int processGeoByType(JSONArray geo, String requestedLayerType){
        int requestedLayerTypeCount = 0;

        for(int i=0; i < geo.size() ; i++){
            JSONObject obj = (JSONObject) geo.get(i);
            JSONArray geoInfo = (JSONArray)obj.get("geo");
            for(int j=0; j < geoInfo.size(); j++){
                JSONObject layerObj =(JSONObject) geoInfo.get(j);

                String layterType = (String)layerObj.get("layerType");
                layterType = layterType.trim();
                requestedLayerType = requestedLayerType.trim();

                if(layterType.equals(requestedLayerType)) {
                    requestedLayerTypeCount = requestedLayerTypeCount + 1;
                }

            }

        }
        return requestedLayerTypeCount;
    }

    public static int getKMLSummeryDataSetByLayerType(String shortName, String layerType) {
        int sumOfLayerCount = 0;
        JSONParser cParser = new JSONParser();
        try{
            String arrayString = sendGet("http://gis.micromappers.org/MMAPI/rest/pam/reports/MM_Aerial_PAM");

            JSONArray reportList = (JSONArray)cParser.parse(arrayString)  ;



            for(int i= 0; i < reportList.size(); i++){
                JSONObject aReport = (JSONObject)reportList.get(i);
                String temp = (String)aReport.get("response");
                JSONObject obj = (JSONObject)cParser.parse(temp);
                JSONArray geo = (JSONArray)obj.get("geo");

                if(geo.size() >= 3){
                    int returnCount = processGeoByType(geo, layerType);
                    sumOfLayerCount = sumOfLayerCount +  returnCount;
                }
            }


        }
        catch(Exception e){
            System.out.println("GetKMLSummeryDataSetForReport ERROR : " + e.toString());
        }

        return sumOfLayerCount;
    }

    public static String sendGet(String url) {
        HttpURLConnection con = null;
        StringBuffer response = new StringBuffer();
        // System.out.println("sendGet url : " + url);
        // logger.debug("[sendGet url  for debugger: ]" + url);

        try {
            URL connectionURL = new URL(url);
            con = (HttpURLConnection) connectionURL.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = con.getResponseCode();


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"UTF-8"));
            String inputLine;
            // logger.debug("[response code ]" + responseCode);
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        }catch (Exception ex) {
            System.out.println("ex Code sendGet: " + ex + " : sendGet url = " + url);
            System.out.println("[errror on sendGet ]" + url);
        }

        return response.toString();
    }
}
