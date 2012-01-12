package utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 22.08.11
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static void printArray(byte[] values) {
        for (int i = 0; i < values.length; ++i) {
            int v = byteToUByte(values[i]);
            System.out.print(v);
            System.out.print(" ");
        }
        System.out.println("");
    }

    public static void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static int uBytesToInt(int high, int low) {
        return high * 256 + low;
    }

    public static int byteToUByte(byte src) {
        int i_src = src < 0 ? src + 256 : src;
        return i_src;
    }

    public static void exec(String execCommandToRun, String param) {
        String params;
        try {
            if (param != null)
                params = param;
            else params = "";
            Process p = Runtime.getRuntime().exec(RobotConsts.EXEC_COMMANDS_PATH + execCommandToRun + ".cmd " + params, null, new File(RobotConsts.EXEC_COMMANDS_PATH));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static String getOkAnswer() {
        return "Ok";
    }

    public static String getWaitAnswer() {
        return "Wait a second, please";
    }

    public static String getDoneAnswer() {
        return "Done";
    }

    public static String getReadyAnswer() {
        return "Yes";
    }
}
