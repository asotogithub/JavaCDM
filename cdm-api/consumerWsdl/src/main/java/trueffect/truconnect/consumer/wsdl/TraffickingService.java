
package trueffect.truconnect.consumer.wsdl;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint;


@WebService(name = "TraffickingService", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    com.microsoft.schemas._2003._10.serialization.ObjectFactory.class,
    com.microsoft.schemas._2003._10.serialization.arrays.ObjectFactory.class,
    trueffect.truconnect.consumer.wsdl.ObjectFactory.class
})
public interface TraffickingService {


    @WebMethod(operationName = "TrafficCampaign", action = "http://tempuri.org/TraffickingService/TrafficCampaign")
    @Oneway
    @RequestWrapper(localName = "TrafficCampaign", targetNamespace = "http://tempuri.org/", className = "org.tempuri.TrafficCampaign")
    public void trafficCampaign(
        @WebParam(name = "EntityId", targetNamespace = "http://tempuri.org/")
        Integer entityId,
        @WebParam(name = "PriorStatusId", targetNamespace = "http://tempuri.org/")
        Integer priorStatusId,
        @WebParam(name = "SelectedAgencyContacts", targetNamespace = "http://tempuri.org/")
        ArrayOfint selectedAgencyContacts,
        @WebParam(name = "SelectedSiteContacts", targetNamespace = "http://tempuri.org/")
        ArrayOfint selectedSiteContacts,
        @WebParam(name = "CampaignOwner", targetNamespace = "http://tempuri.org/")
        Integer campaignOwner,
        @WebParam(name = "TraffickingUserId", targetNamespace = "http://tempuri.org/")
        Integer traffickingUserId);


    @WebMethod(operationName = "TrafficSiteMeasurement", action = "http://tempuri.org/TraffickingService/TrafficSiteMeasurement")
    @Oneway
    @RequestWrapper(localName = "TrafficSiteMeasurement", targetNamespace = "http://tempuri.org/", className = "org.tempuri.TrafficSiteMeasurement")
    public void trafficSiteMeasurement(
        @WebParam(name = "EntityId", targetNamespace = "http://tempuri.org/")
        Integer entityId,
        @WebParam(name = "PriorStatusId", targetNamespace = "http://tempuri.org/")
        Integer priorStatusId,
        @WebParam(name = "SiteMeasurementOwner", targetNamespace = "http://tempuri.org/")
        Integer siteMeasurementOwner,
        @WebParam(name = "TraffickingUserId", targetNamespace = "http://tempuri.org/")
        Integer traffickingUserId);

}
