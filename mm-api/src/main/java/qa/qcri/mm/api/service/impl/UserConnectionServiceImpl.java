package qa.qcri.mm.api.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.aidr_predict_entity.UserConnection;
import qa.qcri.mm.api.repository.UserConnectionRepository;
import qa.qcri.mm.api.service.UserConnectionService;

@Service("userConnectionService")
public class UserConnectionServiceImpl implements UserConnectionService{

	@Inject
	private UserConnectionRepository userConnectionRepository;
	
	@Override
	@Transactional(readOnly=false)
	public void register(UserConnection userConnection) {
		userConnectionRepository.save(userConnection);
	}

    @Override
    @Transactional(readOnly = true)
    public List<UserConnection> getByUserId(String userId) {
        return userConnectionRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void update(UserConnection userConnection) {
        userConnectionRepository.save(userConnection);
    }
}
