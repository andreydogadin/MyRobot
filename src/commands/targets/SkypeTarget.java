package commands.targets;

import com.skype.Skype;
import com.skype.SkypeException;
import utils.RobotConsts;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 12.10.11
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class SkypeTarget extends ResultTarget {
        private static SkypeTarget _instance = null;
    private SkypeTarget() {}
    public static synchronized SkypeTarget getInstance() {
        if (_instance == null)
            _instance = new SkypeTarget();
        return _instance;
    }
    @Override
    public void outResult(String result) {
        try {
            Skype.chat(RobotConsts.OWNER_SKYPE_ID).send(result);
        } catch (SkypeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
