package qa.qcri.mm.api.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import qa.qcri.mm.api.aidr_predict_entity.UserAccountActivity;

public interface UserAccountActivityRepository extends CrudRepository<UserAccountActivity, Long>{

	@Query("SELECT u FROM UserAccountActivity u where u.account.id = :id and u.activityDate >= :fromDate and u.activityDate < :toDate") 
	public List<UserAccountActivity> findByAccountIdandActivityDate(@Param("id") Long id, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

}
