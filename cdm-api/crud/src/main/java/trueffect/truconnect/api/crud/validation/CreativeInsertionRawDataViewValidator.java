package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.crud.service.CreativeManager;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * CreativeInsertionImportView validator
 * @author abel.soto
 */
public class CreativeInsertionRawDataViewValidator implements Validator {

    public static final String CREATIVE_INSERTION_ID = "Creative Insertion ID";
    public static final String END_DATE = "End Date";
    public static final String START_DATE = "Start Date";

    public static final String ERR_MSG_INVALID_DATE = "%1$s is invalid. It should be a valid Date with the following format: %2$s";
    public static final String ERR_MSG_INVALID_NUMBER = "%s is invalid. It can only contain numbers from 0 - 9";

    public static final String ERR_MSG_BEFORE_THAN = "%1$s should be before than %2$s";
    public static final String ERR_MSG_BLANK_ID = "Blank %s. This row will be ignored";
    public static final String FIELD_GROUP_WEIGHT = "groupWeight";
    public static final String FIELD_CREATIVE_WEIGHT = "creativeWeight";
    public static final String FIELD_CLICKTHROUGH = "creativeClickThroughUrl";
    public static final String FIELD_START_DATE = "creativeStartDate";
    public static final String FIELD_END_DATE = "creativeEndDate";
    public static final String FIELD_CREATIVE_INSERTION_ID = "creativeInsertionId";

