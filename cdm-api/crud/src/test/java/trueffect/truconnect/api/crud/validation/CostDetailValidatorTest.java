package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Unit Tests for {@code CostDetailValidator} Created by marcelo.heredia on
 * 10/9/2015.
 */
public class CostDetailValidatorTest {

    private CostDetailValidator validator;
    private BeanPropertyBindingResult validationResult;
    private Placement placement;
    private MediaRawDataView mediaRawDataView;
    private BeanPropertyBindingResult mrdvErrors;

    @Before
    public void setUp() {
        placement = EntityFactory.createPlacement();
        validator = new CostDetailValidator();
        List<CostDetail> costDetails = EntityFactory.createCostDetailList(10);
        placement.setCostDetails(costDetails);
        validationResult = new BeanPropertyBindingResult(placement, "placement");
        mediaRawDataView = EntityFactory.createMediaRawDataView();
        mrdvErrors = new BeanPropertyBindingResult(mediaRawDataView,
                mediaRawDataView.getClass().getSimpleName());
    }

    @Test
    public void testValidateBasics() {
        List<CostDetail> costDetails = placement.getCostDetails();
        int length = costDetails.size();
        validator.validateForUpdate(costDetails, validationResult);
        assertThat(validationResult, is(notNullValue()));
        assertThat(validationResult.hasErrors(), is(false));
        assertThat(costDetails, is(notNullValue()));
        assertThat(costDetails.size(), is(equalTo(length)));
    }

    @Test
    public void testValidateForCreateWithWrongData() {
        CostDetail costDetail = placement.getCostDetails().iterator().next();
        costDetail.setMargin(-1.0D);
        costDetail.setPlannedGrossRate(-1.0D);
        costDetail.setPlannedGrossAdSpend(-1.0D);
        costDetail.setRateType(100L);
        validationResult.pushNestedPath("costDetails[0]");
        validator.validateForCreate(costDetail, validationResult);
        validationResult.popNestedPath();
        assertThat(validationResult.hasErrors(), is(true));
        assertThat(validationResult.getErrorCount(), is(4));

        FieldError error = validationResult.getFieldErrors().get(0);
        assertThat(error.getField(), is("costDetails[0].margin"));
        assertThat(error.getDefaultMessage(), is("Invalid margin, it must be greater than or equals to 0.0."));
        error = validationResult.getFieldErrors().get(1);
        assertThat(error.getField(), is("costDetails[0].plannedGrossAdSpend"));
        assertThat(error.getDefaultMessage(), is("Invalid plannedGrossAdSpend, it must be greater than or equals to 0.0."));
        error = validationResult.getFieldErrors().get(2);
        assertThat(error.getField(), is("costDetails[0].plannedGrossRate"));
        assertThat(error.getDefaultMessage(), is("Invalid plannedGrossRate, it must be greater than or equals to 0.0."));
        error = validationResult.getFieldErrors().get(3);
        assertThat(error.getField(), is("costDetails[0].rateType"));
        assertThat(error.getDefaultMessage(), is("100 is not supported"));
    }

    @Test
    public void testValidateNullCostDetailsCollection() {
        placement.setCostDetails(null);
        List<CostDetail> costDetails = placement.getCostDetails();
        validator.validateForUpdate(costDetails, validationResult);
        assertThat(validationResult.getErrorCount(), is(1));
        FieldError error = validationResult.getFieldErrors().iterator().next();
        assertThat(error.getField(), is("costDetails"));
        assertThat(error.getDefaultMessage(), is("Invalid costDetails, it cannot be empty."));
    }

    @Test
    public void testValidateRoundingOFCostDetailOnValidation() {
        CostDetail costDetail = placement.getCostDetails().iterator().next();
        // Define all plannedGrossAdSpend, plannedGrossRate, and margin places with more than 14 decimal places
        costDetail.setMargin(50.123456789012349000001D);
        costDetail.setPlannedGrossAdSpend(100.987654321023456789012349000002D);
        costDetail.setPlannedGrossRate(20.111111111111114500006D);
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(costDetail, "costDetail");
        ValidationUtils.invokeValidator(validator, costDetail, validationResult);
        assertThat(validationResult.getErrorCount(), is(0));
        assertThat(costDetail.getMargin().doubleValue(), is(equalTo(50.12345678901235D)));
        assertThat(costDetail.getPlannedGrossAdSpend().doubleValue(), is(equalTo(100.98765432102346D)));
        assertThat(costDetail.getPlannedGrossRate().doubleValue(), is(equalTo(20.11111111111111D)));
    }

    @Test
    public void testValidateEmptyCostDetailsCollection() {
        List<CostDetail> costDetails = placement.getCostDetails();
        costDetails.clear();
        validator.validateForUpdate(costDetails, validationResult);
        assertThat(validationResult.getErrorCount(), is(1));
        FieldError error = validationResult.getFieldErrors().iterator().next();
        assertThat(error.getField(), is("costDetails"));
        assertThat(error.getDefaultMessage(), is("Invalid costDetails, collection should not be empty."));
    }

