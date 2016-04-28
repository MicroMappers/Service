package qa.qcri.mm.api.service.impl;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.aidr_predict_entity.UserAccountActivity;
import qa.qcri.mm.api.repository.UserAccountActivityRepository;
import qa.qcri.mm.api.service.UserAcountActivityService;


@Service("userAccountActivityService")
public class UserAcountActivityServiceImpl implements UserAcountActivityService{

	@Autowired
    private UserAccountActivityRepository userAccountActivityRepository;
    
	@Override
	@Transactional(readOnly=false)
	public void save(UserAccountActivity userAccountActivity) {
		userAccountActivityRepository.save(userAccountActivity);
	}
	
	@Override
	public List<UserAccountActivity> findByAccountIdandActivityDate(Long id, Date fromDate, Date toDate) {
		return userAccountActivityRepository.findByAccountIdandActivityDate(id, fromDate, toDate);
	}
		
}
