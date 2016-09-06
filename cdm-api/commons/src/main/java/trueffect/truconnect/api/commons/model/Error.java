package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Error")
public class Error {

    private String message;
    private String type;

    public Error() {
    }

    public Error(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Error{" + "message=" + message + ", type=" + type + '}';
    }
}
