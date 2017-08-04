package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "SuccessResponse")
public class SuccessResponse extends ServiceResponse {

    public SuccessResponse() {
    	super();
    }

    public SuccessResponse(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "SuccessResponse{" + "message=" + message + '}';
    }
}
