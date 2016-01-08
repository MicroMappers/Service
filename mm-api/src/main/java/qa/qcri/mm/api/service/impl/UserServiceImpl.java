package qa.qcri.mm.api.service.impl;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.RoleType;
import qa.qcri.mm.api.aidr_predict_entity.UserAccount;
import qa.qcri.mm.api.aidr_predict_entity.UserAccountRole;
import qa.qcri.mm.api.repository.UserAccountRepository;
import qa.qcri.mm.api.repository.UserAccountRoleRepository;
import qa.qcri.mm.api.service.UserService;


@Service("userService")
public class UserServiceImpl implements UserService{
    private Logger logger = Logger.getLogger(getClass());

	//@Resource(name="userRepository")
	@Autowired
	private UserAccountRepository userRepository;
	
	@Autowired
    private UserAccountRoleRepository userRoleRepository;
    
	@Override
	@Transactional(readOnly=false)
	public void save(UserAccount user) {
		userRepository.save(user);
	}
	
	@Override
	@Transactional(readOnly=true)
	public UserAccount fetchByUserName(String username) {
		return userRepository.findByUserName(username);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<RoleType> getUserRoles(Long userId) {
		List<UserAccountRole> roles = userRoleRepository.findByAccountId(userId);
		
		List<RoleType> roleTypes = new ArrayList<RoleType>();
		if(roles != null) {
			for(UserAccountRole role : roles) {
				roleTypes.add(role.getRole().getRoleType());
			}
		}
		return roleTypes;
	}
}
