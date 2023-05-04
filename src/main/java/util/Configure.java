package util;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CommandLine.Command(name = "ENRE-CPP", mixinStandardHelpOptions = true, version = "1.0.2")

public class
Configure {

    private static Configure configure = new Configure();
    public Configure() {}
    @CommandLine.Parameters(index = "0", arity = "1", description = "The directory to be analyzed", paramLabel = "directory")
    private String inputSrcPath;
    @CommandLine.Parameters(index = "1", arity = "1", description = "A short alias name of the anayzed source code project", paramLabel = "projectName")
    private String projectName;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display help for command")
    public boolean help = false;

    public static Configure getConfigureInstance() {
        return configure;
    }
    @CommandLine.Option(names = { "-v", "--version" }, versionHelp = true,
            description = "print version information and exit")
    boolean versionRequested;

    @CommandLine.Option(names = { "-p", "--program_environment" },
            description = "the program environment.")
    private Set<String> program_environment = new HashSet<>();
    @CommandLine.Option(names = {"-d", "--dir"},
            description = "other directory need to analysis.")
    private List<String> dirs;


//    @CommandLine.Parameters(index = "3", arity = "1", description = "The output path", paramLabel = "outputPath")
//    private String usageSrcPath;

    public void dealWithInputSrcPath() throws IOException{
        File folder = new File(this.inputSrcPath);
        if (!folder.exists()) {
            File a = new File(System.getProperty("user.dir"));
            File parentFolder = new File(a.getParent());
            File b = new File(parentFolder, this.inputSrcPath);
            this.inputSrcPath = b.getCanonicalPath();
        }else{
            this.inputSrcPath = folder.getCanonicalPath();
        }
    }

    public String getInputSrcPath() {
        return this.inputSrcPath;
    }
    public Set<String> getProgram_environment() { return this.program_environment; }
    public List<String> getOtherDirs() { return this.dirs; }

    public static final String CPP = "cpp";
    private String curr_pro_suffix = ".cpp";
    public static final String CPP_PRO_SUFFIX = ".cpp";
    public static final String OS_DOT_NAME = "os.name";

    public static final String WINDOWS = "windows";
    public static final String LINUX = "linux";
    public static final String MAC = "mac";

    public static final String CPP_LANG = "cpp";
    public static final String EXTERNAL_DATA_SOURCE = "datasource";


    private String schemaVersion = "1.0";

    private List<String> includePath;

    public void setInputSrcPath(String inputSrcPath) {
        this.inputSrcPath = inputSrcPath;
    }
    public void setIncludePath(List<String> includePath) {
        this.includePath = includePath;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public static final int ENTITY_KIND_NUM = 16;
    public static final int Namespace = 1;
    public static final int File = 2;
    public static final int Class = 3;
    public static final int Struct = 4;
    public static final int Union = 5;
    public static final int Function = 6;
    public static final int Enum = 7;
    public static final int Enumerator = 8;
    public static final int Typedef = 9;
    public static final int Alias = 10;
    public static final int Macro = 11;
    public static final int Variable = 12;
    public static final int Label = 13;
    public static final int Virtual = 14;
    public static final int Default= 14;



    public static final int NOTFOUNDENTITY = -1;
    public static final int FOUNDENTITY = 0;
    public static final int FOUNDOVERLOADENTITY = 1;
}
