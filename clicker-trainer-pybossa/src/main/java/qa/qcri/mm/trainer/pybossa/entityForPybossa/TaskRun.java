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
@Table(name = "task_run")
public class TaskRun implements Serializable {

	public TaskRun(Integer id, String created, Project project, Task task, User user, String userIp, String finishTime,
			Integer timeout, Integer calibration, JSONObject info) {
		super();
		this.id = id;
		this.created = created;
		this.project = project;
		this.task = task;
		this.user = user;
		this.userIp = userIp;
		this.finishTime = finishTime;
		this.timeout = timeout;
		this.calibration = calibration;
		this.info = info;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2249177418601711058L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column (name = "created", columnDefinition="TEXT")
    private String created;
    
    @ManyToOne
    @JoinColumn (name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne
    @JoinColumn (name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;

    @Column (name = "user_ip", columnDefinition="TEXT")
    private String userIp;
    
    @Column (name = "finish_time", columnDefinition="TEXT")
    private String finishTime;

    @Column (name = "timeout")
    private Integer timeout;

    @Column (name = "calibration")
    private Integer calibration;
    
    @Column(name="info")
   	@Type(type = "CustomJsonObject")
   	private JSONObject info;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getCalibration() {
		return calibration;
	}

	public void setCalibration(Integer calibration) {
		this.calibration = calibration;
	}

	public JSONObject getInfo() {
		return info;
	}

	public void setInfo(JSONObject info) {
		this.info = info;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
