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
@Table(name = "task")
public class Task implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5875156956444073467L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column (name = "quorum")
    private Integer quorum;

    @Column (name = "calibration")
    private Integer calibration;

    @Column (name = "state", columnDefinition="TEXT")
    private String state;

    @Column (name = "priority_0")
    private Double priority0;

    @Column (name = "n_answers")
    private Integer nAnswers;

    @Column (name = "created", columnDefinition="TEXT")
    private String created;
    
    @Column(name="info")
	@Type(type = "CustomJsonObject")
	private JSONObject info;
    
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Integer getQuorum() {
		return quorum;
	}

	public void setQuorum(Integer quorum) {
		this.quorum = quorum;
	}

	public Integer getCalibration() {
		return calibration;
	}

	public void setCalibration(Integer calibration) {
		this.calibration = calibration;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Double getPriority0() {
		return priority0;
	}

	public void setPriority0(Double priority0) {
		this.priority0 = priority0;
	}

	public Integer getnAnswers() {
		return nAnswers;
	}

	public void setnAnswers(Integer nAnswers) {
		this.nAnswers = nAnswers;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public JSONObject getInfo() {
		return info;
	}

	public void setInfo(JSONObject info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", project=" + project + ", quorum=" + quorum + ", calibration=" + calibration
				+ ", state=" + state + ", priority0=" + priority0 + ", nAnswers=" + nAnswers + ", created=" + created
				+ ", info=" + info + "]";
	}

   
}
