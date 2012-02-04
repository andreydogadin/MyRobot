import robot.MyRobot;
import robothreads.*;


/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/10/11
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class Simulate {

    public static void main(String[] args) throws Exception {

        MyRobot.getInstance();
        MyRobot.getInstance().openPorts();

        new MaintainThread();

        //Thread that reads commands from console and pass them to dispatcher
        new ConsoleCommandThread();
        //new AudioRecognitionThread();

        //Thread that performs operations from command stack in dispatcher
        new CommandThread();
    }
}
