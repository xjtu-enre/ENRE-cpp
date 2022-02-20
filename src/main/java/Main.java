import picocli.CommandLine;
import util.Configure;

import java.util.Set;

public class Main {
	
	/**
	* @methodsName: main
	* @description: main function
	*/
	public static void main(String[] args) throws Exception {

		Configure configure = CommandLine.populateCommand(new Configure(),args);

		String lang = configure.getLang();
		String inputDir = configure.getInputSrcPath();
	    // String usageDir = configure.getUsageSrcPath();
		String projectName = configure.getProjectName();
		Set<String> Program_environment = configure.getProgram_environment();
		System.out.println(Program_environment.size());

		if(lang.equals("cpp")) {
			Processor processor = new Processor(Program_environment);
			processor.parseAllFlie(inputDir);
			System.out.println("Entity found finish!");
			processor.dependencyBuild();
			processor.outputFile(projectName);
			System.out.println("Process finish!");
		}
	}
}
