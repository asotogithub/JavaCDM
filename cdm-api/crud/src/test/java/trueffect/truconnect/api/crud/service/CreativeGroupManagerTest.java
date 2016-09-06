package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static trueffect.truconnect.api.crud.EntityFactory.random;

import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ErrorCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Creative Group Manager Tests
 *
 * @author Marleny Patsi, Marcelo Heredia
 */
public class CreativeGroupManagerTest extends AbstractManagerTest {

    //Test Mockito variables
    private CreativeGroupDao creativeGroupDao;
    private CreativeGroupCreativeDao creativeGroupCreativeDao;
    private CreativeInsertionDao creativeInsertionDao;
    private CreativeDao creativeDao;
    private CampaignDao campaignDao;
    private UserDao userDao;
    private CreativeGroupManager creativeGroupManager;
    private CreativeGroupManager.UtilityWrapper utilityWrapperCGM;
    private CreativeManager.UtilityWrapper utilityWrapperCM;
    private ExtendedPropertiesDao extendedPropertiesDao;

    private CreativeGroupCreativeDTO groupCreatives;
    private Campaign campaign;
    private CreativeGroup creativeGroup;
    private CreativeGroup group;
    private Creative creativeExistent;
    private List<Creative> newCreatives;
    private Creative creative;
    private String path;
    private String testPath;
    private Map<Long, CreativeGroup> existingGroupsMap;

