package robothreads;

import commands.CommandDispatcher;
import commands.RobotCommand;
import robothreads.abstracts.RobotThread;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/10/11
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandThread extends RobotThread {

    public void run() {
        while (true) {
            RobotCommand command = CommandDispatcher.getInstance().getNextCommand();
            if (command != null)
            {
                log.info(command.getClass() + " is going to be executed.");
                CommandDispatcher.getInstance().executeNextCommand();
                log.info(command.getClass() + " has been executed.");
            }
            Utils.sleep(100);
        }
    }

}
