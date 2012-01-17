package robot.view.face;

import robot.media.Memory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 20.09.11
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class RobotFace extends JFrame {
    private StatusPanel statusPanel = new StatusPanel();
    private MemoryPanel memoryPanel = new MemoryPanel();

    public RobotFace() {
    //Create and set up the window.
        JFrame frame = new JFrame("Eva");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //panel.setOpaque(true); //content panes must be opaque
        //frame.setContentPane(panel);
        frame.add(statusPanel);
        frame.add(memoryPanel);
        frame.setLayout(new GridLayout(2, 1));
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setSize(600, 120);
    }

    public void setRecognizerState(String recognizerState){
        if (recognizerState == null) return;
        this.statusPanel.recognizerState.setText(recognizerState);
        if (recognizerState.equalsIgnoreCase("recognizerListening"))
            this.statusPanel.recognizerState.setBackground(Color.GREEN);
        else
            this.statusPanel.recognizerState.setBackground(Color.RED);
    }

    public void setPlatformAkkuState(Integer value)
    {
        statusPanel.platformAkku.setText("Platform battery: " + value.toString() + "%");
        statusPanel.platformAkku.setBackground(getColorForValue(value));
    }

    public void setLaptopAkkuState(Integer value)
    {
        statusPanel.laptopAkku.setText("Laptop battery: " + value.toString() + "%");
        statusPanel.laptopAkku.setBackground(getColorForValue(value));
    }

    public void updateMemory(HashMap<String, Memory.MemoryItem> memoryData){
        this.memoryPanel.setData(memoryData);
    }

    public void updatePlatformOnOff(Boolean value){
        statusPanel.platformOnOff.setText(value.toString());
        statusPanel.platformOnOff.setBackground(getColorForBoolean(value));
    }

    private Color getColorForValue(Integer value)
    {
        if (value <= 25) return Color.RED;
        if (value > 25 && value <= 85) return Color.YELLOW;
        if (value > 85) return Color.GREEN;
        return Color.BLACK;
    }
    
    private Color getColorForBoolean(Boolean value){
        if (value) return Color.GREEN;
        else return Color.RED;
    }

}
