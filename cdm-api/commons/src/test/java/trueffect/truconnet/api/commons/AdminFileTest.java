package trueffect.truconnet.api.commons;

import trueffect.truconnect.api.commons.AdminFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import java.io.File;


/**
 *
 * @author rodrigo.alarcon
 */
public class AdminFileTest {

    private File corruptedExcelFile;
    private File excelFile;

    @Before
    public void init() throws Exception {
        //set default values
        corruptedExcelFile = new File(getClass().getResource("/template/xlsx/Corrupted Excel File.xlsx").toURI());
        excelFile = new File(getClass().getResource("/template/xlsx/Schedule Edit Template.xlsx").toURI());
    }

    @Test
    public void validateFileXLSXCorruptFile() {
        AdminFile.FileCheckXLSXResult result = AdminFile.validateFileXLSX(corruptedExcelFile);
        assertThat(result, is(AdminFile.FileCheckXLSXResult.INVALID_FILE_TYPE));
    }
    
    @Test
    public void validateFileXLSXNonCorruptFile() {
        AdminFile.FileCheckXLSXResult result = AdminFile.validateFileXLSX(excelFile);
        assertThat(result, is(AdminFile.FileCheckXLSXResult.VALID_FILE_TYPE));
    }
}
