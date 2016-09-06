package trueffect.truconnect.api.crud.mybatis.reshandler.accumulator;

/**
 * Created by richard.jaldin on 8/19/2016.
 */
public class BooleanAccumulator implements Accumulator<Boolean> {

    private Boolean accumulatedResult;

    public BooleanAccumulator(Boolean resultAccumulator) {
        this.accumulatedResult = resultAccumulator;
    }

    @Override
    public void accumulate(Boolean partialResult) {
        accumulatedResult = accumulatedResult && partialResult;
    }

    @Override
    public Boolean getAccumulatedResults() {
        return accumulatedResult;
    }
}
