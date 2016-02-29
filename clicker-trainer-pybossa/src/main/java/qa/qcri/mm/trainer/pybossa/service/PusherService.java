package qa.qcri.mm.trainer.pybossa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pusher.rest.Pusher;
import com.pusher.rest.data.Result;

@Service("pusherService")
@Transactional(readOnly = true)
public class PusherService {
	
	Pusher pusher = new Pusher("143040", "1eb98c94c2976297709d", "cd8e0fcfc1cc785ccd4a");
	{
		pusher.setRequestTimeout(10000);
	}
	
	public Result triggerNotification(Long crisisID, Long clientAppID, String clickerType, String crisisName, long processStartTime){
		Map<String, Object> map = new HashMap();
		map.put("crisisID", crisisID);
		map.put("clientAppID", clientAppID);
		map.put("clickerType", clickerType);
		map.put("crisisName", crisisName);
		map.put("processStartTime", processStartTime);
		return pusher.trigger("micromaps", "location_added", map);
	}
	
}
