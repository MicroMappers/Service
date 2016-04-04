package qa.qcri.mm.api.template;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/31/13
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrisisApplicationListModel {

    private Long nominalAttributeID;
    private String name;
    private String url;
    private Long remaining;
    private Long totaltaskNumber;

    public CrisisApplicationListModel(Long nominalAttributeID, String name , String url, Long remaining, Long totaltaskNumber){
        this.nominalAttributeID = nominalAttributeID;
        this.name = name;
        this.url = url;
        this.remaining = remaining;
        this.totaltaskNumber = totaltaskNumber;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getRemaining() {
        return remaining;
    }

    public void setRemaining(Long remaining) {
        this.remaining = remaining;
    }

    public Long getTotaltaskNumber() {
        return totaltaskNumber;
    }

    public void setTotaltaskNumber(Long totaltaskNumber) {
        this.totaltaskNumber = totaltaskNumber;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNominalAttributeID() {
        return nominalAttributeID;
    }

    public void setNominalAttributeID(Long nominalAttributeID) {
        this.nominalAttributeID = nominalAttributeID;
    }


}
