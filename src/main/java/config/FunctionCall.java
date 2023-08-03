package config;

import model.Fun;
import model.Variable;

public class FunctionCall {
    private final int recursionDepth;
    private final Fun function;

    private final Variable resultDestination;
    private final int displacement;


    public FunctionCall(int recursionDepth, Fun function, Variable resultDestination, int displacement) {
        this.recursionDepth = recursionDepth;
        this.function = function;
        this.resultDestination = resultDestination;
        this.displacement = displacement;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public int getDisplacement() {
        return displacement;
    }

    public Fun getFunction() {
        return function;
    }

    public Variable getResultDestination() {
        return resultDestination;
    }
}