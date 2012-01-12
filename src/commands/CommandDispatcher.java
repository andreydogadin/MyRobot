package commands;

import robot.MyRobot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/11/11
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandDispatcher {
    private List<RobotCommand> commandPipe;
    private static CommandDispatcher _instance = null;

    public static synchronized CommandDispatcher getInstance() {
        if (_instance == null)
            _instance = new CommandDispatcher();
        return _instance;
    }

    public CommandDispatcher() {
        commandPipe = new ArrayList<RobotCommand>();
    }

    synchronized public void addCommand(RobotCommand command) {
        if (command != null)
        {
            commandPipe.add(command);
        }
    }

    synchronized public void executeNextCommand() {
        if (commandPipe.size() > 0) {
            try {
                commandPipe.get(0).execute(MyRobot.getInstance());
            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
                commandPipe.remove(0);
            }
        }
    }
    synchronized public RobotCommand getNextCommand() {
        if (commandPipe.size() > 0) {
            return this.commandPipe.get(0);
        }
        return null;
    }
}
