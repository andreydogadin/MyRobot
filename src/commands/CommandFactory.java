package commands;

import commands.datatypes.InputString;
import utils.RobotConsts;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 22.08.11
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class CommandFactory {
    private static RobotCommand getCommand(String commandLine) {
        if (commandLine.isEmpty()) return null;
        RobotCommand result = null;
        String [] parsedString = commandLine.split(" ", 2);
        String commandParam = null;
        String commandName = parsedString[0];
        if (parsedString.length > 1) commandParam = parsedString[1];
        try {
            Class commandClass = Class.forName("commands.commandsImpl." + commandName);
            result = (RobotCommand) commandClass.newInstance();
            if (commandParam != null) result.setParam(commandParam);
        } catch (Exception ex) {
            System.out.println("Something wrong with: " + "commands.commandsImpl." + commandName);
        }
        return result;
    }
    public static RobotCommand getCommand(InputString inputString)
    {
        RobotCommand result = CommandFactory.getCommand(inputString.getValue());
        if (result != null)
            result.setResultTarget(inputString.getTarget());
        return result;
    }

    private static String getClassFullName(String abbreviation) {
        String fullName = null;
        String matchPattern = "";
        abbreviation = abbreviation.toUpperCase();
        File commandDir = new File(RobotConsts.COMMANDS_PATH);
        String[] commands = commandDir.list();
        for (char c : abbreviation.toCharArray())
            matchPattern += c + "\\w*";
        matchPattern += ".java";
        Pattern p = Pattern.compile(matchPattern);
        for (String command : commands) {
            Matcher m = p.matcher(command);
            if (m.matches())
                fullName = command.substring(0, command.indexOf("."));
        }
        return fullName;
    }
}
