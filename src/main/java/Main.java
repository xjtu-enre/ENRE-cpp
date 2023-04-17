import cdt.TypeBinding;
import picocli.CommandLine;
import util.Configure;
import util.UnionFind;

import java.util.Set;

public class Main {

	/**
	* @methodsName: main
	* @description: main function
	*/
	public static void main(String[] args) throws Exception {
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
		Set<String> Program_environment = configure.getProgram_environment();

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
	}
}
