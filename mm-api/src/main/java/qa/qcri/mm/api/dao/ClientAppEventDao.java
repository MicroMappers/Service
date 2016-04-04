package qa.qcri.mm.api.dao;

import qa.qcri.mm.api.entity.ClientAppEvent;

public interface ClientAppEventDao extends AbstractDao<ClientAppEvent, String>  {

	void update(ClientAppEvent clientAppEvent);

}