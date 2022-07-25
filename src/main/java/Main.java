import picocli.CommandLine;
import util.Configure;

import java.util.Set;

public class Main {
	
	/**
	* @methodsName: main
	* @description: main function
	*/
	public static void main(String[] args) throws Exception {

		Configure configure = CommandLine.populateCommand(Configure.getConfigureInstance(),args);

		String lang = configure.getLang();
		String inputDir = configure.getInputSrcPath();
		String projectName = configure.getProjectName();
		Set<String> Program_environment = configure.getProgram_environment();
		
		long startTime = System.currentTimeMillis();
		if(lang.equals("cpp")) {
			Processor processor = new Processor(Program_environment);
			processor.parseAllFile(inputDir);
			System.out.println("Entity found finish!");
			processor.dependencyBuild();
			processor.outputFile(projectName);
//			System.out.println("Process finish!");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Consumed time: " + (float) ((endTime - startTime) / 1000.00) + " s,  or "
				+ (float) ((endTime - startTime) / 60000.00) + " min.");
	}
}
