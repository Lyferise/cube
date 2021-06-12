package com.lyferise.cube.cli;

import org.apache.commons.cli.*;

public class ArgumentParser {
    private final Option optionHost = Option.builder("h")
            .required(true)
            .hasArg()
            .desc("Host address")
            .longOpt("host")
            .build();
    private final Option optionUser = Option.builder("u")
            .required(true)
            .hasArg()
            .desc("Username")
            .longOpt("user")
            .build();
    private final Option optionPass = Option.builder("p")
            .required(true)
            .hasArg()
            .desc("Password")
            .longOpt("password")
            .build();
    private final Option optionHelp = Option.builder()
            .required(false)
            .desc("Print this message")
            .longOpt("help")
            .build();

    private String host;
    private String username;
    private String password;

    public String getHost() {
        return this.host;
    }

    public String getUser() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean parse(String[] args) {
        Options helpOptions = new Options();
        helpOptions.addOption(optionHelp);

        Options options = new Options();
        options.addOption(optionHost);
        options.addOption(optionUser);
        options.addOption(optionPass);
        options.addOption(optionHelp);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(helpOptions, args, true);
            if(commandLine.getOptions().length == 0) {
                commandLine = parser.parse(options, args);

                if (commandLine.hasOption("host")) {
                    this.host = commandLine.getOptionValue("host");
                }
                if (commandLine.hasOption("user")) {
                    this.username = commandLine.getOptionValue("user");
                }
                if (commandLine.hasOption("password")) {
                    this.password = commandLine.getOptionValue("password");
                }
            }
            else {
                printHelpOptions(options);
                return false;
            }
        } catch (ParseException exception) {
            System.out.println(exception.getMessage());
            printHelpOptions(options);
            return false;
        }
        return true;
    }

    private void printHelpOptions(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(getPackageName(), options, true);
    }

    private String getPackageName() {
        return new java.io.File(ArgumentParser.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
