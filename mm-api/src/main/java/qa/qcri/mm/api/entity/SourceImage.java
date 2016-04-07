package qa.qcri.mm.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 3/28/15
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "source_image")
public class SourceImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7849846941673572471L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "file_name", nullable = true)
    private String fileName;
    
    @ManyToOne
    @JoinColumn (name = "image_config_id")
    private ImageConfig imageConfig;
    
    @Column (name = "created_at")
    private Date createdAt;
    
    public SourceImage(ImageConfig imageConfig, String fileName) {
		super();
		this.imageConfig = imageConfig;
		this.fileName = fileName;
	}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ImageConfig getImageConfig() {
		return imageConfig;
	}

	public void setImageConfig(ImageConfig imageConfig) {
		this.imageConfig = imageConfig;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
    
}
