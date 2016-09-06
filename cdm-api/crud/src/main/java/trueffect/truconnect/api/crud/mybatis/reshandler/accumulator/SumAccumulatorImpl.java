package trueffect.truconnect.api.crud.mybatis.reshandler.accumulator;

/**
 * Accumulates results for any number results.
 * Created by richard.jaldin on 3/31/2016.
 */
public class SumAccumulatorImpl<T> implements Accumulator<T> {

    private T accumulatedResult;

    public SumAccumulatorImpl(T resultAccumulator) {
        this.accumulatedResult = resultAccumulator;
    }

    @Override
    public void accumulate(T partialResult) {
        if (accumulatedResult instanceof Long) {
            accumulatedResult = (T) new Long((Long) accumulatedResult + (Long) partialResult);
        } else if (accumulatedResult instanceof Integer) {
            accumulatedResult = (T) new Integer((Integer) accumulatedResult + (Integer) partialResult);
        } else if (accumulatedResult instanceof Double) {
            accumulatedResult = (T) new Double((Double) accumulatedResult + (Double) partialResult);
        }
    }

    @Override
    public T getAccumulatedResults() {
        return accumulatedResult;
    }
}
