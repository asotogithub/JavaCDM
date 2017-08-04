package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author marleny.patsi
 */
public class PlacementActionTagAssocParamValidator implements Validator {

    @Override
    public boolean supports(Class aClass) {
        return PlacementActionTagAssocParam.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        // tagId
        ApiValidationUtils.rejectIfBlank(errors, "htmlInjectionId");

        // levelType
        this.validateCommonData(o, errors);

        if (errors.getErrorCount() == 0) {

            PlacementActionTagAssocParam createAssocParam = (PlacementActionTagAssocParam) o;

            // campaignId, siteId, sectionId, placementId,
            List<PlacementFilterParamLevelTypeEnum> hierarchyByLevelType =
                    PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.
                            get(PlacementFilterParamLevelTypeEnum.valueOf(
                                    PlacementFilterParamLevelTypeEnum.class,
                                    createAssocParam.getLevelType().toUpperCase()));

            if ((hierarchyByLevelType != null) && !hierarchyByLevelType.isEmpty()) {
                List<PlacementFilterParamLevelTypeEnum> hierarchyLevelToValidate =
                        new ArrayList<>(hierarchyByLevelType);
                validateFilterParamIds(createAssocParam, hierarchyLevelToValidate, errors);
            }
            // clean ids
            this.cleanParamIds(createAssocParam);
        }
    }

    private void validateCommonData(Object o, Errors errors) {

        PlacementActionTagAssocParam createAssocParam = (PlacementActionTagAssocParam) o;

        ApiValidationUtils.rejectIfBlank(errors, "levelType");

        ApiValidationUtils.rejectIfBlank(errors, "action");

        if (!errors.hasFieldErrors("levelType")) {
            // levelType --> lowercase
            boolean isLevelTypeOk = createAssocParam.getLevelType().equals(
                    createAssocParam.getLevelType().toLowerCase());

            if (!isLevelTypeOk) {
                errors.rejectValue("levelType", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "levelType"));
            }

            if (isLevelTypeOk) {
                try {
                    //validate type values
                    PlacementFilterParamLevelTypeEnum.valueOf(PlacementFilterParamLevelTypeEnum.class,
                            createAssocParam.getLevelType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("levelType", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                    "levelType",
                                    Arrays.toString(PlacementFilterParamLevelTypeEnum.values())));
                }
            }
        }
        if (!errors.hasFieldErrors("action")) {
            // levelType --> lowercase
            boolean isActionOk = createAssocParam.getAction().equals(
                    createAssocParam.getAction().toLowerCase());

            if (!isActionOk) {
                errors.rejectValue("action", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "action"));
            }

            if (isActionOk) {
                try {
                    PlacementFilterParamLevelTypeEnum.valueOf(PlacementActionTagAssocParam.PlacementAction.class,
                            createAssocParam.getAction().toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("action", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                    "action",
                                    Arrays.toString(PlacementActionTagAssocParam.PlacementAction.values())));
                }
            }
        }
    }

    private void validateFilterParamIds(Object o,
                                        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelTypes,
                                        Errors errors) {

        PlacementActionTagAssocParam createAssocParam = (PlacementActionTagAssocParam) o;
        for (PlacementFilterParamLevelTypeEnum type : hierarchyLevelTypes) {
            switch (type) {
                case CAMPAIGN:
                    ApiValidationUtils.rejectIfBlank(errors, "campaignId");
                    break;
                case SITE:
                    ApiValidationUtils.rejectIfBlank(errors, "siteId");
                    break;
                case SECTION:
                    ApiValidationUtils.rejectIfBlank(errors, "sectionId");
                    break;
                case PLACEMENT:
                    ApiValidationUtils.rejectIfBlank(errors, "placementId");
                    break;
            }
        }
    }

    private void cleanParamIds(PlacementActionTagAssocParam createAssocParam) {

        List<PlacementFilterParamLevelTypeEnum> hierarchyByLevelTypes =
                PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_FROM_LEVEL.
                        get(PlacementFilterParamLevelTypeEnum.valueOf(
                                PlacementFilterParamLevelTypeEnum.class, 
                                createAssocParam.getLevelType().toUpperCase()));

        List<PlacementFilterParamLevelTypeEnum> hierarchyLevelToClean =
                new ArrayList<>(hierarchyByLevelTypes);
        // campaign or site
        hierarchyLevelToClean.remove(0);

        for (PlacementFilterParamLevelTypeEnum type : hierarchyLevelToClean) {
            switch (type) {
                case CAMPAIGN:
                    createAssocParam.setCampaignId(null);
                    break;
                case SITE:
                    createAssocParam.setSiteId(null);
                    break;
                case SECTION:
                    createAssocParam.setSectionId(null);
                    break;
                case PLACEMENT:
                    createAssocParam.setPlacementId(null);
                    break;
            }
        }
    }
}
