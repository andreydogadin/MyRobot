package robothreads;

import commands.CommandDispatcher;
import commands.commandsImpl.Alarm;
import commands.commandsImpl.CheckInternetConnection;
import commands.commandsImpl.RobotGetOnOff;
import robot.MyRobot;
import robothreads.abstracts.RobotThread;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 09.10.11
 * Time: 21:16
 * To change this template use File | Settings | File Templates.
 */
public class MaintainThread extends RobotThread {
    private Integer second = 1000;
    private Integer second10Init = 10;
    private Integer second30Init = 30;

    @Override
    public void run() {
        Integer second10 = second10Init;
        Integer second30 = second30Init;
        while (true) {
            second10--;
            second30--;

            MyRobot.getInstance().getMemory().forgetOldItems();

            if (second10 == 0) {
                second10 = second10Init;
                CommandDispatcher.getInstance().addCommand(new RobotGetOnOff());
                MyRobot.getInstance().getFaceController().updateFace();
            }

            if (second30 == 0) {
                second30 = second30Init;
                CommandDispatcher.getInstance().addCommand(new CheckInternetConnection());
                CommandDispatcher.getInstance().addCommand(new Alarm());
                //CommandDispatcher.getInstance().addCommand(new RobotCheckSensors());
                //CommandDispatcher.getInstance().addCommand(new Test());
            }
            Utils.sleep(second);
        }
    }
}
