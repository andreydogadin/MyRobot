package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import utils.RobotConsts;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 24.10.11
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class Eva extends RobotCommand {
    @Override
    public void execute(MyRobot robot) {
        this.resultTarget.outResult(Utils.getReadyAnswer());
        robot.getMemory().putCurrent(RobotConsts.ACCEPT_VOICE_COMMAND, new Boolean(true), 30);
    }
}
