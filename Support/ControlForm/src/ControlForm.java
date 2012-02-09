import com.skype.*;
import commands.ButtonsListener;
import commands.ICommandProcessor;
import commands.SkypeLinuxCommandProcessor;
import commands.SkypeWinCommandProcessor;
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
        ICommandProcessor commandProcessor = new SkypeWinCommandProcessor();
        ButtonsListener.setCommandProcessor(commandProcessor);

        ControlFrame frame = new ControlFrame();
    }
}
