package trueffect.truconnect.api.commons.exceptions.business;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.FileUploadCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Base Error container Class
 * Created by Marcelo Heredia on 11/21/2014.
 * @author Marcelo Heredia
 */
@XmlRootElement
@XmlSeeAlso({BusinessCode.class,
             SecurityCode.class,
             ValidationCode.class,
             FileUploadCode.class,
             ValidationError.class,
             BusinessError.class,
             AccessError.class,
             DuplicateCIError.class,
             FileUploadError.class,
             ImportExportCellError.class,
             ImportExportRowError.class,
             EmailError.class})
public class Error implements Comparable, Serializable {

    protected String message;
    protected ErrorCode code;

    public Error() {
    }

    public Error(String message, ErrorCode code) {
        this.message = message;
        this.code = code;
    }
    public Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlElement
    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(Object o) {
        Error e = (Error)o;
        return message.compareTo(e.getMessage()) ;
    }
}
