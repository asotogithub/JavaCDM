package trueffect.truconnect.api.commons.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "TagEmail")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagEmailWrapper {
    
    @XmlElement(name = "UserEmail")
    @JsonProperty("UserEmail")
    private String userEmail;
    
    @XmlElement(name = "FileType")
    @JsonProperty("FileType")
    private String fileType;
    
    @XmlElementWrapper(name = "PlacementIds")
    @XmlElement(name = "int")
    @JsonProperty("PlacementIds")
    private List<Integer> placementIds;
    
    
    @XmlElement(name = "ToEmails")
    @JsonProperty("ToEmails")
    private TagEmailRecipientsWrapper toEmails;
    
    @XmlElement(name = "IsSuccess")
    @JsonProperty("IsSuccess")
    private boolean isSuccess;
    
    @XmlElement(name = "Message")
    @JsonProperty("Message")
    private String message;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<Integer> getPlacementIds() {
        return placementIds;
    }

    public void setPlacementIds(List<Integer> placementIds) {
        this.placementIds = placementIds;
    }

    public TagEmailRecipientsWrapper getToEmails() {
        return toEmails;
    }

    public void setToEmails(TagEmailRecipientsWrapper toEmails) {
        this.toEmails = toEmails;
    }
    
    
    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
