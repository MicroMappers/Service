package qa.qcri.mm.api.template;

import java.util.Date;

public class CrisisModel {

	private Long id;
	private Long crisisID;
	private Long clientAppID;
	private String displayName;
	private String description;
    private Date activationStart;
    private Date activationEnd;
    private String clickerType;
    private String bounds;
    
	public CrisisModel() {
		super();
	}
	public CrisisModel(Long id, Long crisisID, Long clientAppID,
			String displayName, String description, Date activationStart,
			Date activationEnd, String clickerType, String bounds) {
		super();
		this.id = id;
		this.crisisID = crisisID;
		this.clientAppID = clientAppID;
		this.displayName = displayName;
		this.description = description;
		this.activationStart = activationStart;
		this.activationEnd = activationEnd;
		this.clickerType = clickerType;
		this.bounds = bounds;
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
	public String getBounds() {
		return bounds;
	}
	public void setBounds(String bounds) {
		this.bounds = bounds;
	}
}
