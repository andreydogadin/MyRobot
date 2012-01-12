/*
 */

package samples.applet;

import netscape.javascript.*;
import netscape.security.*;

import java.awt.*;
import java.net.*;
import java.util.*;

import javax.speech.*;
import javax.speech.synthesis.*;
import javax.speech.recognition.*;


/**
 * This applet is a demo of simple voice-controlled navigation.
 * It should be run by opening the "browser.html" file, which
 * loads the browser_applet.html file in the top frame of a two-frame
 * frameset. It uses Javascript in the applet frame to detect all the links
 * in the "MAIN" frame.
 *<P>
 * This applet has been tested with Sun's 1.4.1 plugin in IE6 and
 * Netscape 6, the Microsoft VM version 5.5 in IE6, and the Netscape 
 * VM in Netscape 4.79.
 *<P>
 * Read the docs/applets.html file for setup instructions
 *<P>
 * The applet lets you navigate among files from the same
 * source as the applet (eg, if the applet is loaded from a local
 * file system then you will be able to navigate local files only).
 * This is due to a Javascript security restriction which denies
 * Javascript in one frame from accessing the document of a
 * separate frame which comes from a different source (eg,
 * a different web server).
 *<P>
 * To navigate to a link, just say the link's text. Some basic
 * text-parsing is done (to remove any HTML tags in the text)
 * but multiple links with the same text are permitted - again,
 * this is just a demo applet and will need some work to handle
 * all real-life situations.
 *<P>
 * It will also perform simple windows functions (say "page up" or
 * "page down") and history commands ("go forward" and "go back").
 *
 */
public class BrowserApplet extends java.applet.Applet {
    
    private Synthesizer synth;
    private Recognizer rec;
    private LevelMeter levelMeter;
    
    private static JSObject win;
    
    private boolean inited = false;
    private boolean stopped;
    
    private RuleGrammar linkGram, winCmdGram;
    private Vector history = new Vector();
    private int historyIndex = 0;
    private int numLinks = 0;
    private long time;
    private ResultAdapter linkResultAdapter, winCmdResultAdapter;
    
    public static int initCount;
    
    public void init() {
	initCount++;
	//The Netscape JVM (version 4.79, at least) creates two applets in separate
	//environments. You can see this by looking at the Java Console output -
	// - the static variable initCount is 1 in both of them.
	System.out.println("init "+initCount+" @"+hashCode());
	initComponents();
	levelMeter = new LevelMeter(20);
	add(levelMeter, java.awt.BorderLayout.WEST);
	repaint();
	levelMeter.setSize(50,15);
	setBackground(Color.white);
	setStatusText("Starting engines");
	
    }
    
