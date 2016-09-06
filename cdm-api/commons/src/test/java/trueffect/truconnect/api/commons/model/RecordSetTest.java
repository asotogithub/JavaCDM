package trueffect.truconnect.api.commons.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecordSetTest {

    private Random random = new Random();

    @Test
    public void testCreatingRecordSetWithNullCollection() {
        RecordSet<Long> longRecordSet = new RecordSet<>(null);
        assertThat(longRecordSet, is(notNullValue()));
        assertThat(longRecordSet.getPageSize(), is(equalTo(0)));
        assertThat(longRecordSet.getRecords(), hasSize(0));
        assertThat(longRecordSet.getStartIndex(), is(equalTo(0)));
        assertThat(longRecordSet.getTotalNumberOfRecords(), is(equalTo(0)));
    }

    @Test
    public void testCreatingRecordSetWithCollection() {
        int size = random.nextInt(20);
        List<Long> longs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            longs.add(random.nextLong());
        }
        RecordSet<Long> longRecordSet = new RecordSet<>(longs);
        assertThat(longRecordSet.getPageSize(), is(equalTo(size)));
        assertThat(longRecordSet.getRecords(), hasSize(size));
        assertThat(longRecordSet.getStartIndex(), is(equalTo(0)));
        assertThat(longRecordSet.getTotalNumberOfRecords(), is(equalTo(size)));
    }
}
