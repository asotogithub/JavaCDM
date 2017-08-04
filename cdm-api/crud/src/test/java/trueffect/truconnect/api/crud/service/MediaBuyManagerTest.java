package trueffect.truconnect.api.crud.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.MediaBuy;
import trueffect.truconnect.api.commons.model.MediaBuyCampaign;
import trueffect.truconnect.api.commons.model.enums.GeneralStatusEnum;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.MediaBuyDao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;

/**
 *
 * @author marleny.patsi
 */
public class MediaBuyManagerTest extends AbstractManagerTest {

    private MediaBuyManager mediaBuyManager;
    private MediaBuyDao mediaBuyDao;
    private MediaBuy mediaBuy;
    private MediaBuyCampaign mediaBuyCampaign;
    private Long campaignId;

    @Before
    public void init() {
        // mock daos
        mediaBuyDao = mock(MediaBuyDao.class);

        mediaBuyManager = new MediaBuyManager(mediaBuyDao, accessControlMockito);

        //variables
        mediaBuy = EntityFactory.createMediaBuy();
        campaignId = Math.abs(EntityFactory.random.nextLong());
        mediaBuyCampaign = new MediaBuyCampaign();
        mediaBuyCampaign.setCampaignId(campaignId);
        mediaBuyCampaign.setMediaBuyId(mediaBuy.getId());
        mediaBuyCampaign.setLogicalDelete("N");

        // Mock Session
        when(mediaBuyDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(mediaBuyDao).close(sqlSessionMock);

        // Mocks common behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.MEDIA_BUY, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);

        when(mediaBuyDao.get(eq(mediaBuy.getId()), eq(sqlSessionMock))).thenReturn(mediaBuy);
        when(mediaBuyDao.getByCampaign(eq(campaignId), eq(sqlSessionMock))).thenReturn(mediaBuy);
    }

    @Test
    public void getByIdOk() throws Exception {
        // prepare data

        //call method to test
        MediaBuy result = mediaBuyManager.get(mediaBuy.getId(), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(equalTo(mediaBuy.getId())));
    }

    @Test
    public void getByIdWithNullParameters() throws Exception {
        // prepare data
        mediaBuy.setId(null);
        key = null;

        //call method to test
        try {
            mediaBuyManager.get(mediaBuy.getId(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Media Buy Id cannot be null."));
        }
    }

    @Test
    public void getByIdWithWrongAccess() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.MEDIA_BUY, Collections.singletonList(mediaBuy.getId()),
                sqlSessionMock);

        //call method to test
        try {
            mediaBuyManager.get(mediaBuy.getId(), key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(),
                    is("Data not found for User: " + key.getUserId()));
        }
    }

    @Test
    public void getByCampaignIdOk() throws Exception {
        // prepare data

        //call method to test
        MediaBuy result = mediaBuyManager.getByCampaignId(campaignId, key);

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void getByCampaignIdWithoutParameters() throws Exception {
        // prepare data
        campaignId = null;

        //call method to test
        try {
            mediaBuyManager.getByCampaignId(campaignId, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Campaign Id cannot be null."));
        }
    }

    @Test
    public void getByCampaignIdWithWrongAccess() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, Collections.singletonList(campaignId),
                sqlSessionMock);

        //call method to test
        try {
            mediaBuyManager.getByCampaignId(campaignId, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(),
                    is("CampaignId: [" + campaignId + "]Not found for User: " + key.getUserId()));
        }
    }

    @Test
    public void getByCampaignIdWithoutMediaData() throws Exception {

        //customize mock's behavior
        when(mediaBuyDao.getByCampaign(eq(campaignId), eq(sqlSessionMock))).thenReturn(null);

        //call method to test
        try {
            mediaBuyManager.getByCampaignId(campaignId, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), is("Record not found."));
        }
    }

