package entity;

import org.eclipse.cdt.core.dom.ast.IASTExpression;

public class Expression{
	String expression;
	String allInfor;
	boolean call = false;
	Entity referredEntity = null;
	IASTExpression astNode = null;
	
	public Expression(String expression,String infor) {
		this.expression = expression;
		this.allInfor = infor;
	}
	public void addReferredEntity(Entity toEntity) {
		this.referredEntity = toEntity;
	} 
	public void setToEntity(Entity entity) {
		this.referredEntity = entity;
	}
	public Entity getToEntity() {
		return referredEntity;
	}
	public void setCall(boolean call) {
		this.call = call;
	}
	public boolean isCall() {
		return call;
	}
	public String getExpression() {
		return expression;
	}
	public String getAllInfor() {
		return allInfor;
	}
	
	public void setNode(IASTExpression node) {
		this.astNode = node;
	}
	
	public IASTExpression getNode() {
		return this.astNode;
	}
}
