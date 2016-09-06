package trueffect.truconnect.api.crud.dao.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.crud.EntityFactory;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.impl.dim.DimCostDetailDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.service.AbstractManagerTest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author marleny.patsi
 */
public class DimCostdetaildaoImplTest extends AbstractManagerTest {

    private DimCostDetailDao dimCostDetailDao;
    private Set<Long> ids;
    private Map<String, Integer> mapCounter;
    private Map<String, Integer> elementsLastPage;

    private int numberIds;
    private int numberPages;
    private int numberElementsLastPage;

    @Before
    public void setUp() throws Exception {
        // Mock context
        PersistenceContext context = mock(PersistenceContext.class);

        // variables 
        ids = new HashSet<>();
        mapCounter = new HashMap<>();
        elementsLastPage = new HashMap<>();

        dimCostDetailDao = new DimCostDetailDaoImpl(context);

        // Mocks behaviors
        doAnswer(execute()).when(context).execute(anyString(), any(Object.class), eq(sqlSessionMock));
    }

    @Test
    public void testAcumulatorSinglePage() {
        // prepare data
        numberIds = Constants.MAX_NUMBER_VALUES_IN_CLAUSE - EntityFactory.random.nextInt(Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
        numberPages = 1;
        numberElementsLastPage = numberIds;

        ids = new HashSet<>(EntityFactory.getLongList(numberIds));

        // perform test
        dimCostDetailDao.dimHardRemoveCostDetails(ids, ids, sqlSessionMock);
        assertThat(mapCounter, is(notNullValue()));
        assertThat(mapCounter.size(), is(equalTo(6)));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_PLACEMENT), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_COST_DETAIL), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST_DETAIL), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PRODUCT_BUY_COST), is(numberPages));
        assertThat(elementsLastPage, is(notNullValue()));
        assertThat(elementsLastPage.size(), is(equalTo(6)));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_PLACEMENT), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_COST_DETAIL), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST_DETAIL), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PRODUCT_BUY_COST), is(numberElementsLastPage));
    }

    @Test
    public void testAcumulatorMultiplePages() {
        // prepare data
        numberIds = 9999;
        numberPages = (int) Math.ceil((double) numberIds / (double) Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
        numberElementsLastPage = numberIds % Constants.MAX_NUMBER_VALUES_IN_CLAUSE;

        ids = new HashSet<>(EntityFactory.getLongList(numberIds));

        // perform test
        dimCostDetailDao.dimHardRemoveCostDetails(ids, ids, sqlSessionMock);
        assertThat(mapCounter, is(notNullValue()));
        assertThat(mapCounter.size(), is(equalTo(6)));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_PLACEMENT), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_COST_DETAIL), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST_DETAIL), is(numberPages));
        assertThat(mapCounter.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PRODUCT_BUY_COST), is(numberPages));
        assertThat(elementsLastPage, is(notNullValue()));
        assertThat(elementsLastPage.size(), is(equalTo(6)));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_PLACEMENT), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PACKAGE_COST_DETAIL), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PLACEMENT_COST_DETAIL), is(numberElementsLastPage));
        assertThat(elementsLastPage.get(DimCostDetailDaoImpl.STATEMENT_REMOVE_FROM_PRODUCT_BUY_COST), is(numberElementsLastPage));
    }

    private Answer<Void> execute() {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                String statement = (String) invocation.getArguments()[0];
                HashMap<String, Object> params = (HashMap<String, Object>) invocation.getArguments()[1];
                List<Long> ids = (List<Long>) params.get("ids");

                if (mapCounter.get(statement) == null) {
                    mapCounter.put(statement, 0);
                }
                int counter = mapCounter.get(statement) + 1;
                mapCounter.put(statement, counter);
                if (elementsLastPage.get(statement) == null) {
                    elementsLastPage.put(statement, 0);
                }
                elementsLastPage.put(statement, ids.size());
                return null;
            }
        };
    }
}
