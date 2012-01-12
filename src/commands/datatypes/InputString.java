package commands.datatypes;

import commands.targets.ResultTarget;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 03.01.12
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class InputString {
    private String value;
    private ResultTarget target;

    public String getValue() {
        return value;
    }

    public ResultTarget getTarget() {
        return target;
    }

    public InputString(String value, ResultTarget target) {
        this.value = value;
        this.target = target;
    }
}
