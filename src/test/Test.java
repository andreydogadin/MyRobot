package test;

import commands.CommandDispatcher;
import commands.RobotCommand;
import commands.commandsImpl.LearnFace;
import commands.targets.ConsoleTarget;
import robot.MyRobot;
import robot.media.network.mail.EMailFetcher;
import robothreads.VideoRecognitionThread;
import utils.RobotConsts;
import utils.Utils;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 15.10.11
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        ArrayList t = new EMailFetcher(RobotConsts.eMailAddresses[0]).fetch();
    }
}
