package commands.commandsImpl;

import commands.RobotCommand;
import robot.MyRobot;
import robot.createsdk.CreateCOI;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 25.02.12
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public class MoveCam extends RobotCommand{
    protected String validationMessage = "Wrong parameter format. Command should looks like MoveCam X+";
    @Override
    protected void executeCommand(MyRobot robot) {
        boolean paramsValid = true;
        String params = this.getParam();

        if (paramsValid){
            byte cam = (byte)(params.toCharArray())[0];
            byte command = (byte)params.toCharArray()[1];
            robot.moveCam(cam, command);
        }
        else
            this.resultTarget.outResult(validationMessage);
        //if (params.length() == 2){


    }

    @Override
    protected boolean validateParam() {
        String params = this.getParam();

        boolean paramsValid = true;
        if (params.length() != 2)
            paramsValid = false;
        else if((byte)params.toCharArray()[0] != CreateCOI.AT_CAM_X ||
                (byte)params.toCharArray()[0] != CreateCOI.AT_CAM_Y){
            paramsValid = false;
        }
        else if((byte)params.toCharArray()[1] != CreateCOI.AT_CAM_PLUS ||
                (byte)params.toCharArray()[1] != CreateCOI.AT_CAM_MINUS ||
                (byte)params.toCharArray()[1] != CreateCOI.AT_CAM_RESET){
            paramsValid = false;
        }
        return paramsValid;
    }
}