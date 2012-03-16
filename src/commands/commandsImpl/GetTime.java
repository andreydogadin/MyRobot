package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.08.11
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public class GetTime extends RobotCommand {
    @Override
    protected void executeCommand(MyRobot robot) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String strDate = sdf.format(cal.getTime());
        String result = "<sayas class=\"time\">" + strDate + "</sayas>";
        this.resultTarget.outResult(result);
    }

}
