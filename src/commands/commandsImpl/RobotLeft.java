package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/10/11
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class RobotLeft extends RobotCommand {
    public void execute(MyRobot robot) {
        robot.rotateLeft();
    }
}
