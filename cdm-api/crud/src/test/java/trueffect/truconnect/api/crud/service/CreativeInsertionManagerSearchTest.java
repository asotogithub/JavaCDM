package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionSearchOptions;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.enums.CreativeInsertionFilterParamTypeEnum;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.AccessStatement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard.jaldin on 2/19/2016.
 */
public class CreativeInsertionManagerSearchTest extends CreativeInsertionManagerBaseTest {

    private List<CreativeInsertionView> data;

    private Long campaignId;
    private CreativeInsertionFilterParam parentIds;
    private String pattern;
    private CreativeInsertionSearchOptions searchOptions;

    @Before
    public void setUp() throws Exception {

        campaignId = Math.abs(EntityFactory.random.nextLong());
        parentIds = new CreativeInsertionFilterParam();
        searchOptions = new CreativeInsertionSearchOptions();
        searchOptions.setSite(true);
        searchOptions.setSection(true);
        searchOptions.setPlacement(true);
        searchOptions.setGroup(true);
        searchOptions.setCreative(true);
        data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CreativeInsertionView schedule = EntityFactory.createCreativeInsertionView();
            schedule.setCreativeAlias((i % 3 == 0 ? "Creative " : "") + schedule.getCreativeAlias());
            schedule.setCampaignId(campaignId);
            schedule.setCreativeGroupName((i % 2 == 0 ? "Group " : "") + schedule.getCreativeGroupName());
            data.add(schedule);
        }

