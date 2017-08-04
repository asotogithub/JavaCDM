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

import trueffect.truconnect.api.commons.model.Size;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.SizeDao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

/**
 *
 * @author Gustavo Claure
 */
public class SizeManagerTest extends AbstractManagerTest {

    private SizeManager sizeManager;
    private SizeDao sizeDao;
    private Size size;

    @Before
    public void setUp() {
        // mock daos
        sizeDao = mock(SizeDao.class);

        sizeManager = new SizeManager(sizeDao, accessControlMockito);

        //variables
        size = EntityFactory.createSize();

        // Mock Session
        when(sizeDao.openSession()).thenReturn(sqlSessionMock);
        doNothing().when(sizeDao).close(sqlSessionMock);
        
        // Mocks common behavior
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.AD_SIZE, sqlSessionMock);
        
        when(sizeDao.getById(eq(size.getId()), eq(sqlSessionMock))).thenReturn(size);
    }

    @Test
    public void getOk() throws Exception {
        // prepare data

        //call method to test
        Size result = sizeManager.get(size.getId(), key);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(equalTo(size.getId())));
    }

    @Test
    public void getWithNullParameters() throws Exception {
        // prepare data
        size.setId(null);
        
        //call method to test
        try {
            sizeManager.get(size.getId(), key);
            fail("This test should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    is("id cannot be null"));
        }
    }

    private Answer<Size> savedSize(final Size size) {
        return new Answer<Size>() {
            @Override
            public Size answer(InvocationOnMock invocationOnMock) throws Throwable {
                size.setId(size.getId() == null ? EntityFactory.random.nextLong() : size.getId());
                size.setCreatedTpwsKey(key.getTpws());
                size.setModifiedTpwsKey(key.getTpws());
                size.setCreatedDate(new Date());
                size.setModifiedDate(new Date());
                return size;
            }
        };
    }
}
