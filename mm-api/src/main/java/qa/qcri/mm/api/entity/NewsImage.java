package qa.qcri.mm.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "news_image")
public class NewsImage implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column (name = "client_app_id", nullable = true)
    private Long clientAppID;

    @Column (name = "language")
    private String language;
    
    @Column (name = "article_link")
    private String articleLink;
    
    @Column (name = "image_url")
    private String imageURL;
    
    @Column (name = "created")
    private String created;
    
    @Column (name = "location")
    private String location;
    
    @Column (name = "latitude")
    private String latitude;
    
    @Column (name = "longitude")
    private String longitude;
    
    @Column (name = "crisis_code")
    private String crisisCode;
    
	public NewsImage() {
		super();
	}

	public NewsImage(String language, String articleLink, String imageURL,
			String created, String location, String latitude, String longitude,
			String crisisCode) {
		super();
		this.language = language;
		this.articleLink = articleLink;
		this.imageURL = imageURL;
		this.created = created;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		this.crisisCode = crisisCode;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getArticleLink() {
		return articleLink;
	}

	public void setArticleLink(String articleLink) {
		this.articleLink = articleLink;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCrisisCode() {
		return crisisCode;
	}

	public void setCrisisCode(String crisisCode) {
		this.crisisCode = crisisCode;
	}
    
}
