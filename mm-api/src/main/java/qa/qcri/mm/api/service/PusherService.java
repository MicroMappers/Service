package qa.qcri.mm.api.service;

import java.util.Collections;

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
	
	public Result triggerNotification(){
		return pusher.trigger("channel-one", "test_event", Collections.singletonMap("message", "hello world"));
	}
	
}
