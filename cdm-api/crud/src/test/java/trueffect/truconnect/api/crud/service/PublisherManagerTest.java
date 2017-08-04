package trueffect.truconnect.api.crud.service;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.ConflictException;
import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.PublisherDao;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Rambert Rioja, Richard Jaldin
 */
public class PublisherManagerTest extends AbstractManagerTest {

    private PublisherManager publisherManager;
    private PublisherDao publisherDao;
    private Publisher publisher;

    @Before
    public void init() {
        // mock daos
        publisherDao = mock(PublisherDao.class);

        publisherManager = new PublisherManager(publisherDao, accessControlMockito);

        //prepare data
        publisher = EntityFactory.createPublisher();

        // Mock Session
        when(publisherDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(publisherDao).commit(any(SqlSession.class));
        doNothing().when(publisherDao).close(any(SqlSession.class));

        //mock behaviors - DAC
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.PUBLISHER, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.SITE, sqlSessionMock);

        //mock common behavior
        when(publisherDao.get(anyLong(), any(SqlSession.class))).thenReturn(publisher);
        when(publisherDao.exists(any(Publisher.class), any(SqlSession.class))).thenReturn(null);
        when(publisherDao.getNextId(any(SqlSession.class))).thenReturn(publisher.getId());
        when(publisherDao.create(any(Publisher.class), any(SqlSession.class))).thenAnswer(createPublisher());
        when(publisherDao.update(any(Publisher.class), any(SqlSession.class))).thenAnswer(createPublisher());
        doNothing().when(publisherDao).remove(eq(publisher.getId()), eq(publisher.getAgencyId()), eq(key), any(SqlSession.class));
    }

    @Test
    public void getByIdOk() throws Exception {
        //Perform test
        Publisher result = publisherManager.get(publisher.getId(), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(equalTo(publisher.getId())));
    }

