package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "TpTag")
public class TpTag {

    @TableFieldMapping(table = "TP_TAG", field = "TP_TAG_ID")
    private Long tpTagId;
    @TableFieldMapping(table = "TP_TAG", field = "TP_VENDOR_ID")
    private Long tpVendorId;
    @TableFieldMapping(table = "TP_TAG", field = "TAG_NAME")
    private String tagName;
    @TableFieldMapping(table = "TP_TAG", field = "TAG_DESCRIPTION")
    private String tagDescription;
    @TableFieldMapping(table = "TP_TAG", field = "MATCH_EXPRESSION")
    private String matchExpression;
    @TableFieldMapping(table = "TP_TAG", field = "HEIGHT_EXPRESSION")
    private String heightExpression;
    @TableFieldMapping(table = "TP_TAG", field = "WIDTH_EXPRESSION")
    private String widthExpression;

    public Long getTpTagId() {
        return tpTagId;
    }

    public void setTpTagId(Long tpTagId) {
        this.tpTagId = tpTagId;
    }

    public Long getTpVendorId() {
        return tpVendorId;
    }

    public void setTpVendorId(Long tpVendorId) {
        this.tpVendorId = tpVendorId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public String getMatchExpression() {
        return matchExpression;
    }

    public void setMatchExpression(String matchExpression) {
        this.matchExpression = matchExpression;
    }

    public String getHeightExpression() {
        return heightExpression;
    }

    public void setHeightExpression(String heightExpression) {
        this.heightExpression = heightExpression;
    }

    public String getWidthExpression() {
        return widthExpression;
    }

    public void setWidthExpression(String widthExpression) {
        this.widthExpression = widthExpression;
    }

    @Override
    public String toString() {
        return "TpTag{" + "tpTagId=" + tpTagId + ", tpVendorId=" + tpVendorId + ", tagName="
                + tagName + ", tagDescription=" + tagDescription + ", matchExpression=" + matchExpression
                + ", heightExpression=" + heightExpression + ", widthExpression=" + widthExpression + '}';
    }
}
