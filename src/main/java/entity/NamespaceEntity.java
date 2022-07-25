package entity;

import java.util.HashMap;

import symtab.BaseScope;

public class NamespaceEntity extends DataAggregateEntity{
	
	HashMap<String,Entity> entities	 = new HashMap<>();
	public NamespaceEntity(String rawName, String qualifiedName, Entity parent,Integer id, BaseScope scope) {
		super(rawName,qualifiedName, parent,id, scope, null );
		//setQualifiedName(rawName); 
	}

//	public NamespaceEntity(String rawName,String qualifiedName, Entity currentFile, Integer id) {
//		//super(rawName, qualifiedName, currentFile,id, location);
//	}
}
