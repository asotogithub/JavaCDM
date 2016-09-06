package trueffect.truconnect.api.crud.mybatis;

import org.junit.Before;
import org.junit.Test;
import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.MapAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Unit Tests for {@code ResultSetAccumulatorImpl}
 * Created by marcelo.heredia on 3/2/2016.
 */
public class ResultSetAccumulatorImplTest {

    public static final int NUMBER_OF_IDS = 9999;
    public static final int NUMBER_OF_PAGES = (int) Math.ceil((double) NUMBER_OF_IDS / (double) Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
    public static final int NUMBER_OF_ELEMENTS_IN_LAST_PAGE = NUMBER_OF_IDS % Constants.MAX_NUMBER_VALUES_IN_CLAUSE;
    private HashMap<String, Object> params;
    private List<Long> ids;
    private int pagesNumber = 0;
    private int keySuffix = 0;
    private int counter = 0;

    @Before
    public void setUp() {
        ids = new ArrayList<>();

        for(int i = 0; i < NUMBER_OF_IDS; i++) {
            ids.add((long) i);
        }
        params = new HashMap<>();
        params.put("ids", ids);
        params.put("otherParam", EntityFactory.random.nextLong());
        params.put("anotherParam", EntityFactory.random.nextInt());

    }

    @Test
    public void testPaging() {
        List<String> result = new ArrayList<>();

        Accumulator<List<String>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        new ResultSetAccumulatorImpl<List<String>>(
                "ids",
                ids,
                resultAccumulator,
                params) {
            @Override
            public List<String> execute(Object parameters) {
                pagesNumber++;
                List<String> creativeList = new ArrayList<String>(){{
                    add("Hi");
                }};
                return creativeList;
            }
        }.getResults();

        assertThat(pagesNumber, is(NUMBER_OF_PAGES));
    }

    @Test
    public void testNumberOfElementsPerPage() {
        List<String> result = new ArrayList<>();

        Accumulator<List<String>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        new ResultSetAccumulatorImpl<List<String>>(
                "ids",
                ids,
                resultAccumulator,
                params) {
            @Override
            public List<String> execute(Object parameters) {
                Map<String, Object> params = (Map) parameters;
                List<Long> ids = (List<Long>) params.get("ids");
                assertThat(ids, is(notNullValue()));
                if(pagesNumber == NUMBER_OF_PAGES - 1) {
                    assertThat(ids.size(), is(NUMBER_OF_ELEMENTS_IN_LAST_PAGE));
                } else {
                    assertThat(ids.size(), is(Constants.MAX_NUMBER_VALUES_IN_CLAUSE));
                }
                pagesNumber++;
                return new ArrayList<>();
            }
        }.getResults();
    }

    @Test
    public void testAccumulatedResultsAsCollection() {
        List<String> result = new ArrayList<>();

        Accumulator<List<String>> resultAccumulator = new CollectionAccumulatorImpl<>(result);
        result = new ResultSetAccumulatorImpl<List<String>>(
                "ids",
                ids,
                resultAccumulator,
                params) {
            @Override
            public List<String> execute(Object parameters) {
                return new ArrayList<String>(){{
                    add("Hello");
                    add("You");
                }};
            }
        }.getResults();
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(NUMBER_OF_PAGES * 2));
    }

    @Test
    public void testAccumulatedResultsAsVoid() {

        Accumulator<Void> emptyAccumulator = new Accumulator<Void>() {
            @Override
            public void accumulate(Void partialResult) {
                // Do nothing
            }

            @Override
            public Void getAccumulatedResults() {
                // Do nothing;
                return null;
            }
        };
        new ResultSetAccumulatorImpl<Void>(
                "ids",
                ids,
                emptyAccumulator,
                params) {
            @Override
            public Void execute(Object parameters) {
                counter++;
                return null;
            }
        }.getResults();
        assertThat(counter, is(10));
    }

    @Test
    public void testAccumulatedResultsAsMap() {
        Map<String, Long> result = new HashMap<>();

        Accumulator<Map<String, Long>> resultAccumulator = new MapAccumulatorImpl<>(result);
        result = new ResultSetAccumulatorImpl<Map<String, Long>>(
                "ids",
                ids,
                resultAccumulator,
                params) {
            @Override
            public Map<String, Long> execute(Object parameters) {
                keySuffix++;
                return new HashMap<String, Long>(){{
                    put("key_" + keySuffix, (long) keySuffix);
                }};
            }
        }.getResults();
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(NUMBER_OF_PAGES));
    }

    @Test
    public void testAccumulatedResultsAsSum() {
        Long result = 0L;
        Accumulator<Long> resultAccumulator = new SumAccumulatorImpl<>(result);
        result = new ResultSetAccumulatorImpl<Long>(
                "ids",
                ids,
                resultAccumulator,
                params) {
            @Override
            public Long execute(Object parameters) {
                return 1L;
            }
        }.getResults();
        assertThat(result, is(notNullValue()));
        assertThat(result, is(10L));
    }
}