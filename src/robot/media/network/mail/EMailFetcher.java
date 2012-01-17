package robot.media.network.mail;

import commands.datatypes.EMail;
import commands.datatypes.EMailAddress;

import javax.mail.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 08.01.12
 * Time: 22:50
 * To change this template use File | Settings | File Templates.
 */
public class EMailFetcher {
    private EMailAddress eMailAddress;
    private static Integer MSG_TO_READ_CNT = 5;

    public EMailFetcher(EMailAddress eMailAddress) {
        this.eMailAddress = eMailAddress;
    }

    public ArrayList<EMail> fetch() {
        try {
            Session session = Session.getInstance(new Properties());
            Store store = session.getStore("pop3");
            store.connect(this.eMailAddress.getPop3Host(), this.eMailAddress.getPop3Port(), this.eMailAddress.getEmail(), this.eMailAddress.getPassword());
            Folder fldr = store.getFolder("INBOX");
            fldr.open(Folder.READ_ONLY);
            int count = fldr.getUnreadMessageCount();
            System.out.println(count + " total messages");
            ArrayList<EMail> result = new ArrayList<EMail>();

            for (int i = 0; i < Math.min(count, MSG_TO_READ_CNT); i++) {
                // Get  a message by its sequence number
                Message m = fldr.getMessage(count - i);

                // Get some headers
                Date date = m.getSentDate();
                Address[] from = m.getFrom();
                String subj = m.getSubject();
                result.add(new EMail(from[0].toString(), subj));
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<EMail>();
        }
    }
}
