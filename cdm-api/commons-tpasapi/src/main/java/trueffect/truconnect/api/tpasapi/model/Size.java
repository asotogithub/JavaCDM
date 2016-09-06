package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Size")
@XmlType(propOrder = {"id", "agencyId", "width", "height", "label",
    "createdDate", "modifiedDate"})
public class Size implements Serializable {

    private Long id;
    private Long agencyId;
    private Long width;
    private Long height;
    private String label;
    private Date createdDate;
    private Date modifiedDate;

    public Size() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    @Override
    public String toString() {
        return "Size{" + "id=" + id + ", agencyId=" + agencyId + ", width="
                + width + ", height=" + height + ", label=" + label
                + ", createdDate=" + createdDate
                + ", modifiedDate=" + modifiedDate + '}';
    }
}
