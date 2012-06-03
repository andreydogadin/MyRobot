import commands.*;
import gui.ControlFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 27.10.11
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class ControlForm {
    public static void main(String[] args) throws Exception{
        ICommandSender commandSender = new SocketCommandSender();
        ButtonsListener.setCommandSender(commandSender);

        ControlFrame frame = new ControlFrame();

        //commandSender.close();
    }
}
