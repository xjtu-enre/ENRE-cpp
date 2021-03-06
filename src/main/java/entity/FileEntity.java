package entity;

import symtab.BaseScope;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FileEntity extends DataAggregateEntity {
	String path;
	List<String> includeFile;
	List<FileEntity> includeEntity= new ArrayList<FileEntity>();
	Map<String, String> macrorepo;
	
	public FileEntity(String name,String qualifiedName, Entity parent, int id,String filefullpath, BaseScope scope, Location location) {
		super(name,qualifiedName, parent, id, scope, location);
		this.path = filefullpath;
		this.includeFile = new ArrayList<String>();
	}
	
	public String getPath() {
		return path;
	}
	public void addinclude(String path) {
		includeFile.add(path);
	}
	public List<String> getInclude(){
		return includeFile;
	}
	public void addincludeEntity(FileEntity entity) {
		includeEntity.add(entity);
	}
	public List<FileEntity> getIncludeEntity(){
		if(includeEntity == null) return new ArrayList<FileEntity>();
		return includeEntity;
	}
	public void addMacroRepo(Map<String, String> macro) {
		this.macrorepo.putAll(macro);
	}
	public void setMacroRepo(Map<String, String> macrorepo) {
		this.macrorepo = macrorepo;
	}
	
	public Map<String, String> getMacroRepo() {
		if(macrorepo == null)  macrorepo = new HashMap<String, String>();
		return macrorepo;
	}

}
