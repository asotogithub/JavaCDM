package trueffect.truconnect.api.crud.validation;

import trueffect.truconnect.api.commons.exceptions.business.ImportExportCellError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.importexport.RawDataView;
import trueffect.truconnect.api.commons.model.importexport.enums.ImportIssueType;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validates that a {@code MediaRawDataView} is valid
 * Created by richard.jaldin on 5/5/2016.
 * @author Richard Jaldin
 */
public class MediaRawDataViewValidator implements Validator {

    private PublisherValidator publisherValidator;
    private InsertionOrderValidator insertionOrderValidator;
    private SiteValidator siteValidator;
    private SiteSectionValidator siteSectionValidator;
    private SizeValidator sizeValidator;
    private PackageValidator packageValidator;
    private PlacementValidator placementValidator;
    private CostDetailImportValidator costDetailValidator;

    public MediaRawDataViewValidator(PublisherValidator publisherValidator,
                                     InsertionOrderValidator insertionOrderValidator,
                                     SiteValidator siteValidator,
                                     SiteSectionValidator siteSectionValidator,
                                     SizeValidator sizeValidator,
                                     PackageValidator packageValidator,
                                     PlacementValidator placementValidator,
                                     CostDetailImportValidator costDetailValidator) {
        this.publisherValidator = publisherValidator;
        this.insertionOrderValidator = insertionOrderValidator;
        this.siteValidator = siteValidator;
        this.siteSectionValidator = siteSectionValidator;
        this.sizeValidator = sizeValidator;
        this.packageValidator = packageValidator;
        this.placementValidator = placementValidator;
        this.costDetailValidator = costDetailValidator;
    }

    @Override
    public boolean supports(Class clazz) {
        return MediaRawDataView.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (errors == null) {
            throw new IllegalArgumentException("Errors cannot be null");
        }
        MediaRawDataView mediaImportView = (MediaRawDataView) o;
        if (mediaImportView.getPlacementId() == null && mediaImportView
                .getSiteId() == null && mediaImportView.getMediaPackageId() == null) {
            // All the elements are for a new Placement
            // 1. Validate Publisher fields
            publisherValidator.validateFieldsForImport(mediaImportView, errors);
            // 2. Validate Insertion Order fields
            insertionOrderValidator.validateFieldsForImport(mediaImportView, errors);
            // 3. Validate Site fields
            siteValidator.validateFieldsForImport(mediaImportView, errors);
            // 4. Validate Site Section fields
            siteSectionValidator.validateFieldsForImport(mediaImportView, errors);
            // 5. Validate Size fields
            sizeValidator.validateFieldsForImport(mediaImportView, errors);
        } else {
            // media has to be updated and there is an error
            String message = null;
            String field = null;
            if (mediaImportView.getPlacementId() == null) {
                field = "placementId";
                message = ResourceBundleUtil.getString("global.error.requiredField",
                        ResourceBundleUtil.getString("global.label.placementId"));
            } else if (mediaImportView.getSiteId() == null) {
                field = "siteId";
                message = ResourceBundleUtil.getString("global.error.requiredField",
                        ResourceBundleUtil.getString("global.label.siteId"));
            } else if (mediaImportView.getMediaPackageId() == null && mediaImportView.getMediaPackageName() != null) {
                field = "mediaPackageId";
                message = ResourceBundleUtil.getString("global.error.requiredField",
                        ResourceBundleUtil.getString("global.label.mediaPackageId"));
            }
            if (field != null) {
                ApiValidationUtils.rejectIfBlank(errors, field, ApiValidationUtils.TYPE_REQUIRED,
                        message);
            }
        }
        // 6. Validate Package fields
        packageValidator.validateFieldsForImport(mediaImportView, errors);
        // 7. Validate Placement fields
        placementValidator.validateFieldsForImport(mediaImportView, errors);
        // 8. Validate Cost Details fields
        ValidationUtils.invokeValidator(costDetailValidator, mediaImportView, errors);
    }


    /**
     * Copies a list of Spring {@code Errors} into a {@code RawDataView}
     *
     * @param rawData The {@code RawDataView} to copy to
     * @param errors The {@code Errors} object where to extract the errors from
     */
    public static void copyErrors(RawDataView rawData, Errors errors) {
        if(rawData == null) {
            throw new IllegalArgumentException("rawData cannot be null");
        }
        if(errors == null) {
            throw new IllegalArgumentException("errors cannot be null");
        }
        Map<String, List<ImportExportCellError>> errorMap =
                rawData.getIssues() == null ? new HashMap<String, List<ImportExportCellError>>() : rawData
                        .getIssues();
        for (FieldError fieldError : errors.getFieldErrors()) {
            if (errorMap.get(fieldError.getField()) == null) {
                errorMap.put(fieldError.getField(), new ArrayList<ImportExportCellError>());
            }
            ImportExportCellError error =
                    new ImportExportCellError(fieldError.getDefaultMessage(), ValidationCode.INVALID);
            error.setField(fieldError.getField());
            error.setType(ApiValidationUtils.TYPE_IMPORT_WARNING.equals(fieldError
                    .getCode()) ? ImportIssueType.WARNING : ImportIssueType.ERROR);
            errorMap.get(fieldError.getField()).add(error);
        }
        rawData.setIssues(errorMap);
    }
}
