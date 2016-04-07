package qa.qcri.mm.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "client_app_event")
public class ClientAppEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long clientAppEventID;

    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "client_app_id", nullable = false)
    private Long clientAppID;

    @Column (name = "sequence", nullable = false)
    private Integer sequence;

    @Column (name = "event_id", nullable = false)
    private Long eventID;

    @Column (name = "created", nullable = true)
    private Date created;
    
    public ClientAppEvent() {
		super();
	}

	public ClientAppEvent(Long clientAppEventID, String name, Long clientAppID,
			Integer sequence, Long eventID, Date created) {
		super();
		this.clientAppEventID = clientAppEventID;
		this.name = name;
		this.clientAppID = clientAppID;
		this.sequence = sequence;
		this.eventID = eventID;
		this.created = created;
	}

	public Long getClientAppID() {
        return clientAppID;
    }

    public void setClientAppID(Long clientAppID) {
        this.clientAppID = clientAppID;
    }

    public Long getClientAppEventID() {
        return clientAppEventID;
    }

    public void setClientAppEventID(Long clientAppEventID) {
        this.clientAppEventID = clientAppEventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


}