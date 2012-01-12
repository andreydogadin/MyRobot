package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import utils.RobotConsts;

import java.net.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 09.10.11
 * Time: 21:05
 * To change this template use File | Settings | File Templates.
 */
public class CheckInternetConnection extends RobotCommand {
    @Override
    public void execute(MyRobot robot) {
        String urlRoute = "192.168.1.1";
        String urlInternet = "ya.ru";
        String result = "No network connection";

        try {
            Socket socket = new Socket(urlRoute, 80);
            result = "Router is available, but not Internet";
            socket.close();
            socket = new Socket(urlInternet, 80);
            result = "Internet is available";
            socket.close();
            robot.getMemory().putCurrent(RobotConsts.MEMORY_INTERNET_STATUS, result, 600);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        log.info("Internet state: " + result);
        this.resultTarget.outResult(result);


    }
}
