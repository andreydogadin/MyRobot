package robothreads.abstracts;

import org.apache.log4j.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 22.08.11
 * Time: 17:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class RobotThread extends Thread {
    protected Logger log;


    public RobotThread() {
        log = Logger.getLogger(this.getClass());
        start();
    }
    public abstract void run();

}
