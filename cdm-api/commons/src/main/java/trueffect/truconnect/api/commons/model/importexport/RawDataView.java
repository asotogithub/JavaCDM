package trueffect.truconnect.api.commons.model.importexport;

import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;
import trueffect.truconnect.api.commons.model.importexport.enums.ImportIssueType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Superclass for all Raw Data that maps a impor/export-able XLS file
 * Created by marcelo.heredia on 7/6/2016.
 * @author Marcelo Heredia
 */
public class RawDataView implements Serializable{
    protected Map<String, List<ImportExportCellError>> issues;
    protected List<String> fieldsWithFormulaError;

    public RawDataView() {
        issues = new HashMap<>();
        fieldsWithFormulaError = new ArrayList<>();
    }

    public Map<String, List<ImportExportCellError>> getIssues() {
        return issues;
    }

    public void setIssues(Map<String, List<ImportExportCellError>> issues) {
        this.issues = issues;
    }

    private List<String> getIssuesOf(ImportIssueType issueType) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<ImportExportCellError>> entry : issues.entrySet()) {
            if(entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (ImportExportCellError error : entry.getValue()) {
                    if(error.getType().equals(issueType) && error.getInAppType() == null) {
                        result.add(error.getMessage());
                    }
                }
            }
        }
        return result;
    }

    public List<String> getErrors() {
        return getIssuesOf(ImportIssueType.ERROR);
    }

    public List<String> getWarnings() {
        return getIssuesOf(ImportIssueType.WARNING);
    }

    public List<ImportExportCellError> getActions() {
        List<ImportExportCellError> result = new ArrayList<>();

        for (Map.Entry<String, List<ImportExportCellError>> entry : issues.entrySet()) {
            if(entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (ImportExportCellError error : entry.getValue()) {
                    if(error.getInAppType() != null) {
                        result.add(error);
                    }
                }
            }
        }

        return result;
    }

    public List<String> getFieldsWithFormulaError() {
        return fieldsWithFormulaError;
    }

    public void setFieldsWithFormulaError(List<String> fieldsWithFormulaError) {
        this.fieldsWithFormulaError = fieldsWithFormulaError;
    }
}
