package demo.util;

import java.io.File;
import java.util.List;


public class Configure {
	
	private static Configure configure = new Configure();
	public static Configure getConfigureInstance() {
        return configure;
    }
	private String lang = "cpp";
	private String inputSrcPath;
    private String usageSrcPath;
	
	public static final String CPP = "cpp";
	private String curr_pro_suffix = ".cpp";
	public static final String CPP_PRO_SUFFIX = ".cpp";
	public static final String OS_DOT_NAME = "os.name";
	private String analyzedProjectName = "beego";
	private String  ProjectName= "beego";
	
	public static final String WINDOWS = "windows";
	public static final String LINUX = "linux";
	public static final String MAC = "mac";
	
	public static final String CPP_LANG = "cpp";
	public static final String EXTERNAL_DATA_SOURCE = "datasource";
	    
	
    private String outputDotFile = analyzedProjectName + ".dot";
    private String outputCsvNodeFile = analyzedProjectName + "_node.csv";
    private String outputCsvEdgeFile = analyzedProjectName + "_edge.csv";
    private String outputJsonFile = analyzedProjectName  + "_dep.json";
    private String outputXmlFile = analyzedProjectName + "_dep.xml";
    private String attributeName = analyzedProjectName + "-sdsm";
    private String schemaVersion = "1.0";
    
    private List<String> includePath;
    
	public void setLang(String lang) {
        this.lang = lang;

        if(lang.equals(CPP)) {
            curr_pro_suffix = CPP_PRO_SUFFIX;
        }
    }
	
	public void setInputSrcPath(String inputSrcPath) {
        this.inputSrcPath = inputSrcPath;
    }
	public void setUsageSrcPath(String usageSrcPath) {
        this.usageSrcPath = usageSrcPath;
    }
	public void setIncludePath(List<String> includePath) {
		this.includePath = includePath;
	}
	
    public void setAnalyzedProjectName(String analyzedProjectName) {
        new File(analyzedProjectName + "-out").mkdir();
        if(OsUtil.isWindows()) {
            this.analyzedProjectName = analyzedProjectName + "-out\\" + analyzedProjectName;
        }
        if(OsUtil.isMac() || OsUtil.isLinux()) {
            this.analyzedProjectName = analyzedProjectName + "-out/" + analyzedProjectName;
        }
        this.ProjectName = analyzedProjectName;
    }
    
    public void setDefault() {
        outputJsonFile = analyzedProjectName  + "_dep.json";
        outputDotFile = analyzedProjectName + ".dot";
        outputXmlFile = analyzedProjectName + "_dep.xml";
        outputCsvNodeFile = analyzedProjectName + "_node.csv";
        outputCsvEdgeFile = analyzedProjectName + "_edge.csv";
        attributeName = analyzedProjectName + "-sdsm";
    }
    public String getAnalyzedProjectName() {
        return analyzedProjectName;
    }
    public String getProjectName() {
    	return ProjectName;
    }
}
