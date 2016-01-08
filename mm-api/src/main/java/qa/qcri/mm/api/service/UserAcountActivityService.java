package qa.qcri.mm.api.service;

import java.util.Date;
import java.util.List;

import qa.qcri.mm.api.aidr_predict_entity.UserAccountActivity;

public interface UserAcountActivityService {

	public void save(UserAccountActivity userAccountActivity);
	public List<UserAccountActivity> findByAccountIdandActivityDate(Long id, Date fromDate, Date toDate);

}
