package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.BusinessError;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CreativeAssociationsDTO;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo Claure
 * @author Marcelo Heredia
 */
public class CreativeManagerTest extends CreativeManagerBaseTest {



    private static Map<Long, List<Long>> associatedCreatives; //(creative id, group id)

    private String creativeFilename;
    private String sourceFilePath;



    @Before
    public void managerInit() throws Exception {

        ArrayList<CreativeGroupCreative> cgcList = new ArrayList<>();
        CreativeGroupCreative cgc = new CreativeGroupCreative();
        cgc.setCreativeGroupId(1L);
        cgc.setCreativeGroupName("Group1");
        cgcList.add(cgc);
        cgc = new CreativeGroupCreative();
        cgc.setCreativeGroupId(2L);
        cgc.setCreativeGroupName("Group2");
        cgcList.add(cgc);


        associatedCreatives = new HashMap<>();

        // Mocks common behavior

        when(creativeGroupCreativeDao.getByCreative(anyLong(),
                any(SqlSession.class))).thenReturn(cgcList);
        doNothing().when(creativeDao).update(any(Creative.class), eq(key), any(SqlSession.class));

        when(creativeGroupCreativeDao.save(any(CreativeGroupCreative.class),
                any(SqlSession.class))).thenReturn(new CreativeGroupCreative());
        doNothing().when(creativeDao).removeCreativeClickThrough(any(Long.class), eq(key), any(SqlSession.class));

        when(creativeDao.getCreativeIdByCampaignIdAndFileName(anyLong(), any(String.class), eq(sqlSessionMock))).thenReturn(null);
        // customize mock behavior

    }

    @Test
    public void testSaveCreative() throws Exception {
        // set data
        creative.setId(null);
        String filename = creative.getFilename();
        
        // perform test
        creative = creativeManager.saveCreative(creative, key);
        assertThat(creative, is(notNullValue()));
        assertThat(filename, is(equalTo(creative.getFilename())));
    }

