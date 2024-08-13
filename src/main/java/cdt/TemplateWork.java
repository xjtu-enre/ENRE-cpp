package cdt;

import picocli.CommandLine;
import util.Configure;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class TemplateWork {
    public static Processor execute(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        final Configure CliParam = new Configure();
        final CommandLine commandLine = new CommandLine(CliParam);
        try {
            final CommandLine.ParseResult parseResult = commandLine.parseArgs(args);
            if (parseResult.isUsageHelpRequested()) {
                commandLine.usage(System.out);
                System.exit(0);
            }
            if (parseResult.isVersionHelpRequested()) {
                commandLine.printVersionHelp(System.out);
                System.exit(0);
            }
        } catch (CommandLine.ParameterException e) {
            commandLine.usage(System.out);
            System.exit(1);
        }

        Configure configure = CommandLine.populateCommand(Configure.getConfigureInstance(),args);
        configure.dealWithInputSrcPath();
        String inputDir = configure.getInputSrcPath();
        String projectName = configure.getProjectName();
        System.out.println(projectName);
        Set<String> Program_environment = configure.getProgram_environment();
        System.out.println(Program_environment);
        long startTime = System.currentTimeMillis();

        Processor processor = new Processor(Program_environment);
        processor.parseAllFile(inputDir);
        System.out.println("Entity found finish!");
        processor.typeBinding();
        processor.dependencyBuild();
        processor.outputFile(projectName);
        System.out.println("Process finish!");

        long endTime = System.currentTimeMillis();
        System.out.println("Consumed time: " + (float) ((endTime - startTime) / 1000.00) + " s,  or "
                + (float) ((endTime - startTime) / 60000.00) + " min.");
        return processor;
    }
}
