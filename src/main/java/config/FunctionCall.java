package config;

import model.Fun;
import model.Variable;

public class FunctionCall {
    private final int recursionDepth;
    private final Fun function;

    private final Variable resultDestination;


    public FunctionCall(int recursionDepth, Fun function, Variable resultDestination) {
        this.recursionDepth = recursionDepth;
        this.function = function;
        this.resultDestination = resultDestination;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public Fun getFunction() {
        return function;
    }

    public Variable getResultDestination() {
        return resultDestination;
    }
}