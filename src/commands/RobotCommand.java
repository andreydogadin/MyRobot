package commands;

import commands.targets.ConsoleTarget;
import commands.targets.ResultTarget;
import org.apache.log4j.Logger;
import robot.MyRobot;
import utils.RobotConsts;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/10/11
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RobotCommand {
    protected Logger log;
    protected HashMap<String, String> params;

    public ResultTarget getResultTarget() {
        return resultTarget;
    }

    public void setResultTarget(ResultTarget resultTarget) {
        this.resultTarget = resultTarget;
    }

    protected ResultTarget resultTarget;

    public RobotCommand() {
        params = new HashMap<String, String> ();
        log = Logger.getLogger(this.getClass());
        resultTarget = ConsoleTarget.getInstance();
    }

    public void setParam(String param){
        this.params.put(RobotConsts.COMMAND_PARAM_1, param);
    }

    public String getParam(){
        if (this.params.get(RobotConsts.COMMAND_PARAM_1) == null)
            return "";
        else
            return this.params.get(RobotConsts.COMMAND_PARAM_1);
    }

    abstract public void execute(MyRobot robot);
}
