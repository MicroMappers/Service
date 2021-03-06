package qa.qcri.mm.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 9/29/14
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "namibia_report")
public class NamibiaReport  implements Serializable {

    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long task_id;

    @Lob
    @Column(name = "geo", length = 100000 )
    private String geo;

    @Column(name = "source_image", nullable = false)
    private String sourceImage;

    @Column(name = "sliced_image", nullable = false)
    private String slicedImage;

    @Column(name = "found_count", nullable = false)
    private Integer foundCount;

    @Column(name = "no_found_count", nullable = false)
    private Integer noFoundCount;

    public NamibiaReport(Long task_id, String geo, String sourceImage, String slicedImage, Integer foundCount, Integer noFoundCount) {
        this.task_id = task_id;
        this.geo = geo;
        this.sourceImage = sourceImage;
        this.slicedImage = slicedImage;
        this.foundCount = foundCount;
        this.noFoundCount = noFoundCount;
    }

    public NamibiaReport() {
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTask_id() {
        return task_id;
    }

    public void setTask_id(Long task_id) {
        this.task_id = task_id;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    public String getSlicedImage() {
        return slicedImage;
    }

    public void setSlicedImage(String slicedImage) {
        this.slicedImage = slicedImage;
    }

    public Integer getFoundCount() {
        return foundCount;
    }

    public void setFoundCount(Integer foundCount) {
        this.foundCount = foundCount;
    }

    public Integer getNoFoundCount() {
        return noFoundCount;
    }

    public void setNoFoundCount(Integer noFoundCount) {
        this.noFoundCount = noFoundCount;
    }
}
