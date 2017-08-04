package trueffect.truconnect.api.commons.model.importexport.enums;

import java.io.Serializable;

public enum ImportIssueType implements Serializable {
    ERROR,
    WARNING;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
