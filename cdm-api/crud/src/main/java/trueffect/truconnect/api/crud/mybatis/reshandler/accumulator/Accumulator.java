package trueffect.truconnect.api.crud.mybatis.reshandler.accumulator;

/**
 * Defines methods to allow accumulation of different type of elements.
 * Created by richard.jaldin on 3/31/2016.
 * @author Richard Jaldin
 */
public interface Accumulator<T> {

    /**
     * Defines how to do the accumulation process
     * @param partialResult result to be accumulated
     */
    void accumulate(T partialResult);

    /**
     * Return the Accumulated results
     * @return Return the Accumulated results
     */
    T getAccumulatedResults();
}
