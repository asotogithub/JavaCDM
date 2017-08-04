package trueffect.truconnect.api.crud.service;

import com.trueffect.delivery.formats.adm.Cookie;
import com.trueffect.delivery.formats.adm.CookieList;
import com.trueffect.delivery.formats.adm.DatasetConfig;
import com.trueffect.delivery.formats.adm.FailThroughDefault;
import com.trueffect.delivery.formats.adm.FailThroughDefaults;
import com.trueffect.delivery.formats.adm.failThroughEnum;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.dto.DatasetMetrics;
import trueffect.truconnect.api.commons.model.dto.DatedSummary;
import trueffect.truconnect.api.commons.model.dto.Summary;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.crud.validation.DatasetConfigViewValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import org.springframework.validation.BeanPropertyBindingResult;

import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.immutable.Set;
import scala.collection.mutable.StringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manager for dataset configs.
 */
public class DatasetManager extends AbstractGenericManager {

    // Format for dates in DatasetMetrics YYYY-MM-DDT00:00:00-07:00
    private static final DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTimeNoMillis();

    private final DatasetConfigDao datasetConfigDao;
    private final AdvertiserDao advertiserDao;
    private final AdmTransactionDao admTransactionDao;
    private DatasetConfigViewValidator validator;

    public DatasetManager(DatasetConfigDao datasetConfigDao, AdvertiserDao advertiserDao, AdmTransactionDao admTransactionDao, AccessControl accessControl) {
        super(accessControl);
        this.datasetConfigDao = datasetConfigDao;
        this.advertiserDao = advertiserDao;
        this.admTransactionDao = admTransactionDao;
        validator = new DatasetConfigViewValidator();
    }

    /**
     * Check if the user has authorization for the Advertiser and Agency associated with the dataset
     * @param datasetConfig the dataset's DatasetConfig
     * @param key           authorization key
     * @param session       AdvertiserDao session
     * 
     * @return      an Error if permissions are not granted, null if permissions are granted
     */
    private Error checkAgencyAdvertiserAuthorization(DatasetConfig datasetConfig, OauthKey key, SqlSession session) {
        // Make sure the user has permission to get this dataset configuration.
        if (!userValidFor(AccessStatement.AGENCY, datasetConfig.agencyId(), key.getUserId(), session)) {
            return new Error("User is not authorized to work on the agency associated with this dataset configuration.", SecurityCode.PERMISSION_DENIED);
        }
        if (!userValidFor(AccessStatement.ADVERTISER, datasetConfig.advertiserId(), key.getUserId(), session)) {
            return new Error("User is not authorized to work on the advertiser associated with this dataset configuration.", SecurityCode.PERMISSION_DENIED);
        }
        return null;
    }
    
