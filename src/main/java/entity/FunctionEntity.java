package entity;

import symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class FunctionEntity extends DataAggregateEntity{
	List<ParameterEntity> parameter;
	String nameWithSignature;
	String returnType;
	Entity returnEntity;
	BaseScope pointScope;
	boolean isCallbackCall = false;
	boolean hasBeenDefined = false;
	
	
	public FunctionEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
		parameter =  new ArrayList();
		this.nameWithSignature = null;
	}
	public void addParameter(ParameterEntity var) {
		parameter.add(var);
	}
	public List<ParameterEntity> getParameter(){
		return parameter;
	}

	public boolean equals(String name, List<String> parameterLists){
		this.nameWithSignature = this.getNameWithSignature();
		String nameWithSignatureB = name;
		if(parameterLists.size() != this.parameter.size()) return false;
		for(int i=0;i<parameterLists.size();i++){
			nameWithSignatureB = nameWithSignatureB + "_" +parameterLists.get(i);
		}
		if(nameWithSignatureB.equals(this.nameWithSignature)) return true;
		return false;
	}
	public String getNameWithSignature() {
		if(this.nameWithSignature!=null) return this.nameWithSignature;
		this.nameWithSignature = this.name;
		for(int i=0;i<this.parameter.size();i++){
			this.nameWithSignature = this.nameWithSignature + "_" + this.parameter.get(i).getType().getTypeName();
		}
		return this.nameWithSignature;
	}
	public void setReturn(String returnType) {
		this.returnType = returnType;
	}
	public String getReturnType() {
		return this.returnType;
	}
	public void setReturn(Entity returnType) {
		this.returnEntity = returnType;
	}
	public Entity getReturnEntity() {
		if(this.returnEntity == null) return null;
		return this.returnEntity;
	}
	
	
	public void setCallbackCall() {
		this.isCallbackCall = true;
	}
	public boolean isCallbackCall() {
		return this.isCallbackCall;
	}

	public void setHasBeenDefined(){ this.hasBeenDefined = true; }
	public boolean isHasBeenDefined() { return this.hasBeenDefined; }
}
