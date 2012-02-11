import com.skype.Skype;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 27.10.11
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class SkypeAutoResp {
    public static void main(String[] args) throws Exception {
        final TrayIcon trayIcon = new TrayIcon(new ImageIcon("resources/icon.png").getImage());
        final SystemTray tray = SystemTray.getSystemTray();
        final PopupMenu popup = new PopupMenu();
        tray.add(trayIcon);
        MenuItem closeItem = new MenuItem("Close");
        closeItem.addActionListener(new CloseActionListener());
        popup.add(closeItem);
        trayIcon.setPopupMenu(popup);

        Skype.addCallListener(new CallAdapterDisp());
    }

}
