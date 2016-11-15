package qa.qcri.mm.trainer.pybossa.entityForPybossa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.json.JSONObject;



/**
 * Created by Kushal
 */
@Entity
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = JSONObjectUserType.class) })
@Table(name = "project")
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3343407538542566784L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Integer id;

	@Column (name = "updated", columnDefinition="TEXT")
    private String updated;
	    
    @Column (name = "created", columnDefinition="TEXT")
    private String created;
    
    @Column (name = "name", nullable = false, unique=true, length=255)
    private String name;

    @Column (name = "short_name", nullable = false, unique=true, length=255)
    private String shortName;

    @Column (name = "description", nullable = false, length=255)
    private String description;

    @Column (name = "long_description", columnDefinition="TEXT")
    private String long_description;

    @Column (name = "webhook", columnDefinition="TEXT")
    private String webhook;

    @Column(name="allow_anonymous_contributors")
   	private Boolean allow_anonymous_contributors;
    
    @Column (name = "long_tasks")
    private Integer longTasks;
    
    @Column (name = "hidden")
    private Integer hidden;

    @Column (name = "featured", nullable = false)
    private Boolean featured;

    @Column (name = "contacted", nullable = false)
    private Boolean contacted;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @Column (name = "time_estimate")
    private Integer timeEstimate;
    
    @Column (name = "time_limit")
    private Integer timeLimit;
    
    @Column (name = "calibration_frac")
    private Double calibrationFrac;

    @Column (name = "bolt_course_id")
    private Integer boltCourseId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name="info")
   	@Type(type = "CustomJsonObject")
   	private JSONObject info;
    
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLong_description() {
		return long_description;
	}

	public void setLong_description(String long_description) {
		this.long_description = long_description;
	}

	public String getWebhook() {
		return webhook;
	}

	public void setWebhook(String webhook) {
		this.webhook = webhook;
	}

	public Boolean getAllow_anonymous_contributors() {
		return allow_anonymous_contributors;
	}

	public void setAllow_anonymous_contributors(Boolean allow_anonymous_contributors) {
		this.allow_anonymous_contributors = allow_anonymous_contributors;
	}

	public Integer getLongTasks() {
		return longTasks;
	}

	public void setLongTasks(Integer longTasks) {
		this.longTasks = longTasks;
	}

	public Integer getHidden() {
		return hidden;
	}

	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}

	public Boolean getFeatured() {
		return featured;
	}

	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}

	public Boolean getContacted() {
		return contacted;
	}

	public void setContacted(Boolean contacted) {
		this.contacted = contacted;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getTimeEstimate() {
		return timeEstimate;
	}

	public void setTimeEstimate(Integer timeEstimate) {
		this.timeEstimate = timeEstimate;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Double getCalibrationFrac() {
		return calibrationFrac;
	}

	public void setCalibrationFrac(Double calibrationFrac) {
		this.calibrationFrac = calibrationFrac;
	}

	public Integer getBoltCourseId() {
		return boltCourseId;
	}

	public void setBoltCourseId(Integer boltCourseId) {
		this.boltCourseId = boltCourseId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public JSONObject getInfo() {
		return info;
	}

	public void setInfo(JSONObject info) {
		this.info = info;
	}
}
