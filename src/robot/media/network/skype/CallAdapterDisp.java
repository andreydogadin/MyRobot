package robot.media.network.skype;

import com.skype.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 22.08.11
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class CallAdapterDisp extends CallAdapter {
    @Override
    public void callReceived(Call receivedCall) throws SkypeException {
        receivedCall.answer();
        try {
            Skype.chat(receivedCall.getPartnerId()).send("Starting Video...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Skype.chat(receivedCall.getPartnerId()).send(e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        receivedCall.setReceiveVideoEnabled(true);
    }
};