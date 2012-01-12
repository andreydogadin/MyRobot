package robot.media.network.skype;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.SkypeException;
import commands.datatypes.InputString;
import commands.targets.SkypeTarget;
import workflow.WorkflowManager;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 22.08.11
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class ChatMessageAdapterDisp extends ChatMessageAdapter {

    public void chatMessageReceived(ChatMessage received) throws SkypeException {
        if (received.getType().equals(ChatMessage.Type.SAID)) {
            InputString inputString = new InputString(received.getContent(), SkypeTarget.getInstance());
            WorkflowManager.process(inputString);
        }
    }
}
