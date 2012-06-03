package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 29.08.11
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class LearnFace extends RobotCommand {
    @Override
    protected void executeCommand(MyRobot robot) {
        robot.initVideo();
        robot.getVideoRecognizer().learnFace(this.getParam());
    }

}
