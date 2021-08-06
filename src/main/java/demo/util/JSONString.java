package demo.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


import demo.entity.Entity;
import demo.entity.EntityRepo;


public class JSONString {
	protected static Configure configure = Configure.getConfigureInstance();
	
	public static String JSONWriteRelation(Map<Integer, Map<Integer, Map<String, Integer>>> relationMap, EntityRepo entityrepo) throws Exception {

       
        JSONObject obj=new JSONObject();//创建JSONObject对象
        String projectName = configure.getProjectName();
        for(int fromEntity:relationMap.keySet()) {
        	for(int toEntity:relationMap.get(fromEntity).keySet()) {
        		for(String type:relationMap.get(fromEntity).get(toEntity).keySet()) {
        			JSONObject subObj=new JSONObject();//创建对象数组里的子对象
        			subObj.put("_from", projectName+"/"+fromEntity+ "");
        			subObj.put("_to", projectName+"/"+ toEntity +  "");

        			JSONObject reObj=new JSONObject();//创建对象数组里的子对象
        			reObj.put(type, 1);
        			subObj.accumulate("values",reObj);
        			
        			obj.accumulate("cells",subObj);
        		}
        			
        	}
        }
        
        return obj.toString();
	}
	public static String JSONWriteEntity(Map<Integer, Entity> entityList) throws Exception {

	       
        JSONObject obj=new JSONObject();//创建JSONObject对象
        
        JSONArray jsonArray = new JSONArray();
        List<JSONObject> subObjVariable=new ArrayList<JSONObject>();//创建对象数组里的子对象
		for(Integer en:entityList.keySet()) {
			
			JSONObject subObj=new JSONObject();//创建对象数组里的子对象
			subObj.put("_key",en+"");
			subObj.put("name",entityList.get(en).getQualifiedName());
			subObjVariable.add(subObj);
			jsonArray.put(subObj);
		}
        obj.put("variables",subObjVariable);
        return obj.toString();
	}
	
}
