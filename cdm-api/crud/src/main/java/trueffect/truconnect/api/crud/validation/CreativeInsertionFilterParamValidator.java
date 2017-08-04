package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
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
public class CreativeInsertionFilterParamValidator implements Validator {

    @Override
    public boolean supports(Class aClass) {
        return CreativeInsertionFilterParam.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;

        this.validateCommonData(o, errors);

        if (errors.getErrorCount() == 0) {

            List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelByPivot = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParam.getPivotType().toUpperCase())).
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParam.getType().toUpperCase()));

            if ((hierarchyLevelByPivot != null) && !hierarchyLevelByPivot.isEmpty()) {
                validateCreativeInsertionFilterIds(o, hierarchyLevelByPivot, errors);
            }
        }
    }

    public void validateToGetData(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;

        this.validateCommonData(o, errors);

        if (errors.getErrorCount() == 0) {

            List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelByPivot = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParam.getPivotType().toUpperCase())).
                    get(CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class, filterParam.getType().toUpperCase()));

            if ((hierarchyLevelByPivot != null) && !hierarchyLevelByPivot.isEmpty()) {
                List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelToValidate = new ArrayList<>(hierarchyLevelByPivot);
                hierarchyLevelToValidate.remove(hierarchyLevelToValidate.size() - 1);
                validateCreativeInsertionFilterIds(o, hierarchyLevelToValidate, errors);
            }
        }
    }

    public void validateToSearchData(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;
        ApiValidationUtils.rejectIfBlank(errors, "pivotType");

        if (filterParam.getPivotType() != null && !filterParam.getPivotType().isEmpty()) {
            // types --> lowercase
            boolean isPivotTypeOk = filterParam.getPivotType().equals(filterParam.getPivotType().toLowerCase());
            boolean isLevelTypeOk = true;

            if (filterParam.getType() != null && !filterParam.getPivotType().isEmpty()) {
                isLevelTypeOk = filterParam.getType().equals(filterParam.getType().toLowerCase());
            }
            if (!isPivotTypeOk) {
                errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "pivotType"));
            }

            if (!isLevelTypeOk) {
                errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "type"));
            }

            if (isLevelTypeOk && isPivotTypeOk) {

                CreativeInsertionFilterParamTypeEnum pivotTypeEnum = null;
                try {
                    //validate pivot values
                    pivotTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class,
                            filterParam.getPivotType().toUpperCase());

                    if (!CreativeInsertionFilterParamTypeEnum.PIVOT_TYPES.contains(pivotTypeEnum)) {
                        pivotTypeEnum = null;
                        errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.invalid", "pivotType"));
                    }
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                    "pivotType", Arrays.toString(CreativeInsertionFilterParamTypeEnum.values())));
                }

                if (filterParam.getType() != null && !filterParam.getPivotType().isEmpty()) {
                    //Type and ID's validations only when type value is != null
                    CreativeInsertionFilterParamTypeEnum levelTypeEnum = null;
                    try {
                        //validate type values
                        levelTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class,
                                filterParam.getType().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                        "type", Arrays.toString(CreativeInsertionFilterParamTypeEnum.values())));
                    }

                    // validate id values
                    if (levelTypeEnum != null && pivotTypeEnum != null) {

                        List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelByPivot = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                                get(pivotTypeEnum).get(levelTypeEnum);
                        if ((hierarchyLevelByPivot != null) && !hierarchyLevelByPivot.isEmpty()) {
                            validateCreativeInsertionFilterIds(o, hierarchyLevelByPivot, errors);
                        } else {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                    }
                }
            }
        }
    }

    public void validateToGetPlacements(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;

        if (filterParam != null
                && (filterParam.getPivotType() != null && !filterParam.getPivotType().isEmpty()
                || filterParam.getType() != null && !filterParam.getType().isEmpty())) {

            this.validateCommonData(o, errors);

            if (errors.getErrorCount() == 0) {
                //Check valid pivots for getPlacement service
                CreativeInsertionFilterParamTypeEnum pivotTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(
                        CreativeInsertionFilterParamTypeEnum.class,
                        filterParam.getPivotType().toUpperCase());
                CreativeInsertionFilterParamTypeEnum levelTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(
                        CreativeInsertionFilterParamTypeEnum.class,
                        filterParam.getType().toUpperCase());

                switch (pivotTypeEnum) {
                    case SITE:
                        //check type
                        if (!levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SITE)) {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                        break;
                    case PLACEMENT:
                        //wrong pivot
                        errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.invalid", "pivotType"));
                        break;
                    case GROUP:
                        //check type
                        if (!(levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.GROUP)
                                || levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SITE))) {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                        break;
                    case CREATIVE:
                        //check type
                        if (!(levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.CREATIVE)
                                || levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SITE))) {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                        break;
                }

                if (errors.getErrorCount() == 0) {

                    List<CreativeInsertionFilterParamTypeEnum> hierarchyTypes = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                            get(pivotTypeEnum).get(levelTypeEnum);

                    if ((hierarchyTypes != null) && !hierarchyTypes.isEmpty()) {
                        validateCreativeInsertionFilterIds(o, hierarchyTypes, errors);
                    }
                }
            }
        }
    }

    public void validateToGetGroupCreatives(Object o, Errors errors) {

        if (errors == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Errors"));
        }

        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;

        if (filterParam != null
                && (filterParam.getPivotType() != null && !filterParam.getPivotType().isEmpty()
                || filterParam.getType() != null && !filterParam.getType().isEmpty())) {

            this.validateCommonData(o, errors);

            if (errors.getErrorCount() == 0) {
                //Check valid pivots for getGroupCreatives service
                CreativeInsertionFilterParamTypeEnum pivotTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(
                        CreativeInsertionFilterParamTypeEnum.class,
                        filterParam.getPivotType().toUpperCase());
                CreativeInsertionFilterParamTypeEnum levelTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(
                        CreativeInsertionFilterParamTypeEnum.class,
                        filterParam.getType().toUpperCase());

                switch (pivotTypeEnum) {
                    case SITE:
                        //check type
                        if (levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SECTION)
                                || levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SCHEDULE)) {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                        break;
                    case PLACEMENT:
                    case GROUP:
                        //check type
                        if (levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SCHEDULE)) {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                        break;
                    case CREATIVE:
                        //check type
                        if (levelTypeEnum.equals(CreativeInsertionFilterParamTypeEnum.SCHEDULE)) {
                            errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                    ResourceBundleUtil.getString("global.error.invalid", "type"));
                        }
                        break;
                }

                if (errors.getErrorCount() == 0) {

                    List<CreativeInsertionFilterParamTypeEnum> hierarchyTypes = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                            get(pivotTypeEnum).get(levelTypeEnum);

                    if ((hierarchyTypes != null) && !hierarchyTypes.isEmpty()) {
                        validateCreativeInsertionFilterIds(o, hierarchyTypes, errors);
                    }
                }
            }
        }
    }

    private void validateCommonData(Object o, Errors errors) {

        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;

        ApiValidationUtils.rejectIfBlank(errors, "type");
        ApiValidationUtils.rejectIfBlank(errors, "pivotType");

        if (filterParam.getType() != null && filterParam.getPivotType() != null) {
            // types --> lowercase
            boolean isLevelTypeOk = filterParam.getType().equals(filterParam.getType().toLowerCase());
            boolean isPivotTypeOk = filterParam.getPivotType().equals(filterParam.getPivotType().toLowerCase());

            if (!isLevelTypeOk) {
                errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "type"));
            }
            if (!isPivotTypeOk) {
                errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.invalid", "pivotType"));
            }

            if (isLevelTypeOk && isPivotTypeOk) {
                CreativeInsertionFilterParamTypeEnum pivotTypeEnum = null;
                CreativeInsertionFilterParamTypeEnum levelTypeEnum = null;
                try {
                    //validate pivot values
                    pivotTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class,
                            filterParam.getPivotType().toUpperCase());

                    if (!CreativeInsertionFilterParamTypeEnum.PIVOT_TYPES.contains(pivotTypeEnum)) {
                        pivotTypeEnum = null;
                        errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.invalid", "pivotType"));
                    }
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("pivotType", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                    "pivotType", Arrays.toString(CreativeInsertionFilterParamTypeEnum.values())));
                }

                try {
                    //validate type values
                    levelTypeEnum = CreativeInsertionFilterParamTypeEnum.valueOf(CreativeInsertionFilterParamTypeEnum.class,
                            filterParam.getType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidIfDoesNotContain",
                                    "type", Arrays.toString(CreativeInsertionFilterParamTypeEnum.values())));
                }

                // Check if type is present into pivot hierarchy
                if (levelTypeEnum != null && pivotTypeEnum != null) {

                    List<CreativeInsertionFilterParamTypeEnum> hierarchyLevelByPivot = CreativeInsertionFilterParamTypeEnum.HIERARCHIES_BY_PIVOT_TO_LEVEL.
                            get(pivotTypeEnum).get(levelTypeEnum);
                    if (hierarchyLevelByPivot == null || hierarchyLevelByPivot.isEmpty()) {
                        errors.rejectValue("type", ApiValidationUtils.TYPE_INVALID,
                                ResourceBundleUtil.getString("global.error.invalid", "type"));
                    }
                }
            }
        }
    }

    private void validateCreativeInsertionFilterIds(Object o, List<CreativeInsertionFilterParamTypeEnum> hierarchyTypes, Errors errors) {
        CreativeInsertionFilterParam filterParam = (CreativeInsertionFilterParam) o;

        for (CreativeInsertionFilterParamTypeEnum type : hierarchyTypes) {
            switch (type) {
                case SITE:
                    ApiValidationUtils.rejectIfBlank(errors, "siteId");
                    break;
                case SECTION:
                    ApiValidationUtils.rejectIfBlank(errors, "sectionId");
                    break;
                case PLACEMENT:
                    ApiValidationUtils.rejectIfBlank(errors, "placementId");
                    break;
                case GROUP:
                    ApiValidationUtils.rejectIfBlank(errors, "groupId");
                    break;
                case CREATIVE:
                    ApiValidationUtils.rejectIfBlank(errors, "creativeId");
                    break;
                case SCHEDULE:
                    ApiValidationUtils.rejectIfBlank(errors, "creativeId");
                    break;
            }
        }
    }
}
