package samples.remote;

import samples.*;

import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.sound.sampled.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

/**
 * Used in conjunction with the WhatTimeIsItClient to tell the
 * time/date to a remote client. Start up this class first, then
 * the WhatTimeIsItClient on the remote machine. Read the
 * comments in the WhatTimeIsItClient code.
 */
public class WhatTimeIsItServer implements ClientListener {
    static Recognizer rec = null;
    static Synthesizer synth = null;
    static boolean done = false;
    static int numClients = 0;
    AudioServerSource recServer;

    public static void main(String[] args) {
	new WhatTimeIsItServer().start();
    }
    

    public void bytesTransferred(ClientEvent evt) {
    }
    
    public void clientRemoved(ClientEvent evt) {
	System.out.println("\nclient removed "+evt.getClientName()+" on thread "+evt.getThread()+"\n");
    }
    
    public void clientAdded(ClientEvent evt) {
	System.out.println("\nclient added "+evt.getClientName()+" on thread "+evt.getThread()+"\n");
	numClients++;
	synth.speakPlainText("hello client "+numClients,null);
	try {
	    recServer.startSending();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void start() {
	try {
	    
	    synth = Central.createSynthesizer(new EngineModeDesc(null,null,Locale.ENGLISH,null));
	    synth.addEngineListener(new TestEngineListener());
	    
	    synth.allocate();
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    synth.resume();
	    
	    CGAudioManager audioManSyn = (CGAudioManager)synth.getAudioManager();
	    AudioFormat fmt = new AudioFormat(16000,16,1,true,false);
	    System.out.println("Set synth server format to "+fmt);
	    AudioServerSink sink = new AudioServerSink("TimeServerSyn", null,1099);
	    sink.setAudioFormat(fmt);
	    new AudioFormatConverter(audioManSyn, sink);
	    sink.addClientListener(this);
	    
	    rec = Central.createRecognizer(new EngineModeDesc(null,null,Locale.ENGLISH,null));
	    System.out.println("Using "+rec.getEngineModeDesc());
	    rec.addEngineListener(new TestEngineListener());
	    
	    rec.addResultListener(new ResultAdapter() {
		public void resultUpdated(ResultEvent e) {
		    System.out.println("updated "+e);
		}
		public void resultAccepted(ResultEvent e) {
		    System.out.println("accepted "+e);
		    try {
			FinalRuleResult r = (FinalRuleResult)(e.getSource());
			String tags[] = r.getTags();
			if(tags == null || tags.length == 0) return;
			SimpleDateFormat df;
			Date d = new Date();
			if(tags[0].equals("TIME")) {
			    df = new SimpleDateFormat("h:mm a, zzzz");
			    String msg = "The time is "+df.format(d);
			    System.out.println("Speaking : "+msg);
			    synth.speak(msg,null);
			} else if(tags[0].equals("DATE")) {
			    df = new SimpleDateFormat("EEE, MMM d, yyyy");
			    String msg = "The date is "+df.format(d);
			    System.out.println("Speaking : "+msg);
			    synth.speak(msg,null);
			} else return;
			System.out.println("speaking");
			synth.waitEngineState(synth.QUEUE_EMPTY);
			
			//could test for some command, like "exit", and deallocate here
			//rec.deallocate();
		    } catch(Exception e1) {
			e1.printStackTrace(System.out);
		    }
		}
	    }
	    );
	    
	    RecognizerAudioAdapter raud = new TestAudioListener();
	    rec.getAudioManager().addAudioListener(raud);
	    
	    rec.allocate();
	    
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    RuleSequence rs1 = new RuleSequence();
	    RuleToken rt1 = new RuleToken("what");
	    rs1.append(rt1);
	    RuleTag rt2 = new RuleTag(new RuleToken("time"),"TIME");
	    RuleTag rt3 = new RuleTag(new RuleToken("date"),"DATE");
	    RuleAlternatives ra1 = new RuleAlternatives();
	    ra1.append(rt2);
	    ra1.append(rt3);
	    rs1.append(ra1);
	    RuleToken rt4 = new RuleToken("is it");
	    rs1.append(rt4);
	    RuleGrammar gram = rec.newRuleGrammar("time");
	    gram.setRule("testRule",rs1,true);
	    
	    gram.setEnabled(true);
	    
	    rec.commitChanges();
	    
	    InetAddress client = null;
	    //Include this line to restrict access to machine "sunflower"...
	    //client = InetAddress.getByName("sunflower");
	    CGAudioManager audioManRec = (CGAudioManager)rec.getAudioManager();
	    recServer = new AudioServerSource("TimeServerRec", client,1099);
	    recServer.addClientListener(this);
	    recServer.setAudioFormat(fmt);
	    
	    new AudioFormatConverter(recServer, audioManRec);
	    
	    System.out.println("converting from server fmt "+recServer.getAudioFormat());
	    System.out.println("to rec fmt "+audioManRec.getAudioFormat());
	    recServer.startSending();
	    
	    rec.requestFocus();
	    rec.waitEngineState(Recognizer.LISTENING);
	    
	    System.out.println("Start up the WhatTimeIsItClient now!");
	    
	    //Need to call closeOutput otherwise sink.drain() never finishes since
	    //Synthesizer output stream is never closed!
	    System.out.println("waiting for DEALLOCATED");
	    rec.waitEngineState(rec.DEALLOCATED);
	    System.out.println("rec deallocated");
	    audioManSyn.closeOutput();
	    System.out.println("closing output");
	    sink.drain();
	    System.out.println("drained");
	    synth.deallocate();
	    System.out.println("synth deallocating");
	    synth.waitEngineState(synth.DEALLOCATED);
	    System.out.println("synth deallocated");
	} catch(Throwable e) {
	    e.printStackTrace(System.out);
	} finally {
	    System.exit(0);
	}
    }
    
}
