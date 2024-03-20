package cdt;


import entity.EntityRepo;
import entity.FileEntity;
import entity.Location;
import entity.MacroEntity;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.internal.core.index.CIndex;
import org.eclipse.cdt.internal.core.index.IIndexFragment;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.InternalParserUtil;
import org.eclipse.cdt.internal.core.parser.SavedFilesProvider;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import relation.Relation;
import relation.RelationRepo;
import relation.RelationType;
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
	RelationRepo relationrepo;
	FileEntity fileEntity;
	HashMap<String, String> macrorepo;
	HashMap<String,Integer> fileList;
	Map<String, String> definedMacros = new HashMap<>();
	Set<String> Program_environment;
	Configure configure = Configure.getConfigureInstance();
	MacroRepo macroRepo = new MacroRepo();
	public FileParser(String filepath, EntityRepo entityrepo,RelationRepo relationrepo, HashMap<String,Integer> fileList, Set<String> environment) {
		this.filepath = filepath;
		this.entityrepo = entityrepo;
		this.relationrepo = relationrepo;
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
//		try{
		if(exitFile(filepath)) {
			if(isFileParse(filepath)) {
				return ;
			}
		}
		System.out.println("Parse file path: " + this.filepath);
		fileList.put(filepath,1);
		final FileContent content = FileContent.createForExternalFileLocation(filepath);
		if(content == null){
			return;
		}
		IParserLogService log = new DefaultLogService();
		boolean isIncludePath = false;
		String[] includePaths = new String[0];
		definedMacros.put("__cplusplus", "1");
		definedMacros.put("DT_VOID", "");
		definedMacros.putAll(macroRepo.getDefinedMacros());
		IASTTranslationUnit tu = GPPLanguage.getDefault().getASTTranslationUnit(content,
				new ScannerInfo(definedMacros), IncludeFileContentProvider.getEmptyFilesProvider(),
				EmptyCIndex.INSTANCE, 0, log);

		CppVisitor visitor = new CppVisitor(entityrepo, relationrepo, filepath);
		fileEntity = visitor.getfile();
		HashMap<String, Integer> includePathset = getdirectedinclude(tu);

//			ArrayList<String> includeFilePathsArray = new ArrayList<>();
		for(String includePath:includePathset.keySet()) {
			if(!isFileParse(includePath)) {
				FileParser fileparse = new FileParser(includePath, entityrepo, relationrepo, fileList, Program_environment);
				fileparse.parse();
			}
			if(entityrepo.getEntityByName(includePath)!=null && entityrepo.getEntityByName(includePath) instanceof FileEntity) {
				FileEntity includeFileEntity = (FileEntity)entityrepo.getEntityByName(includePath);
				fileEntity.addincludeEntity(includeFileEntity);
				fileEntity.addRelation(new Relation(fileEntity, includeFileEntity, RelationType.INCLUDE, fileEntity.getId(),
						includePathset.get(includePath), -1));
//					includeFilePathsArray.add(includeFileEntity.getQualifiedName());
				fileEntity.getMacroRepo().putAll(includeFileEntity.getMacroRepo());
			}
			isIncludePath = true;
		}
		getMacro(filepath);
		/*
		 * 当存在Include关系时，建立一个include循环
		 */
		ArrayList<String> includeFilePathsArray =new ArrayList<String>(fileEntity.getIncludeFilePathsArray());

		if(isIncludePath) {
			final String[] EMPTY_ARRAY_STRING = new String[0];
			// Returns an array of paths that are searched when processing an include directive.
			String[] includePath = new String[includeFilePathsArray.size()];
			String[] includeFiles = new String[includeFilePathsArray.size()];
			for(int i=0; i<includeFilePathsArray.size();i++){
				String filePath = includeFilePathsArray.get(i);
				File file = new File(filePath);
				includePath[i] = file.getParent();
				includeFiles[i] = file.getName();
			}
			String[] macroFiles = EMPTY_ARRAY_STRING;
			IScannerInfo scannerInfo = new ExtendedScannerInfo(definedMacros, includePath, macroFiles, includeFiles);
			InternalFileContentProvider includeContentProvider = new InternalFileContentProvider() {
				@Override
				public InternalFileContent getContentForInclusion(String filePath, IMacroDictionary macroDictionary) {
					InternalFileContent ifc = (InternalFileContent) FileContent.createForExternalFileLocation(filePath);
					return ifc;
				}
				@Override
				public InternalFileContent getContentForInclusion(IIndexFileLocation ifl, String astPath) {
					InternalFileContent c = InternalParserUtil.createFileContent(ifl);
					return c;
				}
			};

			IIndex idx = new CIndex(new IIndexFragment[] {});
			int options = ILanguage.OPTION_PARSE_INACTIVE_CODE;
			tu = GPPLanguage.getDefault().getASTTranslationUnit(content, scannerInfo, includeContentProvider, idx, options, log);
		}
		IASTPreprocessorStatement[] statements= tu.getAllPreprocessorStatements();
		getallstatements(statements);

		tu.accept(visitor);
	}


	private boolean isRightFile(String include, String toMatch) {
		if (System.getProperty("os.name").contains("Win"))
			return include.equalsIgnoreCase(toMatch);
		return include.equals(toMatch);
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
						this.fileEntity.getId());
				MacroEntity macroEntity = new MacroEntity(statement.getRawSignature(),
						macroname, fileEntity, entityrepo.generateId(), location);
				this.fileEntity.addRelation(new Relation(fileEntity, macroEntity, RelationType.DEFINE, fileEntity.getId(),
						statement.getFileLocation().getStartingLineNumber(),
						statement.getFileLocation().getNodeOffset()));
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
		return GCCLanguage.getDefault().getASTTranslationUnit( reader, new ScannerInfo(), IncludeFileContentProvider.getSavedFilesProvider(), null,  ILanguage.OPTION_NO_IMAGE_LOCATIONS, new DefaultLogService());
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
	public HashMap<String, Integer> getdirectedinclude(IASTTranslationUnit tu) {
		IASTPreprocessorIncludeStatement[] statements = tu.getIncludeDirectives();
		HashMap<String, Integer> includeFile = new LinkedHashMap<String, Integer>();
		for(IASTPreprocessorIncludeStatement includeStatement:statements) {
			String path = includeStatement.getName().toString();
			File file = new File(includeStatement.getContainingFilename());
			String filePath = file.getParent();
			String checkPath = uniformPath(ScannerUtility.reconcilePath(filePath), ScannerUtility.reconcilePath(path));
			checkPath =  uniformPath(checkPath);
			if(exitFile(checkPath)) {
				includeFile.put(checkPath, includeStatement.getFileLocation().getStartingLineNumber());
				continue;
			}
			else {
				for(String en_path:Program_environment) {
					checkPath = ScannerUtility.createReconciledPath(en_path, path);
					checkPath = uniformPath(checkPath);
					if(exitFile(checkPath)) {
						includeFile.put(checkPath, includeStatement.getFileLocation().getStartingLineNumber());
						break;
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
		final FileContent content = FileContent.createForExternalFileLocation(file);
		IScanner scanner = new CPreprocessor(content, new ScannerInfo(),
				ParserLanguage.CPP,new NullLogService(), GPPScannerExtensionConfiguration.getInstance(new ScannerInfo()),
				IncludeFileContentProvider.getEmptyFilesProvider());
		scanner.setProcessInactiveCode(true);
		if (scanner==null) return;
		Map<String, IMacroBinding> macros = scanner.getMacroDefinitions();
	}



	public String uniformPath(String root, String include_file) {
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
			if(exitFile(sb.toString())){
				return sb.toString();
			}
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
		if(exitFile(sb.toString())){
			return sb.toString();
		}

		for(int i=root_split.length-1;i>0;i--) {
			StringBuilder sb_short_root = new StringBuilder();
			for(int j=0; j<root_split.length-1; j++){
				sb_short_root.append(root_split[j]);
				sb_short_root.append(File.separator);
			}
			sb_short_root.append(include_file);
			if(exitFile(sb_short_root.toString())){
				return sb_short_root.toString();
			}
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
