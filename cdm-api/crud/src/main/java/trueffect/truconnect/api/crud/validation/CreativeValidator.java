package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.service.CreativeManager;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 *
 * @author Richard Jaldin
 */
public class CreativeValidator implements Validator {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Creative.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Creative");
        validateCommons(o, errors);
        Creative cg = (Creative) o;
        if (StringUtils.contains(cg.getFilename(), ' ')) {
            errors.rejectValue("filename", ApiValidationUtils.TYPE_INVALID, "The Creative is missing or requires "
                    + "corrections to be made to the filename. It cannot contain spaces. Please correct the details for the information provided.");
        }
    }

    public void validateForUpdate(Creative creative, Long id, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Creative");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "extProp1",
                Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "extProp2", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "extProp3", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "extProp4", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "extProp5", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "purpose", Constants.DEFAULT_CHARS_LENGTH);
        validateClickthroughs(creative, errors);
        validateCreativeGroupAssociations(creative, errors);
        // Update externalId
        // Make sure to replace spaces for '_' , only for Creative update. Other places using
        // external IDs don't support this same rule (yet)
        if(StringUtils.isNotEmpty(creative.getExternalId())) {
            creative.setExternalId(creative.getExternalId().replace(' ', '_'));
        }
        validateAliasAndVersions(creative, errors);
    }

    private void validateCreativeGroupAssociations(Creative creative, Errors errors) {
        if (creative.getCreativeGroups() != null && creative.getCreativeGroups().isEmpty()) {
            errors.rejectValue("creativeGroups",
                    ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("creative.error.noGroupAssociations"));
        }
    }

    private void validateAliasAndVersions(Creative creative, Errors errors) {
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "alias", Constants.DEFAULT_CHARS_LENGTH);
        // If no aliases to validate; then, quit
        if(creative.getVersions() == null) {
            creative.setVersions(new ArrayList<CreativeVersion>());
            return;
        }

        Set<CreativeVersion> versions = new LinkedHashSet<>(creative.getVersions());
        for(int i = 0; i < versions.size(); i++) {
            // Reset the Creative id for each Version received.
            // Assign the id that came in the Creative itself
            String field = String.format("versions[%s]", i);
            ((CreativeVersion)errors.getFieldValue(field)).setCreativeId(creative.getId());
            errors.pushNestedPath(field);
            ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors,
                    "alias",
                    Constants.DEFAULT_CHARS_LENGTH);
            ApiValidationUtils.rejectIfBlank(errors,
                    "versionNumber");
            ApiValidationUtils.rejectIfNotPositiveLong(errors,
                    "versionNumber",
                    ResourceBundleUtil.getString("global.error.positiveNumber",
                            "versionNumber"));
            errors.popNestedPath();
        }
        creative.setVersions(new ArrayList<>(versions));
    }

    private void validateClickthroughs(Creative creative, Errors errors) {
        if (creative.getCreativeType().equalsIgnoreCase(CreativeManager.CreativeType.TRD.getCreativeType())) {
            if (!StringUtils.isBlank(creative.getClickthrough())) {
                errors.rejectValue("clickthrough", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil
                                .getString("creativeInsertion.error.clickthroughNotRequired"));
            }
        } else {
            if (StringUtils.isBlank(creative.getClickthrough())) {
                errors.rejectValue("clickthrough", ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("creativeInsertion.error.clickthroughRequired"));
            } else {
                String trimmedUrl = creative.getClickthrough().trim();
                Matcher matcher = ValidationConstants.PATTERN_CLICKTHROUGH_URL.matcher(trimmedUrl);
                if (!matcher.matches()) {
                    errors.rejectValue("clickthrough", ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughLegacy"));
                } else {
                    creative.setClickthrough(trimmedUrl);
                    if (creative.getClickthroughs() != null) {
                        int i = 0;
                        for (Clickthrough ct : creative.getClickthroughs()) {
                            trimmedUrl = ct.getUrl().trim();
                            matcher = ValidationConstants.PATTERN_CLICKTHROUGH_URL.matcher(trimmedUrl);
                            if (!matcher.matches()) {
                                errors.rejectValue("clickthroughs[" + i + "].url", ApiValidationUtils.TYPE_INVALID,
                                        ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthroughLegacy"));
                            } else {
                                ct.setUrl(trimmedUrl);
                            }
                            i++;
                        }
                    }
                }
            }
        }
    }

    private void validateCommons(Object o, Errors errors) {
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "alias", 256);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "purpose", 256);
    }
}