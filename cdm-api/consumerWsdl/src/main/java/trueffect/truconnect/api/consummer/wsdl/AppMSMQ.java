package trueffect.truconnect.api.consummer.wsdl;

/**
 * Hello world!
 *
 */
public class AppMSMQ 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        
        try { 
            com.trueffect.tags.MSMQService service = new com.trueffect.tags.MSMQService();
            com.trueffect.tags.IMSMQService port = service.getBasicHttpBindingIMSMQService();
            Integer campaignId = 8548417;
            Integer agencyId = 1061024;
            String sessionKey = "79ca8c84-68fb-49b7-945f-8d711056357d";
            Boolean result = port.insertMessageToQueue(campaignId, agencyId, sessionKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        
    }
}
