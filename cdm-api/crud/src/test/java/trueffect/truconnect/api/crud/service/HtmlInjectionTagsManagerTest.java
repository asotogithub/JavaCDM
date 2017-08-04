package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.HtmlInjectionTagAssociation;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.delivery.TruQTagList;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.commons.model.dto.HtmlInjectionTagAssociationDTO;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam.PlacementAction;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.enums.PlacementFilterParamLevelTypeEnum;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.CustomInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CookieDomainDao;
import trueffect.truconnect.api.crud.dao.HtmlInjectionTagsDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.SiteSectionDao;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.factory.HtmlInjectionTagFactory;
import trueffect.truconnect.api.external.proxy.TruQProxy;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saulo Lopez
 */
public class HtmlInjectionTagsManagerTest extends AbstractManagerTest {

    private HtmlInjectionTagsManager htmlInjectionTagsManager;
    private HtmlInjectionTagsDao htmlInjectionTagsDao;
    private PlacementDao placementDao;
    private UserDao userDao;
    private HtmlInjectionTags htmlInjectionTag;
    private CampaignDao campaignDao;
    private Campaign campaign;
    private TruQProxy truQProxy;
    private TruQTagList truQTagList;

    @Before
    public void setUp() throws ProxyException {
        // Mock daos
        AgencyDao agencyDao = mock(AgencyDao.class);
        htmlInjectionTagsDao = mock(HtmlInjectionTagsDao.class);
        placementDao = mock(PlacementDao.class);
        SiteSectionDao sectionDao = mock(SiteSectionDao.class);
        CookieDomainDao cookieDomainDao = mock(CookieDomainDao.class);
        userDao = mock(UserDao.class);
        campaignDao = mock(CampaignDao.class);

        // Mock Utilities
        truQProxy = mock(TruQProxy.class);
        truQTagList = mock(TruQTagList.class);
        
        // Variables
        campaign = EntityFactory.createCampaign();
        htmlInjectionTag = EntityFactory.createHtmlInjectionTag();
        htmlInjectionTagsManager = new HtmlInjectionTagsManager(htmlInjectionTagsDao,
                placementDao, sectionDao, campaignDao, userDao, accessControlMockito, 
                truQProxy);

        // mock sessions
        when(htmlInjectionTagsDao.openSession()).thenReturn(sqlSessionMock);
        when(agencyDao.openSession()).thenReturn(sqlSessionMock);
        when(cookieDomainDao.openSession()).thenReturn(sqlSessionMock);

        // Mocks access control
        MockAccessControlUtil.allowAllForUser(accessControlMockito, sqlSessionMock);

        // Mocks truQProxy
        when(truQProxy.post(any(TruQTagList.class))).thenReturn(truQTagList);
    }
    /**
     * Tests for getting Html Injection
     */
    @Test
    public void gettingHtmlInjectionTagsShouldReturnSuccessful() {
        when(htmlInjectionTagsDao.getHtmlInjectionTagsForAgency(anyLong(), any(OauthKey.class),
                any(SqlSession.class))).thenReturn(createHtmlInjectionTags(2));

        Either<Error, RecordSet<HtmlInjectionTags>> result
                = htmlInjectionTagsManager.getHtmlInjectionTagsForAgency(1L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
        assertThat(result.success().getRecords(), hasSize(2));
    }

    @Test
    public void gettingHtmlInjectionTagsShouldReturnFail() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);

