package trueffect.truconnect.api.crud.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.Errors;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeVersion;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.ibatis.session.SqlSession;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit Tests for Updating a Creative
 * Created by marcelo.heredia on 8/15/2016.
 * @author Marcelo Heredia
 */
public class CreativeManagerUpdateTest extends CreativeManagerBaseTest{

    private int numberOfVersions;

    @Before
    public void init() {

        // Initialize parameters
        List<CreativeGroup> groups = new ArrayList<>();
        CreativeGroup group = new CreativeGroup();
        group.setId(1L);
        groups.add(group);
        group = new CreativeGroup();
        group.setId(2L);
        groups.add(group);

        creative.setCreativeGroups(groups);
        numberOfVersions = 10;
        creative.setVersions(EntityFactory.createCreativeVersions(
                creative.getId(),
                campaign.getId(),
                numberOfVersions));
        // Mock DAOs
        when(creativeDao.get(eq(creative.getId()),
                eq(sqlSessionMock))).thenReturn(
                creative);
        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(creative.getCampaignId()),
                anyList(), any(SqlSession.class))).thenReturn(new Long(creative.getCreativeGroups().size()));
    }

    @Test
    public void testUpdateCreativeWithExternalIdWithSpaces() throws Exception {
        // set data
        Long creativeId = creative.getId();
        creative.setExternalId("     ");

        // perform test
        Either<Errors, Creative> result = creativeManager.updateCreative(creativeId, creative, key);
        assertThat(result.success().getExternalId(), is(equalTo("_____")));

        // set data: Spaces in the middle
        creative.setExternalId("1    2");
        // perform test
        result = creativeManager.updateCreative(creativeId, creative, key);
        assertThat(result.success().getExternalId(), is(equalTo("1____2")));

        // set data: Spaces in the end
        creative.setExternalId("   12   ");
        // perform test
        result = creativeManager.updateCreative(creativeId, creative, key);
        assertThat(result.success().getExternalId(), is(equalTo("___12___")));
    }

    @Test
    public void testUpdateWithWrongAccess() throws Exception {
        //customize mock behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE,
                Collections.singletonList(creative.getId()),
                sqlSessionMock);
        //call method to test
        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(), creative, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        String message = String.format("%s: %s Not found for User: %s", "CreativeId", String.valueOf(creative.getId()), userId);
        assertThat(result.error().getErrors().iterator().next().getMessage(),
                is(equalTo(message)));
    }

    @Test
    public void testUpdateCreativeWithCreativeGroupsEmpty() throws Exception {

        //object with creativeGroups empty
        creative.setCreativeGroups(new ArrayList<CreativeGroup>());

        //call method to test
        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(), creative, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        assertThat(result.error().getErrors().iterator().next().getMessage(),
                is(equalTo("A Creative must have at least one Creative Group association.")));

    }

    @Test
    public void testUpdateCreativeWithCreativeGroupsAnotherCampaign() throws Exception {

        //customize mock behavior
        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(creative.getCampaignId()),
                anyList(), any(SqlSession.class))).thenReturn(0L);

        //call method to test
        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(), creative, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        assertThat(result.error().getErrors().iterator().next().getMessage(),
                is(equalTo("CreativeGroup does not belong to the same Creative campaign.")));
    }

    @Test
    public void testUpdateCreativeWithCreativeGroups() throws Exception {
        //call test
        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(), creative, key);
        MatcherAssert.assertThat(result, is(notNullValue()));
        assertThat(result.success().getCreativeGroups().size(),
                is(equalTo(result.success().getCreativeGroups().size())));
    }

    @Test
    public void testUpdateCreative() throws Exception {
        // set data
        creative.setId(null);
        Creative creative = creativeManager.saveCreative(this.creative, key);
        MatcherAssert.assertThat(creative, is(notNullValue()));
        Long creativeId = creative.getId();
        String newAlias = EntityFactory.faker.name().firstName();
        creative.setAlias(newAlias);

        // perform test
        Either<Errors, Creative> result = creativeManager.updateCreative(creativeId, creative, key);
        assertThat(result, is(notNullValue()));
        assertThat(newAlias, is(equalTo(result.success().getAlias())));
    }

    @Test
    public void testUpdateCreative3RDWithClickthroughShouldFail(){
        creative.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative.setClickthrough(Constants.DEFAULT_CLICKTHROUGH_CREATIVE);
        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(), creative,
                key);
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));
        assertThat(result.error().getErrors().size(), is(equalTo(1)));
        assertThat(result.error().getErrors().iterator().next().getMessage(),
                is(equalTo("Clickthrough should be empty to update 3rd type Creative or CreativeInsertion.")));

    }

    @Ignore
    @Test
    public void testUpdateCreative3rdOk() {
        creative.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative.setClickthrough(null);
        when(creativeGroupDao.getCountCreativeGroupsByCampaignId(eq(creative.getCampaignId()),
                anyListOf(Long.class), eq(sqlSessionMock))).thenReturn(1L);
        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative, key);
        assertThat(result.success().getCreativeType(), is(equalTo(creative.getCreativeType())));
        assertThat(result.success().getClickthrough(), is(equalTo(null)));
    }

    @Ignore
    @Test
    public void testUpdateCreative3rd() {
        Creative creative3rd = EntityFactory.createCreative();
        creative3rd.setCreativeType(CreativeManager.CreativeType.TRD.getCreativeType());
        creative3rd.setClickthrough(" ");
        when(creativeDao.get(anyLong(), any(SqlSession.class))).thenReturn(creative3rd);
        creativeManager.updateCreative(creative3rd.getId(), creative3rd, key);
    }

    @Test
    public void testUpdateCreativeVersionsWithBlankAliasShouldFail() {

        // Clean up all versions
        for(CreativeVersion cv : creative.getVersions()) {
            cv.setAlias("");
        }

        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative,
                key);

        String message = "Invalid alias, it cannot be empty.";
        assertCreativeVersionError(numberOfVersions, result, message);
    }

    @Test
    public void testUpdateCreativeVersionsWithLargeAliasShouldFail() {

        // Make all aliases longer than max (256)
        for(CreativeVersion cv : creative.getVersions()) {
            cv.setAlias(EntityFactory.faker.lorem().fixedString(257));
        }

        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative,
                key);

        String message = "Invalid alias, it supports characters up to 256.";
        assertCreativeVersionError(numberOfVersions, result, message);
    }

    @Test
    public void testUpdateCreativeVersionsWithNegativeVersionNumberShouldFail() {

        // Clean up all versions
        for(CreativeVersion cv : creative.getVersions()) {
            cv.setVersionNumber(-(Math.abs(EntityFactory.random.nextLong())));
        }

        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative,
                key);

        String message = "versionNumber should be positive number.";
        assertCreativeVersionError(numberOfVersions, result, message);
    }

    @Test
    public void testUpdateCreativeVersionsWithNoVersionNumberShouldFail() {

        // Clean up all versions
        for(CreativeVersion cv : creative.getVersions()) {
            cv.setVersionNumber(null);
        }

        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative,
                key);

        String message = "Invalid versionNumber, it cannot be empty.";
        assertCreativeVersionError(1, result, message);
    }

    @Test
    public void testUpdateCreativeVersionsWithRepeatedVersionNumbersShouldPass() {
        // Randomly assign a version from 0 to 5 to all versions
        Long version = Long.valueOf(EntityFactory.random.nextInt(5));
        for(CreativeVersion cv : creative.getVersions()) {
            cv.setVersionNumber(version);
        }

        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative,
                key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(result.success().getVersions().size(),
                is(greaterThan(0)));
        assertThat(result.success().getVersions().size(),
                is(lessThan(numberOfVersions)));
    }

    @Test
    public void testUpdateCreativeVersionsWithRepeatedVersionNumbersShouldKeepFirstVersionFound() {
        // Get the 5th version and 10th.
        CreativeVersion fith = creative.getVersions().get(4);
        CreativeVersion tenth = creative.getVersions().get(9);
        // Make them the same version
        tenth.setVersionNumber(fith.getVersionNumber());

        Either<Errors, Creative> result = creativeManager.updateCreative(creative.getId(),
                creative,
                key);

        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(notNullValue()));
        assertThat(result.error(), is(nullValue()));

        assertThat(result.success().getVersions().size(),
                is(equalTo(9)));
        assertThat(result.success().getVersions().get(4).getAlias(),
                is(equalTo(fith.getAlias())));

    }

    @Test
    public void testUpdateCreativeVersionsWithAliasOnCreativeShouldOverwriteLastVersion() throws Exception {
        int lastVersionIndex = 0;
        long lastKnownVersion = creative.getVersions().get(lastVersionIndex).getVersionNumber();
        Creative creativeToTest = new Creative();
        BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
        BeanUtils.copyProperties(creativeToTest, creative);
        creativeToTest.setAlias(EntityFactory.faker.letterify("??????????"));
        // Clear out all versions
        creativeToTest.setVersions(new ArrayList<CreativeVersion>());
        // Make sure that last known version has a different alias than the one defined in Creative's payload
        creativeToTest.getVersions().add(new CreativeVersion(creative.getId(),
                lastKnownVersion,
                EntityFactory.faker.letterify("??????????"),
                new Date(),
                0L,
                creative.getCampaignId()));
        // Mock DAOs
        final Map<Long, CreativeVersion> versionsMap = new HashMap<>();
        for(CreativeVersion version : creative.getVersions()) {
            versionsMap.put(version.getVersionNumber(), version);
        }
        when(creativeDao.updateVersions(anyListOf(CreativeVersion.class),
                eq(sqlSessionMock))).then(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<CreativeVersion> versions =
                        (List<CreativeVersion>) invocationOnMock.getArguments()[0];
                // Versions to update
                for (CreativeVersion version : versions) {
                    CreativeVersion v = versionsMap.get(version.getVersionNumber());
                    if (v != null) {
                        v.setAlias(version.getAlias());
                    }

                }
                return null;
            }
        });
        // Test
        Either<Errors, Creative> result = creativeManager.updateCreative(
                creativeToTest.getId(),
                creativeToTest,
                key);

        assertThat(result, is(notNullValue()));
        Creative resultingCreative = result.success();
        assertThat(resultingCreative, is(notNullValue()));
        assertThat(result.error(), is(nullValue()));
        assertThat(resultingCreative.getVersions().size(),
                is(equalTo(numberOfVersions)));
        assertThat(resultingCreative.getVersions().get(lastVersionIndex).getAlias(),
                is(equalTo(creativeToTest.getAlias())));
    }

    private void assertCreativeVersionError(int numberOfVersions, Either<Errors, Creative> result,
                                           String message) {
        assertThat(result, is(notNullValue()));
        assertThat(result.success(), is(nullValue()));
        assertThat(result.error(), is(notNullValue()));

        List<Error> errorList =
                result.error().getErrors();
        assertThat(errorList.toString(), errorList.size(), is(numberOfVersions));

        for(Error error : errorList) {
            assertThat(error.getMessage(), is(equalTo(message)));
        }
    }
}
