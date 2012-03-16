package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import utils.RobotConsts;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 24.10.11
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class StopAlarm extends RobotCommand {
    @Override
    protected void executeCommand(MyRobot robot) {
        robot.getMemory().forgetCurrent(RobotConsts.MEMORY_ALARM_TIME);
    }
}
