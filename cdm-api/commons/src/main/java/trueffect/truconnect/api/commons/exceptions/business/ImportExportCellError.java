package trueffect.truconnect.api.commons.exceptions.business;

import trueffect.truconnect.api.commons.model.importexport.enums.ImportIssueType;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppOption;
import trueffect.truconnect.api.commons.model.importexport.enums.InAppType;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author rodrigo.alarcon
 */
@XmlRootElement
public class ImportExportCellError extends Error {

    private String field;
    private ImportIssueType type;
    
    private InAppType inAppType;
    private List<InAppOption> options;
    private InAppOption defaultOption;

    public ImportExportCellError() {
        super();
    }

    public ImportExportCellError(String message, ErrorCode code) {
        super(message, code);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ImportIssueType getType() {
        return type;
    }

    public void setType(ImportIssueType type) {
        this.type = type;
    }

    public InAppType getInAppType() {
        return inAppType;
    }

    public void setInAppType(InAppType inAppType) {
        this.inAppType = inAppType;
    }

    public List<InAppOption> getOptions() {
        return options;
    }

    public void setOptions(List<InAppOption> options) {
        this.options = options;
    }

    public InAppOption getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(InAppOption defaultOption) {
        this.defaultOption = defaultOption;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