    @Test
    public void testValidateSingleElementCostDetailsCollection() {
        List<CostDetail> costDetails = EntityFactory.createCostDetailList(1);
        placement.setCostDetails(costDetails);
        validator.validateForUpdate(costDetails, validationResult);
        assertThat(validationResult.hasErrors(), is(false));
    }

    @Test
    public void testValidateIDsAndDateRangesAreSortedAndConsecutive() {
        List<CostDetail> costDetails = placement.getCostDetails();
        Collections.shuffle(costDetails);
        validator.validateForUpdate(costDetails, validationResult);
        assertThat(validationResult.hasErrors(), is(false));
        CostDetail previous = costDetails.remove(0);
        assertDateRangesFor(previous);
        for (CostDetail current : costDetails) {
            assertDateRangesFor(current);
            assertThat(previous.getId(), lessThanOrEqualTo(current.getId()));
            assertThat(datesConsecutive(current.getStartDate(), previous.getEndDate()), is(true));
            previous = current;
        }
    }

    @Test
    public void testValidateIDsAndDateRangesAreSortedAndConsecutiveWithNullIDs() {
        List<CostDetail> costDetails = placement.getCostDetails();
        int listSize = costDetails.size();

        for (int j = 0; j <= listSize / 2; j++) {
            costDetails.get(EntityFactory.random.nextInt(listSize)).setId(null);
        }
        CostDetailValidator.renderList(costDetails);
        Collections.shuffle(costDetails);
        CostDetailValidator.renderList(costDetails);
        placement.setCostDetails(costDetails);
        validator.validateForUpdate(costDetails, validationResult);
        boolean groupChanged = false;
        for (CostDetail costDetail : costDetails) {
            if (!groupChanged) {
                groupChanged = costDetail.getId() == null;
            }
            if (groupChanged) {
                assertThat(costDetail.getId(), is(nullValue()));
            } else {
                assertThat(costDetail.getId(), is(notNullValue()));
            }
        }
    }

    @Test
    public void testValidateNonConsecutiveDates() {
        List<CostDetail> costDetails = placement.getCostDetails();
        // Grab 3rd Cost Detail and mess up the date range
        CostDetail costDetail = costDetails.get(7);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(costDetail.getStartDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        costDetail.setStartDate(calendar.getTime());
        validator.validateForUpdate(costDetails, validationResult);
        assertThat(validationResult.hasErrors(), is(true));
        assertThat(validationResult.getErrorCount(), is(1));
        FieldError error = validationResult.getFieldErrors().iterator().next();
        assertThat(error.getField(), is("costDetails[7].startDate"));
        assertThat(error.getDefaultMessage(), containsString("is not a consecutive date of"));
    }

    @Test
    public void testStartDateAfterEndDateFails() {
        List<CostDetail> costDetails = placement.getCostDetails();
        // Grab 3rd Cost Detail and mess up the date range
        CostDetail costDetail = costDetails.get(7);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(costDetail.getStartDate());
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        costDetail.setStartDate(calendar.getTime());
        costDetail.setMargin(50.0D);
        costDetail.setPlannedGrossAdSpend(100.0D);
        costDetail.setPlannedGrossRate(20.0D);
        BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(costDetail, "costDetail");
        validator.validate(costDetail, validationResult);
        assertThat(validationResult.hasErrors(), is(true));
        assertThat(validationResult.getErrorCount(), is(1));
        FieldError error = validationResult.getFieldErrors().iterator().next();
        assertThat(error.getField(), is("endDate"));
        assertThat(error.getDefaultMessage(), is(equalTo(String.format("%1$tF %1$tT is after %2$tF %2$tT", costDetail.getStartDate(), costDetail.getEndDate()))));
    }

    private void assertDateRangesFor(CostDetail costDetail) {
        assertThat(costDetail.getStartDate().before(costDetail.getEndDate()), is(true));
    }

    private boolean datesConsecutive(Date current, Date previous) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DateConverter.endDate(calendar.getTime()).compareTo(previous) == 0;
    }

