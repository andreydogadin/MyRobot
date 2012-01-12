package samples.recognition;

import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;
import java.net.*;

/**
 * Loads in a grammar file (error_test.gram) which contains several JSGF errors, to demonstrate reporting of errors.
 */
public class GrammarErrorTest {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
        try {
            rec = Central.createRecognizer(null);
            rec.allocate();
            rec.waitEngineState(Recognizer.ALLOCATED);
            
            System.out.println("Using engine "+rec.getEngineModeDesc().getEngineName());
            
            RuleGrammar gram = rec.newRuleGrammar("test1");
            try {
                Rule rule = gram.ruleForJSGF("<this is an error");
                System.out.println("rule = "+rule);
            } catch(GrammarException e) {
                e.printStackTrace();
            }
            RuleGrammar gram2 = null;
            try {
                URL dir=new File("examples").toURL();
                gram2 = rec.loadJSGF(dir,"grammars.error_test", true,true,null);
                String[] names = gram2.listRuleNames();
                System.out.println(gram2.getName()+" has "+names.length+" public rules");
                for(int i=0;i<names.length; i++) {
                    Rule rule = gram2.getRule(names[i]);
                    System.out.println("<"+names[i]+"> = "+rule);
                }
                gram2.setEnabled(true);
                rec.commitChanges();
                rec.waitEngineState(rec.LISTENING);
                System.out.println("\nCommitted "+gram2.getName());
            } catch(GrammarException e) {
                e.printStackTrace();
            }
            
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        }
        /*
        try {
            rec.deallocate();
            rec.waitEngineState(Recognizer.DEALLOCATED);
        } catch(Throwable e) {
            e.printStackTrace();
        }
         */
        System.exit(0);
    }
}
