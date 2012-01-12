package robot.media.comport;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 15.10.11
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */

import robot.MyRobot;
import utils.RobotConsts;
import utils.Utils;

import javax.comm.*;
import java.io.*;
import java.util.Enumeration;

public class RobotComPort implements SerialPortEventListener {

    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected SerialPort serialPort;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public RobotComPort() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier portId =
                    (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() ==
                    CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(RobotConsts.ROBOT_COM_PORT)) {

                    try {
                        serialPort = (SerialPort) portId.open("TerminalApp", 2000);
                        serialPort.addEventListener(this);
                        serialPort.notifyOnDataAvailable(true);
                        serialPort.setSerialPortParams(57600,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                        outputStream = serialPort.getOutputStream();
                        inputStream = serialPort.getInputStream();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void Release()
    {
        if (serialPort != null) serialPort.close();
    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                Utils.sleep(200);
                try {
                    byte [] readBuffer = new byte[200];
                    int numBytes = 0;
                    while (inputStream.available() > 0) {
                        numBytes = inputStream.read(readBuffer);
                    }
                    MyRobot.getInstance().parseSensors(readBuffer, numBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
