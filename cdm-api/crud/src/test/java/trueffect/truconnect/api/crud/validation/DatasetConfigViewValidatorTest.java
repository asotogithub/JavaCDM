package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.model.dto.adm.Cookie;
import trueffect.truconnect.api.commons.model.dto.adm.CookieList;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.adm.FailThroughDefaults;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.EntityFactory;

import com.trueffect.delivery.formats.adm.CookieDefault$;
import com.trueffect.delivery.formats.adm.KeyDefault$;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

/**
 *
 * @author marleny.patsi
 */
public class DatasetConfigViewValidatorTest {

    private DatasetConfigViewValidator validator;
    private DatasetConfigView datasetConfigView;
    private UUID uuid;

    @Before
    public void setUp() {
        validator = new DatasetConfigViewValidator();
        uuid = UUID.randomUUID();
        datasetConfigView = prepareDatasetConfigView(uuid);
    }

    @Test
    public void testValidateOnUpdateNominalCase() {
        //preapare data
        List<DatasetConfigView> datasetConfigViewList = prepareListDatasetConfigView(100);

        // perform test
        for (DatasetConfigView dataset : datasetConfigViewList) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dataset, "datasetConfigView");
            validator.validateOnUpdate(dataset, String.valueOf(uuid), errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void testValidateOnUpdateUuidEmptyValue() {

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, "", errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("datasetId"));
        assertThat(error.getDefaultMessage(), is("Invalid datasetId, it cannot be empty."));
    }