    /**
     * Get a dataset config from storage and convert it to a view that has additional information.
     *
     * @param id
     * @param key
     * @return
     */
    public Either<Error, DatasetConfigView> get(UUID id, OauthKey key) {
        if (id == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "dataset config ID"), ValidationCode.REQUIRED));
        }
        if (key == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "OAuth key"), ValidationCode.REQUIRED));
        }

        DatasetConfigView result;

        SqlSession session = advertiserDao.openSession();

        try {
            DatasetConfig datasetConfig = datasetConfigDao.getDataset(id);
            if(datasetConfig == null) {
                return Either.error(new Error(
                        ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "datasetId", id.toString(), key.getUserId()),
                        BusinessCode.NOT_FOUND));
            }
            Error authorizationError = checkAgencyAdvertiserAuthorization(datasetConfig, key, session);
            if (authorizationError != null) {
                return Either.error(authorizationError);
            }

            // Get additional data for the view of the config.
            Advertiser advertiser = advertiserDao.get(datasetConfig.advertiserId(), session);
            if(advertiser == null) {
                return Either.error(new Error(String.format("Advertiser %d does not exist for the Dataset Config", datasetConfig.advertiserId()), ValidationCode.INVALID));
            }
            result = new DatasetConfigView(datasetConfig);
            result.setAdvertiserName(advertiser.getName());
        } catch (Exception e) {
            String message = String.format("Unable to get the dataset %s", id.toString());
            log.warn(message, e);
            return Either.error(new Error(message, BusinessCode.INTERNAL_ERROR));
        } finally {
            advertiserDao.close(session);
        }

        return Either.success(result);
    }

    /**
     * Merge the data from an existing dataset config with new data from a view.
     *
     * @param existing
     * @param view
     * @return merged config.
     */
    protected DatasetConfig merge(DatasetConfig existing, DatasetConfigView view) {

        DatasetConfig converted = fromView(view);

        return new DatasetConfig(
                existing.id(),
                existing.agencyId(),
                existing.advertiserId(),
                converted.domain(),
                existing.path(),
                converted.fileNamePrefix(),
                converted.cookieExpirationDays(),
                existing.successNotificationTopics(),
                existing.failureNotificationTopics(),
                converted.ttlExpirationSeconds(),
                converted.cookiesToCapture(),
                converted.durableCookies(),
                converted.matchCookieName(),
                converted.alias(),
                converted.isActive(),
                existing.latestUpdate(),
                converted.contentChannels(),
                converted.failThroughDefaults()
        );
    }

    /**
     * Builds a {@code DatasetConfig} from a {@code DatasetConfigView}.
     * @param view The DTO object where to get the data from
     * @return a {@code DatasetConfig} built from the provided DTO
     */
    protected DatasetConfig fromView(DatasetConfigView view) {
        // Conversion to scala structures
        List<Cookie> cookies = new ArrayList<>();
        FailThroughDefaults failThroughDefaults = null;
        trueffect.truconnect.api.commons.model.dto.adm.FailThroughDefaults viewFailThroughDefaults = view.getFailThroughDefaults();
        if(viewFailThroughDefaults != null) {
            if( viewFailThroughDefaults.getDefaultCookieList() != null && !viewFailThroughDefaults.getDefaultCookieList().isEmpty()) {
                for(trueffect.truconnect.api.commons.model.dto.adm.Cookie cookie : viewFailThroughDefaults.getDefaultCookieList()) {
                    cookies.add(new Cookie(cookie.getName(), cookie.getValue()));
                }
            }
            failThroughEnum defaultType = FailThroughDefault.validateDefaultType(viewFailThroughDefaults.getDefaultType());
            failThroughDefaults = new FailThroughDefaults(
                    viewFailThroughDefaults.isEnabled(),
                    defaultType,
                    Option.apply(viewFailThroughDefaults.getDefaultKey()),
                    JavaConversions.asScalaBuffer(cookies).toSeq()
            );
        }



        Option<String> matchCookieName;
        if (StringUtils.isEmpty(view.getMatchCookieName())) {
            matchCookieName = Option.apply(null);
        } else {
            matchCookieName = Option.apply(view.getMatchCookieName());
        }

        CookieList<String> cookiesToCapture = null;
        if(view.getCookiesToCapture() != null) {
            cookiesToCapture = new CookieList<>(
                    view.getCookiesToCapture().isEnabled(),
                    JavaConversions.asScalaBuffer(view.getCookiesToCapture().getCookies()).toSeq());
        }

        CookieList<String> durableCookies = null;
        if(view.getCookiesToCapture() != null) {
            durableCookies = new CookieList<>(
                    view.getDurableCookies().isEnabled(),
                    JavaConversions.asScalaBuffer(view.getDurableCookies().getCookies()).toSeq());
        }

        List<String> contentChannels = view.getContentChannels();
        if(contentChannels == null || contentChannels.isEmpty()) {
            contentChannels = new ArrayList<>();
        }
        Set<String> contentChannelsSet = JavaConversions.asScalaBuffer(contentChannels).toSet();

        StringBuilder pathBuilder = new StringBuilder().append("s3://").
                append(ResourceUtil.get("adm.ftp.bucket", "unknown")).
                append("/").append(view.getFtpAccount()).
                append("/").append(view.getPath());

        List<String> notificationTopics = new ArrayList<>(1);
        notificationTopics.add(ResourceUtil.get("adm.default.notificationTopic", "unknown"));

        Option<String> alias;
        if (StringUtils.isEmpty(view.getAlias())) {
            alias = Option.apply(null);
        } else {
            alias = Option.apply(view.getAlias());
        }
        Option<DateTime> latestUpdate;
        if (view.getLatestUpdate() == null) {
            latestUpdate = Option.apply(null);
        } else {
            latestUpdate = Option.apply(new DateTime(view.getLatestUpdate()));
        }

        return new DatasetConfig(
                view.getDatasetId(),
                view.getAgencyId(),
                view.getAdvertiserId(),
                view.getDomain(),
                pathBuilder.toString(),
                view.getFileNamePrefix(),
                view.getCookieExpirationDays(),
                JavaConversions.asScalaBuffer(notificationTopics).toSeq(),
                JavaConversions.asScalaBuffer(notificationTopics).toSeq(),
                view.getTtlExpirationSeconds(),
                cookiesToCapture,
                durableCookies,
                matchCookieName,
                alias,
                view.isActive(),
                latestUpdate,
                contentChannelsSet,
                failThroughDefaults
        );
    }

    /**
     * Create a new dataset config based on the view supplied.
     *
     * @param view
     * @param key
     * @return
     */
    public Either<Error, DatasetConfigView> create(DatasetConfigView view, OauthKey key) {
        if (view == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "dataset config"), ValidationCode.REQUIRED));
        }
        if (view.getDatasetId() != null) {
            return Either.error(new Error("Dataset ID must be null for creation.", ValidationCode.INVALID));
        }
        if (key == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "OAuth key"), ValidationCode.REQUIRED));
        }

        SqlSession session = advertiserDao.openSession();
        view.setDatasetId(UUID.randomUUID());
        DatasetConfig config = fromView(view);

        try {
            Error authorizationError = checkAgencyAdvertiserAuthorization(config, key, session);
            if (authorizationError != null) {
                return Either.error(authorizationError);
            }

            Advertiser advertiser = advertiserDao.get(config.advertiserId(), session);
            if (advertiser == null) {
                return Either.error(new Error(String.format("Advertiser %d does not exist.", config.advertiserId()), ValidationCode.INVALID));
            }

            Either<Error, Void> result = datasetConfigDao.save(config);
            if (result.isError()) {
                return Either.error(result.error());
            }
        } finally {
            advertiserDao.close(session);
        }

        return Either.success(view);
    }

    /**
     * Update an existing dataset config based on the view provided.
     *
     * @param view
     * @param key
     * @return
     */
    public Either<Errors, DatasetConfigView> update(DatasetConfigView view, String uuid, OauthKey key) {
        if (view == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "dataset config"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OAuth key"));
        }
        // All model validations performed below
        BeanPropertyBindingResult bErrors = new BeanPropertyBindingResult(view,
                DatasetConfigView.class.getSimpleName());
        validator.validateOnUpdate(view, uuid, bErrors);
        Errors errors = new Errors();
        if (bErrors.hasErrors()) {
            errors.getErrors().addAll(ApiValidationUtils.parseToValidationError(bErrors));
            return Either.error(errors);
        }
        DatasetConfigView datasetConfigView;
        SqlSession session = advertiserDao.openSession();

        try {
            // Get the existing from the back-end
            DatasetConfig existing = datasetConfigDao.getDataset(view.getDatasetId());
            if (existing == null) {
                errors.addError(new Error(
                        ResourceBundleUtil.getString("global.error.doesNotExist", "Advertiser", view.getDatasetId()),
                        ValidationCode.INVALID));
                return Either.error(errors);
            }

            Error authorizationError = checkAgencyAdvertiserAuthorization(existing, key, session);
            if (authorizationError != null)
            {
                errors.addError(authorizationError);
                return Either.error(errors);
            }

            DatasetConfig merged = merge(existing, view);

            Either<Error, Void> result = datasetConfigDao.save(merged);
            if (result.isError()) {
                errors.addError(result.error());
                return Either.error(errors);
            }

            datasetConfigView = new DatasetConfigView(merged);
            datasetConfigView.setAdvertiserName(view.getAdvertiserName());
        }
        finally {
            advertiserDao.close(session);
        }

        return Either.success(datasetConfigView);
    }
    
    public Either<Error, DatasetMetrics> datasetMetrics(String id, String startDate, String endDate, OauthKey key) {
        if (id == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "dataset config ID"), ValidationCode.REQUIRED));
        }
        if (key == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("global.error.null", "Oauth Key"), ValidationCode.REQUIRED));
        }

        //Start Validations
        UUID datasetId;
        try {
            datasetId = UUID.fromString(id);
        } catch(IllegalArgumentException e) {
            return Either.error(new Error( ResourceBundleUtil.getString("global.error.invalid", "dataset config ID"), ValidationCode.INVALID));
        }

        DateTime localStartDate = null;
        DateTime localEndDate = null;
        if(startDate != null) {
            try {
                localStartDate = new DateTime(startDate);
            } catch(IllegalArgumentException e) {
                return Either.error(new Error( ResourceBundleUtil.getString("global.error.invalid", "start date"), ValidationCode.INVALID));
            }
        }
        if(endDate != null) {
            try {
                localEndDate = new DateTime(endDate);
            } catch(IllegalArgumentException e) {
                return Either.error(new Error( ResourceBundleUtil.getString("global.error.invalid", "end date"), ValidationCode.INVALID));
            }
        }

        if (localStartDate != null && localEndDate != null) {
            if(localStartDate.compareTo(localEndDate) > 0) {
                return Either.error(new Error(
                        ResourceBundleUtil.getString("global.error.dateAfter", localStartDate.toDate(), localEndDate.toDate()),
                        ValidationCode.INVALID));
            }
        }
        //End Validations

        SqlSession session = null;
        try {
            DatasetConfig datasetConfig = datasetConfigDao.getDataset(datasetId);

            session = advertiserDao.openSession();

            Error authorizationError = checkAgencyAdvertiserAuthorization(datasetConfig, key, session);
            if (authorizationError != null) {
                return Either.error(authorizationError);
            }
            com.trueffect.adm.data.DatasetMetrics data = startDate != null && endDate != null ?
                    admTransactionDao.datasetMetrics(datasetId, localStartDate, localEndDate):
                    admTransactionDao.datasetMetrics(datasetId);
            return Either.success(convertToDto(data));
        } catch (Exception e) {
            String message = String.format("Unable to get the dataset %s", datasetId.toString());
            log.warn(message, e);
            return Either.error(new Error(message, BusinessCode.INTERNAL_ERROR));
        } finally {
            advertiserDao.close(session);
        }
    }

    public static DatasetMetrics convertToDto(com.trueffect.adm.data.DatasetMetrics data) {
        List<DatedSummary> dates = new ArrayList<>(data.dates().length());
        for (com.trueffect.adm.data.DatedSummary d: JavaConversions.seqAsJavaList(data.dates())) {
            DatedSummary ds = new DatedSummary(dateFormatter.print(d.date()), d.engagements(), d.cached(), d.matched(), d.updated(), d.matchRate());
            dates.add(ds);
        }
        com.trueffect.adm.data.Summary dataSummary = data.summary();
        Summary summary = new Summary(dataSummary.engagements(), dataSummary.cached(), dataSummary.matched(), dataSummary.updated(), dataSummary.matchRate());
        return new DatasetMetrics(summary, dates);
    }
}
