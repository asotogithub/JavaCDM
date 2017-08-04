package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.InsertionOrder;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

/**
 * Insertion Order Validation Tests Created by marcelo.heredia on 4/26/2016.
 *
 * @author Marcelo Heredia
 */
public class InsertionOrderValidatorTest {

    private MediaRawDataView mediaRawDataView;
    private InsertionOrder insertionOrder;
    private InsertionOrderValidator validator;
    private BeanPropertyBindingResult errors;
    private BeanPropertyBindingResult mrdvErrors;

    @Before
    public void setUp() {
        validator = new InsertionOrderValidator();
        insertionOrder = EntityFactory.createInsertionOrder();
        mediaRawDataView = EntityFactory.createMediaRawDataView();
        mediaRawDataView.setOrderName(EntityFactory.faker.letterify("??????"));
        errors = new BeanPropertyBindingResult(insertionOrder,
                insertionOrder.getClass().getSimpleName());
        mrdvErrors = new BeanPropertyBindingResult(mediaRawDataView,
                mediaRawDataView.getClass().getSimpleName());
    }

    @Test
    public void testSuccessfulValidationWithAllCorrectValues() {
        ValidationUtils.invokeValidator(validator, insertionOrder, errors);
        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
        assertThat(errors.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testFailedValidationWithWrongIONumberLessThanZero() {
        int invalidIONumber = -Math.abs(EntityFactory.random.nextInt());
        insertionOrder.setIoNumber(invalidIONumber);

        ValidationUtils.invokeValidator(validator, insertionOrder, errors);
        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getField(), is(equalTo("ioNumber")));
        assertThat((int) fieldError.getRejectedValue(), is(equalTo(invalidIONumber)));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                String.format("Invalid %s, it supports values between %s and %s.", "ioNumber",
                        String.valueOf(Constants.INSERTION_ORDER_NUMBER_MIN_VALUE),
                        String.valueOf(Constants.INSERTION_ORDER_NUMBER_MAX_VALUE)))));
    }

    @Test
    public void testSuccessfulValidationForImport() {
        mediaRawDataView.setOrderNumber(String.valueOf(Math.abs(EntityFactory.random.nextInt())));
        mediaRawDataView.setOrderName(EntityFactory.faker.letterify("??????????????"));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(false));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void testFailedValidationForImportWithNoValues() {
        mediaRawDataView.setOrderNumber(null);
        mediaRawDataView.setOrderName(null);

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(2)));
        FieldError fieldError = mrdvErrors.getFieldError("orderName");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Order Name field is required")));

        fieldError = mrdvErrors.getFieldError("orderNumber");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo("Order Number field is required")));

    }

    @Test
    public void testFailedValidationForImportWithIllegalOrderName() {
        mediaRawDataView.setOrderName(EntityFactory.faker.lorem().sentence(257));
        mediaRawDataView.setOrderNumber(String.valueOf(Math.abs(EntityFactory.random.nextInt())));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = mrdvErrors.getFieldError("orderName");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Invalid Order Name. Max character limit is 128")));

    }

    @Test
    public void testFailedValidationForImportWithOrderNumberNaN() {
        mediaRawDataView.setOrderNumber(EntityFactory.faker.letterify("??????"));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = mrdvErrors.getFieldError("orderNumber");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Invalid Order Number. Only numbers allowed. We support values between 0 and 2147483647")));
    }

    @Test
    public void testFailedValidationForImportWithOrderNumberNegative() {
        mediaRawDataView.setOrderNumber(String.valueOf(-Math.abs(EntityFactory.random.nextInt())));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = mrdvErrors.getFieldError("orderNumber");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Invalid Order Number. Values must be greater than 0")));
    }

    @Test
    public void testFailedValidationForImportWithOrderNumberLargerThanMax() {
        mediaRawDataView.setOrderNumber(String.valueOf(Long.MAX_VALUE));

        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError fieldError = mrdvErrors.getFieldError("orderNumber");
        assertThat(fieldError, is(notNullValue()));
        assertThat(fieldError.getDefaultMessage(), is(equalTo(
                "Invalid Order Number. Only numbers allowed. We support values between 0 and 2147483647")));
    }
}
