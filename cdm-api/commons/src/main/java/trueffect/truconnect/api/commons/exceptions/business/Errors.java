package trueffect.truconnect.api.commons.exceptions.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Error container. It allows wrapping a list of {@link trueffect.truconnect.api.commons.exceptions.business.Error} elements
 * @author Marcelo Heredia
 */
@XmlRootElement
@XmlSeeAlso({ImportExportErrors.class})
public class Errors {

    protected List<Error> errors;

    public Errors() {
        errors = new ArrayList<>();
    }

    public Errors(List<Error> errors) {
        this.errors = errors;
    }

    public void addError(Error error){
        if(errors == null){
            errors = new ArrayList<>();
        }
        errors.add(error);
    }
    @XmlElement(name = "error")
    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public void sort() {
        Collections.sort(errors);
    }

    public boolean isEmpty(){
        return errors.isEmpty();
    }

    public void addAll(Errors errors){
        if(errors == null) {
            throw new IllegalArgumentException("Errors cannot be null");
        }
        this.errors.addAll(errors.getErrors());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
