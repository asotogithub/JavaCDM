package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementAttrSettingsMethodology;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by richard.jaldin on 8/12/2016.
 */
public class SiteMeasurementValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == SiteMeasurementDTO.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        SiteMeasurementDTO siteMeasurement = (SiteMeasurementDTO) o;
        ApiValidationUtils.rejectIfBlank(errors, "brandId", ApiValidationUtils.TYPE_REQUIRED);
        ApiValidationUtils.rejectIfBlank(errors, "cookieDomainId",
                ApiValidationUtils.TYPE_REQUIRED);
        ApiValidationUtils.rejectIfBlank(errors, "expirationDate",
                ApiValidationUtils.TYPE_REQUIRED);
        applyDefaults(siteMeasurement);
        validateCommons(errors);
    }

    private void applyDefaults(SiteMeasurementDTO siteMeasurement) {
        //STATE VALUE by default 1--> NEW
        if ((siteMeasurement.getState() != null &&
                siteMeasurement.getState() != Constants.STATE_TRAFFICKED_SITEMEASUREMENT)
                || siteMeasurement.getState() == null) {
            siteMeasurement.setState(Constants.DEFAULT_STATE_NEW_SITEMEASUREMENT);
        }
        if (siteMeasurement.getTtVer() == null) {
            siteMeasurement.setTtVer(Constants.DEFAULT_SITE_MEASUREMENT_TRU_TAG_VERSION);
        }

        //DEFAULT VALUES FOR ATTRIBUTION SETTINGS
        if (siteMeasurement.getAssocMethod() == null) {
            siteMeasurement.setAssocMethod(Constants.DEFAULT_SITE_MEASUREMENT_METHODOLOGY);
        }
        if (!siteMeasurement.getAssocMethod()
                            .equals(SiteMeasurementAttrSettingsMethodology.CHNL.name())) {
            if (siteMeasurement.getClWindow() == null) {
                siteMeasurement.setClWindow(Constants.DEFAULT_SITE_MEASUREMENT_CLICK_WINDOW);
            }
            if (siteMeasurement.getVtWindow() == null) {
                siteMeasurement.setVtWindow(Constants.DEFAULT_SITE_MEASUREMENT_VIEW_WINDOW);
            }
        }
    }

    public void validateForUpdate(Long id, SiteMeasurementDTO dto,
                                  Errors errors) {
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        ApiValidationUtils.rejectIfNotInEnum(errors, "assocMethod",
                SiteMeasurementAttrSettingsMethodology.class, true, null);

        if (dto.getAssocMethod() != null && (dto.getAssocMethod()
                .equals(SiteMeasurementAttrSettingsMethodology.FIRST.name()) || dto
                .getAssocMethod().equals(SiteMeasurementAttrSettingsMethodology.CLICK.name()))) {
            if (dto.getClWindow() == null) {
                dto.setClWindow(Constants.DEFAULT_SITE_MEASUREMENT_CLICK_WINDOW);
            } else if (dto.getClWindow() > Constants.MAX_CLICK_WINDOW_VALUE || dto
                    .getClWindow() < Constants.MIN_CLICK_WINDOW_VALUE) {
                String message = ResourceBundleUtil.getString("global.error.outOfRange",
                        ResourceBundleUtil.getString("global.label.clWindow"),
                        Constants.MIN_CLICK_WINDOW_VALUE, Constants.MAX_CLICK_WINDOW_VALUE);
                errors.rejectValue("clWindow", ApiValidationUtils.TYPE_INVALID, message);
            }
            if (dto.getVtWindow() == null) {
                dto.setVtWindow(Constants.DEFAULT_SITE_MEASUREMENT_VIEW_WINDOW);
            } else if (dto.getVtWindow() > Constants.MAX_VIEW_WINDOW_VALUE || dto
                    .getVtWindow() < Constants.MIN_VIEW_WINDOW_VALUE) {
                String message = ResourceBundleUtil.getString("global.error.outOfRange",
                        ResourceBundleUtil.getString("global.label.vtWindow"),
                        Constants.MIN_VIEW_WINDOW_VALUE, Constants.MAX_VIEW_WINDOW_VALUE);
                errors.rejectValue("vtWindow", ApiValidationUtils.TYPE_INVALID, message);
            }
        } else if (dto.getAssocMethod() != null && dto.getAssocMethod()
                      .equals(SiteMeasurementAttrSettingsMethodology.CHNL.name())) {
            if (dto.getClWindow() != null) {
                String message = ResourceBundleUtil.getString("global.error.invalid",
                        ResourceBundleUtil.getString("global.label.clWindow"));
                errors.rejectValue("clWindow", ApiValidationUtils.TYPE_INVALID, message);
            }
            if (dto.getVtWindow() != null) {
                String message = ResourceBundleUtil.getString("global.error.invalid",
                        ResourceBundleUtil.getString("global.label.vtWindow"));
                errors.rejectValue("vtWindow", ApiValidationUtils.TYPE_INVALID, message);
            }
        }

        validateCommons(errors);
    }

    private void validateCommons(Errors errors) {
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name",
                Constants.SITE_MEASUREMENT_NAME_MAX_LENGTH);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "notes",
                Constants.SITE_MEASUREMENT_NOTES_MAX_LENGTH);
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "assocMethod",
                Constants.SITE_MEASUREMENT_ASSOCIATION_METHOD_MAX_LENGTH);
    }
}
