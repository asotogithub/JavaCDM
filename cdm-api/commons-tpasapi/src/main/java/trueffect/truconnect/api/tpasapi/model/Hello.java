package trueffect.truconnect.api.tpasapi.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement
public class Hello {

    private String message;

    public Hello() {
        this.message = "Hello!";
    }

    public Hello(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
