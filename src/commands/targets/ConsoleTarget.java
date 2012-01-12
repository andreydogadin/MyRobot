package commands.targets;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 12.10.11
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleTarget extends ResultTarget{
    private static ConsoleTarget _instance = null;
    private ConsoleTarget() {}
    public static synchronized ConsoleTarget getInstance() {
        if (_instance == null)
            _instance = new ConsoleTarget();
        return _instance;
    }
    @Override
    public void outResult(String result) {
        System.out.println(result);
    }
}
