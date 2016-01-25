package qa.qcri.mm.api.service;

import java.util.List;

import qa.qcri.mm.api.RoleType;
import qa.qcri.mm.api.aidr_predict_entity.UserAccount;

public interface UserService {

	public void save(UserAccount user);
	
	public List<RoleType> getUserRoles(Long userId);
	
	public UserAccount fetchByUserName(String username);

}
