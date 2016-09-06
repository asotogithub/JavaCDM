package trueffect.truconnect.api.crud.mybatis.reshandler;

import trueffect.truconnect.api.commons.Constants;

/**
 * Defines methods to allow accumulation of result sets for queries
 * that use {@code IN} clauses. By default, when a set of IDs is provided,
 * it will run in chunks of one thousand ids (1000)
 * Created by marcelo.heredia on 3/2/2016.
 * @author Marcelo Heredia
 */
public interface ResultSetAccumulator<T> {
    /**
     * Defines the method where the query will be executed
     * @param parameters Contains all of the parameters for the query, but guarantees that the collection of
     *                   IDs is limited to a page size. For example, for {@code ResultSetAccumulatorImpl}, the default
     *                   value of each page is {@link Constants#MAX_NUMBER_VALUES_IN_CLAUSE}
     * @return The results for the limited number of parameters
     */
    T execute(Object parameters);

    /**
     * Obtains the whole list of results already accumulated
     * @return The list of accumulated results
     */
    T getResults();
}
