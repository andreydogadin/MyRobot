package commands.commandsImpl;

import commands.CommandDispatcher;
import commands.CommandFactory;
import commands.RobotCommand;
import commands.datatypes.InputString;
import robot.MyRobot;
import robothreads.VideoRecognitionThread;
import utils.RobotConsts;
import workflow.WorkflowManager;

import javax.wsdl.Input;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/16/11
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetCurrentPerson extends RobotCommand {

    @Override
    public void execute(MyRobot robot) {
        if (MyRobot.getInstance().getMemory().getCurrent(RobotConsts.CURRENT_VIEW_PERSON) != null){
            InputString is = new InputString(MyRobot.getInstance().getMemory().getCurrent(RobotConsts.CURRENT_VIEW_PERSON).getValue().toString(), this.resultTarget);
            WorkflowManager.process(is);
        } else {
            InputString is = new InputString("OutText Please, provide your name or stay in from of camera.", this.resultTarget);
            RobotCommand command = CommandFactory.getCommand(is);
            CommandDispatcher.getInstance().addCommand(command);
            new VideoRecognitionThread(this.resultTarget);
        }
    }
}
