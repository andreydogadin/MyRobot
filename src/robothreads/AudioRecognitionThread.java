package robothreads;

import com.cloudgarden.audio.AudioFormatConverter;
import com.cloudgarden.audio.AudioLineSource;
import com.cloudgarden.speech.CGAudioManager;
import robot.MyRobot;
import robot.media.audio.recognition.AudioListener;
import robot.media.audio.recognition.EngineListener;
import robot.media.audio.recognition.ResultListener;
import robothreads.abstracts.RobotThread;
import utils.RobotConsts;

import javax.sound.sampled.AudioFormat;
import javax.speech.Central;
import javax.speech.recognition.*;
import java.io.FileReader;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/16/11
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AudioRecognitionThread extends RobotThread {

    static RuleGrammar ruleGrammar;
    DictationGrammar dictationGrammar;

    public void run() {
        try {

            RecognizerModeDesc desc = new RecognizerModeDesc(null, Boolean.TRUE);
            MyRobot.getInstance().setRecognizer(Central.createRecognizer(desc));
            MyRobot.getInstance().getRecognizer().addEngineListener(new EngineListener());
            CGAudioManager audioMan = (CGAudioManager) MyRobot.getInstance().getRecognizer().getAudioManager();
            audioMan.addAudioListener(new AudioListener());


            MyRobot.getInstance().getRecognizer().allocate();

            MyRobot.getInstance().getRecognizer().waitEngineState(Recognizer.ALLOCATED);
            RecognizerProperties props = MyRobot.getInstance().getRecognizer().getRecognizerProperties();

            log.info("Using engine " + MyRobot.getInstance().getRecognizer().getEngineModeDesc());
            SpeakerManager speakerManager = MyRobot.getInstance().getRecognizer().getSpeakerManager();

            SpeakerProfile[] profs = speakerManager.listKnownSpeakers();
            for(int i=0;i<profs.length; i++) {
                log.info("Found Profile "+i+" = "+profs[i].getName());
            }
            SpeakerProfile prof = profs[0];
            speakerManager.setCurrentSpeaker(prof);
            log.info("Changed Current Profile to " + speakerManager.getCurrentSpeaker());

            dictationGrammar = MyRobot.getInstance().getRecognizer().getDictationGrammar(null);

            Reader reader = new FileReader(RobotConsts.RECOGNITION_GRAMMAR);
            ruleGrammar = MyRobot.getInstance().getRecognizer().loadJSGF(reader);

            ruleGrammar.addResultListener(new ResultListener(MyRobot.getInstance().getRecognizer()));
            ruleGrammar.setEnabled(true);

            MyRobot.getInstance().getRecognizer().suspend();
            MyRobot.getInstance().getRecognizer().commitChanges();
            MyRobot.getInstance().getRecognizer().waitEngineState(Recognizer.LISTENING);

            AudioFormat fmt = audioMan.getAudioFormat();
            AudioLineSource source = null;
            try {
                source = new AudioLineSource(fmt);
                audioMan.setSource(source);
            } catch (Exception e) {
                log.error("unable to create DataLine with format " + fmt);
                fmt = new AudioFormat(16000, 16, 1, true, false);
                log.error("trying to create DataLine with format " + fmt);
                source = new AudioLineSource(fmt);
                new AudioFormatConverter(source, audioMan);
            }

            MyRobot.getInstance().getRecognizer().requestFocus();
            log.info("Start sending data from Mic");
            source.startSending();
            MyRobot.getInstance().getRecognizer().waitEngineState(Recognizer.DEALLOCATED);
            System.out.println("All done");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                MyRobot.getInstance().getRecognizer().deallocate();
            } catch (Exception e2) {
                e2.printStackTrace(System.out);
            }
        }

    }
}
