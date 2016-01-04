package qa.qcri.mm.trainer.pybossa.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Aman
 *
 */
@Entity
@Table(name = "report_template_typhoon_ruby")
public class ReportTemplateTyphoonRuby implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @Column(name = "id")
    private Long reportTemplateID;
    
    @Column(name = "client_app_id")
    private Long clientAppID;
    
    @Column(name = "task_queue_id")
    private Long taskQueueID;
    
    @Column(name = "task_id")
    private Long taskID;

    @Column (name = "tweet")
    private String tweet;

    @Column (name = "tweet_id")
    private String tweetID;
    
    @Column (name = "author")
    private String author;
    
    @Column (name = "lat")
    private String lat;
    
    @Column (name = "lon")
    private String lon;
    
    @Column (name = "url")
    private String url;
    
    @Column (name = "created")
    private String created;
    
    @Column (name = "answer")
    private String answer;
    
    @Column (name = "status")
    private int status;
    
    @Column (name = "updated")
    private Date updated;

    public ReportTemplateTyphoonRuby(){}

	public Long getReportTemplateID() {
		return reportTemplateID;
	}

	public void setReportTemplateID(Long reportTemplateID) {
		this.reportTemplateID = reportTemplateID;
	}

	public Long getClientAppID() {
		return clientAppID;
	}

	public void setClientAppID(Long clientAppID) {
		this.clientAppID = clientAppID;
	}

	public Long getTaskQueueID() {
		return taskQueueID;
	}

	public void setTaskQueueID(Long taskQueueID) {
		this.taskQueueID = taskQueueID;
	}

	public Long getTaskID() {
		return taskID;
	}

	public void setTaskID(Long taskID) {
		this.taskID = taskID;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getTweetID() {
		return tweetID;
	}

	public void setTweetID(String tweetID) {
		this.tweetID = tweetID;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

}
