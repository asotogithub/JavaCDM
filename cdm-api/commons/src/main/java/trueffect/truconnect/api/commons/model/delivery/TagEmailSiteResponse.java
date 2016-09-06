package trueffect.truconnect.api.commons.model.delivery;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "TagEmailSiteResponse")
public class TagEmailSiteResponse {
    
    private Integer siteId;
    private Boolean success;
    private String message;

    public TagEmailSiteResponse(){
        
    }
    
    public TagEmailSiteResponse(Integer siteId, Boolean success, String message) {
        this.siteId = siteId;
        this.success = success;
        this.message = message;
    }
    
    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
