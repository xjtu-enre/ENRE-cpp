package cdt;

import util.FileTraversal;
import util.FileTraversal.IFileVisitor;
import util.FileUtil;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.internal.core.parser.scanner.ScannerUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PreprocessorHandler {
	private List<String> includePaths;
	private String inputSrcPath;
	private HashSet<String> allFiles = new HashSet<>();
	public PreprocessorHandler(String inputSrcPath, List<String> includePaths) throws Exception{
		this.inputSrcPath = inputSrcPath;
		this.includePaths = includePaths;
		buildAllFiles();
	}
	public List<String> getIncludePaths() {
		return includePaths;
	}
	
	class AllFileVisitor implements IFileVisitor{
		@Override
		public void visit(File file) {
			try {
				allFiles.add(file.getCanonicalPath());
			} catch (IOException e) {
			}
		}
	}
	
	/**
	* @methodsName: buildAllFiles
	* @description: Build file traversers for the whole project
	* @param:  null
	* @return: void
	* @throws: 
	*/
	private void buildAllFiles() throws Exception {
		allFiles = new HashSet<>();
		AllFileVisitor v = new AllFileVisitor();
		if (inputSrcPath!=null) {
			FileTraversal ft = new FileTraversal(v,false,true);
			ft.travers(inputSrcPath);
		}
		for (String includePath:includePaths) {
			FileTraversal ft = new FileTraversal(v,false,true);
			ft.travers(includePath);
		}
	}
	
	/**
	* @methodsName: existFile
	* @description: Check whether the file exists based on the path
	* @param:  String checkPath
	* @return: boolean
	* @throws: 
	*/
	private boolean existFile(String checkPath) {
		checkPath = FileUtil.uniformPath(checkPath);
		return allFiles.contains(checkPath);
	}
	
	/**
	* @methodsName: getDirectIncludedFiles
	* @description: Get the include file of the file according to the preprocessing information
	* @param:  IASTPreprocessorStatement[] statements, String fileLocation
	* @return: List<String>
	* @throws: 
	*/
	public List<String> getDirectIncludedFiles(IASTPreprocessorStatement[] statements, String fileLocation) {
		ArrayList<String> includedFullPathNames = new ArrayList<>();
		for (int statementIndex=0;statementIndex<statements.length;statementIndex++) {
			if (statements[statementIndex] instanceof IASTPreprocessorIncludeStatement)
			{
				IASTPreprocessorIncludeStatement incl = (IASTPreprocessorIncludeStatement)(statements[statementIndex]);
				if (!incl.getFileLocation().getFileName().equals(fileLocation))
					continue;
				String path = resolvePath(incl);
				if (!existFile(path)) {
					continue;
				}
				if (FileUtil.isDirectory(path)) {
					continue;
				}
				includedFullPathNames.add(path);
			}
		}
		return includedFullPathNames;
	}
	
	/**
	* @methodsName: resolvePath
	* @description: Process file paths according to Include Statement(IASTPreprocessorIncludeStatement)
	* @param:  IASTPreprocessorIncludeStatement incl
	* @return: String
	* @throws: 
	*/
	private String resolvePath(IASTPreprocessorIncludeStatement incl) {
		String path = incl.toString();
		int pos = path.indexOf(' ');
		path = path.substring(pos+1).trim();
		if (path.startsWith("\"") || path.startsWith("<")){
			path = path.substring(1);
			path = path.substring(0,path.length()-1);
		}
		//First search in local directory
		IASTFileLocation location = incl.getFileLocation();
		String locationDir = FileUtil.getLocatedDir(location.getFileName());
		ArrayList<String> searchPath = new ArrayList<>();
		searchPath.add(locationDir);
		searchPath.addAll(includePaths);
		for (String includePath:searchPath) {
			String checkPath = ScannerUtility.createReconciledPath(includePath,path);
			if (existFile(checkPath)) {
				return FileUtil.uniqFilePath(checkPath);
			}
		}
		return "";
	}
	


}
