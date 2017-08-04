package trueffect.truconnect.api.crud.mybatis.reshandler.accumulator;

import java.util.Collection;

/**
 * Created by richard.jaldin on 4/1/2016.
 */
public class CollectionAccumulatorImpl<T extends Collection> implements Accumulator<T> {

    private T accumulatedResult;

    public CollectionAccumulatorImpl(T resultAccumulator) {
        this.accumulatedResult = resultAccumulator;
    }

    @Override
    public void accumulate(T partialResult) {
        accumulatedResult.addAll(partialResult);
    }

    @Override
    public T getAccumulatedResults() {
        return accumulatedResult;
    }
}
