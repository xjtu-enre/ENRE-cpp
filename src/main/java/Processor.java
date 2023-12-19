import cdt.CDTParser;
import cdt.TypeBinding;
import entity.EntityRepo;
import relation.RelationContext;
import relation.RelationRepo;
import symtab.Type;
import util.FileTraversal;
import util.FileUtil;
import util.JSONString;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Set;


public class Processor {
	static CDTParser cdtparser;
	final static String[] SUFFIX = new String[] { ".cpp", ".cc", ".c", ".c++", ".h", ".hpp", ".hh", ".cxx", ".hxx" };
	static HashMap<String,Integer> fileList;

	public Processor(Set<String> Program_environment) {
		Processor.cdtparser = new CDTParser(Program_environment);
	}
	TypeBinding typeBinding;
	RelationContext relationcontext ;
	

	/**
	 * 解析指定目录下所有文件
	 *
	 * @param inputSrcPath 指定目录路径
	 * @throws Exception 解析文件过程中可能抛出的异常
	 */
	public static void parseAllFile(String inputSrcPath) throws Exception {
		FileTraversal fileTrasversal = new FileTraversal(new FileTraversal.IFileVisitor() {
			@Override
			public void visit(File file) throws Exception {
				String fileFullPath = file.getAbsolutePath();
				fileFullPath = FileUtil.uniqFilePath(fileFullPath);
				parseFile(fileFullPath);
			}
		});
		fileTrasversal.getFileList(inputSrcPath);
		fileList = fileTrasversal.getfile();
		fileTrasversal.extensionFilter(SUFFIX);
		fileTrasversal.travers(inputSrcPath);
	}
	
	/**
	 * 解析单个文件
	 *
	 * @param inputSrcPath 输入源路径
	 * @throws Exception 解析过程中可能抛出的异常
	 */
	public static void parseFile(String inputSrcPath) throws Exception {
		cdtparser.setFileList(fileList);
		cdtparser.parseFile(inputSrcPath);
	}
	
	
	/**
	 * 依赖构建函数
	 *
	 * @throws Exception 抛出异常
	 */
	public void dependencyBuild() throws Exception {
		EntityRepo entityrepo = cdtparser.getEntityRepo();
		RelationRepo relationRepo = cdtparser.getRelationRepo();
		this.relationcontext = new RelationContext(entityrepo, relationRepo);
		this.relationcontext.relationListDeal();
		this.relationcontext.AggregateDeal();
		this.relationcontext.ClassDeal();
		this.relationcontext.FunctionDeal();
		this.relationcontext.NamespaceAliasDeal();
		this.relationcontext.relationListDealAfter();
	}

	/**
	 * 类型绑定
	 *
	 * @throws Exception 类型绑定过程中可能抛出的异常
	 */
	public void typeBinding() throws Exception{
		EntityRepo entityrepo = cdtparser.getEntityRepo();
		this.typeBinding = new TypeBinding(entityrepo);
		this.typeBinding.typeBindingDeal();
	}

	/**
	 * 输出文件
	 *
	 * @param projectName 项目名称
	 * @throws Exception 异常
	 */
	public void outputFile(String projectName) throws Exception {
		EntityRepo entityrepo = cdtparser.getEntityRepo();
		JSONString node_str = new JSONString();
		FileOutputStream outputStream = new FileOutputStream(projectName + "_out.json");
		node_str.writeJsonStream(outputStream, entityrepo.getEntities(), this.relationcontext.getRelationRepo().getrelationrepo());
	}

}