    /** Initializes the applet SpeechApplet */
    public void start() {
	System.out.println("start called @"+hashCode());
	stopped = false;
	
	time = System.currentTimeMillis();
	String appletContext = getAppletContext().getClass().getName();
	if (appletContext.startsWith("netscape.applet.")) {
	    new Thread() {
		public void run() {
		    try {
			Thread.currentThread().sleep(500);
			if(stopped) {
			    System.out.println("not starting engines");
			    return;
			}
			initialize();
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		}
	    }.start();
	} else {
	    initialize();
	}
    }
    
    public void stop() {
	System.out.println("stop called @"+hashCode());
	stopped = true;
	System.out.println("Seconds elapsed since start() = "+(System.currentTimeMillis() - time)/1000.0);
	if(inited) cleanup();
	//For Netscape JVM, the first Applet instance is stopped soon (< 0.5 secs) after it
	//is started, so if stop is called less than 0.5 secs after start, we don't initialize the
	//speech engines - see the start() method.
	inited = false;
    }
    
    public void initialize() {
	try {
	    win = JSObject.getWindow(this);
	    System.out.println("win="+win+" @"+hashCode());
	} catch(Throwable e) {
	    e.printStackTrace();
	}
	
	String vendor = System.getProperty("java.vendor").toLowerCase();
	if(vendor.indexOf("netscape") >= 0) {
	    netscapeCheck();
	}
	
	try {
	    if(!initSpeechComponents()) {
		System.out.println("initSpeechComponents failed, @"+hashCode());
		return;
	    }
	    inited = true;
	    setStatusText("Engines started");
	    callJSVoidFunction("setup");
	    speak("ready");
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void destroy() {
	System.out.println("destroy called @"+hashCode());
	super.destroy();
    }
    
    public void cleanup() {
	try {

	    if(synth != null) {
		//speak("bye");
		//synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
		System.out.println("synth deallocating");
		synth.deallocate();
	    }
	    
	    if(rec != null) {
		rec.getAudioManager().removeAudioListener(levelMeter);
		linkGram.removeResultListener(linkResultAdapter);
		winCmdGram.removeResultListener(winCmdResultAdapter);
		rec.pause();
		System.out.println("rec paused");
		rec.forceFinalize(true);
		System.out.println("rec finalized");
		rec.deallocate();
		System.out.println("rec deallocating");
	    }

	    if(synth != null) {
		synth.waitEngineState(Synthesizer.DEALLOCATED);
		System.out.println("synth deallocated");
	    }
	    
	    if(rec != null) {
		rec.waitEngineState(Recognizer.DEALLOCATED);
		System.out.println("rec deallocated");
	    }
	    
	} catch(Throwable e) {
	    e.printStackTrace();
	}
    }
    
    public void speak(String words) {
	if(synth == null) return;
	System.out.println("speaking \""+words+"\"");
	synth.cancelAll();
	synth.speakPlainText(words,null);
    }
    
    /**
     * Called from JavaScript before adding links from the MAIN frame.
     */
    public synchronized void startGrammars() {
	numLinks = 0;
	rec.pause();
	try {
	    setStatusText("loading page...");
	    String[] rules = linkGram.listRuleNames();
	    for(int i=0;i<rules.length;i++) linkGram.deleteRule(rules[i]);
	    linkGram.setEnabled(false);
	} catch(Throwable t) {
	    t.printStackTrace();
	}
    }
    
    /**
     * Called from JavaScript to add a link read from the MAIN frame
     */
    public synchronized void addLink(String href, String text) {
	try {
	    text = removeHTMLTags(text);
	    //System.out.println("got link href="+href+" text="+text);
	    Rule r = new RuleTag(new RuleToken(text), href);
	    linkGram.setRule("link"+numLinks, r ,true);
	    numLinks++;
	} catch(Throwable t) {
	    t.printStackTrace();
	}
    }
    
    /**
     * Called from JavaScript after all links have been added
     */
    public synchronized void commitGrammars() {
	try {
	    System.out.println("About to commit");
	    if(numLinks != 0) {
		linkGram.setEnabled(true);
	    }
	    rec.commitChanges();
	    rec.waitEngineState(Recognizer.LISTENING);
	    System.out.println("Committed grammars");
	    rec.resume();
	    rec.waitEngineState(Recognizer.LISTENING);
	    setStatusText("page loaded");
	} catch(Throwable t) {
	    t.printStackTrace();
	}
    }
    
    private String callJSStringFunction(String function) {
	String val = null;
	try {
	    val = (String)win.call(function,null);
	} catch(Exception e) {
	    System.out.println("Error calling "+function+" @"+hashCode());
	}
	return val;
    }
    
    private void callJSVoidFunction(String function) {
	try {
	    win.call(function,null);
	} catch(Exception e) {
	    System.out.println("Error calling "+function+" @"+hashCode());
	}
    }
    
    private void callJSVoidFunction(String function, String[] args) {
	try {
	    win.call(function, args);
	} catch(Exception e) {
	    System.out.println("Error calling "+function+" @"+hashCode());
	}
    }
    
    private void goToPage(String url) {
	try {
	    //callJSVoidFunction("goTo", new String[] {tags});
	    getAppletContext().showDocument(new URL(url),"MAIN");
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    private void goBack() {
	if(historyIndex == 0) return;
	historyIndex--;
	try {
	    String url = (String)history.elementAt(historyIndex);
	    System.out.println("go back to "+url);
	    setupNewPage(url);
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    private void goForward() {
	if(historyIndex == history.size()-1) return;
	historyIndex++;
	try {
	    setupNewPage((String)history.elementAt(historyIndex));
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    private void setStatusText(String status) {
	statusLabel.setText(status);
	repaint();
    }
    
    /**
     * Loads a new url into the MAIN frame, waits till it is loaded then
     * calls the Javascript "setup()" function in browser_applet.html
     * to add all links from the new url.
     */
    private void setupNewPage(String newUrl) {
	URL currURL = null, oldURL = null;
	try {
	    String url = callJSStringFunction("getMainURL");
	    try {
		oldURL = new URL(url);
		if(oldURL.equals(new URL(newUrl))) return;
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    goToPage(newUrl);
	    
	    //wait until the browser loads the new url
	    int count = 0;
	    do {
		try {
		    currURL = new URL(callJSStringFunction("getMainURL"));
		    System.out.println("currURL="+currURL+"\noldURL="+oldURL);
		    Thread.currentThread().sleep(200);
		} catch(Exception ex) {}
	    } while(count++ < 100 && currURL == null || currURL.equals(oldURL));
	    System.out.println("page changed");
	    
	    //get all the new links
	    win.call("setup", null);
	    System.out.println("setup finished");
	} catch(Exception e2) {
	    e2.printStackTrace();
	}
	
    }
    
    private boolean initSpeechComponents() {
	try {
	    //Turn off synchronization with the AWT EventQueue.
	    com.cloudgarden.speech.CGEngineCentral.setAWTSynchronization(false);
	    
	    synth = Central.createSynthesizer(new SynthesizerModeDesc(null,"SAPI4",Locale.ENGLISH,null,null));
	    if(synth == null) {
		synth = Central.createSynthesizer(new SynthesizerModeDesc(null,"SAPI5",Locale.ENGLISH,null,null));
	    }
	    System.out.println("got synthesizer "+synth.getEngineModeDesc());
	    synth.allocate();
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    synth.resume();
	    
	    String url = callJSStringFunction("getMainURL");
	    if(url != null) {
		//initialize history
		history.addElement(url);
		historyIndex = history.size()-1;
	    }
	    
	    linkResultAdapter = new ResultAdapter() {
		public void resultAccepted(ResultEvent e) {
		    try {
			System.out.println("accepted "+e);
			Result r = (Result)(e.getSource());
			ResultToken[] tokens = r.getBestTokens();
			String res = "";
			for(int i=0; i<tokens.length; i++) res += tokens[i].getWrittenText()+" ";
			resTextField.setText(res);
			
			String[] tags = ((FinalRuleResult)r).getTags();
			
			if(tags == null || tags.length != 1) {
			    System.out.println("*** Error? tags = "+tags);
			}
			
			setupNewPage(tags[0]);
			history.addElement(tags[0]);
			historyIndex = history.size()-1;
			
		    } catch(Throwable t) {
			t.printStackTrace();
		    }
		}
		public void resultUpdated(ResultEvent e) {
		    Result r = (Result)(e.getSource());
		    ResultToken[] tokens = r.getBestTokens();
		    String res = "";
		    for(int i=0; i<tokens.length; i++) res += tokens[i].getWrittenText()+" ";
		    resTextField.setText(res+" ...?");
		    System.out.println("updated "+e);
		}
	    };
	    
	    rec = Central.createRecognizer(new EngineModeDesc("SAPI5",null,Locale.ENGLISH,Boolean.FALSE));
	    if(rec == null) {
		rec = Central.createRecognizer(new EngineModeDesc("SAPI4",null,Locale.ENGLISH,Boolean.FALSE));
	    }
	    System.out.println("got recognizer "+rec.getEngineModeDesc());
	    rec.allocate();
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    rec.getAudioManager().addAudioListener(levelMeter);
	    
	    linkGram = rec.newRuleGrammar("links");
	    linkGram.addResultListener(linkResultAdapter);
	    
	    winCmdGram = rec.newRuleGrammar("winCmds");
	    winCmdGram.setRule("goBack", new RuleTag(new RuleToken("go back"), "goBack") ,true);
	    winCmdGram.setRule("goForward", new RuleTag(new RuleToken("go forward"), "goForward") ,true);
	    winCmdGram.setRule("pageDown", new RuleTag(new RuleToken("page down"), "pageDown") ,true);
	    winCmdGram.setRule("pageUp", new RuleTag(new RuleToken("page up"), "pageUp") ,true);
	    
	    winCmdResultAdapter = new ResultAdapter() {
		public void resultAccepted(ResultEvent e) {
		    try {
			System.out.println("winCmds accepted "+e);
			Result r = (Result)(e.getSource());
			ResultToken[] tokens = r.getBestTokens();
			String res = "";
			for(int i=0; i<tokens.length; i++) res += tokens[i].getWrittenText()+" ";
			resTextField.setText(res);
			
			String[] tags = ((FinalRuleResult)r).getTags();
			if(tags[0].equals("goBack")) {
			    goBack();
			} else if(tags[0].equals("goForward")) {
			    goForward();
			} else {
			    callJSVoidFunction(tags[0]);
			}
			
		    } catch(Throwable t) {
			t.printStackTrace();
		    }
		}
	    };
	    winCmdGram.addResultListener(winCmdResultAdapter);
	    
	    rec.requestFocus();
	    rec.resume();
	    rec.waitEngineState(rec.LISTENING);
	    
	    return true;
	    
	} catch(Throwable e) {
	    e.printStackTrace();
	    return false;
	}
    }
    
    private String removeHTMLTags(String text) {
	if(text == null) return null;
	int start, end;
	while((start = text.indexOf("<")) >= 0) {
	    end = text.indexOf(">",start);
	    text = text.substring(0,start)+text.substring(end+1);
	}
	return text;
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
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
	resTextField = new java.awt.TextField();
	statusLabel = new java.awt.Label();
	
	setLayout(new java.awt.BorderLayout(1, 1));
	
	resTextField.setColumns(30);
	resTextField.setEditable(false);
	resTextField.setEnabled(false);
	resTextField.setFont(new java.awt.Font("Verdana", 0, 11));
	add(resTextField, java.awt.BorderLayout.CENTER);
	
	statusLabel.setBackground(new java.awt.Color(0, 0, 255));
	statusLabel.setFont(new java.awt.Font("Verdana", 0, 11));
	statusLabel.setForeground(new java.awt.Color(255, 255, 255));
	statusLabel.setText("starting browser.......");
	add(statusLabel, java.awt.BorderLayout.EAST);
	
    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.TextField resTextField;
    private java.awt.Label statusLabel;
    // End of variables declaration//GEN-END:variables
    
}
