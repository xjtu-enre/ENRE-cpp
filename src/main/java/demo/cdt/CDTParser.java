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
	public void setFileList(HashMap<String,Integer> fileList) {
		this.fileList = fileList;
		List<String> wordList = new ArrayList<String>();
		wordList.add("lib");
		wordList.add("src");
		for(String path:fileList.keySet()) {
			for(String word:wordList) {
				if(path.contains(word)) {
					int i = path.indexOf(word);//首先获取字符的位置
					String newPath = path.substring(0,i);//再对字符串进行截取，获得想要得到的字符串
					include_path.add(newPath + word);
				}
			}
		}
	}
	
	public void parseFile(String filefullpath) throws Exception {
		/*
		 * 该函数用于针对单个文件进行分析，获取得到内容
		 * 构建fileparser用于对单个C++文件进行分析，生成分析单元tu
		 */
//		
//		include_path.add("C:\\Users\\ding7\\Downloads\\phantomjs-master\\phantomjs-master\\src");

		FileParser cdt = new FileParser(filefullpath,entityrepo,fileList,include_path);
		cdt.parse();
		
	}
	
	public void testprintentityrepo() {
		
		Iterator<Entity> print = entityrepo.entityIterator();
		while(print.hasNext()) {
			System.out.println(print.next().toString());
		}
	}
	public EntityRepo getEntityRepo() {
		return entityrepo;
	}


}

