
package trueffect.truconnect.consumer.wsdl;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;



@WebServiceClient(name = "TraffickingService", targetNamespace = "http://tempuri.org/", wsdlLocation = "")
public class TraffickingService_Service
    extends Service
{

    private final static URL TRAFFICKINGSERVICE_WSDL_LOCATION;
    private final static WebServiceException TRAFFICKINGSERVICE_EXCEPTION;
    private final static QName TRAFFICKINGSERVICE_QNAME = new QName("http://tempuri.org/", "TraffickingService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL(SEIFactory.urlWSDL);
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        TRAFFICKINGSERVICE_WSDL_LOCATION = url;
        TRAFFICKINGSERVICE_EXCEPTION = e;
    }

    public TraffickingService_Service() {
        super(__getWsdlLocation(), TRAFFICKINGSERVICE_QNAME);
    }



    public TraffickingService_Service(URL wsdlLocation) {
        super(wsdlLocation, TRAFFICKINGSERVICE_QNAME);
    }



    public TraffickingService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }


    @WebEndpoint(name = "BasicHttpBinding_TraffickingService")
    public TraffickingService getBasicHttpBindingTraffickingService() {
        return super.getPort(new QName("http://tempuri.org/", "BasicHttpBinding_TraffickingService"), TraffickingService.class);
    }


    @WebEndpoint(name = "BasicHttpBinding_TraffickingService")
    public TraffickingService getBasicHttpBindingTraffickingService(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "BasicHttpBinding_TraffickingService"), TraffickingService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (TRAFFICKINGSERVICE_EXCEPTION!= null) {
            throw TRAFFICKINGSERVICE_EXCEPTION;
        }
        return TRAFFICKINGSERVICE_WSDL_LOCATION;
    }

}
