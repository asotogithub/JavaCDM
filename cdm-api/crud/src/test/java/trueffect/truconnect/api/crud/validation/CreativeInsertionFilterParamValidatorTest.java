package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.crud.EntityFactory;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;

/**
 *
 * @author marleny.patsi
 */
public class CreativeInsertionFilterParamValidatorTest {

    private CreativeInsertionFilterParamValidator validator;
    private CreativeInsertionFilterParam creativeInsertionFilterParam;

    @Before
    public void setUp() {
        validator = new CreativeInsertionFilterParamValidator();
        creativeInsertionFilterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
    }

    @Test
    public void testValidateNominalCase() {
        //set data
        List<CreativeInsertionFilterParam> creativeInsertionsBulk = EntityFactory.createCreativeInsertionFilterForTypeAndPivot(1000);

        for (CreativeInsertionFilterParam bulk : creativeInsertionsBulk) {
            bulk.setType(bulk.getType().toLowerCase());
            bulk.setPivotType(bulk.getPivotType().toLowerCase());
        }

        // perform test
        for (CreativeInsertionFilterParam ciBulk : creativeInsertionsBulk) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ciBulk, "creativeInsertionFilterParam");
            ValidationUtils.invokeValidator(validator, ciBulk, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void testValidateNullTypes() {
        //set data
        creativeInsertionFilterParam.setPivotType(null);
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getDefaultMessage(), is("Invalid type, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));
    }

    @Test
    public void testValidateCapitalLevelTypeName() {
        //set data
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toLowerCase());
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void testValidateCapitalPivotTypeName() {
        //set data
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toUpperCase());
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void testValidateInvalidLevelType() {
        //set data
        creativeInsertionFilterParam.setType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void testValidateInvalidPivotType() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void testValidateToWrongPivotValueType() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void testValidateWrongTypeByPivotType() {
        //set data
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void testValidateNullId() {
        //set data
        creativeInsertionFilterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toLowerCase());
        creativeInsertionFilterParam.setPlacementId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        ValidationUtils.invokeValidator(validator, creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("placementId"));
        assertThat(String.valueOf(error.getRejectedValue()), is(String.valueOf(creativeInsertionFilterParam.getPlacementId())));
        assertThat(error.getDefaultMessage(), is("Invalid placementId, it cannot be empty."));
    }

    @Test
    public void validateToGetDataNominalCase() {
        //set data
        List<CreativeInsertionFilterParam> creativeInsertionsBulk = EntityFactory.createCreativeInsertionFilterForTypeAndPivot(100);

        for (CreativeInsertionFilterParam bulk : creativeInsertionsBulk) {
            bulk.setType(bulk.getType().toLowerCase());
            bulk.setPivotType(bulk.getPivotType().toLowerCase());
        }

        // perform test
        for (CreativeInsertionFilterParam ciBulk : creativeInsertionsBulk) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ciBulk, "creativeInsertionFilterParam");
            validator.validateToGetData(ciBulk, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void validateToGetDataNullType() {
        //set data
        creativeInsertionFilterParam.setType(null);
        creativeInsertionFilterParam.setPivotType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getDefaultMessage(), is("Invalid type, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));
    }

    @Test
    public void validateToGetDataCapitalTypeName() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toUpperCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetDataCapitalPivotTypeName() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGetDataInvalidLevelType() {
        //set data
        creativeInsertionFilterParam.setType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetDataInvalidLevelCreativeType() {
        //set data --> SITE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> PLACEMENT pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> GROUP pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.GROUP.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> CREATIVE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetDataInvalidPivotType() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetDataWrongPivotTypeValue() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGetDataWrongTypeByPivotType() {
        //set data
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetDataNullId() {
        //set data
        creativeInsertionFilterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toLowerCase());
        creativeInsertionFilterParam.setPlacementId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("placementId"));
        assertThat(String.valueOf(error.getRejectedValue()), is(String.valueOf(creativeInsertionFilterParam.getPlacementId())));
        assertThat(error.getDefaultMessage(), is("Invalid placementId, it cannot be empty."));
    }

    @Test
    public void validateToToSearchDataWithoutContextNominalCase() {
        //set data
        List<CreativeInsertionFilterParam> creativeInsertionsBulk = EntityFactory.createCreativeInsertionFilterForTypeAndPivot(20);

        for (CreativeInsertionFilterParam bulk : creativeInsertionsBulk) {
            //this test does not need a type (without context)
            bulk.setType(null);
            bulk.setPivotType(bulk.getPivotType().toLowerCase());
        }

        // perform test
        for (CreativeInsertionFilterParam ciBulk : creativeInsertionsBulk) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ciBulk, "creativeInsertionFilterParam");
            validator.validateToSearchData(ciBulk, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void validateToSearchDataWithoutContextNullPivotType() {
        //set data
        creativeInsertionFilterParam.setPivotType(null);
        //this test does not need a type (without context)
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));
    }

    @Test
    public void validateToSearchDataWithoutContextEmptyPivotType() {
        //set data
        creativeInsertionFilterParam.setPivotType("");
        //this test does not need a type (without context)
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));
    }

    @Test
    public void validateToSearchDataWithoutContextCapitalPivotTypeName() {
        //set data
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toUpperCase());
        //this test does not need a type (without context)
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToSearchDataWithoutContextInvalidPivotType() {
        //set data
        creativeInsertionFilterParam.setPivotType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());
        //this test does not need a type (without context)
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToSearchDataWithoutContextWrongPivotValueType() {
        //set data
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());
        //this test does not need a type (without context)
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToSearchDataWithContextNominalCase() {
        //set data
        List<CreativeInsertionFilterParam> creativeInsertionsBulk = EntityFactory.createCreativeInsertionFilterForTypeAndPivot(100);

        for (CreativeInsertionFilterParam bulk : creativeInsertionsBulk) {
            bulk.setType(bulk.getType().toLowerCase());
            bulk.setPivotType(bulk.getPivotType().toLowerCase());
        }

        // perform test
        for (CreativeInsertionFilterParam ciBulk : creativeInsertionsBulk) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(ciBulk, "creativeInsertionFilterParam");
            validator.validateToSearchData(ciBulk, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void validateToSearchDataWithContextCapitalTypeName() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toUpperCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToSearchDataWithContextCapitalPivotTypeName() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToSearchDataWithContextInvalidPivotType() {
        //set data
        creativeInsertionFilterParam.setPivotType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToSearchDataWithContextWrongPivotValueType() {
        //set data
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToSearchDataInvalidLevelType() {
        //set data
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToSearchDataInvalidLevelCreativeType() {
        //set data --> SITE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> PLACEMENT pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> GROUP pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.GROUP.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> CREATIVE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToSearchDataWithContextWrongTypeByPivotType() {
        //set data
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateTToSearchDataWithContextNullId() {
        //set data
        creativeInsertionFilterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toLowerCase());
        creativeInsertionFilterParam.setPlacementId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToSearchData(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("placementId"));
        assertThat(String.valueOf(error.getRejectedValue()), is(String.valueOf(creativeInsertionFilterParam.getPlacementId())));
        assertThat(error.getDefaultMessage(), is("Invalid placementId, it cannot be empty."));
    }

    @Test
    public void validateToGetPlacementsNominalCase() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetPlacementsNominalCaseNullParameter() {
        //set data
        creativeInsertionFilterParam = null;

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetPlacementsNominalCaseEmptyPivotAndTypeValues() {
        //set data
        creativeInsertionFilterParam.setType(null);
        creativeInsertionFilterParam.setPivotType("");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetPlacementsEmtpyPivotTypeValue() {
        //set data --> null
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));

        //set data --> empty
        creativeInsertionFilterParam.setPivotType("");

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetPlacementsEmtpyLevelValue() {
        //set data --> null
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getDefaultMessage(), is("Invalid type, it cannot be empty."));

        //set data --> empty
        creativeInsertionFilterParam.setType("");

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetPlacementsCapitalLevelTypeName() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetPlacementsCapitalPivotTypeName() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGetPlacementsInvalidLevelType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetPlacementsInvalidPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetPlacementsWrongTextOnPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGetPlacementsWrongTypeByPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetPlacementsWrongPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGetPlacementsInvalidLevelCreativeType() {
        //set data --> SITE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> PLACEMENT pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> GROUP pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.GROUP.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> CREATIVE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetPlacementsWithSitePivotWrongLevelType() {
        //set data SECTION type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SECTION);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data PLACEMENT type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data GROUP type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetPlacementsWithGroupPivotWrongLevelType() {
        //set data PLACEMENT type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.PLACEMENT);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetPlacementsWithCreativePivotWrongLevelType() {
        //set data PLACEMENT type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.CREATIVE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data GROUP type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.CREATIVE, CreativeInsertionFilterParamTypeEnum.GROUP);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.CREATIVE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetPlacementsNullIds() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setSiteId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetPlacements(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("siteId"));
        assertThat(String.valueOf(error.getRejectedValue()), is(String.valueOf(creativeInsertionFilterParam.getSiteId())));
        assertThat(error.getDefaultMessage(), is("Invalid siteId, it cannot be empty."));
    }

    @Test
    public void validateToGetGroupCreativesNominalCase() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetGroupCreativesNominalCaseNullParameters() {
        //set data
        creativeInsertionFilterParam = null;

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetGroupCreativesNominalCaseEmptyPivotAndTypeValues() {
        //set data
        creativeInsertionFilterParam.setType(null);
        creativeInsertionFilterParam.setPivotType("");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetGroupCreativesEmtpyPivotTypeValue() {
        //set data --> null
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));

        //set data --> empty
        creativeInsertionFilterParam.setPivotType("");

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetGroupCreativesEmtpyLevelValue() {
        //set data --> null
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getDefaultMessage(), is("Invalid type, it cannot be empty."));

        //set data --> empty
        creativeInsertionFilterParam.setType("");

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGetGroupCreativesCapitalLevelTypeName() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetGroupCreativesCapitalPivotTypeName() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toUpperCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGettGroupCreativesInvalidLevelType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid type, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGettGroupCreativesInvalidPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(),
                is("Invalid pivotType, it should be one of " + Arrays.toString(CreativeInsertionFilterParamTypeEnum.values()) + "."));
    }

    @Test
    public void validateToGettGroupCreativesWrongTextOnPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SITE);
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("pivotType"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getPivotType()));
        assertThat(error.getDefaultMessage(), is("Invalid pivotType value."));
    }

    @Test
    public void validateToGettGroupCreativesWrongTypeByPivotType() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.GROUP);
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.SECTION.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetGroupCreativesInvalidLevelCreativeType() {
        //set data --> SITE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.SITE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> PLACEMENT pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.PLACEMENT.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> GROUP pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.GROUP.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is("creative"));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data --> CREATIVE pivot
        creativeInsertionFilterParam.setPivotType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());
        creativeInsertionFilterParam.setType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateToGetGroupCreativesWithSitePivotWrongLevelType() {
        //set data SECTION type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SECTION);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetGroupCreativesWithPlacementPivotWrongLevelType() {
        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));

    }

    @Test
    public void validateToGetGroupCreativesWithGroupPivotWrongLevelType() {
        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.GROUP, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGetGroupCreativesWithCreativePivotWrongLevelType() {
        //set data SCHEDULE type
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.CREATIVE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("type"));
        assertThat(error.getRejectedValue().toString(), is(creativeInsertionFilterParam.getType()));
        assertThat(error.getDefaultMessage(), is("Invalid type value."));
    }

    @Test
    public void validateToGettGroupCreativesNullIds() {
        //set data
        creativeInsertionFilterParam = prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);
        creativeInsertionFilterParam.setGroupId(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creativeInsertionFilterParam, "creativeInsertionFilterParam");
        validator.validateToGetGroupCreatives(creativeInsertionFilterParam, errors);
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("groupId"));
        assertThat(String.valueOf(error.getRejectedValue()), is(String.valueOf(creativeInsertionFilterParam.getGroupId())));
        assertThat(error.getDefaultMessage(), is("Invalid groupId, it cannot be empty."));
    }

    private CreativeInsertionFilterParam prepareDataToGetTest(CreativeInsertionFilterParamTypeEnum pivotType, CreativeInsertionFilterParamTypeEnum levelType) {
        creativeInsertionFilterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                pivotType, levelType);
        creativeInsertionFilterParam.setPivotType(creativeInsertionFilterParam.getPivotType().toLowerCase());
        creativeInsertionFilterParam.setType(creativeInsertionFilterParam.getType().toLowerCase());
        return creativeInsertionFilterParam;
    }
}
