package qa.qcri.mm.trainer.pybossa.entity;

import java.io.Serializable;

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
@Table(name = "typhoon_ruby_text_geo_clicker")
public class TyphoonRubyTextGeoClicker implements Serializable {
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @Column(name = "task_id")
    private Long taskId;

    @Column (name = "tweet")
    private String tweet;

    @Column (name = "tweet_id")
    private String tweetID;
    
    @Column (name = "author")
    private String author;
    
    @Column (name = "final_tweet_id")
    private String finalTweetID;

    public TyphoonRubyTextGeoClicker(){}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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

	public String getFinalTweetID() {
		return finalTweetID;
	}

	public void setFinalTweetID(String finalTweetID) {
		this.finalTweetID = finalTweetID;
	}

}
