package trueffect.truconnect.api.consummer.wsdl;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");


        try { // Call Web Service Operation
            final trueffect.truconnect.consumer.wsdl.TraffickingService_Service service = new trueffect.truconnect.consumer.wsdl.TraffickingService_Service();
            final trueffect.truconnect.consumer.wsdl.TraffickingService port = service.getBasicHttpBindingTraffickingService();
            // TODO initialize WS operation arguments here
            final java.lang.Integer entityId = Integer.valueOf(0);
            final java.lang.Integer priorStatusId = Integer.valueOf(0);
            final com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint selectedAgencyContacts = new com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint();
            final com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint selectedSiteContacts = new com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfint();
            final java.lang.Integer campaignOwner = Integer.valueOf(0);
            final java.lang.Integer traffickingUserId = Integer.valueOf(0);
            port.trafficCampaign(entityId, priorStatusId, selectedAgencyContacts, selectedSiteContacts, campaignOwner, traffickingUserId);

        } catch (Exception ex) {
        }
    }
}
