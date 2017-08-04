package trueffect.truconnect.api.commons.model.delivery;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "TagEmailResponse")
public class TagEmailResponse {
    
    private List<TagEmailSiteResponse> tagEmailSiteResponses;

    public TagEmailResponse(){
        
    }
    
    public List<TagEmailSiteResponse> getTagEmailSiteResponses() {
        return tagEmailSiteResponses;
    }

    public void setTagEmailSiteResponses(List<TagEmailSiteResponse> tagEmailSiteResponses) {
        this.tagEmailSiteResponses = tagEmailSiteResponses;
    }
    
    
}
