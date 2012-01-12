package commands.commandsImpl;

import commands.RobotCommand;
import commands.targets.SpeechTarget;
import robot.MyRobot;
import utils.RobotConsts;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 24.10.11
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
public class Alarm extends RobotCommand {
    @Override
    public void execute(MyRobot robot) {
        Date alarmDate = null;
        this.resultTarget = SpeechTarget.getInstance();
        if (robot.getMemory().getCurrent(RobotConsts.MEMORY_ALARM_TIME)!=null)
            alarmDate = (Date)robot.getMemory().getCurrent(RobotConsts.MEMORY_ALARM_TIME).getValue();
        if (alarmDate!= null && new Date().after(alarmDate))
            resultTarget.outResult("Wake up, Sir!");
    }
}
