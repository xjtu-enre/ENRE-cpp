package entity;

import symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class FunctionEntity extends DataAggregateEntity{
	List<ParameterEntity> parameter;
	String nameWithSignature;
	String returnType;
	Entity returnEntity;
	boolean isCallbackCall = false;
	boolean hasBeenDefined = false;
	boolean isPureVirtual = false;
	boolean isTaskNode = false;
	
	
	public FunctionEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
		parameter =  new ArrayList();
		this.nameWithSignature = null;
		if(name.startsWith("s_") && name.endsWith("Main")){
			this.isTaskNode = true;
		}
	}
	public void clearParamter(){
		parameter = new ArrayList();
	}
	public void addParameter(ParameterEntity var) {
		parameter.add(var);
	}
	public List<ParameterEntity> getParameter(){
		return parameter;
	}

	public boolean equals(String name, List<String> parameterLists){
		if(parameterLists.size() == 0 && this.parameter.size() == 0) return true;
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
			this.nameWithSignature = this.nameWithSignature + "_" + this.parameter.get(i).getType();
		}
		return this.nameWithSignature;
	}
	public void setReturn(String returnType) {
		this.returnType = returnType;
	}
	public void setReturn(Entity returnType) {
		this.returnEntity = returnType;
	}
	public Entity getReturnEntity() {
		if(this.returnEntity == null) return null;
		return this.returnEntity;
	}
	public String getReturnType(){
		return this.returnType;
	}
	
	
	public void setCallbackCall() {
		this.isCallbackCall = true;
	}
	public boolean isCallbackCall() {
		return this.isCallbackCall;
	}

	public void setHasBeenDefined(){ this.hasBeenDefined = true; }
	public boolean isHasBeenDefined() { return this.hasBeenDefined; }

	public void setPureVirtual() { this.isPureVirtual = true; }
	public boolean isPureVirtual() { return this.isPureVirtual; }

	public boolean isTaskNode() {return this.isTaskNode; }
}
