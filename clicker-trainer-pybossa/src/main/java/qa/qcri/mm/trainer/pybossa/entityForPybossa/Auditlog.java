package qa.qcri.mm.trainer.pybossa.entityForPybossa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * Created by Kushal
 */
@Entity
@Table(name = "auditlog")
public class Auditlog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3918224980544233425L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column (name = "project_id", nullable = false)
    private Integer projectId;

    @Column (name = "project_short_name", columnDefinition="TEXT", nullable = false)
    private String projectShortName;

    @Column (name = "user_id", nullable = false)
    private Integer userId;
    
    @Column (name = "user_name", columnDefinition="TEXT", nullable = false)
    private String userName;

    @Column (name = "created", columnDefinition="TEXT", nullable = false)
    private String created;

    @Column (name = "action", columnDefinition="TEXT", nullable = false)
    private String action;

    @Column (name = "caller", columnDefinition="TEXT", nullable = false)
    private String caller;

    @Column (name = "attribute", columnDefinition="TEXT", nullable = false)
    private String attribute;

    @Column (name = "old_value", columnDefinition="TEXT")
    private String oldValue;

    @Column (name = "new_value", columnDefinition="TEXT")
    private String newValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectShortName() {
		return projectShortName;
	}

	public void setProjectShortName(String projectShortName) {
		this.projectShortName = projectShortName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

    
}
