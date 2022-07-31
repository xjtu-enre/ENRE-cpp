package util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import entity.*;

import org.json.JSONObject;
import symtab.FunctionSymbol;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.*;


public class JSONString {
	//protected static Configure configure = Configure.getConfigureInstance();

	Configure configure = Configure.getConfigureInstance();
	public String resolveTypeName(Entity entity) {
		String typeName = entity.getClass().toString();
		String resolvedName = "";
		switch(typeName) {
		case "class entity.VarEntity":
			resolvedName = "Object";
			break;
		case "class entity.FunctionEntityDefine":
			resolvedName = "Function";
			break;
		case "class entity.FunctionEntity":
			resolvedName = "Function";
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
		case "class entity.NamespaceEntity":
			resolvedName = "Namespace";
			break;
		case "class entity.AliasEntity":
			resolvedName = "Alias";
			break;
		case "class entity.UnionEntity":
			resolvedName = "Union";
			break;
		case "class entity.TypedefEntity":
			resolvedName = "Typedef";
			break;
		case "class entity.NamespaceAliasEntity":
			resolvedName = "Namespace Alias";
			break;
		case "class entity.ParameterEntity":
			resolvedName = "Parameter";
			break;
		default:
			System.out.println("Unmapped entity type:"+ typeName);
		}
		
    	if(entity instanceof DataAggregateEntity) {
    		if(((DataAggregateEntity) entity).getIsTemplate()) {
    			resolvedName = resolvedName + " Template";
//    			if(((DataAggregateEntity) entity).getIsSpecializationTemplate()) {
//    				resolvedName = resolvedName + " Specialization";
//    			}
    		}
    	}
    	
		return resolvedName;
	}
	
	
	class EntityTemp {
		private String qualifiedName;
		private Integer id;
		private String entityType;
		private String entityFile;
		private int startLine;
		private int startColumn;
		private int endLine;
		private int endColumn;
		public EntityTemp(){}
		public EntityTemp(String name, Integer key, String type, String entityFile){
			this.qualifiedName = name;
			this.id = key;
			this.entityType = type;
			this.entityFile = entityFile;
			this.startLine = -1;
			this.startColumn = -1;
			this.endLine = -1;
			this.endColumn = -1;
		}
		public EntityTemp(String name, Integer key, String type, String entityFile, 
				int startLine, int startColumn, int endLine, int endColumn){
			this.qualifiedName = name;
			this.id = key;
			this.entityType = type;
			this.entityFile = entityFile;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
		}
	}
	
	public EntityTemp resolveEntity(Entity entity) {
		String entityName = entity.getQualifiedName();
		if(entity instanceof FileEntity) {
			entityName = entityName.replace(configure.getInputSrcPath()+"\\", "");
		}
    	entityName = entityName.replace("\\", "/");
    	entityName = entityName.replace("\"", "");
    	
    	String entityType = this.resolveTypeName(entity);

    	String entityFile = "null";
    	
    	EntityTemp entitytemp;
    	if(entity.getLocation() != null) {
    		entityFile = entity.getLocation().getFileName();
    		entityFile = entityFile.replace(configure.getInputSrcPath()+"\\", "");
    		entityFile = entityFile.replace("\\", "/");
        	
    		if(entity.getLocation().getStartLine() != null) {
    			entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile,
    					entity.getLocation().getStartLine(), entity.getLocation().getStartColumn(), 
    					entity.getLocation().getEndLine(), entity.getLocation().getEndColumn());
    		}
    		else entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
    	}
    	else {
    		entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
    	}
    	return entitytemp;
	}
	
	public void writeEntityJsonStream(OutputStream out, Map<Integer, Entity> entityList) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        for (Integer en:entityList.keySet()) {
        	Entity entity = entityList.get(en);
        	GsonBuilder builder = new GsonBuilder(); 
        	builder.setPrettyPrinting();
            Gson gson = builder.create(); 
            gson.toJson(this.resolveEntity(entity), EntityTemp.class, writer);
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
		Map<String, List<Integer>> overload = new HashMap<String, List<Integer>>();

		for (Integer en:entityList.keySet()) {
        	Entity entity = entityList.get(en);
			builder.setPrettyPrinting();
			entityTempList.add(this.resolveEntity(entity));
			if(entity instanceof FunctionEntity){
				if(entity.getScope() instanceof FunctionSymbol){
					FunctionSymbol symbol = (FunctionSymbol)(entity.getScope());
					if(symbol.isOverload()){
						if(overload.get(entity.getQualifiedName())!=null){
							overload.get(entity.getQualifiedName()).add(entity.getId());
						}else{
							List<Integer> list = new ArrayList<Integer>();
							list.add(entity.getId());
							overload.put(entity.getQualifiedName(), list);
						}
					}
				}
			}
		}
		System.out.println("Overload Function Lists:");
		for(List<Integer> list: overload.values()){
			System.out.print("{");
			System.out.print(Arrays.toString(list.toArray()));
			System.out.print("}, ");
		}
		System.out.println();

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
