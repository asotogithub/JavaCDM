package trueffect.truconnect.api.commons.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import trueffect.truconnect.api.commons.util.Either;

import org.junit.Test;

public class SystemStatusTest {

    @Test
    public void testConstructor() {
        Either<String, Boolean> cmValue = Either.success(true);
        String metricsMessage = "I am a failure";
        Either<String, Boolean> metricsValue = Either.error(metricsMessage);

        SystemStatus systemStatus = new SystemStatus(cmValue, metricsValue);
        assertThat(systemStatus.getCmDbConnectionValid(), is(true));
        assertThat(systemStatus.getCmDbStatusMessage(), is(equalTo("Success")));
        assertThat(systemStatus.getMetricsDbConnectionValid(), is(false));
        assertThat(systemStatus.getMetricsDbStatusMessage(), is(equalTo(metricsMessage)));
    }
}
