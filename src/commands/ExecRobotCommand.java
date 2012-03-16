package commands;

import robot.MyRobot;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 26.09.11
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExecRobotCommand extends RobotCommand{
    @Override
    protected void executeCommand(MyRobot robot) {
        String className = this.getClass().getSimpleName();
        Utils.exec(className, this.getParam());
    }
}
