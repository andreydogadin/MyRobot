package robot.createsdk;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import robot.media.comport.MyComPort;
import utils.RobotConsts;
import utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 29.08.11
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class RobotPlatform implements SerialPortEventListener{

    MyComPort robotComPort;
    MyComPort atComPort;

    protected int platformAkku;
    protected int akkuTemp;
    protected boolean platformOn;

    public RobotPlatform() {
        robotComPort = new MyComPort(RobotConsts.ROBOT_COM_PORT);
        atComPort = new MyComPort(RobotConsts.AT_COM_PORT);
    }
    public void openPorts(){
        robotComPort.open();
        atComPort.open();
    }

    public void release() {
        this.stop();
        this.robotComPort.release();
        this.atComPort.release();
    }

    public Integer getPlatformAkkuState() {
        return platformAkku;
    }

    public void start(){
        byte[] command = {CreateCOI.START, CreateCOI.FULL};
        this.executeRobot(command);
    }

    public void drive() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 0, CreateCOI.SPEED, (byte) 0, CreateCOI.SPEED};
        this.executeRobot(command);
    }

    public void back() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 255, (byte) 206, (byte) 255, (byte) 206};
        this.executeRobot(command);
    }


    public void stop() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        this.executeRobot(command);
    }


    public void rotateLeft() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte) 0, 50, (byte) 255, (byte)205};
        this.executeRobot(command);
    }
    
    public void rotateRight() {
        byte[] command = {CreateCOI.DRIVE_DIRECT, (byte)255, (byte)205, (byte) 0, 50};
        this.executeRobot(command);

    }

    public void demo(byte mode) {
        byte[] command = {CreateCOI.DEMO, mode};
        this.executeAt(command);
    }

    public void changeOnOff() {
        byte[] command = {CreateCOI.AT_SET_STATUS};
        this.executeAt(command);
        Utils.sleep(5000);
    }

    public void getOnOff() {
        byte[] command = {CreateCOI.AT_GET_STATUS};
        this.executeAt(command);
    }

    public void playMusic() {
        byte[] setMusic = {CreateCOI.SET_MUSIC, 0, 16, 91, 24, 89, 12, 87, 36, 87,
                  24, 89, 12, 91, 24, 91, 12, 9, 12, 89,
                  12,87, 12, 89, 12, 91, 12, 89, 12, 87,
                  24, 86, 12, 87, 48};
        this.executeRobot(setMusic);
        byte[] command = {CreateCOI.PLAY_MUSIC, 0};
        this.executeRobot(command);
    }

    public void moveCam(byte cam, byte command){
        byte [] camArr = {cam};
        byte [] commandArr = {command};
        this.executeAt(camArr);
        this.executeAt(commandArr);
    }

    public void playLeds() {
        byte[] advLed = {CreateCOI.LEDS, CreateCOI.LED_ADV, 0, 0};
        byte[] playLed = {CreateCOI.LEDS, CreateCOI.LED_PLAY, 0, 0};
        byte[] pwrLed = {CreateCOI.LEDS, CreateCOI.LED_NOTHING, (byte) 255, (byte)255};
        byte[] zeroLed = {CreateCOI.LEDS, CreateCOI.LED_NOTHING, 0, 0};

        this.executeRobot(zeroLed);
        Utils.sleep(100);
        this.executeRobot(advLed);
        Utils.sleep(100);
        this.executeRobot(playLed);
        Utils.sleep(100);
        this.executeRobot(pwrLed);
        Utils.sleep(100);
        this.executeRobot(zeroLed);
    }


    public void checkSensors()
    {
        byte[] command = {CreateCOI.SENSORS, 0};
        this.executeRobot(command);
    }

    public Boolean isPlatformOn() {
        return platformOn;
    }

    public synchronized void parseSensors(byte[] sensors) {
        if (sensors == null || sensors.length != 26) return;
        akkuTemp = Utils.byteToUByte(sensors[21]);
        int akkuTotal = Utils.uBytesToInt(Utils.byteToUByte(sensors[22]), Utils.byteToUByte(sensors[23]));
        int akkuState = Utils.uBytesToInt(Utils.byteToUByte(sensors[24]), Utils.byteToUByte(sensors[25]));
        if (akkuTotal == 0) platformAkku = 0;
            else platformAkku = akkuState/akkuTotal*100;
    }

    public synchronized void parseOnOff(byte[] data) {
        if (data == null || data.length < 0) return;
        if (data[0] == CreateCOI.AT_STATUS_ON)
            this.platformOn = true;
        if (data[0] == CreateCOI.AT_STATUS_OFF)
            this.platformOn = false;
    }


    public synchronized void executeRobot(byte[] command) {
        try {
            logBeforeSend(command);
            robotComPort.writeBytes(command);
        } catch (SerialPortException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public synchronized void executeAt(byte[] command) {
        try {
            logBeforeSend(command);
            atComPort.writeBytes(command);
        } catch (SerialPortException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
    public void logBeforeSend(byte [] data){
    }

    public void logAfterRelieve(byte [] data){
    }

    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0){
            try {
                byte [] data = null;
                if (event.getPortName().equalsIgnoreCase(RobotConsts.ROBOT_COM_PORT)){
                    data = this.robotComPort.readBytes();
                    this.parseSensors(data);
                }
                if (event.getPortName().equalsIgnoreCase(RobotConsts.AT_COM_PORT)){
                    data = this.atComPort.readBytes();
                    parseOnOff(data);
                }
                logAfterRelieve(data);
            }
            catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
    }

}
