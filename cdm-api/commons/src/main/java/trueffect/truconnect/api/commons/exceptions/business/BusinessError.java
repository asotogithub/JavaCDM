package trueffect.truconnect.api.commons.exceptions.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Error class for Business-related errors.
 * Created by Marcelo Heredia on 11/25/2014.
 */
@XmlRootElement
public class BusinessError extends Error{

    private String field;

    public BusinessError() {
        super();
    }

    public BusinessError(String message, ErrorCode code, String field) {
        super(message, code);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
