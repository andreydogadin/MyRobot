package samples.recognition;

import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;
import java.net.*;

/**
 * Just loads in a JSGF file which contains several uses of the <NULL> and <VOID> rules,
 * then quits after listing out the rules.
 */
public class NullAndVoidTest {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
        try {
            
            RecognizerModeDesc desc = null;
            //use one of the below if you want to load specifically a SAPI4 or SAPI5 engine...
            //desc = new RecognizerModeDesc("SAPI4",null,null,null,null,null);
            //desc = new RecognizerModeDesc("SAPI5",null,null,null,null,null);
            rec = Central.createRecognizer(desc);
            
            rec.allocate();
            rec.waitEngineState(Recognizer.ALLOCATED);
            
            System.out.println("Using engine "+rec.getEngineModeDesc().getEngineName());
            URL dir=new File("examples").toURL();
            
            RuleGrammar gram;
            String[] names;
            
            RuleGrammar gram2 = rec.loadJSGF(dir,"grammars.null_void_test", true,true,null);
            names = gram2.listRuleNames();
            for(int i=0;i<names.length; i++) {
                Rule rule = gram2.getRule(names[i]);
                System.out.println("<"+names[i]+"> = "+rule);
            }
            gram2.setEnabled(true);
            rec.commitChanges();
            rec.waitEngineState(rec.LISTENING);
            System.out.println("\nCommitted "+gram2.getName());
            
            rec.deallocate();
            rec.waitEngineState(Recognizer.DEALLOCATED);
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        }
        System.exit(0);
    }
}
