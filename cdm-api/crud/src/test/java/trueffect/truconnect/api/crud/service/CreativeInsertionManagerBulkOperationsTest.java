package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.dto.BulkCreativeInsertion;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionBulkUpdate;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@code CreativeInsertionManager}
 * @author Marcelo Heredia
 */
public class CreativeInsertionManagerBulkOperationsTest extends CreativeInsertionManagerBaseTest {

    BulkCreativeInsertion bulkCI;
    Map<Long, CreativeInsertion> mapInsertionsExisting;
    Map<Long, CreativeGroup> mapGroupExisting;

    @Before
    public void setUp() throws Exception {
        mapGroupExisting = new HashMap<>();
        mapInsertionsExisting = new HashMap<>();
    }

    @Test
    public void bulkCreateFirstInsertionsForCampaignOk() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height,
                        TypeCreativesOnDBToTest.CREATIVES_NON_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
    }
    
    @Test
    public void bulkCreateInsertionsCGCOk() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height,
                        TypeCreativesOnDBToTest.CREATIVES_NON_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));
        when(creativeGroupCreativeDao.getExistingCGC(anyList(), anyLong(), any(SqlSession.class))).
                thenReturn(new ArrayList<String>());

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
    }
    
    @Test
    public void bulkCreateInsertionsCGCNotOk() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(0L);
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height,
                        TypeCreativesOnDBToTest.CREATIVES_NON_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));
        List<String> list = getCGC(bulkCI);
        list.remove(1);
        when(creativeGroupCreativeDao.getExistingCGC(anyList(), anyLong(), any(SqlSession.class))).
                thenReturn(list);

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
    }
    
    private List<String> getCGC(BulkCreativeInsertion bulkCI) {
        List<String> result = new ArrayList<>();
        for (CreativeInsertion ci : bulkCI.getCreativeInsertions()) {
            result.add(ci.getCreativeGroupId() + "_" + ci.getCreativeId());
        }
        return result;
    }

    @Test
    public void bulkCreateWithExistentInDBOk() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(
                bulkCI, countInsertions, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_NON_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
    }

    @Test
    public void bulkCreateWithNullParameters() {
        //set values
        bulkCI = null;

        // perform test
        try {
            manager.bulkCreate(bulkCI, key, campaign.getId());
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("BulkCreativeInsertion cannot be null."));
        }
    }

    @Test
    public void bulkCreateWithEmptyList() {
        //set values
        bulkCI = new BulkCreativeInsertion();
        bulkCI.setCreativeInsertions(new ArrayList<CreativeInsertion>());

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void bulkCreateWrongDataIntoPayload() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), INSERTIONS_DUPLICATED_REJECTED);
        bulkCI.getCreativeInsertions().get(5).setWeight(EntityFactory.random.nextLong());

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(ValidationCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void bulkCreateWrongCampaignIntoPayload() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), INSERTIONS_DUPLICATED_REJECTED);
        bulkCI.getCreativeInsertions().get(5).setCampaignId(EntityFactory.random.nextLong());

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(ValidationCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void bulkCreateWithWrongAccessOnCampaign() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), eq(Collections.singletonList(campaign.getId())),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(SecurityCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void bulkCreateWithWrongAccessOnGroup() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP), anyCollection(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(SecurityCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void bulkCreateWithWrongAccessOnPlacement() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT), anyCollection(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(SecurityCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void bulkCreateWithWrongAccessOnCreative() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE), anyCollection(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        AccessError error = (AccessError) result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(SecurityCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void bulkCreateWithDuplicateDataIntoPayloadError() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), INSERTIONS_DUPLICATED_REJECTED);
        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.get(anyLong(), eq(sqlSessionMock))).
                thenAnswer(getCreative(campaign.getId(), width, height));
        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_NON_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat("error is " + error, error.getCode(), instanceOf(BusinessCode.class));
        assertThat("error is " + error, error.getCode().toString(), is(BusinessCode.DUPLICATE.toString()));
    }

    @Test
    public void bulkCreateWithDuplicateDataInDBError() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        int countInsertions = 5;
        List<CreativeInsertionView> existentsInDB = prepareListCreativeInsertionView(bulkCI, countInsertions, campaign.getId(), INSERTIONS_DUPLICATED_REJECTED);
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_NON_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(BusinessCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.DUPLICATE.toString()));
    }

    @Test
    public void bulkCreateWithMixedCategoryPlacementsOnPayloadError() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);
        for (CreativeInsertion ins : bulkCI.getCreativeInsertions()) {
            ins.setPlacementId(placement.getId());
            ins.setCreativeGroupId(1L);
        }

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.EMPTY));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_MIXED));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.INVALID.toString()));
    }

    @Test
    public void bulkCreateWithMixedCategoryPlacementsOnDBNonXMLAndCreativeXMLError() throws Exception {
        //set values
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        //customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_XML));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        Error error = result.error().getErrors().get(0);
        assertThat(error.getCode(), instanceOf(ValidationCode.class));
        assertThat(error.getCode().toString(), is(BusinessCode.INVALID.toString()));
    }

    @Test
    public void bulkUpdateWithNullObjectShouldReturnEmptyObject() {
        CreativeInsertionBulkUpdate update = manager.bulkUpdate(null, key);
        assertThat(update, is(notNullValue()));
        assertThat(update.getCreativeGroups(), is(nullValue()));
        assertThat(update.getCreativeInsertions(), is(nullValue()));
    }

    @Test
    public void bulkUpdateMethodValidatesClickthroughIsUnallowedFor3rd() throws Exception {
        CreativeInsertionBulkUpdate cibu = prepareCreativeInsertionBulkUpdate(1, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        // Set creative type as 3rd for all Creatives and make sure we provide a Clickthrough
        RecordSet<CreativeInsertion> creativeInsertions = new RecordSet<>();
        List<CreativeInsertion> list = new ArrayList<>();
        for (CreativeInsertion ci : cibu.getCreativeInsertions()) {
            ci.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
            list.add(ci);
            ci.setClickthrough(Constants.DEFAULT_CLICKTHROUGH_CREATIVE);
        }
        creativeInsertions.setRecords(list);

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        // Customize mock's behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_INSERTION, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, sqlSessionMock);

        when(creativeInsertionDao.get(any(SearchCriteria.class), any(OauthKey.class),
                eq(sqlSessionMock))).thenReturn(creativeInsertions);
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                        getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_3RD));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        try {
            manager.bulkUpdate(cibu, key);
            fail("This test should throw a SystemException");
        } catch (SystemException e) {
            Map<String, Object> map = e.getProperties();
            for (Map.Entry<String, Object> entrySet : map.entrySet()) {
                Object value = entrySet.getValue();
                Errors errors = (Errors) value;
                List<Error> errorList = errors.getErrors();
                for (Error error : errorList) {
                    assertThat(error.getCode(), instanceOf(ValidationCode.class));
                    assertThat(error.getMessage(), is("3rd Creatives cannot have either primary or additional Clickthroughs."));
                }
            }
        }
    }

    @Test(expected = SystemException.class)
    public void bulkUpdateNotAllowedWhenNoAccessToCreativeInsertion() throws Exception {
        CreativeInsertionBulkUpdate bulkUpdate = EntityFactory.createCreativeInsertionBulkUpdate(1, 0);
        List<Long> ciIds = new ArrayList<>(1);
        ciIds.add(bulkUpdate.getCreativeInsertionIds().get(0));
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_INSERTION, ciIds, sqlSessionMock);

        manager.bulkUpdate(bulkUpdate, key);
    }

    @Test(expected = SystemException.class)
    public void bulkUpdateNotAllowedWhenNoAccessToCreativeGroup() throws Exception {
        CreativeInsertionBulkUpdate bulkUpdate = EntityFactory.createCreativeInsertionBulkUpdate(0, 1);
        List<Long> cgIds = new ArrayList<>(1);
        cgIds.add(bulkUpdate.getCreativeGroupIds().get(0));
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, cgIds, sqlSessionMock);
        manager.bulkUpdate(bulkUpdate, key);
    }

    @Test
    public void bulkUpdatePerformsUpdateToListOfCreativeInsertions() throws Exception {
        int countInsertions = 1500;
        int countGroups = 1200;
        CreativeInsertionBulkUpdate bulkUpdate = EntityFactory.createCreativeInsertionBulkUpdate(countInsertions, countGroups);
        List<CreativeGroup> updateListCG = bulkUpdate.getCreativeGroups();

        for (CreativeGroup toUpdate : updateListCG) {
            CreativeGroup existing = EntityFactory.createCreativeGroup();
            existing.setCampaignId(toUpdate.getCampaignId());
            existing.setId(toUpdate.getId());
            mapGroupExisting.put(existing.getId(), existing);
        }

        List<CreativeInsertion> updateList = bulkUpdate.getCreativeInsertions();

        for (CreativeInsertion toUpdate : updateList) {
            CreativeInsertion existing = EntityFactory.createCreativeInsertion();
            existing.setCampaignId(toUpdate.getCampaignId());
            existing.setCreativeGroupId(toUpdate.getCreativeGroupId());
            existing.setPlacementId(toUpdate.getPlacementId());
            existing.setCreativeId(toUpdate.getCreativeId());
            existing.setId(toUpdate.getId());
            mapInsertionsExisting.put(existing.getId(), existing);
        }

        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_INSERTION, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, sqlSessionMock);
        when(creativeGroupDao.get(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).
                thenAnswer(getGroupsByCriteriaToUpdate(mapGroupExisting));
        when(creativeInsertionDao.get(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).
                thenAnswer(getInsertionsByCriteriaToUpdate(mapInsertionsExisting));
        when(creativeInsertionDao.bulkUpdate(anyListOf(CreativeInsertion.class), eq(sqlSessionMock), eq(key))).thenReturn(updateList);

        CreativeInsertionBulkUpdate updated = manager.bulkUpdate(bulkUpdate, key);
        verify(creativeInsertionDao, times(1)).bulkUpdate(anyListOf(CreativeInsertion.class), eq(sqlSessionMock), eq(key));

        assertThat(updated.getCreativeInsertions(), is(notNullValue()));
        assertThat(updated.getCreativeInsertions(), hasSize(countInsertions));
    }

    @Test
    public void bulkUpdatePerformsUpdateToListOfCreativeGroups() throws Exception {
        CreativeInsertionBulkUpdate bulkUpdate = EntityFactory.createCreativeInsertionBulkUpdate(0, 1);
        List<CreativeGroup> updateList = bulkUpdate.getCreativeGroups();

        List<CreativeGroup> existingList = new ArrayList<>(updateList.size());

        for (CreativeGroup toUpdate : updateList) {
            CreativeGroup existing = EntityFactory.createCreativeGroup();
            existing.setCampaignId(toUpdate.getCampaignId());
            existing.setId(toUpdate.getId());
            existingList.add(existing);
        }

        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_INSERTION, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, sqlSessionMock);
        when(creativeGroupDao.get(any(SearchCriteria.class), eq(key), eq(sqlSessionMock))).thenReturn(new RecordSet<>(existingList));
        when(creativeGroupDao.bulkUpdate(anyListOf(CreativeGroup.class), eq(sqlSessionMock), eq(key))).thenReturn(updateList);

        CreativeInsertionBulkUpdate updated = manager.bulkUpdate(bulkUpdate, key);
        verify(creativeInsertionDao, times(0)).bulkUpdate(anyListOf(CreativeInsertion.class), eq(sqlSessionMock), eq(key));
        verify(creativeGroupDao, times(1)).bulkUpdate(anyListOf(CreativeGroup.class), eq(sqlSessionMock), eq(key));

        assertThat(updated.getCreativeInsertions(), is(nullValue()));
        assertThat(updated.getCreativeGroups(), is(notNullValue()));
        assertThat(updated.getCreativeGroups(), hasSize(1));
    }

    @Test
    public void deleteCreativeInsertionsBulkOk() throws Exception {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(20);
        Long campaignId = EntityFactory.random.nextLong();

        //customize mock's behavior
        when(creativeInsertionDao.bulkDeleteByFilterParam(eq(campaignId),
                any(CreativeInsertionFilterParam.class), any(String.class), eq(sqlSessionMock))).thenReturn(1);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCreativeInsertionsBulkWithNullParameters() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = null;
        Long campaignId = null;

        // perform test
        manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
    }

    @Test
    public void deleteCreativeInsertionsBulkWithEmptyRecordSet() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = new RecordSet<>(new ArrayList<CreativeInsertionFilterParam>());
        Long campaignId = EntityFactory.random.nextLong();

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.REQUIRED.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongPayload() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        filterParam.getRecords().get(0).setType(null);
        filterParam.getRecords().get(0).setPivotType(null);
        Long campaignId = EntityFactory.random.nextLong();

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongCampaignAccess() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        Long campaignId = EntityFactory.random.nextLong();

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), eq(Collections.singletonList(campaignId)),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongSiteAccess() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        Long campaignId = EntityFactory.random.nextLong();
        CreativeInsertionFilterParam filterParamSection = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SECTION);
        filterParamSection.setType(filterParamSection.getType().toLowerCase());
        filterParamSection.setPivotType(filterParamSection.getPivotType().toLowerCase());
        filterParam.getRecords().set(0, filterParamSection);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE), anyCollection(),
            eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongSectionAccess() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        Long campaignId = EntityFactory.random.nextLong();
        CreativeInsertionFilterParam filterParamSection = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SECTION);
        filterParamSection.setType(filterParamSection.getType().toLowerCase());
        filterParamSection.setPivotType(filterParamSection.getPivotType().toLowerCase());
        filterParam.getRecords().set(0, filterParamSection);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE_SECTION), anyCollection(),
            eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongPlacementAccess() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        Long campaignId = EntityFactory.random.nextLong();
        CreativeInsertionFilterParam filterParamSection = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        filterParamSection.setType(filterParamSection.getType().toLowerCase());
        filterParamSection.setPivotType(filterParamSection.getPivotType().toLowerCase());
        filterParam.getRecords().set(0, filterParamSection);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT), anyCollection(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongGroupAccess() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        Long campaignId = EntityFactory.random.nextLong();
        CreativeInsertionFilterParam filterParamSection = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.GROUP);
        filterParamSection.setType(filterParamSection.getType().toLowerCase());
        filterParamSection.setPivotType(filterParamSection.getPivotType().toLowerCase());
        filterParam.getRecords().set(0, filterParamSection);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP), anyCollection(),
            eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void deleteCreativeInsertionsBulkWithWrongCreativeAccess() {
        //set values
        RecordSet<CreativeInsertionFilterParam> filterParam = prepareCreativeInsertionFilterParams(5);
        Long campaignId = EntityFactory.random.nextLong();
        CreativeInsertionFilterParam filterParamSection = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(
                CreativeInsertionFilterParamTypeEnum.SITE, CreativeInsertionFilterParamTypeEnum.SCHEDULE);
        filterParamSection.setType(filterParamSection.getType().toLowerCase());
        filterParamSection.setPivotType(filterParamSection.getPivotType().toLowerCase());
        filterParam.getRecords().set(0, filterParamSection);

        //customize mock's behavior
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE), anyCollection(),
            eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        // perform test
        Either<Error,  String> result = manager.creativeInsertionsBulkDelete(campaignId, filterParam, key);
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
    public void bulkCreateMethodValidatesClickthroughIsUnallowedFor3rd(){
        bulkCI = prepareBulkCreativeInsertion(10, campaign.getId(), ALL_INSERTIONS_UNIQUE_ACCEPTED);

        // Set creative type as 3rd for all Creatives and make sure we provide a Clickthrough

        for(CreativeInsertion ci : bulkCI.getCreativeInsertions()) {
            ci.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        }

        List<CreativeInsertionView> existentsInDB = new ArrayList<>();
        Long width = 10L;
        Long height = 10L;

        // Customize mock's behavior
        when(creativeInsertionDao.getAllCreativeInsertionsCount(eq(campaign.getId()),
                eq(sqlSessionMock))).thenReturn(new Long(existentsInDB.size()));
        when(creativeInsertionDao.getAllCreativeInsertions(eq(campaign.getId()), anyLong(), anyLong(),
                eq(sqlSessionMock))).thenAnswer(getAllCreativeInsertions(existentsInDB));
        when(creativeInsertionDao.getCreativeClassificationByPlacementId(eq(campaign.getId()),
                anyCollection(), eq(sqlSessionMock))).thenAnswer(
                getCreativeClassificationByPlacementId(TypeClassPlacementOnDBToTest.PLACEMENT_NON_XML));

        when(creativeDao.getCreativesByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getCreatives(campaign.getId(), width, height, TypeCreativesOnDBToTest.CREATIVES_3RD));
        when(placementDao.getPlacementsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getPlacementsById(campaign.getId(), width, height));
        when(creativeGroupDao.getCreativeGroupsByIds(anyCollection(), eq(sqlSessionMock))).
                thenAnswer(getGroupsById(campaign.getId()));

        // perform test
        Either<Errors,  BulkCreativeInsertion> result = manager.bulkCreate(bulkCI, key, campaign.getId());
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error().getErrors().size(), is(10));

        for(Error error : result.error().getErrors()) {
            ValidationError ve = (ValidationError) error;
            assertThat(ve.getCode(), instanceOf(ValidationCode.class));
            assertThat(ve.getMessage(), is("3rd Creatives cannot have either primary or additional Clickthroughs."));
        }
    }

    private RecordSet<CreativeInsertionFilterParam> prepareCreativeInsertionFilterParams (int counter){
        List<CreativeInsertionFilterParam> list = EntityFactory.createCreativeInsertionFilterForTypeAndPivot(counter);
        for (CreativeInsertionFilterParam filterParam : list) {
            filterParam.setPivotType(filterParam.getPivotType().toLowerCase());
            filterParam.setType(filterParam.getType().toLowerCase());
        }
        return new RecordSet<>(list);
    }

    private BulkCreativeInsertion prepareBulkCreativeInsertion (int counter, Long campaignId, int testType){

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
                creativeInsertions.set(counterD, creativeInsertions.get(counterD-1));
                counterD +=5;
            }
        }
        result.setCreativeInsertions(creativeInsertions);
        return result;
    }
    private CreativeInsertionBulkUpdate prepareCreativeInsertionBulkUpdate (int counter, Long campaignId, int testType){
        CreativeInsertionBulkUpdate result = new CreativeInsertionBulkUpdate();
        List<CreativeInsertion> creativeInsertions = new ArrayList<>();
        CreativeInsertion insertion;
        for (int i = 0; i < counter; i++) {
            insertion = EntityFactory.createCreativeInsertion();
            insertion.setCampaignId(campaignId);
            creativeInsertions.add(insertion);
        }
        result.setCreativeInsertions(creativeInsertions);
        List<CreativeGroup> creativeGroups = new ArrayList<>();
        CreativeGroup group;
        for (int i = 0; i < counter; i++) {
            group = EntityFactory.createCreativeGroup();
            group.setCampaignId(campaignId);
            creativeGroups.add(group);
        }
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
                            if (result.size()== numberOfRecords) {
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
                                if ((i-counterIni)%freq == 0) {
                                    //duplicates
                                    int j =  (i-counterIni) / freq;
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

    private static Answer<Creative> getCreative(final Long campaignId, final Long width, final Long height) {
        return new Answer<Creative>() {
            @Override
            public Creative answer(InvocationOnMock invocation) {
                Long creativeId = (Long) invocation.getArguments()[0];
                Creative result = EntityFactory.createCreative();
                result.setCampaignId(campaignId);
                result.setId(creativeId);
                result.setWidth(width);
                result.setHeight(height);
                return result;
            }
        };
    }

    private static Answer<RecordSet<CreativeInsertion>> getInsertionsByCriteriaToUpdate(final Map<Long, CreativeInsertion> existingMap) {
        return new Answer<RecordSet<CreativeInsertion>>() {
            @Override
            public RecordSet<CreativeInsertion> answer(InvocationOnMock invocation) {
                SearchCriteria ciCriteria = (SearchCriteria) invocation.getArguments()[0];
                String criteria = ciCriteria.getQuery();

                // get ids to recover
                String delimit = ",";
                criteria = criteria.substring(7, criteria.length()-1);
                String[] idsString = criteria.split(delimit);

                List<CreativeInsertion> listInsertions = new ArrayList<>();
                for (String idString : idsString) {
                    Long id = Long.valueOf(idString.trim());
                    listInsertions.add(existingMap.get(id));
                }
                RecordSet<CreativeInsertion> result = new RecordSet<>(listInsertions);
                return result;
            }
        };
    }

    private static Answer<RecordSet<CreativeGroup>> getGroupsByCriteriaToUpdate(final Map<Long, CreativeGroup> existingMap) {
        return new Answer<RecordSet<CreativeGroup>>() {
            @Override
            public RecordSet<CreativeGroup> answer(InvocationOnMock invocation) {
                SearchCriteria ciCriteria = (SearchCriteria) invocation.getArguments()[0];
                String criteria = ciCriteria.getQuery();

                // get ids to recover
                String delimit = ",";
                criteria = criteria.substring(7, criteria.length()-1);
                String[] idsString = criteria.split(delimit);

                List<CreativeGroup> listGroups = new ArrayList<>();
                for (String idString : idsString) {
                    Long id = Long.valueOf(idString.trim());
                    listGroups.add(existingMap.get(id));
                }
                RecordSet<CreativeGroup> result = new RecordSet<>(listGroups);
                return result;
            }
        };
    }

    private static Answer<List<CreativeGroup>> getGroupsById(final Long campaignId) {
        return new Answer<List<CreativeGroup>>() {
            @Override
            public List<CreativeGroup> answer(InvocationOnMock invocation) {
                List<Long> groups;
                if (invocation.getArguments()[0] instanceof Set) {
                    Set<Long> groupsSet =  (Set<Long>) invocation.getArguments()[0];
                    groups = new ArrayList<>(groupsSet);
                } else {
                    groups = (List<Long>) invocation.getArguments()[0];
                }

                List<CreativeGroup> resultList = new ArrayList<>();
                for(Long groupId : groups) {
                    CreativeGroup result = EntityFactory.createCreativeGroup();
                    result.setCampaignId(campaignId);
                    result.setId(groupId);
                    resultList.add(result);
                }
                return resultList;
            }
        };
    }

    private static Answer<List<Creative>> getCreatives(final Long campaignId, final Long width, final Long height, final TypeCreativesOnDBToTest type) {
        return new Answer<List<Creative>>() {
            @Override
            public List<Creative> answer(InvocationOnMock invocation) {
                List<Long> creatives;
                if (invocation.getArguments()[0] instanceof Set) {
                    Set<Long> creativesSet =  (Set<Long>) invocation.getArguments()[0];
                    creatives = new ArrayList<>(creativesSet);
                } else {
                    creatives = (List<Long>) invocation.getArguments()[0];
                }

                List<Creative> resultList = new ArrayList<>();
                String creativeType = null;
                for (int i = 0; i < creatives.size(); i++) {
                    Long creativeId = creatives.get(i);
                    switch (type){
                        case CREATIVES_XML:
                            creativeType = CreativeManager.CreativeType.XML.getCreativeType();
                            break;
                        case CREATIVES_NON_XML:
                            creativeType = CreativeManager.CreativeType.JPG.getCreativeType();
                            break;
                        case CREATIVES_3RD:
                            creativeType = CreativeManager.CreativeType.TRD.getCreativeType();
                            break;
                        case CREATIVES_MIXED:
                            creativeType = (i%3)==0? CreativeManager.CreativeType.XML.getCreativeType() : CreativeManager.CreativeType.JPG.getCreativeType();
                            break;
                    }
                    Creative result = EntityFactory.createCreative();
                    result.setCampaignId(campaignId);
                    result.setId(creativeId);
                    result.setWidth(width);
                    result.setHeight(height);
                    result.setCreativeType(creativeType);
                    resultList.add(result);

                }
                return resultList;
            }
        };
    }

    private static Answer<List<Placement>> getPlacementsById(final Long campaignId, final Long width, final Long height) {
        return new Answer<List<Placement>>() {
            @Override
            public List<Placement> answer(InvocationOnMock invocation) {
                List<Long> placements;
                if (invocation.getArguments()[0] instanceof Set) {
                    Set<Long> placementsSet =  (Set<Long>) invocation.getArguments()[0];
                    placements = new ArrayList<>(placementsSet);
                } else {
                    placements = (List<Long>) invocation.getArguments()[0];
                }

                List<Placement> resultList = new ArrayList<>();
                for(Long placementId : placements) {
                    Placement result = EntityFactory.createPlacement();
                    result.setCampaignId(campaignId);
                    result.setId(placementId);
                    result.setStatus(InsertionOrderStatusEnum.ACCEPTED.getName());
                    result.setWidth(width);
                    result.setHeight(height);
                    result.setSizeName(width + "x" + height);
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
                if (invocation.getArguments()[1] instanceof Set) {
                    Set<Long> placementsSet =  (Set<Long>) invocation.getArguments()[1];
                    placements = new ArrayList<>(placementsSet);
                } else {
                    placements = (List<Long>) invocation.getArguments()[1];
                }
                
                Map<Long, CreativeManager.CreativeGlobalClassification> result = new HashMap<>();
                CreativeManager.CreativeGlobalClassification type = null;
                switch (typePlacementTest){
                    case PLACEMENT_NON_XML:
                        type = CreativeManager.CreativeGlobalClassification.NON_XML;
                        break;
                    case PLACEMENT_XML:
                        type = CreativeManager.CreativeGlobalClassification.XML;
                        break;
                    case EMPTY:
                        return result;
                }
                
                for(Long placementId : placements) {
                    result.put(placementId, type);
                }
                return result;
            }
        };
    }
}