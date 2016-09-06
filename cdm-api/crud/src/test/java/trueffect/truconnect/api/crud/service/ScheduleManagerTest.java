package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import trueffect.truconnect.api.commons.model.Schedule;
import trueffect.truconnect.api.commons.model.ScheduleSet;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.enums.InsertionOrderStatusEnum;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupCreativeDao;
import trueffect.truconnect.api.crud.dao.CreativeGroupDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;
import trueffect.truconnect.api.crud.dao.ExtendedPropertiesDao;
import trueffect.truconnect.api.crud.dao.PlacementDao;
import trueffect.truconnect.api.crud.dao.UserDao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marleny.patsi
 */
public class ScheduleManagerTest extends AbstractManagerTest {

    private ScheduleManager scheduleManager;
    private CreativeInsertionDao creativeInsertionDao;
    private PlacementDao placementDao;
    private CreativeDao creativeDao;
    private ExtendedPropertiesDao extendedPropertiesDao;
    private CreativeGroupDao creativeGroupDao;
    private CreativeGroupCreativeDao creativeGroupCreativeDao;
    private CampaignDao campaignDao;
    private UserDao userDao;

    private ScheduleSet scheduleSet;
    private static Long campaignId;
    private static Placement placement;
    private Map<Long, List<CreativeInsertion>> dataInsertionsByGroupMap;
    private Map<Long, CreativeInsertion> dataInsertionsByIdMap;

    private static enum TypeAccessOnCreativesToTest {

        ALL_CREATIVES_FROM_GROUP_CAMPAIGN,
        CREATIVES_FROM_OTHER_CAMPAIGN
    };

    private static enum TypeCreativesToTest {

        CREATIVES_NON_3RD,
        CREATIVES_3RD
    };

