package qa.qcri.mm.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 5/12/15
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "crisis")
public class Crisis implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column (name = "collection_id", nullable = true)
    private Long crisisID;

//    @Column (name = "crisisName", nullable = true)
//    private String crisisName;

    @Column (name = "client_app_id", nullable = true)
    private Long clientAppID;

    @Column (name = "display_name", nullable = true)
    private String displayName;

    @Column (name = "description", nullable = true)
    private String description;

    @Column (name = "activation_start", nullable = true)
    private Date activationStart;

    @Column (name = "activation_end", nullable = true)
    private Date activationEnd;

    @Column (name = "clicker_type", nullable = true)
    private String clickerType;
    
    @Column (name = "bounds")
    private String bounds;

//    @Column (name = "refreshInMinute", nullable = true)
//    private Integer refreshInMinute;

    @OneToOne
    @JoinColumn(name="client_app_id" ,nullable = false, insertable = false, updatable = false)
    private ClientApp clientApp;


    public Crisis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCrisisID() {
        return crisisID;
    }

    public void setCrisisID(Long crisisID) {
        this.crisisID = crisisID;
    }

//    public String getCrisisName() {
//        return crisisName;
//    }

//    public void setCrisisName(String crisisName) {
//        this.crisisName = crisisName;
//    }

    public Long getClientAppID() {
        return clientAppID;
    }

    public void setClientAppID(Long clientAppID) {
        this.clientAppID = clientAppID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActivationStart() {
        return activationStart;
    }

    public void setActivationStart(Date activationStart) {
        this.activationStart = activationStart;
    }

    public Date getActivationEnd() {
        return activationEnd;
    }

    public void setActivationEnd(Date activationEnd) {
        this.activationEnd = activationEnd;
    }

    public String getClickerType() {
        return clickerType;
    }

    public void setClickerType(String clickerType) {
        this.clickerType = clickerType;
    }

    public ClientApp getClientApp() {
        return clientApp;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

	public String getBounds() {
		return bounds;
	}

	public void setBounds(String bounds) {
		this.bounds = bounds;
	}
    

/*    public Integer getRefreshInMinute() {
        return refreshInMinute;
    }

    public void setRefreshInMinute(Integer refreshInMinute) {
        this.refreshInMinute = refreshInMinute;
    }*/
}
