package qa.qcri.mm.trainer.pybossa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/20/13
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "client_app_answer")
public class ClientAppAnswer implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @Column(name = "client_app_id")
    private Long clientAppID;

    @Column (name = "answer", nullable = false)
    private String answer;

    @Column (name = "answer_marker_info", nullable = true)
    private String answerMarkerInfo;
    
    @Column (name = "created", nullable = false)
    private Date created;

    @Column(name="vote_cut_off", nullable = false)
    private Integer voteCutOff;

    @Column (name = "active_answer_key", nullable = true)
    private String activeAnswerKey;


    public ClientAppAnswer(){}

    public ClientAppAnswer(Long clientAppID, String answer){
        this.clientAppID = clientAppID;
        this.answer = answer;

    }

    public String getActiveAnswerKey() {
        return activeAnswerKey;
    }

    public void setActiveAnswerKey(String activeAnswerKey) {
        this.activeAnswerKey = activeAnswerKey;
    }

    public Integer getVoteCutOff() {
        return voteCutOff;
    }

    public void setVoteCutOff(Integer voteCutOff) {
        this.voteCutOff = voteCutOff;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getClientAppID() {
        return clientAppID;
    }

    public void setClientAppID(Long clientAppID) {
        this.clientAppID = clientAppID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

	public String getAnswerMarkerInfo() {
		return answerMarkerInfo;
	}

	public void setAnswerMarkerInfo(String answerMarkerInfo) {
		this.answerMarkerInfo = answerMarkerInfo;
	}
}
