package workflow;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 03.01.12
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowPoint {
    Integer currentStep;
    Integer nextStep;
    String inputString;
    String command;

    public String getCommand() {
        return command;
    }

    public String getInputString() {
        return inputString;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public Integer getNextStep() {
        return nextStep;
    }

    public WorkflowPoint(Integer currentStep, Integer nextStep, String inputString, String command) {
        this.currentStep = currentStep;
        this.nextStep = nextStep;
        this.inputString = inputString;
        this.command = command;
    }

}
