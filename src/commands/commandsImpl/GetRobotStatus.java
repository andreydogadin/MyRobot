package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 29.10.11
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
public class GetRobotStatus  extends RobotCommand {

    @Override
    public void execute(MyRobot robot) {
        this.resultTarget.outResult(robot.getStatus());
    }
}
