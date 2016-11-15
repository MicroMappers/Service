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



/**
 * Created by Kushal
 */
@Entity
@Table(name = "blogpost")
public class BlogPost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2351660312411486094L;

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
    @JoinColumn (name = "user_id")
    private User user;

    @Column (name = "title", length=255, nullable = false)
    private String title;

    @Column (name = "body", columnDefinition="TEXT", nullable = false)
    private String body;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
    
}
