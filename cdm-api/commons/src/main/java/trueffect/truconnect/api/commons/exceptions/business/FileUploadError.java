package trueffect.truconnect.api.commons.exceptions.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Error class for File Upload related errors.
 * Created by Marcelo Heredia on 07/09/2015.
 */
@XmlRootElement
public class FileUploadError extends Error{

    private String filename;

    public FileUploadError() {
        super();
    }

    public FileUploadError(String message, ErrorCode code, String filename) {
        super(message, code);
        this.filename = filename;
    }

    public FileUploadError(String message, ErrorCode code) {
        super(message, code);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
