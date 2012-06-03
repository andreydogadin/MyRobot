package commands.commandsImpl;

import commands.RobotCommand;
import commands.datatypes.EMail;
import commands.datatypes.EMailAddress;
import robot.MyRobot;
import robot.media.network.mail.EMailFetcher;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 07.01.12
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class CheckMail extends RobotCommand{
    @Override
    protected void executeCommand(MyRobot robot) {
        if (this.getParam().isEmpty())
            this.resultTarget.outResult("No person was defined");
        else {
            EMailAddress address = EMailAddress.getEMailAddressByName(this.getParam());
            EMailFetcher fetcher = new EMailFetcher(address);
            ArrayList<EMail> emails = fetcher.fetch();
            StringBuilder sb = new StringBuilder();
            for (EMail e: emails){
                sb.append(e.toString());
            }
            this.resultTarget.outResult(sb.toString());
        }
    }
}
