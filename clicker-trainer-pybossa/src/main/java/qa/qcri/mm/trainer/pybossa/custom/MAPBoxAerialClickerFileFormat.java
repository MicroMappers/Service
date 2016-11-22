package qa.qcri.mm.trainer.pybossa.custom;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import qa.qcri.mm.trainer.pybossa.entity.ImageMetaData;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 3/28/15
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class MAPBoxAerialClickerFileFormat {

	protected static Logger logger = Logger.getLogger(MAPBoxAerialClickerFileFormat.class);
	
    public static JSONArray createAerialClickerData(List<ImageMetaData> metaDataList){
        JSONArray jsonArray = new JSONArray();
        JSONArray sizes = getBoundSize();
        JSONParser parser = new JSONParser();
        try{
            //metaDataList.get(i).getBounds()
        	for (ImageMetaData metadata : metaDataList) {
	
        		JSONObject jsonObject = new JSONObject();
        		JSONArray bounds = (JSONArray)parser.parse(metadata.getBounds());
	
        		jsonObject.put("bounds", bounds);
        		jsonObject.put("size", sizes);
        		jsonObject.put("latlng",getLatLng(metadata));
        		jsonObject.put("source", metadata.getFileName());
		
        		jsonObject.put("url", metadata.getFileName());
		
        		jsonArray.add(jsonObject);
        	}
        }
        catch (Exception e){
           logger.error("Excpetion in MAPBoxAerialClickerFileFormat.createAerialClickerData - " + e.getMessage());
        }
        return jsonArray;
    }

    public static JSONArray getBoundSize(){
        JSONArray boundSize = new JSONArray();
        boundSize.add(new Integer(256));
        boundSize.add(new Integer(256));

        return boundSize;
    }

    public static JSONArray getLatLng(ImageMetaData imageMetaData){
        JSONArray coordinates = new JSONArray();
        coordinates.add(Double.parseDouble(imageMetaData.getLat()));
        coordinates.add(Double.parseDouble(imageMetaData.getLng()));

        return coordinates;
    }


}
