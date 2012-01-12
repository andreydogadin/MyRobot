package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 07.01.12
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class CheckMail extends RobotCommand{
    @Override
    public void execute(MyRobot robot) {
        if (this.getParam().isEmpty())
            this.resultTarget.outResult("What kind of music you want to listen?");
        else {
            this.resultTarget.outResult(this.getParam());
        }
    }
}
