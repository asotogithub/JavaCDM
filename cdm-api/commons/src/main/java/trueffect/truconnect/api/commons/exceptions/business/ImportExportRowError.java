package trueffect.truconnect.api.commons.exceptions.business;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 5/24/2016.
 */
@XmlRootElement
public class ImportExportRowError extends ImportExportCellError {

    private String rownum;

    public ImportExportRowError() {
    }

    public ImportExportRowError(String message,
                                ErrorCode code) {
        super(message, code);
    }

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
    }
}
