package qa.qcri.mm.api.template;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/12/14
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClientAppModel {
    private Long id;
    private Long platformID;
    private Long crisisID;
    private String name;
    private String shortName;
    private Integer appType;
    private Integer status;
    private Integer taskAvailable;
    private Integer totalTask;

    public ClientAppModel(){}

    public ClientAppModel(Long id, Long platformID, Long crisisID, String name, String shortName, Integer appType) {
        this.id = id;
        this.platformID = platformID;
        this.crisisID = crisisID;
        this.name = name;
        this.shortName = shortName;
        this.appType = appType;
    }
    

    public ClientAppModel(Long id, Long platformID, Long crisisID, String name,
			String shortName, Integer appType, Integer status,
			Integer taskAvailable, Integer totalTask) {
		super();
		this.id = id;
		this.platformID = platformID;
		this.crisisID = crisisID;
		this.name = name;
		this.shortName = shortName;
		this.appType = appType;
		this.status = status;
		this.taskAvailable = taskAvailable;
		this.totalTask = totalTask;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatformID() {
        return platformID;
    }

    public void setPlatformID(Long platformID) {
        this.platformID = platformID;
    }

    public Long getCrisisID() {
        return crisisID;
    }

    public void setCrisisID(Long crisisID) {
        this.crisisID = crisisID;
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

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTaskAvailable() {
		return taskAvailable;
	}

	public void setTaskAvailable(Integer taskAvailable) {
		this.taskAvailable = taskAvailable;
	}

	public Integer getTotalTask() {
		return totalTask;
	}

	public void setTotalTask(Integer totalTask) {
		this.totalTask = totalTask;
	}
	
}
