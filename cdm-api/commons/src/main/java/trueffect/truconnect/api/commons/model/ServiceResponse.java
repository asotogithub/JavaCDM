package trueffect.truconnect.api.commons.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "ServiceResponse")
public class ServiceResponse implements Serializable {

    protected String message;

    public ServiceResponse() {
    }

    public ServiceResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" + "message=" + message + '}';
    }
}
