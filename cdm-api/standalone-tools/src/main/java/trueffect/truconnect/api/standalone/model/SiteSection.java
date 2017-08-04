package trueffect.truconnect.api.standalone.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "SiteSection")
@XmlType(propOrder = {"id", "name", "agencyNotes", "publisherNotes",
        "extProp1", "extProp2", "extProp3", "extProp4", "extProp5",
        "createdDate", "modifiedDate"})
public class SiteSection {

    private Long id;
    private String name;
    private String agencyNotes;
    private String publisherNotes;
    private Date createdDate;
    private Date modifiedDate;
    private String extProp1;
    private String extProp2;
    private String extProp3;
    private String extProp4;
    private String extProp5;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgencyNotes() {
        return agencyNotes;
    }

    public void setAgencyNotes(String agencyNotes) {
        this.agencyNotes = agencyNotes;
    }

    public String getPublisherNotes() {
        return publisherNotes;
    }

    public void setPublisherNotes(String publisherNotes) {
        this.publisherNotes = publisherNotes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getExtProp1() {
        return extProp1;
    }

    public void setExtProp1(String extProp1) {
        this.extProp1 = extProp1;
    }

    public String getExtProp2() {
        return extProp2;
    }

    public void setExtProp2(String extProp2) {
        this.extProp2 = extProp2;
    }

    public String getExtProp3() {
        return extProp3;
    }

    public void setExtProp3(String extProp3) {
        this.extProp3 = extProp3;
    }

    public String getExtProp4() {
        return extProp4;
    }

    public void setExtProp4(String extProp4) {
        this.extProp4 = extProp4;
    }

    public String getExtProp5() {
        return extProp5;
    }

    public void setExtProp5(String extProp5) {
        this.extProp5 = extProp5;
    }

    @Override
    public String toString() {
        return "SiteSection [id=" + id + ", name=" + name + ", agencyNotes="
                + agencyNotes + ", publisherNotes=" + publisherNotes
                + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + ", extProp1=" + extProp1 + ", extProp2="
                + extProp2 + ", extProp3=" + extProp3 + ", extProp4="
                + extProp4 + ", extProp5=" + extProp5 + "]";
    }
}
