package qa.qcri.mm.api.service;

import java.util.List;

import qa.qcri.mm.api.aidr_predict_entity.UserConnection;

public interface UserConnectionService {
	public void register (UserConnection userConnection);
	
    public List<UserConnection> getByUserId (String userId);

    public void update (UserConnection userConnection);

}
