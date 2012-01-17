package robot;

import com.skype.Skype;
import com.skype.SkypeException;
import robot.controller.RobotFaceController;
import robot.createsdk.RobotPlatform;
import robot.media.Memory;
import robot.media.audio.synth.SpeechSynth;
import robot.media.laptop.Kernel32;
import robot.media.network.skype.ChatMessageAdapterDisp;
import robot.media.video.Camera;
import robot.media.video.VideoRecognizer;
import robot.view.face.RobotFace;

import javax.speech.recognition.Recognizer;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/10/11
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyRobot extends RobotPlatform {

    private SpeechSynth speechSynth;
    private VideoRecognizer videoRecognizer;

    private Recognizer rec = null;
    private String recognitionState;
    private Memory memory;
    int laptopAkku;

    private RobotFace face;
    private RobotFaceController faceController;

    private static MyRobot _instance = null;

    public static synchronized MyRobot getInstance() {
        if (_instance == null)
            _instance = new MyRobot();
        return _instance;
    }

    private MyRobot() {
        super();
        memory = new Memory();
        face = new RobotFace();
        faceController = new RobotFaceController();
        this.speechSynth = new SpeechSynth();
        try {
            Skype.addChatMessageListener(new ChatMessageAdapterDisp());
        } catch (SkypeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public Integer getLaptopAkkuState(){
        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        this.laptopAkku = batteryStatus.BatteryLifePercent;
        return this.laptopAkku;
    }

    public String getStatus()
    {
        String result;
        result = String.format("Platform battery is charged for %s percents.\n ", this.platformAkku);
        result += String.format("Laptop battery is charged for %s percents. \n", this.laptopAkku);
        result += String.format("Platform is ON: %s \n", this.isPlatformOn().toString());
        return result;
    }

    public void initVideo() {
        if (this.videoRecognizer == null)
            videoRecognizer = new VideoRecognizer();
    }

    public RobotFaceController getFaceController() {
        return faceController;
    }

    public String getRecognitionState() {
        return recognitionState;
    }

    public void setRecognitionState(String recognitionState) {
        this.recognitionState = recognitionState;
    }

    public void release() {
        super.release();
        if (this.getSpeechSynth() != null) this.getSpeechSynth().Release();
        if (this.getRecognizer() != null) this.getRecognizer().forceFinalize(true);
    }

    public SpeechSynth getSpeechSynth() {
        return speechSynth;
    }

    public void setSpeechSynth(SpeechSynth speechSynth) {
        this.speechSynth = speechSynth;
    }

    public VideoRecognizer getVideoRecognizer() {
        return videoRecognizer;
    }

    public void setVideoRecognizer(VideoRecognizer videoRecognizer) {
        this.videoRecognizer = videoRecognizer;
    }

    public Memory getMemory() {
        return memory;
    }

    public Recognizer getRecognizer() {
        return rec;
    }

    public RobotFace getFace() {
        return face;
    }

    public void setRecognizer(Recognizer recognizer) {
        rec = recognizer;
    }

}
