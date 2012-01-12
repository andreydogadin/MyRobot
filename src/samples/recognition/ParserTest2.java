package samples.recognition;

import com.cloudgarden.speech.CGRuleGrammar;

import javax.speech.recognition.*;

/**
 * Demonstrates use of the RuleGrammar.parse method
 */
class ParserTest2 {
    
    /** Creates a new instance of ParserTest2 */
    public ParserTest2() {
    }
    
    public static void main(String[] args) {
        try {
            CGRuleGrammar gram = new CGRuleGrammar("test", 0, null);
            gram.setRule("test", gram.ruleForJSGF("one two three"),true);
            gram.setRule("test2",gram.ruleForJSGF("one two (four | forty) five"),true);
            gram.setRule("test3",gram.ruleForJSGF("one two three four"),true);
            gram.setRule("test4",gram.ruleForJSGF("(one two three four | one two two four)"),true);
            gram.setEnabled(true);
            String[] rules = gram.listRuleNames();
            for(int i=0;i<rules.length; i++) {
                System.out.println("rule "+rules[i]+" = "+gram.getRule(rules[i]));
            }
            
            RuleParse rp;
	    System.out.println("accept unique guesses");
	    gram.setUniqueGuessesAccepted(true);
            rp = gram.parse(new String[] {"one two  three"},null);
            System.out.println("parsing 'one two  three' = "+rp);
            
            rp = gram.parse(new String[] {"one two  two"},null);
            System.out.println("parsing 'one two  two' = "+rp);
            
            rp = gram.parse(new String[] {"one two forty"},null);
            System.out.println("parsing 'one two forty' ="+rp);
            
	    gram.setUniqueGuessesAccepted(false);
	    System.out.println("DON'T accept unique guesses");
            rp = gram.parse(new String[] {"one two  three"},null);
            System.out.println("parsing 'one two  three' = "+rp);
            
            rp = gram.parse(new String[] {"one two forty"},null);
            System.out.println("parsing 'one two forty' ="+rp);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    
}
