package trueffect.truconnect.api.crud.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.Clickthrough;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.MockAccessControlUtil;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CreativeDao;

import org.junit.Test;
import org.junit.Before;

import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author Abel Soto
 */
public class ClickthroughManagerTest extends AbstractManagerTest {

    private ClickthroughManager clickthrougManager;
    Clickthrough click;
    Creative creative = new Creative();
    List<Clickthrough> clickthroughs;
    private CreativeDao creativeDaoMock;

    @Before
    public void init() throws Exception {
        // Create a mock context and mock accessControl for tests

        // Set key value for oauth
        key = new OauthKey(EncryptUtil.encryptAES("dummy@user"), "00000");

        creativeDaoMock = mock(CreativeDao.class);
        clickthrougManager = new ClickthroughManager(creativeDaoMock, accessControlMockito);
        
        click = new Clickthrough();
        creative = new Creative();
        clickthroughs = new ArrayList<>();

        when(creativeDaoMock.get(anyLong(),
                eq(sqlSessionMock))).thenReturn(creative);
        when(creativeDaoMock.openSession()).thenReturn(sqlSessionMock);
        MockAccessControlUtil.allowAccessForUser(accessControlMockito,
                AccessStatement.CREATIVE,
                sqlSessionMock);
    }

    @Test
    public void tesInsertClickthrough() throws Exception {
        click.setUrl("http://my.site.com/elsewhere");
        click.setSequence(2L);
        clickthroughs.add(click);
        click = new Clickthrough();
        click.setUrl("http://my.site.com/somewhere");
        click.setSequence(3L);
        clickthroughs.add(click);
        creative.setId(8585422L);
        creative.setClickthroughs(clickthroughs);
        creative.setCreatedTpwsKey("000000");
        when(creativeDaoMock.saveCreativeClickThrough(eq(creative), eq(key),
                eq(sqlSessionMock))).thenReturn(creative);

        Creative crea = clickthrougManager.saveCreativeClickthrough(creative, key);
        assertNotNull(crea);
    }

    @Test
    public void testRemoveClickthrough() throws Exception {
        clickthrougManager.removeCreativeClickthrough(8585422L, key);
    }
}
