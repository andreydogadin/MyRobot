package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 1/17/12
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RobotChangeOnOff extends RobotCommand{
    @Override
    protected void executeCommand(MyRobot robot) {
        robot.changeOnOff();
    }
}
