package robot.view.face;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 21.09.11
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
   public class StatusPanel extends JPanel {
        protected JButton recognizerState, laptopAkku, platformAkku, platformOnOff;

        public StatusPanel() {

            recognizerState = new JButton("NA");
            recognizerState.setVerticalTextPosition(AbstractButton.CENTER);
            recognizerState.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
            recognizerState.setBackground(Color.RED);

            laptopAkku = new JButton("NA");
            laptopAkku.setEnabled(false);

            platformAkku = new JButton("NA");
            platformAkku.setEnabled(false);

            platformOnOff = new JButton("NA");
            platformOnOff.setEnabled(false);

            //Add Components to this container, using the default FlowLayout.
            add(recognizerState);
            add(laptopAkku);
            add(platformAkku);
            add(platformOnOff);
            this.setLayout(new GridLayout(1,3));
        }
    }
