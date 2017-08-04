package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.SmEvent;
import trueffect.truconnect.api.commons.model.dto.SiteMeasurementDTO;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventTagType;
import trueffect.truconnect.api.commons.model.enums.SiteMeasurementEventType;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rodrigo.alarcon on 8/2/2016.
 */
public class SiteMeasurementEventValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == SmEvent.class;
    }

    @Override
    public void validate(Object o, Errors errors) {

    }

    /**
     * Validates if a Site Measurement Event can be updated
     * @param smEventId The Site Measurement Event ID
     * @param smEvent The Site Measurement Event object to validate
     * @param errors The {@code Errors} object containing the validation results
     */
    public void validateSiteMeasurementEventForUpdate(Long smEventId, SmEvent smEvent, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "SmEvent");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", smEventId);

        validateCommons(smEvent, errors);
    }

    protected void validateCommons(Object o, Errors errors) {

        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "eventName", Constants.SITE_MEASUREMENT_EVENT_NAME_MAX_LENGTH);

        SmEvent smEvent = (SmEvent) o;

        if (smEvent.getEventType() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Event Tag Type Id");
            errors.rejectValue("eventType", ApiValidationUtils.TYPE_INVALID, message);
        }

        if (smEvent.getSmEventType() == null) {
            String message = ResourceBundleUtil.getString("global.error.null", "Event Type Id");
            errors.rejectValue("smEventType", ApiValidationUtils.TYPE_INVALID, message);
        }

        List<String> tagTypes = Arrays.asList(
                String.valueOf(SiteMeasurementEventTagType.STANDARD.ordinal()),
                String.valueOf(SiteMeasurementEventTagType.TRU_TAG.ordinal()));
        if (smEvent.getEventType() != null) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "eventType", tagTypes);
        }

        List<String> eventTypes = Arrays.asList(
                String.valueOf(SiteMeasurementEventType.OTHER.ordinal()),
                String.valueOf(SiteMeasurementEventType.MEASURED.ordinal()),
                String.valueOf(SiteMeasurementEventType.CONVERSION.ordinal()),
                String.valueOf(SiteMeasurementEventType.CONVERSION_WITH_REVENUE.ordinal()));
        if (smEvent.getSmEventType() != null) {
            ApiValidationUtils.rejectIfDoesNotContain(errors, "smEventType", eventTypes);
        }
    }

    public void validateSiteMeasurementEventStateForUpdate(SmEvent smEvent, SmEvent existingSmEvent,
                                                           SiteMeasurementDTO existingSiteMeasurement, Errors errors) {

        if (!(smEvent.getEventName() == null && existingSmEvent.getEventName() == null)
                && !smEvent.getEventName().equals(existingSmEvent.getEventName())) {
            String message = ResourceBundleUtil.getString("global.error.cannotChange", "Event Name");
            errors.rejectValue("eventName", ApiValidationUtils.TYPE_INVALID, message);
        }

        if (!(smEvent.getLocation() == null && existingSmEvent.getLocation() == null)
                && !smEvent.getLocation().equals(existingSmEvent.getLocation())) {
            String message = ResourceBundleUtil.getString("global.error.cannotChange", "Location");
            errors.rejectValue("location", ApiValidationUtils.TYPE_INVALID, message);
        }

        if (smEvent.getEventType() != existingSmEvent.getEventType()) {
            String message = ResourceBundleUtil.getString("global.error.cannotChange", "Event Tag Type");
            errors.rejectValue("eventType", ApiValidationUtils.TYPE_INVALID, message);
        }
    }
}
