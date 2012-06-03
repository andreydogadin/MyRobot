package utils;

import commands.datatypes.EMailAddress;
import jssc.SerialPort;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/12/11
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RobotConsts {
    public enum ExecutionMode {DO_NOTHING, SIMULATE, EXECUTE};
    public static String ROBOT_COM_PORT = "COM8";
    public static String AT_COM_PORT = "COM1";
    public static HashMap<String, Integer> portSpeeds = new HashMap<String, Integer>();
    static {
        portSpeeds.put(ROBOT_COM_PORT, SerialPort.BAUDRATE_57600);
        portSpeeds.put(AT_COM_PORT, SerialPort.BAUDRATE_9600);
    }
    
    public static String COMMAND_PARAM_1 = "COMMAND_PARAM_1";

    public static String LEARN_PATH = "db\\learn";

    public static String CURRENT_VIEW_PERSON = "CURRENT_VIEW_PERSON";
    public static String MEMORY_INTERNET_STATUS = "MEMORY_INTERNET_STATUS";
    public static String ACCEPT_VOICE_COMMAND = "ACCEPT_VOICE_COMMAND";
    public static String MEMORY_ALARM_TIME = "MEMORY_ALARM_TIME";
    public static String CURRENT_WORKFLOW = "CURRENT_WORKFLOW";
    public static String CURRENT_VIEW_JPEG = "C:\\Windows\\Temp\\RobotCurrentView.jpg";
    public static String RECOGNITION_GRAMMAR = "grammar.txt";
    public static String EXEC_COMMANDS_PATH = "D:\\Dropbox\\iCreate\\Project\\scripts\\";
    public static String DORGEM_PATH ="D:\\Dropbox\\iCreate\\Project\\scripts\\tools\\external\\Dorgem.exe";

    public static String OWNER_SKYPE_ID = "andrey.dogadin";
    public static String ROBOT_SKYPE_ID = "home.robot0";
    public static String WEATHER_BORYSPOL = "UPXX0003";
    public static String WEATHER_KIEV = "UPXX0016";

    public static EMailAddress[] eMailAddresses = new EMailAddress[2];
    static {
        eMailAddresses[0] = new EMailAddress("Andrey Dogadin", "andrey.dogadin@mail.ru", ".iddqd17");
        eMailAddresses[1] = new EMailAddress("Osipenko Lyudmila", "", "");
    }
}
