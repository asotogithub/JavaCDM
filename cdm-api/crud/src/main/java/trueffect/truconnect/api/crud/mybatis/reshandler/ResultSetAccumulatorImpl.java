package trueffect.truconnect.api.crud.mybatis.reshandler;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;

import java.util.List;
import java.util.Map;

/**
 * Default Implementation of {@link ResultSetAccumulator}. It accumulates the results
 * of a query execution (defined inline the code) by separating out groups of 1000 IDs
 * (Oracle's limitation for SQL 'IN' statement)
 * <p/>
 * Created by marcelo.heredia on 3/2/2016.
 * @author Marcelo Heredia
 */
public abstract class ResultSetAccumulatorImpl<T> implements ResultSetAccumulator {

    private String idsKey;
    private List<?> ids;
    private Accumulator<T> resultsAccumulator;
    private Map<String, Object> parameters;

    /**
     * Default constructor
     * @param idsKey The name of the key property that holds the collection of IDs for the query
     * @param ids The collection of IDs to use in the query
     * @param resultsContainer The collection that will hold the accumulative results
     * @param parameters All the additional necessary parameters to run the query
     */
    public ResultSetAccumulatorImpl(String idsKey, List<?> ids, Accumulator<T> resultsContainer, Map<String, Object> parameters) {
        this.idsKey = idsKey;
        this.ids = ids;
        this.resultsAccumulator = resultsContainer;
        this.parameters = parameters;
    }

    @Override
    public abstract T execute(Object parameters);


    @Override
    public T getResults() {
        int fromIndex = 0;
        int iterations = (int) Math.ceil((double) ids.size() / (double) Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
        for (int i = 0; i < iterations; i++) {
            int toIndex = (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE) > ids.size() ?
                    ids.size() : (fromIndex + Constants.MAX_NUMBER_VALUES_IN_CLAUSE);
            parameters.put(idsKey, ids.subList(fromIndex, toIndex));
            // According to the type, add the elements obtained so far
            resultsAccumulator.accumulate(execute(parameters));
            fromIndex += Constants.MAX_NUMBER_VALUES_IN_CLAUSE;
        }
        return resultsAccumulator.getAccumulatedResults();
    }
}
