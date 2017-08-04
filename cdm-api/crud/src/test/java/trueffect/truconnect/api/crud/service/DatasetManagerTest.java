package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trueffect.truconnect.api.crud.EntityFactory.createAdvertiser;
import static trueffect.truconnect.api.crud.EntityFactory.createDatasetConfig;
import static trueffect.truconnect.api.crud.EntityFactory.createDatasetConfigView;

import com.trueffect.adm.data.DatedSummary;
import com.trueffect.delivery.formats.adm.DatasetConfig;
import com.trueffect.delivery.formats.adm.FailThroughDefault;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.DatasetMetrics;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import scala.Option;
import scala.collection.Iterator;
import scala.collection.JavaConversions;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

public class DatasetManagerTest extends AbstractManagerTest {

    private AdvertiserDao advertiserDao;
    private DatasetConfigDao datasetConfigDao;

    private DatasetManager datasetManager;

    @Before
    public void init() {
        advertiserDao = mock(AdvertiserDao.class);
        datasetConfigDao = mock(DatasetConfigDao.class);
        AdmTransactionDao admTransactionDao = mock(AdmTransactionDao.class);

        when(advertiserDao.openSession()).thenReturn(sqlSessionMock);
        datasetManager = new DatasetManager(datasetConfigDao, advertiserDao, admTransactionDao, accessControlMockito);
    }

    @Test
    public void testGettingExistingDatasetConfig() throws Exception {
        Advertiser advertiser = createAdvertiser();
        DatasetConfig config = createDatasetConfig();
        when(accessControlMockito.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(advertiser);
        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(config);

        Either<Error, DatasetConfigView> viewEither = datasetManager.get(config.id(), key);

        assertThat(viewEither.isSuccess(), is(true));
        assertThat(viewEither.success().getDatasetId(), is(equalTo(config.id())));
    }

    @Test
    public void testGettingDatasetConfigWithoutAdvertiserPermission() throws Exception {
        Advertiser advertiser = createAdvertiser();
        DatasetConfig config = createDatasetConfig();
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.AGENCY, sqlSessionMock);
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.ADVERTISER, sqlSessionMock);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(advertiser);
        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(config);

        Either<Error, DatasetConfigView> viewEither = datasetManager.get(config.id(), key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
        assertThat(viewEither.error().getMessage(), is(equalTo("User is not authorized to work on the advertiser associated with this dataset configuration.")));
    }

    @Test
    public void testGettingDatasetConfigWithoutAgencyPermission() throws Exception {
        Advertiser advertiser = createAdvertiser();
        DatasetConfig config = createDatasetConfig();
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.AGENCY, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.ADVERTISER, sqlSessionMock);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(advertiser);
        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(config);

        Either<Error, DatasetConfigView> viewEither = datasetManager.get(config.id(), key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
        assertThat(viewEither.error().getMessage(), is(equalTo("User is not authorized to work on the agency associated with this dataset configuration.")));
    }

