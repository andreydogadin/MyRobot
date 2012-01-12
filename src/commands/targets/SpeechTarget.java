package commands.targets;

import robot.MyRobot;
import utils.RobotConsts;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 12.10.11
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class SpeechTarget extends ResultTarget{

    private static SpeechTarget _instance = null;
    private SpeechTarget() {}
    public static synchronized SpeechTarget getInstance() {
        if (_instance == null)
            _instance = new SpeechTarget();
        return _instance;
    }
    @Override
    public void outResult(String result) {
        if (result.isEmpty())
            MyRobot.getInstance().getSpeechSynth().read("NOT");
        else {
             MyRobot.getInstance().getSpeechSynth().read(result);
        }
    }
}
