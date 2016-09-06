package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.service.CreativeManager;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;


/**
 * Unit Tests for {@code CreativeInsertionRawDataViewValidator}
 * Created by marcelo.heredia on 11/25/2015.
 */
public class CreativeInsertionRawDataViewValidatorTest {

    private CreativeInsertionRawDataViewValidator validator;
    private CreativeInsertionRawDataView creativeInsertion;
    private static final String[] VALID_DATES = new String[]{
            "11/27/2015 10:48:59 PM MST",
            "11/27/2015 10:48:59 PM",
            "11/27/2015 10:48:59",
            "11/27/2015",
            "",
            null
    };

    private static final String[] INVALID_DATES = new String[]{
            "11/27/2015 10:48:59 PM MSTX",
            "11/27/ABCD 10:48:59 PM MST",
            "11/27/2015 10:48:59 XY",
            "11/27/2015 10:48:TU XY",
            "11/27/2015 10:AB:59",
            "11/27/2015 ABCDEFGH",
            "25/27/2015",
            "MM/dd/yyyy",
            "---",
            "ABC",
            "2006."
    };



    private static final String MIX_VALID_INVALID_CLICKTHROUGHS =
            "https://www.valid.com,ftp://invalid.net,  invalid,,      ,      http://valid.net   ";

