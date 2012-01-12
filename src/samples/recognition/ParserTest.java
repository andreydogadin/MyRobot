package samples.recognition;
import javax.speech.*;
import javax.speech.recognition.*;

/**
 * Demonstrates use of the RuleGrammar.parse method
 */
public class ParserTest {
    
    /** Creates a new instance of ParserTest */
    public ParserTest() {
    }
    
    public static void main(String[] args) {
        try {
            Recognizer rec = Central.createRecognizer(null);
            rec.allocate();
            rec.waitEngineState(rec.ALLOCATED);
            
            RuleGrammar gram = rec.newRuleGrammar("test");
            gram.setRule("number", gram.ruleForJSGF("a {1} | one {1} | two {2} | three {3}"),true);
            gram.setRule("item",gram.ruleForJSGF("(snicker | snickers) {CANDY BAR} | (bananas | banana) {FRUIT} | toothbrush {DENTAL}"),true);
            gram.setRule("list_item",gram.ruleForJSGF("<number> <item> "),true);
            gram.setRule("list",gram.ruleForJSGF("<list_item> (and <list_item>)*"),true);
            gram.setEnabled(true);
            String[] rules = gram.listRuleNames();
            for(int i=0;i<rules.length; i++) {
                System.out.println("rule "+rules[i]+" = "+gram.getRule(rules[i]));
            }
            
            java.net.URL dir=new java.io.File("examples").toURL();
            RuleGrammar gram2 = rec.loadJSGF(dir,"grammars.helloWorld", true,true,null);
            
            rec.commitChanges();
            rec.waitEngineState(rec.LISTENING);
            RuleParse rp;
            rp = gram.parse(new String[] {"two snickers and one banana and a toothbrush"},null);
            System.out.println("parse="+rp);
            
            rp = gram2.parse(new String[] {"I'd like three carrots and four bananas"},null);
            System.out.println("parse="+rp);
            
            rp = gram2.parse(new String[] {"I'd like three carrots and four hundred and five bananas"},null);
            System.out.println("parse="+rp);
            rec.deallocate();
            rec.waitEngineState(rec.DEALLOCATED);
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    
}