    @Test
    public void testGetCreative() throws Exception {
        // set data
        creative.setId(null);
        creative = creativeManager.saveCreative(creative, key);
        assertThat(creative, is(notNullValue()));
        Long creativeId = creative.getId();
        String alias = creative.getAlias();
        String purpose = creative.getPurpose();
        
        // perform test
        Creative result = creativeManager.getCreative(creativeId, key);
        assertThat(result, is(notNullValue()));
        assertThat(alias, is(equalTo(result.getAlias())));
        assertThat(purpose, is(equalTo(result.getPurpose())));
        assertThat(result.getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }

    @Test
    public void testGetCreatives() throws Exception {
        String criteria = "id inside [";
        int numberOfRecords = 3;
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        for (int i = 0; i < numberOfRecords; i++) {
            creative = EntityFactory.createCreative();
            creative.setId(null);
            creative.setAgencyId(agencyId);
            creative.setCampaignId(campaignId);

            creative = creativeManager.saveCreative(creative, key);
            assertThat(creative, is(notNullValue()));
            criteria += creative.getId() + ",";
        }

        when(creativeDao.getCreatives(any(SearchCriteria.class), eq(key),
                any(SqlSession.class))).thenReturn(
                new RecordSet<Creative>(0, 1000, existingCreatives.size(),
                        new ArrayList<Creative>(existingCreatives.values())));

        // Remove last comma
        criteria = criteria.substring(0, criteria.length() - 1) + "]";

        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setQuery(criteria);

        searchCriteria.setPageSize(numberOfRecords);
        RecordSet<Creative> result = creativeManager.getCreatives(searchCriteria, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getRecords().size(), is(equalTo(existingCreatives.size())));
        assertThat(result.getRecords().get(0).getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }

    @Test
    public void testGetCreativesFullyUnassociated() throws Exception {
        Long groupId = Math.abs(EntityFactory.random.nextLong());
        int numberOfExpectedRecords = 2;
        //Associate
        int index = 0;
        for (Map.Entry<Long, Creative> entry : existingCreatives.entrySet()) {
            if(index % 2 == 0){
                if(associatedCreatives.get(entry.getValue().getId()) == null) {
                    associatedCreatives.put(entry.getValue().getId(), new ArrayList<Long>());
                }
                associatedCreatives.get(entry.getValue().getId()).add(groupId);
            }
            index++;
        }

        when(creativeDao.getCreativesWithNoGroupAssociation(
                eq(campaign.getId()),
                eq(groupId),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenAnswer(getCreatives());
        when(creativeDao.getCountForCreativesWithNoGroupAssociation(
                eq(campaign.getId()),
                eq(groupId),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenReturn(new Long(numberOfExpectedRecords));

        Either<Error, RecordSet<Creative>> result = creativeManager.getCreativesWithNoGroupAssociation(
                campaign.getId(),
                groupId,
                null,
                null,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(numberOfExpectedRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(numberOfExpectedRecords)));
    }

    @Test
    public void testGetCreativesPartiallyUnassociated() throws Exception {
        Long fstGroupId = Math.abs(EntityFactory.random.nextLong());
        Long sndGroupId = Math.abs(EntityFactory.random.nextLong());
        int numberOfExpectedRecords = 4;
        //Associate
        int index = 0;
        for (Map.Entry<Long, Creative> entry : existingCreatives.entrySet()) {
            if(associatedCreatives.get(entry.getValue().getId()) == null) {
                associatedCreatives.put(entry.getValue().getId(), new ArrayList<Long>());
            }
            if(index == 0){
                associatedCreatives.get(entry.getValue().getId()).add(fstGroupId);
                associatedCreatives.get(entry.getValue().getId()).add(sndGroupId);
            } else if(index == 1) {
                associatedCreatives.get(entry.getValue().getId()).add(sndGroupId);
            }
            index++;
        }

        when(creativeDao.getCreativesWithNoGroupAssociation(
                eq(campaign.getId()),
                eq(fstGroupId),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenAnswer(getCreatives());
        when(creativeDao.getCountForCreativesWithNoGroupAssociation(
                eq(campaign.getId()),
                eq(fstGroupId),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenReturn(new Long(numberOfExpectedRecords));

        Either<Error, RecordSet<Creative>> result = creativeManager.getCreativesWithNoGroupAssociation(
                campaign.getId(),
                fstGroupId,
                null,
                null,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(numberOfExpectedRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(numberOfExpectedRecords)));
    }

    @Test
    public void testGetCreativesFullyAssociated() throws Exception {
        Long fstGroupId = Math.abs(EntityFactory.random.nextLong());
        Long sndGroupId = Math.abs(EntityFactory.random.nextLong());
        int numberOfExpectedRecords = 0;
        //Associate all creatives with all groups
        int index = 0;
        for (Map.Entry<Long, Creative> entry : existingCreatives.entrySet()) {
            if(associatedCreatives.get(entry.getValue().getId()) == null) {
                associatedCreatives.put(entry.getValue().getId(), new ArrayList<Long>());
            }
            associatedCreatives.get(entry.getValue().getId()).add(fstGroupId);
            associatedCreatives.get(entry.getValue().getId()).add(sndGroupId);
            index++;
        }

        when(creativeDao.getCreativesWithNoGroupAssociation(
                eq(campaign.getId()),
                eq(fstGroupId),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenAnswer(getCreatives());
        when(creativeDao.getCountForCreativesWithNoGroupAssociation(
                eq(campaign.getId()),
                eq(fstGroupId),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenReturn(new Long(numberOfExpectedRecords));

        Either<Error, RecordSet<Creative>> result = creativeManager.getCreativesWithNoGroupAssociation(
                campaign.getId(),
                fstGroupId,
                null,
                null,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(numberOfExpectedRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(numberOfExpectedRecords)));

        result = creativeManager.getCreativesWithNoGroupAssociation(
                campaign.getId(),
                sndGroupId,
                null,
                null,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(numberOfExpectedRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(numberOfExpectedRecords)));
    }

    @Test
    public void testGetCreativesByCampaign() throws Exception {
        int numberOfExpectedRecords = 5;

        when(creativeDao.getCreativesByCampaign(
                eq(campaign.getId()),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenAnswer(getCreatives());
        when(creativeDao.getCountForCreativesByCampaign(
                eq(campaign.getId()),
                anyLong(),
                anyLong(),
                any(SqlSession.class))).thenReturn(new Long(numberOfExpectedRecords));

        Either<Error, RecordSet<Creative>> result = creativeManager.getCreativesByCampaign(
                campaign.getId(),
                null,
                null,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(numberOfExpectedRecords)));
        assertThat(result.success().getTotalNumberOfRecords(), is(equalTo(numberOfExpectedRecords)));
    }

    @Test
    public void testGetCreativesByCampaignWithWrongId() throws Exception {
        when(accessControlMockito.isUserValidFor(eq(AccessStatement.CAMPAIGN), anyList(),
                eq(key.getUserId()), eq(sqlSessionMock))).thenReturn(false);

        Either<Error, RecordSet<Creative>> result = creativeManager.getCreativesByCampaign(
                campaign.getId(),
                null,
                null,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getCode().getNumber(), is(SecurityCode.NOT_FOUND_FOR_USER.getNumber()));
        assertThat(result.error().getMessage(),
                is("The user is not allowed in this context or the requested entity does not exist"));
    }

    @Test
    public void getScheduleAssocPerGroupByCreativeIdPass() {
        // Prepare data
        List<CreativeAssociationsDTO> list = new ArrayList<>();
        CreativeAssociationsDTO assoc = new CreativeAssociationsDTO();
        assoc.setGroupId(Math.abs(EntityFactory.random.nextLong()));
        assoc.setSchedules(Math.abs(EntityFactory.random.nextLong()));
        list.add(assoc);
        assoc = new CreativeAssociationsDTO();
        assoc.setGroupId(Math.abs(EntityFactory.random.nextLong()));
        assoc.setSchedules(Math.abs(EntityFactory.random.nextLong()));
        list.add(assoc);

        // customize mock's behavior
        when(creativeDao.getScheduleAssocPerGroupByCreativeId(any(Long.class),
                any(SqlSession.class))).thenReturn(list);

        // perform test
        Either<Errors, RecordSet<CreativeAssociationsDTO>> result =
                creativeManager.getScheduleAssocPerGroupByCreativeId(creative.getId(), key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success().getRecords().size(), is(equalTo(list.size())));
    }

    @Test
    public void getScheduleAssocPerGroupByCreativeIdFailedDueNullId() {
        try {
            creativeManager.getScheduleAssocPerGroupByCreativeId(null, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is(equalTo(String.format("%s cannot be null.", "Creative Id"))));
        }
    }

    @Test
    public void getScheduleAssocPerGroupByCreativeIdPassFailedDueWrongAccess() {
        // customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE, sqlSessionMock);

        // perform test
        Either<Errors, RecordSet<CreativeAssociationsDTO>> result =
                creativeManager.getScheduleAssocPerGroupByCreativeId(creative.getId(), key);
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
    public void testRemoveCreative() throws Exception {
        // set data
        creative.setId(null);
        // Create a new creative
        creative = creativeManager.saveCreative(creative, key);
        assertThat(creative, is(notNullValue()));
        Long creativeId = creative.getId();
        
        // perform test
        creativeManager.removeCreative(creativeId, key, false);
    }

    @Test
    public void testRemoveCreatives() throws Exception {
        // set data
        RecordSet<Creative> input = new RecordSet<>();
        List<Creative> records = new ArrayList<>();
        
        Long campaignId = Math.abs(EntityFactory.random.nextLong());
        Long agencyId = Math.abs(EntityFactory.random.nextLong());
        for (int i = 0; i < 5; i++) {
            creative = EntityFactory.createCreative();
            creative.setId(null);
            creative.setAgencyId(agencyId);
            creative.setCampaignId(campaignId);

            Creative record = creativeManager.saveCreative(creative, key);
            assertThat(record, is(notNullValue()));
            records.add(record);
        }
        input.setRecords(records);

        // perform test
        SuccessResponse result = creativeManager.removeCreatives(input, key);
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testRemoveCreativeWithNoAssociationsPass()
            throws Exception {
        // prepare data
        List<CreativeGroupCreative> cgcRecords = new ArrayList<>();
        cgcRecords.add(new CreativeGroupCreative(Math.abs(EntityFactory.random.nextLong()),
                creative.getId(), tpws, tpws));

        // customize mock's behavior
        when(creativeInsertionDao
                .getScheduleCountByCreativeIds(any(SearchCriteria.class), any(String.class),
                        any(SqlSession.class))).thenReturn(0L);
        when(creativeGroupCreativeDao.getByCreative(anyLong(), eq(sqlSessionMock)))
                .thenReturn(
                        cgcRecords);

        // perform test
        creativeManager.removeCreative(creative.getId(), key, false);
        verify(creativeInsertionDao, times(1))
                .getScheduleCountByCreativeIds(any(SearchCriteria.class), any(String.class),
                        any(SqlSession.class));
        verify(creativeDao, times(1)).remove(anyLong(), eq(key), any(SqlSession.class));
    }

    @Test
    public void testRemoveCreativeWithAssociations() throws Exception {
        // prepare data

        // customize mock's behavior
        when(creativeInsertionDao
                .getScheduleCountByCreativeIds(any(SearchCriteria.class), eq(key.getUserId()),
                        any(SqlSession.class))).thenReturn(1L);

        // perform test
        try {
            creativeManager.removeCreative(creative.getId(), key, false);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(), is(String
                    .format("Creative %s could not be deleted because it is already scheduled.",
                            creative.getId())));
        }
    }

    @Test
    public void testRemoveCreativesWithSomeGroupsAssociatedNoSchedules() throws Exception {
        Long fstGroupId = Math.abs(EntityFactory.random.nextLong());
        Long sndGroupId = Math.abs(EntityFactory.random.nextLong());
        Long trdGroupId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Long> groupIds = new RecordSet<>();
        groupIds.setRecords(Arrays.asList(fstGroupId, sndGroupId));

        associatedCreatives.put(creative.getId(), new ArrayList<Long>());
        associatedCreatives.get(creative.getId()).add(fstGroupId);
        associatedCreatives.get(creative.getId()).add(sndGroupId);
        associatedCreatives.get(creative.getId()).add(trdGroupId);

        List<CreativeGroupCreative> cgcRecords = new ArrayList<>();
        cgcRecords.add(new CreativeGroupCreative(fstGroupId, creative.getId(), tpws, tpws));
        cgcRecords.add(new CreativeGroupCreative(sndGroupId, creative.getId(), tpws, tpws));
        cgcRecords.add(new CreativeGroupCreative(trdGroupId, creative.getId(), tpws, tpws));

        when(creativeInsertionDao
                .getScheduleCountByCreativeIds(any(SearchCriteria.class), any(String.class),
                        any(SqlSession.class))).thenReturn(0L);
        when(creativeGroupCreativeDao.getByCreative(anyLong(), eq(sqlSessionMock)))
                .thenReturn(cgcRecords);

        Either<Errors, SuccessResponse> result = creativeManager.removeCreative(
                creative.getId(), groupIds, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(), is(equalTo(
                "Creative " + creative.getId() + " successfully deleted from groups " + groupIds
                        .getRecords().toString() + ".")));
    }

    @Test
    public void testRemoveCreativesWithAllGroupsAssociatedNoSchedules() throws Exception {
        Long fstGroupId = Math.abs(EntityFactory.random.nextLong());
        Long sndGroupId = Math.abs(EntityFactory.random.nextLong());
        Long trdGroupId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Long> groupIds = new RecordSet<>();
        groupIds.setRecords(Arrays.asList(fstGroupId, sndGroupId, trdGroupId));

        associatedCreatives.put(creative.getId(), new ArrayList<Long>());
        associatedCreatives.get(creative.getId()).add(fstGroupId);
        associatedCreatives.get(creative.getId()).add(sndGroupId);
        associatedCreatives.get(creative.getId()).add(trdGroupId);

        List<CreativeGroupCreative> cgcRecords = new ArrayList<>();
        cgcRecords.add(new CreativeGroupCreative(fstGroupId, creative.getId(), tpws, tpws));
        cgcRecords.add(new CreativeGroupCreative(sndGroupId, creative.getId(), tpws, tpws));
        cgcRecords.add(new CreativeGroupCreative(trdGroupId, creative.getId(), tpws, tpws));

        when(creativeInsertionDao
                .getScheduleCountByCreativeIds(any(SearchCriteria.class), any(String.class),
                        any(SqlSession.class))).thenReturn(0L);
        when(creativeGroupCreativeDao.getByCreative(anyLong(), eq(sqlSessionMock)))
                .thenReturn(cgcRecords);

        Either<Errors, SuccessResponse> result = creativeManager.removeCreative(
                creative.getId(), groupIds, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.success().getMessage(), is(equalTo(
                "Creative " + creative.getId() + " successfully deleted.")));
    }

    @Test
    public void testRemoveCreativesWithSchedules() throws Exception {
        Long fstGroupId = Math.abs(EntityFactory.random.nextLong());
        RecordSet<Long> groupIds = new RecordSet<>();
        groupIds.setRecords(Arrays.asList(fstGroupId));

        when(creativeInsertionDao
                .getCountSchedulesByrCreativeAndGroupIds(anyLong(), anyList(), eq(key.getUserId()),
                        any(SqlSession.class))).thenReturn(1L);

        Either<Errors, SuccessResponse> result = creativeManager.removeCreative(
                creative.getId(), groupIds, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        BusinessError error = (BusinessError) result.error().getErrors().get(0);
        assertThat(error.getMessage(), is(equalTo("Creative " + creative.getId() +
                " could not be deleted because it is already scheduled.")));
    }

    @Test
    public void testGetCreativePreviewAndType() throws Exception{
        //Get JPG Creative
        Creative jpgCreative = getByType(AdminFile.FileType.JPG);

        Map<String, Object> result = creativeManager.getCreativePreviewAndType(jpgCreative.getId(), key);
        assertResult(result, AdminFile.FileType.JPG);
        Creative gifCreative = getByType(AdminFile.FileType.GIF);
        result = creativeManager.getCreativePreviewAndType(gifCreative.getId(), key);
        assertResult(result, AdminFile.FileType.GIF);
        Creative zipCreative = getByType(AdminFile.FileType.ZIP);
        result = creativeManager.getCreativePreviewAndType(zipCreative.getId(), key);
        assertResult(result, AdminFile.FileType.ZIP);
        Creative txtCreative = getByType(AdminFile.FileType.TXT);
        result = creativeManager.getCreativePreviewAndType(txtCreative.getId(), key);
        assertResult(result, AdminFile.FileType.TXT);
        Creative trdCreative = getByType(AdminFile.FileType.TRD);
        result = creativeManager.getCreativePreviewAndType(trdCreative.getId(), key);
        assertResult(result, AdminFile.FileType.TRD);
    }

    @Test
    public void saveCreativeFileWithVastFileTypeOk() throws Exception{
        // set data
        String filename = "VASTsample";
        String extension = "xml";

        prepareDataForSaveCreativeFilesTest(filename, extension);

        // perform test
        Creative filenameOnlyCreative = new Creative();
        filenameOnlyCreative.setFilename(creativeFilename);
        Creative result = creativeManager.saveCreativeFile(inputStream, filenameOnlyCreative,
                campaign.getId(), 1L, 2L, false, CreativeGroupManager.DISALLOW_VERSIONING,key, sqlSessionMock);
        assertThat(result, is(notNullValue()));
        String filenameExpected = filename + "." + AdminFile.FileType.XML.getFileType();
        assertThat(result.getFilename(), is(equalTo(filenameExpected)));       
        assertThat(result.getCreativeType(), is(equalTo(AdminFile.FileType.XML.getFileType())));
        assertThat(result.getWidth(), is(equalTo(Constants.DEFAULT_WIDTH_XML_CREATIVE)));
        assertThat(result.getHeight(), is(equalTo(Constants.DEFAULT_HEIGHT_XML_CREATIVE)));
        assertThat(result.getCreativeVersion(),
                is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }

    @Test
    public void saveCreativeFileVersionTest() throws Exception{
        // set data
        String filename = "VASTsample";
        String extension = "xml";

        prepareDataForSaveCreativeFilesTest(filename, extension);
        // perform test
        Creative filenameOnlyCreative = new Creative();
        filenameOnlyCreative.setFilename(creativeFilename);
        Creative result = creativeManager.saveCreativeFile(inputStream, filenameOnlyCreative,
                campaign.getId(), 1L, 2L, false, CreativeGroupManager.DISALLOW_VERSIONING, key, sqlSessionMock);

        assertThat(result, is(notNullValue()));
        assertThat(result.getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }
    
    @Test
    public void saveCreativeFileWithVmapFileWithNamespaceTypeOk() throws Exception{
        // set data
        String filename = "VMAPsample";
        String extension = "xml";
        
        prepareDataForSaveCreativeFilesTest(filename, extension);

        // perform test
        Creative filenameOnlyCreative = new Creative();
        filenameOnlyCreative.setFilename(creativeFilename);
        Creative result = creativeManager.saveCreativeFile(inputStream, filenameOnlyCreative,
                campaign.getId(), 1L, 2L, false, CreativeGroupManager.DISALLOW_VERSIONING, key, sqlSessionMock);
        assertThat(result, is(notNullValue()));
        String filenameExpected = filename + "." + AdminFile.FileType.XML.getFileType();
        assertThat(result.getFilename(), is(equalTo(filenameExpected)));       
        assertThat(result.getCreativeType(), is(equalTo(AdminFile.FileType.XML.getFileType())));
        assertThat(result.getWidth(), is(equalTo(Constants.DEFAULT_WIDTH_XML_CREATIVE)));
        assertThat(result.getHeight(), is(equalTo(Constants.DEFAULT_HEIGHT_XML_CREATIVE)));
        assertThat(result.getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }

    @Test
    public void saveCreativeFileWithVmapFileWithoutNamespaceTypeOk() throws Exception{
        // set data
        String filename = "VMAPsample2";
        String extension = "xml";
        
        prepareDataForSaveCreativeFilesTest(filename, extension);

        // perform test
        Creative filenameOnlyCreative = new Creative();
        filenameOnlyCreative.setFilename(creativeFilename);
        Creative result = creativeManager.saveCreativeFile(inputStream, filenameOnlyCreative,
                campaign.getId(), 2L, 3L, false, CreativeGroupManager.DISALLOW_VERSIONING, key, sqlSessionMock);
        assertThat(result, is(notNullValue()));
        String filenameExpected = filename + "." + AdminFile.FileType.XML.getFileType();
        assertThat(result.getFilename(), is(equalTo(filenameExpected)));       
        assertThat(result.getCreativeType(), is(equalTo(AdminFile.FileType.XML.getFileType())));
        assertThat(result.getWidth(), is(equalTo(Constants.DEFAULT_WIDTH_XML_CREATIVE)));
        assertThat(result.getHeight(), is(equalTo(Constants.DEFAULT_HEIGHT_XML_CREATIVE)));
        assertThat(result.getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }
    
    @Test
    public void saveCreativeFileWithXmlFileTypeOk() throws Exception{
        // set data
        String filename = "xmlsample";
        String extension = "xml";

        prepareDataForSaveCreativeFilesTest(filename, extension);

        // perform test
        Creative filenameOnlyCreative = new Creative();
        filenameOnlyCreative.setFilename(creativeFilename);
        Creative result = creativeManager.saveCreativeFile(inputStream, filenameOnlyCreative,
                campaign.getId(), 3L, 4L, false, false, key, sqlSessionMock);
        assertThat(result, is(notNullValue()));
        String filenameExpected = filename + "." + AdminFile.FileType.XML.getFileType();
        assertThat(result.getFilename(), is(equalTo(filenameExpected)));       
        assertThat(result.getCreativeType(), is(equalTo(AdminFile.FileType.XML.getFileType())));
        assertThat(result.getWidth(), is(equalTo(Constants.DEFAULT_WIDTH_XML_CREATIVE)));
        assertThat(result.getHeight(), is(equalTo(Constants.DEFAULT_HEIGHT_XML_CREATIVE)));
        assertThat(result.getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }
    
    @Test
    public void testSaveCreative3rd() throws Exception {
        Creative creative3rd = EntityFactory.createCreative();
        creative3rd.setId(null);
        creative3rd.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative3rd.setClickthrough(" ");
        Creative result = creativeManager.saveCreative(creative3rd, key);
        assertThat(result.getCreativeType(), is(equalTo(creative3rd.getCreativeType())));
        assertThat(result.getClickthrough(), is(equalTo(" ")));
    }

    @Test
    public void testSaveCreative3rdWithCT() throws Exception {
        Creative creative3rd = EntityFactory.createCreative();
        creative3rd.setId(null);
        creative3rd.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative3rd.setClickthrough(Constants.DEFAULT_CLICKTHROUGH_CREATIVE);
        Creative result = creativeManager.saveCreative(creative3rd, key);
        assertThat(result.getCreativeType(), is(equalTo(creative3rd.getCreativeType())));
        assertThat(result.getClickthrough(), is(equalTo(" ")));
    }

    @Test
    public void testSaveCreativeJpg() throws Exception {
        Creative creative3rd = EntityFactory.createCreative();
        creative3rd.setId(null);
        creative3rd.setClickthrough(" ");
        Creative result = creativeManager.saveCreative(creative3rd, key);
        assertThat(result.getCreativeType(), is(equalTo(creative3rd.getCreativeType())));
        assertThat(result.getClickthrough(), is(equalTo(Constants.DEFAULT_CLICKTHROUGH_CREATIVE)));
    }

    @Test
    public void testSaveCreativeJpgWithCT() throws Exception {
        Creative creative3rd = EntityFactory.createCreative();
        creative3rd.setId(null);
        creative3rd.setClickthrough(Constants.DEFAULT_CLICKTHROUGH_CREATIVE);
        Creative result = creativeManager.saveCreative(creative3rd, key);
        assertThat(result.getCreativeType(), is(equalTo(creative3rd.getCreativeType())));
        assertThat(result.getClickthrough(), is(equalTo(Constants.DEFAULT_CLICKTHROUGH_CREATIVE)));
    }

    @Test
    public void saveCreativeFileWith3rdFileTypeOk() throws Exception {
        // set data
        String filename = "eclick";
        String extension = "3rd";

        prepareDataForSaveCreativeFilesTest(filename, extension);

        // perform test
        Creative filenameOnlyCreative = new Creative();
        filenameOnlyCreative.setFilename(creativeFilename);
        Creative result = creativeManager.saveCreativeFile(inputStream, filenameOnlyCreative,
                campaign.getId(), 2L, 1L, true, CreativeGroupManager.DISALLOW_VERSIONING, key, sqlSessionMock);

        assertThat(result, is(notNullValue()));
        String filenameExpected = filename + "." + AdminFile.FileType.TRD.getFileType();
        assertThat(result.getFilename(), is(equalTo(filenameExpected)));
        assertThat(result.getCreativeType(), is(equalTo(AdminFile.FileType.TRD.getFileType())));
        assertThat(result.getHeight(), is(equalTo(2L)));
        assertThat(result.getWidth(), is(equalTo(1L)));
        assertThat(result.getCreativeVersion(),
                is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
    }

    @Test
    public void saveCreativeFileWith3rdFileTmpTypeOk() throws Exception {
        // set data
        String filename = "eclick";
        String extension = "3rd";

        prepareDataForSaveCreativeFilesTest(filename, extension);

        // perform test
        Creative result = creativeManager.saveCreativeFileInTmp(inputStream, creativeFilename,
                campaign.getId(), true, 1L, 2L, key);
        assertThat(result, is(notNullValue()));
        String filenameExpected = filename + "." + AdminFile.FileType.TRD.getFileType();
        assertThat(result.getFilename(), is(equalTo(filenameExpected)));
        assertThat(result.getCreativeType(), is(equalTo(AdminFile.FileType.TRD.getFileType())));
        assertThat(result.getWidth(), is(equalTo(2L)));
        assertThat(result.getHeight(), is(equalTo(1L)));
        assertThat(result.getCreativeVersion(), is(equalTo(Constants.DEFAULT_CREATIVE_INITIAL_VERSION)));
        assertThat(result.getIsExpandable(), is(equalTo(1L)));
    }

    private static Answer<List<Creative>> getCreatives() {
        return new Answer<List<Creative>>() {
            public List<Creative> answer(InvocationOnMock invocation) {
                Long campaignId = (Long) invocation.getArguments()[0];
                Long groupId = (Long) invocation.getArguments()[1];

                List<Creative> result = new ArrayList<>();
                for (Map.Entry<Long, Creative> entry : existingCreatives.entrySet()) { //fully unassociated
                    if(associatedCreatives.get(entry.getKey()) == null ||
                            (associatedCreatives.get(entry.getKey()) != null && !associatedCreatives.get(entry.getKey()).contains(groupId))){
                        result.add(entry.getValue());
                    }
                }
                return result;
            }
        };
    }

    private Creative getByType(AdminFile.FileType fileType){
        for(Map.Entry<Long, Creative> entry : existingCreatives.entrySet()){
            if(AdminFile.FileType.typeOf(entry.getValue().getCreativeType()) == fileType){
                return entry.getValue();
            }
        }
        return null;
    }

    private void assertResult(Map<String, Object> result, AdminFile.FileType expectedType){
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(2)));
        assertThat(result.get(CreativeManager.CREATIVE_KEY_OBJECT), is(notNullValue()));
        assertThat(result.get(CreativeManager.CREATIVE_KEY_FILE_TYPE), is(notNullValue()));
        assertThat(result.get(CreativeManager.CREATIVE_KEY_FILE_TYPE).toString(), is(expectedType.getFileType()));
    }

    private void prepareDataForSaveCreativeFilesTest(String filename, String extension){
        creativeFilename = filename + "." + extension;
        sourceFilePath = "/images/" + creativeFilename;         
        inputStream = getClass().getResourceAsStream(sourceFilePath);
    }
}
