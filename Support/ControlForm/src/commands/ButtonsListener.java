package commands;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 27.10.11
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class ButtonsListener implements MouseListener {
    private static ICommandSender commandSender;

    public static void setCommandSender(ICommandSender myCommandSender) {
        commandSender = myCommandSender;
    }

    public void mousePressed(MouseEvent e) {
        String command = ((RobotControlButton)e.getSource()).getCommand();
        try{
            commandSender.sendMessage(command);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (((RobotControlButton)e.getSource()).isShouldStop())
            try{
                commandSender.sendMessage("RobotStop");
            } catch (Exception ex){
                ex.printStackTrace();
            }
    }

    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseClicked(MouseEvent e) {
    }

}


