package workflow.workflowsImpl;

import commands.datatypes.EMailAddress;
import utils.RobotConsts;
import workflow.Workflow;
import workflow.WorkflowPoint;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 03.01.12
 * Time: 19:21
 * To change this template use File | Settings | File Templates.
 */
public class CheckMyMailWorkflow extends Workflow{
    public CheckMyMailWorkflow() {
        super();

        String inputString = null;
        String commandString = null;

        inputString = "CheckMyMail";
        commandString = "SetCurrentPerson";
        this.getWorkflowPoints().add(new WorkflowPoint(0, 1, inputString, commandString));

        for (EMailAddress e : RobotConsts.eMailAddresses){
            inputString = e.getName();
            commandString = "CheckMail"+ " " + inputString;
            this.getWorkflowPoints().add(new WorkflowPoint(1, 0, inputString, commandString));
        }

    }
}
