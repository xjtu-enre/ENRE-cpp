package demo;
import static java.lang.System.exit;
import demo.util.Configure;


public class Main {

	//private static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	/**
	* @methodsName: main
	* @description: main function
	* @param:  String[] args
	* @return: void
	* @throws: 
	*/
	public static void main(String[] args) throws Exception {
		
		//BasicConfigurator.configure();
		String log4jConfPath = "lib/log4j.properties";
		//PropertyConfigurator.configure(log4jConfPath);
		 
		checkInput(args);
		String lang = args[0];
		String inputDir = args[1];
	    String usageDir = args[2];
	    String projectName = usageDir;
	    if (args.length > 3) {
	    	projectName = args[3];
	    }
	    config(lang, inputDir, usageDir, projectName);
		if(!lang.equals(Configure.EXTERNAL_DATA_SOURCE)) {
			Processor processor = new Processor();
			processor.parseAllFlie(inputDir);
			System.out.println("Entity found finish!");
			processor.dependencyBuild();
			System.out.println("Process finish!");
		}
	}
	
	/**
	* @methodsName: checkInput
	* @description: Check the accuracy of input
	* @param:  String[] args
	* @return: void
	* @throws: 
	*/
	private static void checkInput(String[] args) {
        
        if(args.length < 2) {
            System.out.println("Not enough parameters!");
            exit(1);
        }
        if(!args[0].equals(Configure.CPP_LANG)&& !args[0].equals(Configure.EXTERNAL_DATA_SOURCE)) {
            System.out.println("Not support this language: " + args[0]);
            exit(1);
        }
    }
	
	/**
	* @methodsName: config
	* @description: Configuration information
	* @param:  String lang, String inputDir, String usageDir, String projectName
	* @return: void
	* @throws: 
	*/
	private static void config(String lang, String inputDir, String usageDir, String projectName) {
        Configure configure = Configure.getConfigureInstance();

        configure.setLang(lang);
        configure.setInputSrcPath(inputDir);
        configure.setUsageSrcPath(usageDir);
        configure.setAnalyzedProjectName(projectName);
        configure.setDefault();
    }
	
}
