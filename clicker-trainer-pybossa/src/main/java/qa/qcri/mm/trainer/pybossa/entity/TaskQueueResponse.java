package qa.qcri.mm.trainer.pybossa.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/20/13
 * Time: 1:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "task_queue_response")
public class TaskQueueResponse implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @Column(name = "task_queue_id")
    private Long taskQueueID;

    @Column (name = "response",  length = 100000)
    private String response;

    @Column (name = "task_info", nullable = false)
    private String taskInfo;

    @Column (name = "created", nullable = false)
    private Date created;

    @Column (name = "exported", nullable = false)
    private boolean exported;

    public TaskQueueResponse(){}

    public TaskQueueResponse(Long taskQueueID, String response, String taskInfo){
        this.taskQueueID = taskQueueID;
        this.response = response;
        this.taskInfo = taskInfo;
    }

    public String getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getTaskQueueID() {
        return taskQueueID;
    }

    public void setTaskQueueID(Long taskQueueID) {
        this.taskQueueID = taskQueueID;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

	@Override
	public String toString() {
		return "TaskQueueResponse [taskQueueID=" + taskQueueID + ", response="
				+ response + ", taskInfo=" + taskInfo + ", created=" + created
				+ ", exported=" + exported + "]";
	}
}
