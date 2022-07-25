package cdt;


import entity.EntityRepo;
import entity.FileEntity;
import entity.Location;
import entity.MacroEntity;
import util.Configure;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.dom.parser.cpp.GPPScannerExtensionConfiguration;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;
import org.eclipse.cdt.internal.core.parser.scanner.ScannerUtility;

import java.io.*;
import java.util.*;
public class FileParser {
	String filepath;
	EntityRepo entityrepo;
	FileEntity fileEntity;
	HashMap<String, String> macrorepo;
	HashMap<String,Integer> fileList;
	Map<String, String> definedMacros = new HashMap<>();
	Set<String> Program_environment;
	Configure configure = Configure.getConfigureInstance();
	public FileParser(String filepath, EntityRepo entityrepo,HashMap<String,Integer> fileList, Set<String> environment) {
		this.filepath = filepath;
		this.entityrepo = entityrepo;
		this.macrorepo = new HashMap<String, String>();
		this.fileList = fileList;
		this.Program_environment = environment;
	}

	/**
	 * @methodsName: parse
	 * @description: parse one file
	 * @param:  null
	 * @return: void
	 * @throws:
	 */
	public void parse( ) throws Exception {
		if(exitFile(filepath)) {
			if(isFileParse(filepath)) {
				return ;
			}
		}
		fileList.put(filepath,1);
		final FileContent content = FileContent.createForExternalFileLocation(filepath);
		IParserLogService log = new DefaultLogService();
		
		boolean isIncludePath = false;
		String[] includePaths = new String[0];
		definedMacros.put("__cplusplus", "1");
		IASTTranslationUnit tu = GPPLanguage.getDefault().getASTTranslationUnit(content,
				new ScannerInfo(definedMacros), IncludeFileContentProvider.getEmptyFilesProvider(),
				EmptyCIndex.INSTANCE, 0, log);

		CppVisitor visitor = new CppVisitor(entityrepo, filepath);
		fileEntity = visitor.getfile();
		HashSet<String> includePathset = getdirectedinclude(tu);

		IASTPreprocessorStatement[] statements= tu.getAllPreprocessorStatements();
		getallstatements(statements);
		for(String includePath:includePathset) {
			if(!isFileParse(includePath)) {
				FileParser fileparse = new FileParser(includePath, entityrepo, fileList, Program_environment);
				fileparse.parse();
			}
			if(entityrepo.getEntityByName(includePath)!=null && entityrepo.getEntityByName(includePath) instanceof FileEntity) {
				FileEntity includeFileEntity = (FileEntity)entityrepo.getEntityByName(includePath);
				fileEntity.addincludeEntity(includeFileEntity);
				definedMacros.putAll(includeFileEntity.getMacroRepo());
				fileEntity.getMacroRepo().putAll(includeFileEntity.getMacroRepo());
			}
			isIncludePath = true;
		}

		getMacro(filepath);
		for(String macroInfo:fileEntity.getMacroRepo().keySet()) {
			definedMacros.remove(macroInfo);
		}

		if(isIncludePath) {
			tu = GPPLanguage.getDefault().getASTTranslationUnit(content,
					new ScannerInfo(definedMacros), IncludeFileContentProvider.getEmptyFilesProvider(),
					EmptyCIndex.INSTANCE, 0, log);
		}
		tu.accept(visitor);
	}
	
	/**
	 * @methodsName: exitFile
	 * @description: Check whether the file exists based on the path
	 * @param:  String path
	 * @return: boolean
	 * @throws:
	 */
	public boolean exitFile(String path) {
		if(fileList.containsKey(path)) return true;
		return false;
	}
	/**
	 * @methodsName: isFileParse
	 * @description: Check whether the file has been parsed
	 * @param:  String path
	 * @return: boolean
	 * @throws:
	 */
	public boolean isFileParse(String path) {
		if(fileList.containsKey(path)) {
			if(fileList.get(path)==0) {
				return false;
			}
			else
				return true;
		}
		return true;
	}

	/**
	 * @methodsName: getallstatements
	 * @description: Get all preprocessed statements
	 * @param:  IASTPreprocessorStatement[] statements
	 * @return: void
	 * @throws:
	 */
	public void getallstatements(IASTPreprocessorStatement[] statements) {
		for(IASTPreprocessorStatement statement:statements) {
			if(statement instanceof IASTPreprocessorMacroDefinition) {
				IASTPreprocessorMacroDefinition macroDefinition = (IASTPreprocessorMacroDefinition)statement;
				String macroname = macroDefinition.getName().toString();
				String expansion = macroDefinition.getExpansion();
				fileEntity.getMacroRepo().put(((IMacroBinding)macroDefinition.getName().resolveBinding()).toString(), expansion);
				Location location = new Location(statement.getFileLocation().getNodeLength(),
						statement.getFileLocation().getStartingLineNumber(),
						statement.getFileLocation().getEndingLineNumber(),
						statement.getFileLocation().getNodeOffset(),
						statement.getFileLocation().getFileName().toString());
				MacroEntity macroEntity = new MacroEntity(statement.getRawSignature(),
						macroname, fileEntity, entityrepo.generateId(), location);
				entityrepo.add(macroEntity);
			}
		}
	}

