package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import utils.RobotConsts;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 23.09.11
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class SetAlarm extends RobotCommand {
    @Override
    protected void executeCommand(MyRobot robot) {
        if (this.getParam().isEmpty())
            this.resultTarget.outResult("Please, specify time to alarm");
        else
        {
            String alarmStr = this.getParam();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmStr));
            calendar.set(Calendar.MINUTE, 0);
            Date alarmDate = calendar.getTime();
            robot.getMemory().putCurrent(RobotConsts.MEMORY_ALARM_TIME, alarmDate, 60*60*24);
        }
    }
}
