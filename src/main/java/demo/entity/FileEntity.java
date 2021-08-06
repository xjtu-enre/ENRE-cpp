package demo.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;

import macro.MacroRepo;



public class FileEntity extends DataAggregateEntity {
	String path;
	List<String> includeFile;
	List<FileEntity> includeEntity= new ArrayList<FileEntity>();
	List<String> usingImport = new ArrayList<String>();
	Map<String, String> macrorepo;
	
	public FileEntity(String name,String qualifiedName, Entity parent, int id,String filefullpath, BaseScope scope) {
		super(name,qualifiedName, parent, id,scope);
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
	public void setUsing(String using) {
		if(using != null) this.usingImport.add(using);
	}
}
