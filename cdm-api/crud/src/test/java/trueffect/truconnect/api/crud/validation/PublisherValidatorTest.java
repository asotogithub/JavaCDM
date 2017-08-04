package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

import javax.swing.text.html.parser.Entity;


/**
 * Publisher Validation Tests
 * Created by marcelo.heredia on 4/26/2016.
 * @author Marcelo Heredia
 */
public class PublisherValidatorTest {

    private Publisher publisher;
    private PublisherValidator validator;
    private MediaRawDataView mediaRawDataView;
    private BeanPropertyBindingResult errors;
    private BeanPropertyBindingResult mrdvErrors;


    @Before
    public void setUp() {
        publisher = EntityFactory.createPublisher();
        validator = new PublisherValidator();
        mediaRawDataView = EntityFactory.createMediaRawDataView();
        errors = new BeanPropertyBindingResult(publisher,
                publisher.getClass().getSimpleName());
        mrdvErrors = new BeanPropertyBindingResult(mediaRawDataView,
                mediaRawDataView.getClass().getSimpleName());
    }

    @Test
    public void testValidate() throws Exception {

    }

    @Test
    public void testFailedValidationWithLongName() {
        publisher.setId(null);
        publisher.setAgencyId(EntityFactory.random.nextLong());
        publisher.setName(EntityFactory.faker.lorem().fixedString(260));

        ValidationUtils.invokeValidator(validator, publisher, errors);
        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors + "", errors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid name, it supports characters up to 200.")));

    }

    @Test
    public void testSuccessPublisherValidationForImport() {
        mediaRawDataView.setPublisher(EntityFactory.faker.letterify("??????"));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(false));
    }

    @Test
    public void testFailedPublisherValidationForImportNameExceedingMaxLength() {
        String publisherName = EntityFactory.faker.lorem().fixedString(201);
        mediaRawDataView.setPublisher(publisherName);
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getFieldErrorCount(), is(1));

        FieldError fieldError = mrdvErrors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Publisher Name. Max character limit is 200")));

    }
}