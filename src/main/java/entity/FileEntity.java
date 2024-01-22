package entity;

import symtab.BaseScope;
import java.util.*;


public class FileEntity extends DataAggregateEntity {
	String path;
	List<String> includeFile;
	List<FileEntity> includeEntity= new ArrayList<FileEntity>();
	Map<String, String> macrorepo;
	HashSet<String> includeFilePathsArray = new HashSet<>();

	public FileEntity(String name,String qualifiedName, Entity parent, int id,String filefullpath, BaseScope scope) {
		super(name,qualifiedName, parent, id, scope);
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
		includeFilePathsArray.add(entity.getPath());
		includeFilePathsArray.addAll(entity.getIncludeFilePathsArray());
	}
	public List<FileEntity> getIncludeEntity(){
		if(includeEntity == null) return new ArrayList<FileEntity>();
		return includeEntity;
	}
	public HashSet<String> getIncludeFilePathsArray(){
		return this.includeFilePathsArray;
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
