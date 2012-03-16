package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.10.11
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class RobotCheckSensors extends RobotCommand {
    @Override
    protected void executeCommand(MyRobot robot) {
        robot.checkSensors();
    }
}
