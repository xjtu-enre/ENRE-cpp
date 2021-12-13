package demo.entity;

import java.util.ArrayList;
import java.util.List;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;


public class DataAggregateEntity extends Entity{
	
	boolean isScopeTrue = true;
	List<Entity> useList = new ArrayList<Entity>();
	
	public DataAggregateEntity(String name, String qualifiedName, Entity parent, Integer id,BaseScope scope, Location location) {
		super(name,qualifiedName, parent,id, location);
		this.scope = scope;
		if(name.contains("::")) {
			isScopeTrue = false;
		}
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


}