    @Before
    public void init() throws Exception {

        //mock objects
        creativeGroupDao = mock(CreativeGroupDao.class);
        creativeGroupCreativeDao = mock(CreativeGroupCreativeDao.class);
        creativeInsertionDao = mock(CreativeInsertionDao.class);
        creativeDao = mock(CreativeDao.class);
        campaignDao = mock(CampaignDao.class);
        userDao = mock(UserDao.class);
        utilityWrapperCGM = mock(CreativeGroupManager.UtilityWrapper.class);
        utilityWrapperCM = mock(CreativeManager.UtilityWrapper.class);
        extendedPropertiesDao = mock(ExtendedPropertiesDao.class);

        //manager Mockito
        creativeGroupManager = new CreativeGroupManager(creativeGroupDao,
                creativeGroupCreativeDao, creativeInsertionDao, creativeDao, campaignDao, userDao,
                extendedPropertiesDao, accessControlMockito, utilityWrapperCGM, utilityWrapperCM);

        //prepare data
        groupCreatives = EntityFactory.createCreativeGroupCreativeDTO();
        campaign = EntityFactory.createCampaign();
        campaign.setId(groupCreatives.getCampaignId());
        creativeGroup = EntityFactory.createCreativeGroup();
        creativeExistent = groupCreatives.getCreatives().get(0);
        existingGroupsMap = new HashMap<>();

        group = new CreativeGroup();
        group.setCampaignId(campaign.getId());

        creative = new Creative();
        creative.setFilename(creativeExistent.getFilename());
        creative.setCampaignId(groupCreatives.getCampaignId());
        creative.setAgencyId(random.nextLong());
        creative.setCreativeVersion(1L);

        newCreatives = new ArrayList<>();
        newCreatives.add(creative);
        //set only one creative
        groupCreatives.setCreatives(newCreatives);

        //mock behaviors - session
        when(creativeGroupCreativeDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeGroupCreativeDao).commit(any(SqlSession.class));
        doNothing().when(creativeGroupCreativeDao).close(any(SqlSession.class));
        when(creativeGroupDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeGroupDao).commit(any(SqlSession.class));
        doNothing().when(creativeGroupDao).close(any(SqlSession.class));

        //mock behaviors - DAC
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.AGENCY, sqlSessionMock);
    }

    @After
    public void removeFiles() {
        //remove files temp
        if (path != null && !path.isEmpty()) {
            deleteAllFiles(path);
        }
        //remove files finalCreativePath
        if (testPath != null && !testPath.isEmpty()) {
            deleteAllFiles(testPath);
        }
    }

    @Ignore
    @Test
    public void test() {
        // set data
        Long campaingId = 1L;
        List<Creative> existing = new ArrayList<>();
        Creative c = new Creative();
        c.setId(10L);
        c.setFilename("test.jpg");
        existing.add(c);
        c = new Creative();
        c.setId(20L);
        c.setFilename("test2.jpg");
        existing.add(c);
        
        List<Creative> toProcess = new ArrayList<>();
        Creative c2 = new Creative();
        c2.setFilename("test.jpg");
        c2.setAlias("testAlias");
        toProcess.add(c2);
        c2 = new Creative();
        c2.setFilename("test2.jpg");
        c2.setAlias("testAlias2");
        toProcess.add(c2);
        
        when(creativeDao.getDupVersionAliasByCampaignAndCreativeIdAlias(eq(campaingId),
                anyList(), any(SqlSession.class))).thenReturn(toProcess);
        
        // perform test
        Either<Errors, Void> result = creativeGroupManager.aliasValidations(campaingId, existing, toProcess, sqlSessionMock);
        
        assertThat(result.error(), is(notNullValue()));
    }

    @Test
    public void testSaveCreativeGroup() throws Exception {
        // prepare data
        creativeGroup.setId(null);
        creativeGroup.setExternalId(null);

        //customize mock's behavior
        when(creativeGroupDao.creativeGroupExists(any(CreativeGroup.class), any(SqlSession.class))).
                thenReturn(false);
        when(creativeGroupDao.save(any(CreativeGroup.class), eq(key), any(SqlSession.class))).
                thenAnswer(createGroup());

        // perform test
        CreativeGroup result = creativeGroupManager.save(creativeGroup, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void testDuplicatedCreativeGroup() throws Exception {
        // prepare data
        creativeGroup.setId(null);
        creativeGroup.setExternalId(null);

        //customize mock's behavior
        when(creativeGroupDao.creativeGroupExists(any(CreativeGroup.class), any(SqlSession.class))).
                thenReturn(true);

        // perform test
        try {
            creativeGroupManager.save(creativeGroup, key);
            fail("This test should throw a ConflictException");
        } catch (ConflictException e) {
            assertThat(e.getMessage(), is("Creative Group name already exists."));
        }
    }

    @Test
    public void testGetMultipleCreativeGroups() throws Exception {
        // prepare data
        int numberOfRecords = 10;
        String criteria = "id in [";

        for (int i = 0; i < numberOfRecords; i++) {
            creativeGroup = EntityFactory.createCreativeGroup();
            creativeGroup.setId(Math.abs(EntityFactory.random.nextLong()));
            existingGroupsMap.put(creativeGroup.getId(), group);
            if (i % 3 == 0) {
                criteria += creativeGroup.getId() + ",";
            }
        }
        criteria = criteria.substring(0, criteria.length() - 1) + "]";
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setQuery(criteria);

        //customize mock's behavior
        when(creativeGroupDao.get(any(SearchCriteria.class), eq(key), any(SqlSession.class))).
                thenAnswer(getGroupsByCriteria(existingGroupsMap));

        // perform test
        RecordSet<CreativeGroup> result = creativeGroupManager.get(searchCriteria, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getRecords().size(), greaterThan(0));
    }

    @Test
    public void testUpdateCreativeGroup() throws Exception {
        // prepare data
        creativeGroup = EntityFactory.createCreativeGroup();
        creativeGroup.setId(Math.abs(EntityFactory.random.nextLong()));
        creativeGroup.setExternalId(null);
        CreativeGroup existentGroup = new CreativeGroup();
        existentGroup.setId(creativeGroup.getId());
        existentGroup.setName(creativeGroup.getName());
        creativeGroup.setName(creativeGroup.getName() + "Updated");

        //customize mock's behavior
        when(creativeGroupDao.get(anyLong(), eq(key), any(SqlSession.class))).thenReturn(existentGroup);
        when(creativeGroupDao.creativeGroupExists(any(CreativeGroup.class), any(SqlSession.class))).
                thenReturn(false);
        when(creativeGroupDao.update(any(CreativeGroup.class), eq(key), any(SqlSession.class))).
                thenReturn(creativeGroup);

        // Perform test
        CreativeGroup result = creativeGroupManager.update(creativeGroup.getId(), creativeGroup, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void testUpdateCreativeGroupFailByWrongPayload() throws Exception {
        // prepare data
        creativeGroup = EntityFactory.createCreativeGroup();
        creativeGroup.setId(Math.abs(EntityFactory.random.nextLong()));
        creativeGroup.setExternalId(null);
        creativeGroup.setName("´´" + creativeGroup.getName());

        // perform test
        try {
            creativeGroupManager.update(creativeGroup.getId(), creativeGroup, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getFieldErrors().size(), is(1));
            FieldError error = e.getErrors().getFieldErrors().get(0);
            assertThat(error.getField(), is("name"));
            assertThat(error.getDefaultMessage(),
                    is("Invalid name, it contains illegal characters."));
        }
    }

    @Test
    public void removeCreativeGroup() throws Exception {
        // prepare data
        creativeGroup = EntityFactory.createCreativeGroup();
        creativeGroup.setId(Math.abs(EntityFactory.random.nextLong()));
        creativeGroup.setExternalId(null);

        //customize mock's behavior
        when(creativeGroupDao.get(anyLong(), eq(key), any(SqlSession.class))).thenReturn(creativeGroup);
        when(creativeInsertionDao.getCreativeInsertionsByGroupId(anyLong(), eq(key), eq(sqlSessionMock))).
                thenReturn(new ArrayList<CreativeInsertion>());
        doNothing().when(creativeGroupDao).remove(eq(creativeGroup.getId()), eq(key), eq(sqlSessionMock));

        // perform test
        SuccessResponse result = creativeGroupManager.remove(creativeGroup.getId(), key, false);
        assertThat(result, is(notNullValue()));
        assertThat(result.getMessage(), is("Creative Group " + creativeGroup.getId() + " successfully deleted."));
    }

    @Test
    public void testSaveCreativeGroupCreative() throws Exception {
        // prepare data
        CreativeGroupCreative groupCreative = EntityFactory.createCreativeGroupCreative();

        //customize mock's behavior
        when(creativeGroupCreativeDao.get(anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(null);
        when(creativeGroupCreativeDao.save(any(CreativeGroupCreative.class), eq(sqlSessionMock))).
                thenReturn(groupCreative);

        // perform test
        CreativeGroupCreative result = creativeGroupManager.saveCreativeGroupCreative(
                groupCreative.getCreativeGroupId(), groupCreative, key);

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testUpdateCreativeGroupCreative() throws Exception {
        // prepare data
        CreativeGroupCreative groupCreative = EntityFactory.createCreativeGroupCreative();
        CreativeGroupCreative groupCreativeUpd = new CreativeGroupCreative();
        groupCreativeUpd.setCreativeGroupId(groupCreative.getCreativeGroupId());
        groupCreativeUpd.setCreativeId(groupCreative.getCreativeId());
        groupCreativeUpd.setDisplayOrder(5L);
        groupCreativeUpd.setDisplayQuantity(10L);

        //customize mock's behavior
        when(creativeGroupCreativeDao.get(anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(groupCreative);
        when(creativeGroupCreativeDao.update(any(CreativeGroupCreative.class), eq(key), eq(sqlSessionMock))).
                thenReturn(groupCreativeUpd);

        // perform test
        CreativeGroupCreative result = creativeGroupManager.saveCreativeGroupCreative(
                groupCreativeUpd.getCreativeGroupId(), groupCreativeUpd, key);
        assertThat(result, is(notNullValue()));
    }

    @Ignore
    @Test
    public void testCreateAssociationsNewCreative() throws Exception {
        // prepare data
        prepareDataToAssociationsTest();

        // perform test
        Either<Errors, String> result = creativeGroupManager.createAssociations(groupCreatives, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success(), is("Associations successfully created."));
    }

    @Ignore
    @Test
    public void testCreateAssociationsExistentCreative() throws Exception {
        // prepare data
        prepareDataToAssociationsTest();

        creative.setId(creativeExistent.getId());
        newCreatives = new ArrayList<>();
        newCreatives.add(creative);
        //set only one creative
        groupCreatives.setCreatives(newCreatives);

        // perform test
        Either<Errors, String> result = creativeGroupManager.createAssociations(groupCreatives, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success(), is("Associations successfully created."));
    }

    @Test
    public void testCreateAssociationsExistentCreativeLongAliasName() throws Exception {
        // prepare data
        prepareDataToAssociationsTest();

        creative.setId(creativeExistent.getId());
        creative.setAlias(EntityFactory.faker.lorem().fixedString(257));
        newCreatives = new ArrayList<>();
        newCreatives.add(creative);
        //set only one creative
        groupCreatives.setCreatives(newCreatives);

        // perform test
        Either<Errors, String> result = creativeGroupManager.createAssociations(groupCreatives, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is(equalTo(
                String.format("Invalid %1s, it supports characters up to %2s.", "Creative.alias", 256))));
    }

    @Test
    public void testCreateAssociationsWithoutCreative() throws Exception {
        // prepare data
        groupCreatives.setCreatives(new ArrayList<Creative>());

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(BusinessCode.INVALID.toString()));
            assertThat(e.getMessage(), is("Invalid entity or payload"));
        }
    }

    @Test
    public void testSetAlias() {
        Creative testCreative = new Creative();

        testCreative.setFilename("aFileName.jpg");
        testCreative.setAlias("someAlias");

        //case with alias already set
        String result = creativeGroupManager.getAppropriateAlias(testCreative,true);
        assertThat(result, is(equalTo("someAlias")));

        testCreative.setAlias("aFileName");
        result = creativeGroupManager.getAppropriateAlias(testCreative,false);
        assertThat(result, is(equalTo("aFileName")));

        testCreative.setAlias("");
        result = creativeGroupManager.getAppropriateAlias(testCreative,false);
        assertThat(result, is(equalTo("aFileName_1")));

        testCreative.setAlias(null);
        result = creativeGroupManager.getAppropriateAlias(testCreative,false);
        assertThat(result, is(equalTo("aFileName_1")));

        testCreative.setAlias("aFileName");
        result = creativeGroupManager.getAppropriateAlias(testCreative,true);
        assertThat(result, is(equalTo("aFileName")));

        testCreative.setCreativeType(CreativeManager.CreativeType.TRD.toString());
        testCreative.setFilename("aFileName.3rd");
        testCreative.setAlias(null);
        result = creativeGroupManager.getAppropriateAlias(testCreative,true);
        assertThat(result, is(equalTo("aFileName")));
    }

    @Ignore
    @Test
    public void testCreateAssociationsWithoutGroups() throws Exception {
        // prepare data
        groupCreatives.setCreativeGroupIds(new ArrayList<Long>());

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(BusinessCode.INVALID.toString()));
            assertThat(e.getMessage(), is("Invalid entity or payload"));
        }
    }

    @Test
    public void testCreateAssociationsWithWrongAccessCampaign() throws Exception {

        //customize mock behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN, sqlSessionMock);

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(SecurityCode.ILLEGAL_USER_CONTEXT.toString()));
            assertThat(e.getMessage(), is("User not allowed in this context"));
        }
    }

    @Test
    public void testCreateAssociationsGroupsOutCampaign() throws Exception {
        //customize mock behavior 
        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(groupCreatives.getCampaignId()),
                anyList(), any(SqlSession.class))).thenReturn(0L);

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(BusinessCode.NOT_FOUND.toString()));
            assertThat(e.getMessage(), is("The provided entity doesn't exist"));
        }
    }

    @Test
    public void testCreateAssociationsCreativeNonexistent() throws Exception {
        //prepare data
        creative.setId(creativeExistent.getId());
        newCreatives = new ArrayList<>();
        newCreatives.add(creative);
        //set only one creative
        groupCreatives.setCreatives(newCreatives);

        //customize mock behavior 
        when(creativeDao.get(anyLong(), any(SqlSession.class))).thenReturn(null);

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(BusinessCode.NOT_FOUND.toString()));
            assertThat(e.getMessage(), is("The provided entity doesn't exist"));
        }
    }

    @Ignore
    @Test
    public void testCreateAssociationsCreativeWrongAccess() throws Exception {
        //prepare data
        creative.setId(creativeExistent.getId());
        creative.setFilename(null);
        newCreatives = new ArrayList<>();
        newCreatives.add(creative);
        //set only one creative
        groupCreatives.setCreatives(newCreatives);

        //customize mock behavior 
        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(groupCreatives.getCampaignId()),
                anyList(), any(SqlSession.class))).thenReturn(new Long(groupCreatives.getCreativeGroupIds().size()));
        when(creativeDao.get(anyLong(), any(SqlSession.class))).thenReturn(creativeExistent);
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CREATIVE, sqlSessionMock);

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(SecurityCode.ILLEGAL_USER_CONTEXT.toString()));
            assertThat(e.getMessage(), is("User not allowed in this context"));
        }
    }

    @Ignore
    @Test
    public void testCreateAssociationsCreativeOutCampaign() throws Exception {
        //prepare data
        creative = EntityFactory.createCreative();
        creative.setFilename(null);
        newCreatives = new ArrayList<>();
        newCreatives.add(creative);
        //set only one creative
        groupCreatives.setCreatives(newCreatives);

        //customize mock behavior
        when(creativeDao.get(anyLong(), any(SqlSession.class))).thenReturn(creative);
        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(groupCreatives.getCampaignId()), anyList(),
                any(SqlSession.class))).thenReturn(new Long(groupCreatives.getCreativeGroupIds().size()));
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE, sqlSessionMock);

        // perform test
        try {
            creativeGroupManager.createAssociations(groupCreatives, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            ErrorCode error = e.getErrorCode();
            assertThat(error.toString(), is(BusinessCode.NOT_FOUND.toString()));
            assertThat(e.getMessage(), is("The provided entity doesn't exist"));
        }
    }

    @Test
    public void testGetGroupCreativesByParamsWithFilterParamsOk() throws Exception {
        //prepare data
        int countReult = 10;
        CreativeInsertionFilterParam filterParam = prepareDataToGetGroupCreativesTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);

        //customize mock's behavior
        when(creativeGroupCreativeDao.getGroupCreativesByFilterParam(eq(campaign.getId()), eq(filterParam), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getGroupCreativesByFilterParam(countReult));
        when(creativeGroupCreativeDao.getCountGroupCreativesByFilterParam(eq(campaign.getId()), eq(filterParam),
                eq(sqlSessionMock))).thenReturn(new Long(countReult));

        // Perform test
        Either<Errors, RecordSet<CreativeGroupCreativeView>> result = creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords(), is(notNullValue()));
    }

    @Test
    public void testGetGroupCreativesByParamsWithNullFilterParamsOk() throws Exception {
        //prepare data
        int countReult = 10;
        CreativeInsertionFilterParam filterParam = null;

        //customize mock's behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN, sqlSessionMock);
        when(creativeGroupCreativeDao.getGroupCreativesByFilterParam(eq(campaign.getId()), eq(filterParam), eq(key.getUserId()),
                eq(sqlSessionMock))).thenAnswer(getGroupCreativesByFilterParam(countReult));
        when(creativeGroupCreativeDao.getCountGroupCreativesByFilterParam(eq(campaign.getId()), eq(filterParam),
                eq(sqlSessionMock))).thenReturn(new Long(countReult));

        // Perform test
        Either<Errors, RecordSet<CreativeGroupCreativeView>> result = creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords(), is(notNullValue()));
    }

    @Test
    public void testGetGroupCreativesByParamsWithNullCampaignId() throws Exception {
        //prepare data
        campaign.setId(null);
        CreativeInsertionFilterParam filterParam = prepareDataToGetGroupCreativesTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);

        // Perform test
        try {
            creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Campaign Id cannot be null."));
        }
    }

    @Test
    public void testGetGroupCreativesByParamsWithWrongPayload() throws Exception {
        //prepare data
        CreativeInsertionFilterParam filterParam = prepareDataToGetGroupCreativesTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);
        filterParam.setGroupId(null);

        // Perform test
        Either<Errors, RecordSet<CreativeGroupCreativeView>> result = creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is("Invalid groupId, it cannot be empty."));
    }

    @Test
    public void testGetGroupCreativesByParamsWithWrongPayloadIds() throws Exception {
        //prepare data
        CreativeInsertionFilterParam filterParam = prepareDataToGetGroupCreativesTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);
        filterParam.setGroupId(null);
        filterParam.setSiteId(null);

        // Perform test
        Either<Errors, RecordSet<CreativeGroupCreativeView>> result = creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(2));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is("Invalid siteId, it cannot be empty."));
        error = result.error().getErrors().get(1);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(error.getMessage(), is("Invalid groupId, it cannot be empty."));
    }

    @Test
    public void testGetGroupCreativesByParamsWithWrongAccessToCampaign() throws Exception {
        //prepare data
        CreativeInsertionFilterParam filterParam = prepareDataToGetGroupCreativesTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN,
                Collections.singletonList(campaign.getId()), sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<CreativeGroupCreativeView>> result = creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void testGetGroupCreativesByParamsWithWrongAccessToPayloadIds() throws Exception {
        //prepare data
        CreativeInsertionFilterParam filterParam = prepareDataToGetGroupCreativesTest(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.SITE,
                Collections.singletonList(filterParam.getSiteId()), sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<CreativeGroupCreativeView>> result = creativeGroupManager.getGroupCreativesByCreativeInsertionFilterParam(campaign.getId(), filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(1));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(SecurityCode.class));
        assertThat(error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
        assertThat(error.getMessage(), is("The user is not allowed in this context or the requested entity does not exist"));
    }

    private CreativeInsertionFilterParam prepareDataToGetGroupCreativesTest(CreativeInsertionFilterParamTypeEnum pivotType, CreativeInsertionFilterParamTypeEnum levelType) throws Exception {
        CreativeInsertionFilterParam filterParam = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                pivotType, levelType);
        filterParam.setPivotType(filterParam.getPivotType().toLowerCase());
        filterParam.setType(filterParam.getType().toLowerCase());

        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.SITE, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.SITE_SECTION, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.PLACEMENT, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE, sqlSessionMock);
        return filterParam;
    }

    private void prepareDataToAssociationsTest() throws Exception {
        //customize mock's behavior
        when(utilityWrapperCGM.createFile(anyString())).thenAnswer(createFile());
        testPath = System.getProperty("java.io.tmpdir") + File.separator + "test";
        File f = new File(testPath);
        if (!f.exists() && !f.isDirectory()) {
            f.mkdirs();
        }
        when(utilityWrapperCM.getPath(eq("image.path"))).thenReturn(testPath);
        when(utilityWrapperCM.getPath(eq("tmp.path"))).thenReturn(testPath);

        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(groupCreatives.getCampaignId()), anyList(),
                any(SqlSession.class))).thenReturn(new Long(groupCreatives.getCreativeGroupIds().size()));
        when(creativeGroupCreativeDao.getCreativeGroupCreativesByCG(anyLong(), eq(key),
                any(SqlSession.class))).thenReturn(new ArrayList<CreativeGroupCreative>());

        when(campaignDao.get(anyLong(), any(SqlSession.class))).thenReturn(EntityFactory.createCampaign());
        when(creativeDao.getCreativeIdByCampaignIdAndFileName(anyLong(), any(String.class), any(SqlSession.class))).thenReturn(null);
        when(creativeDao.getNextId(any(SqlSession.class))).thenReturn(creativeExistent.getId());
        when(creativeDao.create(any(Creative.class), eq(key), any(SqlSession.class))).thenAnswer(saveCreative());
        when(creativeDao.get(anyLong(), any(SqlSession.class))).thenReturn(creativeExistent);

        when(creativeGroupDao.get(anyLong(), eq(key), any(SqlSession.class))).thenReturn(group);
        when(creativeDao.getCountCreativesByCampaignIdAndIds(anyList(), eq(groupCreatives.getCampaignId()),
                any(SqlSession.class))).thenReturn((long) groupCreatives.getCreatives().size());
        when(creativeGroupCreativeDao.save(any(CreativeGroupCreative.class),
                any(SqlSession.class))).thenReturn(new CreativeGroupCreative());
        doNothing().when(creativeGroupCreativeDao).remove(anyLong(), anyLong(), any(SqlSession.class));
    }

    private Answer<InputStream> createFile() {
        return new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws IOException {
                String pathFilename = (String) invocation.getArguments()[0];
                String[] filenameParts = pathFilename.split("\\" + File.separator);
                String filename = filenameParts[filenameParts.length - 1];
                // Create test directory
                path = System.getProperty("java.io.tmpdir") + File.separator + "unitTestCG" + groupCreatives.getCampaignId() + File.separator;
                File baseFile = new File(path);
                if (!baseFile.exists()) {
                    if (!baseFile.mkdir()) {
                        fail("Could not create path " + path);
                    }
                }

                //create a file
                File f = new File(path, filename);
                f.createNewFile();
                FileWriter writer = new FileWriter(f, true);
                writer.write("This file needs content for method validations");
                writer.close();
                InputStream inputstream = new FileInputStream(f);

                return inputstream;
            }
        };
    }

    private static Answer<Creative> saveCreative() {
        return new Answer<Creative>() {
            @Override
            public Creative answer(InvocationOnMock invocation) {
                Creative result = (Creative) invocation.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                result.setLogicalDelete("N");
                return result;
            }
        };
    }

    private static Answer<CreativeGroup> createGroup() {
        return new Answer<CreativeGroup>() {
            @Override
            public CreativeGroup answer(InvocationOnMock invocation) {
                CreativeGroup result = (CreativeGroup) invocation.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                result.setLogicalDelete("N");
                return result;
            }
        };
    }

    private static Answer<RecordSet<CreativeGroup>> getGroupsByCriteria(final Map<Long, CreativeGroup> existingGroupsMap) {
        return new Answer<RecordSet<CreativeGroup>>() {
            @Override
            public RecordSet<CreativeGroup> answer(InvocationOnMock invocation) {
                SearchCriteria ciCriteria = (SearchCriteria) invocation.getArguments()[0];
                String criteria = ciCriteria.getQuery();

                // get ids to recover
                String delimit = ",";
                criteria = criteria.substring(7, criteria.length() - 1);
                String[] idsString = criteria.split(delimit);

                List<CreativeGroup> resultList = new ArrayList<>();
                for (String idString : idsString) {
                    Long id = Long.valueOf(idString.trim());
                    resultList.add(existingGroupsMap.get(id));
                }
                RecordSet<CreativeGroup> result = new RecordSet<>(resultList);
                return result;
            }
        };
    }

    private static Answer<List<CreativeGroupCreativeView>> getGroupCreativesByFilterParam(final int counter) {
        return new Answer<List<CreativeGroupCreativeView>>() {
            @Override
            public List<CreativeGroupCreativeView> answer(InvocationOnMock invocation) {
                List<CreativeGroupCreativeView> result = new ArrayList<>();
                CreativeGroupCreativeView view;
                for (int i = 0; i < counter; i++) {
                    view = EntityFactory.createCreativeGroupCreativeView();
                    view.setCreativeGroupWeightEnabled(null);
                    result.add(view);
                }
                return result;
            }
        };
    }

    private void deleteAllFiles(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteFile(c);
            }
        }

        if (!f.delete()) {
            fail("Could not clean up test directory: " + f.getAbsolutePath());
        }
    }

    private void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteFile(c);
            }
        }
        if (!f.delete()) {
            fail("Could not clean up test directory");
        }
    }
}
