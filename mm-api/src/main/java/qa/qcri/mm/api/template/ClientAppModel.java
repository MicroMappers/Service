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
    private Long crisisSrID;
    private Long crisisID;
    private String crisisName;
    private String classifierName;
    private String name;
    private String shortName;
    private Integer appType;
    private Integer status;
    private Long taskAvailable;
    private Long totalTask;
    private Integer taskRunsPerTask;
    private Boolean isCustom;
    private Integer tcProjectID;

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
			Long taskAvailable, Long totalTask) {
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

	public ClientAppModel(Long id, Long platformID, Long crisisSrID, Long crisisID,
			String crisisName, String classifierName, String name,
			String shortName, Integer appType, Integer status,
			Long taskAvailable, Long totalTask, Integer taskRunsPerTask,
			Boolean isCustom, Integer tcProjectID) {
		super();
		this.id = id;
		this.platformID = platformID;
		this.crisisSrID = crisisSrID;
		this.crisisID = crisisID;
		this.crisisName = crisisName;
		this.classifierName = classifierName;
		this.name = name;
		this.shortName = shortName;
		this.appType = appType;
		this.status = status;
		this.taskAvailable = taskAvailable;
		this.totalTask = totalTask;
		this.taskRunsPerTask = taskRunsPerTask;
		this.isCustom = isCustom;
		this.tcProjectID = tcProjectID;
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

	public Long getTaskAvailable() {
		return taskAvailable;
	}

	public void setTaskAvailable(Long taskAvailable) {
		this.taskAvailable = taskAvailable;
	}

	public Long getTotalTask() {
		return totalTask;
	}

	public void setTotalTask(Long totalTask) {
		this.totalTask = totalTask;
	}

	public Integer getTaskRunsPerTask() {
		return taskRunsPerTask;
	}

	public void setTaskRunsPerTask(Integer taskRunsPerTask) {
		this.taskRunsPerTask = taskRunsPerTask;
	}

	public Boolean getIsCustom() {
		return isCustom;
	}

	public void setIsCustom(Boolean isCustom) {
		this.isCustom = isCustom;
	}

	public Integer getTcProjectID() {
		return tcProjectID;
	}

	public void setTcProjectID(Integer tcProjectID) {
		this.tcProjectID = tcProjectID;
	}

	public String getCrisisName() {
		return crisisName;
	}

	public void setCrisisName(String crisisName) {
		this.crisisName = crisisName;
	}

	public String getClassifierName() {
		return classifierName;
	}

	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}

	public Long getCrisisSrID() {
		return crisisSrID;
	}

	public void setCrisisSrID(Long crisisSrID) {
		this.crisisSrID = crisisSrID;
	}
	
}
