/*
 */

package samples.applet;

import netscape.javascript.*;
import netscape.security.*;

import java.awt.*;

import com.cloudgarden.speech.userinterface.Mouth;

import javax.speech.*;
import javax.speech.synthesis.*;


/**
 * This is an Applet that plays synthesized speech. It can be used
 * in Netscape 4.7, IE5.5 (with VM version 5) or with the Java Plugin.
 * It interacts with JavaScript by using the netscape.javascript package
 * to query a JS variable in the web page which holds the next words
 * to be spoken. 
 */
public class SpeechApplet extends java.applet.Applet {
    
    private Synthesizer synth;
    JSObject win;
    boolean running = true;
    static boolean inited = false;
    
    /** Initializes the applet SpeechApplet */
    public void init() {
	//if(inited) return;
	inited = true;
	System.out.println("init");
	String vendor = System.getProperty("java.vendor").toLowerCase();
	if(vendor.indexOf("netscape") >= 0) {
	    netscapeCheck();
	}
	initComponents();
	initSpeechComponents();
	try {
	    win = JSObject.getWindow(this);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	listen();
    }
    
    public static final void netscapeCheck() {
	try {
	    SecurityManager securityManager = System.getSecurityManager();
	    String JSAPI_DLL = com.cloudgarden.loader.Loader.JSAPI_DLL;
	    if(securityManager != null && securityManager.toString().indexOf("netscape") != -1) {
		try {
		    PrivilegeManager.enablePrivilege("UniversalLinkAccess");
		    System.loadLibrary(JSAPI_DLL);
		    com.cloudgarden.loader.Loader.jsapiLibraryLoaded();
		} catch(Exception e) {
		    System.out.println("Error trying to load native library");
		    e.printStackTrace();
		}
	    }
	} catch(Throwable t) {
	    t.printStackTrace();
	    System.out.println("error trying to get security manager");
	}
    }
    
    public void stop() {
	running = false;
	try {
	    System.out.println("deallocating");
	    synth.deallocate();
	    synth.waitEngineState(Synthesizer.DEALLOCATED);
	    System.out.println("deallocated");
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    public void speak(String words) {
	System.out.println("speaking \""+words+"\"");
	synth.cancelAll();
	synth.speakPlainText(words,null);
    }
    
    private void listen() {
	System.out.println("listen, win="+win);
	if(win == null) return;
	
	Thread lt = new Thread() {
	    public void run() {
		while(running) {
		    String words = null;
		    try {
			//Get the words from the web page (call JS function)
			words = (String)win.call("getSpeakables", null);
		    } catch(Exception e) {
			System.out.println("Unable to call JavaScript function.\nDon't panic, it's probably just while page is initializing\n"+e);
		    }
		    if(words != null && !words.equals("")) {
			speak(words);
		    }
		    //wait a bit then look for words again.
		    try {
			Thread.currentThread().sleep(200);
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	};
	lt.start();
    }
    
    private void initSpeechComponents() {
	try {
	    //Turn off synchronization with the AWT EventQueue.
	    com.cloudgarden.speech.CGEngineCentral.setAWTSynchronization(false);
	    synth = Central.createSynthesizer(new EngineModeDesc(null,null,java.util.Locale.ENGLISH,null));
	    if(synth == null) {
		System.out.println("Synthesizer not created");
		return;
	    }
	    System.out.println("got engine "+synth.getEngineModeDesc());
	    synth.allocate();
	    System.out.println("waiting till allocated");
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    synth.resume();
	    synth.addSpeakableListener(new SpeakableAdapter() {
		public void wordStarted(SpeakableEvent e) {
		    wordLabel.setText(e.getText().substring(e.getWordStart(), e.getWordEnd()));
		}
	    });
	    Mouth mouth = new Mouth();
	    panel1.add(mouth, BorderLayout.CENTER);
	    mouth.setSize(new Dimension(200,60));
	    wordLabel.setSize(new Dimension(200,60));
	    panel1.setSize(new Dimension(400,100));
	    synth.addSpeakableListener(mouth);
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    public static void main(String[] args) {
	Frame frame = new Frame("test");
	SpeechApplet app = new SpeechApplet();
	frame.add(app);
	frame.pack();
	frame.show();
	app.init();
    }
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
	speakButton = new java.awt.Button();
	textArea1 = new java.awt.TextArea();
	panel1 = new java.awt.Panel();
	wordLabel = new java.awt.Label();
	
	setLayout(new java.awt.BorderLayout());
	
	speakButton.setLabel("Now Say it!");
	speakButton.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		speakButtonActionPerformed(evt);
	    }
	});
	
	add(speakButton, java.awt.BorderLayout.SOUTH);
	
	add(textArea1, java.awt.BorderLayout.CENTER);
	
	panel1.setLayout(new java.awt.BorderLayout());
	
	wordLabel.setText("                                                              ");
	panel1.add(wordLabel, java.awt.BorderLayout.EAST);
	
	add(panel1, java.awt.BorderLayout.NORTH);
	
    }//GEN-END:initComponents
    
    private void speakButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speakButtonActionPerformed
	String text = textArea1.getText();
	text.replace('\n','.');
	text.replace('\r','.');
	synth.cancelAll();
	synth.speakPlainText(text,null);
    }//GEN-LAST:event_speakButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.TextArea textArea1;
    private java.awt.Label wordLabel;
    private java.awt.Button speakButton;
    private java.awt.Panel panel1;
    // End of variables declaration//GEN-END:variables
    
}
