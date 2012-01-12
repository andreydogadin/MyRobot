package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/12/11
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class OutText extends RobotCommand {

    public void execute(MyRobot robot) {
        this.resultTarget.outResult(this.getParam());
    }
}
