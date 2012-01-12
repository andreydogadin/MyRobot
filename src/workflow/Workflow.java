package workflow;

import commands.CommandDispatcher;
import commands.CommandFactory;
import commands.RobotCommand;
import commands.datatypes.InputString;

import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 03.01.12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class Workflow {
    private Integer currentStep;
    protected ArrayList <WorkflowPoint> workflowPoints;

    protected Workflow() {
        this.currentStep = 1;
        workflowPoints = new ArrayList<WorkflowPoint>();
    }

    public Integer getNextStep(InputString inputString){
        for (WorkflowPoint p : workflowPoints){
            if (p.getInputString().equalsIgnoreCase(inputString.getValue())){
                return p.nextStep;
            }
        }
        return -1;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public ArrayList<WorkflowPoint> getWorkflowPoints() {
        return workflowPoints;
    }

    public void processStep(InputString inputString){
        for (WorkflowPoint p : workflowPoints){
            if (p.getInputString().equalsIgnoreCase(inputString.getValue())){
                this.currentStep = p.getNextStep();
                RobotCommand command = CommandFactory.getCommand(new InputString(p.getCommand(), inputString.getTarget()));
                CommandDispatcher.getInstance().addCommand(command);
                break;
            }
        }
    }
}
