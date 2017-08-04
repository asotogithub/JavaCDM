package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@code CreativeInsertionManager}
 *
 * @author Marcelo Heredia
 */
public class CreativeInsertionManagerTest extends CreativeInsertionManagerBaseTest {

    private BulkCreativeInsertion bulkCI;

    @Before
    public void setUp() throws Exception {
        bulkCI = new BulkCreativeInsertion();
        when(creativeInsertionDao.create(any(CreativeInsertion.class),
                eq(sqlSessionMock))).thenAnswer(saveCreativeInsertion());
    }

    @Test
    public void createFirstInsertionForCampaignOk() throws Exception {
        //set values
        creativeInsertion.setId(null);

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(new ArrayList<CreativeInsertionView>());
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        CreativeInsertion result = manager.create(creativeInsertion, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void createInsertionForExistingPlacementNonXMLAndCreativeNonXMLOk() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());

        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions,
                campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));
        when(creativeGroupCreativeDao.getExistingCGC(anyList(),
                anyLong(), any(SqlSession.class))).thenReturn(new ArrayList<String>());

        //call method to test
        CreativeInsertion result = manager.create(creativeInsertion, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void createInsertionForExistingPlacementXMLAndCreativeXMLOk() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCreativeType(CreativeManager.CreativeType.XML.getCreativeType());

        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions,
                campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        CreativeInsertion result = manager.create(creativeInsertion, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInsertionWithWrongPayload() throws Exception {
        //set values
        creativeInsertion = null;

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInsertionWithoutKey() throws Exception {
        //set values
        key = null;

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ValidationException.class)
    public void createInsertionWithErrorsOnModelValidations() throws Exception {
        //set values
        creativeInsertion.setWeight(EntityFactory.random.nextLong());

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test
    public void createMethodValidatesClickthroughIsUnallowedFor3rd() throws Exception {
        //set values
        creativeInsertion.setCreativeType(AdminFile.FileType.TRD.getFileType());
        creativeInsertion.setClickthrough("anInvalidClickThrough");

        // mock dao behavior
        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        for (CreativeInsertionView ciView : existentsInDB) {
            ciView.setCreativeType(AdminFile.FileType.TRD.getFileType());
            ciView.setPrimaryClickthrough(null);
            ciView.setAdditionalClickthroughs(null);
        }
        creative.setCreativeType(AdminFile.FileType.TRD.getFileType());
        creative.setClickthrough(null);
        creative.setClickthroughs(null);

        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        try {
            manager.create(creativeInsertion, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("3rd Creatives cannot have either primary or additional Clickthroughs."));
        }
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void createInsertionWithWrongAccessOnCampaign() throws Exception {
        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE_GROUP, Collections.singletonList(creativeInsertion.getCreativeGroupId()),
                sqlSessionMock);

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void createInsertionWithWrongAccessOnCreative() throws Exception {
        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE, Collections.singletonList(creativeInsertion.getCreativeId()),
                sqlSessionMock);

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void createInsertionWithWrongAccessOnGroup() throws Exception {
        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE_GROUP, Collections.singletonList(creativeInsertion.getCreativeGroupId()),
                sqlSessionMock);

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = DataNotFoundForUserException.class)
    public void createInsertionWithWrongAccessOnPlacement() throws Exception {
        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PLACEMENT, Collections.singletonList(creativeInsertion.getPlacementId()),
                sqlSessionMock);

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ConflictException.class)
    public void createInsertionWithDuplicateDataOnDB() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());

        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions,
                campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        CreativeInsertionView view = EntityFactory.createCreativeInsertionView();
        view.setCampaignId(creativeInsertion.getCampaignId());
        view.setPlacementId(creativeInsertion.getPlacementId());
        view.setCreativeGroupId(creativeInsertion.getCreativeGroupId());
        view.setCreativeId(creativeInsertion.getCreativeId());
        existentsInDB.set(countInsertions - 1, view);

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ValidationException.class)
    public void createInsertionForExistingPlacementNonXMLAndCreativeXMLError() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCreativeType(CreativeManager.CreativeType.XML.getCreativeType());

        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ValidationException.class)
    public void createInsertionForExistingPlacementXMLAndCreativeNonXMLError() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());

        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions,
                campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ValidationException.class)
    public void createInsertionWrongStatusOnPlacement() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCreativeType(CreativeManager.CreativeType.JPG.getCreativeType());
        placement.setStatus(InsertionOrderStatusEnum.NEW.getName());

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(new ArrayList<CreativeInsertionView>());
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ValidationException.class)
    public void createInsertionWithElementsOnDifferentCampaignFromInsertionError() throws Exception {
        //set values
        creativeInsertion.setId(null);
        creative.setCampaignId(Math.abs(EntityFactory.random.nextLong()));
        placement.setCampaignId(Math.abs(EntityFactory.random.nextLong()));

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(new ArrayList<CreativeInsertionView>());
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test(expected = ValidationException.class)
    public void createInsertionNotMatchDimensions() throws Exception {
        //set values
        creativeInsertion.setId(null);
        placement.setWidth(Math.abs(EntityFactory.random.nextLong()));
        placement.setSizeName(placement.getWidth() + "x" + placement.getHeight());

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenReturn(new ArrayList<CreativeInsertionView>());
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singletonList(creative)));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenReturn(new ArrayList(Collections.singleton(placement)));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        //call method to test
        manager.create(creativeInsertion, key);
    }

    @Test
    public void getCreativeInsertionsDynamicWithNoCreativeInsertionsOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        ciBulk.setPlacementId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesPlacementLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenReturn(new ArrayList<CreativeInsertionView>());

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getCreativeInsertionsDynamicSiteLevelOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SITE);
        ciBulk.setSiteId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesSiteLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenAnswer(getCreativeInsertionsDynamic(10, true));

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getCreativeInsertionsDynamicSectionLevelOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SECTION);
        ciBulk.setSectionId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesSectionLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenAnswer(getCreativeInsertionsDynamic(10, false));

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getCreativeInsertionsDynamicPlacementLevelOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        ciBulk.setPlacementId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesPlacementLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenAnswer(getCreativeInsertionsDynamic(10, false));

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getCreativeInsertionsDynamicGroupLevelOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);
        ciBulk.setGroupId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesGroupLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenAnswer(getCreativeInsertionsDynamic(10, false));

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getCreativeInsertionsDynamicScheduleLevelOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setCreativeId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesScheduleLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenAnswer(getCreativeInsertionsDynamic(10, false));

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test
    public void getCreativeInsertionsDynamicCreativeLevelOk() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.CREATIVE, CreativeInsertionFilterParamTypeEnum.CREATIVE);
        ciBulk.setCreativeId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(creativeInsertionDao.getSchedulesCreativeLevel(eq(campaignId),
                any(CreativeInsertionFilterParam.class), anyLong(), anyLong(),
                any(SqlSession.class))).thenAnswer(getCreativeInsertionsDynamic(10, false));

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCreativeInsertionsDynamicWithNullParameters() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        ciBulk.setPlacementId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());

        // Perform test
        manager.getCreativeInsertions(null, ciBulk, null, null, key);
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongPayload() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        ciBulk.setPlacementId(null);
        ciBulk.setType(ciBulk.getType().toUpperCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongPaginatorValues() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        ciBulk.setPlacementId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(
                campaignId, ciBulk, null, new Long(SearchCriteria.SEARCH_CRITERIA_PAGE_SIZE + 1), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongCampaignAccess() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        ciBulk.setPlacementId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), anyString(), any(SqlSession.class))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongSiteAccessPivotSite() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        ciBulk.setCreativeId(null);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE), eq(Collections.singletonList(ciBulk.getSiteId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongSectionAccessPivotSite() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setCreativeId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE_SECTION), eq(Collections.singletonList(ciBulk.getSectionId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongPlacementAccessPivotSite() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setCreativeId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT), eq(Collections.singletonList(ciBulk.getPlacementId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongGroupAccessPivotSite() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setCreativeId(null);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP), eq(Collections.singletonList(ciBulk.getGroupId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongPlacementAccessPivotPlacement() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        ciBulk.setCreativeId(null);
        ciBulk.setSiteId(null);
        ciBulk.setSectionId(null);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT), eq(Collections.singletonList(ciBulk.getPlacementId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void getCreativeInsertionsDynamicWithWrongGroupAccessPivotPlacement() {
        // set data
        CreativeInsertionFilterParam ciBulk = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.PLACEMENT, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        ciBulk.setType(ciBulk.getType().toLowerCase());
        ciBulk.setPivotType(ciBulk.getPivotType().toLowerCase());
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        ciBulk.setCreativeId(null);
        ciBulk.setSiteId(null);
        ciBulk.setSectionId(null);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP), eq(Collections.singletonList(ciBulk.getGroupId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.getCreativeInsertions(campaignId, ciBulk, null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAllCreativeInsertionsWithNullCampaignId() {

        // Perform test
        manager.getAllCreativeInsertions(null, null, null, key);
    }

    @Test(expected = SystemException.class)
    public void getAllCreativeInsertionsWithWrongAccessToCampaign() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), anyString(), any(SqlSession.class))).thenReturn(false);

        // Perform test
        manager.getAllCreativeInsertions(campaignId, null, null, key);
    }

    @Test(expected = SystemException.class)
    public void getAllCreativeInsertionsForNonExistentCampaign() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(campaignDao.get(eq(campaignId), eq(sqlSessionMock))).thenReturn(null);

        // Perform test
        manager.getAllCreativeInsertions(campaignId, null, null, key);
    }

    @Test
    public void getAllCreativeInsertionsForCampaignWithNoCreativeInsertionsOk() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());

        // Initialize mocks
        when(campaignDao.get(eq(campaignId), eq(sqlSessionMock))).thenReturn(new Campaign());
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaignId), anyLong(),
                anyLong(), eq(sqlSessionMock))).thenReturn(Collections.<CreativeInsertionView>emptyList());
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaignId), eq(sqlSessionMock))).thenReturn(0L);

        // Perform test
        RecordSet<CreativeInsertionView> creativeInsertions = manager.getAllCreativeInsertions(campaignId, null, null, key);
        assertThat(creativeInsertions.getRecords(), is(empty()));
        assertThat(creativeInsertions.getTotalNumberOfRecords(), is(equalTo(0)));
    }

    @Test
    public void getAllCreativeInsertionsForCampaignWithCreativeInsertionsOk() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        List<CreativeInsertionView> list = EntityFactory.createCreativeInsertionViewListForCampaign(campaignId, 2, 2);

        // Initialize mocks
        when(campaignDao.get(eq(campaignId), eq(sqlSessionMock))).thenReturn(new Campaign());
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaignId), anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(list);
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaignId), eq(sqlSessionMock))).thenReturn(new Long(list.size()));

        // Perform test
        RecordSet<CreativeInsertionView> creativeInsertions = manager.getAllCreativeInsertions(campaignId, null, null, key);
        assertThat(creativeInsertions.getRecords(), hasSize(2));
        assertThat(creativeInsertions.getTotalNumberOfRecords(), is(equalTo(2)));
    }

    @Test
    public void getAllCreativeInsertionsForCampaignWithAdditionalClickthroughsOk() {
        // set data
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        List<CreativeInsertionView> list = EntityFactory.createCreativeInsertionViewListForCampaign(campaignId, 1, 2);

        // Initialize mocks
        when(campaignDao.get(eq(campaignId), eq(sqlSessionMock))).thenReturn(new Campaign());
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaignId), anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(list);
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaignId), eq(sqlSessionMock))).thenReturn(new Long(list.size()));

        // Perform test
        RecordSet<CreativeInsertionView> creativeInsertions = manager.getAllCreativeInsertions(campaignId, null, null, key);
        List<Clickthrough> actualClickthroughs = creativeInsertions.getRecords().get(0).getAdditionalClickthroughs();
        assertThat(actualClickthroughs, hasSize(2));
    }

    @Test
    public void getByCreativeIdPass() {
        // prepare data
        int numberOfRecord = 5;
        List<CreativeInsertionView> list = EntityFactory.createCreativeInsertionViewListForCampaign(
                creative.getCampaignId(), numberOfRecord, 1);

        // customize mock's behavior
        when(creativeDao.get(eq(creative.getId()), eq(sqlSessionMock))).thenReturn(creative);
        when(creativeInsertionDao.getSchedulesByCreativeId(
                eq(creative.getCampaignId()), eq(creative.getId()),
                anyString(), anyLong(), anyLong(), eq(sqlSessionMock))).thenReturn(list);
        when(creativeInsertionDao.getCountSchedulesByCreativeId(eq(creative.getCampaignId()),
                eq(creative.getId()), anyString(), eq(sqlSessionMock)))
                .thenReturn(Long.valueOf(numberOfRecord));

        // Perform test
        Either<Errors, RecordSet<CreativeInsertionView>> result =
                manager.getByCreativeId(creative.getId(), null, null, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(numberOfRecord)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(numberOfRecord)));
    }

    @Test
    public void getByCreativeIdWithNullParameters() {
        // prepare data
        creative.setId(null);

        // Perform test
        try {
            manager.getByCreativeId(creative.getId(), null, null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Creative Id"))));
        }
    }

    @Test
    public void getByCreativeIdWithWrongAccess() {
        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CREATIVE,
                sqlSessionMock);

        // Perform test
        Either<Errors, RecordSet<CreativeInsertionView>> result =
                manager.getByCreativeId(creative.getId(), null, null, key);

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
    public void updateMethodValidatesClickthroughIsUnallowedFor3rd() throws Exception {
        // mock dao behavior
        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        for (CreativeInsertionView ciView : existentsInDB) {
            ciView.setCreativeType(AdminFile.FileType.TRD.getFileType());
            ciView.setPrimaryClickthrough(null);
            ciView.setAdditionalClickthroughs(null);
        }
        creative.setCreativeType(AdminFile.FileType.TRD.getFileType());
        creative.setClickthrough(null);
        creative.setClickthroughs(null);

        //set values
        CreativeInsertion oldCi = new CreativeInsertion(
                creativeInsertion.getId(),
                existentsInDB.get(1).getCampaignId(),
                existentsInDB.get(1).getCampaignId(),
                existentsInDB.get(1).getCreativeId(),
                existentsInDB.get(1).getPlacementId(),
                creativeInsertion.getStartDate(),
                creativeInsertion.getEndDate(),
                creativeInsertion.getTimeZone(),
                creativeInsertion.getWeight(),
                creativeInsertion.getClickthrough(),
                creativeInsertion.getReleased(),
                creativeInsertion.getSequence(),
                creativeInsertion.getCreatedTpwsKey(),
                creativeInsertion.getModifiedTpwsKey()
        );
        oldCi.setCreativeType(AdminFile.FileType.TRD.getFileType());

        creativeInsertion.setCreativeType(AdminFile.FileType.TRD.getFileType());
        creativeInsertion.setClickthrough("anInvalidClickThrough");
        creativeInsertion.setId(existentsInDB.get(1).getId());
        creativeInsertion.setCampaignId(existentsInDB.get(1).getCampaignId());
        creativeInsertion.setCreativeId(existentsInDB.get(1).getCreativeId());
        creativeInsertion.setCreativeGroupId(existentsInDB.get(1).getCreativeGroupId());
        creativeInsertion.setPlacementId(existentsInDB.get(1).getPlacementId());

        when(creativeDao.openSession()).thenReturn(sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE_INSERTION, sqlSessionMock);
        when(creativeInsertionDao.get(eq(creativeInsertion.getId()),
                eq(sqlSessionMock))).thenReturn(oldCi);

        //call method to test
        try {
            manager.update(existentsInDB.get(1).getId(), creativeInsertion, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors(), is(notNullValue()));
            List<ObjectError> errors = e.getErrors().getAllErrors();
            for (ObjectError error : errors) {
                String[] codes = error.getCodes();
                for (String code : codes) {
                    assertThat(code, containsString("Invalid"));
                }
                assertThat(error.getCode(), is("Invalid"));
                assertThat(error.getDefaultMessage(), is("3rd Creatives cannot have either primary or additional Clickthroughs."));
                assertThat(error.getObjectName(), is("CreativeInsertion"));
            }
        }
    }

    private static Answer<CreativeInsertion> saveCreativeInsertion() {
        return new Answer<CreativeInsertion>() {
            public CreativeInsertion answer(InvocationOnMock invocation) {
                CreativeInsertion ci = (CreativeInsertion) invocation.getArguments()[0];
                ci.setId(Math.abs(EntityFactory.random.nextLong()));
                return ci;
            }
        };
    }

    private static Answer<List<CreativeInsertionView>> getCreativeInsertionsDynamic(final int counter, final boolean isSiteLevel) {
        return new Answer<List<CreativeInsertionView>>() {
            @Override
            public List<CreativeInsertionView> answer(InvocationOnMock invocation) {
                Long campaignId = (Long) invocation.getArguments()[0];
                CreativeInsertionFilterParamTypeEnum type = CreativeInsertionFilterParamTypeEnum.SITE;
                if (!isSiteLevel) {
                    CreativeInsertionFilterParam parentIds = (CreativeInsertionFilterParam) invocation.getArguments()[1];
                    type = CreativeInsertionFilterParamTypeEnum.valueOf(parentIds.getType().toUpperCase());
                }
                CreativeInsertionView insertion = new CreativeInsertionView();
                insertion.setCampaignId(campaignId);
                List<CreativeInsertionView> result = new ArrayList<>();
                for (int i = 0; i < counter; i++) {
                    switch (type) {
                        case SITE:
                            insertion.setSiteId(Math.abs(EntityFactory.random.nextLong()));
                            insertion.setSiteName(EntityFactory.faker.name().firstName());
                            result.add(insertion);
                            break;
                        case SECTION:
                            insertion.setSiteSectionId(EntityFactory.random.nextLong());
                            insertion.setSiteSectionName(EntityFactory.faker.name().firstName());
                            result.add(insertion);
                            break;
                        case PLACEMENT:
                            Placement placement = EntityFactory.createPlacement();
                            insertion.setSizeName(placement.getWidth() + "x" + placement.getHeight());
                            insertion.setPlacementId(placement.getId());
                            insertion.setPlacementStartDate(placement.getStartDate());
                            insertion.setPlacementEndDate(placement.getEndDate());
                            insertion.setPlacementName(placement.getName());
                            insertion.setPlacementStatus(placement.getStatus());
                            insertion.setPlacementAssociationsWithCreativeGroups(5L);
                            result.add(insertion);
                            break;
                        case GROUP:
                            CreativeGroup group = EntityFactory.createCreativeGroup();
                            insertion.setCreativeGroupId(group.getId());
                            insertion.setCreativeGroupName(group.getName());
                            insertion.setCreativeGroupWeight(group.getWeight());
                            insertion.setCreativeGroupWeightEnabled(group.getEnableGroupWeight());
                            insertion.setCreativeGroupRotationType(group.getRotationType());
                            insertion.setCreativeGroupDoGeoTargeting(group.getDoGeoTargeting());
                            insertion.setCreativeGroupDoCookieTargeting(group.getDoCookieTargeting());
                            insertion.setCreativeGroupFrequencyCap(group.getFrequencyCap());
                            insertion.setCreativeGroupFrequencyCapWindow(group.getFrequencyCapWindow());
                            insertion.setCreativeGroupPriority(group.getPriority());
                            insertion.setCreativeGroupDaypartTarget(group.getDaypartTarget());
                            insertion.setCreativeGroupDoDaypartTarget(group.getDoDaypartTargeting());
                            insertion.setCreativeGroupAssociationsWithPlacements(1L);
                            insertion.setCreativeGroupAssociationsWithCreatives(10L);
                            result.add(insertion);
                            break;
                        case SCHEDULE:
                            insertion = EntityFactory.createCreativeInsertionView();
                            result.add(insertion);
                            break;
                        case CREATIVE:
                            Creative creative = EntityFactory.createCreative();
                            insertion.setCreativeId(creative.getId());
                            insertion.setCampaignId(creative.getCampaignId());
                            insertion.setCreativeAlias(creative.getAlias());
                            insertion.setCreativeType(creative.getCreativeType());
                            insertion.setFilename(creative.getFilename());
                            insertion.setPrimaryClickthrough(creative.getClickthrough());
                            insertion.setSizeName(creative.getWidth() + "x" + creative.getHeight());
                            insertion.setCreativeAssociationsWithPlacements(1L);
                            insertion.setCreativeAssociationsWithCreativeGroups(1L);
                            result.add(insertion);
                            break;
                    }
                }
                return result;
            }
        };
    }

    private BulkCreativeInsertion prepareBulkCreativeInsertion(int counter, Long campaignId, int testType) {

        BulkCreativeInsertion result = new BulkCreativeInsertion();
        List<CreativeInsertion> creativeInsertions = new ArrayList<>();
        CreativeInsertion insertion;
        for (int i = 0; i < counter; i++) {
            insertion = EntityFactory.createCreativeInsertion();
            insertion.setCampaignId(campaignId);
            insertion.setId(null);
            creativeInsertions.add(insertion);
        }

        if (testType == INSERTIONS_DUPLICATED_REJECTED) {
            int counterD = 3;
            while (counterD < creativeInsertions.size()) {
                creativeInsertions.set(counterD, creativeInsertions.get(counterD - 1));
                counterD += 5;
            }
        }
        result.setCreativeInsertions(creativeInsertions);
        return result;
    }

    private static Answer<List<CreativeInsertionView>> getAllCreativeInsertions(final List<CreativeInsertionView> existentCreatives) {
        return new Answer<List<CreativeInsertionView>>() {
            @Override
            public List<CreativeInsertionView> answer(InvocationOnMock invocation) {
                Long campaignId = (Long) invocation.getArguments()[0];
                Long numberOfRecords = (Long) invocation.getArguments()[2];

                //existentCreatives
                CreativeInsertionView insertion;

                List<CreativeInsertionView> result = new ArrayList<>();
                if (existentCreatives != null && !existentCreatives.isEmpty()) {
                    if (existentCreatives.size() >= numberOfRecords) {
                        for (CreativeInsertionView ciView : existentCreatives) {
                            ciView.setId(Math.abs(EntityFactory.random.nextLong()));
                            ciView.setCampaignId(campaignId);
                            ciView.setLogicalDelete("N");
                            ciView.setCreatedDate(new Date());
                            ciView.setModifiedDate(new Date());
                            result.add(ciView);
                            if (result.size() == numberOfRecords) {
                                break;
                            }
                        }
                    } else {
                        // create news or add duplicates to result
                        int counterIni = numberOfRecords.intValue() % existentCreatives.size();
                        int freq = numberOfRecords.intValue() / existentCreatives.size();
                        for (int i = 0; i < numberOfRecords; i++) {
                            if (counterIni < i) {
                                //first news
                                insertion = EntityFactory.createCreativeInsertionView();
                                insertion.setCampaignId(campaignId);
                            } else {
                                if ((i - counterIni) % freq == 0) {
                                    //duplicates
                                    int j = (i - counterIni) / freq;
                                    insertion = existentCreatives.get(j);
                                    insertion.setId(Math.abs(EntityFactory.random.nextLong()));
                                    insertion.setCampaignId(campaignId);
                                    insertion.setLogicalDelete("N");
                                    insertion.setCreatedDate(new Date());
                                    insertion.setModifiedDate(new Date());

                                } else {
                                    //new
                                    insertion = EntityFactory.createCreativeInsertionView();
                                    insertion.setCampaignId(campaignId);
                                }

                            }
                            result.add(insertion);
                        }
                    }
                } else {
                    for (int i = 0; i < numberOfRecords; i++) {
                        //new
                        insertion = EntityFactory.createCreativeInsertionView();
                        insertion.setCampaignId(campaignId);
                        result.add(insertion);
                    }

                }
                return result;
            }
        };
    }

    private static Answer<List<CreativeGroup>> getGroupsById(final Long campaignId) {
        return new Answer<List<CreativeGroup>>() {
            @Override
            public List<CreativeGroup> answer(InvocationOnMock invocation) {
                List<Long> groups;
                if (invocation.getArguments()[0] instanceof java.util.Set) {
                    Set<Long> groupsSet = (Set<Long>) invocation.getArguments()[0];
                    groups = new ArrayList<>(groupsSet);
                } else {
                    groups = (List<Long>) invocation.getArguments()[0];
                }

                List<CreativeGroup> resultList = new ArrayList<>();
                for (Long groupId : groups) {
                    CreativeGroup result = EntityFactory.createCreativeGroup();
                    result.setCampaignId(campaignId);
                    result.setId(groupId);
                    resultList.add(result);
                }
                return resultList;
            }
        };
    }

    private static Answer<Map<Long, CreativeManager.CreativeGlobalClassification>> getCreativeClassificationByPlacementId(final TypeClassPlacementOnDBToTest typePlacementTest) {
        return new Answer<Map<Long, CreativeManager.CreativeGlobalClassification>>() {
            @Override
            public Map<Long, CreativeManager.CreativeGlobalClassification> answer(InvocationOnMock invocation) {
                List<Long> placements;
                if (invocation.getArguments()[1] instanceof java.util.Set) {
                    Set<Long> placementsSet = (Set<Long>) invocation.getArguments()[1];
                    placements = new ArrayList<>(placementsSet);
                } else {
                    placements = (List<Long>) invocation.getArguments()[1];
                }

                Map<Long, CreativeManager.CreativeGlobalClassification> result = new HashMap<>();
                CreativeManager.CreativeGlobalClassification type = null;
                switch (typePlacementTest) {
                    case PLACEMENT_NON_XML:
                        type = CreativeManager.CreativeGlobalClassification.NON_XML;
                        break;
                    case PLACEMENT_XML:
                        type = CreativeManager.CreativeGlobalClassification.XML;
                        break;
                    case EMPTY:
                        return result;
                }

                for (Long placementId : placements) {
                    result.put(placementId, type);
                }
                return result;
            }
        };
    }
}
