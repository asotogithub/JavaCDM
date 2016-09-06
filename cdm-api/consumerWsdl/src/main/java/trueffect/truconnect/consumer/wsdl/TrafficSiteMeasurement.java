
package trueffect.truconnect.consumer.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entityId",
    "priorStatusId",
    "siteMeasurementOwner",
    "traffickingUserId"
})
@XmlRootElement(name = "TrafficSiteMeasurement")
public class TrafficSiteMeasurement {

    @XmlElement(name = "EntityId")
    protected Integer entityId;
    @XmlElement(name = "PriorStatusId")
    protected Integer priorStatusId;
    @XmlElement(name = "SiteMeasurementOwner")
    protected Integer siteMeasurementOwner;
    @XmlElement(name = "TraffickingUserId")
    protected Integer traffickingUserId;


    public Integer getEntityId() {
        return entityId;
    }


    public void setEntityId(Integer value) {
        this.entityId = value;
    }


    public Integer getPriorStatusId() {
        return priorStatusId;
    }


    public void setPriorStatusId(Integer value) {
        this.priorStatusId = value;
    }


    public Integer getSiteMeasurementOwner() {
        return siteMeasurementOwner;
    }


    public void setSiteMeasurementOwner(Integer value) {
        this.siteMeasurementOwner = value;
    }


    public Integer getTraffickingUserId() {
        return traffickingUserId;
    }

    public void setTraffickingUserId(Integer value) {
        this.traffickingUserId = value;
    }

}
