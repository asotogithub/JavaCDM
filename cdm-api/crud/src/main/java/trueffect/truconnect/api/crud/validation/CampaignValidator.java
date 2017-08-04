package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Rambert Rioja
 */
public class CampaignValidator implements Validator {

    private static final int NAME_LENGTH_LIMIT = 200;
    private static final int DESCRIPTION_LENGTH_LIMIT = 256;
    private static final double OVERALL_BUDGET_MIN = 0.0;
    private static final double OVERALL_BUDGET_MAX = 2147483647.0;

    @SuppressWarnings("rawtypes")
    public boolean supports(Class type) {
        return type == Campaign.class;
    }

    public void validate(Object o, Errors errors) {
        ApiValidationUtils.rejectIfPostWithId(errors, "id", "Campaign");
        ApiValidationUtils.rejectIfBlankOrCharactersUpTo(errors, "name", NAME_LENGTH_LIMIT);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "description", DESCRIPTION_LENGTH_LIMIT);

        validateCommons(o, errors);
    }

    public void validate(Long id, Object o, Errors errors) {
        ApiValidationUtils.rejectIfPutWithoutId(errors, "id", "Campaign");
        ApiValidationUtils.rejectIfIdDoesntMatch(errors, "id", id);
        ApiValidationUtils.rejectIfCharactersUpTo(errors, "name", NAME_LENGTH_LIMIT);
        validateCommons(o, errors);
    }

    private void validateCommons(Object o, Errors errors) {
        Campaign campaign = (Campaign) o;
        ApiValidationUtils.rejectIfNull(errors, "overallBudget");
        ApiValidationUtils.rejectIfOutOfRangeOrNotNumber(errors, "overallBudget", OVERALL_BUDGET_MIN, OVERALL_BUDGET_MAX);
        // startDate -> today's date, endDate -> today's date + 10 years
        if (campaign.getStartDate() == null) {
            campaign.setStartDate(new Date());
        }
        if (campaign.getEndDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, 10);
            campaign.setEndDate(calendar.getTime());
        }
        if (campaign.getStartDate().compareTo(campaign.getEndDate()) > 0) {
            String message = "End Date is less than Start Date; Start Date:" + campaign.getStartDate() + ", End Date:" + campaign.getEndDate();
            errors.rejectValue("endDate", ApiValidationUtils.TYPE_INVALID, message);
        }
    }
}
