package qa.qcri.mm.api.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by jlucas on 5/19/15.
 */
@Entity
@Table(name = "marker_style")
public class MarkerStyle implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column (name = "client_app_id", nullable = false)
    private Long clientAppID;

    @Column (name = "app_type", nullable = false)
    private String appType;

    @Column (name = "style", nullable = false)
    private String style;

    @Column (name = "is_default", nullable = false)
    private boolean isDefault;

    public MarkerStyle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientAppID() {
        return clientAppID;
    }

    public void setClientAppID(Long clientAppID) {
        this.clientAppID = clientAppID;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
