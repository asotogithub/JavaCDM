package trueffect.truconnect.api.crud.mybatis.reshandler.accumulator;

import java.util.Map;

/**
 * Created by richard.jaldin on 4/1/2016.
 */
public class MapAccumulatorImpl<T extends Map> implements Accumulator<T> {

    private T accumulatedResult;

    public MapAccumulatorImpl(T resultAccumulator) {
        this.accumulatedResult = resultAccumulator;
    }

    @Override
    public void accumulate(T partialResult) {
        accumulatedResult.putAll(partialResult);
    }

    @Override
    public T getAccumulatedResults() {
        return accumulatedResult;
    }
}
