package trueffect.truconnect.api.commons.model.dto;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import trueffect.truconnect.api.commons.model.CreativeGroup;
import trueffect.truconnect.api.commons.model.CreativeInsertion;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreativeInsertionBulkUpdateTest {

    private Random random = new Random();

    @Test
    public void gettingCreativeInsertionIdsWhenListIsNullShouldReturnEmptyList() {
        CreativeInsertionBulkUpdate cibu = new CreativeInsertionBulkUpdate();
        cibu.setCreativeInsertions(null);

        assertThat(cibu.getCreativeInsertionIds(), is(empty()));
    }

    @Test
    public void gettingCreativeInsertionIdsWhenListIsNotNullShouldGetAllIds() {
        List<Long> ciIds = new ArrayList<>();
        List<CreativeInsertion> creativeInsertions = new ArrayList<>();

        int num = random.nextInt(100);

        for (int i = 0; i < num; i++) {
            Long ciId = random.nextLong();
            CreativeInsertion ci = new CreativeInsertion();
            ci.setId(ciId);
            ciIds.add(ciId);
            creativeInsertions.add(ci);
        }

        CreativeInsertionBulkUpdate cibu = new CreativeInsertionBulkUpdate();
        cibu.setCreativeInsertions(creativeInsertions);

        assertThat(cibu.getCreativeInsertionIds(), hasSize(num));
        assertThat(cibu.getCreativeInsertionIds(), is(equalTo(ciIds)));
    }

    @Test
    public void gettingCreativeGroupIdsWhenListIsNullShouldReturnEmptyList() {
        CreativeInsertionBulkUpdate cibu = new CreativeInsertionBulkUpdate();
        cibu.setCreativeGroups(null);

        assertThat(cibu.getCreativeGroupIds(), is(empty()));
    }

    @Test
    public void gettingCreativeGroupIdsWhenListIsNotNullShouldGetAllIds() {
        List<Long> cgIds = new ArrayList<>();
        List<CreativeGroup> creativeGroups = new ArrayList<>();

        int num = random.nextInt(100);

        for (int i = 0; i < num; i++) {
            Long cgId = random.nextLong();
            CreativeGroup cg = new CreativeGroup();
            cg.setId(cgId);
            cgIds.add(cgId);
            creativeGroups.add(cg);
        }

        CreativeInsertionBulkUpdate cibu = new CreativeInsertionBulkUpdate();
        cibu.setCreativeGroups(creativeGroups);

        assertThat(cibu.getCreativeGroupIds(), hasSize(num));
        assertThat(cibu.getCreativeGroupIds(), is(equalTo(cgIds)));
    }
}