    @Test
    public void testValidateOnUpdateDatasetIdNullValue() {
        //prepare data
        uuid = UUID.randomUUID();

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("datasetId"));
        assertThat(error.getDefaultMessage(), is("The entity dataset ID is not the same UUID in the path"));
    }

    @Test
    public void testValidateOnUpdateUuidMismatchDatasetId() {
        //prepare data
        datasetConfigView.setDatasetId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("datasetId"));
        assertThat(error.getDefaultMessage(), is("Invalid datasetId, it cannot be empty."));
    }

    @Test
    public void testValidateOnUpdateTtlExpirationSeconds() {
        //prepare data --> NULL value
        datasetConfigView.setTtlExpirationSeconds(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("ttlExpirationSeconds"));
        assertThat(error.getDefaultMessage(), is("Invalid ttlExpirationSeconds, it cannot be empty."));

        //prepare data --> NEGATIVE value
        datasetConfigView.setTtlExpirationSeconds(Math.abs(EntityFactory.random.nextLong()) * (-1));

        // perform test
        errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("ttlExpirationSeconds"));
        assertThat(error.getDefaultMessage(), is("ttlExpirationSeconds should be positive number."));
    }

    @Test
    public void testValidateOnUpdateCookieExpirationDays() {
        //prepare data --> NULL value
        datasetConfigView.setCookieExpirationDays(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("cookieExpirationDays"));
        assertThat(error.getDefaultMessage(), is("Invalid cookieExpirationDays, it cannot be empty."));

        //prepare data --> NEGATIVE value
        datasetConfigView.setCookieExpirationDays(Math.abs(EntityFactory.random.nextInt()) * (-1));

        // perform test
        errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("cookieExpirationDays"));
        assertThat(error.getDefaultMessage(), is("cookieExpirationDays should be positive number."));
    }

    @Test
    public void testValidateOnUpdateCookieExpirationDaysOverMaximum() {
        //prepare data --> Over Max value
        datasetConfigView.setCookieExpirationDays(293);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("cookieExpirationDays"));
        assertThat(error.getDefaultMessage(), is("cookieExpirationDays is invalid. It must be a whole number equal to or between 1 and 292"));
    }

    @Test
    public void testValidateOnUpdateAlias() {
        //prepare data --> NULL value
        datasetConfigView.setAlias(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("alias"));
        assertThat(error.getDefaultMessage(), is("Invalid alias, it cannot be empty."));

        //prepare data --> alias size > max length
        datasetConfigView.setAlias(EntityFactory.faker.lorem().fixedString(Constants.DEFAULT_CHARS_LENGTH + EntityFactory.random.nextInt(50) + 1));

        // perform test
        errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("alias"));
        assertThat(error.getDefaultMessage(), is("Invalid alias, it supports characters up to " + Constants.DEFAULT_CHARS_LENGTH + "."));
    }

    @Test
    public void testValidateOnUpdateAgencyIdNullValue() {
        //prepare data --> NULL value
        datasetConfigView.setAgencyId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("agencyId"));
        assertThat(error.getDefaultMessage(), is("Invalid agencyId, it cannot be empty."));
    }

    @Test
    public void testValidateOnUpdateDomain() {
        //prepare data --> NULL value
        datasetConfigView.setDomain(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("domain"));
        assertThat(error.getDefaultMessage(), is("Invalid domain, it cannot be empty."));

        //prepare data --> EMPTY value
        datasetConfigView.setDomain("");

        // perform test
        errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("domain"));
        assertThat(error.getDefaultMessage(), is("Invalid domain, it cannot be empty."));
    }

    @Test
    public void testValidateOnUpdateFileNamePrefix() {
        //prepare data --> NULL value
        datasetConfigView.setFileNamePrefix(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("fileNamePrefix"));
        assertThat(error.getDefaultMessage(), is("Invalid fileNamePrefix, it cannot be empty."));

        //prepare data --> EMPTY value
        datasetConfigView.setFileNamePrefix("");

        // perform test
        errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("fileNamePrefix"));
        assertThat(error.getDefaultMessage(), is("Invalid fileNamePrefix, it cannot be empty."));
    }

    // Tests for cookiesToCapture

    @Test
    public void testValidateCookiesToCaptureAsNull() {
        // Prepare data --> NULL value
        datasetConfigView.setCookiesToCapture(null);

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("cookiesToCapture"));
        assertThat(error.getDefaultMessage(), is("Required field: cookiesToCapture"));
    }

    @Test
         public void testValidateCookiesToCaptureAsEmptyAndEnabled() {
        // Prepare data --> empty list and 'enable' as 'true'
        datasetConfigView.setCookiesToCapture(new CookieList(true, new ArrayList<String>()));

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("cookiesToCapture.cookies"));
        assertThat(error.getDefaultMessage(), is("List of 'cookies' should have at least one element if 'enabled' is set to 'true'."));
    }

    @Test
    public void testValidateCookiesToCaptureWithDuplicateElements() {
        // Prepare data --> duplicate cookie names
        datasetConfigView.setCookiesToCapture(new CookieList());
        datasetConfigView.getCookiesToCapture().setEnabled(true);
        List<String> cookieNames = new ArrayList<>();
        cookieNames.add("cookie1");
        cookieNames.add("cookie2");
        cookieNames.add(EntityFactory.faker.letterify("??????"));
        cookieNames.add("cookie1");
        cookieNames.add(EntityFactory.faker.letterify("?????"));
        cookieNames.add("cookie2");
        datasetConfigView.getCookiesToCapture().setCookies(cookieNames);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("cookiesToCapture.cookies"));
        assertThat(error.getDefaultMessage(), is("Duplicated cookie name found: [cookie1,cookie2]."));

        Errors apiErrors = new Errors();
        apiErrors.getErrors().addAll(ApiValidationUtils.parseToValidationError(errors));
        ValidationError apiError = (ValidationError) apiErrors.getErrors().get(0);
        apiError.getRejectedValue();
        assertThat(apiError.getRejectedValue(), is("cookie1,cookie2"));
    }

    @Test
    public void testValidateCookiesToCaptureAsEmptyAndDisabled() {
        // Prepare data --> empty list and 'enable' as 'false'
        datasetConfigView.setCookiesToCapture(new CookieList(false, new ArrayList<String>()));

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
    }

    @Test
    public void testValidateCookiesToCaptureAsListWithEmptyElementsAndDisabled() {
        // Prepare data --> Four cookies, only two of them are valid
        datasetConfigView.setCookiesToCapture(new CookieList(false, new ArrayList<>(Arrays.asList(
                "",
                "GoodName",
                "    AnotherCookie   ",
                ""))));


        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
        List<String> cookies = datasetConfigView.getCookiesToCapture().getCookies();
        assertThat(cookies, is(notNullValue()));
        assertThat(cookies.size(), is(equalTo(2)));
        Iterator<String> iterator = cookies.iterator();
        assertThat(iterator.next(), is(equalTo("GoodName")));
        assertThat(iterator.next(), is(equalTo("AnotherCookie")));
    }

    // Tests for durableCookies

    @Test
    public void testValidateDurableCookiesAsNull() {
        // Prepare data --> NULL value
        datasetConfigView.setDurableCookies(null);

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("durableCookies"));
        assertThat(error.getDefaultMessage(), is("Required field: durableCookies"));
    }

    @Test
    public void testValidateDurableCookiesAsEmptyAndEnabled() {
        // Prepare data --> empty list and 'enable' as 'true'
        datasetConfigView.setDurableCookies(new CookieList(true, new ArrayList<String>()));

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("durableCookies.cookies"));
        assertThat(error.getDefaultMessage(), is("List of 'cookies' should have at least one element if 'enabled' is set to 'true'."));
    }

    @Test
    public void testValidateDurableCookiesWithDuplicateElements() {
        // Prepare data --> duplicate cookie names
        datasetConfigView.setDurableCookies(new CookieList());
        datasetConfigView.getCookiesToCapture().setEnabled(true);
        List<String> cookieNames = new ArrayList<>();
        cookieNames.add("cookie1");
        cookieNames.add("cookie2");
        cookieNames.add(EntityFactory.faker.letterify("??????"));
        cookieNames.add("cookie1");
        cookieNames.add(EntityFactory.faker.letterify("?????"));
        cookieNames.add("cookie2");
        datasetConfigView.getDurableCookies().setCookies(cookieNames);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("durableCookies.cookies"));
        assertThat(error.getDefaultMessage(), is("Duplicated cookie name found: [cookie1,cookie2]."));

        Errors apiErrors = new Errors();
        apiErrors.getErrors().addAll(ApiValidationUtils.parseToValidationError(errors));
        ValidationError apiError = (ValidationError) apiErrors.getErrors().get(0);
        apiError.getRejectedValue();
        assertThat(apiError.getRejectedValue(), is("cookie1,cookie2"));
    }

    @Test
    public void testValidateDurableCookiesAsEmptyAndDisabled() {
        // Prepare data --> empty list and 'enable' as 'false'
        datasetConfigView.setDurableCookies(new CookieList(false, new ArrayList<String>()));

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
    }

    @Test
    public void testValidateDurableCookiesAsListWithEmptyElementsAndDisabled() {
        // Prepare data --> Four cookies, only two of them are valid
        datasetConfigView.setDurableCookies(new CookieList(false, new ArrayList<>(Arrays.asList(
                "",
                "GoodName",
                "    AnotherCookie   ",
                ""))));


        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
        List<String> cookies = datasetConfigView.getDurableCookies().getCookies();
        assertThat(cookies, is(notNullValue()));
        assertThat(cookies.size(), is(equalTo(2)));
        Iterator<String> iterator = cookies.iterator();
        assertThat(iterator.next(), is(equalTo("GoodName")));
        assertThat(iterator.next(), is(equalTo("AnotherCookie")));
    }


    // Tests for failThroughtDefaults

    @Test
    public void testValidateFailThroughDefaultsAsNull() {
        // Prepare data --> NULL value
        datasetConfigView.setFailThroughDefaults(null);

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults"));
        assertThat(error.getDefaultMessage(), is("Required field: failThroughDefaults"));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledTypeAsNull() {
        // Prepare data --> NULL value
        datasetConfigView.setFailThroughDefaults(new FailThroughDefaults());
        datasetConfigView.getFailThroughDefaults().setDefaultType(null);
        datasetConfigView.getFailThroughDefaults().setEnabled(true);

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultType"));
        assertThat(error.getDefaultMessage(), is("Required field: failThroughDefaults.defaultType"));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledTypeAsUnsupported() {
        // Prepare data --> NULL value
        datasetConfigView.setFailThroughDefaults(new FailThroughDefaults());
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        datasetConfigView.getFailThroughDefaults().setDefaultType(EntityFactory.faker.letterify("??????"));

        // Perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultType"));
        assertThat(error.getDefaultMessage(), is("Invalid failThroughDefaults.defaultType, it should be one of [CookieDefault, KeyDefault]."));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledAndCookiesAsNull() {
        //prepare data --> DefaultType = CookieDefault and DefaultCookiesList NULL value
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        datasetConfigView.getFailThroughDefaults().setDefaultType(CookieDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultCookieList"));
        assertThat(error.getDefaultMessage(), is("Required field: failThroughDefaults.defaultCookieList"));
    }

    @Test
       public void testValidateFailThroughDefaultsEnabledCookiesWithCookieNameAsNull() {
        //prepare data --> DefaultType = CookieDefault and DefaultCookiesList NULL value
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setDefaultType(CookieDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        ArrayList<Cookie> cookies;
        cookies = new ArrayList<>();
        cookies.add(new Cookie(null, String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultCookieList[0].name"));
        assertThat(error.getDefaultMessage(), is("Invalid cookie.name, it cannot be empty."));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledWithCookieValueAsNull() {
        //prepare data --> DefaultType = CookieDefault and DefaultCookiesList NULL value
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setDefaultType(CookieDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        ArrayList<Cookie> cookies;
        cookies = new ArrayList<>();
        cookies.add(new Cookie(EntityFactory.faker.letterify("?????"), null));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultCookieList[0].value"));
        assertThat(error.getDefaultMessage(), is("Invalid cookie.value, it cannot be empty."));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledWithEmptyCookiesAndDefaultTypeAsCookieDefault() {
        //prepare data --> DefaultType = CookieDefault and DefaultCookiesList has empty items
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        datasetConfigView.getFailThroughDefaults().setDefaultType(CookieDefault$.MODULE$.productPrefix());
        ArrayList<Cookie> cookies;
        cookies = new ArrayList<>();
        cookies.add(new Cookie("", "123"));
        cookies.add(new Cookie("KEY1", "123"));
        cookies.add(new Cookie("KEY2", ""));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultCookieList[0].name"));
        assertThat(error.getDefaultMessage(), is("Invalid cookie.name, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("failThroughDefaults.defaultCookieList[2].value"));
        assertThat(error.getDefaultMessage(), is("Invalid cookie.value, it cannot be empty."));
    }

    @Test
    public void testValidateFailThroughDefaultsDisabledWithEmptyCookiesAndDefaultTypeAsCookieDefault() {
        //prepare data --> DefaultType = CookieDefault and DefaultCookiesList has empty items
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setEnabled(false);
        datasetConfigView.getFailThroughDefaults().setDefaultType(CookieDefault$.MODULE$.productPrefix());
        ArrayList<Cookie> cookies;
        cookies = new ArrayList<>();
        cookies.add(new Cookie("", "123"));
        cookies.add(new Cookie("   KEY1   ", "   123   "));
        cookies.add(new Cookie("KEY2", ""));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
        List<Cookie> cleanCookies = datasetConfigView.getFailThroughDefaults().getDefaultCookieList();
        assertThat(cleanCookies, is(notNullValue()));
        assertThat(cleanCookies.size(), is(equalTo(1)));
        Iterator<Cookie> iterator = cleanCookies.iterator();
        Cookie cookie = iterator.next();
        assertThat(cookie.getName(), is(equalTo("KEY1")));
        assertThat(cookie.getValue(), is(equalTo("123")));
    }

    @Test
    public void testValidateFailThroughDefaultsDisabledWithNonEmptyCookiesAndBlankDefaultKeyAndDefaultTypeAsKeyDefault() {
        // Prepare data
        // - enabled = false
        // - DefaultType = KeyDefault
        // - DefaultKey = ""
        // - DefaultCookiesList = [non-empty]

        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setEnabled(false);
        datasetConfigView.getFailThroughDefaults().setDefaultType(KeyDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setDefaultKey("     ");
        ArrayList<Cookie> cookies;
        cookies = new ArrayList<>();
        cookies.add(new Cookie("", "123"));
        cookies.add(new Cookie("   KEY1   ", "   123   "));
        cookies.add(new Cookie("KEY2", ""));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
        List<Cookie> cleanCookies = datasetConfigView.getFailThroughDefaults().getDefaultCookieList();
        assertThat(cleanCookies, is(notNullValue()));
        assertThat(cleanCookies.size(), is(equalTo(1)));
        Iterator<Cookie> iterator = cleanCookies.iterator();
        Cookie cookie = iterator.next();
        assertThat(cookie.getName(), is(equalTo("KEY1")));
        assertThat(cookie.getValue(), is(equalTo("123")));
        assertThat(datasetConfigView.getFailThroughDefaults().getDefaultKey(), is(nullValue()));
    }

    @Test
    public void testValidateFailThroughDefaultsDisabledWithEmptyCookiesAndBlankDefaultKeyAndDefaultTypeAsKeyDefault() {
        // Prepare data
        // - enabled = false
        // - DefaultType = KeyDefault
        // - DefaultKey = ""
        // - DefaultCookiesList = [empty]

        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setEnabled(false);
        datasetConfigView.getFailThroughDefaults().setDefaultType(KeyDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setDefaultKey("     ");
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));
        List<Cookie> cleanCookies = datasetConfigView.getFailThroughDefaults().getDefaultCookieList();
        assertThat(cleanCookies, is(nullValue()));
        assertThat(datasetConfigView.getFailThroughDefaults().getDefaultKey(), is(nullValue()));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledCookiesWithEmptyCookiesAndDefaultTypeAsKeyDefault() {
        //prepare data --> DefaultType = CookieDefault and DefaultCookiesList has empty items
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        datasetConfigView.getFailThroughDefaults().setDefaultType(KeyDefault$.MODULE$.productPrefix());
        ArrayList<Cookie> cookies;
        cookies = new ArrayList<>();
        cookies.add(new Cookie("", "123"));
        cookies.add(new Cookie("KEY1", "123"));
        cookies.add(new Cookie("KEY2", ""));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(0));

        List<Cookie> cookieList = datasetConfigView.getFailThroughDefaults().getDefaultCookieList();
        assertThat(cookieList, is(notNullValue()));
        assertThat("Cookie list : " + cookieList, cookieList.size(), is(equalTo(1)));
        Iterator<Cookie> iterator = cookieList.iterator();
        Cookie cookie = iterator.next();
        assertThat(cookie.getName(), is(equalTo("KEY1")));
        assertThat(cookie.getValue(), is(equalTo("123")));
    }


    @Test
    public void testValidateFailThroughDefaultsEnabledWithDuplicateCookieNames() {
        //prepare data --> DUPLICATE cookie names
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setDefaultType(CookieDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        ArrayList<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("cookie1", String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie(EntityFactory.faker.letterify("??????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie(EntityFactory.faker.letterify("?????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie("cookie1", String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie("cookie2", String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie(EntityFactory.faker.letterify("?????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie(EntityFactory.faker.letterify("?????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie("cookie2", String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultCookieList"));
        assertThat(error.getDefaultMessage(), is("Duplicated cookie name found: [cookie1,cookie2]."));

        Errors apiErrors = new Errors();
        apiErrors.getErrors().addAll(ApiValidationUtils.parseToValidationError(errors));
        ValidationError apiError = (ValidationError) apiErrors.getErrors().get(0);
        apiError.getRejectedValue();
        assertThat(apiError.getRejectedValue(), is("cookie1,cookie2"));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledCookiesWithCookieValueAsAllCharactersSupported() {
        List<Cookie> cookies = new ArrayList<Cookie>();
        cookies.add(new Cookie(" X ", " ASDFGHJ "));
        cookies.add(new Cookie(" A ", " 4* =[5] "));
        cookies.add(new Cookie(" Z ", " #e we:-!·$%& "));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));

        List<Cookie> validatedCookies = datasetConfigView.getFailThroughDefaults().getDefaultCookieList();

        List<String> names = new ArrayList<>(validatedCookies.size());
        List<String> values = new ArrayList<>(validatedCookies.size());
        for(Cookie ck : validatedCookies) {
            names.add(ck.getName());
            values.add(ck.getValue());
        }
        assertThat(names, contains("X", "A", "Z"));
        assertThat(values, contains("ASDFGHJ", "4* =[5]", "#e we:-!·$%&"));
    }

    @Test
    public void testValidateFailThroughDefaultsEnabledWithDefaultTypeAsKeyDefaultAndNoDefaultKey() {

        //prepare data --> DefaultType = KeyDefault$ and DefaultKey NULL value
        datasetConfigView = prepareDatasetConfigView(uuid);
        datasetConfigView.getFailThroughDefaults().setDefaultType(KeyDefault$.MODULE$.productPrefix());
        datasetConfigView.getFailThroughDefaults().setEnabled(true);
        datasetConfigView.getFailThroughDefaults().setDefaultKey(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("failThroughDefaults.defaultKey"));
        assertThat(error.getDefaultMessage(), is("Invalid failThroughDefaults.defaultKey, it cannot be empty."));
    }

    @Test
    public void testValidationDoesntChangeOrderOfDurableCookies() {

        ArrayList orderedCookieNames = new ArrayList<>(Arrays.asList("A", "B", "Z", "E"));
        datasetConfigView.setDurableCookies(new CookieList(Boolean.TRUE,
                orderedCookieNames));

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));

        List<String> cookies = datasetConfigView.getDurableCookies().getCookies();
        assertThat(cookies, contains("A", "B", "Z", "E"));
    }

    @Test
    public void testValidationDoesntChangeOrderOfCookiesToCapture() {

        ArrayList orderedCookieNames = new ArrayList<>(Arrays.asList("A", "B", "Z", "E"));
        datasetConfigView.setCookiesToCapture(new CookieList(Boolean.TRUE,
                orderedCookieNames));

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));

        List<String> cookies = datasetConfigView.getCookiesToCapture().getCookies();
        assertThat(cookies, contains("A", "B", "Z", "E"));
    }

    @Test
    public void testValidationDoesntChangeOrderOfFailThroughDefaults() {

        List<Cookie> cookies = new ArrayList<Cookie>();
        cookies.add(new Cookie("X", "100"));
        cookies.add(new Cookie("A", "200"));
        cookies.add(new Cookie("Z", "300"));
        datasetConfigView.getFailThroughDefaults().setDefaultCookieList(cookies);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(false));

        List<Cookie> validatedCookies = datasetConfigView.getFailThroughDefaults().getDefaultCookieList();
        List<String> names = new ArrayList<>(validatedCookies.size());
        List<String> values = new ArrayList<>(validatedCookies.size());
        for(Cookie ck : validatedCookies) {
            names.add(ck.getName());
            values.add(ck.getValue());
        }
        assertThat(names, contains("X", "A", "Z"));
        assertThat(values, contains("100", "200", "300"));
    }

    @Test
    public void testValidateOnUpdateContentChannels() {
        //prepare data --> empty value on List
        List<String> contentChannel = new ArrayList<>();
        contentChannel.add("");
        datasetConfigView.setContentChannels(contentChannel);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("contentChannels"));
        assertThat(error.getDefaultMessage(), is("One or more values are not supported. Supported values are [Display, Site, Email]"));

        //prepare data --> UNSUPORTED value on list
        contentChannel = Arrays.asList("Display", "Site", "Email", EntityFactory.faker.letterify("???????"));
        datasetConfigView.setContentChannels(contentChannel);

        // perform test
        errors = new BeanPropertyBindingResult(datasetConfigView, "datasetConfigView");
        validator.validateOnUpdate(datasetConfigView, String.valueOf(uuid), errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("contentChannels"));
        assertThat(error.getDefaultMessage(), is("One or more values are not supported. Supported values are [Display, Site, Email]"));
    }

    private List<DatasetConfigView> prepareListDatasetConfigView(int counter) {
        List<DatasetConfigView> result = new ArrayList<>();
        DatasetConfigView dataset;

        for (int i = 0; i < counter; i++) {
            dataset = prepareDatasetConfigView(uuid);
            result.add(dataset);
        }
        return result;
    }

    private DatasetConfigView prepareDatasetConfigView(UUID uuidValue) {
        DatasetConfigView result = EntityFactory.createDatasetConfigView();
        result.setDatasetId(uuidValue);
        result.setAlias(EntityFactory.faker.letterify("???????"));

        ArrayList<Cookie> cookies;
        FailThroughDefaults failThroughDefaults;
        cookies = new ArrayList<>();
        cookies.add(new Cookie(EntityFactory.faker.letterify("????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie(EntityFactory.faker.letterify("?????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        cookies.add(new Cookie(EntityFactory.faker.letterify("??????"), String.valueOf(Math.abs(EntityFactory.random.nextLong()))));
        failThroughDefaults = new FailThroughDefaults(
                EntityFactory.random.nextBoolean(),
                CookieDefault$.MODULE$.productPrefix(),
                EntityFactory.faker.letterify("???"),
                cookies
        );
        result.setFailThroughDefaults(failThroughDefaults);
        return result;
    }

}
