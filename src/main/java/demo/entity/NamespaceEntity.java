package demo.entity;

import java.util.HashMap;

public class NamespaceEntity extends Entity{
	
	HashMap<String,Entity> entities	 = new HashMap<>();
	public NamespaceEntity(String rawName,String qualifiedName, Integer id) {
		super(rawName, qualifiedName, null,id);
		setQualifiedName(rawName); 
	}

	public NamespaceEntity(String rawName,String qualifiedName, Entity currentFile, Integer id) {
		super(rawName, qualifiedName, currentFile,id);
	}
}