    @Before
    public void init() throws Exception {

        // mock Dao objects
        creativeInsertionDao = mock(CreativeInsertionDao.class);
        placementDao = mock(PlacementDao.class);
        creativeDao = mock(CreativeDao.class);
        extendedPropertiesDao = mock(ExtendedPropertiesDao.class);
        creativeGroupDao = mock(CreativeGroupDao.class);
        creativeGroupCreativeDao = mock(CreativeGroupCreativeDao.class);
        campaignDao = mock(CampaignDao.class);
        userDao = mock(UserDao.class);

        // manager
        scheduleManager = new ScheduleManager(creativeInsertionDao, placementDao, creativeDao,
                extendedPropertiesDao, creativeGroupDao, creativeGroupCreativeDao, campaignDao,
                userDao, accessControlMockito);

        // variables
        scheduleSet = null;
        dataInsertionsByGroupMap = null;
        dataInsertionsByIdMap = null;
        campaignId = Math.abs(EntityFactory.random.nextLong());
        placement = getPlacement(campaignId);

        // Mocks Session behavior
        when(creativeInsertionDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeInsertionDao).close(eq(sqlSessionMock));
        doNothing().when(creativeInsertionDao).commit(eq(sqlSessionMock));
        doNothing().when(creativeInsertionDao).rollback(eq(sqlSessionMock));
        when(creativeDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeDao).close(eq(sqlSessionMock));
        when(creativeGroupCreativeDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(creativeGroupCreativeDao).close(eq(sqlSessionMock));
        when(placementDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(placementDao).close(eq(sqlSessionMock));

        // Mocks DAC behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_GROUP, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE_INSERTION, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CAMPAIGN, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.CREATIVE, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito, AccessStatement.PLACEMENT, sqlSessionMock);

        doNothing().when(creativeInsertionDao).delete(anyLong(), eq(key), eq(sqlSessionMock));
        doNothing().when(creativeGroupCreativeDao).remove(anyLong(), anyLong(), eq(sqlSessionMock));

        when(creativeGroupCreativeDao.getCreativeGroupCreativesByCG(anyLong(), eq(key), eq(sqlSessionMock))).
                thenReturn(new ArrayList<CreativeGroupCreative>());
        when(placementDao.get(anyLong(), eq(sqlSessionMock))).
                thenReturn(placement);
    }

    @Test
    public void updateSchedulesNominalCaseOk() throws Exception {
        // set data
        int countRecords = 10;
        scheduleSet = prepareDataToTest(countRecords, TypeCreativesToTest.CREATIVES_NON_3RD);

        // mock behaviors
        when(creativeInsertionDao.getCreativeInsertionsByGroupId(anyLong(), eq(key), eq(sqlSessionMock))).
                thenAnswer(getCreativeInsertionsByGroupId(dataInsertionsByGroupMap));
        when(creativeGroupCreativeDao.save(any(CreativeGroupCreative.class), eq(sqlSessionMock))).
                thenAnswer(createCretiveGroupCreative());
        when(creativeInsertionDao.getCampaingIdByGreativeGroupId(anyLong())).thenReturn(campaignId);

        when(creativeGroupDao.get(anyLong(), eq(key), eq(sqlSessionMock))).
                thenAnswer(getCreativeGroup(campaignId));
        when(creativeDao.getCountCreativesByCampaignIdAndIds(anyList(), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getCountCreativesByCampaignId(TypeAccessOnCreativesToTest.ALL_CREATIVES_FROM_GROUP_CAMPAIGN));
        when(creativeDao.getCreativesByIds(anyList(), eq(sqlSessionMock))).
                thenAnswer(getCreatives());
        when(placementDao.getPlacementsByIds(anyList(), eq(sqlSessionMock))).
                thenAnswer(getPlacements());
        when(creativeGroupDao.getCreativeGroupsByIds(anyList(), eq(sqlSessionMock))).
                thenAnswer(getCreativeGroups());

        when(creativeInsertionDao.get(anyLong(), eq(sqlSessionMock))).
                thenAnswer(getCreativeInsertionsById(dataInsertionsByIdMap));
        when(creativeDao.get(anyLong(), eq(sqlSessionMock))).
                thenReturn(getCreative(campaignId, placement.getWidth(), placement.getHeight(), TypeCreativesToTest.CREATIVES_NON_3RD));
        when(creativeInsertionDao.update(any(CreativeInsertion.class), eq(sqlSessionMock), eq(key))).
                thenAnswer(updateInsertion());
        when(creativeInsertionDao.create(any(CreativeInsertion.class), any(SqlSession.class))).
                thenReturn(EntityFactory.createCreativeInsertion());

        // perform test
        ScheduleSet result = scheduleManager.updateSchedules(scheduleSet, key);
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void updateSchedulesNominalWithClickthroughIsUnallowedFor3rd() throws Exception {
        // set data
        int countRecords = 10;
        scheduleSet = prepareDataToTest(countRecords, TypeCreativesToTest.CREATIVES_3RD);

        // mock behaviors
        when(creativeInsertionDao.getCreativeInsertionsByGroupId(anyLong(), eq(key), eq(sqlSessionMock))).
                thenAnswer(getCreativeInsertionsByGroupId(dataInsertionsByGroupMap));
        when(creativeGroupCreativeDao.save(any(CreativeGroupCreative.class), eq(sqlSessionMock))).
                thenAnswer(createCretiveGroupCreative());
        when(creativeInsertionDao.getCampaingIdByGreativeGroupId(anyLong())).thenReturn(campaignId);

        when(creativeGroupDao.get(anyLong(), eq(key), eq(sqlSessionMock))).
                thenAnswer(getCreativeGroup(campaignId));
        when(creativeDao.getCountCreativesByCampaignIdAndIds(anyList(), anyLong(), eq(sqlSessionMock))).
                thenAnswer(getCountCreativesByCampaignId(TypeAccessOnCreativesToTest.ALL_CREATIVES_FROM_GROUP_CAMPAIGN));
        when(creativeDao.getCreativesByIds(anyList(), eq(sqlSessionMock))).
                thenAnswer(getCreatives());
        when(placementDao.getPlacementsByIds(anyList(), eq(sqlSessionMock))).
                thenAnswer(getPlacements());
        when(creativeGroupDao.getCreativeGroupsByIds(anyList(), eq(sqlSessionMock))).
                thenAnswer(getCreativeGroups());
        when(creativeInsertionDao.get(anyLong(), eq(sqlSessionMock))).
                thenAnswer(getCreativeInsertionsById(dataInsertionsByIdMap));
        when(creativeDao.get(anyLong(), eq(sqlSessionMock))).
                thenReturn(getCreative(campaignId, placement.getWidth(), placement.getHeight(), TypeCreativesToTest.CREATIVES_3RD));
        when(creativeInsertionDao.update(any(CreativeInsertion.class), eq(sqlSessionMock), eq(key))).
                thenAnswer(updateInsertion());
        when(creativeInsertionDao.create(any(CreativeInsertion.class), eq(sqlSessionMock))).
                thenReturn(EntityFactory.createCreativeInsertion());

        // perform test
        try {
            scheduleManager.updateSchedules(scheduleSet, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Clickthrough should be empty to update 3rd type Creative or CreativeInsertion."));
        }
    }

    private ScheduleSet prepareDataToTest(int countRecords, TypeCreativesToTest typeCreatives){
        List<Schedule> schedules = new ArrayList<>();
        List<CreativeInsertion> insertions = new ArrayList<>();
        dataInsertionsByGroupMap = new HashMap<>();
        dataInsertionsByIdMap = new HashMap<>();
        // data on db
        for (int i = 0; i < countRecords + 10; i++) {
            CreativeInsertion insertion = EntityFactory.createCreativeInsertion();
            insertion.setCampaignId(campaignId);
            insertion.setTimeZone(Constants.DEFAULT_TIMEZONE);
            if (i > 0 && i % 3 == 0) {
                insertion.setCreativeGroupId(insertions.get(i - 1).getCreativeGroupId());
            }
            if (i != 0) {
                insertion.setId(null);
            }
            insertions.add(insertion);
        }
        String creativeType = "";
        switch (typeCreatives) {
            case CREATIVES_NON_3RD:
                creativeType = CreativeManager.CreativeType.JPG.getCreativeType();
                break;
            case CREATIVES_3RD:
                creativeType = CreativeManager.CreativeType.TRD.getCreativeType();
                break;
        }
        // set data on map and result
        for (CreativeInsertion insertion : insertions) {
            insertion.setCreativeType(creativeType);
            Schedule schedule = EntityFactory.createSchedule(insertion.getId(), insertion.getPlacementId(),
                    insertion.getCreativeGroupId(), insertion.getCreativeId());
            schedules.add(schedule);

            if (!dataInsertionsByGroupMap.containsKey(insertion.getCreativeGroupId())) {
                dataInsertionsByGroupMap.put(insertion.getCreativeGroupId(), new ArrayList<CreativeInsertion>());
            }
            dataInsertionsByGroupMap.get(insertion.getCreativeGroupId()).add(insertion);
            dataInsertionsByIdMap.put(insertion.getId(), insertion);
        }

        ScheduleSet result = new ScheduleSet();
        result.setSchedules(schedules);
        return result;
    }

    private static Answer<List<CreativeInsertion>> getCreativeInsertionsByGroupId(final Map<Long, List<CreativeInsertion>> dataInsertionsMap) {
        return new Answer<List<CreativeInsertion>>() {
            @Override
            public List<CreativeInsertion> answer(InvocationOnMock invocation) {
                Long groupId = (Long) invocation.getArguments()[0];
                List<CreativeInsertion> result = dataInsertionsMap.get(groupId);
                return result;
            }
        };
    }

    private static Answer<List<Creative>> getCreatives() {
        return new Answer<List<Creative>>() {
            @Override
            public List<Creative> answer(InvocationOnMock invocation) {
                List<Long> creativeIds = (List<Long>) invocation.getArguments()[0];
                List<Creative> result = EntityFactory.createCreativesFromIds(creativeIds, campaignId);
                for (Creative creative : result) {
                    creative.setWidth(placement.getWidth());
                    creative.setHeight(placement.getHeight());
                }
                return result;
            }
        };
    }

    private static Answer<List<Placement>> getPlacements() {
        return new Answer<List<Placement>>() {
            @Override
            public List<Placement> answer(InvocationOnMock invocation) {
                List<Long> placementIds = (List<Long>) invocation.getArguments()[0];
                List<Placement> result = EntityFactory.createPlacementsFromIds(placementIds, campaignId);
                return result;
            }
        };
    }

    private static Answer<List<CreativeGroup>> getCreativeGroups() {
        return new Answer<List<CreativeGroup>>() {
            @Override
            public List<CreativeGroup> answer(InvocationOnMock invocation) {
                List<Long> creativeGroupIds = (List<Long>) invocation.getArguments()[0];
                List<CreativeGroup> result = EntityFactory.createCreativeGroupsFromIds(creativeGroupIds, campaignId);
                return result;
            }
        };
    }

    private static Answer<CreativeInsertion> getCreativeInsertionsById(final Map<Long, CreativeInsertion> dataInsertionsMap) {
        return new Answer<CreativeInsertion>() {
            @Override
            public CreativeInsertion answer(InvocationOnMock invocation) {
                Long insertionId = (Long) invocation.getArguments()[0];
                CreativeInsertion result = dataInsertionsMap.get(insertionId);
                return result;
            }
        };
    }

    private static Answer<CreativeGroup> getCreativeGroup(final Long campaignId) {
        return new Answer<CreativeGroup>() {
            @Override
            public CreativeGroup answer(InvocationOnMock invocation) {
                Long groupId = (Long) invocation.getArguments()[0];
                CreativeGroup result = EntityFactory.createCreativeGroup();
                result.setCampaignId(campaignId);
                return result;
            }
        };
    }

    private Placement getPlacement(Long campaignId) {
        Placement result = EntityFactory.createPlacement();
        result.setCampaignId(campaignId);
        result.setStatus(InsertionOrderStatusEnum.ACCEPTED.getName());
        return result;
    }

    private Creative getCreative(Long campaignId, Long width, Long height, TypeCreativesToTest typeCreatives) {
        Creative result = EntityFactory.createCreative();
        result.setCampaignId(campaignId);
        result.setWidth(width);
        result.setHeight(height);
        String creativeType = "";
        switch (typeCreatives) {
            case CREATIVES_NON_3RD:
                creativeType = CreativeManager.CreativeType.JPG.getCreativeType();
                break;
            case CREATIVES_3RD:
                creativeType = CreativeManager.CreativeType.TRD.getCreativeType();
                break;
        }
        result.setCreativeType(creativeType);
        return result;
    }

    private static Answer<Long> getCountCreativesByCampaignId(final TypeAccessOnCreativesToTest typeAccess) {
        return new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) {
                List<Long> creativeIds = (List<Long>) invocation.getArguments()[0];
                Long campaignId = (Long) invocation.getArguments()[1];
                Long result = 0L;
                switch (typeAccess) {
                    case ALL_CREATIVES_FROM_GROUP_CAMPAIGN:
                        result = Long.valueOf(creativeIds.size());
                        break;
                    case CREATIVES_FROM_OTHER_CAMPAIGN:
                        result = Long.valueOf(creativeIds.size()) - 1;
                        break;
                }
                return result;
            }
        };
    }

    private static Answer<CreativeInsertion> updateInsertion() {
        return new Answer<CreativeInsertion>() {
            @Override
            public CreativeInsertion answer(InvocationOnMock invocation) {
                CreativeInsertion result = (CreativeInsertion) invocation.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                result.setLogicalDelete("N");
                return result;
            }
        };
    }

    private static Answer<CreativeGroupCreative> createCretiveGroupCreative() {
        return new Answer<CreativeGroupCreative>() {
            @Override
            public CreativeGroupCreative answer(InvocationOnMock invocation) {
                //doNothing().when(creativeGroupCreativeDao).create(any(CreativeGroupCreative.class), eq(sqlSessionMock));
                CreativeGroupCreative result = (CreativeGroupCreative) invocation.getArguments()[0];
                result.setLogicalDelete("N");
                return result;
            }
        };
    }
}
