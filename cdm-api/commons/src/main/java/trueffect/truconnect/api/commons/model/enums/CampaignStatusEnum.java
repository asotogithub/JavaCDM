package trueffect.truconnect.api.commons.model.enums;

/**
 *
 * @author marleny.patsi
 */
public enum CampaignStatusEnum {
    
    NEW(1L),
    AACT(2L),
    ACTCHG(3L),
    ARCHV(4L),
    TRFKG(5L);
    
    private Long statusCode;

    private CampaignStatusEnum(Long statusCode) {
        this.statusCode = statusCode;
    }
    
    public Long getStatusCode() {
        return statusCode;
    }
    
    public static CampaignStatusEnum typeOf(Long type) {
        if(type == null){
            throw new IllegalArgumentException("Type cannot be null");
        }
        for(CampaignStatusEnum campaignStatus : values()){
            if(campaignStatus.getStatusCode().equals(type)){
                return campaignStatus;
            }
        }
        return null;
    }      
}
