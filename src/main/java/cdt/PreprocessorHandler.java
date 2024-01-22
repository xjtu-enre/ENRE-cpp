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
	 * 构建所有文件集合
	 *
	 * @throws Exception 如果构建过程中出现异常，则抛出该异常
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
	 * 判断指定路径的文件是否存在
	 *
	 * @param checkPath 要检查的文件路径
	 * @return 如果文件存在返回true，否则返回false
	 */
	private boolean existFile(String checkPath) {
		checkPath = FileUtil.uniformPath(checkPath);
		return allFiles.contains(checkPath);
	}

	/**
	 * 获取直接包含的文件列表
	 *
	 * @param statements 预处理语句数组
	 * @param fileLocation 文件位置
	 * @return 包含文件的完整路径列表
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
	 * 根据Include Statement(IASTPreprocessorIncludeStatement)处理文件路径
	 *
	 * @param incl 包含文件路径的Include Statement对象
	 * @return 解析后的文件路径
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
