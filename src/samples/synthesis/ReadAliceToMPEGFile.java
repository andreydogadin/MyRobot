package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.io.*;

import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

import javax.media.protocol.*;
import javax.media.*;
import javax.media.datasink.*;

/**
 * Demonstrates the new methods in AudioManager to save speech to
 * an mpeg file.
 */
public class ReadAliceToMPEGFile implements ControllerListener, DataSinkListener {
    
    Object waitFileSync = new Object();
    boolean fileDone = false, fileSuccess = true;
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    
    public static void main(String args[]) {
        ReadAliceToMPEGFile mp3 = new ReadAliceToMPEGFile();
        mp3.speak();
    }
    
    public void speak() {
        Synthesizer synth = null;
        try {
            SpeechEngineChooser chooser = SpeechEngineChooser.getSynthesizerDialog();
            chooser.show();
            SynthesizerModeDesc desc = chooser.getSynthesizerModeDesc();
            synth = Central.createSynthesizer(desc);

            CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
            
            synth.addSpeakableListener(new SpeakableAdapter() {
                public void wordStarted(SpeakableEvent ev) {
                    System.out.println(ev.getText().substring(ev.getWordStart(),ev.getWordEnd()));
                }
            });
            
            synth.allocate();
            synth.waitEngineState(synth.ALLOCATED);
            
            synth.resume();
            
            SynthesizerProperties props = synth.getSynthesizerProperties();
            String name;
            Voice v1 = chooser.getVoice();
            props.setVoice(v1);
            
            synth.speakPlainText("Getting ready",null);
            //Wait till all done or you'll speak to the file...
            synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
            //synth.removeSpeakableListener(listener);
            
            javax.sound.sampled.AudioFormat fmt;
            if(audioMan.canSetAudioFormat()) {
                fmt = new javax.sound.sampled.AudioFormat(16000,16,1,true,false);
                audioMan.setAudioFormat(fmt);
            }
            fmt = audioMan.getAudioFormat();
            System.out.println("Synthesizer Format = "+fmt);
            
            DataSource ds = audioMan.getDataSource();
            
            FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.MPEG_AUDIO);
            System.out.println("Setting up MPEG3 Processor");
            ProcessorModel pm = new ProcessorModel(ds, null, cd);
            Processor p = Manager.createRealizedProcessor(pm);
            p.addControllerListener(this);
            MediaLocator outML =  new MediaLocator("file:alice_ch1.mp3");
            
            DataSink dsink = Manager.createDataSink(p.getDataOutput(), outML);
            dsink.open();
            
            dsink.addDataSinkListener(this);
            
            System.out.println("Ready to speak");
            
            Date start = new Date();
            
            try {
                p.start();
                dsink.start();
                
                synth.speak(new java.net.URL("file:resources\\alice_ch1.txt"),null);
                synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
                System.out.println("\nqueue empty");
                //need to close the output file otherwise the results are uncertain
                audioMan.closeOutput();
                
                synchronized(waitFileSync) {
                    while(!fileDone) {
                        System.out.println("...processing...");
                        waitFileSync.wait(500);
                    }
                }
                dsink.close();
                Date end = new Date();
                System.out.println("That took "+((end.getTime()-start.getTime())/1000)+" seconds");
                if(!fileSuccess) System.out.println("Failed to save file");
                
                System.out.println("\ndone");
            } catch(Exception e) {
                //If the file cannot be opened (eg if it is already opened by
                //another application) we will get an IOException
                e.printStackTrace();
            }
            
            //...and now speak to the default audio device
            //synth.addSpeakableListener(listener);
            try {
                audioMan.setDefaultOutput();
            } catch(IOException e) {
                e.printStackTrace();
            }
            synth.speakPlainText("All done",null);
            synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                synth.deallocate();
                //wait till we are deallocated...
                synth.waitEngineState(Engine.DEALLOCATED);
            } catch(Exception e2) {
                e2.printStackTrace();
            }
            System.out.println("All Done - now play the audio file");
            System.out.println("alice_ch1.mp3 to hear if it worked");
        }
        //...before closing up shop
        System.exit(0);
    }
    
    public void dataSinkUpdate(DataSinkEvent evt) {
        //System.out.println("dataSinkUpdate "+evt);
        if (evt instanceof EndOfStreamEvent) {
            synchronized (waitFileSync) {
                fileDone = true;
                waitFileSync.notifyAll();
            }
        } else if (evt instanceof DataSinkErrorEvent) {
            synchronized (waitFileSync) {
                fileDone = true;
                fileSuccess = false;
                waitFileSync.notifyAll();
            }
        }
    }
    
    public void controllerUpdate(ControllerEvent evt) {
        //System.out.println("controllerUpdate "+evt);
        if (evt instanceof EndOfMediaEvent) {
            evt.getSourceController().close();
        }
    }
    
}