	/**
	 * @methodsName: getchild
	 * @description:
	 * @param:  IASTNode parent
	 * @return: IASTNode
	 * @throws:
	 */
	public IASTNode getchild(IASTNode parent) {
		if(parent.getChildren() == null) {
			return null;
		}
		else {
			for(IASTNode child:parent.getChildren()) {
				getchild(child);
			}
		}
		return null;
	}

	/**
	 * @methodsName: getTranslationUnit
	 * @description: Get IASTTranslationUnit through FileContent
	 * @param:  File source
	 * @return: IASTTranslationUnit
	 * @throws:
	 */
	public IASTTranslationUnit getTranslationUnit(File source) throws Exception{
		FileContent reader = FileContent.create(source.getAbsolutePath(), getContentFile(source).toCharArray());
		return GCCLanguage.getDefault().getASTTranslationUnit( reader, new ScannerInfo(), IncludeFileContentProvider.getSavedFilesProvider(), null,  ILanguage.OPTION_IS_SOURCE_UNIT, new DefaultLogService());
	}

	/**
	 * @methodsName: getContentFile
	 * @description: Read file content
	 * @param:  File file
	 * @return: String
	 * @throws:
	 */
	public String getContentFile(File file) throws IOException {
		StringBuilder content = new StringBuilder();
		String line;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)))) {
			while ((line = br.readLine()) != null)
				content.append(line).append('\n');
		}

		return content.toString();
	}

	/**
	 * @methodsName: getdirectedinclude
	 * @description: Obtain the include information of the file
	 * @param:  IASTTranslationUnit tu
	 * @return: HashSet<String>
	 * @throws:
	 */
	public HashSet<String> getdirectedinclude(IASTTranslationUnit tu) {
		IASTPreprocessorStatement[] statements = tu.getAllPreprocessorStatements();
		HashSet<String> includeFile = new HashSet<String>();
		for(IASTPreprocessorStatement includefile:statements) {
			if(includefile instanceof IASTPreprocessorIncludeStatement) {
				String path = ((IASTPreprocessorIncludeStatement)includefile).toString();
				int pos = path.indexOf(' ');
				path = path.substring(pos+1).trim();
				if (path.startsWith("\"") || path.startsWith("<")){
					path = path.substring(1);
					path = path.substring(0,path.length()-1);
				}
				File file = new File(includefile.getContainingFilename());
				String filePath = file.getParent();
				String checkPath = uniformPath(ScannerUtility.reconcilePath(filePath), ScannerUtility.reconcilePath(path));
				checkPath =  uniformPath(checkPath);
				if(exitFile(checkPath)) {
					includeFile.add(checkPath);
//					if(checkPath.endsWith(".h")) {
//						checkPath = checkPath.replace(".h", ".cpp");
//						if(exitFile(checkPath)&& !checkPath.equals(this.filepath)) {
//							includeFile.add(checkPath);
//							fileEntity.addinclude(checkPath);
//						}
//					}
//					else if(checkPath.endsWith(".hpp")) {
//						checkPath = checkPath.replace(".hpp", ".cpp");
//						if(exitFile(checkPath)) {
//							includeFile.add(checkPath);
//
//						}
//					}
					continue;
				}

				else {
					for(String en_path:Program_environment) {
						checkPath = ScannerUtility.createReconciledPath(en_path, path);
						checkPath = uniformPath(checkPath);
						if(exitFile(checkPath)) {
							includeFile.add(checkPath);
							if(checkPath.endsWith(".h")) {
								checkPath = checkPath.replace(".h", ".cpp");
								if(exitFile(checkPath)) includeFile.add(checkPath);
								break;
							}
							if(checkPath.endsWith(".hpp")) {
								checkPath = checkPath.replace(".hpp", ".cpp");
								if(exitFile(checkPath)) includeFile.add(checkPath);
							}
							break;
						}
					}
				}
			}
		}

		return includeFile;
	}

	/**
	 * @methodsName: concat
	 * @description: String comparison
	 * @param:  String s1, String s2
	 * @return: String
	 * @throws:
	 */
	public String concat(String s1, String s2) {
		if (s1 == null)
			return s2;
		if (s2 == null)
			return s1;
		int len = Math.min(s1.length(), s2.length());
		// Find the index for the end of overlapping part
		int index = -1;
		for (int i = len; i > 0; i--) {
			String substring = s2.substring(0, i);
			if (s1.endsWith(substring)) {
				index = i;
				break;
			}
		}
		StringBuilder sb = new StringBuilder(s1);
		if (index < 0)
			sb.append(s2);
		else if (index <= s2.length())
			sb.append(s2.substring(index));
		return sb.toString();
	}
	public Map<String, String> getdefinedMacros(){
		return this.definedMacros;
	}



	/**
	 * @methodsName: getMacro
	 * @description: Get the macro definition information for the file
	 * @param:  String file
	 * @return: void
	 * @throws:
	 */
	public void getMacro(String file) {
		String content = "";
		try {
			CodeReader cr = new CodeReader(file);
			content = new String(cr.buffer);
		} catch (IOException e) {
		}
		IScanner scanner = new CPreprocessor(FileContent.create(file, content.toCharArray()), new ScannerInfo(),
				ParserLanguage.CPP,new NullLogService(), GPPScannerExtensionConfiguration.getInstance(new ScannerInfo()),
				IncludeFileContentProvider.getEmptyFilesProvider());
		scanner.setProcessInactiveCode(true);
		if (scanner==null) return;
		Map<String, IMacroBinding> macros = scanner.getMacroDefinitions();
		for (String key : macros.keySet()) {
			IMacroBinding imb = macros.get(key);
			String exp = new String(macros.get(key).getName());
			fileEntity.getMacroRepo().put(macros.get(key).toString(), exp);
		}

	}
	
	
