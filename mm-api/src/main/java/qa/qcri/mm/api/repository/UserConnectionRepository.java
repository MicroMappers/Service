package qa.qcri.mm.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.aidr_predict_entity.UserConnection;

@Repository
public interface UserConnectionRepository extends CrudRepository<UserConnection, Long>{

	List<UserConnection> findByUserId(String userId);


}
