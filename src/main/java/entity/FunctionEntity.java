package entity;

import org.antlr.symtab.BaseScope;

import java.util.ArrayList;
import java.util.List;


public class FunctionEntity extends DataAggregateEntity{
	List<Entity> parameter;
	String returnType;
	Entity returnEntity;
	BaseScope pointScope;
	boolean isTemplate = false;
	boolean isCallbackCall = false;
	
	
	public FunctionEntity(String name, String qualifiedName, Entity parent, Integer id, BaseScope scope, Location location) {
		super(name, qualifiedName, parent, id, scope, location);
		parameter =  new ArrayList();

	}
	public void addParameter(Entity var) {
		parameter.add(var);
	}
	public List<Entity> getParameter(){
		return parameter;
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
	
	public void setTemplate() {
		this.isTemplate = true;
	}
	public boolean isTemplate() {
		return this.isTemplate;
	}
	
	
	public void setCallbackCall(boolean flag) {
		this.isCallbackCall = flag;
	}
	public boolean isCallbackCall() {
		return this.isCallbackCall;
	}
}
