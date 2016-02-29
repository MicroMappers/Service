package qa.qcri.mm.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.RoleType;
import qa.qcri.mm.api.aidr_predict_entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>{

	public Role findByRoleType(RoleType roleType);

}
