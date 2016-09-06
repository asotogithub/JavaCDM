package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;


/**
 * Site SectionValidation Tests
 * Created by marcelo.heredia on 4/28/2016.
 * @author Marcelo Heredia
 */
public class SiteSectionValidatorTest {

    private SiteSection siteSection;
    private SiteSectionValidator validator;
    private BeanPropertyBindingResult errors;
    private BeanPropertyBindingResult mrdvErrors;
    private MediaRawDataView mediaRawDataView;

    @Before
    public void setUp() {
        siteSection = EntityFactory.createSiteSection();
        validator = new SiteSectionValidator();
        mediaRawDataView = EntityFactory.createMediaRawDataView();
        errors = new BeanPropertyBindingResult(siteSection,
                siteSection.getClass().getSimpleName());
        mrdvErrors = new BeanPropertyBindingResult(mediaRawDataView,
                mediaRawDataView.getClass().getSimpleName());

    }

    @Test
    public void testSuccessfulValidationWithAllCorrectValues() {
        siteSection.setId(null);
        siteSection.setName("testName");
        ValidationUtils.invokeValidator(validator, siteSection, errors);
        assertThat(errors, is(notNullValue()));
        assertThat(errors+"", errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testFailedValidationWithoutName() {

        siteSection.setId(null);
        siteSection.setName(null);

        ValidationUtils.invokeValidator(validator, siteSection, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("name")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid name, it cannot be empty.")));
    }

    @Test
    public void testFailedValidationWithLongName() {

        siteSection.setId(null);
        siteSection.setName(EntityFactory.faker.lorem().fixedString(201));

        ValidationUtils.invokeValidator(validator, siteSection, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("name")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid name, it supports characters up to 200.")));
    }

    @Test
    public void testFailedValidationWithLongAgencyNotes() {

        siteSection.setId(null);
        siteSection.setAgencyNotes(EntityFactory.faker.lorem().fixedString(257));

        ValidationUtils.invokeValidator(validator, siteSection, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("agencyNotes")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid agencyNotes, it supports characters up to 256.")));
    }

    @Test
    public void testFailedValidationWithLongAgencyNotes2() {

        siteSection.setId(null);
        siteSection.setName("testName");
        siteSection.setAgencyNotes(EntityFactory.faker.lorem().fixedString(257));

        ValidationUtils.invokeValidator(validator, siteSection, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("agencyNotes")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid agencyNotes, it supports characters up to 256.")));
    }

    @Test
    public void testFailedValidationWithLongPublisherNotes() {

        siteSection.setId(null);
        siteSection.setName("testName");
        siteSection.setPublisherNotes(EntityFactory.faker.lorem().fixedString(257));

        ValidationUtils.invokeValidator(validator, siteSection, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("publisherNotes")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid publisherNotes, it supports characters up to 256.")));
    }

    @Test
    public void testSuccessfulValidationForImport() {
        mediaRawDataView.setSection(EntityFactory.faker.letterify("??????????????"));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(false));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testFailedSectionValidationAsEmpty() {

        mediaRawDataView.setSection("");

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = mrdvErrors.getFieldError("section");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Site Section field is required")));
    }

    @Test
    public void testFailedSectionValidationForImportWithIllegalLength() {

        mediaRawDataView.setSection(EntityFactory.faker.lorem().sentence(201));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = mrdvErrors.getFieldError("section");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Invalid Site Section. Max character limit is 200")));

    }

    @Test
    public void testFailedValidationInvalidCharactersOnName() {

        siteSection.setId(null);
        siteSection.setName("SiteSection Name( ñ");

        ValidationUtils.invokeValidator(validator, siteSection, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("name")));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid characters on Section name")));
    }
}