    @Before
    public void setUp() throws Exception {
        validator = new CreativeInsertionRawDataViewValidator();
        creativeInsertion = EntityFactory.createCreativeInsertionImportView();
    }
    @Test
    public void testValidateNominalCase() throws Exception {
        List<CreativeInsertionRawDataView> creativeInsertions = EntityFactory.createCreativeInsertionImportViewList(10);
        for (CreativeInsertionRawDataView cInsertImport : creativeInsertions) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(cInsertImport, "creativeInsertionImportView");
            ValidationUtils.invokeValidator(validator, cInsertImport, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }
    @Test
    public void testValidateWrongCreativeInsertionId() throws Exception {
        // Change to letters
        creativeInsertion.setCreativeInsertionId(EntityFactory.faker.letterify("??"));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeInsertionId"));
        assertThat(error.getDefaultMessage(),
                is("Creative Insertion ID is invalid. It can only contain numbers from 0 - 9"));
        // Change to negative number
        creativeInsertion.setCreativeInsertionId("-" + (1 + Math.abs(EntityFactory.random.nextInt(999))));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeInsertionId"));
        assertThat(error.getDefaultMessage(),
                is("Creative Insertion ID is invalid. It can only contain numbers from 0 - 9"));
    }

    @Test
    public void testValidateWrongCreativeInsertionWeight() throws Exception {
        // Set weight as letters
        creativeInsertion.setCreativeWeight(EntityFactory.faker.letterify("??"));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000"));
        // Change to any value greater than max allowed (10000)
        creativeInsertion.setCreativeWeight("" + (Constants.CREATIVE_INSERTION_MAX_WEIGHT +
                Math.abs(EntityFactory.random.nextInt(1000))));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000"));
        // Change to negative number
        creativeInsertion.setCreativeWeight("-" + Math.abs(EntityFactory.random.nextInt(1000)));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000"));
        // Change to negative decimal number
        creativeInsertion.setCreativeWeight("-" + Math.abs(EntityFactory.random.nextDouble()*100));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000"));
        // Change to positive decimal number
        creativeInsertion.setCreativeWeight(""+Math.abs(EntityFactory.random.nextDouble()*100));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000"));
    }

    @Test
    public void testValidateWrongCreativeGroupWeight() throws Exception {
        // Set weight as letters
        creativeInsertion.setGroupWeight(EntityFactory.faker.letterify("??"));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100"));
        // Change to any value greater than max allowed (100)
        creativeInsertion.setGroupWeight("" + (Constants.CREATIVE_GROUP_MAX_WEIGHT +
                Math.abs(EntityFactory.random.nextInt(1000)) + 1));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100"));
        // Change to negative number
        creativeInsertion.setGroupWeight("-" + (Math.abs(EntityFactory.random.nextInt(1000)) + 1));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100"));
        // Change to negative decimal number
        creativeInsertion.setGroupWeight("-" + Math.abs(EntityFactory.random.nextDouble() * 100));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100"));
        // Change to positive decimal number
        creativeInsertion.setGroupWeight("" + Math.abs(EntityFactory.random.nextDouble() * 100));
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100"));
    }

    @Test
    public void testValidStartDates() throws Exception {
        assertDateFormats(new CreativeInsertionModifier() {
            @Override
            public CreativeInsertionRawDataView modifyProperty(CreativeInsertionRawDataView ci, String value) {
                ci.setCreativeStartDate(value);
                return ci;
            }

            @Override
            public void runAssertions(BeanPropertyBindingResult errors) {
                assertThat("Reason:" + errors.getAllErrors(), errors.hasErrors(), is(false));
            }
        }, VALID_DATES);
    }

    @Test
    public void testValidEndDates() throws Exception {
        assertDateFormats(new CreativeInsertionModifier() {
            @Override
            public CreativeInsertionRawDataView modifyProperty(CreativeInsertionRawDataView ci, String value) {
                ci.setCreativeEndDate(value);
                ci.setCreativeStartDate(null);
                return ci;
            }

            @Override
            public void runAssertions(BeanPropertyBindingResult errors) {
                assertThat("Reason:" + errors.getAllErrors(), errors.hasErrors(), is(false));
            }
        }, VALID_DATES);
    }

    @Test
    public void testInvalidStartDates() throws Exception {
        assertDateFormats(new CreativeInsertionModifier() {
            @Override
            public CreativeInsertionRawDataView modifyProperty(CreativeInsertionRawDataView ci, String value) {
                ci.setCreativeStartDate(value);
                ci.setCreativeEndDate(null);
                return ci;
            }

            @Override
            public void runAssertions(BeanPropertyBindingResult errors) {
                assertThat(String.format("Date should be invalid: %s", errors.getFieldValue("creativeStartDate")), errors.hasErrors(), is(true));
            }
        }, INVALID_DATES);
    }

    @Test
    public void testValidDateRanges() throws Exception {
        // Define valid date range
        creativeInsertion.setCreativeStartDate("11/27/2015 10:48:59 PM");
        creativeInsertion.setCreativeEndDate("12/27/2015 01:48:59 PM");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(false));
        // Define valid date range, both start and end date the same
        creativeInsertion.setCreativeStartDate("01/01/2015");
        creativeInsertion.setCreativeEndDate("01/01/2015");
        errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void testInvalidDateRanges() throws Exception {
        // Define invalid date range
        creativeInsertion.setCreativeStartDate("11/27/2015 10:48:59 PM");
        creativeInsertion.setCreativeEndDate("10/27/2015 01:48:59 PM");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeStartDate"));
        assertThat(error.getDefaultMessage(),
                is("Start Date should be before than End Date"));
    }

    @Test
    public void testValidSingleClickthrough() throws Exception {
        // Define valid clickthrough
        creativeInsertion.setCreativeClickThroughUrl(EntityFactory.createFakeURL());
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void testInvalidSingleClickthroughWithComma() throws Exception {
        // Define valid clickthrough
        creativeInsertion.setCreativeClickThroughUrl(EntityFactory.INVALID_CLICKTHROUGHS[11]);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthrough(creativeInsertion, errors);
        assertThat(creativeInsertion.getCreativeType(), is("jpg"));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeClickThroughUrl"));
        assertThat(error.getDefaultMessage(),
                is(("URL 'http://www.example.com/wpstyle/?p=36,4' is invalid; " +
                        "a valid URL should start with either http:// or https:// " +
                        "and it should be a well formed URL")));
    }

    @Test
    public void testInvalidSingleClickthroughsWithNoComma() throws Exception {
        assertDateFormats(new CreativeInsertionModifier() {
            @Override
            public CreativeInsertionRawDataView modifyProperty(CreativeInsertionRawDataView ci, String value) {
                ci.setCreativeClickThroughUrl(value);
                return ci;
            }

            @Override
            public void runAssertions(BeanPropertyBindingResult errors) {
                assertThat(
                        String.format("Clickthrough URL should be invalid: [%s]",
                                errors.getFieldValue("creativeClickThroughUrl")),
                        errors.hasErrors(), is(true));
                assertThat(errors.getErrorCount(), is(1));
                FieldError error = errors.getFieldErrors().get(0);
                assertThat(error.getField(), is("creativeClickThroughUrl"));
                assertThat(error.getDefaultMessage(),
                        is(String.format("URL '%s' is invalid; " +
                                "a valid URL should start with either http:// or https:// " +
                                "and it should be a well formed URL", error.getRejectedValue())));
            }
        },
        // Using invalid URLs that do not have ',' (comma)
        Arrays.copyOfRange(EntityFactory.INVALID_CLICKTHROUGHS, 0, 9));
    }

    @Test
    public void testValidMultipleClickthrough() throws Exception {
        // Define valid clickthrough
        String url = "http://www.first-url.com";
        for(int  i = 0; i < 10; i++) {
            url += "," + EntityFactory.createFakeURL();
        }
        creativeInsertion.setCreativeClickThroughUrl(url);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(String.format("Clickthrough URL [%s] is invalid", url), errors.hasErrors(), is(false));
    }

    @Test
    public void testValidAllClickthrough() throws Exception {
        for(int  i = 0; i < EntityFactory.VALID_CLICKTHROUGHS.length; i++) {
            String url = EntityFactory.VALID_CLICKTHROUGHS[i];
            creativeInsertion.setCreativeClickThroughUrl(url);
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    creativeInsertion,
                    "creativeInsertionImportView");
            ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
            assertThat(String.format("Clickthrough URL [%s] is invalid", url), errors.hasErrors(), is(false));
        }
    }

    @Test
    public void testValidEmptyClickthrough() throws Exception {
        // Define valid clickthrough
        creativeInsertion.setCreativeClickThroughUrl("");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void testAllInvalidMultipleClickthrough() throws Exception {
        // Define invalid clickthrough
        String urls = StringUtils.join(EntityFactory.INVALID_CLICKTHROUGHS, ',');
        creativeInsertion.setCreativeClickThroughUrl(urls);
        creativeInsertion.setCreativeType("html5");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthrough(creativeInsertion, errors);
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeClickThroughUrl"));
        String expectedError = "URL 'I am not a valid URL' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'www.domain.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'https://' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'http://' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'ftp://valid-ftp.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'my@email.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL '123' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'http://!@#$%^&*()=+{}[]|?~<>-_;:.google.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'http://www.!@#$%^&*()=+{}[]|?~<>-_;:.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'http://!@#$%^&*()=+{}[]|?~<>-_;:.!@#$%^&*()=+{}[]|?~<>-_;:.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'http://www.goo(>gle.com' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL '4' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL '' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'c' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'abc' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'https://us' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'er@test.com:test.com.ar/path' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'https://user@te' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'st.com:test.com.ar/path' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'https://us' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'er@te' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL 'st.com:test.com.ar/path' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL";
        assertThat(error.getDefaultMessage(),
                is(expectedError));
    }

    @Test
    public void testSomeInvalidMultipleClickthrough() throws Exception {
        // Define invalid clickthrough
        creativeInsertion.setCreativeClickThroughUrl(MIX_VALID_INVALID_CLICKTHROUGHS);
        creativeInsertion.setCreativeType("html5");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthrough(creativeInsertion, errors);
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeClickThroughUrl"));
        String expectedError =
                "URL 'ftp://invalid.net' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL '  invalid' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL '' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL, " +
                "URL '      ' is invalid; " +
                "a valid URL should start with either http:// or https:// and it should be a well formed URL";
        assertThat(error.getDefaultMessage(),
                is(expectedError));
    }

    @Test
    public void validateClickthroughIsUnallowedFor3rd() throws Exception {
        // Define invalid clickthrough for 3RD Creative Type
        creativeInsertion.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creativeInsertion.setCreativeClickThroughUrl(EntityFactory.VALID_CLICKTHROUGHS[0]);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        validator.validateClickthrough(creativeInsertion, errors);
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeClickThroughUrl"));
        assertThat(error.getDefaultMessage(),
                is("3rd Creatives cannot have either primary or additional Clickthroughs."));
    }

    @Test
    public void testCreativeInsertionRawDataViewValidationInvalidStartDateFail() throws Exception {
        CreativeInsertionRawDataView cirdv = EntityFactory.createCreativeInsertionImportView();
        cirdv.setCreativeStartDate("10000000");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                cirdv, cirdv.getClass().getSimpleName());
        ValidationUtils.invokeValidator(validator, cirdv, errors);
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeStartDate"));
        assertThat(error.getDefaultMessage(),
                is("creativeStartDate is invalid. It should be a Date between 01/01/0000 and 12/31/9999"));
    }

    @Test
    public void testCreativeInsertionRawDataViewValidationInvalidEndDateFail() throws Exception {
        CreativeInsertionRawDataView cirdv = EntityFactory.createCreativeInsertionImportView();
        cirdv.setCreativeEndDate("10000000");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                cirdv, cirdv.getClass().getSimpleName());
        ValidationUtils.invokeValidator(validator, cirdv, errors);
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("creativeEndDate"));
        assertThat(error.getDefaultMessage(),
                is("creativeEndDate is invalid. It should be a Date between 01/01/0000 and 12/31/9999"));
    }

    private void assertDateFormats(CreativeInsertionModifier creativeInsertionModifier, String[] dates) {
        // Define a number of valid formats
        for(String s : dates) {
            CreativeInsertionRawDataView ci = EntityFactory.createCreativeInsertionImportView();
            ci = creativeInsertionModifier.modifyProperty(ci, s);
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    ci,
                    "creativeInsertionImportView");
            ValidationUtils.invokeValidator(validator, ci, errors);
            validator.validateClickthrough(ci, errors);
            creativeInsertionModifier.runAssertions(errors);
        }
    }

    @Test
    public void testValidateMultipleErrors() throws Exception {
        // Setting up a wrong values
        creativeInsertion.setCreativeInsertionId(EntityFactory.faker.letterify("??"));
        creativeInsertion.setCreativeWeight(EntityFactory.faker.letterify("??"));
        creativeInsertion.setGroupWeight("-" + Math.abs(EntityFactory.random.nextInt(1000)));

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(3));
        // First error
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100"));
        // Second error
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("creativeWeight"));
        assertThat(error.getDefaultMessage(),
                is("Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000"));
        // Third error
        error = errors.getFieldErrors().get(2);
        assertThat(error.getField(), is("creativeInsertionId"));
        assertThat(error.getDefaultMessage(),
                is("Creative Insertion ID is invalid. It can only contain numbers from 0 - 9"));
    }

    @Test
    public void testValidateAsErrorCSVString() throws Exception {
        // Setting up wrong values
        creativeInsertion.setCreativeInsertionId(EntityFactory.faker.letterify("??"));
        creativeInsertion.setCreativeWeight(EntityFactory.faker.letterify("??"));
        creativeInsertion.setGroupWeight("-" + (Math.abs(EntityFactory.random.nextInt(1000)+1)));

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");

        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        String csvErr = ApiValidationUtils.asCSVString(errors);
        String expectedError = "Creative Group Weight is invalid. It must be a whole number equal to or between 0 and 100, " +
                "Creative Weight is invalid. It must be a whole number equal to or between 0 and 10,000, " +
                "Creative Insertion ID is invalid. It can only contain numbers from 0 - 9";
        assertThat(csvErr, is(equalTo(expectedError)));
   }

    @Test
    public void testValidatorCleansUpValidValues() throws Exception {
        // Define a set of valid values but with extra spaces or non-standard valid formats
        String creativeInsertionId = "    1    ";
        String creativeStartDate = "    12/04/2015    ";
        String creativeEndDate = "    12/05/2015 23:00:00 UTC";
        String creativeClickThroughUrl = "    http://a.com, https://b.net         ,http://host.com ";
        String creativeWeight = "    100 ";
        String groupWeight = "    1 ";
        String creativeType = "zip";
        creativeInsertion.setCreativeInsertionId(creativeInsertionId);
        creativeInsertion.setCreativeStartDate(creativeStartDate);
        creativeInsertion.setCreativeEndDate(creativeEndDate);
        creativeInsertion.setCreativeClickThroughUrl(creativeClickThroughUrl);
        creativeInsertion.setCreativeWeight(creativeWeight);
        creativeInsertion.setGroupWeight(groupWeight);
        creativeInsertion.setCreativeType(creativeType);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                creativeInsertion,
                "creativeInsertionImportView");
        ValidationUtils.invokeValidator(validator, creativeInsertion, errors);
        validator.validateClickthrough(creativeInsertion, errors);
        assertThat(String.format("Should not have any errors: %s", errors.getAllErrors()), errors.hasErrors(), is(false));
        assertThat(creativeInsertion.getCreativeInsertionId(), is(equalTo(creativeInsertionId.trim())));
        String localTimezone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        assertThat(creativeInsertion.getCreativeStartDate(), is(equalTo("12/04/2015 00:00:00 " + localTimezone)));
        assertThat(creativeInsertion.getCreativeEndDate(), is(equalTo("12/05/2015 23:59:59 " + localTimezone)));
        assertThat(creativeInsertion.getCreativeClickThroughUrl(), is(equalTo("http://a.com,https://b.net,http://host.com")));
        assertThat(creativeInsertion.getCreativeWeight(), is(equalTo(creativeWeight.trim())));
        assertThat(creativeInsertion.getGroupWeight(), is(equalTo(groupWeight.trim())));
    }

    interface CreativeInsertionModifier {
        CreativeInsertionRawDataView modifyProperty(CreativeInsertionRawDataView ci, String value);
        void runAssertions(BeanPropertyBindingResult errors);
    }
}