    @Override
    public boolean supports(Class clazz) {
        return CreativeInsertionRawDataView.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if(errors == null){
            throw new IllegalArgumentException("Errors cannot be null");
        }
        CreativeInsertionRawDataView cInsertionImportView = (CreativeInsertionRawDataView) o;

        // Creative Group weight.
        if (StringUtils.isNotBlank(cInsertionImportView.getGroupWeight())) {
            long min = Constants.CREATIVE_GROUP_MIN_WEIGHT;
            long max = Constants.CREATIVE_GROUP_MAX_WEIGHT;
            try {
                long weight = Long.parseLong(cInsertionImportView.getGroupWeight().trim());

                if (weight < min || weight > max) {
                    errors.rejectValue(FIELD_GROUP_WEIGHT,
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.outOfRanges",
                                    ResourceBundleUtil.getString("creativeInsertion.label.creativeGroupWeight"),
                                    min,
                                    max));
                }
                else {
                    cInsertionImportView.setGroupWeight(String.valueOf(weight));
                }
            } catch (NumberFormatException e) {
                errors.rejectValue(FIELD_GROUP_WEIGHT,
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.outOfRanges",
                                ResourceBundleUtil.getString("creativeInsertion.label.creativeGroupWeight"),
                                min,
                                max));
            }
        }
        // Creative Insertion weight
        if (StringUtils.isNotBlank(cInsertionImportView.getCreativeWeight())) {
            long min = Constants.CREATIVE_INSERTION_MIN_WEIGHT;
            long max = Constants.CREATIVE_INSERTION_MAX_WEIGHT;
            try {
                long weight = Long.parseLong(cInsertionImportView.getCreativeWeight().trim());

                if (weight < min || weight > max) {
                    errors.rejectValue(FIELD_CREATIVE_WEIGHT,
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.outOfRanges",
                                    ResourceBundleUtil.getString("creativeInsertion.label.creativeWeight"),
                                    min,
                                    max));
                }
                else {
                    cInsertionImportView.setCreativeWeight(String.valueOf(weight));
                }
            } catch (NumberFormatException e) {
                errors.rejectValue(FIELD_CREATIVE_WEIGHT,
                        ApiValidationUtils.TYPE_INVALID,
                        ResourceBundleUtil.getString("global.error.outOfRanges",
                                ResourceBundleUtil.getString("creativeInsertion.label.creativeWeight"),
                                min,
                                max));
            }
        }

        // Start and End dates
        Date startDate = null, endDate = null;
        if(StringUtils.isNotBlank(cInsertionImportView.getCreativeStartDate())) {
            try {
                String currentStartDate = cInsertionImportView.getCreativeStartDate().trim();
                if(currentStartDate.matches(ValidationConstants.XLS_DATE_VALUE_REGEXP)){
                    startDate = DateUtil.getJavaDate(Double.valueOf(currentStartDate));
                } else {
                    startDate = DateConverter.parseForImport(cInsertionImportView.getCreativeStartDate().trim());
                }
                startDate = DateConverter.toDefaultTimezone(startDate);
                cInsertionImportView.setCreativeStartDate(
                        DateConverter.importExportFormat(DateConverter.startDate(startDate)));
                if (startDate.before(Constants.MIN_DATE) || startDate.after(Constants.MAX_DATE)) {
                    errors.rejectValue(FIELD_START_DATE,
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidDate",
                                    FIELD_START_DATE,
                                    ResourceBundleUtil.getString("global.minDate"),
                                    ResourceBundleUtil.getString("global.maxDate")));
                    startDate = null;
                }

            } catch (ParseException e) {
                errors.rejectValue(FIELD_START_DATE,
                        ApiValidationUtils.TYPE_INVALID,
                        String.format(ERR_MSG_INVALID_DATE,
                                START_DATE,
                                DateConverter.DEFAULT_IMPORT_EXPORT_FORMAT));
            }
        }

        if(StringUtils.isNotBlank(cInsertionImportView.getCreativeEndDate())) {
            try {
                String currentEndDate = cInsertionImportView.getCreativeEndDate().trim();
                if(currentEndDate.matches(ValidationConstants.XLS_DATE_VALUE_REGEXP)){
                    endDate = DateUtil.getJavaDate(Double.valueOf(currentEndDate));
                } else {
                    endDate = DateConverter.parseForImport(cInsertionImportView.getCreativeEndDate().trim());
                }
                endDate = DateConverter.toDefaultTimezone(endDate);
                cInsertionImportView.setCreativeEndDate(DateConverter.importExportFormat(DateConverter.endDate(endDate)));
                if (endDate.before(Constants.MIN_DATE) || endDate.after(Constants.MAX_DATE)) {
                    errors.rejectValue(FIELD_END_DATE,
                            ApiValidationUtils.TYPE_INVALID,
                            ResourceBundleUtil.getString("global.error.invalidDate",
                                    FIELD_END_DATE,
                                    ResourceBundleUtil.getString("global.minDate"),
                                    ResourceBundleUtil.getString("global.maxDate")));
                }
            } catch (ParseException e) {
                errors.rejectValue(FIELD_END_DATE,
                        ApiValidationUtils.TYPE_INVALID,
                        String.format(ERR_MSG_INVALID_DATE,
                                END_DATE,
                                DateConverter.DEFAULT_IMPORT_EXPORT_FORMAT));
            }
        }


        // Validate Date Range only if previous two are valid
        if(startDate != null && endDate != null) {
            if(startDate.compareTo(endDate) > 0) {
                errors.rejectValue(FIELD_START_DATE,
                        ApiValidationUtils.TYPE_INVALID,
                        String.format(ERR_MSG_BEFORE_THAN, START_DATE, END_DATE));
            }
        } else {
            // In this case, we make sure to null out the start and end dates as they
            // should be provided both at the same time
            cInsertionImportView.setCreativeStartDate(null);
            cInsertionImportView.setCreativeEndDate(null);
        }

        // Creative Insertion Id
        if (StringUtils.isNotBlank(cInsertionImportView.getCreativeInsertionId())) {
            try {
                long id = Long.parseLong(cInsertionImportView.getCreativeInsertionId().trim());
                if (id < 0) {
                    errors.rejectValue(FIELD_CREATIVE_INSERTION_ID,
                            ApiValidationUtils.TYPE_INVALID,
                            String.format(ERR_MSG_INVALID_NUMBER, CREATIVE_INSERTION_ID));
                } else {
                    cInsertionImportView.setCreativeInsertionId(id + "");
                }
            }catch (NumberFormatException e) {
                errors.rejectValue(FIELD_CREATIVE_INSERTION_ID,
                        ApiValidationUtils.TYPE_INVALID,
                        String.format(ERR_MSG_INVALID_NUMBER, CREATIVE_INSERTION_ID));
            }
        } else {
            errors.rejectValue(FIELD_CREATIVE_INSERTION_ID,
                    ApiValidationUtils.TYPE_INVALID,
                    String.format(ERR_MSG_BLANK_ID,
                            CREATIVE_INSERTION_ID));
        }
    }
    
    public void validateClickthrough(CreativeInsertionRawDataView cInsertionImportView, Errors errors) {
        String creativeType = cInsertionImportView.getCreativeType();
        // Creative Insertion Clickthroughs
        if (creativeType != null) {
            if (!creativeType.equalsIgnoreCase(CreativeManager.CreativeType.TRD.getCreativeType())) {
                if (StringUtils.isNotBlank(cInsertionImportView.getCreativeClickThroughUrl())) {
                    String urls = cInsertionImportView.getCreativeClickThroughUrl();
                    String cts[] = new String[]{urls};
                    if (creativeType.equalsIgnoreCase(
                            CreativeManager.CreativeType.ZIP.getCreativeType()) || creativeType
                            .equalsIgnoreCase(
                                    CreativeManager.CreativeType.HTML5.getCreativeType())) {
                        cts = urls.split(",");
                    }
                    List<String> trimmedUrls = new ArrayList<>();
                    String msg = "";
                    for (String url : cts) {
                        String trimmedUrl = url.trim();
                        Matcher matcher = ValidationConstants.PATTERN_CLICKTHROUGH_URL.matcher(trimmedUrl);
                        if (StringUtils.isEmpty(url) || !matcher.matches()) {
                            msg += ResourceBundleUtil.getString("creativeInsertion.error.invalidClickthrough", url) + ", ";
                        } else {
                            trimmedUrls.add(trimmedUrl);
                        }
                    }
                    if (StringUtils.isNotEmpty(msg)) {
                        msg = msg.substring(0, msg.length() - 2);
                        errors.rejectValue(FIELD_CLICKTHROUGH,
                                ApiValidationUtils.TYPE_INVALID,
                                msg);
                    } else {
                        cInsertionImportView.setCreativeClickThroughUrl(StringUtils.join(trimmedUrls, ','));
                    }
                } else {
                    // Setting to null to make Data Validation workflow to complete successfully
                    cInsertionImportView.setCreativeClickThroughUrl(null);
                }
            } else {
                ApiValidationUtils.rejectIfNotNull(errors, "creativeClickThroughUrl");
            }
        }
    }
}