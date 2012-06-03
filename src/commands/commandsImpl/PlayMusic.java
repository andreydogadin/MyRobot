package commands.commandsImpl;

import commands.ExecRobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.09.11
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class PlayMusic extends ExecRobotCommand {

    @Override
    protected void executeCommand(MyRobot robot) {
        if (this.getParam().isEmpty())
            this.resultTarget.outResult("What kind of music you want to listen?");
        else
            super.executeCommand(robot);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
