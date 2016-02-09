package qa.qcri.mm.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
@Table(name = "sliced_image")
public class SlicedImage implements Serializable {
	private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column (name = "sliced_file_url", nullable = true)
    private String slicedFileURL;

    @Column (name = "thumbnail", nullable = true)
    private String thumbnail;

    @Column (name = "lat", nullable = true)
    private String lat;

    @Column (name = "lon", nullable = true)
    private String lon;

    @Column (name = "bounds", nullable = true)
    private String bounds;
    
    @ManyToOne
    @JoinColumn (name = "source_image_id")
    private SourceImage sourceImage;
    
    @Column (name = "created_at")
    private Date createdAt;
    
    public SlicedImage(SlicedImage imageMetaData) {
		super();
		this.setLat(imageMetaData.getLat());
		this.setLon(imageMetaData.getLon());
		this.bounds = imageMetaData.getBounds();
		this.setSourceImage(imageMetaData.getSourceImage());
	}

	public SlicedImage() {
    }

    public SlicedImage(String fileName, String lat, String lon){
        this.slicedFileURL = fileName;
        this.setLat(lat);
        this.setLon(lon);
    }

    public SlicedImage(String fileName, String lat, String lon, String bounds){
    	this.slicedFileURL = fileName;
        this.setLat(lat);
        this.setLon(lon);
        this.bounds = bounds;
    }

    public SlicedImage(String sourcePath, String slicedFileURL, String lat, String lon,
			String bounds, SourceImage sourceImage) {
		this.slicedFileURL = slicedFileURL;
		this.setLat(lat);
		this.setLon(lon);
		this.bounds = bounds;
		this.setSourceImage(sourceImage);
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

	public String getSlicedFileURL() {
		return slicedFileURL;
	}

	public void setSlicedFileURL(String slicedFileURL) {
		this.slicedFileURL = slicedFileURL;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public SourceImage getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(SourceImage sourceImage) {
		this.sourceImage = sourceImage;
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

}