    @Test
    public void getByIdWithNullParameters() throws Exception {
        // prepare data
        publisher.setId(null);

        //Perform test
        try {
            publisherManager.get(publisher.getId(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Id cannot be null."));
        }
    }

    @Test
    public void getByIdWithWrongAccess() throws Exception {

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.PUBLISHER, Collections.singletonList(publisher.getId()),
                sqlSessionMock);

        //Perform test
        try {
            publisherManager.get(publisher.getId(), key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(), is(equalTo(
                    String.format("%s: [%s]Not found for User: %s", "PublisherId",
                            String.valueOf(publisher.getId()),
                            key.getUserId()))));
        }
    }

    @Test
    public void testGetCriteria() throws Exception {
        //prepare data
        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery("id in [" + publisher.getId() + "] ordering name asc");
        criteria.setStartIndex(1);
        criteria.setPageSize(5);

        List<Publisher> publisherList = new ArrayList<>();
        publisherList.add(publisher);
        RecordSet<Publisher> publishers = new RecordSet<>(publisherList);

        //customize mock's behavior
        when(publisherDao.get(any(SearchCriteria.class), eq(key), any(SqlSession.class))).thenReturn(publishers);

        //call method to test
        RecordSet<Publisher> result = publisherManager.getPublishers(criteria, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getRecords(), is(notNullValue()));
        assertThat(result.getRecords().size(), is(greaterThan(0)));
    }

    @Test
    public void createOk() throws Exception {
        //prepare data
        publisher.setId(null);

        //call method to test
        Publisher result = publisherManager.create(publisher, key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void createWithNullParameters() throws Exception {
        //prepare data
        publisher = null;

        //call method to test
        try {
            publisherManager.create(publisher, key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Publisher cannot be null."));
        }
    }

    @Test
    public void createWithWrongPayload() throws Exception {
        //prepare data
        publisher.setId(Math.abs(EntityFactory.random.nextLong()));

        //call method to test
        try {
            publisherManager.create(publisher, key);
            fail("This test should throw a ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getErrors().getAllErrors().size(), is(1));
            FieldError fieldError = (FieldError) e.getErrors().getAllErrors().get(0);
            assertThat(fieldError, is(notNullValue()));
            assertThat(fieldError.getField(), is(equalTo("id")));
            assertThat((Long) fieldError.getRejectedValue(), is(equalTo(publisher.getId())));
            assertThat(fieldError.getDefaultMessage(), is(equalTo(
                    String.format("%s should not have the id field. If you are trying to update an existing %s use PUT service.",
                            ResourceBundleUtil.getString("global.label.publisher"),
                            ResourceBundleUtil.getString("global.label.publisher")))));
        }
    }

    @Test
    public void createWithWrongAccess() throws Exception {
        //prepare data
        publisher.setId(null);

        //customize mock's behavior
        MockAccessControlUtil.disallowAccessForUser(accessControlMockito,
                AccessStatement.AGENCY, Collections.singletonList(publisher.getAgencyId()),
                sqlSessionMock);

        //call method to test
        try {
            publisherManager.create(publisher, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (DataNotFoundForUserException e) {
            assertThat(e.getMessage(), is(equalTo(
                    String.format("%s: [%s]Not found for User: %s", "AgencyId",
                            String.valueOf(publisher.getAgencyId()),
                            key.getUserId()))));
        }
    }

    @Test
    public void createWithExistentName() throws Exception {
        //prepare data
        publisher.setId(null);

        //customize mock's behavior
        // publisherDao.exists(publisher, session);
        when(publisherDao.exists(eq(publisher), eq(sqlSessionMock))).thenReturn(1L);

        //call method to test
        try {
            publisherManager.create(publisher, key);
            fail("This test should throw a DataNotFoundForUserException");
        } catch (ConflictException e) {
            assertThat(e.getMessage(), is(equalTo("Publisher name already exists.")));
        }
    }

    @Test
    public void createPublisherOk() {
        //prepare data
        publisher.setId(null);

        //call method to test
        Publisher result = publisherManager.createPublisher(publisher, key, sqlSessionMock);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test
    public void createPublisherWithNullParameters() {
        //prepare data
        sqlSessionMock = null;

        //call method to test
        try {
            publisherManager.createPublisher(publisher, key, sqlSessionMock);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("Session cannot be null."));
        }
    }

    @Test
    public void testUpdatePublisher() throws Exception {
        //prepare data
        Publisher siteToUpdate = publisher;
        siteToUpdate.setName(EntityFactory.faker.name().name());

        //customize mock behaviors
        when(publisherDao.get(anyLong(), any(SqlSession.class))).thenReturn(siteToUpdate);

        //call method to test
        Publisher result = publisherManager.update(publisher.getId(), siteToUpdate, key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(equalTo(siteToUpdate.getName())));
    }

    @Test
    public void testDeletePublisher() throws Exception {
        //call method to test
        SuccessResponse result = publisherManager.remove(publisher.getId(), publisher.getAgencyId(), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getMessage(), is(equalTo("Publisher " + publisher.getId() + " successfully deleted")));
    }

    @Test(expected = ValidationException.class)
    public void testUpdatePublisherWithWrongData() throws Exception {
        //prepare data
        Publisher siteToUpdate = publisher;
        siteToUpdate.setAgencyId(null);
        siteToUpdate.setName(EntityFactory.faker.lorem().fixedString(260));

        //call method to test
        publisherManager.update(publisher.getId(), siteToUpdate, key);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdatePublisherNonExistent() throws Exception {

        //customize mock behavior
        when(publisherDao.get(anyLong(), any(SqlSession.class))).thenReturn(null);

        //call method to test
        publisherManager.update(publisher.getId(), publisher, key);
    }

    private Answer<Publisher> createPublisher() {
        return new Answer<Publisher>() {
            @Override
            public Publisher answer(InvocationOnMock invocationOnMock) throws Throwable {
                Publisher result = (Publisher) invocationOnMock.getArguments()[0];
                result.setId(result.getId() == null ? Math.abs(EntityFactory.random.nextLong()) : result.getId());
                result.setCreatedTpwsKey(result.getId() == null ? key.getTpws() : result.getCreatedTpwsKey());
                result.setModifiedTpwsKey(key.getTpws());
                result.setCreatedDate(new Date());
                result.setModifiedDate(new Date());
                return result;
            }
        };
    }
}
