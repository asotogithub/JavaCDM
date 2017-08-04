
package com.trueffect.tags;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


@WebService(name = "IMSMQService", targetNamespace = "http://tags.trueffect.com/")
@XmlSeeAlso({
    com.microsoft.schemas._2003._10.serialization.ObjectFactory.class,
    com.trueffect.tags.ObjectFactory.class
})
public interface IMSMQService {


    /**
     * 
     * @param campaignId
     * @param sessionKey
     * @param agencyId
     * @return
     *     returns java.lang.Boolean
     */
    @WebMethod(operationName = "InsertMessageToQueue", action = "http://tags.trueffect.com/IMSMQService/InsertMessageToQueue")
    @WebResult(name = "InsertMessageToQueueResult", targetNamespace = "http://tags.trueffect.com/")
    @RequestWrapper(localName = "InsertMessageToQueue", targetNamespace = "http://tags.trueffect.com/", className = "com.trueffect.tags.InsertMessageToQueue")
    @ResponseWrapper(localName = "InsertMessageToQueueResponse", targetNamespace = "http://tags.trueffect.com/", className = "com.trueffect.tags.InsertMessageToQueueResponse")
    public Boolean insertMessageToQueue(
        @WebParam(name = "CampaignId", targetNamespace = "http://tags.trueffect.com/")
        Integer campaignId,
        @WebParam(name = "AgencyId", targetNamespace = "http://tags.trueffect.com/")
        Integer agencyId,
        @WebParam(name = "SessionKey", targetNamespace = "http://tags.trueffect.com/")
        String sessionKey);

}