    @Test
    public void testGettingDatasetConfigWithoutMatchingAdvertiser() throws Exception {
        DatasetConfig config = createDatasetConfig();
        when(accessControlMockito.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(null);
        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(config);

        Either<Error, DatasetConfigView> viewEither = datasetManager.get(config.id(), key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
        assertThat(viewEither.error().getMessage(), is(equalTo(String.format("Advertiser %s does not exist for the Dataset Config", config.advertiserId()))));
    }

    @Test
    public void testMergingDatasetConfigAndViewWithAllFieldsPresent() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig config = createDatasetConfig();

        DatasetConfig merged = datasetManager.merge(config, view);

        // Non-modifiable fields
        assertThat(merged.id(), is(equalTo(config.id())));
        assertThat(merged.agencyId(), is(equalTo(config.agencyId())));
        assertThat(merged.advertiserId(), is(equalTo(config.advertiserId())));
        assertThat(merged.path(), is(equalTo(config.path())));
        assertThat(merged.successNotificationTopics(), is(equalTo(config.successNotificationTopics())));
        assertThat(merged.failureNotificationTopics(), is(equalTo(config.failureNotificationTopics())));
        assertThat(merged.latestUpdate(), is(equalTo(config.latestUpdate())));

        // Overridden fields
        assertThat(merged.domain(), is(equalTo(view.getDomain())));
        assertThat(merged.fileNamePrefix(), is(equalTo(view.getFileNamePrefix())));
        assertThat(merged.cookieExpirationDays(), is(equalTo(view.getCookieExpirationDays())));
        assertThat(merged.ttlExpirationSeconds(), is(equalTo(view.getTtlExpirationSeconds())));
        assertThat(
                merged.cookiesToCapture().cookies(),
                is(equalTo(JavaConversions.asScalaBuffer(view.getCookiesToCapture().getCookies()).toSeq())));
        assertThat(
                merged.durableCookies().cookies(),
                is(equalTo(JavaConversions.asScalaBuffer(view.getDurableCookies().getCookies()).toSeq())));
        assertThat(merged.matchCookieName(), is(equalTo(Option.apply(view.getMatchCookieName()))));
        assertThat(merged.alias(), is(equalTo(Option.apply(view.getAlias()))));
        assertThat(merged.isActive(), is(equalTo(view.isActive())));
        assertThat(JavaConversions.seqAsJavaList(merged.contentChannels().toSeq()),
                        is(equalTo(view.getContentChannels())));

        assertThat(merged.failThroughDefaults(), is(notNullValue()));
        assertThat(merged.failThroughDefaults().defaultType(),
                is(equalTo(FailThroughDefault.validateDefaultType(view.getFailThroughDefaults().getDefaultType()))));
        assertThat(merged.failThroughDefaults().defaultKey().get(),
                is(equalTo(view.getFailThroughDefaults().getDefaultKey())));
        assertThat(merged.failThroughDefaults().defaultCookieList(),
                is(notNullValue()));
        assertThat(merged.failThroughDefaults().defaultCookieList().size(),
                is(equalTo(view.getFailThroughDefaults().getDefaultCookieList().size())));
        int i = 0;
        Iterator<com.trueffect.delivery.formats.adm.Cookie> iterator = merged.failThroughDefaults().defaultCookieList().iterator();
        while(iterator.hasNext()) {
            com.trueffect.delivery.formats.adm.Cookie cookie = iterator.next();
            trueffect.truconnect.api.commons.model.dto.adm.Cookie viewCookie = view.getFailThroughDefaults().getDefaultCookieList().get(i);
            assertThat(cookie.name(),
                    is(equalTo(viewCookie.getName())));
            assertThat(cookie.value(),
                    is(equalTo(viewCookie.getValue())));
            i++;
        }
    }

    @Test
    public void testMergingDatasetConfigAndViewWithOptionalFieldsBeingNull() {
        DatasetConfigView view = createDatasetConfigView();
        view.setCookiesToCapture(null);
        view.setDurableCookies(null);
        view.setMatchCookieName(null);
        view.setFileNamePrefix(null);
        view.setAlias(null);
        view.setLatestUpdate(null);
        view.setContentChannels(null);
        view.setFailThroughDefaults(null);
        DatasetConfig config = createDatasetConfig();

        DatasetConfig merged = datasetManager.merge(config, view);

        // Non-modifiable fields
        assertThat(merged.advertiserId(), is(equalTo(config.advertiserId())));
        assertThat(merged.agencyId(), is(equalTo(config.agencyId())));
        assertThat(merged.id(), is(equalTo(config.id())));
        assertThat(merged.path(), is(equalTo(config.path())));
        assertThat(merged.successNotificationTopics(), is(equalTo(config.successNotificationTopics())));
        assertThat(merged.failureNotificationTopics(), is(equalTo(config.failureNotificationTopics())));

        // Overridden fields
        assertThat(merged.domain(), is(equalTo(view.getDomain())));
        assertThat(merged.fileNamePrefix(), is(equalTo(view.getFileNamePrefix())));
        assertThat(merged.cookieExpirationDays(), is(equalTo(view.getCookieExpirationDays())));
        assertThat(merged.ttlExpirationSeconds(), is(equalTo(view.getTtlExpirationSeconds())));

        assertThat(merged.cookiesToCapture(), is(nullValue()));
        assertThat(merged.durableCookies(), is(nullValue()));
        assertThat(merged.matchCookieName(), is(Option.<String>apply(null)));
        assertThat(merged.alias(), is(Option.<String>apply(null)));
        assertThat(merged.isActive(), is(equalTo(view.isActive())));
        assertThat(JavaConversions.seqAsJavaList(merged.contentChannels().toSeq()).size(),
                is(equalTo(0)));

        assertThat(merged.failThroughDefaults(), is(nullValue()));
    }

    @Test
    public void testGettingConfigFromAViewWithAllValues() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig datasetConfig = datasetManager.fromView(view);

        // Non-modifiable fields
        assertThat(datasetConfig.id(), is(equalTo(view.getDatasetId())));
        assertThat(datasetConfig.agencyId(), is(equalTo(view.getAgencyId())));
        assertThat(datasetConfig.advertiserId(), is(equalTo(view.getAdvertiserId())));
        assertThat(datasetConfig.path(), startsWith("s3://"));
        assertThat(datasetConfig.path(), containsString(view.getFtpAccount()));
        assertThat(datasetConfig.path(), containsString(view.getPath()));
        assertThat(datasetConfig.successNotificationTopics().size(), is(equalTo(1)));
        assertThat(datasetConfig.failureNotificationTopics().size(), is(equalTo(1)));
        assertThat(datasetConfig.latestUpdate().get().toDate(), is(equalTo(view.getLatestUpdate())));

        // Overridden fields
        assertThat(datasetConfig.domain(), is(equalTo(view.getDomain())));
        assertThat(datasetConfig.fileNamePrefix(), is(equalTo(view.getFileNamePrefix())));
        assertThat(datasetConfig.cookieExpirationDays(), is(equalTo(view.getCookieExpirationDays())));
        assertThat(datasetConfig.ttlExpirationSeconds(), is(equalTo(view.getTtlExpirationSeconds())));
        assertThat(
                datasetConfig.cookiesToCapture().cookies(),
                is(equalTo(JavaConversions.asScalaBuffer(view.getCookiesToCapture().getCookies()).toSeq())));
        assertThat(
                datasetConfig.durableCookies().cookies(),
                is(equalTo(JavaConversions.asScalaBuffer(view.getDurableCookies().getCookies()).toSeq())));
        assertThat(datasetConfig.matchCookieName(), is(equalTo(Option.apply(view.getMatchCookieName()))));
        assertThat(datasetConfig.alias(), is(equalTo(Option.apply(view.getAlias()))));
        assertThat(datasetConfig.isActive(), is(equalTo(view.isActive())));
        assertThat(JavaConversions.seqAsJavaList(datasetConfig.contentChannels().toSeq()),
                is(equalTo(view.getContentChannels())));

        assertThat(datasetConfig.failThroughDefaults(), is(notNullValue()));
        assertThat(datasetConfig.failThroughDefaults().defaultType(),
                is(equalTo(FailThroughDefault.validateDefaultType(view.getFailThroughDefaults().getDefaultType()))));
        assertThat(datasetConfig.failThroughDefaults().defaultKey().get(),
                is(equalTo(view.getFailThroughDefaults().getDefaultKey())));
        assertThat(datasetConfig.failThroughDefaults().defaultCookieList(),
                is(notNullValue()));
        assertThat(datasetConfig.failThroughDefaults().defaultCookieList().size(),
                is(equalTo(view.getFailThroughDefaults().getDefaultCookieList().size())));
        int i = 0;
        Iterator<com.trueffect.delivery.formats.adm.Cookie> iterator = datasetConfig.failThroughDefaults().defaultCookieList().iterator();
        while(iterator.hasNext()) {
            com.trueffect.delivery.formats.adm.Cookie cookie = iterator.next();
            trueffect.truconnect.api.commons.model.dto.adm.Cookie viewCookie = view.getFailThroughDefaults().getDefaultCookieList().get(i);
            assertThat(cookie.name(),
                    is(equalTo(viewCookie.getName())));
            assertThat(cookie.value(),
                    is(equalTo(viewCookie.getValue())));
            i++;
        }
    }

