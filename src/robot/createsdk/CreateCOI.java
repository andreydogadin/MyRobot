package robot.createsdk;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/10/11
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateCOI {
    public static byte START = (byte) 128;
    public static byte DRIVE_DIRECT = (byte) 145;

    public static byte SPEED = (byte) 150;
    public static byte SPEED_BACK = (byte) 300;

    public static byte SAFE = (byte) 131;
    public static byte FULL = (byte) 132;
    public static byte DEMO = (byte) 136;
    public static byte SET_MUSIC = (byte) 140;
    public static byte PLAY_MUSIC = (byte) 141;

    public static byte BEGIN_SCRIPT = (byte) 152;
    public static byte PLAY_SCRIPT = (byte) 153;

    public static byte LEDS = (byte) 139;
    public static byte LED_NOTHING = (byte) 0;
    public static byte LED_PLAY = (byte) 2;
    public static byte LED_ADV = (byte) 8;
    public static byte LED_PWR = (byte) 127;

    public static byte SENSORS = (byte) 142;
    public static byte AKKU_TEMP = (byte) 24;
    public static byte AKKU_CAPACITY = (byte) 25;
    public static byte AKKU_CAPACITY_TOTAL = (byte) 26;
    public static byte OI_MODE = (byte) 35;

    public static byte AT_GET_STATUS  = (byte)'G';
    public static byte AT_SET_STATUS  = (byte)'S';
    public static byte AT_STATUS_ON  = (byte)'N';
    public static byte AT_STATUS_OFF = (byte)'F';


}
