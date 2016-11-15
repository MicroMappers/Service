package qa.qcri.mm.trainer.pybossa.entityForPybossa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "\"user\"")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4868919277738652409L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column (name = "created", columnDefinition="TEXT")
    private String created;
    
    @Column (name = "email_addr", nullable = false, length=254, unique=true)
    private String emailAddr;

    @Column (name = "name", nullable = false, length=254, unique=true)
    private String name;

    @Column (name = "fullname", nullable = false, length=500)
    private String fullname;

    @Column (name = "locale", nullable = false, length=254)
    private String locale;
    
    @Column (name = "api_key", length=36, unique=true)
    private String apiKey;

    @Column (name = "passwd_hash", length=254, unique=true)
    private String passwdHash;
    
    @Column(name="admin")
   	private Boolean admin;
    
    @Column(name="pro")
   	private Boolean pro;
    
    @Column(name="privacy_mode", nullable = false)
   	private Boolean privacyMode;
    
    @Column (name = "category")
    private Integer category;
    
    @Column (name = "flags")
    private Integer flags;

    @Column (name = "twitter_user_id", unique=true)
    private Long twitterUserId;

    @Column (name = "facebook_user_id", unique=true)
    private Long facebookUserId;

    @Column (name = "google_user_id", unique=true)
    private String googleUserId;

    @Column (name = "ckan_api", unique=true)
    private String ckanApi;
    
    @Column (name = "newsletter_prompted")
    private Boolean newsletterPrompted;
    
    @Column (name = "valid_email")
    private Boolean validEmail;

    @Column (name = "confirmation_email_sent")
    private Boolean confirmationEmailSent;

    @Column (name = "subscribed")
    private Boolean subscribed;
    
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

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getPasswdHash() {
		return passwdHash;
	}

	public void setPasswdHash(String passwdHash) {
		this.passwdHash = passwdHash;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Boolean getPro() {
		return pro;
	}

	public void setPro(Boolean pro) {
		this.pro = pro;
	}

	public Boolean getPrivacyMode() {
		return privacyMode;
	}

	public void setPrivacyMode(Boolean privacyMode) {
		this.privacyMode = privacyMode;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getFlags() {
		return flags;
	}

	public void setFlags(Integer flags) {
		this.flags = flags;
	}

	public Long getTwitterUserId() {
		return twitterUserId;
	}

	public void setTwitterUserId(Long twitterUserId) {
		this.twitterUserId = twitterUserId;
	}

	public Long getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(Long facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	public String getGoogleUserId() {
		return googleUserId;
	}

	public void setGoogleUserId(String googleUserId) {
		this.googleUserId = googleUserId;
	}

	public String getCkanApi() {
		return ckanApi;
	}

	public void setCkanApi(String ckanApi) {
		this.ckanApi = ckanApi;
	}

	public Boolean getNewsletterPrompted() {
		return newsletterPrompted;
	}

	public void setNewsletterPrompted(Boolean newsletterPrompted) {
		this.newsletterPrompted = newsletterPrompted;
	}

	public Boolean getValidEmail() {
		return validEmail;
	}

	public void setValidEmail(Boolean validEmail) {
		this.validEmail = validEmail;
	}

	public Boolean getConfirmationEmailSent() {
		return confirmationEmailSent;
	}

	public void setConfirmationEmailSent(Boolean confirmationEmailSent) {
		this.confirmationEmailSent = confirmationEmailSent;
	}

	public Boolean getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;
	}

	public JSONObject getInfo() {
		return info;
	}

	public void setInfo(JSONObject info) {
		this.info = info;
	}
}
