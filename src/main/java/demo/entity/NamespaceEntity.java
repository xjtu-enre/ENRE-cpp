package demo.entity;

import java.util.HashMap;

public class NamespaceEntity extends Entity{
	
	HashMap<String,Entity> entities	 = new HashMap<>();
	public NamespaceEntity(String rawName,String qualifiedName, Integer id, Location location) {
		super(rawName, qualifiedName, null,id, location);
		setQualifiedName(rawName); 
	}

	public NamespaceEntity(String rawName,String qualifiedName, Entity currentFile, Integer id, Location location) {
		super(rawName, qualifiedName, currentFile,id, location);
	}
}
