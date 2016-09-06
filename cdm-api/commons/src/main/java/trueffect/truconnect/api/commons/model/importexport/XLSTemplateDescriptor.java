package trueffect.truconnect.api.commons.model.importexport;

import java.util.List;
import java.util.Map;

/**
 * MS Excel Template Descriptor.
 * <p>
 * It defines the methods that a POJO should implement if it wants to be used
 * as a data container during a import or export process
 *
 * Created by marcelo.heredia on 12/16/2015.
 * @author Marcelo Heredia
 */
public interface XLSTemplateDescriptor {
    int getHeaderRow();
    Map<String, Column> getTemplateMapping();
    XLSTemplateDescriptor getAlternativeClassType();
    void setRowError(String rowError);
    void setFieldsWithFormulaError(List<String> fieldsWithError);
}
