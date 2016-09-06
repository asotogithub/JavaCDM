package trueffect.truconnect.api.crud.service;

import static org.mockito.Mockito.mock;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.util.EncryptUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;

/**
 * Base class for manager tests.
 */
abstract public class AbstractManagerTest {
    protected SqlSession sqlSessionMock;
    protected AccessControl accessControlMockito;
    protected OauthKey key;
    protected String userId = "foo@bar.com";
    protected String tpws = "00000";

    @Before
    public void initBasics() throws Exception {
        sqlSessionMock = mock(SqlSession.class);
        accessControlMockito = mock(AccessControl.class);
        key = new OauthKey(EncryptUtil.encryptAES(userId), tpws);
    }
}
