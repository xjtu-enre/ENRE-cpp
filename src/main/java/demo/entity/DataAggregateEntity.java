package demo.entity;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;


public class DataAggregateEntity extends Entity{
	
	boolean isScopeTrue = true;
	
	public DataAggregateEntity(String name, String qualifiedName, Entity parent, Integer id,BaseScope scope) {
		super(name,qualifiedName, parent,id);
		this.scope = scope;
		if(name.contains("::")) {
			isScopeTrue = false;
		}
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