    @Test
    public void testSuccessCostDetailValidationForImport() {
        mediaRawDataView.setPlacementName(EntityFactory.faker.letterify("??????"));
        mediaRawDataView.setExtPlacementId(EntityFactory.faker.numerify("#######"));
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(false));
    }
    
    @Test
    public void testCostDetailValidationForImportInvalidStartDateFail() {
        MediaRawDataView mrdv = EntityFactory.createMediaRawDataView();
        mrdv.setStartDate("10000000");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(mrdv,
                mrdv.getClass().getSimpleName());
        validator.validateFieldsForImport(mrdv, errors);
        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getFieldError().getField(), is("startDate"));
        assertThat(errors.getFieldError().getCode(), is("Invalid"));
        assertThat(errors.getFieldError().getDefaultMessage(), is("Start Date is invalid. It should be a Date between 01/01/0000 and 12/31/9999"));
        assertThat(errors.getFieldError().getObjectName(), is("MediaRawDataView"));
    }
    
    @Test
    public void testCostDetailValidationForImportInvalidEndDateFail() {
        MediaRawDataView mrdv = EntityFactory.createMediaRawDataView();
        mrdv.setEndDate("10000000");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(mrdv,
                mrdv.getClass().getSimpleName());
        validator.validateFieldsForImport(mrdv, errors);
        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getFieldError().getField(), is("endDate"));
        assertThat(errors.getFieldError().getCode(), is("Invalid"));
        assertThat(errors.getFieldError().getDefaultMessage(), is("End Date is invalid. It should be a Date between 01/01/0000 and 12/31/9999"));
        assertThat(errors.getFieldError().getObjectName(), is("MediaRawDataView"));
    }

    @Test
    @Ignore
    public void testFailedCostDetailValidationForImportPlannedAdSpendRequired() {
        mediaRawDataView.setPlannedAdSpend(null);

        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "plannedAdSpend",
                "Planned Ad Spend field is required"
        );

    }

    @Test
    public void testFailedCostDetailValidationForImportPlannedAdSpendAsLetters() {
        String invalidValue = EntityFactory.faker.letterify("??????");
        mediaRawDataView.setPlannedAdSpend(invalidValue);
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "plannedAdSpend",
                "Invalid Planned Ad Spend. Values must be in correct format (ex: 00.00)"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportPlannedAdSpendAsNegativeNumber() {
        mediaRawDataView.setPlannedAdSpend(
                String.valueOf(-Math.abs(EntityFactory.random.nextDouble())));
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "plannedAdSpend",
                "Invalid Planned Ad Spend. Values must be greater than or equal to 0.0"
        );
    }

    @Test
    @Ignore
    public void testFailedCostDetailValidationForImportRateRequired() {
        mediaRawDataView.setRate(null);

        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "rate",
                "Rate field is required"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportRateDecimalNumber() {
        String invalidValue = EntityFactory.faker.letterify("??????");
        mediaRawDataView.setRate(invalidValue);
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "rate",
                "Invalid Rate. Values must be in correct format (ex: 00.00)"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportRateNegativeNumber() {
        mediaRawDataView.setRate(String.valueOf(-Math.abs(EntityFactory.random.nextDouble())));
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "rate",
                "Invalid Rate. Values must be greater than or equal to 0.0"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportStartDateRequired() {
        mediaRawDataView.setStartDate(null);

        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "startDate",
                "Start Date field is required"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportStartDateWrongFormat() {
        String invalidValue = EntityFactory.faker.letterify("??????");
        mediaRawDataView.setStartDate(invalidValue);
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "startDate",
                "Invalid Start Date format. Format must be mm/dd/yyyy"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportEndDateBeforeStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateConverter.SUPPORTED_FORMATS[0]); //Format MM/dd/yyyy

        Calendar c = Calendar.getInstance();
        Date startDate = c.getTime();
        mediaRawDataView.setEndDate(sdf.format(startDate));
        c.add(Calendar.DAY_OF_YEAR, 100);
        Date endDate = c.getTime();
        mediaRawDataView.setStartDate(sdf.format(endDate));
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "startDate",
                ResourceBundleUtil.getString("global.error.startAfterEndDate", startDate, endDate)
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportEndDateRequired() {
        mediaRawDataView.setEndDate(null);

        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "endDate",
                "End Date field is required"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportEndDateWrongFormat() {
        String invalidValue = EntityFactory.faker.letterify("??????");
        mediaRawDataView.setEndDate(invalidValue);
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "endDate",
                "Invalid End Date format. Format must be mm/dd/yyyy"
        );
    }

    @Test
    @Ignore
    public void testFailedCostDetailValidationForImportRateTypeRequired() {
        mediaRawDataView.setRateType(null);
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "rateType",
                "Rate Type field is required"
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportRateTypeInvalid() {
        mediaRawDataView.setRateType(EntityFactory.faker.letterify("??????"));
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "rateType",
                String.format("Invalid Rate Type. %s is not supported", mediaRawDataView.getRateType())
        );
    }

    @Test
    public void testFailedCostDetailValidationForImportKnownRateTypeInLowercase() {
        mediaRawDataView.setRateType("cpm");
        assertMediaRaw(mediaRawDataView,
                mrdvErrors,
                "rateType",
                "Invalid Rate Type. cpm is not supported"
        );
    }

    private void assertMediaRaw(MediaRawDataView mediaRawDataView,
            BeanPropertyBindingResult mrdvErrors,
            String fieldName,
            String message) {
        validator.validateFieldsForImport(mediaRawDataView, mrdvErrors);
        assertThat(mrdvErrors, is(notNullValue()));
        assertThat(mrdvErrors.hasErrors(), is(true));
        assertThat(mrdvErrors + "", mrdvErrors.getErrorCount(), is(equalTo(1)));
        FieldError error = mrdvErrors.getFieldErrors().iterator().next();
        assertThat(error.getField(), is(fieldName));
        assertThat(error.getDefaultMessage(), is(equalTo(message)));
    }

}
