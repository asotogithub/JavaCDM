package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

/**
 * Created by richard.jaldin on 4/28/2016.
 */
public class SizeValidatorTest {
    private SizeValidator validator;
    private Size size;
    private MediaRawDataView mediaRawDataView;
    private BeanPropertyBindingResult sizeErrors;
    private BeanPropertyBindingResult mrdvErrors;

    @Before
    public void setUp() throws Exception {
        validator = new SizeValidator();
        size = EntityFactory.createSize();
        mediaRawDataView = EntityFactory.createMediaRawDataView();
        sizeErrors = new BeanPropertyBindingResult(size,
                size.getClass().getSimpleName());
        mrdvErrors = new BeanPropertyBindingResult(mediaRawDataView,
                mediaRawDataView.getClass().getSimpleName());
    }

    @Test
    public void testSuccessSizeValidationForCreationWithAllCorrectFields() {
        size.setId(null);
        ValidationUtils.invokeValidator(validator, size, sizeErrors);
        assertThat(sizeErrors, is(notNullValue()));
        assertThat(sizeErrors + "", sizeErrors.hasErrors(), is(false));
        assertThat(sizeErrors.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testFailedSizeValidationForCreationWithAllIncorrectFields() {
        size.setId(EntityFactory.random.nextLong());
        size.setAgencyId(null);
        size.setWidth(-1L);
        size.setHeight(0L);
        ValidationUtils.invokeValidator(validator, size, sizeErrors);
        assertThat(sizeErrors, is(notNullValue()));
        assertThat(sizeErrors.hasErrors(), is(true));
        assertThat(sizeErrors + "", sizeErrors.getErrorCount(), is(equalTo(3)));

        FieldError fieldError = sizeErrors.getFieldError("agencyId");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid agencyId, it cannot be empty.")));

        fieldError = sizeErrors.getFieldError("width");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Ad Width. Values must be greater than 0")));

        fieldError = sizeErrors.getFieldError("height");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Ad Height. Values must be greater than 0")));
    }

    @Test
    public void testSuccessSizeValidationForImport() {
        mediaRawDataView.setAdWidth(EntityFactory.faker.numerify("####"));
        mediaRawDataView.setAdHeight(EntityFactory.faker.numerify("####"));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(sizeErrors, is(notNullValue()));
        assertThat(sizeErrors.hasErrors(), is(false));
    }

    @Test
    public void testFailedSizeValidationForImportWidthRequired() {
        mediaRawDataView.setAdWidth(null);
        mediaRawDataView.setAdHeight(EntityFactory.faker.numerify("####"));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("adWidth");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Ad Width field is required")));
    }

    @Test
    public void testFailedSizeValidationForImportWidthDecimal() {
        mediaRawDataView.setAdWidth(String.valueOf(Math.abs(EntityFactory.random.nextDouble())));
        mediaRawDataView.setAdHeight("100");
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors + "", mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("adWidth");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Ad Width. Only numbers allowed. We support values between 0 and 9999")));
    }

    @Test
    public void testFailedSizeValidationForImportWidthNegative() {
        mediaRawDataView.setAdWidth(String.valueOf(-(1 + Math.abs(EntityFactory.random.nextInt(9998)))));
        mediaRawDataView.setAdHeight("100");
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors + "", mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("adWidth");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Ad Width. Values must be greater than 0")));
    }


    @Test
    public void testFailedSizeValidationForImportHeightRequired() {
        mediaRawDataView.setAdWidth(String.valueOf((1 + Math.abs(EntityFactory.random.nextInt(9998)))));
        mediaRawDataView.setAdHeight(null);
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("adHeight");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Ad Height field is required")));
    }

    @Test
    public void testFailedSizeValidationForImportHeightDecimal() {
        mediaRawDataView.setAdWidth("100");
        mediaRawDataView.setAdHeight(String.valueOf(Math.abs(EntityFactory.random.nextDouble())));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("adHeight");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Ad Height. Only numbers allowed. We support values between 0 and 9999")));
    }

    @Test
    public void testFailedSizeValidationForImportHeightNegative() {
        mediaRawDataView.setAdWidth("100");
        mediaRawDataView.setAdHeight(String.valueOf(-(1 + Math.abs(EntityFactory.random.nextInt(9998)))));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors + "", mrdvErrors.getErrorCount(), is(equalTo(1)));

        FieldError fieldError = mrdvErrors.getFieldError("adHeight");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Invalid Ad Height. Values must be greater than 0")));
    }

    @Test
    public void testSuccessSiteApplyDefaults() {
        size = new Size();
        size.setWidth(Long.valueOf(EntityFactory.faker.numerify("####")));
        size.setHeight(Long.valueOf(EntityFactory.faker.numerify("####")));
        size.setLabel(
                EntityFactory.faker.numerify("####") + "x" + EntityFactory.faker.numerify("####"));
        size = validator.applyDefaults(size);

        assertThat(size, is(notNullValue()));
        assertThat(size.getId(), is(nullValue()));
        assertThat(size.getAgencyId(), is(nullValue()));
        assertThat(size.getWidth(), is(notNullValue()));
        assertThat(size.getHeight(), is(notNullValue()));
        assertThat(size.getLabel(), is(notNullValue()));
        assertThat(size.getLabel(), is(size.getWidth() + "x" + size.getHeight()));
        assertThat(size.getCreatedTpwsKey(), is(nullValue()));
        assertThat(size.getModifiedTpwsKey(), is(nullValue()));
        assertThat(size.getCreatedDate(), is(nullValue()));
        assertThat(size.getModifiedDate(), is(nullValue()));
    }
}
