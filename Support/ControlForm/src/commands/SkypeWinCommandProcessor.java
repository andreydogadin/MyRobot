package commands;

import com.skype.Skype;
import com.skype.SkypeException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 1/31/12
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class SkypeWinCommandProcessor implements ICommandProcessor{

    private String executorFile = "MsgToSkype.exe";
    public void sendMessage(String message) throws Exception{
        String command = "-U " + ROBOT_SKYPE_ID + " \"" + message+ "\"";
        System.out.println(executorFile + " " + command);
        Process process = Runtime.getRuntime().exec(executorFile + " "+command);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s;
        while((s = bufferedReader.readLine()) != null) System.out.println(s);
    }
}