    @Test
    public void testGettingConfigFromAViewWithNoOptionalValues() {
        DatasetConfigView view = createDatasetConfigView();
        view.setFailThroughDefaults(null);
        view.setCookiesToCapture(null);
        view.setDurableCookies(null);
        view.setMatchCookieName(null);
        view.setContentChannels(null);
        view.setFileNamePrefix(null);
        DatasetConfig datasetConfig = datasetManager.fromView(view);

        assertThat(datasetConfig.id(), is(equalTo(view.getDatasetId())));
        assertThat(datasetConfig.agencyId(), is(equalTo(view.getAgencyId())));
        assertThat(datasetConfig.advertiserId(), is(equalTo(view.getAdvertiserId())));
        assertThat(datasetConfig.domain(), is(equalTo(view.getDomain())));
        assertThat(datasetConfig.path(), startsWith("s3://"));
        assertThat(datasetConfig.path(), containsString(view.getFtpAccount()));
        assertThat(datasetConfig.path(), containsString(view.getPath()));
        assertThat(datasetConfig.fileNamePrefix(), is(nullValue()));
        assertThat(datasetConfig.cookieExpirationDays(), is(equalTo(view.getCookieExpirationDays())));
        assertThat(datasetConfig.successNotificationTopics().size(), is(equalTo(1)));
        assertThat(datasetConfig.failureNotificationTopics().size(), is(equalTo(1)));
        assertThat(datasetConfig.ttlExpirationSeconds(), is(equalTo(view.getTtlExpirationSeconds())));
        assertThat(datasetConfig.cookiesToCapture(), is(nullValue()));
        assertThat(datasetConfig.durableCookies(), is(nullValue()));
        assertThat(datasetConfig.matchCookieName().isEmpty(), is(true));
        assertThat(datasetConfig.failThroughDefaults(), is(nullValue()));
        assertThat(datasetConfig.contentChannels(), is(not(nullValue())));
        assertThat(datasetConfig.contentChannels().size(), is(0));
    }

