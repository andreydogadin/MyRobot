package commands;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 1/31/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICommandSender {
    public static String ROBOT_SKYPE_ID = "home.robot0";

    public void sendMessage(String message) throws Exception;

    public String recieveMessage();

    public void close();
}
