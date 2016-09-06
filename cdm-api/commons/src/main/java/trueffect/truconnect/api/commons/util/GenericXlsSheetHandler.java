package trueffect.truconnect.api.commons.util;

import trueffect.truconnect.api.commons.model.importexport.Column;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class in charge of processing an XLS file using the Event API.
 *
 * The contents of the XLSs file will be accumulated into an internal
 * List. It also validates if the header row and number of columns is valid.
 * It supports reading formulas as well.
 * Created by richard.jaldin on 1/15/2016.
 * @author Marcelo Heredia, Richard Jaldin
 */
public class GenericXlsSheetHandler<E extends XLSTemplateDescriptor> extends DefaultHandler implements AccumulativeHandler<E> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<E> list;
    protected Class<E> clazz;
    protected E current;
    protected int currentRow;
    protected String currentCell;

    protected SharedStringsTable sharedStringsTable;
    protected String cellContent;
    protected boolean nextIsString;
    protected boolean hasCellError;
    
    protected List<String> fieldsWithError;

    public GenericXlsSheetHandler(SharedStringsTable sharedStringsTable, Class<E> clazz) {
        this.sharedStringsTable = sharedStringsTable;
        this.clazz = clazz;
        this.list = new ArrayList<>();
        
        currentRow = 0;
        currentCell = "";
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if(name.equals("row")) {// row => row
            try {
                current = clazz.newInstance();
                currentCell = "";
                currentRow = Integer.valueOf(attributes.getValue("r"));
                current.setRowError(String.valueOf(currentRow));
                fieldsWithError = new ArrayList<>();
            } catch (Exception e) {
                String message = "Illegal State. Cannot start reading row";
                IllegalStateException ise = new IllegalStateException(message, e);
                logger.warn(message, ise);
                throw ise;
            }
        } else if(name.equals("c")) { // c => cell
            // Figure out if the value is an index in the SST
            String cellType = attributes.getValue("t");
            currentCell = attributes.getValue("r");
            
            nextIsString = false;
            hasCellError = false;
            if(cellType != null) {
                nextIsString = "s".equals(cellType);
                hasCellError = "e".equals(cellType);
            }
        }
        // Clear contents cache
        cellContent = "";
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        if(nextIsString) {
            int idx = Integer.parseInt(cellContent);
            cellContent = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx)).toString();
            nextIsString = false;
        }

        if(name.equals("v")) { // v => contents of a cell
            String propertyName = fromCellReference(currentCell);
            if(currentRow > current.getHeaderRow()) {
                // If it is not a header row, we process the row as data
                try {
                    if(propertyName != null) { // Otherwise is an extra cell that does not need to be populate into the result
                        if (hasCellError){
                            this.fieldsWithError.add(StringUtils.uncapitalize(propertyName));
                        }
                        
                        Method method = current.getClass().getMethod("set" + propertyName, String.class);
                        method.invoke(current, cellContent);
                    }
                } catch (Exception e) {
                    String message = "Illegal State. Cannot read cell";
                    IllegalStateException ise = new IllegalStateException(message, e);
                    logger.warn(message, ise);
                    throw ise;
                }
            }
        } else if (name.equals("row")) {
            if (currentRow > current.getHeaderRow()) {
                current.setFieldsWithFormulaError(this.fieldsWithError);
                list.add(current);
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        cellContent += new String(ch, start, length);
    }

    protected String fromCellReference(String s) {
        Column cell = current.getTemplateMapping().get(s.replaceAll("[0-9]", ""));
        return cell != null ? cell.getProperty() : null;
    }

    public List<E> getList() {
        return this.list;
    }
}
