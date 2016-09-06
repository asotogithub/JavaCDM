package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.dto.SmPingEventDTO;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventPingTagType;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventPingType;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rodrigo.alarcon on 8/12/2016.
 */
public class SiteMeasurementEventPingValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == SmPingEventDTO.class;
    }

    @Override
    public void validate(Object o, Errors errors) {

    }

    public void validateSiteMeasurementEventForCreate(SmPingEventDTO smPingEvent, Errors errors) {
        validateCommons(smPingEvent, errors);
    }

    public void validateSiteMeasurementEventForUpdate(SmPingEventDTO smPingEvent, Errors errors) {
        if (smPingEvent.getPingId() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Ping Id");
            errors.rejectValue("id", ApiValidationUtils.TYPE_INVALID, message);
        }

        validateCommons(smPingEvent, errors);
    }

    private void validateCommons(SmPingEventDTO smPingEvent, Errors errors) {
        if (smPingEvent.getId() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Event Id");
            errors.rejectValue("id", ApiValidationUtils.TYPE_INVALID, message);
        }

        if (smPingEvent.getPingType() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Ping Type Id");
            errors.rejectValue("pingType", ApiValidationUtils.TYPE_INVALID, message);
        }

        if (smPingEvent.getPingTagType() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Ping Tag Type Id");
            errors.rejectValue("pingTagType", ApiValidationUtils.TYPE_INVALID, message);
        }

        if (smPingEvent.getSiteId() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Site Id");
            errors.rejectValue("siteId", ApiValidationUtils.TYPE_INVALID, message);
        }

        List<String> pingTypes = Arrays.asList(
                String.valueOf(SiteMeasurementEventPingType.SELECTIVE.value()),
                String.valueOf(SiteMeasurementEventPingType.BROADCAST.value()));
        if (smPingEvent.getPingType() != null) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "pingType", pingTypes);
        }

        List<String> pingTagTypesForBroadcast = Arrays.asList(
                String.valueOf(SiteMeasurementEventPingTagType.IMG.ordinal()),
                String.valueOf(SiteMeasurementEventPingTagType.IFRAME.ordinal()),
                String.valueOf(SiteMeasurementEventPingTagType.TAG.ordinal()));
        if (smPingEvent.getPingTagType() != null
                && smPingEvent.getPingType() == SiteMeasurementEventPingType.BROADCAST.value()) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "pingTagType", pingTagTypesForBroadcast);
        }

        List<String> pingTagTypesForSelective= Arrays.asList(
                String.valueOf(SiteMeasurementEventPingTagType.IMG.ordinal()));
        if (smPingEvent.getPingTagType() != null
                && smPingEvent.getPingType() == SiteMeasurementEventPingType.SELECTIVE.value()) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "pingTagType", pingTagTypesForSelective);
        }

        ApiValidationUtils.rejectIfBlank(errors, "pingContent");

        if (smPingEvent.getPingTagType() == SiteMeasurementEventPingTagType.TAG.ordinal()
                && StringUtils.isNotBlank(smPingEvent.getPingContent())) {
            ApiValidationUtils.rejectIfCannotFindUsingPattern(errors, "pingContent",
                    ResourceBundleUtil.getString("global.error.invalidHtml"),
                    ValidationConstants.PATTERN_HTML_CONTENT);
        }

        if ((smPingEvent.getPingTagType() == SiteMeasurementEventPingTagType.IMG.ordinal()
                || smPingEvent.getPingTagType() == SiteMeasurementEventPingTagType.IFRAME.ordinal())
                && StringUtils.isNotBlank(smPingEvent.getPingContent())) {
            ApiValidationUtils.rejectIfCannotFindUsingPattern(errors, "pingContent",
                    ResourceBundleUtil.getString("global.error.invalidUrl"),
                    ValidationConstants.PATTERN_URL);
        }
    }
}
