package trueffect.truconnect.api.commons.exceptions.business;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 11/30/2015.
 */
@XmlRootElement(name = "ImportExportErrors")
public class ImportExportErrors extends Errors {

    private Integer total;
    private Integer validCount;
    private Integer invalidCount;

    public ImportExportErrors() {
        super();
        this.total = 0;
        this.validCount = 0;
        this.invalidCount = 0;
    }

    public ImportExportErrors(List<Error> errors) {
        super(errors);
        this.total = 0;
        this.validCount = 0;
        this.invalidCount = 0;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getValidCount() {
        return validCount;
    }

    public void setValidCount(Integer validCount) {
        this.validCount = validCount;
    }

    public Integer getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(Integer invalidCount) {
        this.invalidCount = invalidCount;
    }
}
