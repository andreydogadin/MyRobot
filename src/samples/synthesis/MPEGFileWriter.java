package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.io.*;

import com.cloudgarden.speech.*;

import javax.media.protocol.*;
import javax.media.*;
import javax.media.datasink.*;

/**
 * Utility class for saving synthesized speech to an MP3 file. Displays a window
 * for feedback on the progress of writing to the file.
 *
 * Used by the VoicePad application.
 *
 *@see samples.userinterface.VoicePad
 */
public class MPEGFileWriter implements ControllerListener, DataSinkListener {
    
    Object waitFileSync = new Object();
    boolean fileDone = false, fileSuccess = true;
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    boolean useWin = true;
    
    public void useNotificationWindow(boolean useWin) {
	this.useWin = useWin;
    }
    
    public void speakToMedia(Synthesizer synth, String text, MediaLocator outputMedia)
    throws EngineException, IOException {
	ProgressWindow nw = null;
	if(useWin) nw = new ProgressWindow();
	try {
	    if(useWin) {
		nw.setMessage("Initializing MP3 Writer");
		nw.show();
	    }
	    CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	    DataSource ds = audioMan.getDataSource();
	    
	    FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.MPEG_AUDIO);
	    ProcessorModel pm = new ProcessorModel(ds, null, cd);
	    Processor p = Manager.createRealizedProcessor(pm);
	    p.addControllerListener(this);
	    
	    DataSink dsink = Manager.createDataSink(p.getDataOutput(), outputMedia);
	    dsink.open();
	    
	    dsink.addDataSinkListener(this);
	    
	    p.start();
	    dsink.start();
	    if(useWin) {
		nw.setMessage("Initialized MP3 Writer");
		//The progress bar gives only a very vague indication of progress
		nw.setProgress(5);
	    }
	    synth.speakPlainText(text,null);
	    if(useWin) {
		nw.setMessage("Generating speech ...");
		//The progress bar gives only a very vague indication of progress
		nw.setProgress(10);
	    }
	    int n=1;
	    while(!synth.testEngineState(synth.QUEUE_EMPTY)) {
		//The progress bar gives only a very vague indication of progress
		if(useWin) {
		    nw.setProgress(10+n);
		    Thread.currentThread().sleep(500);
		    n++;
		    if(n == 70) n=5;
		}
	    }
	    if(useWin) {
		nw.setMessage("Generated speech");
		nw.setProgress(80);
	    }
	    n=0;
	    audioMan.closeOutput();
	    synchronized(waitFileSync) {
		while(!fileDone) {
		    waitFileSync.wait(500);
		    if(useWin) {
			nw.setMessage("Saving to file ...");
			//The progress bar gives only a very vague indication of progress
			nw.setProgress(80+n*1);
			n++;
			if(n==20) n=5;
		    } else System.out.println("...processing...");
		}
	    }
	    dsink.stop();
	    dsink.close();
	    if(!fileSuccess) throw new IOException("Failed to save to "+outputMedia);
	    audioMan.setDefaultOutput();
	} catch(Exception e) {
	    throw new IOException("Failed to save to "+outputMedia+" "+e);
	}
	if(useWin) nw.dispose();
    }
    
    public void dataSinkUpdate(DataSinkEvent evt) {
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
	if (evt instanceof EndOfMediaEvent) {
	    evt.getSourceController().close();
	}
    }
    
}

