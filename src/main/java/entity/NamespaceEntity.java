package entity;

import java.util.HashMap;

import symtab.BaseScope;

public class NamespaceEntity extends DataAggregateEntity{
	
	HashMap<String,Entity> entities	 = new HashMap<>();
	Integer scale = 0;
	public NamespaceEntity(String rawName, String qualifiedName, Entity parent,Integer id, BaseScope scope) {
		super(rawName,qualifiedName, parent,id, scope, null );
		//setQualifiedName(rawName); 
	}

	public void addScale(Integer temp_scale){
		this.scale = this.scale + temp_scale;
	}

	public Integer getScale(){
		return this.scale;
	}

//	public NamespaceEntity(String rawName,String qualifiedName, Entity currentFile, Integer id) {
//		//super(rawName, qualifiedName, currentFile,id, location);
//	}
}
