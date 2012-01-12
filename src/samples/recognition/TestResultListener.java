package samples.recognition;

import java.util.Date;
import javax.speech.*;
import javax.speech.recognition.*;
import com.cloudgarden.speech.CGResult;

/**
 * Class to print out result events, and deallocate the recognizer after a
 * given number of accepted results. Also demonstrates playback
 * of recorded speech and use of tokenCorrection (except the
 * actual tokenCorrection line has been commented out).
 */
public class TestResultListener extends ResultAdapter {
    private int nRecs = 0;
    private Recognizer rec;
    private boolean playAudio;
    
    /**
     * Creates a ResultListener which will deallocate the given Recognizer
     * after "nRecs" accepted recognitions, and will re-play the recorded audio
     * if "playAudio" is true (and audio is being saved using
     * RecognizerProperties.setResultAudioProvided(true))
     */
    public TestResultListener(Recognizer rec, int nRecs, boolean playAudio) {
        this.rec = rec;
        this.nRecs = nRecs;
        this.playAudio = playAudio;
    }
    
    public void resultRejected(ResultEvent e) {
        Result r = (Result)(e.getSource());
        System.out.println("Result Rejected "+r);
    }
    public void resultCreated(ResultEvent e) {
        Result r = (Result)(e.getSource());
        System.out.println("Result Created ");
    }
    public void resultUpdated(ResultEvent e) {
        Result r = (Result)(e.getSource());
        System.out.println("Result Updated... "+r);
        ResultToken[] tokens = r.getBestTokens();
        if(tokens != null && tokens.length > 0) {
            displayTimes(tokens[0]);
        }
    }
    
    private void displayTimes(ResultToken token) {
        Date start = new Date(token.getStartTime());
        Date now = new Date(System.currentTimeMillis());
        System.out.println("Result start = "+start.getMinutes()+":"+start.getSeconds()+
        ", length = "+((token.getEndTime() - token.getStartTime())/1000.0)+
        ", now="+now.getMinutes()+":"+now.getSeconds());
    }
    
    public  void resultAccepted(ResultEvent e) {
        final FinalResult r = (FinalResult)(e.getSource());
        Runnable lt = new Runnable() {
            public void run() {
                try {
                    System.out.print("Result Accepted: "+r);
                    ResultToken tokens[] = null;
                    //only way to find out if it's a FinalRuleResult or a FinalDictationResult is to see whether
                    //it's grammar is a Rule or Dictation Grammar, since all Results obtained from a ResultEvent
                    //implement both FinalRuleResult and FinalDictationResult interfaces (see JSAPI documentation)
                    if(r.getGrammar() instanceof RuleGrammar) {
                        System.out.println("\nRuleGrammar name="+((FinalRuleResult)r).getRuleGrammar(0).getName());
                        System.out.println("Rule name="+((FinalRuleResult)r).getRuleName(0));
                        tokens = ((FinalRuleResult)r).getAlternativeTokens(0);
                    } else {
                        System.out.println("\nGrammar name="+r.getGrammar().getName());
                        tokens = r.getBestTokens();
                    }
                    
                    if(tokens != null && tokens.length > 0) {
                        displayTimes(tokens[0]);
                    }
                    
                    if(playAudio) {
                        try {
                            System.out.println("Speaking all tokens of recorded speech");
                            java.applet.AudioClip clip = null;
                            if(tokens != null && tokens.length > 0) clip = r.getAudio(tokens[0],tokens[tokens.length-1]);
                            if(clip != null) {
                                rec.pause();
                                clip.play();
                                rec.resume();
                            } else System.out.println("Audio clip is null - can't play");
                            //release it so it doesn't hang about wasting space
                            r.releaseAudio();
                        } catch(EngineStateError e) {
                            System.out.println("Non-fatal error: "+e);
                        }
                    }
                    
                    //Test out token correction - here we just get the "nAlt"th alternative
                    //and "correct" the result using it
                    //(except here we've commented out the tokenCorrection call
                    //...include that line if you actually want to update your profile randomly!
                    
                    try {
                        ResultToken[] toks = null;
                        int nAlt = 3;
                        if(r.getGrammar() instanceof DictationGrammar) {
                            
                            //Print out first three alternatives
                            String str = "";
                            FinalDictationResult fdr = (FinalDictationResult)r;
                            ResultToken start = fdr.getBestToken(0);
                            ResultToken end =  fdr.getBestToken(fdr.numTokens()-1);
                            ResultToken[][] tokenArray = fdr.getAlternativeTokens(start,end,3);
                            if(tokenArray != null) {
                                for(int i=0;i<tokenArray.length;i++) {
                                    str+="\nAlternative (engineConf="+((CGResult)fdr).getEngineConfidence(i)+") "+i+" =";
                                    for(int j=0;j<tokenArray[i].length;j++) {
                                        str+=" "+tokenArray[i][j].getSpokenText();
                                    }
                                }
                            }
                            System.out.println(str);
                            
                            tokenArray = fdr.getAlternativeTokens(start, end, 3);
                            if(tokenArray != null && tokenArray.length > nAlt) toks = tokenArray[nAlt];
                            
                        }  else {
                            //Print out first three alternatives
                            String str = "";
                            FinalRuleResult frr = (FinalRuleResult)r;
                            for(int i=0;i<3; i++) {
                                ResultToken[] tokenArray = frr.getAlternativeTokens(i);
                                if(tokenArray != null) {
                                    str+="\nAlternative (engineConf="+((CGResult)frr).getEngineConfidence(i)+") "+i+" =";
                                    for(int j=0;j<tokenArray.length;j++) {
                                        str+=" "+tokenArray[j].getSpokenText();
                                    }
                                }
                            }
                            System.out.println(str);
                            toks = ((FinalRuleResult)r).getAlternativeTokens(nAlt);
                        }
                        if(toks != null) {
                            String[] stoks = new String[toks.length];
                            for(int i=0;i<stoks.length;i++) stoks[i] = toks[i].getSpokenText();
                            System.out.print("Could correct ... '");
                            for(int i=0;i<tokens.length;i++) System.out.print(tokens[i].getSpokenText()+" ");
                            System.out.print("' ...with... '");
                            for(int i=0;i<stoks.length;i++)  System.out.print(stoks[i]+" ");
                            System.out.println("' ...(except the tokenCorrection call is commented out)");
                            //r.tokenCorrection(stoks,tokens[0],tokens[tokens.length-1], 0);
                            
                            // NOTE: for SAPI4 engines, after correction, the alternatives are deleted,
                            // but for SAPI5 engines the alternatives are rearranged to be consistent
                            // with the correction - you can observe this by looking at the output of the
                            // System.out.println("RESULT is "+r); line below
                            
                        }
                    } catch(ResultStateError er) {
                        er.printStackTrace(System.out);
                        //Result is not a DictationResult
                    }
                } catch(Exception e1) {
                    e1.printStackTrace(System.out);
                } catch(ResultStateError er) {
                    er.printStackTrace(System.out);
                }
                nRecs--;
                if(nRecs == 0 && rec.testEngineState(Recognizer.ALLOCATED)
                && !rec.testEngineState(Recognizer.DEALLOCATING_RESOURCES)) {
                    try {
                        System.out.println("forcing finalize");
                        rec.forceFinalize(true);
                        System.out.println("deallocating");
                        rec.deallocate();
                    } catch(Exception e2) {
                        e2.printStackTrace(System.out);
                    }
                }
            }
        };
        (new Thread(lt)).start();
        
    }
}
