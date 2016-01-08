package qa.qcri.mm.api.repository;

import org.springframework.data.repository.CrudRepository;

import qa.qcri.mm.api.aidr_predict_entity.UserAccount;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long>{

	public UserAccount findByUserName(String userName);

}