    @Test
    public void createOk() throws Exception {
        // prepare data
        mediaBuy.setId(null);

        //customize mock's behavior
        when(mediaBuyDao.get(eq(mediaBuy.getId()), eq(sqlSessionMock))).thenReturn(mediaBuy);
        when(mediaBuyDao.create(eq(mediaBuy), eq(sqlSessionMock))).thenAnswer(create());

        //call method to test
        MediaBuy result = mediaBuyManager.create(mediaBuy, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void createWithDefaultStatusOk() throws Exception {
        // prepare data
        mediaBuy.setId(null);
        mediaBuy.setState(null);

        //customize mock's behavior
        when(mediaBuyDao.get(eq(mediaBuy.getId()), eq(sqlSessionMock))).thenReturn(mediaBuy);
        when(mediaBuyDao.create(eq(mediaBuy), eq(sqlSessionMock))).thenAnswer(create());

        //call method to test
        MediaBuy result = mediaBuyManager.create(mediaBuy, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getState(), is(equalTo(GeneralStatusEnum.NEW.getStatusCode())));
    }

    @Test
    public void createWithNullParams() throws Exception {
        // prepare data
        mediaBuy = null;

        //call method to test
        try {
            mediaBuyManager.create(mediaBuy, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("MediaBuy cannot be null."));
        }
    }

    @Test
    public void createWithMediaBuyId() throws Exception {
        // prepare data
        mediaBuy.setId(Math.abs(EntityFactory.random.nextLong()));

        //call method to test
        try {
            mediaBuyManager.create(mediaBuy, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Media Buy should not have the id field. If you are trying to update an existing Media Buy use PUT service."));
        }
    }

    @Test
    public void createWithEmptyName() throws Exception {
        // prepare data
        mediaBuy.setId(null);
        mediaBuy.setName(null);

        //call method to test
        try {
            mediaBuyManager.create(mediaBuy, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Invalid Media Buy Name, it cannot be empty."));
        }
    }

    @Test
    public void createWithTooLongName() throws Exception {
        // prepare data
        mediaBuy.setId(null);
        mediaBuy.setName(EntityFactory.faker.lorem().fixedString(Constants.DEFAULT_CHARS_LENGTH + 1));

        //call method to test
        try {
            mediaBuyManager.create(mediaBuy, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Invalid Media Buy Name, it supports characters up to 256."));
        }
    }

    @Test
    public void createWithWrongAccess() throws Exception {
        // prepare data
        mediaBuy.setId(null);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, Collections.singletonList(mediaBuy.getAgencyId()),
                sqlSessionMock);

        //call method to test
        try {
            mediaBuyManager.create(mediaBuy, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(),
                    is("AgencyId: [" + mediaBuy.getAgencyId() + "]Not found for User: " + key.getUserId()));
        }
    }

    @Test
    public void createMediaBuyCampaignOk() throws Exception {

        //customize mock's behavior
        when(mediaBuyDao.createMediaBuyCampaign(eq(mediaBuyCampaign), eq(sqlSessionMock))).thenReturn(mediaBuyCampaign);

        //call method to test
        MediaBuyCampaign result = mediaBuyManager.createMediaBuyCampaign(mediaBuyCampaign, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getMediaBuyId(), is(equalTo(mediaBuyCampaign.getMediaBuyId())));
        assertThat(result.getCampaignId(), is(equalTo(mediaBuyCampaign.getCampaignId())));
    }

    @Test
    public void createMediaBuyCampaignWithNullParams() throws Exception {
        // prepare data
        mediaBuyCampaign = null;

        //call method to test
        try {
            mediaBuyManager.createMediaBuyCampaign(mediaBuyCampaign, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("MediaBuyCampaign cannot be null."));
        }
    }

    @Test
    public void createMediaBuyCampaignWithNullId() throws Exception {
        // prepare data
        mediaBuyCampaign.setMediaBuyId(null);

        //call method to test
        try {
            mediaBuyManager.createMediaBuyCampaign(mediaBuyCampaign, key);
            fail("This test should throw an ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Invalid Media Buy Id, it cannot be empty."));
        }
    }

    @Test
    public void createMediaBuyCampaignWithNullCampaignId() throws Exception {
        // prepare data
        mediaBuyCampaign.setCampaignId(null);

        //call method to test
        try {
            mediaBuyManager.createMediaBuyCampaign(mediaBuyCampaign, key);
            fail("This test should throw an ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage(),
                    is("Invalid Campaign Id, it cannot be empty."));
        }
    }
    
    @Test
    public void createMediaBuyCampaignWithWrongAccessForMediaBuy() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.MEDIA_BUY, Collections.singletonList(mediaBuyCampaign.getMediaBuyId()),
                sqlSessionMock);

        //call method to test
        try {
            mediaBuyManager.createMediaBuyCampaign(mediaBuyCampaign, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(),
                    is("Data not found for User: " + key.getUserId()));
        }
    }

    @Test
    public void createMediaBuyCampaignWithWrongAccessForCampaign() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.CAMPAIGN, Collections.singletonList(mediaBuyCampaign.getCampaignId()),
                sqlSessionMock);

        //call method to test
        try {
            mediaBuyManager.createMediaBuyCampaign(mediaBuyCampaign, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(),
                    is("CampaignId: [" + mediaBuyCampaign.getCampaignId() + "]Not found for User: " + key.getUserId()));
        }
    }

    private static Answer<MediaBuy> create() {
        return new Answer<MediaBuy>() {
            @Override
            public MediaBuy answer(InvocationOnMock invocation) {
                MediaBuy result = (MediaBuy) invocation.getArguments()[0];
                result.setId(Math.abs(EntityFactory.random.nextLong()));
                return result;
            }
        };
    }
}