    @Test
    public void testCreatingDatasetConfigWhenIdIsSet() {
        DatasetConfigView view = createDatasetConfigView();
        view.setDatasetId(UUID.randomUUID());
        Either<Error, DatasetConfigView> viewEither = datasetManager.create(view, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
    }

    @Test
    public void testCreatingDatasetConfig() throws Exception {
        DatasetConfigView view = createDatasetConfigView();
        view.setDatasetId(null);

        when(accessControlMockito.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(createAdvertiser());
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(Either.<Error, Void>success(null));

        Either<Error, DatasetConfigView> viewEither = datasetManager.create(view, key);

        assertThat(viewEither.isSuccess(), is(true));
        assertThat(viewEither.success().getDatasetId(), is(notNullValue()));
    }

    @Test
    public void testCreatingDatasetConfigWithInvalidAdvertiserId() throws Exception {
        DatasetConfigView view = createDatasetConfigView();
        view.setDatasetId(null);

        when(accessControlMockito.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(null);
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(null);

        Either<Error, DatasetConfigView> viewEither = datasetManager.create(view, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
    }

    @Test
    public void testCreatingDatasetConfigWithoutAgencyPermission() throws Exception {
        DatasetConfigView view = createDatasetConfigView();
        view.setDatasetId(null);

        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(false);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(createAdvertiser());
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(null);

        Either<Error, DatasetConfigView> viewEither = datasetManager.create(view, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
    }

    @Test
    public void testCreatingDatasetConfigWithoutAdvertiserPermission() throws Exception {
        DatasetConfigView view = createDatasetConfigView();
        view.setDatasetId(null);

        when(accessControlMockito.isUserValidFor(eq(AccessStatement.ADVERTISER), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(false);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.AGENCY), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(createAdvertiser());
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(null);

        Either<Error, DatasetConfigView> viewEither = datasetManager.create(view, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
    }

    @Test
    public void testCreatingDatasetConfigFailsToSave() throws Exception {
        DatasetConfigView view = createDatasetConfigView();
        view.setDatasetId(null);

        when(accessControlMockito.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(advertiserDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(createAdvertiser());
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(Either.<Error, Void>error(new Error("Internal ADM Error", BusinessCode.EXTERNAL_COMPONENT_ERROR)));

        Either<Error, DatasetConfigView> viewEither = datasetManager.create(view, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(BusinessCode.EXTERNAL_COMPONENT_ERROR.getNumber())));
        assertThat(viewEither.error().getMessage(), is(equalTo("Internal ADM Error")));
    }

    @Test
    public void testUpdatingDatasetConfig() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig existing = createDatasetConfig(view.getAgencyId());

        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);

        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(existing);
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(Either.<Error, Void>success(null));

        Either<Errors, DatasetConfigView> viewEither = datasetManager.update(view, view.getDatasetId().toString(), key);

        assertThat(viewEither.isSuccess(), is(true));
        assertThat(viewEither.success(), is(notNullValue()));
    }

    @Test
    public void testUpdatingDatasetConfigWithInvalidId() {
        DatasetConfigView view = createDatasetConfigView();

        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);

        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(null);
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(null);

        Either<Errors, DatasetConfigView> viewEither = datasetManager.update(view, view.getDatasetId().toString(), key);

        assertThat(viewEither.isError(), is(true));
        Error error = viewEither.error().getErrors().iterator().next();
        assertThat(error.getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
        assertThat(error.getMessage(), is(equalTo(String.format("Advertiser with id %s does not exist", view.getDatasetId()))));
    }

    @Test
    public void testUpdatingDatasetConfigWithoutAgencyPermission() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig existing = createDatasetConfig(view.getAgencyId());

        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.AGENCY, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.ADVERTISER, sqlSessionMock);

        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(existing);
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(null);

        Either<Errors, DatasetConfigView> viewEither = datasetManager.update(view, view.getDatasetId().toString(), key);

        assertThat(viewEither.isError(), is(true));
        Error error = viewEither.error().getErrors().iterator().next();
        assertThat(error.getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
        assertThat(error.getMessage(), is(equalTo("User is not authorized to work on the agency associated with this dataset configuration.")));
    }

    @Test
    public void testUpdatingDatasetConfigWithoutAdvertiserPermission() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig existing = createDatasetConfig(view.getAgencyId());

        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.AGENCY, sqlSessionMock);
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.ADVERTISER, sqlSessionMock);

        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(existing);
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(null);

        Either<Errors, DatasetConfigView> viewEither = datasetManager.update(view, view.getDatasetId().toString(), key);

        assertThat(viewEither.isError(), is(true));
        Error error = viewEither.error().getErrors().iterator().next();
        assertThat(error.getCode().getNumber(), is(equalTo(SecurityCode.PERMISSION_DENIED.getNumber())));
        assertThat(error.getMessage(), is(equalTo("User is not authorized to work on the advertiser associated with this dataset configuration.")));
    }

    @Test
    public void testUpdatingDatasetConfigFailsToSave() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig existing = createDatasetConfig(view.getAgencyId());

        when(accessControlMockito.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(sqlSessionMock))).thenReturn(true);
        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(existing);
        when(datasetConfigDao.save(any(DatasetConfig.class))).thenReturn(Either.<Error, Void>error(new Error("Internal ADM Error", BusinessCode.EXTERNAL_COMPONENT_ERROR)));

        Either<Errors, DatasetConfigView> viewEither = datasetManager.update(view, view.getDatasetId().toString(), key);

        assertThat(viewEither.isError(), is(true));
        Error error = viewEither.error().getErrors().iterator().next();
        assertThat(error.getCode().getNumber(), is(equalTo(BusinessCode.EXTERNAL_COMPONENT_ERROR.getNumber())));
        assertThat(error.getMessage(), is(equalTo("Internal ADM Error")));
    }

