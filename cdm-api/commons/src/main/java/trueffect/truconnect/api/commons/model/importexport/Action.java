package trueffect.truconnect.api.commons.model.importexport;

import trueffect.truconnect.api.commons.model.importexport.enums.InAppOption;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppType;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 6/9/2016.
 */
@XmlRootElement(name = "Action")
public class Action {
    private Integer rownum;
    private String field;
    private InAppType inAppType;
    private InAppOption action;

    public Integer getRownum() {
        return rownum;
    }

    public void setRownum(Integer rownum) {
        this.rownum = rownum;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public InAppType getInAppType() {
        return inAppType;
    }

    public void setInAppType(InAppType inAppType) {
        this.inAppType = inAppType;
    }

    public InAppOption getAction() {
        return action;
    }

    public void setAction(
            InAppOption action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
