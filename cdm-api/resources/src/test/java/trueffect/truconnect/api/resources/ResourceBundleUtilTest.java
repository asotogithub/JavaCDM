package trueffect.truconnect.api.resources;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit Tests for @{code ResourceBundleUtil}
 * Created by marcelo.heredia on 11/23/2015.
 */
public class ResourceBundleUtilTest {
    @Test
    public void testGetStringWithParameter() throws Exception {
        String s = ResourceBundleUtil.getString("my.key", "Hello");
        assertThat(s, is(equalTo("My key says Hello!")));
    }
    @Test
    public void testGetStringWithUnknownKey() throws Exception {
        String s = ResourceBundleUtil.getString("my.unknown.key", "Extra param");
        assertThat(s, is(equalTo("my.unknown.key")));
    }
}