package trueffect.truconnect.api.oauth.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Success Response for OAuth module
 */
@XmlRootElement(name = "SuccessResponse")
public class SuccessResponse {

    private String message;

    public SuccessResponse() {
    }

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
