package qa.qcri.mm.drone.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import qa.qcri.mm.drone.api.store.SubscribeFrequency;

@Entity
@Table(catalog = "mm_uaviators",name = "subscribeuser")
public class SubscribeUser implements Serializable {
	
	private static final long serialVersionUID = -1675411425679142443L;

	@Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
 	
 	@Column (name = "name")
    private String name;
 	
 	@Column (name = "email", nullable = false, unique = true)
    private String email;
 	
 	@Column (name = "subscribeFrequency", nullable = false)
    private SubscribeFrequency subscribeFrequency;
 	
	public SubscribeUser() {
		super();
	}

	public SubscribeUser(String name, String email, SubscribeFrequency subscribeFrequency) {
		super();		
		this.name = name;
		this.email = email;
		this.subscribeFrequency = subscribeFrequency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SubscribeFrequency getSubscribeFrequency() {
		return subscribeFrequency;
	}

	public void setSubscribeFrequency(SubscribeFrequency subscribeFrequency) {
		this.subscribeFrequency = subscribeFrequency;
	}
 	
}
