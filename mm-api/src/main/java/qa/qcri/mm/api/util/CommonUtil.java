package qa.qcri.mm.api.util;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

	public static Map<String, Object> returnSuccess(String msg, Object data){
		Map<String, Object> map = new HashMap<>();
		map.put("success", true);
		map.put("data", data);
		map.put("msg", msg);
		return map;
	}
	
	public static Map<String, Object> returnError(String msg){
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		map.put("msg", msg);
		return map;
	}
	
}
