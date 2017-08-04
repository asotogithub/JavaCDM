package trueffect.truconnect.api.commons.util;

import trueffect.truconnect.api.commons.exceptions.HeaderValidationProcessStoppedException;
import trueffect.truconnect.api.commons.model.importexport.Column;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richard.jaldin on 5/12/2016.
 */
public class GenericXlsSheetHeaderHandler<E extends XLSTemplateDescriptor>
        extends GenericXlsSheetHandler<E> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, String> headers;
    private boolean isFirstColumn;
    private Map<String, Column> tableMapping;
    private XLSTemplateDescriptor alternativeDescriptor;

    public GenericXlsSheetHeaderHandler(
            SharedStringsTable sharedStringsTable, Class<E> clazz) {
        super(sharedStringsTable, clazz);
        headers = new HashMap<>();
        isFirstColumn = true;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
            throws SAXException {
        if (name.equals("row")) {// row => row
            try {
                current = clazz.newInstance();
                tableMapping = tableMapping != null ? tableMapping : current.getTemplateMapping();
                currentCell = "";
                currentRow = Integer.valueOf(attributes.getValue("r"));
            } catch (Exception e) {
                String message = "Illegal State. Cannot start reading row";
                IllegalStateException ise = new IllegalStateException(message, e);
                logger.warn(message, ise);
                throw ise;
            }
        } else if (name.equals("c")) { // c => cell
            // Figure out if the value is an index in the SST
            String cellType = attributes.getValue("t");
            currentCell = attributes.getValue("r");
            nextIsString = cellType != null && "s".equals(cellType);
        }
        // Clear contents cache
        cellContent = "";
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        if (nextIsString) {
            int idx = Integer.parseInt(cellContent);
            cellContent = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx)).toString();
            nextIsString = false;
        }

        if (name.equals("v")) { // v => contents of a cell
            String propertyName = fromCellReference(currentCell);
            if (currentRow == current.getHeaderRow()) {
                // If we are located in the header row, we must make sure
                // the header titles match the setter names. If so, it is a valid header
                // and the counter increases
                cellContent = cellContent.replaceAll("[ |-]", "");
                if(isFirstColumn) {
                    if("rowError".equalsIgnoreCase(cellContent)){
                        alternativeDescriptor = current.getAlternativeClassType();
                        tableMapping = getAlternativeTableMapping();
                    }
                    isFirstColumn = false;
                }

                if (propertyName != null) {
                    headers.put(cellContent.toLowerCase(), currentCell.replaceAll("[0-9]", ""));
                }
            }
        } else if (name.equals("row")) {
            if (currentRow == current.getHeaderRow()) {
                List<String> missing = new ArrayList<>();
                boolean isWrongOrdered = false;
                for (Map.Entry<String, Column> entry : tableMapping.entrySet()) {
                    Column property = entry.getValue();
                    String headerKey = headers.get(property.getProperty().toLowerCase());
                    if (headerKey == null) {
                        missing.add(property.getProperty());
                    } else if (!headerKey.equals(entry.getKey())) {
                        isWrongOrdered = true;
                    }
                }

                if (missing.size() == current.getTemplateMapping().size()) {
                    // Header row does not exist or it is in the wrong place
                    throw new IllegalArgumentException(ResourceBundleUtil.getString(
                            "importExport.error.noHeaderProvided"));
                } else if (!missing.isEmpty()) { // Check if missing some column
                    throw new IllegalArgumentException(ResourceBundleUtil
                            .getString("importExport.error.missingHeaderColumns",
                                    StringUtils.join(missing, ", ")));
                } else if (isWrongOrdered) { // Check if headers are disordered
                    throw new IllegalArgumentException(ResourceBundleUtil.getString(
                            "importExport.error.disorderedHeaders"));
                } else {
                    // Utility exception to stop the check in
                    // case of finding the headers
                    // No need to localize this message as it will never be shown as a
                    // visible response
                    throw new HeaderValidationProcessStoppedException("Valid template structure");
                }
            }
        }
    }

    @Override
    protected String fromCellReference(String s) {
        Column cell = tableMapping.get(s.replaceAll("[0-9]", ""));
        return cell != null ? cell.getProperty() : null;
    }

    private Map<String, Column> getAlternativeTableMapping() {
        Map<String, Column> result = new LinkedHashMap<>();
        result.putAll(alternativeDescriptor.getTemplateMapping());
        List<String> keys = new ArrayList<>();
        keys.addAll(result.keySet());
        for (int i = 0; i < 2; i++) {
            result.remove(keys.get(i));
        }
        return result;
    }

    public Class<? extends XLSTemplateDescriptor> getAlternativeDescriptor(){
        return alternativeDescriptor != null ? alternativeDescriptor.getClass() : null;
    }
}