//	public static String uniformPath(String str1, String str2, String str3) {
//		String str4 = str1.split("\\\\")[str1.split("\\\\").length-1];
//		String str5 = str2.split("\\\\")[0];
//		
//		if(str5.equals(str4)) {
//			String str6 = str1.substring(0, str1.length()-str4.length()-1);
//			String str7 = str2.substring(str5.length()+1);
//			return uniformPath(str6, str7, str5);
//		}
//		
//		if(str3.length() == 0) return str1+"\\"+str2;
//		return str1+"\\" + str3 + "\\"+ str2;
//		
//	}
	public static String uniformPath(String root, String include_file) {
		String[] root_split = root.split("[/|\\\\]");
		String[] include_file_split = include_file.split("[/|\\\\]");
		Stack<String> pathStack = new Stack<>();
		if(include_file_split[0].equals("..")) {
			int up_level = -1;
			for(int i=0;i<include_file_split.length;i++) {
				if(include_file_split[i].equals(".."))
					up_level = i;
				else
					break;
			}
			StringBuilder sb = new StringBuilder();
			for (int i=0;i <root_split.length - 2 - up_level;i++) {
				sb.append(root_split[i]);
				sb.append(File.separator);
			}
			for(int i=up_level+1; i<include_file_split.length;i++) {
				sb.append(include_file_split[i]);
				if (i<include_file_split.length-1)
					sb.append(File.separator);
			}
			return sb.toString();
		}
		
		boolean isRoot = false;
		int same_location = -1;
		int include_file_length = include_file_split.length;
		if(include_file_length > 1) {
			for(int i=root_split.length-1;i>0;i--) {
				if(root_split[i].equals(include_file_split[0])) {
					isRoot = true;
					same_location = i;
					int level_word_number = Math.min(root_split.length-i,include_file_split.length);
					for(int j=0; j<level_word_number; j++) {
						if(root_split[i+j].equals(include_file_split[j])) {
							pathStack.push(include_file_split[j]);
						}
						else {
							isRoot = false;
							continue;
						}
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		if(isRoot) {
			for (int i=0;i < same_location;i++) {
				sb.append(root_split[i]);
				sb.append(File.separator);
			}
			for (int i=0;i<include_file_split.length;i++) {
				sb.append(include_file_split[i]);
				if (i<include_file_split.length-1)
					sb.append(File.separator);
			}
		}
		else {
			for (int i=0;i<root_split.length;i++) {
				sb.append(root_split[i]);
				sb.append(File.separator);
			}
			sb.append(include_file);
		}
		return sb.toString();
	}

	public String uniformPath(String path) {
		String[] paths = path.split("[/|\\\\]");
		StringBuilder sb = new StringBuilder();
		Stack<String> pathStack = new Stack<>();
		for (int i=0;i<paths.length;i++) {
			String s = paths[i];
			if (s.equals(".")) continue;
			if (s.equals("..") && !pathStack.empty()) {
				pathStack.pop();
				continue;
			}
			pathStack.push(s);
		}

		for (int i=0;i<pathStack.size();i++) {
			sb.append(pathStack.get(i));
			if (i<pathStack.size()-1)
				sb.append(File.separator);
		}
		
		return sb.toString();
	}


}
