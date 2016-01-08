package qa.qcri.mm.api.social.configuration;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth1Connection;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.stereotype.Component;

import qa.qcri.mm.api.RoleType;
import qa.qcri.mm.api.aidr_predict_entity.Role;
import qa.qcri.mm.api.aidr_predict_entity.UserAccount;
import qa.qcri.mm.api.aidr_predict_entity.UserAccountRole;
import qa.qcri.mm.api.aidr_predict_entity.UserConnection;
import qa.qcri.mm.api.repository.RoleRepository;
import qa.qcri.mm.api.repository.UserAccountRoleRepository;
import qa.qcri.mm.api.service.UserConnectionService;
import qa.qcri.mm.api.service.UserService;

@Component("userConnectionSignUp")
public class UserConnectionSignUp implements ConnectionSignUp {

	@Autowired
	private UserConnectionService userConnectionService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private UserAccountRoleRepository userRoleRepository;
	
	@Autowired
    private RoleRepository roleRepository;
	
	@Override
    public String execute(Connection<?> connection) {
		if(connection instanceof OAuth1Connection){
			OAuth1Connection<?> oauthConnection = (OAuth1Connection<?>) connection;
			ConnectionData data = oauthConnection.createData();
	        UserProfile profile = oauthConnection.fetchUserProfile();
	        UserConnection userConnection = new UserConnection();
	        userConnection.setUserId(profile.getUsername());
	        userConnection.setImageUrl(data.getImageUrl());
	        userConnection.setProfileUrl(data.getProfileUrl());
	        userConnection.setAccessToken(data.getAccessToken());
	        userConnection.setSecret(data.getSecret());
	        userConnection.setRefreshToken(data.getRefreshToken());
	        userConnection.setProviderUserId(profile.getUsername());
	        /**
	         * This is custom SignUp and for it providerID should be springSecurity
	         */
	        userConnection.setProviderId("springSocialSecurity");
	        userConnection.setDisplayName(data.getDisplayName());
	        userConnection.setRank(1);
	        userConnectionService.register(userConnection);
	        
	        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
	        UserAccount user = new UserAccount();
	        // TODO move to hibernate level
	        user.setApiKey(UUID.randomUUID().toString());
	        user.setCreatedAt(currentTimestamp);
	        user.setUpdatedAt(currentTimestamp);
	        user.setProvider(data.getProviderId());
	        user.setUserName(profile.getUsername());
	        userService.save(user);
	        
	        UserAccount userAccount = userService.fetchByUserName(profile.getUsername());
	        Role role = roleRepository.findByRoleType(RoleType.NORMAL);
	        UserAccountRole userAccountRole = new UserAccountRole(userAccount, role);
	        userRoleRepository.save(userAccountRole);	        
	        
	        return profile.getUsername();
		} else if (connection instanceof OAuth2Connection) {
			OAuth2Connection<?> oauthConnection = (OAuth2Connection<?>) connection;
			ConnectionData data = oauthConnection.createData();
			UserProfile profile = oauthConnection.fetchUserProfile();

			List<UserConnection> userConnections = userConnectionService.getByUserId(profile.getEmail());
			if (userConnections != null && !userConnections.isEmpty()) {
				return profile.getEmail();
			}

			UserConnection userConnection = new UserConnection();
			userConnection.setUserId(profile.getEmail());
			userConnection.setImageUrl(data.getImageUrl());
			userConnection.setProfileUrl(data.getProfileUrl());
			userConnection.setAccessToken(data.getAccessToken());
			userConnection.setSecret(data.getSecret());
			userConnection.setRefreshToken(data.getRefreshToken());
			userConnection.setProviderUserId(data.getProviderUserId());
			userConnection.setProviderId("springSocialSecurity");
			userConnection.setDisplayName(data.getDisplayName());
			userConnection.setRank(1);
			userConnectionService.register(userConnection);

			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			UserAccount user = new UserAccount();
			user.setApiKey(UUID.randomUUID().toString());
			user.setCreatedAt(currentTimestamp);
			user.setUpdatedAt(currentTimestamp);
			user.setProvider(data.getProviderId());
			user.setUserName(profile.getEmail());
			user.setEmail(profile.getEmail());
			userService.save(user);
			
			UserAccount userAccount = userService.fetchByUserName(profile.getEmail());
	        Role role = roleRepository.findByRoleType(RoleType.NORMAL);
	        UserAccountRole userAccountRole = new UserAccountRole(userAccount, role);
	        userRoleRepository.save(userAccountRole);
			
			return profile.getEmail();
		}
		
		return connection.fetchUserProfile().getUsername();
    }

}
