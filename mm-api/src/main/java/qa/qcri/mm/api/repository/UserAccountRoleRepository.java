package qa.qcri.mm.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import qa.qcri.mm.api.aidr_predict_entity.UserAccountRole;

public interface UserAccountRoleRepository extends CrudRepository<UserAccountRole, Long>{
	
	public List<UserAccountRole> findByAccountId(Long userId);
	
}