        // DAC mocks
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.SITE_SECTION),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE_GROUP),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CREATIVE),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(true);

        //customize mock's behavior
        when(creativeInsertionDao.searchSchedulesLevel(
                eq(campaignId),
                eq(parentIds),
                eq(searchOptions),
                anyString(),
                anyLong(),
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(search());
        when(creativeInsertionDao.searchSchedulesLevelCount(
                eq(campaignId),
                eq(parentIds),
                eq(searchOptions),
                anyString(),
                anyLong(),
                anyLong(),
                eq(sqlSessionMock))).thenAnswer(searchCount());
    }

    @Test
    public void searchForCreativesOnSitePivotTest() {
        pattern = "Creative";
        parentIds.setPivotType("site");
        performTest(pattern, 34);
    }

    @Test
    public void searchForGroupsOnSitePivotTest() {
        pattern = "Group";
        parentIds.setPivotType("site");
        performTest(pattern, 50);
    }

    @Test
    public void searchForCreativesOnPlacementPivotTest() {
        pattern = "Creative";
        parentIds.setPivotType("placement");
        performTest(pattern, 34);
    }

    @Test
    public void searchForSitesOnPlacementPivotTest() {
        pattern = "Site";
        parentIds.setPivotType("placement");
        performTest(pattern, 0);
    }

    @Test
    public void searchForSectionsOnPlacementPivotTest() {
        pattern = "Section";
        parentIds.setPivotType("placement");
        performTest(pattern, 0);
    }

    @Test
    public void searchForGroupsOnGroupPivotTest() {
        pattern = "Group";
        parentIds.setPivotType("group");
        performTest(pattern, 50);
    }

    @Test
    public void searchForSectionsOnGroupPivotTest() {
        pattern = "Section";
        parentIds.setPivotType("group");
        performTest(pattern, 0);
    }

    @Test
    public void searchForCreativesAndGroupsOnGroupPivotTest() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setCreativeAlias(
                    (i % 3 == 0 ? "Creative " : "Item with Group face ") + data.get(i).getCreativeAlias());
        }
        pattern = "Group";
        parentIds.setPivotType("group");
        performTest(pattern, 116);
    }

    @Test
    public void searchForCreativesOnCreativePivotTest() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setCreativeAlias(
                    (i % 3 == 0 ? "Creative " : "Item with Group face ") + data.get(i).getCreativeAlias());
            data.get(i).setCreativeGroupName((i % 2 == 0 ? "Group " : "Item with unknown face") + data.get(i).getCreativeGroupName());
        }
        pattern = "Creative";
        parentIds.setPivotType("creative");
        performTest(pattern, 34);

        pattern = "Item";
        performTest(pattern, 116);
    }

    @Test
    public void searchForGroupsOnCreativePivotTest() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setCreativeGroupName((i % 2 == 0 ? "Group " : "Item with unknown face") + data.get(i).getCreativeGroupName());
        }
        pattern = "Group";
        parentIds.setPivotType("creative");
        performTest(pattern, 50);

        pattern = "Item";
        performTest(pattern, 50);
    }

    @Test
    public void searchForSectionsOnCreativePivotTest() {
        pattern = "Section";
        parentIds.setPivotType("creative");
        performTest(pattern, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchCreativeInsertionsWithNullParameters() {
        //set data
        campaignId = null;
        pattern = "Section";
        parentIds.setPivotType(CreativeInsertionFilterParamTypeEnum.SCHEDULE.toString().toLowerCase());
        
        // Perform test
        manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
    }

    @Test
    public void searchCreativeInsertionsWithoutTypeContextWrongPayload() {
        //set data
        pattern = "Section";
        parentIds.setPivotType(null);
        
        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.INVALID.toString()));
    }

    @Test
    public void searchCreativeInsertionsWithoutTypeContextWrongPatternSize() {
        //set data
        pattern = EntityFactory.faker.lorem().fixedString(Constants.SEARCH_PATTERN_MAX_LENGTH + 1);
        parentIds.setPivotType(CreativeInsertionFilterParamTypeEnum.SCHEDULE.toString().toLowerCase());

        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.INVALID.toString()));
        assertThat(result.error().getMessage(),
                is("Invalid Pattern, it supports characters up to " + Constants.SEARCH_PATTERN_MAX_LENGTH + "."));
    }

    @Test
    public void searchCreativeInsertionsWithTypeContextWrongPayload() {
        //set data
        pattern = "Section";
        parentIds = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(CreativeInsertionFilterParamTypeEnum.SITE, 
                CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        parentIds.setPivotType(parentIds.getPivotType().toLowerCase());
        parentIds.setType(parentIds.getType().toLowerCase());
        parentIds.setPlacementId(null);
        
        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getCode(), instanceOf(ValidationCode.class));
        assertThat(result.error().getCode().toString(), is(ValidationCode.INVALID.toString()));        
    }

    @Test
    public void searchCreativeInsertionsWithWrongAccessForCampaign() {
        //set data
        pattern = "Section";
        parentIds.setPivotType(CreativeInsertionFilterParamTypeEnum.CREATIVE.toString().toLowerCase());
        
        // mock behaviors
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);
        
        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }

    @Test
    public void searchCreativeInsertionsWithTypeContextAndWrongAccessForIds() {
        //set data
        pattern = "Section";
        parentIds = EntityFactory.createCreativeInsertionFilterForPivotAndLevelTypes(CreativeInsertionFilterParamTypeEnum.SITE, 
                CreativeInsertionFilterParamTypeEnum.PLACEMENT);
        parentIds.setPivotType(parentIds.getPivotType().toLowerCase());
        parentIds.setType(parentIds.getType().toLowerCase());        

        // mock behaviors
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.PLACEMENT),
                anyList(), eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);        
        
        // Perform test
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getCode(), instanceOf(SecurityCode.class));
        assertThat(result.error().getCode().toString(), is(SecurityCode.NOT_FOUND_FOR_USER.toString()));
    }
    
    private void performTest(String pattern, int numberOfRecordsExpected) {
        Either<Error, RecordSet<CreativeInsertionView>> result = manager.searchCreativeInsertions(campaignId, parentIds, searchOptions, pattern, 0L, 1000L, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getTotalNumberOfRecords(), is(numberOfRecordsExpected));
    }

    private Answer<List<CreativeInsertionView>> search() {
        return new Answer<List<CreativeInsertionView>>() {
            @Override
            public List<CreativeInsertionView> answer(InvocationOnMock invocation) {
                Long campaignId = (Long) invocation.getArguments()[0];
                CreativeInsertionFilterParam parentIds = (CreativeInsertionFilterParam) invocation.getArguments()[1];
                CreativeInsertionSearchOptions searchOptions = (CreativeInsertionSearchOptions) invocation.getArguments()[2];
                String pattern = (String) invocation.getArguments()[3];
                pattern = pattern.substring(1, pattern.length() - 1);
                List<CreativeInsertionView> result = new ArrayList<>();

                for (CreativeInsertionView schedule : data) {
                    if(schedule.getCampaignId().longValue() == campaignId.longValue()) {
                        if ("site".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isSite() && schedule.getSiteName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isSection() && schedule.getSiteSectionName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result.add(schedule);
                            }
                        } else if ("placement".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result.add(schedule);
                            }
                        } else if ("group".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isSite() && schedule.getSiteName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result.add(schedule);
                            }
                        } else if ("creative".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isSite() && schedule.getSiteName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result.add(schedule);
                            }
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result.add(schedule);
                            }
                        }
                    }
                }
                return result;
            }
        };
    }

    private Answer<Long> searchCount() {
        return new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) {
                Long campaignId = (Long) invocation.getArguments()[0];
                CreativeInsertionFilterParam parentIds = (CreativeInsertionFilterParam) invocation.getArguments()[1];
                CreativeInsertionSearchOptions searchOptions = (CreativeInsertionSearchOptions) invocation.getArguments()[2];
                String pattern = (String) invocation.getArguments()[3];
                pattern = pattern.substring(1, pattern.length() - 1);
                Long result = 0L;

                for (CreativeInsertionView schedule : data) {
                    if(schedule.getCampaignId().longValue() == campaignId.longValue()) {
                        if ("site".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isSite() && schedule.getSiteName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isSection() && schedule.getSiteSectionName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result++;
                            }
                        } else if ("placement".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result++;
                            }
                        } else if ("group".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isSite() && schedule.getSiteName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result++;
                            }
                        } else if ("creative".equalsIgnoreCase(parentIds.getPivotType())) {
                            if (searchOptions.isCreative() && schedule.getCreativeAlias().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isSite() && schedule.getSiteName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isPlacement() && schedule.getPlacementName().contains(pattern)) {
                                result++;
                            }
                            if (searchOptions.isGroup() && schedule.getCreativeGroupName().contains(pattern)) {
                                result++;
                            }
                        }
                    }
                }
                return result;
            }
        };
    }
}
