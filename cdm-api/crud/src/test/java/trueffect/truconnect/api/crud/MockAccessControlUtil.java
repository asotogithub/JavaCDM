package trueffect.truconnect.api.crud;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;

import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.List;

/**
 * Utility class for setting up a mock access control.
 */
public class MockAccessControlUtil {

    private MockAccessControlUtil() {}

    public static void disallowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, List<Long> ids, String userName, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(accessStatement, ids, userName, session)).thenReturn(false);
    }

    public static void disallowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, List<Long> ids, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(eq(accessStatement), eq(ids), anyString(), eq(session))).thenReturn(false);
    }

    public static void disallowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, Collection<Long> ids, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(eq(accessStatement), eq(ids), anyString(), eq(session))).thenReturn(
            Boolean.FALSE);
    }

    public static void disallowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(eq(accessStatement), anyListOf(Long.class), anyString(), eq(session))).thenReturn(false);
    }

    public static void allowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, List<Long> ids, String userName, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(accessStatement, ids, userName, session)).thenReturn(true);
    }

    public static void allowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, List<Long> ids, SqlSession session){
        when(mockedAccessControl.isUserValidFor(eq(accessStatement), eq(ids), anyString(), eq(session))).thenReturn(true);
    }

    public static void allowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, SqlSession session)  {
        when(mockedAccessControl.isUserValidFor(eq(accessStatement), anyListOf(Long.class), anyString(), eq(session))).thenReturn(true);
    }

    public static void allowAccessForUser(AccessControl mockedAccessControl, AccessStatement accessStatement, Collection<Long> ids, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(eq(accessStatement), eq(ids), anyString(), eq(session))).thenReturn(
            Boolean.TRUE);
    }

    public static void allowAllForUser(AccessControl mockedAccessControl, SqlSession session) {
        when(mockedAccessControl.isUserValidFor(any(AccessStatement.class), anyListOf(Long.class), anyString(), eq(session))).thenReturn(
                Boolean.TRUE);
    }
}
