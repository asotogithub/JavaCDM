package trueffect.truconnect.api.commons.util;

import trueffect.truconnect.api.commons.exceptions.InvalidTemplateException;
import trueffect.truconnect.api.commons.exceptions.HeaderValidationProcessStoppedException;
import trueffect.truconnect.api.commons.model.importexport.Column;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 */
public class ExportImportUtil {

    private static final Logger log = LoggerFactory.getLogger(ExportImportUtil.class);

    // Import/Export constants
    public static final int DEFAULT_MAX_NUM_ROWS = 1000000;
    public static final int MEDIA_MAX_NUM_ROWS = 1000;
    public static final boolean VALIDATE_HEADERS = true;

    /**
     * Note. This method does not close the {@code SXSSFWorkbook} received. The developer must ensure to properly close it
     * @param wb
     * @param name
     */
    public static void saveFile(SXSSFWorkbook wb, String name) {
        if (wb == null) {
            throw new IllegalArgumentException("Workbook cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(name));
            wb.write(out);
        } catch (IOException e) {
            log.warn("IOException while writing to file", e);
        } finally {
            try {
                if( out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.warn("IOException while closing stream", e);
            }
        }
    }

    /**
     * Converts an XLSX workbook read from File {@code file} into a List that contains elements of type {@code clazz}
     * @param clazz The type of class the resulting list should contain
     * @param file The path to the workbook on disk
     * @return The list of elements imported from the workbook
     * @throws InvalidTemplateException When th XLSX file doesn't have the expected number of columns or the
     * header row is not located in a expected row number
     */
    public static List parseTo(Class<? extends XLSTemplateDescriptor> clazz, File file, boolean haveToValidateHeaders) throws InvalidTemplateException {
        long millis = System.currentTimeMillis();
        log.info("MEASURE: parseTo. Start measure for file {}", file);
        if (file == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "workbook"));
        }
        if (clazz == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Class Type"));
        }
        OPCPackage pkg = null;
        List result = null;
        try {
            //Validate the XLSX file
            pkg = OPCPackage.open(file);
            XSSFReader reader = new XSSFReader(pkg);
            SharedStringsTable sharedStrings = reader.getSharedStringsTable();
            GenericXlsSheetHeaderHandler headerHandler = null;

            if(haveToValidateHeaders) {
                headerHandler = new GenericXlsSheetHeaderHandler(sharedStrings, clazz);
                try {
                    readXlsxFile(headerHandler, reader);
                } catch (HeaderValidationProcessStoppedException e) {
                    // Do nothing, this is valid XLS file
                }
            }

            result = readXlsxFile(new GenericXlsSheetHandler(sharedStrings,
                    headerHandler != null && headerHandler
                            .getAlternativeDescriptor() != null ? headerHandler
                            .getAlternativeDescriptor() : clazz), reader);
        } catch (IllegalArgumentException e) {
            // At this point we determine we can't use the provided XLSX file
            throw new InvalidTemplateException(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error happened while processing XLSX file", e);
        } finally {
            if(pkg != null) {
                try {
                    pkg.close();
                } catch (IOException e) {
                    log.error("Unexpected error happened while closing OPCPackage", e);
                }
            }
        }
        millis = System.currentTimeMillis() - millis;
        log.info("MEASURE: parseTo. Stop measure. Took {} seconds and got {} records" , (millis / 1000), (result == null ? 0 : result.size()));
        return result;
    }

    private static List readXlsxFile(GenericXlsSheetHandler handler,
                                     XSSFReader reader)
            throws IOException, SAXException, InvalidFormatException {
        InputStream inputStream = null;
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(handler);
            // By default process the first Sheet
            inputStream = reader.getSheetsData().next();
            parser.parse(new InputSource(inputStream));
            return handler.getList();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Unexpected error happened while closing inputStream", e);
                }
            }
        }
    }

    /**
     * Converts a List that contains elements of type {@code XLSTemplateDescriptor}
     * into a XLSX workbook contained in the input stream of {@code stream}.
     *
     * @param stream Stream that contains the workbook
     * @param list List containing elements of type {@code XLSTemplateDescriptor}
     * @param type The type of {@code XLSTemplateDescriptor} that should be used
     *             to map the content of each element of the list
     * @return The populated workbook
     * @throws InvalidTemplateException When th XLSX file doesn't have the expected number of columns or the
     * header row is not located in a expected row number
     */
    public static SXSSFWorkbook fromList(InputStream stream,
                                  List<? extends XLSTemplateDescriptor> list,
                                  Class<? extends XLSTemplateDescriptor> type) throws Exception {
        if (stream == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Input Stream"));
        }
        if (list == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "List"));
        }
        if (type == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Type"));
        }
        XLSTemplateDescriptor descriptor = type.newInstance();
        XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(stream);
        SXSSFWorkbook result = new SXSSFWorkbook(wb, SXSSFWorkbook.DEFAULT_WINDOW_SIZE, false, true);
        Sheet sheet = result.getSheetAt(0);

        int rownum = descriptor.getHeaderRow();
        String getterName;
        Method getter;
        Object value;
        Row row;
        Cell cell;
        CellStyle style = wb.createCellStyle();
        style.setDataFormat((short) BuiltinFormats.getBuiltinFormat("General"));
        for (XLSTemplateDescriptor item : list) {
            try {
                row = sheet.createRow(rownum++);
            } catch (Exception e) { //try to create the next row
                row = sheet.createRow(rownum++);
            }
            for (Map.Entry<String, Column> entry : descriptor.getTemplateMapping().entrySet()) {
                row.setRowStyle(style);
                cell = row.createCell(entry.getValue().getIndex());
                getterName = "get" + entry.getValue().getProperty(); //converting "Something" into "getSomething"
                getter = item.getClass().getMethod(getterName);
                value = getter.invoke(item);
                if (value != null && value instanceof Date) {
                    cell.setCellValue(DateConverter.importExportFormat((Date) value));
                } else if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }
        return result;
    }
}
