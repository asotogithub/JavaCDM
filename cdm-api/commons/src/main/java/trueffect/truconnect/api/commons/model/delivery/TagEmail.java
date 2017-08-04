package trueffect.truconnect.api.commons.model.delivery;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "AdTagEmail")
public class TagEmail {
        
    private String userEmail;
    private List<String> toEmails;
    private List<TagEmailSite> tagEmailSites;
    
    public TagEmail(){
        
    }
    
    public TagEmail(String userEmail, String fileType, List<String> toEmails
            , Integer placementId, Integer siteId){
        this.userEmail = userEmail;
        this.toEmails = toEmails;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<String> getToEmails() {
        return toEmails;
    }

    public void setToEmails(List<String> toEmails) {
        this.toEmails = toEmails;
    }

    public List<TagEmailSite> getTagEmailSites() {
        return tagEmailSites;
    }

    public void setTagEmailSites(List<TagEmailSite> tagEmailSites) {
        this.tagEmailSites = tagEmailSites;
    }
}