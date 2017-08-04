package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class PlacementFilterParamValidator implements Validator {

    @Override
    public boolean supports(Class aClass) {
        return PlacementFilterParam.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        this.validateCommonData(o, errors);
        PlacementFilterParam filterParam = (PlacementFilterParam) o;
        if (errors.getErrorCount() == 0) {

            List<PlacementFilterParamLevelTypeEnum> hierarchyByLevelType = PlacementFilterParamLevelTypeEnum.DEFAULT_HIERARCHY_TO_LEVEL.
                    get(PlacementFilterParamLevelTypeEnum.valueOf(PlacementFilterParamLevelTypeEnum.class, filterParam.getLevelType().toUpperCase()));

            if ((hierarchyByLevelType != null) && !hierarchyByLevelType.isEmpty()) {
                List<PlacementFilterParamLevelTypeEnum> hierarchyLevelToValidate = new ArrayList<>(hierarchyByLevelType);
                // remove the last element from hierarchy because this id is not mandatory
                hierarchyLevelToValidate.remove(hierarchyLevelToValidate.size() - 1);
                validateFilterParamIds(filterParam, hierarchyLevelToValidate, errors);
            }
        }
    }

    public void validateGetAssociations(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(
                    ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        this.validateCommonData(o, errors);
        PlacementFilterParam filterParam = (PlacementFilterParam) o;
        if (errors.getErrorCount() == 0) {

            List<PlacementFilterParamLevelTypeEnum> hierarchyByLevelType = PlacementFilterParamLevelTypeEnum
                    .DEFAULT_HIERARCHY_TO_LEVEL.get(PlacementFilterParamLevelTypeEnum.valueOf(
                            PlacementFilterParamLevelTypeEnum.class,
                            filterParam.getLevelType().toUpperCase()));

            if ((hierarchyByLevelType != null) && !hierarchyByLevelType.isEmpty()) {
                validateFilterParamIds(filterParam, hierarchyByLevelType, errors);
            }
        }
    }

    private void validateCommonData(Object o, Errors errors) {

        PlacementFilterParam filterParam = (PlacementFilterParam) o;

        ApiValidationUtils.rejectIfBlank(errors, "levelType");

        if (!errors.hasFieldErrors("levelType")) {
            // TODO: Once we have TA-1226 code, this if validation can be replaced to:
            // ApiValidationUtils.rejectIfNotInEnum(errors, "rateType", PlacementFilterParamLevelTypeEnum.class, true,
            // ResourceBundleUtil.getString("global.error.unsupportedValueForField", "levelType", filterParam.getLevelType()));
            // levelType --> lowercase
            boolean isLevelTypeOk = filterParam.getLevelType().equals(filterParam.getLevelType().toLowerCase());

            if (!isLevelTypeOk) {
                errors.rejectValue("levelType", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "levelType"));
            }

            if (isLevelTypeOk) {
                try {
                    //validate type values
                    PlacementFilterParamLevelTypeEnum.valueOf(PlacementFilterParamLevelTypeEnum.class,
                            filterParam.getLevelType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("levelType", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                    "levelType", Arrays.toString(PlacementFilterParamLevelTypeEnum.values())));
                }
            }
        }
    }

    private void validateFilterParamIds(Object o, List<PlacementFilterParamLevelTypeEnum> hierarchyLevelTypes, Errors errors) {

        PlacementFilterParam filterParam = (PlacementFilterParam) o;
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
}
