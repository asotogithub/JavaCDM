
package com.trueffect.tags;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


@WebServiceClient(name = "MSMQService", targetNamespace = "http://tags.trueffect.com/", wsdlLocation = "https://truconnect-dev.trueffect.com/API/TagManagementTest/MSMQService.svc?wsdl")
public class MSMQService
    extends Service
{

    private final static URL MSMQSERVICE_WSDL_LOCATION;
    private final static WebServiceException MSMQSERVICE_EXCEPTION;
    private final static QName MSMQSERVICE_QNAME = new QName("http://tags.trueffect.com/", "MSMQService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("https://truconnect-dev.trueffect.com/API/TagManagementTest/MSMQService.svc?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        MSMQSERVICE_WSDL_LOCATION = url;
        MSMQSERVICE_EXCEPTION = e;
    }

    public MSMQService() {
        super(__getWsdlLocation(), MSMQSERVICE_QNAME);
    }


    public MSMQService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }


    @WebEndpoint(name = "BasicHttpBinding_IMSMQService")
    public IMSMQService getBasicHttpBindingIMSMQService() {
        return super.getPort(new QName("http://tags.trueffect.com/", "BasicHttpBinding_IMSMQService"), IMSMQService.class);
    }

    @WebEndpoint(name = "BasicHttpBinding_IMSMQService")
    public IMSMQService getBasicHttpBindingIMSMQService(WebServiceFeature... features) {
        return super.getPort(new QName("http://tags.trueffect.com/", "BasicHttpBinding_IMSMQService"), IMSMQService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (MSMQSERVICE_EXCEPTION!= null) {
            throw MSMQSERVICE_EXCEPTION;
        }
        return MSMQSERVICE_WSDL_LOCATION;
    }

}
