package robot.media.comport;

import jssc.SerialPort;
import jssc.SerialPortException;
import robot.MyRobot;
import utils.RobotConsts;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 1/17/12
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyComPort extends SerialPort {
    String portName;

    public MyComPort(String portName) {
        super(portName);
        this.portName = portName;
    }

    public boolean open() {
        boolean result = false;
        try {
            this.setParams(RobotConsts.portSpeeds.get(this.portName), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            this.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            this.addEventListener(MyRobot.getInstance(), SerialPort.MASK_RXCHAR);
            result = this.openPort();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public boolean release() {
        try {
            return this.closePort();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return false;
        }
    }
}
