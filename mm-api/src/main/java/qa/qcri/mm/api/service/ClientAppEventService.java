package qa.qcri.mm.api.service;

import qa.qcri.mm.api.entity.ClientAppEvent;


public interface ClientAppEventService {
	void update(ClientAppEvent markerStyle);
	void save(ClientAppEvent markerStyle);
	ClientAppEvent getClientAppEvent(Long clientAppId);
}
