package samples.recognition;

import samples.*;

import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;

import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

/**
 * Tests activating/deactivating rules and grammars in response to spoken commands - one grammar
 * contains the words "alpha","bravo","charlie","delta", but only one rule is active at a time
 * and the other grammar contains "one","two","three","four" - again, only one rule
 * active at a time: also, only one of the grammars is active at a time - switch between
 * grammars with the command "switch" and activate the next rule in each grammar
 * with the command "next".
 */
public class GramCommitTest {
    static Recognizer rec = null;
    static Synthesizer synth = null;
    static RuleGrammar gram2, gram1, gram0;
    static String[] rules1 = {"alpha","bravo","charlie","delta"}, rules2 = {"one","two","three","four"};
    static int gram1rule = 0 , gram2rule = 0;
    static boolean gram1enabled = true;
    
    public static void listCommands(Recognizer rec) {
        try {
            RuleGrammar[] grams = rec.listRuleGrammars();
            for(int i=0;i<grams.length; i++) {
                if(grams[i].isEnabled()) {
                    System.out.println("Grammar "+grams[i].getName()+" is enabled");
                    String[] rules = grams[i].listRuleNames();
                    for(int j=0;j<rules.length; j++) {
                        if(grams[i].isEnabled(rules[j])) {
                            Rule r = grams[i].getRule(rules[j]);
                            System.out.println("  Rule: "+r+", is enabled");
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            System.out.println(" * Tests activating rules and grammars in response to spoken commands. One grammar");
            System.out.println(" * contains the words \"alpha\",\"bravo\",\"charlie\",\"delta\", but only one rule is active at a time");
            System.out.println(" * and the other grammar contains \"one\",\"two\",\"three\",\"four\" - again, only one rule");
            System.out.println(" * active at a time. Also, only one of the grammars is active at a time. Switch between");
            System.out.println(" * grammars with the command \"switch\" and activate the next rule in each grammar");
            System.out.println(" * with the command \"next\".");
            SpeechEngineChooser chooser = SpeechEngineChooser.getRecognizerDialog();
            chooser.show();
            RecognizerModeDesc desc = chooser.getRecognizerModeDesc();
            rec = Central.createRecognizer(desc);
            
            rec.addResultListener(new ResultAdapter() {
                public void resultAccepted(ResultEvent e) {
                    try {
                        FinalRuleResult r = (FinalRuleResult)(e.getSource());
                        System.out.println("GOT RESULT "+r.getRuleName(0));
                        String tags[] = r.getTags();
                        if(tags != null && tags.length == 1) {
                            if(tags[0].equals("NEXT1")) {
                                gram1.setEnabled(rules1[gram1rule],false);
                                gram1rule++;
                                if(gram1rule == rules1.length) gram1rule = 0;
                                gram1.setEnabled(rules1[gram1rule],true);
                            } else if(tags[0].equals("NEXT2")) {
                                gram2.setEnabled(rules2[gram2rule],false);
                                gram2rule++;
                                if(gram2rule == rules2.length) gram2rule = 0;
                                gram2.setEnabled(rules2[gram2rule],true);
                            } else if(tags[0].equals("SWITCH")) {
                                gram1enabled = !gram1enabled;
                                if(gram1enabled) {
                                    gram2.setEnabled(false);
                                    gram1.setEnabled(rules1[gram1rule],true);
                                    gram1.setEnabled("SWITCH",true);
                                    gram1.setEnabled("NEXT1",true);
                                } else {
                                    gram1.setEnabled(false);
                                    gram2.setEnabled(rules2[gram2rule],true);
                                    gram2.setEnabled("SWITCH",true);
                                    gram2.setEnabled("NEXT2",true);
                                }
                            } else if(tags[0].equals("QUIT")) {
				System.out.println("Shutting down...");
                                rec.deallocate();
                                return;
                            } else return;
                            rec.commitChanges();
                            listCommands(rec);
                            rec.requestFocus();
                            rec.resume();
                        }
                    } catch(Exception e1) {
                        e1.printStackTrace(System.out);
                    }
                }
            }
            );
            
            RecognizerAudioAdapter raud = new TestAudioListener(false);
            rec.getAudioManager().addAudioListener(raud);
            
            rec.allocate();
            rec.waitEngineState(Recognizer.ALLOCATED);
            
            System.out.println("Using engine "+rec.getEngineModeDesc());
            SpeakerManager speakerManager = rec.getSpeakerManager();
            SpeakerProfile prof = chooser.getSpeakerProfile();
            speakerManager.setCurrentSpeaker(prof);
            System.out.println("Changed Current Profile to "+speakerManager.getCurrentSpeaker());
            
            gram0 = rec.newRuleGrammar("QUIT");
            gram0.setRule("QUIT",new RuleTag(new RuleToken("quit"),"QUIT"),true);
            gram0.setEnabled(true);
            
            gram1 = rec.newRuleGrammar("letters");
            gram1.setEnabled(false);
            gram1.setRule("NEXT1",new RuleTag(new RuleToken("next"),"NEXT1"),true);
            gram1.setRule("SWITCH",new RuleTag(new RuleToken("switch"),"SWITCH"),true);
            for(int i=0;i<rules1.length;i++) {
                gram1.setRule(rules1[i],new RuleToken(rules1[i]),true);
            }
            if(gram1enabled) {
                for(int i=0;i<rules1.length;i++) {
                    gram1.setEnabled(rules1[i], i==0 );
                }
            }
            gram1.setEnabled("SWITCH",true);
            gram1.setEnabled("NEXT1",true);
            
            gram2 = rec.newRuleGrammar("numbers");
            gram2.setEnabled(false);
            gram2.setRule("NEXT2",new RuleTag(new RuleToken("next"),"NEXT2"),true);
            gram2.setRule("SWITCH",new RuleTag(new RuleToken("switch"),"SWITCH"),true);
            for(int i=0;i<rules2.length;i++) {
                gram2.setRule(rules2[i],new RuleToken(rules2[i]),true);
            }
            if(!gram1enabled) {
                for(int i=0;i<rules2.length;i++) {
                    gram2.setEnabled(rules2[i], i==0 );
                }
                gram2.setEnabled("SWITCH",true);
                gram2.setEnabled("NEXT2",true);
            }
            rec.commitChanges();
            listCommands(rec);
            rec.requestFocus();
            rec.resume();
            
            rec.waitEngineState(Recognizer.DEALLOCATED);
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        } finally {
            try {
                rec.deallocate();
                rec.waitEngineState(Synthesizer.DEALLOCATED);
            } catch(Exception e2) {
                e2.printStackTrace(System.out);
            }
            System.exit(0);
        }
    }
}
