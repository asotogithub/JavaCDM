package trueffect.truconnect.api.crud.mybatis.dim;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.Properties;

/**
 *
 * @author Gustavo Claure
 */
public class DimPersistenceContextMyBatisTest {

    @Test
    public void test() {
        DimPersistenceContextMyBatis context = new DimPersistenceContextMyBatis();
        Properties p = context.connectionProperties();
        assertThat(p.getProperty("url"), is("url test"));
        assertThat(p.getProperty("password"), is("password test"));
        assertThat(p.getProperty("driver"), is("driver test"));
        assertThat(p.getProperty("username"), is("username test"));
        assertThat(p.getProperty("notExistingProperty"), nullValue());
    }
}
