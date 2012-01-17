package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 1/17/12
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RobotGetOnOff extends RobotCommand{
    @Override
    public void execute(MyRobot robot) {
        robot.getOnOff();
    }
}
