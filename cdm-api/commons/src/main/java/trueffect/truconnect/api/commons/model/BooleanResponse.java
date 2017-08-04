package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Boolean Response Representation
 * Created by marcelo.heredia on 7/3/2015.
 * @author Marcelo Heredia
 */
@XmlRootElement(name="BooleanResponse")
public class BooleanResponse extends ServiceResponse{

    private Boolean result;

    public BooleanResponse() {
    }

    public BooleanResponse(Boolean result) {
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
