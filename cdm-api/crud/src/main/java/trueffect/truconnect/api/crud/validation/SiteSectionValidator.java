package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates a Site Section
 *
 * The default validation is done for Site Section creation
 * Created by marcelo.heredia on 4/28/2016.
 * @author Marcelo Heredia
 */
public class SiteSectionValidator implements Validator {

    public boolean supports(Class type) {
        return type == SiteSection.class;
    }


    @Override
    public void validate(Object o, Errors errors) {

        ApiValidationUtils.rejectIfPostWithId(errors, "id", ResourceBundleUtil.getString("global.label.siteSection"));
        ApiValidationUtils.rejectIfBlank(errors, "siteId");
        Pattern pattern = Pattern.compile(ValidationConstants.REGEXP_ALPHANUMERIC_AND_SPECIAL_CHARACTERS, Pattern.CASE_INSENSITIVE);
        ApiValidationUtils.rejectIfDoesNotMatchPattern(errors, "name",
                    ResourceBundleUtil.getString("global.error.name",
                            ResourceBundleUtil.getString("global.label.section")), pattern);
        ApiValidationUtils.rejectIfBlank(errors, "name");
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "name", Constants.MAX_SITE_SECTION_NAME_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "agencyNotes", Constants.MAX_SITE_SECTION_AGENCY_NOTES_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "publisherNotes", Constants.MAX_SITE_SECTION_PUBLISHER_NOTES_LENGHT);
    }

    /**
     * Validates import fields related to a {@code SiteSection}
     * <p>
     *     Use this in bulk import process
     * </p>
     * @param media The {@code MediaRawDataView} to validate
     * @param errors The {@code Errors} object where all validation errors are collected
     */
    public void validateFieldsForImport(MediaRawDataView media, Errors errors){
        String message;

        if(media.getFieldsWithFormulaError().contains("section")) { //contains error in XLS formulas
            errors.rejectValue("section", ApiValidationUtils.TYPE_INVALID, ResourceBundleUtil
                    .getString("global.error.formulaError",
                            ResourceBundleUtil.getString("global.label.section")));
        } else {
            String section = media.getSection();
            String sectionLabel = ResourceBundleUtil.getString("global.label.siteSection");
            ApiValidationUtils.rejectIfBlank(errors, "section", ApiValidationUtils.TYPE_INVALID,
                    ResourceBundleUtil.getString("global.error.importRequiredField",
                            sectionLabel));

            message = ResourceBundleUtil.getString("global.error.importFieldLength",
                    sectionLabel,
                    Constants.MAX_SITE_SECTION_NAME_LENGTH);

            ApiValidationUtils.rejectIfCharactersUpTo(errors, "section",
                    Constants.MAX_SITE_SECTION_NAME_LENGTH, section, message);
            if (!errors.hasFieldErrors("section")) {
                media.setSection(section.trim());
            }
        }

        // ExtProps
        int i = 1;
        String sectionProp = "sectionProp";
        String sectionPropLabel = ResourceBundleUtil.getString("global.label.sectionProp");

        List<String> extProps = Arrays.asList(media.getSectionProp1(), media.getSectionProp2(),
                media.getSectionProp3(), media.getSectionProp4(), media.getSectionProp5());
        for (; i <= 5; i++) {
            if (media.getFieldsWithFormulaError().contains(sectionProp + i)) { //contains error in XLS formulas
                errors.rejectValue(sectionProp + i, ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString(
                                "global.error.formulaError", sectionPropLabel + i));
            } else {
                String sectionPropValue = extProps.get(i - 1);
                message = ResourceBundleUtil.getString("global.error.importFieldLength",
                        sectionPropLabel + i, Constants.DEFAULT_CHARS_LENGTH);
                ApiValidationUtils.rejectIfCharactersUpTo(errors, sectionProp + i,
                        Constants.DEFAULT_CHARS_LENGTH, sectionPropValue, message);
                if (!errors.hasFieldErrors(sectionProp + i) && StringUtils.isNotEmpty(sectionPropValue)) {
                    extProps.set(i - 1, sectionPropValue.trim());
                }
            }
        }
        i = 0;
        media.setSectionProp1(extProps.get(i++));
        media.setSectionProp2(extProps.get(i++));
        media.setSectionProp3(extProps.get(i++));
        media.setSectionProp4(extProps.get(i++));
        media.setSectionProp5(extProps.get(i));
    }
}
