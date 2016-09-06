package trueffect.truconnect.api.crud.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
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
 * @author marleny.patsi
 */
public class PlacementActionTagAssocParamValidatorTest {

    private PlacementActionTagAssocParamValidator validator;
    private PlacementActionTagAssocParam createParam;

    @Before
    public void setUp() {
        validator = new PlacementActionTagAssocParamValidator();
        createParam = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.PLACEMENT,"c");
    }

    @Test
    public void testValidateNominalCase() {
        // prepare data
        List<PlacementActionTagAssocParam> params =
                EntityFactory.createPlacementCreateTagAssocParam(100, "c");

        // perform test
        for (PlacementFilterParam createParam : params) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                    createParam, "placementCreateTagAssocParam");
            ValidationUtils.invokeValidator(validator, createParam, errors);
            assertThat(errors.hasErrors(), is(false));
        }
    }

    @Test
    public void validateWithNullLevelType() {
        // prepare data
        createParam.setLevelType(null);
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

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
        createParam.setLevelType(null);
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid levelType, it cannot be empty."));
    }

    @Test
    public void validateWithCapitalLevelTypeValue() {
        // prepare data
        createParam.setLevelType(createParam.getLevelType().toUpperCase());
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(createParam.getLevelType())));
        assertThat(error.getDefaultMessage(), is("Invalid levelType value."));
    }

    @Test
    public void validateWithInvalidValueLevelType() {
        // prepare data
        createParam.setLevelType(EntityFactory.faker.lorem().fixedString(10).toLowerCase());
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("levelType"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(createParam.getLevelType())));
        assertThat(error.getDefaultMessage(),
                is("Invalid levelType, it should be one of " + Arrays
                        .toString(PlacementFilterParamLevelTypeEnum.values()) + "."));
    }

    @Test
    public void validateWithNullHttmlTagInjectionId() {
        // prepare data
        createParam.setHtmlInjectionId(null);
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("htmlInjectionId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(),
                is("Invalid htmlInjectionId, it cannot be empty."));
    }

    @Test
    public void validateParamIdsForCampaignLevel() {
        // campaignId -->value
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.CAMPAIGN,"c");
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));

        // campaignId --> null
        // prepare data
        createParam = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN, "c");
        createParam.setCampaignId(null);
        createParam.setAction("c");

        // perform test
        errors = new BeanPropertyBindingResult(createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("campaignId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid campaignId, it cannot be empty."));
    }

    @Test
    public void validateParamIdsForSiteLevel() {
        // campaignId, siteId --> value
        // prepare data
        createParam = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.SITE, "c");
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));

        // campaignId, siteId --> null
        // prepare data
        createParam = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.SITE, "c");
        createParam.setCampaignId(null);
        createParam.setSiteId(null);
        createParam.setAction("c");

        // perform test
        errors = new BeanPropertyBindingResult(createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(2));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("campaignId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid campaignId, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("siteId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid siteId, it cannot be empty."));
    }

    @Test
    public void validateParamIdsForSectionLevel() {
        // campaignId, siteId, sectionId --> value
        // prepare data
        createParam = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.SECTION, "c");
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));

        // campaignId, siteId, sectionId --> null
        // prepare data
        createParam = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.SECTION, "c");
        createParam.setCampaignId(null);
        createParam.setSiteId(null);
        createParam.setSectionId(null);
        createParam.setAction("c");

        // perform test
        errors = new BeanPropertyBindingResult(createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(3));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("campaignId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid campaignId, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("siteId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid siteId, it cannot be empty."));
        error = errors.getFieldErrors().get(2);
        assertThat(error.getField(), is("sectionId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid sectionId, it cannot be empty."));
    }

    @Test
    public void validateParamIdsForPlacementLevel() {
        // campaignId, siteId, sectionId, placementId --> value
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT, "c");
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));

        // campaignId, siteId, sectionId, placementId --> null
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT, "c");
        createParam.setCampaignId(null);
        createParam.setSiteId(null);
        createParam.setSectionId(null);
        createParam.setPlacementId(null);
        createParam.setAction("c");

        // perform test
        errors = new BeanPropertyBindingResult(createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(4));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("campaignId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid campaignId, it cannot be empty."));
        error = errors.getFieldErrors().get(1);
        assertThat(error.getField(), is("siteId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid siteId, it cannot be empty."));
        error = errors.getFieldErrors().get(2);
        assertThat(error.getField(), is("sectionId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid sectionId, it cannot be empty."));
        error = errors.getFieldErrors().get(3);
        assertThat(error.getField(), is("placementId"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid placementId, it cannot be empty."));
    }

    @Test
    public void validateCleanParamIdsForCampaignLevel() {
        // campaignId, siteId, sectionId, placementId --> value
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT, "c");
        createParam.setLevelType(PlacementFilterParamLevelTypeEnum.CAMPAIGN.toString().toLowerCase());
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));
        assertThat(createParam.getCampaignId(), is(notNullValue()));
        assertThat(createParam.getSiteId(), is(nullValue()));
        assertThat(createParam.getSectionId(), is(nullValue()));
        assertThat(createParam.getPlacementId(), is(nullValue()));
    }

    @Test
    public void validateCleanParamIdsForSiteLevel() {
        // campaignId, siteId, sectionId, placementId --> value
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT, "c");
        createParam.setLevelType(PlacementFilterParamLevelTypeEnum.SITE.toString().toLowerCase());
        createParam.setAction("c");


        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));
        assertThat(createParam.getCampaignId(), is(notNullValue()));
        assertThat(createParam.getSiteId(), is(notNullValue()));
        assertThat(createParam.getSectionId(), is(nullValue()));
        assertThat(createParam.getPlacementId(), is(nullValue()));
    }

    @Test
    public void validateCleanParamIdsForSectionLevel() {
        // campaignId, siteId, sectionId, placementId --> value
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT, "c");
        createParam.setLevelType(PlacementFilterParamLevelTypeEnum.SECTION.toString().toLowerCase());
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));
        assertThat(createParam.getCampaignId(), is(notNullValue()));
        assertThat(createParam.getSiteId(), is(notNullValue()));
        assertThat(createParam.getSectionId(), is(notNullValue()));
        assertThat(createParam.getPlacementId(), is(nullValue()));
    }

    @Test
    public void validateCleanParamIdsForPlacementLevel() {
        // campaignId, siteId, sectionId, placementId --> value
        // prepare data
        createParam = EntityFactory.createPlacementActionTagAssocParam(
                PlacementFilterParamLevelTypeEnum.PLACEMENT, "c");
        createParam.setAction("c");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(false));
        assertThat(createParam.getCampaignId(), is(notNullValue()));
        assertThat(createParam.getSiteId(), is(notNullValue()));
        assertThat(createParam.getSectionId(), is(notNullValue()));
        assertThat(createParam.getPlacementId(), is(notNullValue()));
    }

    @Test
    public void validateWithNullAction() {
        // prepare data
        createParam.setAction(null);

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("action"));
        assertThat(error.getRejectedValue(), is(equalTo(null)));
        assertThat(error.getDefaultMessage(), is("Invalid action, it cannot be empty."));
    }

    @Test
    public void validateWithCapitalActionValue() {
        // prepare data
        createParam.setAction("D");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("action"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(createParam.getAction())));
        assertThat(error.getDefaultMessage(), is("Invalid action value."));
    }

    @Test
    public void validateWithInvalidValueAction() {
        // prepare data
        createParam.setAction("x");

        // perform test
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                createParam, "placementCreateTagAssocParam");
        ValidationUtils.invokeValidator(validator, createParam, errors);

        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getErrorCount(), is(1));
        FieldError error = errors.getFieldErrors().get(0);
        assertThat(error.getField(), is("action"));
        assertThat(error.getRejectedValue().toString(),
                is(equalTo(createParam.getAction())));
        assertThat(error.getDefaultMessage(),
                is("Invalid action, it should be one of " + Arrays
                        .toString(PlacementActionTagAssocParam.PlacementAction.values()) + "."));
    }
}
