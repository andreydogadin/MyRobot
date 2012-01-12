package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 29.10.11
 * Time: 20:41
 * To change this template use File | Settings | File Templates.
 */
public class RobotBack extends RobotCommand {
    @Override
    public void execute(MyRobot robot) {
        robot.back();
    }
}
