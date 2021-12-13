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
		   private String entity_type; 
		   public EntityTemp(){} 

		   public String getName() { 
		      return name; 
		   } 
		   public void setName(String name) { 
		      this.name = name; 
		   } 
		   public Integer getKey() { 
		      return _key; 
		   } 
		   public void setKey(Integer _key) { 
		      this._key = _key; 
		   } 
		   public String toString() { 
		      return "EntityTemp [ name: "+name+", _key: "+ _key+ " ]"; 
		   }  
		}

	public void writeEntityJsonStream(OutputStream out, Map<Integer, Entity> entityList) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        for (Integer en:entityList.keySet()) {
        	String entityName = entityList.get(en).getQualifiedName();
        	entityName = entityName.replace("\\", "/");
        	entityName = entityName.replace("\"", "");
        	String jsonString = "{\"name\":\""+entityName+"\", \"_key\":"+en+", \"entity_type\":\""+ entityList.get(en).getClass() +"\"}"; 
        	GsonBuilder builder = new GsonBuilder(); 
            builder.setPrettyPrinting(); 
            Gson gson = builder.create(); 
            EntityTemp entitytemp = gson.fromJson(jsonString, EntityTemp.class); 
            gson.toJson(entitytemp, EntityTemp.class, writer);
        }
        writer.endArray();
        writer.close();
    }
	
	class RelationTemp { 
		private String type; 
		private Integer _fromid; 
		private Integer _toid; 
		public RelationTemp(){} 

		public String getType() {
			return type; 
		} 
		public void setType(String type) { 
		    this.type = type; 
		} 
		public String toString() { 
			return "RelationTemp [ type: "+type+", _fromid: "+ _fromid+", _toid: "+ _toid+ " ]"; 
		}  
	}
	public void writeRelationJsonStream(OutputStream out, 
			Map<String, List<Tuple<Integer, Integer>>> relationList) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        for (String Type: relationList.keySet()) {
        	List<Tuple<Integer, Integer>> relationListByType = relationList.get(Type);
        	for(Tuple<Integer, Integer> relation:relationListByType) {
        		String jsonString = "{\"type\":\""+Type+"\", \"_fromid\":"+relation.first+", \"_toid\":"+relation.second+"}"; 
            	GsonBuilder builder = new GsonBuilder(); 
                builder.setPrettyPrinting(); 
                Gson gson = builder.create(); 
                RelationTemp relationtemp = gson.fromJson(jsonString, RelationTemp.class); 
                gson.toJson(relationtemp, RelationTemp.class, writer);
        	}	
        }
        writer.endArray();
        writer.close();
	}
	
}
