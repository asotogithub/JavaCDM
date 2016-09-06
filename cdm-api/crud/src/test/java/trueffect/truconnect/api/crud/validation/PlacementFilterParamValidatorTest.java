package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class PlacementFilterParamValidatorTest {

    private PlacementFilterParamValidator validator;
    private PlacementFilterParam placementFilterParam;

    @Before
    public void setUp() {
        validator = new PlacementFilterParamValidator();
        placementFilterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);
    }

    @Test
    public void testValidateNominalCase() {
        // prepare data
        List<PlacementFilterParam> filterParams = EntityFactory.createPlacementFilterParam(10);

        // perform test
        for (PlacementFilterParam filterParam : filterParams) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    filterParam, "placementFilterParam");
            ValidationUtils.invokeValidator(validator, filterParam, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void validateWithoutLastIdFromHierarchy() {
        // prepare data
        placementFilterParam.setPlacementId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        ValidationUtils.invokeValidator(validator, placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateWithNullLevelType() {
        // prepare data
        placementFilterParam.setLevelType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        ValidationUtils.invokeValidator(validator, placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid levelType, it cannot be empty."));
    }

    @Test
    public void validateWithEmptyLevelType() {
        // prepare data
        placementFilterParam.setLevelType("");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        ValidationUtils.invokeValidator(validator, placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(), is(equalTo("")));
        assertThat(error.getDefaultMessage(), is("Invalid levelType, it cannot be empty."));
    }

    @Test
    public void validateWithCapitalLevelTypeValue() {
        // prepare data
        placementFilterParam.setLevelType(placementFilterParam.getLevelType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        ValidationUtils.invokeValidator(validator, placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(placementFilterParam.getLevelType())));
        assertThat(error.getDefaultMessage(), is("Invalid levelType value."));
    }

    @Test
    public void validateWithInvalidValueLevelType() {
        // prepare data
        placementFilterParam.setLevelType(EntityFactory.faker.lorem()
                .fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        ValidationUtils.invokeValidator(validator, placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(placementFilterParam.getLevelType())));
        assertThat(error.getDefaultMessage(),
                is("Invalid levelType, it should be one of " + Arrays
                        .toString(PlacementFilterParamLevelTypeEnum.values()) + "."));
    }

    @Test
    public void validateWithNullId() {
        // prepare data
        placementFilterParam.setCampaignId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        ValidationUtils.invokeValidator(validator, placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("campaignId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid campaignId, it cannot be empty."));
    }

    @Test
    public void testValidateGetAssociationsNominalCase() {
        // prepare data
        List<PlacementFilterParam> filterParams = EntityFactory.createPlacementFilterParam(10);

        // perform test
        for (PlacementFilterParam filterParam : filterParams) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    filterParam, "placementFilterParam");
            validator.validateGetAssociations(filterParam, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void validateGetAssociationsWithNullLevelType() {
        // prepare data
        placementFilterParam.setLevelType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        validator.validateGetAssociations(placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid levelType, it cannot be empty."));
    }

    @Test
    public void validateGetAssociationsWithCapitalLevelTypeValue() {
        // prepare data
        placementFilterParam.setLevelType(placementFilterParam.getLevelType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        validator.validateGetAssociations(placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(placementFilterParam.getLevelType())));
        assertThat(error.getDefaultMessage(), is("Invalid levelType value."));
    }

    @Test
    public void validateGetAssociationsWithInvalidValueLevelType() {
        // prepare data
        placementFilterParam.setLevelType(EntityFactory.faker.lorem()
                .fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        validator.validateGetAssociations(placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(placementFilterParam.getLevelType())));
        assertThat(error.getDefaultMessage(),
                is("Invalid levelType, it should be one of " + Arrays
                        .toString(PlacementFilterParamLevelTypeEnum.values()) + "."));
    }

    @Test
    public void validateGetAssociationsWithNullId() {
        // prepare data
        placementFilterParam.setCampaignId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        validator.validateGetAssociations(placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("campaignId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid campaignId, it cannot be empty."));
    }

    @Test
    public void validateGetAssociationsWithoutLastIdFromHierarchy() {
        // prepare data
        placementFilterParam.setPlacementId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                placementFilterParam, "placementFilterParam");
        validator.validateGetAssociations(placementFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("placementId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid placementId, it cannot be empty."));
    }
}
