package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/11/11
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Exit extends RobotCommand {

    public void execute(MyRobot robot) {

        this.resultTarget.outResult(Utils.getDoneAnswer());
        robot.release();
        System.exit(0);
    }
}
