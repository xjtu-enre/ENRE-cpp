package util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import entity.Entity;
import entity.EntityRepo;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class JSONString {
	protected static Configure configure = Configure.getConfigureInstance();
	
	class EntityTemp {
		private String qualifiedName;
		private Integer id;
		private String entityType;
		public EntityTemp(){}
		public EntityTemp(String name, Integer key, String type){
			this.qualifiedName = name;
			this.id = key;
			this.entityType = type;
		}
	}
	
	public String resolveTypeName(String name) {
		String resolvedName = "";
		switch(name) {
		case "class entity.VarEntity":
			resolvedName = "Var";
			break;
		case "class entity.FunctionEntityDefine":
			resolvedName = "FunctionDefine";
			break;
		case "class entity.ClassEntity":
			resolvedName = "Class";
			break;
		case "class entity.FileEntity":
			resolvedName = "File";
			break;
		case "class entity.StructEntity":
			resolvedName = "Struct";
			break;
		case "class entity.EnumEntity":
			resolvedName = "Enum";
			break;
		case "class entity.EnumeratorEntity":
			resolvedName = "Enumerator";
			break;
		case "class entity.MacroEntity":
			resolvedName = "Macro";
			break;
		case "class entity.FunctionEntityDecl":
			resolvedName = "FunctionDecl";
			break;
		case "class entity.NamespaceEntity":
			resolvedName = "Namespace";
			break;
		case "class entity.AliasEntity":
			resolvedName = "Alias";
			break;
		case "class entity.UnionEntity":
			resolvedName = "Union";
			break;
		case "class entity.FunctionTemplateEntity":
			resolvedName = "Function Template";
			break;
		case "class entity.ClassTemplateEntity":
			resolvedName = "Class Template";
			break;
		default:
			System.out.println("Unmapped entity type:"+name);
		}
		return resolvedName;
	}
	
	
	public void writeEntityJsonStream(OutputStream out, Map<Integer, Entity> entityList) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        for (Integer en:entityList.keySet()) {
        	Entity entity = entityList.get(en);
        	String entityName = entity.getQualifiedName();
        	entityName = entityName.replace("\\", "/");
        	entityName = entityName.replace("\"", "");
        	
        	String jsonString = "{\"qualifiedName\":\""+entityName+"\", \"id\":"+en+", \"entityType\":\""+ resolveTypeName(entityList.get(en).getClass().toString()) +"\"}";
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
		private Integer src;
		private Integer dest;
		public RelationTemp(String type, Integer from, Integer to){
			this.type = type;
			this.src = from;
			this.dest = to;
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
        		String jsonString = "{\"type\":\""+Type+"\", \"src\":"+relation.getFirst()+", \"dest\":\""+ relation.getSecond() +"\"}";
				//"{\"type\":\""+Type+"\", \"src\"\":"+relation.first+"\", \"dest\"\":"+relation.second+"}";
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

	class AllTemp {
		private List<EntityTemp> variables;
		private List<RelationTemp> relations;
		public AllTemp(){}
		public AllTemp(List<EntityTemp> variables, List<RelationTemp> relations){
			this.variables = variables;
			this.relations = relations;
		}
	}


	public void writeJsonStream(OutputStream out, Map<Integer, Entity> entityList,
										Map<String, List<Tuple<Integer, Integer>>> relationList) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		writer.beginArray();

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		List<EntityTemp> entityTempList = new ArrayList<EntityTemp>();
		for (Integer en:entityList.keySet()) {
			String entityName = entityList.get(en).getQualifiedName();
			entityName = entityName.replace("\\", "/");
			entityName = entityName.replace("\"", "");
			builder.setPrettyPrinting();
			EntityTemp entitytemp = new EntityTemp(entityName, en, entityList.get(en).getClass().toString());
			entityTempList.add(entitytemp);

		}


		List<RelationTemp> relationTempList = new ArrayList<RelationTemp>();
		for (String Type: relationList.keySet()) {
			List<Tuple<Integer, Integer>> relationListByType = relationList.get(Type);
			for(Tuple<Integer, Integer> relation:relationListByType) {
				builder.setPrettyPrinting();
				RelationTemp relationtemp = new RelationTemp(Type, relation.first, relation.second);
				relationTempList.add(relationtemp);
			}
		}
		AllTemp allTemp = new AllTemp(entityTempList, relationTempList);
		gson.toJson(allTemp, AllTemp.class, writer);

		writer.endArray();
		writer.close();
	}
}
