package demo.util;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import demo.entity.Entity;
import demo.entity.EntityRepo;


public class JSONString {
	protected static Configure configure = Configure.getConfigureInstance();
	
	public static String JSONWriteRelation(Map<String, List<Tuple<Integer, Integer>>> relationMap, EntityRepo entityrepo) throws Exception {

       
        JSONObject obj=new JSONObject();//创建JSONObject对象
        String projectName = configure.getProjectName();
        for(String type:relationMap.keySet()) {
        	for(Tuple<Integer, Integer> relation:relationMap.get(type)) {
    			JSONObject subObj=new JSONObject();//创建对象数组里的子对象
    			subObj.put("_from", projectName+"/"+relation.first+ "");
    			subObj.put("_to", projectName+"/"+ relation.second +  "");

    			JSONObject reObj=new JSONObject();//创建对象数组里的子对象
    			reObj.put(type, 1);
    			subObj.accumulate("values",reObj);
    			
    			obj.accumulate("cells",subObj);        			
        	}
        }
        
        return obj.toString();
	}
	
	class EntityTemp { 
		   private String name; 
		   private Integer _key; 
		   public EntityTemp(){} 

		   public String getName() { 
		      return name; 
		   } 
		   public void setName(String name) { 
		      this.name = name; 
		   } 
		   public Integer getAge() { 
		      return _key; 
		   } 
		   public void setAge(Integer _key) { 
		      this._key = _key; 
		   } 
		   public String toString() { 
		      return "EntityTemp [ name: "+name+", _key: "+ _key+ " ]"; 
		   }  
		}

	public void writeJsonStream(OutputStream out, Map<Integer, Entity> entityList) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        int k = 0;
        for (Integer en:entityList.keySet()) {
        	if(k>500) {
        		break;
        	}
        	k++;
        	String entityName = entityList.get(en).getQualifiedName();
        	entityName = entityName.replace("\\", "/");
        	entityName = entityName.replace("\"", "");
        	String jsonString = "{\"name\":\""+entityName+"\", \"_key\":"+en+"}"; 
            System.out.println(jsonString);
        	GsonBuilder builder = new GsonBuilder(); 
            builder.setPrettyPrinting(); 
            Gson gson = builder.create(); 
            EntityTemp entitytemp = gson.fromJson(jsonString, EntityTemp.class); 
            gson.toJson(entitytemp, EntityTemp.class, writer);
        }
        writer.endArray();
        writer.close();
    }
	
	public static String JSONWriteEntity(Map<Integer, Entity> entityList) throws Exception {
		
		String jsonString = "[";
        JSONObject obj=new JSONObject();//创建JSONObject对象
        List<JSONObject> subObjVariable=new ArrayList<JSONObject>();//创建对象数组里的子对象
        System.out.println(entityList.keySet().size());
        int k = 0;
		for(Integer en:entityList.keySet()) {
			k++;
			JSONObject subObj=new JSONObject();//创建对象数组里的子对象
			subObj.put("_key",en+"");
			subObj.put("name",entityList.get(en).getQualifiedName());
			subObjVariable.add(subObj);
			if(k>=10000) {
				jsonString += subObjVariable.toString().substring(1, subObjVariable.toString().length()-1) +",";
				k = 0;
				subObjVariable = new ArrayList<JSONObject>();
			}
		}
		if(k!=0) {
			jsonString += subObjVariable.toString().substring(1, subObjVariable.toString().length()-1) +",";
		}

        return "{\"variables\":"+jsonString.substring(0,jsonString.length()-1)+"]}";
	}
	
	
	
//	public static String JSONWriteEntity(Map<Integer, Entity> entityList) throws Exception {
//
//	       
////        JSONObject obj=new JSONObject();//创建JSONObject对象
////        
////        JSONArray jsonArray = new JSONArray();
////        List<JSONObject> subObjVariable=new ArrayList<JSONObject>();//创建对象数组里的子对象
////		for(Integer en:entityList.keySet()) {
////			
////			JSONObject subObj=new JSONObject();//创建对象数组里的子对象
////			subObj.put("_key",en+"");
////			subObj.put("name",entityList.get(en).getQualifiedName());
////			subObjVariable.add(subObj);
////			jsonArray.put(subObj);
////		}
////        obj.put("variables",subObjVariable);
////        return obj.toString();
//        List menu = new ArrayList<>();
//        menu.add(new JSONObject().putOpt("name","宫保鸡丁").putOpt("price","28"));
//        menu.add(new JSONObject().putOpt("name","鱼香肉丝").putOpt("price","30"));
//        menu.add(new JSONObject().putOpt("name","肉夹馍").putOpt("price","6"));
//        menu.add(new JSONObject().putOpt("name","煎饼").putOpt("price","6"));
//        menu.stream().filter(jsonObject -> ((JSONObject) jsonObject).getInt("price")<10);
//        return menu.toString();
//	}
	
}
