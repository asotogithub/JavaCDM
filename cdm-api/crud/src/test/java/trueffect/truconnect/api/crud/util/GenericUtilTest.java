package trueffect.truconnect.api.crud.util;

import org.junit.Test;
import trueffect.truconnect.api.commons.model.Creative;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


/**
 * Created by jfryer on 6/27/16.
 */
public class GenericUtilTest {

    @Test
    public void testNonOverlapObjectCopy() throws Exception{
        Creative c1 = new Creative();
        Creative c2 = new Creative();

        c1.setFilename("c1FileName");
        c1.setAlias("c1Alias");

        c2.setAgencyId(1234L);
        c2.setCampaignId(567L);

        GenericUtils.copyOnlyPopulatedFields(c1,c2);

        assertThat(c1.getAgencyId(),is(equalTo(c2.getAgencyId())));
        assertThat(c1.getCampaignId(),is(equalTo(c2.getCampaignId())));
        assertThat(c1.getFilename(),is(equalTo("c1FileName")));
        assertThat(c1.getAlias(),is(equalTo("c1Alias")));
        assertThat(c1.getExtProp2(),is(equalTo(null)));
    }

    @Test
    public void testOverlapObjectCopy() throws Exception{
        Creative c1 = new Creative();
        Creative c2 = new Creative();

        c1.setFilename("c1FileName");
        c1.setAlias("c1Alias");

        c2.setFilename("c2FileName");
        c2.setAlias(null);

        GenericUtils.copyOnlyPopulatedFields(c1,c2);

        assertThat(c1.getFilename(),is(equalTo("c2FileName")));
        assertThat(c1.getAlias(),is(equalTo("c1Alias")));
        assertThat(c1.getExtProp2(),is(equalTo(null)));
    }
}
