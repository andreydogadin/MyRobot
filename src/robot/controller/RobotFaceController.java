package robot.controller;

import robot.MyRobot;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 05.01.12
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */
public class RobotFaceController {
    public void updateRecognizerState(){
        MyRobot.getInstance().getFace().setRecognizerState(MyRobot.getInstance().getRecognitionState());
    }

    public void updatePlatformAkkuState(){
        MyRobot.getInstance().getFace().setPlatformAkkuState(MyRobot.getInstance().getPlatformAkkuState());
    }

    public void updateLaptopAkkuState(){
        MyRobot.getInstance().getFace().setLaptopAkkuState(MyRobot.getInstance().getLaptopAkkuState());
    }

    public void updateMemory(){
        MyRobot.getInstance().getFace().updateMemory(MyRobot.getInstance().getMemory().getCurrentMemory());
    }


    public void updatePlatformOnOff(){
        MyRobot.getInstance().getFace().updatePlatformOnOff(MyRobot.getInstance().isPlatformOn());
    }

    public void updateFace(){
        updateRecognizerState();
        updateLaptopAkkuState();
        updatePlatformAkkuState();
        updateMemory();
        updatePlatformOnOff();
    }
}
