package robothreads;

import commands.CommandDispatcher;
import commands.commandsImpl.SetCurrentPerson;
import commands.datatypes.InputString;
import commands.targets.ResultTarget;
import robot.MyRobot;
import robothreads.abstracts.RobotThread;
import utils.RobotConsts;
import utils.Utils;
import workflow.WorkflowManager;

import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/16/11
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoRecognitionThread extends Thread{
    private Process proc;
    private ResultTarget resultTarget;
    public VideoRecognitionThread(ResultTarget resultTarget) {
        MyRobot.getInstance().initVideo();
        this.resultTarget = resultTarget;
        try {
            this.proc = Runtime.getRuntime().exec(RobotConsts.DORGEM_PATH);
            Utils.sleep(5000);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void run() {
        if (proc == null) return;
        Integer attemptsCount = 30;
        while (attemptsCount > 0) {
            String personName = MyRobot.getInstance().getVideoRecognizer().recognizeFace(RobotConsts.CURRENT_VIEW_JPEG);
            if (personName != null){
                MyRobot.getInstance().getMemory().putCurrent(RobotConsts.CURRENT_VIEW_PERSON, personName, 30);
                InputString is = new InputString(personName, this.resultTarget);
                WorkflowManager.process(is);
                System.out.println(personName);
                break;
            }
            Utils.sleep(1000);
            attemptsCount--;
            System.out.println(attemptsCount);
        }
        proc.destroy();
    }
}
