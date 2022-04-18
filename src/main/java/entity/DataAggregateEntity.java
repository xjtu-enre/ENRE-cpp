package entity;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;

import java.util.ArrayList;
import java.util.List;


public class DataAggregateEntity extends Entity{
	
	boolean isScopeTrue = true;
	boolean isTemplate = false;
	boolean isSpecializationTemplate = false;
	List<Entity> useList = new ArrayList<Entity>();
	List<String> usingImport = new ArrayList<String>();
	
	public DataAggregateEntity(String name, String qualifiedName, Entity parent, Integer id,BaseScope scope, Location location) {
		super(name,qualifiedName, parent,id, location);
		this.scope = scope;
		if(name.contains("::")) {
			isScopeTrue = false;
		}
		this.usingImport = new ArrayList<String>();
	}
	
	/*
	 * set and gets the list of variables to use
	 */
	public void setUse(Entity entity) {
		this.useList.add(entity);
	}
	
	public List<Entity> getUse() {
		return this.useList;
	}
	
	
	/*
	 * Determines whether this entity is a template
	 */
	public void setTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}
	
	public boolean getIsTemplate() {
		return this.isTemplate;
	}
	
	/*
	 * Determines whether this template is a Specialization
	 */
	public void setSpecializationTemplate(boolean isSpecializationTemplate) {
		if(this.isTemplate) {
			this.isSpecializationTemplate = isSpecializationTemplate;
		}
	}
	
	public boolean getIsSpecializationTemplate() {
		return this.isTemplate & this.isSpecializationTemplate;
	}
	
	
	public Scope foundTrueScope(Scope scope, String name) {
		if(isScopeTrue) return scope;
		String scopeName = name.split("::")[0];
		String methodName = name.split("::")[1];
//		if(scope instanceof GloScopeWithSymtab) {
//			GloScopeWithSymtab global = (GloScopeWithSymtab)scope;
//			if(global.getSymtab().contains(methodName)) return global;
//			for(Scope includeScope:global.getincludeScopeList()) {
//				Scope includeResult = foundTrueScope(includeScope, scopeName);
//				if(includeResult!=null && ((GloScopeWithSymtab)includeResult).getSymtab().contains(methodName)) {
//					return includeResult;
//				}
//			}
//		}
//		if(scope instanceof LocScopeWithSymtab) {
//			LocScopeWithSymtab local = (LocScopeWithSymtab)scope;
//			if(local.getSymtab().contains(methodName)) return local;
//			return foundTrueScope(local.getEnclosingScope(),name);
//		}
		return null;
	}

	public void setUsing(String using) {
		if(using != null) this.usingImport.add(using);
	}
	public List<String> getUsingImport(){
		return this.usingImport;
	}

}
