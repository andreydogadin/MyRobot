package robot.createsdk;

import robot.media.comport.RobotComPort;
import utils.RobotConsts;
import utils.Utils;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 29.08.11
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class RobotPlatform {
    RobotComPort robotComPort;
    protected int platformAkku;
    protected int akkuTemp;


    public RobotPlatform() {
        byte[] command = {CreateCOI.START, CreateCOI.FULL};
        robotComPort = new RobotComPort();
        this.execute(command);
    }

    public void Release() {
        this.stop();
        if (this.robotComPort != null) this.robotComPort.Release();
    }

    public Integer getPlatformAkkuState() {
        return platformAkku;
    }

    public void drive() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 0, CreateCOI.SPEED, (byte) 0, CreateCOI.SPEED};
        this.execute(command);
    }

    public void back() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 255, (byte) 206, (byte) 255, (byte) 206};
        this.execute(command);
    }


    public void stop() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        this.execute(command);
    }


    public void rotateLeft() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 0, 50, (byte) 255, (byte)205};
        this.execute(command);

    }
    public void rotateRight() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte)255, (byte)205, (byte) 0, 50};
        this.execute(command);

    }


    public void Demo(byte mode) {
        byte[] command = {CreateCOI.DEMO, mode};
        this.execute(command);
    }


    public void playMusic() {
        byte[] setMusic = {CreateCOI.SET_MUSIC, 0, 16, 91, 24, 89, 12, 87, 36, 87,
                  24, 89, 12, 91, 24, 91, 12, 9, 12, 89,
                  12,87, 12, 89, 12, 91, 12, 89, 12, 87,
                  24, 86, 12, 87, 48};
        this.execute(setMusic);
        byte[] command = {CreateCOI.PLAY_MUSIC, 0};
        this.execute(command);
    }

    public void playLeds() {
        byte[] advLed = {CreateCOI.LEDS, CreateCOI.LED_ADV, 0, 0};
        byte[] playLed = {CreateCOI.LEDS, CreateCOI.LED_PLAY, 0, 0};
        byte[] pwrLed = {CreateCOI.LEDS, CreateCOI.LED_NOTHING, (byte) 255, (byte)255};
        byte[] zeroLed = {CreateCOI.LEDS, CreateCOI.LED_NOTHING, 0, 0};

        this.execute(zeroLed);
        Utils.sleep(100);
        this.execute(advLed);
        Utils.sleep(100);
        this.execute(playLed);
        Utils.sleep(100);
        this.execute(pwrLed);
        Utils.sleep(100);
        this.execute(zeroLed);
    }


    public void checkSensors()
    {
        byte[] command = {CreateCOI.SENSORS, 0};
        this.execute(command);
    }

    public synchronized void parseSensors(byte[] sensors, int length) {
        akkuTemp = Utils.byteToUByte(sensors[21]);
        int akkuTotal = Utils.uBytesToInt(Utils.byteToUByte(sensors[22]), Utils.byteToUByte(sensors[23]));
        int akkuState = Utils.uBytesToInt(Utils.byteToUByte(sensors[24]), Utils.byteToUByte(sensors[25]));
        if (akkuTotal == 0) platformAkku = 0;
            else platformAkku = akkuState/akkuTotal*100;
        System.out.println("Sensors ["+ length + "]: " + new String(sensors));
    }
    
    public synchronized void execute(byte[] command) {
        if (robotComPort.getOutputStream()!= null) {
            try {
                System.out.print("Command is arrived: ");
                Utils.printArray(command);
                Utils.sleep(100);
                robotComPort.getOutputStream().write(command);
                robotComPort.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
    

}
