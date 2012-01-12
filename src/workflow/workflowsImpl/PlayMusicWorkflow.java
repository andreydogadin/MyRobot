package workflow.workflowsImpl;

import workflow.Workflow;
import workflow.WorkflowPoint;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 03.01.12
 * Time: 19:21
 * To change this template use File | Settings | File Templates.
 */
public class PlayMusicWorkflow extends Workflow{
    public PlayMusicWorkflow() {
        super();

        String inputString = null;
        String commandString = null;

        inputString = "PlayMusic";
        commandString = "OutText What kind of music you want to listen?";
        this.getWorkflowPoints().add(new WorkflowPoint(0, 1, inputString, commandString));

        inputString = "Classic";
        commandString = "PlayMusic Classic";
        this.getWorkflowPoints().add(new WorkflowPoint(1, 0, inputString, commandString));

        inputString = "Ballads";
        commandString = "PlayMusic Ballads";
        this.getWorkflowPoints().add(new WorkflowPoint(1, 0, inputString, commandString));

        inputString = "Russian Rock";
        commandString = "PlayMusic RussianRock";
        this.getWorkflowPoints().add(new WorkflowPoint(1, 0, inputString, commandString));
    }
}