    @Test
    public void testUpdatingDatasetConfigWithMismatchIds() {
        DatasetConfigView view = createDatasetConfigView();
        DatasetConfig existing = createDatasetConfig(view.getAgencyId());

        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);
        when(datasetConfigDao.getDataset(any(UUID.class))).thenReturn(existing);

        Either<Errors, DatasetConfigView> viewEither = datasetManager.update(view, UUID.randomUUID().toString(), key);

        assertThat(viewEither.isError(), is(true));
        Error error = viewEither.error().getErrors().iterator().next();
        assertThat(error.getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
        assertThat(error.getMessage(), is(equalTo("The entity dataset ID is not the same UUID in the path")));
    }

    @Test
    public void testDatasetMetricsWithNullId() {
        Either<Error, DatasetMetrics> viewEither = datasetManager.datasetMetrics(null, null, null, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getMessage(), is(equalTo("dataset config ID cannot be null.")));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.REQUIRED.getNumber())));
    }

    @Test
    public void testDatasetMetricsWithInvalidId() {
        Either<Error, DatasetMetrics> viewEither = datasetManager.datasetMetrics("abcdef", null, null, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getMessage(), is(equalTo("Invalid dataset config ID value.")));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
    }

    @Test
    public void testDatasetMetricsWithInvalidStartDate() {
        Either<Error, DatasetMetrics> viewEither = datasetManager.datasetMetrics(UUID.randomUUID().toString(), "abc123", null, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getMessage(), is(equalTo("Invalid start date value.")));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
    }

    @Test
    public void testDatasetMetricsWithInvalidEndDate() {
        Either<Error, DatasetMetrics> viewEither = datasetManager.datasetMetrics(UUID.randomUUID().toString(), null, "abc123", key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getMessage(), is(equalTo("Invalid end date value.")));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
    }

    @Test
    public void testDatasetMetricsWithInvalidDateRange() {
        String startDate = "2015-03-04T00:00:00";
        String endDate = "2015-01-02T23:59:59";

        Either<Error, DatasetMetrics> viewEither = datasetManager.datasetMetrics(UUID.randomUUID().toString(), startDate, endDate, key);

        assertThat(viewEither.isError(), is(true));
        assertThat(viewEither.error().getMessage(), is(equalTo("Start Date should be before End Date. Start Date: '03/04/2015', End Date: '01/02/2015'")));
        assertThat(viewEither.error().getCode().getNumber(), is(equalTo(ValidationCode.INVALID.getNumber())));
    }

    private final DateTimeZone timeZoneMst = DateTimeZone.forOffsetHours(-7);

    @Test
    public void testConvertToDto() throws Exception {
        ArrayList<DatedSummary> dsList = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        dsList.add(new com.trueffect.adm.data.DatedSummary(new DateTime(2015, 1, 2, 0, 0, timeZoneMst), 1, 2, 3, 4, 1.5, uuid));
        dsList.add(new com.trueffect.adm.data.DatedSummary(new DateTime(2015, 1, 3, 0, 0, timeZoneMst), 2, 3, 4, 5, 1.5, uuid));
        scala.collection.immutable.Seq<DatedSummary> dates = JavaConversions.asScalaBuffer(dsList).toList();
        com.trueffect.adm.data.Summary summary = new com.trueffect.adm.data.Summary(5,6,7,8,3.3);
        com.trueffect.adm.data.DatasetMetrics data = new com.trueffect.adm.data.DatasetMetrics(summary, dates);

        trueffect.truconnect.api.commons.model.dto.DatasetMetrics dto = DatasetManager.convertToDto(data);

        assertThat(dto.getSummary().getEngagements(), is(equalTo(5L)));
        assertThat(dto.getDates().size(), is(equalTo(2)));
        assertThat(dto.getDates().get(0).getDate(), is(equalTo("2015-01-02T00:00:00-07:00")));
        assertThat(dto.getDates().get(1).getUpdated(), is(equalTo(5L)));
    }
}
