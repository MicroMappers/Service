package qa.qcri.mm.api.aidr_predict_entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="account_role", uniqueConstraints=@UniqueConstraint(name="account_role_unique_key", columnNames={"account_id", "role_id"}))
public class UserAccountRole extends BaseEntity {
	
	private static final long serialVersionUID = 7176454786035441960L;

	@NotNull
	@ManyToOne
	@JoinColumn(name="account_id")
	private UserAccount account;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="role_id")
	private Role role;
	
	public UserAccountRole() {
		super();
	}
	
	public UserAccountRole(UserAccount account, Role role) {
		super();
		this.account = account;
		this.role = role;
	}

	public UserAccount getAccount() {
		return account;
	}

	public void setAccount(UserAccount account) {
		this.account = account;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
