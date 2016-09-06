package trueffect.truconnect.api.tpasapi.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Value")
public class Value {

    private String text;
    
    public Value() {
    }
    
    public Value(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Value [text=" + text + "]";
    }
}
