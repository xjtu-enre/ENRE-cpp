package util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import entity.*;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.json.JSONObject;
import relation.Relation;
import symtab.FunctionSymbol;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.*;


public class JSONString {
	//protected static Configure configure = Configure.getConfigureInstance();

	Configure configure = Configure.getConfigureInstance();
	public String resolveStorage_class(int tag){
		switch(tag) {
			case IASTDeclSpecifier.sc_typedef:
				return "typedef";
			case IASTDeclSpecifier.sc_extern:
				return "extern";
			case IASTDeclSpecifier.sc_static:
				return "static";
			case IASTDeclSpecifier.sc_auto:
				return "auto";
			case IASTDeclSpecifier.sc_register:
				return "register";
			case IASTDeclSpecifier.sc_mutable:
				return "mutable";
		}
		return null;
	}

	public String resolveVisibility(int tag){
		switch(tag) {
			case ICPPMember.v_public:
				return "public";
			case ICPPMember.v_protected:
				return "protected";
			case ICPPMember.v_private:
				return "private";
		}
		return null;
	}

	public String resolveTypeName(Entity entity) {
		String typeName = entity.getClass().toString();
		String resolvedName = "";
		switch(typeName) {
		case "class entity.VarEntity":
			resolvedName = "Variable";
			break;
		case "class entity.FieldEntity":
			resolvedName = "Variable";
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
			resolvedName = "Parameter Variable";
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
		private String category;
		private Integer entityFile;
		private Integer parentID;
		private Integer startLine;
		private Integer startOffset;
		private Integer endLine;
		private Integer endOffset;
		private Integer scale;
		private String storageClass;
		private String visibility;
		private Integer type;
		private String returnType;
		private String typedefType;
		private Integer parameterIndex = null;

		public EntityTemp(String name, Integer key, String category, Integer entityFile){
			this.qualifiedName = name;
			this.id = key;
			this.category = category;
			this.entityFile = entityFile;
		}
		public void setParentID(Integer parentid) { this.parentID = parentid; }
		public void setStartLine(Integer startLine) { this.startLine = startLine; }
		public void setStartOffset(Integer startOffset){ this.startOffset = startOffset; }
		public void setEndLine(Integer endLine) { this.endLine = endLine; }
		public void setEndOffset(Integer endOffset ){ this.endOffset = endOffset; }
		public void setScale(Integer scale){this.scale = scale; }
		public void setStorageClass(String storageClass) { this.storageClass = storageClass; }
		public void setVisibility(String visibility) { this.visibility = visibility; }
		public void setType(Integer typeId){
			this.type = typeId;
		}
		public void setReturnType(String returnType){
			this.returnType = returnType;
		}
		public void setTypedefType(String typedefType){
			this.typedefType = typedefType;
		}
		public void setParameterIndex(int index) {this.parameterIndex = index;}
	}
	
	public EntityTemp resolveEntity(Entity entity) {
		String entityName = entity.getQualifiedName();
		if(entityName == null) entityName = "";
		if(entity instanceof FileEntity) {
			entityName = entityName.replace(configure.getInputSrcPath()+"\\", "");
		}
    	entityName = entityName.replace("\\", "/");
    	entityName = entityName.replace("\"", "");
    	
    	String entityType = this.resolveTypeName(entity);

    	Integer entityFile = null;
    	
    	EntityTemp entitytemp;
		if(entity instanceof NamespaceEntity){
			entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
			entitytemp.setParentID(entity.getParentId());
			entitytemp.setScale(((NamespaceEntity) entity).getScale());
		}else if(entity instanceof FileEntity){
			entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
		} else if(entity.getLocation() != null) {
    		entityFile = entity.getLocation().getFile();
        	
    		if(entity.getLocation().getStartLine() != null) {
				entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
				entitytemp.setStartLine(entity.getLocation().getStartLine());
				entitytemp.setStartOffset(entity.getLocation().getStartOffset());
				entitytemp.setEndLine(entity.getLocation().getEndLine());
				entitytemp.setEndOffset(entity.getLocation().getEndOffset());
				entitytemp.setParentID(entity.getParentId());
				entitytemp.setStorageClass(resolveStorage_class(entity.getStorgae_class()));
				entitytemp.setVisibility(resolveVisibility(entity.getVisiblity()));

    		}
    		else{
				entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
				entitytemp.setParentID(entity.getParentId());
			}
    	}
    	else {
    		entitytemp = new EntityTemp(entityName, entity.getId(), entityType, entityFile);
			entitytemp.setParentID(entity.getParentId());
    	}
		if(entity instanceof VarEntity){
			if(((VarEntity) entity).getTypeID() != -1){
				entitytemp.setType(((VarEntity) entity).getTypeID());
			}
		}
		if(entity instanceof TypedefEntity){
			if(((TypedefEntity) entity).getType() != null){
				entitytemp.setTypedefType(((TypedefEntity) entity).getType());
			}
		}
		if(entity instanceof FunctionEntity){
			if(((FunctionEntity) entity).getReturnType() != null){
				entitytemp.setReturnType(((FunctionEntity) entity).getReturnType());
			}
		}
		if(entity instanceof ParameterEntity){
			entitytemp.setParameterIndex(((ParameterEntity) entity).getIndex());
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
		private String category;
		private Integer from;
		private Integer to;

		class Location{
			private Integer file = null;
			private Integer line = null;
			private Integer offset = null;
		}
		private Location loc;

		public RelationTemp(String type, Integer from, Integer to){
			this.category = type;
			this.from = from;
			this.to = to;
		}

		public void setFile(Integer fileID) {
			if(this.loc == null) this.loc = new Location();
			this.loc.file = fileID;
		}

		public void setLine(Integer line) {
			if(this.loc == null) this.loc = new Location();
			this.loc.line = line;
		}

		public void setOffset(Integer Offset) {
			if(this.loc == null) this.loc = new Location();
			this.loc.offset = Offset;
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
        		String jsonString = "{\"category\":\""+Type+"\", \"src\":"+relation.getFirst()+", \"dest\":\""+ relation.getSecond() +"\"}";
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
										List<Relation> relationList) throws IOException {
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
		}

		List<RelationTemp> relationTempList = new ArrayList<RelationTemp>();
		for (Relation relation: relationList) {
			RelationTemp relationtemp = new RelationTemp(relation.getType(), relation.getFromEntity().getId(),
					relation.getToEntity().getId());
			if(relation.getFileID() != -1){
				relationtemp.setFile(relation.getFileID());
			}
			if(relation.getStartLine() != -1){
				relationtemp.setLine(relation.getStartLine());
			}
			if(relation.getStartOffset() != -1){
				relationtemp.setOffset(relation.getStartOffset());
			}
			relationTempList.add(relationtemp);
		}
		AllTemp allTemp = new AllTemp(entityTempList, relationTempList);
		gson.toJson(allTemp, AllTemp.class, writer);

		writer.endArray();
		writer.close();
	}
}
