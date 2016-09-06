package trueffect.truconnect.consumer.utility;

/**
 *
 * @author rodrigo.alarcon
 */
public interface TruQClient {
    
    Boolean pushHtmlTagToTruQ(Integer campaignId, Integer agencyId, String sessionKey);
    
}
