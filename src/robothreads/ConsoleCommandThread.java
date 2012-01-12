package robothreads;

import commands.datatypes.InputString;
import commands.targets.ConsoleTarget;
import workflow.WorkflowManager;
import robothreads.abstracts.RobotThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/11/11
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleCommandThread extends RobotThread {

    public void run() {
        while (true) {
            System.out.printf("Enter Command\n");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String line = bufferedReader.readLine();
                InputString inputString = new InputString(line, ConsoleTarget.getInstance());
                WorkflowManager.process(inputString);

            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }
}

