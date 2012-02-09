package gui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 12/6/11
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JFrame {
    public MainFrame(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setOpaque(true); //content panes must be opaque
        this.setContentPane(panel);
        this.setTitle("Skype autoresponder");

        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.setSize(280, 50);
        this.setAlwaysOnTop(true);
    }
}
