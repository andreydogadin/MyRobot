/*
 * LevelMeter.java
 *
 * Created on February 23, 2003, 8:50 AM
 */

package samples.applet;

import java.awt.*;
import javax.speech.recognition.*;

/**
 * A simple AWT component which can be attached as a RecognizerAudioListener to
 * a recognizer's AudioManager, and also displayed inside an Applet etc, to
 * provide a progressing display of audio level. See the BrowserApplet code
 * for a demonstration of it's use.
 */
public class LevelMeter extends Canvas implements RecognizerAudioListener {
    private float level;
    private Color color = Color.red;
    private Color bg = new Color(210,210,250);
    private int numSections = 10;
    private int currentSection = 0;
    private float[] levels;
    private boolean[] speaking;
    private boolean isSpeaking = false;
    
    /**
     * numSections determines the number of vertically-oriented level bars
     * displayed horizontally across the LevelMeter.
     */
    public LevelMeter(int numSections) {
	this.numSections = numSections;
	levels = new float[numSections];
	speaking = new boolean[numSections];
    }
    
    public void paint(Graphics g) {
	int w = getSize().width;
	int h = getSize().height;
	g.setColor(bg);
	g.fillRect(0, 0, w, h);
	int x = 0, x0;
	int sec = currentSection;
	for(int i=0;i<numSections; i++) {
	    if(speaking[sec]) g.setColor(Color.green.darker());
	    else g.setColor(Color.red);
	    x0 = x;
	    x = w*(i+1)/numSections;
	    g.fillRect(x0, h-(int)(h*levels[sec]), (x-x0), 2*(int)(h*levels[sec]));
	    sec++;
	    if(sec >= numSections) sec = 0;
	}
	g.setColor(Color.black);
	g.drawRect(0, 0, w-1, h-1);
    }
    
    public void update(Graphics g) {
	paint(g);
    }
    
    public void audioLevel(final RecognizerAudioEvent e) {
	level = e.getAudioLevel();
	levels[currentSection] = level;
	speaking[currentSection] = isSpeaking;
	currentSection++;
	if(currentSection >= numSections) currentSection = 0;
	//System.out.println("level = "+e.getAudioLevel());
	repaint();
	getParent().repaint();
    }
    
    public void speechStarted(RecognizerAudioEvent e) {
	isSpeaking = true;
	//color = Color.green;
	//repaint();
	//getParent().repaint();
    }
    
    public void speechStopped(RecognizerAudioEvent e) {
	isSpeaking = false;
	//color = Color.red;
	//repaint();
	//getParent().repaint();
    }
    
}
