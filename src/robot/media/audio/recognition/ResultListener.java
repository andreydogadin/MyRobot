package robot.media.audio.recognition;

import commands.CommandDispatcher;
import commands.RobotCommand;
import commands.commandsImpl.OutText;
import commands.datatypes.InputString;
import commands.targets.SpeechTarget;
import workflow.WorkflowManager;
import robot.MyRobot;
import utils.RobotConsts;

import javax.speech.recognition.*;

/**
 * Class to print out result events, and deallocate the recognizer after a
 * given number of accepted results. Also demonstrates playback
 * of recorded speech and use of tokenCorrection (except the
 * actual tokenCorrection line has been commented out).
 */
public class ResultListener extends ResultAdapter {
    private Recognizer rec;
    private CommandDispatcher dispatcher;
    /**
     * Creates a ResultListener which will deallocate the given Recognizer
     * after "nRecs" accepted recognitions, and will re-play the recorded audio
     * if "playAudio" is true (and audio is being saved using
     * RecognizerProperties.setResultAudioProvided(true))
     */
    public ResultListener(Recognizer rec) {
        this.rec = rec;
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
    }

  public  void resultAccepted(ResultEvent e) {
        /*final*/ FinalResult r = (FinalResult)(e.getSource());
//        Runnable lt = new Runnable() {
//            public void run() {

                RobotCommand sayCommand = new OutText();
                sayCommand.setResultTarget(SpeechTarget.getInstance());
                try {
                    System.out.print("Result Accepted: "+r);
                    String tags[] = null;
                    if(r.getGrammar() instanceof RuleGrammar) {
                        System.out.println("\nRuleGrammar name="+((FinalRuleResult)r).getRuleGrammar(0).getName());
                        System.out.println("Rule name="+((FinalRuleResult)r).getRuleName(0));

                        tags = ((FinalRuleResult) r).getTags();
                    }
                    if (tags.length > 0)
                    {
                        InputString inputString = new InputString(tags[0], SpeechTarget.getInstance());
                        System.out.println(tags[0]);
                        if (tags[0].equalsIgnoreCase("Eva"))
                            WorkflowManager.process(inputString);
                        else
                        {
                            if (MyRobot.getInstance().getMemory().getCurrent(RobotConsts.ACCEPT_VOICE_COMMAND) != null)
                                WorkflowManager.process(inputString);
                        }
                    }

                } catch(Exception e1) {
                    e1.printStackTrace(System.out);
                }
//            }
//        };
//        (new Thread(lt)).start();

    }
}
