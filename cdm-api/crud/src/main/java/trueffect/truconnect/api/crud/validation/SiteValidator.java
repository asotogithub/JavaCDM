package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

/**
 *
 * @author Gustavo Claure
 */
public class SiteValidator implements Validator {

    public enum TagType {
        IFRAME,
        SCRIPT,
        IMAGE,
        HREF
    }
    public enum Enabled {
        Y, N;
        @Override
        public String toString() {
            return name();
        }
    }

    public enum Target {
        _TOP,
        _BLANK;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public boolean supports(Class type) {
        return type == Site.class;
    }

    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Site");
        ApiValidationUtils.rejectIfBlank(errors, "publisherId");
        validateCommons(o, errors);
    }

    public void validatePUT(Object o, Long id, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Site");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        Site site = (Site) o;
        Pattern pattern = Pattern.compile(ValidationConstants.REGEXP_ALPHANUMERIC_AND_SPECIAL_CHARACTERS, Pattern.CASE_INSENSITIVE);
        ApiValidationUtils.rejectIfDoesNotMatchPattern(errors, "name",
                    ResourceBundleUtil.getString("global.error.name",
                            ResourceBundleUtil.getString( "global.label.site")), pattern);
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "agencyNotes", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "publisherNotes", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "url", Constants.DEFAULT_CHARS_LENGTH);
        ApiValidationUtils.rejectIfNotInEnum(errors, "preferredTag", TagType.class, false, null);

        applyDefaults(site);

        ApiValidationUtils.rejectIfNotInEnum(errors, "clickTrack", Enabled.class, false, null);
        ApiValidationUtils.rejectIfNotInEnum(errors, "richMedia", Enabled.class, false, null);
        ApiValidationUtils.rejectIfNotInEnum(errors, "acceptsFlash", Enabled.class, false, null);
        ApiValidationUtils.rejectIfNotInEnum(errors, "targetWin", Target.class, false, null);
        ApiValidationUtils.rejectIfNotInEnum(errors, "encode", Enabled.class, false, null);
    }

    public Site applyDefaults(Site site) {
        if (StringUtils.isBlank(site.getClickTrack())) {
            site.setClickTrack(Constants.NO_FLAG);
        }

        if (StringUtils.isBlank(site.getRichMedia())) {
            site.setRichMedia(Constants.NO_FLAG);
        }

        if (StringUtils.isBlank(site.getAcceptsFlash())) {
            site.setAcceptsFlash(Constants.NO_FLAG);
        }

        if (StringUtils.isBlank(site.getEncode())) {
            site.setEncode(Constants.NO_FLAG);
        }

        if (StringUtils.isBlank(site.getTargetWin())) {
            site.setTargetWin(Target._BLANK.name().toLowerCase());
        }

        if (StringUtils.isBlank(site.getPreferredTag())) {
            site.setPreferredTag(TagType.IFRAME.toString());
        }



        return site;
    }

    /**
     * Validates all import fields related to a {@code Site}
     * @param media The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors) {
        if(media.getFieldsWithFormulaError().contains("site")) { //contains error in XLS formulas
            errors.rejectValue("site", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.site")));
        } else {
            String site = media.getSite();
            String siteLabel = ResourceBundleUtil.getString("global.label.site");
            ApiValidationUtils.rejectIfBlank(errors, "site", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField",
                            siteLabel));
            String message = ResourceBundleUtil
                    .getString("global.error.importFieldLength", siteLabel,
                            Constants.DEFAULT_CHARS_LENGTH);
            ApiValidationUtils
                    .rejectIfCharactersUpTo(errors, "site", Constants.DEFAULT_CHARS_LENGTH,
                            media.getSite(), message);
            if (errors.getFieldError("site") == null) {
                media.setSite(site.trim());
            }
        }
    }

}