        Either<Error, RecordSet<HtmlInjectionTags>> result
                = htmlInjectionTagsManager.getHtmlInjectionTagsForAgency(1L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error();
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void gettingHtmlInjectionTagsShouldFailNullParam() {
        try {
            htmlInjectionTagsManager.getHtmlInjectionTagsForAgency(1L, null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "OAuth key"))));
        }
    }

    @Test
    public void gettingSingleHtmlInjectionTagsShouldReturnSuccessful() {

        when(htmlInjectionTagsDao.getHtmlInjectionById(anyLong(), any(SqlSession.class))).thenReturn(
                createHtmlInjectionTags(1).getRecords().get(0));

        Either<Error, HtmlInjectionTags> result
                = htmlInjectionTagsManager.getHtmlInjectionTag(1L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void gettingSingleHtmlInjectionTagsShouldReturnFail() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.HTML_INJECTION, sqlSessionMock);

        Either<Error, HtmlInjectionTags> result
                = htmlInjectionTagsManager.getHtmlInjectionTag(1L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error();
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void gettingSingleHtmlInjectionTagsShouldFailNullKey() {
        try {
            htmlInjectionTagsManager.getHtmlInjectionTag(1L, null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "OAuth key"))));
        }
    }

    @Test
    public void gettingSingleHtmlInjectionTagsShouldFailNullId() {
        try {
            htmlInjectionTagsManager.getHtmlInjectionTag(null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "HtmlInjectionTag Id"))));
        }
    }

    /**
     * Tests for Tag Association
     */
    @Test
    public void getTagAssociationsByFilterParamPass() {
        // prepare data
        int totalRecords = 5;
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // customize mock's behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.AGENCY,
                sqlSessionMock);
        when(placementDao.checkPlacementsBelongsCampaignId(anyList(),
                eq(filterParam.getCampaignId()), eq(sqlSessionMock))).thenReturn(Boolean.TRUE);
        when(placementDao.get(eq(filterParam.getPlacementId()), eq(sqlSessionMock)))
                .thenAnswer(getPlacementById(filterParam.getSiteId(), filterParam.getSectionId()));
        when(htmlInjectionTagsDao.getAssociations(eq(agencyId), eq(filterParam), anyLong(),
                anyLong(), anyLong(), anyLong(), eq(sqlSessionMock)))
                .thenAnswer(getTagAssociationsByPlacementFilterParam(totalRecords));
        when(htmlInjectionTagsDao.getCountAssociations(eq(agencyId), eq(filterParam), anyLong(),
                anyLong(), eq(sqlSessionMock))).thenReturn(Long.valueOf(totalRecords));

        // Perform test
        Either<Errors, HtmlInjectionTagAssociationDTO> result = htmlInjectionTagsManager
                .getAssociationsByPlacementFilterParam(agencyId, filterParam, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getDirectAssociations(), is(notNullValue()));
        assertThat(result.success().getInheritedAssociations(), is(notNullValue()));
        int total = result.success().getDirectAssociations().size()
                + result.success().getInheritedAssociations().size();
        assertThat(total, is(equalTo(totalRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(totalRecords)));
    }

    @Test
    public void getTagAssociationsByFilterParamWithNullParameterFail() {
        // prepare data
        Long agencyId = null;
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // Perform test
        try {
            htmlInjectionTagsManager.getAssociationsByPlacementFilterParam(
                    agencyId, filterParam, null, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Agency Id"))));
        }
    }

    @Test
    public void getTagAssociationsByFilterParamWithNullPayloadFail() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = null;

        // Perform test
        Either<Errors, HtmlInjectionTagAssociationDTO> result = htmlInjectionTagsManager
                .getAssociationsByPlacementFilterParam(agencyId, filterParam, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(ValidationError.class));
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(String.format("%s cannot be null.", "Filter Param")));
    }

    @Test
    public void getTagAssociationsByFilterParamWithInvalidPayloadFail() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);
        filterParam.setCampaignId(null);

        // Perform test
        Either<Errors, HtmlInjectionTagAssociationDTO> result = htmlInjectionTagsManager
                .getAssociationsByPlacementFilterParam(agencyId, filterParam, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(ValidationError.class));
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(),
                is(String.format("Invalid %s, it cannot be empty.", "campaignId")));
    }

    @Test
    public void getTagAssociationsByFilterParamWithWrongAccessForAgencyFail() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.AGENCY,
                sqlSessionMock);

        // Perform test
        Either<Errors, HtmlInjectionTagAssociationDTO> result = htmlInjectionTagsManager
                .getAssociationsByPlacementFilterParam(agencyId, filterParam, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void getTagAssociationsByFilterParamWithWrongAccessForFilterParamIdsFail() {
        // prepare data
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        PlacementFilterParam filterParam = EntityFactory
                .createPlacementFilterParam(PlacementFilterParamLevelTypeEnum.PLACEMENT);

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN,
                sqlSessionMock);

        // Perform test
        Either<Errors, HtmlInjectionTagAssociationDTO> result = htmlInjectionTagsManager
                .getAssociationsByPlacementFilterParam(agencyId, filterParam, null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        Error error = result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    /**
     * Tests for creating Html Injection
     */
    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailNullType() {
        try {
            htmlInjectionTagsManager.createTagFromType(1L, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(), is(equalTo(String.format("%s cannot be null.", "Html Injection Type"))));
        }
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailNullKey() {
        try {
            htmlInjectionTagsManager.createTagFromType(1L, null, null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(), is(equalTo(String.format("%s cannot be null.", "OAuth key"))));
        }
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailNullAgencyId() {
        try {
            htmlInjectionTagsManager.createTagFromType(null, null, null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(), is(equalTo(String.format("%s cannot be null.", "Agency Id"))));
        }
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailNullName() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setOptOutUrl("http://www.google.com");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%s cannot be null.", "name"))));
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailLongName() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setOptOutUrl("http://www.google.com");
        adChoicesHtmlInjectionType.setName("This is a very long name with more than 25 characters");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %1s, it supports characters up to %2s.", "name", 25))));
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldEmptyName() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setOptOutUrl("http://www.google.com");
        adChoicesHtmlInjectionType.setName("             ");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %1s, it cannot be empty.", "name"))));
    }
    
    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldInvalidName() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setOptOutUrl("http://www.google.com");
        adChoicesHtmlInjectionType.setName("*Invalid Name*");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%1s has an invalid format", "name"))));
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailNullOptOutUrl() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setName("Name");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%s cannot be null.", "optOutUrl"))));
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailInvalidOptOutUrl() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setName("Name");
        adChoicesHtmlInjectionType.setOptOutUrl("Not an URL");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%1s has an invalid format", "optOutUrl"))));
    }

    @Test
    public void creatingCustomTagsFromTypeShouldFailWithNoTagContentNeitherSecureTagcontent() {
        CustomInjectionType customInjectionType = new CustomInjectionType();
        customInjectionType.setName("Name");

        Either<Errors, HtmlInjectionTags> result =
                htmlInjectionTagsManager.createTagFromType(1L, customInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo("Html tag is empty. " +
                "Please add a valid value for htmlContent or secureHtmlContent.")));
    }

    @Test
    public void creatingHtmlInjectionTagsFromTypeShouldFailNullFirstPartyDomainId() {
        FacebookCustomTrackingInjectionType facebookCustomTrackingInjectionType = new FacebookCustomTrackingInjectionType();
        facebookCustomTrackingInjectionType.setName("Name");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, facebookCustomTrackingInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%s cannot be null.", "firstPartyDomainId"))));
    }

    @Test
    public void creatingHtmlInjectionTagsShouldFailDueAccessPermission() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.AGENCY,
                sqlSessionMock);

        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setOptOutUrl("http://www.google.com");
        adChoicesHtmlInjectionType.setName("Name");

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L, adChoicesHtmlInjectionType, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void creatingTagForFacebookShouldSucceed() throws Exception {
        FacebookCustomTrackingInjectionType facebookCustomTrackingInjectionType =
                new FacebookCustomTrackingInjectionType();
        facebookCustomTrackingInjectionType.setFirstPartyDomainId(1L);
        facebookCustomTrackingInjectionType.setName("Name");

        HtmlInjectionTags htmlInjectionAdChoicesTags =
                HtmlInjectionTagFactory.createHtmlInjectionTag(
                        facebookCustomTrackingInjectionType);

        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.COOKIE_DOMAIN, sqlSessionMock);
        when(htmlInjectionTagsDao.insertHtmlInjection(any(HtmlInjectionTags.class),
                any(SqlSession.class))).thenReturn(
                htmlInjectionAdChoicesTags);
        when(userDao.get(anyString(), anyString(), any(SqlSession.class))).thenReturn(
                EntityFactory.createUser());

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L,
                facebookCustomTrackingInjectionType, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void creatingTagForAdChoicesShouldSucceed() {
        AdChoicesHtmlInjectionType adChoicesHtmlInjectionType = new AdChoicesHtmlInjectionType();
        adChoicesHtmlInjectionType.setOptOutUrl("http://www.google.com");
        adChoicesHtmlInjectionType.setName("Name");

        HtmlInjectionTags htmlInjectionAdChoicesTags = HtmlInjectionTagFactory.createHtmlInjectionTag(adChoicesHtmlInjectionType);

        when(htmlInjectionTagsDao.insertHtmlInjection(any(HtmlInjectionTags.class),
                any(SqlSession.class))).thenReturn(htmlInjectionAdChoicesTags);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L,
                adChoicesHtmlInjectionType, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void creatingTagCustomShouldSucceed() {
        CustomInjectionType customInjectionType = new CustomInjectionType();
        customInjectionType.setName("Name");
        customInjectionType.setTagContent(
                String.format(Constants.HTML_INJECTION_ADCHOICES_HTML_CONTENT, "_blank"));
        customInjectionType.setSecureTagContent(
                String.format(Constants.HTML_INJECTION_ADCHOICES_SECURE_HTML_CONTENT, "_blank"));

        HtmlInjectionTags htmlInjectionTags =
                HtmlInjectionTagFactory.createHtmlInjectionTag(customInjectionType);

        when(htmlInjectionTagsDao.insertHtmlInjection(any(HtmlInjectionTags.class),
                any(SqlSession.class))).thenReturn(htmlInjectionTags);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L,
                customInjectionType, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void creatingTagCustomShouldSucceedEvenNotHavingTagContent() {
        CustomInjectionType customInjectionType = new CustomInjectionType();
        customInjectionType.setName("Name");
        customInjectionType.setSecureTagContent(
                String.format(Constants.HTML_INJECTION_ADCHOICES_SECURE_HTML_CONTENT, "_blank"));

        HtmlInjectionTags htmlInjectionTags =
                HtmlInjectionTagFactory.createHtmlInjectionTag(customInjectionType);

        when(htmlInjectionTagsDao.insertHtmlInjection(any(HtmlInjectionTags.class),
                any(SqlSession.class))).thenReturn(htmlInjectionTags);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L,
                customInjectionType, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void creatingTagCustomShouldSucceedEvenNotHavingSecureTagContent() {
        CustomInjectionType customInjectionType = new CustomInjectionType();
        customInjectionType.setName("Name");
        customInjectionType.setTagContent(
                String.format(Constants.HTML_INJECTION_ADCHOICES_HTML_CONTENT, "_blank"));

        HtmlInjectionTags htmlInjectionTags =
                HtmlInjectionTagFactory.createHtmlInjectionTag(customInjectionType);

        when(htmlInjectionTagsDao.insertHtmlInjection(any(HtmlInjectionTags.class),
                any(SqlSession.class))).thenReturn(htmlInjectionTags);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.createTagFromType(1L,
                customInjectionType, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
    }

    /**
     * Tests for updating HtmlInjectionTags
     */
    @Test
    public void updateHtmlInjectionTagShouldSucceed() throws ProxyException {

        // customize mock's behavior
        when(htmlInjectionTagsDao.updateHtmlInjection(any(HtmlInjectionTags.class),
                any(SqlSession.class))).thenReturn(
                htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.updateHtmlInjectionTag(
                htmlInjectionTag.getId(), htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.isSuccess(), is(true));
        verify(truQProxy, times(1)).post(any(TruQTagList.class));
    }

    @Test
    public void updateHtmlInjectionTagShouldFailNullKey() {
        try {
            htmlInjectionTagsManager
                    .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, null);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "OAuth key"))));
        }
    }

    @Test
    public void updateHtmlInjectionTagShouldFailNullObject() {
        try {
            htmlInjectionTagsManager.updateHtmlInjectionTag(1L, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "HtmlInjectionTag"))));
        }
    }

    @Test
    public void updateHtmlInjectionTagShouldFailDueAccessPermission() {
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.HTML_INJECTION, sqlSessionMock);

        Either<Errors, HtmlInjectionTags> result
                = htmlInjectionTagsManager.updateHtmlInjectionTag(htmlInjectionTag.getId(),
                htmlInjectionTag, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void updateTagShouldFaildDueNullId() {
        htmlInjectionTag.setId(null);
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(
                        htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result =
                htmlInjectionTagsManager.updateHtmlInjectionTag(1L, htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format(
                "%s should have the id field. If you are trying to create a new %s use POST service.",
                "HtmlInjectionTag", "HtmlInjectionTag"))));
    }

    @Test
    public void updateTagShouldFaildDueDifferentId() {
        htmlInjectionTag.setId(1L);
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(
                        htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result =
                htmlInjectionTagsManager.updateHtmlInjectionTag(2L, htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(
                trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode.class));
        assertThat(error.getCode().toString(),
                is(trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode.INVALID
                        .toString()));
        assertThat(error.getMessage(),
                is(equalTo("Id in payload does not match to the parameter id.")));
    }

    @Test
    public void updateTagShouldFaildDueNullName() {
        htmlInjectionTag.setName(null);
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(
                        htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager.updateHtmlInjectionTag(
                htmlInjectionTag.getId(), htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format("%s cannot be null.", "name"))));
    }

    @Test
    public void updateTagShouldFaildDueLongName() {
        htmlInjectionTag.setName("This is a very long name with more than 25 characters");
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %1s, it supports characters up to %2s.", "name", 25))));
    }

    @Test
    public void updateTagShouldFaildDueEmptyName() {
        htmlInjectionTag.setName("         ");
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(),
                is(equalTo(String.format("Invalid %1s, it cannot be empty.", "name"))));
    }

    @Test
    public void updateTagShouldFaildDueInvalidName() {
        htmlInjectionTag.setName("*Invalid Name*");
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(),
                is(equalTo(String.format("%1s has an invalid format", "name"))));
    }

    @Test
    public void updateTagShouldFaildDueBothContentNullOrEmpty() {
        htmlInjectionTag.setHtmlContent(null);
        htmlInjectionTag.setSecureHtmlContent("");
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(
                        htmlInjectionTag);

        Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                "Html tag is empty. Please add a valid value for htmlContent or secureHtmlContent.")));
    }

    @Test
    public void updateTagShouldFaildDueInvalidHtmlTagsContent() {
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(
                        htmlInjectionTag);
        String invalid[] = {
                "<a",
                "tag",
                "<IMG SRC=\"\"\"",
                "<?>",
                "< img >"
        };
        for (String s : invalid) {
            htmlInjectionTag.setHtmlContent(s);
            htmlInjectionTag.setSecureHtmlContent(null);

            Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                    .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, key);

            assertThat(result, is(notNullValue()));
            assertThat(result.toString(), result.success(), is(nullValue()));
            assertThat(result.error(), is(notNullValue()));
            Error error = result.error().getErrors().get(0);
            assertThat(error.getCode(), instanceOf(ValidationCode.class));
            assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
            assertThat(error.getMessage(),
                    is(equalTo("Could not find a valid HTML Tag. Please revise.")));
        }
    }

    @Test
    public void updateTagShouldSucceedWithValidHtmlTagsContent() {
        when(htmlInjectionTagsDao
                .updateHtmlInjection(any(HtmlInjectionTags.class), any(SqlSession.class)))
                .thenReturn(
                        htmlInjectionTag);
        String valid[] = {
                "<IMG SRC=\"http://ad.adlegend.com/ping?spacedesc=@@spacedesc@@&db_afcr=123&group=TEFP&event=FPILINK&x_fpid=@@PREFID@@&x_fpdomain=7384416\" WIDTH=1 HEIGHT=1 ALT=\" \" BORDER=0>",
                "<type=\"javascript\"></script>",
                "<IMG> <this",
                "<(?<endTag>/)?(?<tagname>\\w+)((\\s+(?<attName>\\w+)(\\s*=\\s*(?:\"(?<attVal>[^\"]*)\"|'([^']*)'|([^'\">\\s]+)))?)+\\s*|\\s*)(?<completeTag>/)?",
                // Taken from TA-3979
                "<a a='b'>text",
                "text<a a='1'>",
                "<mytag prop=\"'val'\">",
                "<a>",
                "</a>",
                "<tag>",
                "<tag1><tag2><tag3>",
                "</ <tag>>",
                "<><><tag><><>",
                "< a <tag> b >",
                "> a <tag> b <",
                "<ಠ益ಠ> <valid_tag> <⊙﹏⊙>",
                "<?>a word<img>"
        };
        for (String s : valid) {
            htmlInjectionTag.setHtmlContent(s);
            htmlInjectionTag.setSecureHtmlContent(null);
            Either<Errors, HtmlInjectionTags> result = htmlInjectionTagsManager
                    .updateHtmlInjectionTag(htmlInjectionTag.getId(), htmlInjectionTag, key);
            assertThat(result, is(notNullValue()));
            assertThat("Current tag: " + s, result.success(), is(notNullValue()));
            assertThat(result.error(), is(nullValue()));
        }
    }

    /**
     * Create Associations tests
     */
    @Test
    public void createPlacementAssociationsBulkPass() throws ProxyException {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());
        
        // customize mock's behavior
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(Long.valueOf(1L));
        when(placementDao.getCountCampaignSiteOfPlacementsByIds(anyList(), eq(sqlSessionMock)))
                .thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionPlacementOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        doNothing().when(htmlInjectionTagsDao).createTagAssociations(eq(campaign.getAgencyId()),
                eq(campaign.getAdvertiserId()), eq(campaign.getBrandId()),
                any(PlacementActionTagAssocParam.class), eq(key.getUserId()), eq(sqlSessionMock));

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.placementActionAssociationsBulk(
                campaign.getAgencyId(), campaign.getAdvertiserId(), campaign.getBrandId(), params,
                key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(),
                is("Bulk actions for HtmlInjectionTag Associations completed successfully."));
        verify(truQProxy, times(1)).post(any(TruQTagList.class));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueNullAgencyId() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());
        campaign.setAgencyId(null);

        // Perform test
        try {
            htmlInjectionTagsManager.placementActionAssociationsBulk(campaign.getAgencyId(),
                    campaign.getAdvertiserId(), campaign.getBrandId(), params, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Agency Id"))));
        }
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueNullAdvertiserId() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());
        campaign.setAdvertiserId(null);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Advertiser Id cannot be null."))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueWrongPayload() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());
        params.setLevelType(null);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid levelType, it cannot be empty."))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueWrongAccess() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.ADVERTISER, sqlSessionMock);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("The user is not allowed in this context or the requested entity does not exist"))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueInconsistentCampaignIds() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());

        // customize mock's behavior
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(0L);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("The campaignId has no relationship with the provided advertiserId and brandId."))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueInconsistentSiteId() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.SITE,
                        PlacementAction.C.toString().toLowerCase());

        // customize mock's behavior
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(1L);
        when(placementDao
                .getCountCampaignSiteOfPlacementsByIds(anyList(), eq(sqlSessionMock))).thenReturn(
                0L);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format(
                        "The provided data (siteId) is not related to the campaignId for levelType site."))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueInconsistentSectionId() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.SECTION,
                        PlacementAction.C.toString().toLowerCase());

        // customize mock's behavior
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(1L);
        when(placementDao.getCountCampaignSiteOfPlacementsByIds(anyList(), eq(sqlSessionMock)))
                .thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenReturn(0L);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format(
                        "The provided data (siteId, sectionId) is not related to the campaignId for levelType section."))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueInconsistentPlacementId() {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.PLACEMENT,
                        PlacementAction.C.toString().toLowerCase());

        // customize mock's behavior
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(1L);
        when(placementDao.getCountCampaignSiteOfPlacementsByIds(anyList(), eq(sqlSessionMock)))
                .thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionPlacementOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenReturn(0L);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format(
                        "The provided data (siteId, sectionId, placementId) is not related to the campaignId for levelType placement."))));
    }

    @Test
    public void createPlacementAssociationsBulkFailedDueDataBaseException() throws ProxyException {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.C.toString().toLowerCase());

        // customize mock's behavior
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(1L);
        when(placementDao.getCountCampaignSiteOfPlacementsByIds(anyList(), eq(sqlSessionMock)))
                .thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionPlacementOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        doThrow(new PersistenceException(
                "Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket."))
                .when(htmlInjectionTagsDao).createTagAssociations(eq(campaign.getAgencyId()),
                eq(campaign.getAdvertiserId()), eq(campaign.getBrandId()),
                any(PlacementActionTagAssocParam.class), eq(key.getTpws()), eq(sqlSessionMock));

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager
                .placementActionAssociationsBulk(campaign.getAgencyId(), campaign.getAdvertiserId(),
                        campaign.getBrandId(), params, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        BusinessError error = (BusinessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(BusinessCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.INTERNAL_ERROR.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format(
                "Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket."))));
    }

    @Test
    public void deletePlacementAssociationsBulkPass() throws ProxyException {
        // prepare data
        PlacementActionTagAssocParam params = EntityFactory
                .createPlacementActionTagAssocParam(PlacementFilterParamLevelTypeEnum.CAMPAIGN,
                        PlacementAction.D.toString().toLowerCase());

        // customize mock's behavio
        when(campaignDao.getCountCampaignsByAdvertiserAndBrandIds(eq(campaign.getAdvertiserId()),
                eq(campaign.getBrandId()), anyList(), eq(sqlSessionMock)))
                .thenReturn(1L);
        when(placementDao.getCountCampaignSiteOfPlacementsByIds(anyList(), eq(sqlSessionMock)))
                .thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        when(placementDao.getCountCampaignSiteSectionPlacementOfPlacementsByIds(anyList(),
                eq(sqlSessionMock))).thenAnswer(getCountIds());
        doNothing().when(htmlInjectionTagsDao).createTagAssociations(eq(campaign.getAgencyId()),
                eq(campaign.getAdvertiserId()), eq(campaign.getBrandId()),
                any(PlacementActionTagAssocParam.class), eq(key.getUserId()), eq(sqlSessionMock));

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.placementActionAssociationsBulk(
                campaign.getAgencyId(), campaign.getAdvertiserId(), campaign.getBrandId(), params,
                key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(),
                is("Bulk actions for HtmlInjectionTag Associations completed successfully."));
        verify(truQProxy, times(1)).post(any(TruQTagList.class));
    }

    /**
     * Bulk Delete Test
     */
    @Test
    public void deleteBulkTagsPass() throws ProxyException {
        // prepare data
        int totalRecords = 100;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        User user = EntityFactory.createUser();

        // customize mock's behavior
        when(userDao.get(eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(user);
        when(htmlInjectionTagsDao.bulkDeleteByIds(eq(user.getAgencyId()), anyList(),
                eq(key.getTpws()), eq(sqlSessionMock))).thenReturn(ids.size());

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.deleteBulk(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(),
                is("Bulk delete for HtmlInjectionTags completed successfully."));
        verify(truQProxy, times(1)).post(any(TruQTagList.class));
    }

    @Test
    public void deleteBulkTagsFailedDueNullPayload() {
        // prepare data
        RecordSet<Long> toDelete = null;

        // Perform test
        try {
            htmlInjectionTagsManager.deleteBulk(toDelete, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "BulkDelete"))));
        }
    }

    @Test
    public void deleteBulkTagsFailedDueEmptyPayload() {
        // prepare data
        int totalRecords = 100;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        ids.set(EntityFactory.random.nextInt(totalRecords), null);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.deleteBulk(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s, it cannot be empty.", "id"))));
    }

    @Test
    public void deleteBulkTagsFailedDueEmptyIdsOnPayload() {
        // prepare data
        List<Long> ids = null;
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.deleteBulk(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("%s cannot be null.", "bulkCreate"))));
    }

    @Test
    public void deleteBulkTagsFailedDueWrongAccess() {
        // prepare data
        int totalRecords = 10;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.HTML_INJECTION, sqlSessionMock);

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.deleteBulk(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format(
                        "The user is not allowed in this context or the requested entity does not exist"))));
    }

    @Test
    public void deleteBulkTagsFailedDueDataBaseException() {
        // prepare data
        int totalRecords = 10;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        User user = EntityFactory.createUser();

        // customize mock's behavior
        when(userDao.get(eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(user);
        doThrow(new PersistenceException(
                "Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket."))
                .when(htmlInjectionTagsDao)
                .bulkDeleteByIds(eq(user.getAgencyId()), anyList(), eq(key.getTpws()),
                        eq(sqlSessionMock));

        // Perform test
        Either<Errors, String> result = htmlInjectionTagsManager.deleteBulk(toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        BusinessError error = (BusinessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(BusinessCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.INTERNAL_ERROR.toString()));
        assertThat(error.getMessage(), is(equalTo(String.format(
                "Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket."))));
    }

    /**
     * Get Html Injection Tags Associated to a Placement Id
     */
    @Test
    public void getTagsByPlacementIdPass() {
        // prepare data
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));
        int totalRecords = 100;
        List<HtmlInjectionTags> list = new ArrayList<>();
        for (int i = 0; i < totalRecords; i++) {
            HtmlInjectionTags tag = EntityFactory.createHtmlInjectionTag();
            list.add(tag);
        }

        // customize mock's behavior
        //HtmlInjectionTags
        when(placementDao.get(eq(placement.getId()), eq(sqlSessionMock))).thenReturn(placement);
        when(htmlInjectionTagsDao.getHtmlInjectionTagsByPlacementId(eq(placement.getCampaignId()),
                eq(placement.getId()), anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(list);
        when(htmlInjectionTagsDao.getCountHtmlInjectionTagsByPlacementId(
                eq(placement.getCampaignId()), eq(placement.getId()), eq(sqlSessionMock)))
                .thenReturn(Long.valueOf(list.size()));

        // Perform test
        Either<Errors, RecordSet<HtmlInjectionTags>> result =
                htmlInjectionTagsManager.getTagsByPlacementId(placement.getId(), null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(totalRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(totalRecords)));
    }

    @Test
    public void getTagsByPlacementIdFailedDueNullPathParam() {
        // Perform test
        try {
            htmlInjectionTagsManager.getTagsByPlacementId(null, null, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(String.format("%s cannot be null.", "Placement Id")));
        }
    }

    @Test
    public void getTagsByPlacementIdFailedDueWrongAccessForPlacementId() {
        // prepare data
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.PLACEMENT,
                sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<HtmlInjectionTags>> result =
                htmlInjectionTagsManager.getTagsByPlacementId(placement.getId(), null, null, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error, instanceOf(AccessError.class));
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    /**
     * Bulk deletePlacementHtmlTagInjectionAssocBulk Test
     */
    @Test
    public void deletePlacementHtmlTagInjectionAssocBulkPass() throws ProxyException {
        // prepare data
        int totalRecords = 100;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));

        // customize mock's behavior
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(placement);
        when(campaignDao.get(anyLong(), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCampaign());
        when(htmlInjectionTagsDao.deleteTagAssociations(anyLong(), anyLong(), anyLong(),
                any(PlacementActionTagAssocParam.class), anyString(),
                eq(sqlSessionMock))).thenReturn(totalRecords);

        // Perform test
        Either<Errors, String> result =
                htmlInjectionTagsManager.deletePlacementHtmlTagInjectionAssocBulk(
                        placement.getId(), toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(),
                is("Bulk delete for HtmlInjectionTag Associations completed successfully."));
        verify(truQProxy, times(1)).post(any(TruQTagList.class));
    }

    @Test
    public void deletePlacementHtmlTagInjectionAssocBulkFailedDueNullPayload() {
        // prepare data
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));
        RecordSet<Long> toDelete = null;

        // Perform test
        try {
            htmlInjectionTagsManager
                    .deletePlacementHtmlTagInjectionAssocBulk(placement.getId(), toDelete, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e, is(notNullValue()));
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Tag Ids"))));
        }
    }

    @Test
    public void deletePlacementHtmlTagInjectionAssocBulkFailedDueEmptyPayload() {
        // prepare data
        List<Long> ids = new ArrayList<>();
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));

        // Perform test
        Either<Errors, String> result =
                htmlInjectionTagsManager.deletePlacementHtmlTagInjectionAssocBulk(
                        placement.getId(), toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s, it cannot be empty.", "tagIds List"))));
    }

    @Test
    public void deletePlacementHtmlTagInjectionAssocBulkFailedDueEmptyIdsOnPayload() {
        // prepare data
        List<Long> ids = new ArrayList<>();
        ids.add(null);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));

        // Perform test
        Either<Errors, String> result =
                htmlInjectionTagsManager.deletePlacementHtmlTagInjectionAssocBulk(
                        placement.getId(), toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        ValidationError error = (ValidationError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.REQUIRED.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %s, it cannot be empty.", "tagIds List"))));
    }

    @Test
    public void deletePlacementHtmlTagInjectionAssocBulkFailedDueWrongAccess() {
        // prepare data
        int totalRecords = 10;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));

        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PLACEMENT, sqlSessionMock);

        // Perform test
        Either<Errors, String> result =
                htmlInjectionTagsManager.deletePlacementHtmlTagInjectionAssocBulk(
                        placement.getId(), toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is(equalTo(
                "The user is not allowed in this context or the requested entity does not exist")));
    }

    @Test
    public void deletePlacementHtmlTagInjectionAssocBulkFailedDueDataBaseException() {
        // prepare data
        int totalRecords = 10;
        List<Long> ids = EntityFactory.getLongList(totalRecords);
        RecordSet<Long> toDelete = new RecordSet<>(ids);
        Placement placement = EntityFactory.createPlacement();
        placement.setId(Math.abs(EntityFactory.random.nextLong()));

        // customize mock's behavior
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).thenReturn(placement);
        when(campaignDao.get(anyLong(), eq(sqlSessionMock)))
                .thenReturn(EntityFactory.createCampaign());
        when(htmlInjectionTagsDao.deleteTagAssociations(anyLong(), anyLong(), anyLong(),
                any(PlacementActionTagAssocParam.class), anyString(),
                eq(sqlSessionMock))).thenReturn(totalRecords);
        doThrow(new PersistenceException("Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket.")).when(htmlInjectionTagsDao)
                .deleteTagAssociations(anyLong(), anyLong(), anyLong(),
                        any(PlacementActionTagAssocParam.class), anyString(),
                        eq(sqlSessionMock));

        // Perform test
        Either<Errors, String> result =
                htmlInjectionTagsManager.deletePlacementHtmlTagInjectionAssocBulk(
                        placement.getId(), toDelete, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        BusinessError error = (BusinessError) result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(BusinessCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.INTERNAL_ERROR.toString()));
        assertThat(error.getMessage(), is(equalTo(
                "Error querying database.  Cause: java.sql.SQLRecoverableException: No more data to read from socket.")));
    }

    /**
     * Utility methods
     */
    private Answer<List<HtmlInjectionTagAssociation>> getTagAssociationsByPlacementFilterParam(
            final int counter) {
        return new Answer<List<HtmlInjectionTagAssociation>>() {
            @Override
            public List<HtmlInjectionTagAssociation> answer(InvocationOnMock invocation) {
                Long agencyId = (Long) invocation.getArguments()[0];
                PlacementFilterParam filterParam
                        = (PlacementFilterParam) invocation.getArguments()[1];
                Long entityTypeId = (Long) invocation.getArguments()[2];
                Long entityId = (Long) invocation.getArguments()[3];

                List<HtmlInjectionTagAssociation> result = new ArrayList<>();
                HtmlInjectionTagAssociation assoc;
                for (int i = 0; i < counter; i++) {
                    assoc = EntityFactory.createHtmlInjectionTagAssociation();
                    assoc.setEntityType(entityTypeId);
                    assoc.setEntityId(entityId);
                    if ((i % 2) == 0) {
                        assoc.setIsInherited(Constants.ENABLED);
                    } else {
                        assoc.setIsInherited(Constants.DISABLED);
                    }
                    result.add(assoc);
                }
                return result;
            }
        };
    }

    private Answer<Placement> getPlacementById(final Long siteId, final Long sectionId) {
        return new Answer<Placement>() {
            @Override
            public Placement answer(InvocationOnMock invocation) {
                Long placementId = (Long) invocation.getArguments()[0];
                Placement result = EntityFactory.createPlacement();
                result.setId(placementId);
                result.setSiteId(siteId);
                result.setSiteSectionId(sectionId);
                return result;
            }
        };
    }

    private Answer<Long> getCountIds() {
        return new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) {
                List<String> ids = (List<String>) invocation.getArguments()[0];
                return (long) ids.size();
            }
        };
    }

    private RecordSet<HtmlInjectionTags> createHtmlInjectionTags(int quantity) {
        List<HtmlInjectionTags> resultAsList = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            HtmlInjectionTags tag = new HtmlInjectionTags();
            resultAsList.add(tag);
        }
        return new RecordSet<>(resultAsList);
    }

    private List<CookieDomainDTO> getDomains(int quantity) {
        List<CookieDomainDTO> resultAsList = new ArrayList<>();

        for (Long i = 1L; i <= quantity; i++) {
            CookieDomainDTO cookieDomainDTO = new CookieDomainDTO();
            cookieDomainDTO.setId(i);
            resultAsList.add(cookieDomainDTO);
        }
        return resultAsList;
    }
}
