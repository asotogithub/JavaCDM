package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementAttrSettingsMethodology;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

/**
 * Created by richard.jaldin on 8/29/2016.
 */
public class SiteMeasurementValidatorTest {
    private SiteMeasurementValidator validator;
    private SiteMeasurementDTO smDTO;
    private BeanPropertyBindingResult errors;

    @Before
    public void setUp() throws Exception {
        smDTO = EntityFactory.createSiteMeasurement();
        validator = new SiteMeasurementValidator();
        errors = new BeanPropertyBindingResult(
                smDTO, smDTO.getClass().getSimpleName().toLowerCase());
    }

    @Test
    public void testSmPayloadOnCreate() throws Exception {
        ValidationUtils.invokeValidator(validator, smDTO, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void testSmPayloadWithApplyDefaultsOnCreate() throws Exception {
        smDTO.setAssocMethod(null);
        smDTO.setClWindow(null);
        smDTO.setVtWindow(null);
        smDTO.setState(null);
        smDTO.setTtVer(null);

        ValidationUtils.invokeValidator(validator, smDTO, errors);
        assertThat(errors.hasErrors(), is(false));
        assertThat(smDTO.getAssocMethod(),
                is(equalTo(SiteMeasurementAttrSettingsMethodology.CLICK.name())));
        assertThat(smDTO.getClWindow(),
                is(equalTo(Constants.DEFAULT_SITE_MEASUREMENT_CLICK_WINDOW)));
        assertThat(smDTO.getVtWindow(),
                is(equalTo(Constants.DEFAULT_SITE_MEASUREMENT_VIEW_WINDOW)));
        assertThat(smDTO.getState(), is(equalTo(Constants.DEFAULT_STATE_NEW_SITEMEASUREMENT)));
        assertThat(smDTO.getTtVer(),
                is(equalTo(Constants.DEFAULT_SITE_MEASUREMENT_TRU_TAG_VERSION)));
    }

    @Test
    public void testSmPayloadMissingRequiredOnCreate() throws Exception {
        smDTO.setBrandId(null);
        smDTO.setCookieDomainId(null);
        smDTO.setExpirationDate(null);
        smDTO.setName(null);

        ValidationUtils.invokeValidator(validator, smDTO, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(4)));

        FieldError fieldError = errors.getFieldError("brandId");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid brandId, it cannot be empty.")));

        fieldError = errors.getFieldError("cookieDomainId");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid cookieDomainId, it cannot be empty.")));

        fieldError = errors.getFieldError("expirationDate");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid expirationDate, it cannot be empty.")));

        fieldError = errors.getFieldError("name");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid name, it cannot be empty.")));
    }

    @Test
    public void testSmPayloadExceedingLimitsOnCreate() throws Exception {
        smDTO.setName(EntityFactory.faker.lorem().fixedString(
                Constants.SITE_MEASUREMENT_NAME_MAX_LENGTH + 1));
        smDTO.setNotes(EntityFactory.faker.lorem().fixedString(
                Constants.SITE_MEASUREMENT_NOTES_MAX_LENGTH + 1));
        smDTO.setAssocMethod(EntityFactory.faker.lorem().fixedString(
                Constants.SITE_MEASUREMENT_ASSOCIATION_METHOD_MAX_LENGTH + 1));

        ValidationUtils.invokeValidator(validator, smDTO, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(3)));

        FieldError fieldError = errors.getFieldError("name");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid name, it supports characters up to 128.")));

        fieldError = errors.getFieldError("notes");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid notes, it supports characters up to 200.")));

        fieldError = errors.getFieldError("assocMethod");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid assocMethod, it supports characters up to 5.")));
    }

    @Test
    public void testSmPayloadOnUpdate() throws Exception {
        smDTO.setId(Math.abs(EntityFactory.random.nextLong()));
        validator.validateForUpdate(smDTO.getId(), smDTO, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void testSmPayloadMissingRequiredIdOnUpdate() throws Exception {
        validator.validateForUpdate(smDTO.getId(), smDTO, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = errors.getFieldError("id");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid id, it cannot be empty.")));
    }

    @Test
    public void testSmPayloadMismatchIdOnUpdate() throws Exception {
        smDTO.setId(Math.abs(EntityFactory.random.nextLong()));
        validator.validateForUpdate(Math.abs(EntityFactory.random.nextLong()), smDTO, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = errors.getFieldError("id");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Entity in request body does not match the requested id.")));
    }

    @Test
    public void testSmPayloadMissingAssocMethodOnUpdate() throws Exception {
        smDTO.setId(Math.abs(EntityFactory.random.nextLong()));
        smDTO.setAssocMethod(null);

        validator.validateForUpdate(smDTO.getId(), smDTO, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = errors.getFieldError("assocMethod");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid assocMethod, it cannot be empty.")));
    }

    @Test
    public void testSmPayloadExceedingLimitsOnUpdate() throws Exception {
        smDTO.setId(Math.abs(EntityFactory.random.nextLong()));
        smDTO.setAssocMethod(SiteMeasurementAttrSettingsMethodology.CLICK.name());
        smDTO.setClWindow(Constants.MAX_CLICK_WINDOW_VALUE + 1);
        smDTO.setVtWindow(Constants.MAX_VIEW_WINDOW_VALUE + 1);

        validator.validateForUpdate(smDTO.getId(), smDTO, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(2)));

        FieldError fieldError = errors.getFieldError("clWindow");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Invalid Click Window, it supports values between 1 and 30.")));

        fieldError = errors.getFieldError("vtWindow");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(),
                is(equalTo("Invalid View Window, it supports values between 1 and 30.")));
    }
}
