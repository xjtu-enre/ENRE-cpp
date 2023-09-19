package cdt;

import entity.Entity;
import entity.EntityRepo;
import relation.RelationRepo;

import java.util.*;


public class CDTParser {

	EntityRepo entityrepo;
	RelationRepo relationrepo;
	HashMap<String,Integer> fileList;
	Set<String> Program_environment;
	
	public CDTParser(Set<String> Program_environment) {
		this.entityrepo = new EntityRepo();
		this.relationrepo = new RelationRepo();
		this.Program_environment = Program_environment;
	}

	public EntityRepo getEntityRepo() {
		return entityrepo;
	}
	public RelationRepo getRelationRepo() {
		return relationrepo;
	}

	
	/**
	* @methodsName: setFileList
	* @description: 
	* 	Gets a collection of file paths
	* 	Get a path containing keywords such as 'lib' or 'src' as the default program environment variable
	* @param:  HashMap<String,Integer> fileList
	* @return: void
	* @throws: 
	*/
	public void setFileList(HashMap<String,Integer> fileList) {
//		Program_environment.add("/Users/dzf/Desktop/harmony/arkui_ace_engine_lite/test/moduletest/common");
		this.fileList = fileList;
		List<String> wordList = new ArrayList<String>();
		wordList.add("lib");
		wordList.add("src");
		wordList.add("include");
		for(String path:fileList.keySet()) {
			for(String word:wordList) {
				if(path.contains(word)) {
					// The regularization method gets the path
					int i = path.indexOf(word);
					String newPath = path.substring(0,i);
					Program_environment.add(newPath + word);
				}
			}
		}
	}
	
	
	/**
	* @methodsName: parseFile
	* @description: 
	* 	This function is used to analyze the contents of a single file
	* 	build fileparser to analyze a single C++ file and generate analysis unit tu
	* @param:  String filefullpath
	* @return: void
	* @throws: 
	*/
	public void parseFile(String filefullpath) throws Exception {
		FileParser cdt = new FileParser(filefullpath,entityrepo, relationrepo, fileList, Program_environment);
		cdt.parse();
	}

}

