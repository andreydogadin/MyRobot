package samples.recognition;

import samples.*;
import com.cloudgarden.speech.userinterface.*;
import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;
import java.net.*;

/**
 * Demonstrates loading a grammar file, "grammars.helloWorld", which
 * imports another grammar, "grammars.numbers", and recognition of commands 
 * from the helloWorld grammar.
 */
public class LoadJSGFFromURL {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
        try {
            
            SpeechEngineChooser chooser = SpeechEngineChooser.getRecognizerDialog();
            chooser.show();
            RecognizerModeDesc desc = chooser.getRecognizerModeDesc();
            
            rec = Central.createRecognizer(desc);
            
            rec.addEngineListener(new TestEngineListener());
            rec.addResultListener(new TestResultListener(rec,3,true));
            
            RecognizerAudioAdapter raud = new TestAudioListener();
            rec.getAudioManager().addAudioListener(raud);
            
            rec.allocate();
            
            rec.waitEngineState(Recognizer.ALLOCATED);
            RecognizerProperties props = rec.getRecognizerProperties();
            props.setNumResultAlternatives(5);
            props.setResultAudioProvided(true);
            
            URL dir=new File("examples").toURL(); //The recognizer will look
            //for the grammar files starting from the samples directory
            
            RuleGrammar gram = rec.loadJSGF(dir,"grammars.helloWorld", true,true,null);
            String[] names = gram.listRuleNames();
            for(int i=0;i<names.length; i++) {
                Rule rule = gram.getRule(names[i]);
                System.out.println("<"+names[i]+">="+rule);
            }
            
            //disable numbers grammar - "helloWorld" grammar imports "numbers" grammar,
            //but we only want to accept rules of "helloWorld" grammar
            RuleGrammar numbers = rec.loadJSGF(dir,"grammars.numbers", true,true,null);
            numbers.setEnabled(false);
            
            gram.setEnabled(true);
            
            rec.commitChanges();
            rec.waitEngineState(rec.LISTENING);
            System.out.println("\nUsing engine "+rec.getEngineModeDesc());
            
            rec.requestFocus();
            rec.resume();
            
            rec.waitEngineState(Recognizer.DEALLOCATED);
            //ResultListener deallocates after three recognitions
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        } finally {
            try {
                rec.deallocate();
            } catch(Exception e2) {
                e2.printStackTrace(System.out);
            }
            System.exit(0);
        }
    }
}
