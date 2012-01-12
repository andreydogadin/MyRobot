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
public class CheckSensors extends RobotCommand {
    @Override
    public void execute(MyRobot robot) {
        robot.checkSensors();
    }
}
