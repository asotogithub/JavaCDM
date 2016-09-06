package trueffect.truconnect.api.commons.model;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Errors")
@XmlSeeAlso({Error.class})
public class Errors {

    private List<Error> errors;

    public Errors() {
    }

    public Errors(Error ... errors) {
        this.errors = Arrays.asList(errors);
    }

    public Errors(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "Errors{" + "errors=" + errors + '}';
    }
}
