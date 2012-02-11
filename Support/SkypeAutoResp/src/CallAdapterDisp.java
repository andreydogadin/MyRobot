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
        sleep(SKYPE_DELAY);
        if (receivedCall.isSendVideoEnabled()) {
            Skype.chat(receivedCall.getPartnerId()).send("Starting Video...");
            sleep(SKYPE_DELAY);
            receivedCall.setReceiveVideoEnabled(true);
            SkypeClient.showSkypeWindow();
        }

    }

    private static Integer SKYPE_DELAY = 10 * 1000;

    private void sleep(Integer milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
};