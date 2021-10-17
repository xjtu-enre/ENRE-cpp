package demo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import demo.cdt.CDTParser;
import demo.entity.ClassEntity;
import demo.entity.Entity;
import demo.entity.EntityRepo;
import demo.entity.EnumEntity;
import demo.entity.EnumeratorEntity;
import demo.entity.FunctionEntity;
import demo.entity.FunctionEntityDefine;
import demo.entity.MacroEntity;
import demo.entity.NamespaceEntity;
import demo.entity.StructEntity;
import demo.entity.UnionEntity;
import demo.relation.RelationContext;
import demo.util.Configure;
import demo.util.FileTraversal;
import demo.util.FileUtil;
import demo.util.JSONString;


public class Processor {
	static CDTParser cdtparser;
	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	final static String[] SUFFIX = new String[] { ".cpp", ".cc", ".c", ".c++", ".h", ".hpp", ".hh", ".cxx", ".hxx" };
	static HashMap<String,Integer> fileList;
	protected static Configure configure = Configure.getConfigureInstance();
	public Processor() {
		Processor.cdtparser = new CDTParser();
	}
	public static void printentity() {
		cdtparser.testprintentityrepo();
	}

	public static void parseAllFlie(String inputSrcPath) throws Exception {
		FileTraversal fileTrasversal = new FileTraversal(new FileTraversal.IFileVisitor() {
			@Override
			public void visit(File file) throws Exception {
				String fileFullPath = file.getAbsolutePath();
				fileFullPath = FileUtil.uniqFilePath(fileFullPath);
				LOGGER.info("parse"+fileFullPath);
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
	public static void parseFile(String inputSrcPath) throws Exception {
		cdtparser.setFileList(fileList);
		cdtparser.parseFile(inputSrcPath);
	}
	
	public static void dependencyBuild() throws Exception {
		EntityRepo entityrepo = cdtparser.getEntityRepo();
		JSONString node_str = new JSONString();
		FileOutputStream outputStream = new FileOutputStream(configure.getAnalyzedProjectName() + "_node.json");
		node_str.writeJsonStream(outputStream, entityrepo.getEntities());

		//JsonWriter node_writer = new JsonWriter();
		//node_writer.toJson(node_str.JSONWriteEntity(entityrepo.getEntities()),
		//		configure.getAnalyzedProjectName() + "_node.json");
		
		LOGGER.info("start dependency");
		
		//entityrepo.printAllEntities();
		RelationContext relationcontext = new RelationContext(entityrepo);
		
		relationcontext.AggregateDeal();
		relationcontext.FileDeal();
		relationcontext.ClassDeal();
		relationcontext.FunctionDeal();
		relationcontext.stastics();
		JSONString edge_str = new JSONString();
		//JsonWriter edge_writer = new JsonWriter();
		//edge_writer.toJson(edge_str.JSONWriteRelation(relationcontext.getRelationRepo().getrelationrepo(),entityrepo),configure.getAnalyzedProjectName() + "_edge.json");
		
		
	}

}