import cdt.CDTParser;
import entity.EntityRepo;
import relation.RelationContext;
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
	public static void printentity() {
		cdtparser.testprintentityrepo();
	}


	RelationContext relationcontext ;
	
	/**
	* @methodsName: parseAllFlie
	* @description: Enter the project root path, start the project analysis
	* @param:  String inputSrcPath
	* @return: void
	* @throws: 
	*/
	public static void parseAllFile(String inputSrcPath) throws Exception {
		FileTraversal fileTrasversal = new FileTraversal(new FileTraversal.IFileVisitor() {
			@Override
			public void visit(File file) throws Exception {
				String fileFullPath = file.getAbsolutePath();
				fileFullPath = FileUtil.uniqFilePath(fileFullPath);
				System.out.println("parse "+fileFullPath);
				if (!fileFullPath.startsWith(inputSrcPath)) {
					return;
				}			
				parseFile(fileFullPath);
			}

		});
		fileTrasversal.getFileList(inputSrcPath);
		fileList = fileTrasversal.getfile();
		fileTrasversal.extensionFilter(SUFFIX);
		fileTrasversal.travers(inputSrcPath);
		
	}
	
	/**
	* @methodsName: parseFile
	* @description: Analyze individual files
	* @param:  String inputSrcPath
	* @return: void
	* @throws: 
	*/
	public static void parseFile(String inputSrcPath) throws Exception {
		cdtparser.setFileList(fileList);
		cdtparser.parseFile(inputSrcPath);
	}
	
	
	/**
	* @methodsName: dependencyBuild
	* @description: Dependency analysis function
	* @param:  null
	* @return: void
	* @throws: 
	*/
	public void dependencyBuild() throws Exception {
		EntityRepo entityrepo = cdtparser.getEntityRepo();
		this.relationcontext = new RelationContext(entityrepo);
		this.relationcontext.relationListDeal();
		this.relationcontext.FileDeal();
		this.relationcontext.ClassDeal();
		this.relationcontext.FunctionDeal();
	}

	public void outputFile(String projectName) throws Exception {
		EntityRepo entityrepo = cdtparser.getEntityRepo();
		JSONString node_str = new JSONString();
		FileOutputStream outputEntityStream = new FileOutputStream(projectName + "_node.json");
		node_str.writeEntityJsonStream(outputEntityStream, entityrepo.getEntities());

		FileOutputStream outputRelationStream = new FileOutputStream(projectName + "_edge.json");
		node_str.writeRelationJsonStream(outputRelationStream, this.relationcontext.getRelationRepo().getrelationrepo());

		FileOutputStream outputStream = new FileOutputStream(projectName + "_out.json");
		node_str.writeJsonStream(outputStream, entityrepo.getEntities(), this.relationcontext.getRelationRepo().getrelationrepo());

	}

}