package samples.synthesis;

import java.awt.*;
import java.awt.event.*;
import javax.speech.synthesis.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.*;

/**
 * This class extends the Mouth class, and displays itself in a new Frame,
 * so it displays lip-sync events as well as printing out word and marker events.
 */
public class TestSpeakableListener extends Mouth {
    
    private static int numTests = 0;
    
    public TestSpeakableListener() {
        Frame frame = new Frame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0); //not a good way to close up - should deallocate Synthesizer in practice
            }
        });
        Panel panel = new Panel(new BorderLayout());
        panel.add(this);
        frame.add(panel);
        frame.setSize(new Dimension(200,120));
        frame.move(numTests*100,numTests*60);
        if(numTests< 10) numTests++;
        frame.show();
    }
    
    public void speakableStarted(SpeakableEvent e) {
        System.out.println("speakableStarted "+e.getSource());
    }
    
    public void speakableEnded(SpeakableEvent e) {
        System.out.println("speakableEnded "+e.getSource());
    }
    
    public void wordStarted(SpeakableEvent e) {
        int start = e.getWordStart();
        int end = e.getWordEnd();
        System.out.println(e.getText().substring(start, end));
    }
    public void markerReached(SpeakableEvent e) {
        System.out.println("Mark = "+e.getText());
    }
    public void phoneme(CGSpeakableEvent ev) {
        if(ev.isMSPhoneID()) {
            System.out.println("MS PhoneID = "+ev.getPhoneme());
        } else {
            String str = "\\u"+Integer.toHexString(ev.getPhoneme());
            System.out.println("IPA phoneme code = "+str);
        }
    }
}
