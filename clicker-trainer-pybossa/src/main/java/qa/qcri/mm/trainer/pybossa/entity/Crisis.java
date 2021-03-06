package qa.qcri.mm.trainer.pybossa.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jlucas on 5/19/15.
 */
@Entity
@Table(name = "crisis")
public class Crisis implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Long id;


    @Column (name = "collection_id", nullable = true)
    private Long crisisID;

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
}
