package demo.cdt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import demo.entity.Entity;
import demo.entity.EntityRepo;


public class CDTParser {

	EntityRepo entityrepo;
	HashMap<String,Integer> fileList;
	Set<String> include_path = new HashSet<String>();
	
	public CDTParser() {
		this.entityrepo = new EntityRepo();
	}
	
	
	public EntityRepo getEntityRepo() {
		return entityrepo;
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
		this.fileList = fileList;
		List<String> wordList = new ArrayList<String>();
		wordList.add("lib");
		wordList.add("src");
		for(String path:fileList.keySet()) {
			for(String word:wordList) {
				if(path.contains(word)) {
					// The regularization method gets the path
					int i = path.indexOf(word);
					String newPath = path.substring(0,i);
					include_path.add(newPath + word);
				}
			}
			//include_path.add("D:\\gitrepo\\cpp\\leveldb\\include");
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
		FileParser cdt = new FileParser(filefullpath,entityrepo,fileList,include_path);
		cdt.parse();
		
	}
	
	public void testprintentityrepo() {
		Iterator<Entity> print = entityrepo.entityIterator();
		while(print.hasNext()) {
			System.out.println(print.next().toString());
		}
	}

}

