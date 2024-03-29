/*
 * HelloSapi4.java
 *
 * Created on April 12, 2002, 11:56 AM
 */

package samples.synthesis;


import javax.speech.*;
import javax.speech.synthesis.*;

/**
 * Speaks with a SAPI5 voice when the button is pushed
 */
public class HelloSapi5 extends javax.swing.JFrame {
    
    Synthesizer synth = null;
    Voice voice = null;
    
    /** Creates new form HelloSapi5 */
    public HelloSapi5() {
	initComponents();
	try {
	    
	    EngineList list = Central.availableSynthesizers(null);
	    SynthesizerModeDesc desc = null;
	    for(int i=0;i<list.size();i++) {
		desc = (SynthesizerModeDesc)list.elementAt(i);
		Voice[] voices = desc.getVoices();
		for(int j=0;j<voices.length;j++) {
		    if(voices[j].getName().indexOf("SAPI5") > 0) {
			voice = voices[j];
			break;
		    }
		}
	    }
	    
	    if(voice == null) {
		System.out.println("Unable to find a SAPI5 voice - quitting");
		System.exit(0);
	    }
	    
	    synth = Central.createSynthesizer(desc);
	    synth.allocate();
	    synth.resume();
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    System.out.println("Using voice "+voice);
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    props.setVoice(voice);
	    
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jButton1 = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jButton1.setText("Hello SAPI 5");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        getContentPane().add(jButton1, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
	synth.speakPlainText("This is voice "+voice.getName(),null);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
	try {
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	    synth.deallocate();
	    synth.waitEngineState(synth.DEALLOCATED);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	new HelloSapi5().show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
    
}
