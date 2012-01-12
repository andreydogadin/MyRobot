package workflow;

import commands.CommandDispatcher;
import commands.CommandFactory;
import commands.RobotCommand;
import commands.datatypes.InputString;
import robot.MyRobot;
import workflow.workflowsImpl.CheckMyMailWorkflow;
import workflow.workflowsImpl.PlayMusicWorkflow;
import robot.media.Memory;
import utils.RobotConsts;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 03.01.12
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowManager {
    private static WorkflowManager _instance = null;
    private static ArrayList <Workflow> workflows;

    private WorkflowManager() {
        workflows = new ArrayList<Workflow>();
        workflows.add(new PlayMusicWorkflow());
        workflows.add(new CheckMyMailWorkflow());

    }
    private static synchronized WorkflowManager getInstance() {
        if (_instance == null)
            _instance = new WorkflowManager();
        return _instance;
    }

    public static void process(InputString inputString){
        WorkflowManager.getInstance();
        Workflow activeWorkflow = null;

        // Find in memory
        Memory.MemoryItem memoryItem = MyRobot.getInstance().getMemory().getCurrent(RobotConsts.CURRENT_WORKFLOW);
        if (memoryItem != null)
            activeWorkflow = (Workflow)memoryItem.getValue();
        if (activeWorkflow != null) {
            activeWorkflow.processStep(inputString);
            if (activeWorkflow.getNextStep(inputString) != 0)
                MyRobot.getInstance().getMemory().putCurrent(RobotConsts.CURRENT_WORKFLOW, activeWorkflow, 30);
            else
                MyRobot.getInstance().getMemory().forgetCurrent(RobotConsts.CURRENT_WORKFLOW);
            return;
        }

        // Find in array
        for (Workflow wf : workflows){
            for ( WorkflowPoint wp: wf.getWorkflowPoints()){
                if (wp.getInputString().equalsIgnoreCase(inputString.getValue()) && wp.currentStep == 0){
                    activeWorkflow = wf;
                    break;
                }
            }
        }
        if (activeWorkflow != null){
            // start new workflow
            activeWorkflow.processStep(inputString);
            MyRobot.getInstance().getMemory().putCurrent(RobotConsts.CURRENT_WORKFLOW, activeWorkflow, 30);
        }
        else{
            // Execute as usual command
            RobotCommand command = CommandFactory.getCommand(inputString);
            CommandDispatcher.getInstance().addCommand(command);
        }
    }

}
