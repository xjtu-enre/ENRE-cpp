package demo.cdt;

import java.util.List;
import java.util.Stack;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.ClassSymbol;
import org.antlr.symtab.DataAggregateSymbol;
import org.antlr.symtab.GlobalScope;
import org.antlr.symtab.LocalScope;
import org.antlr.symtab.MethodSymbol;
import org.antlr.symtab.Scope;
import org.antlr.symtab.StructSymbol;
import org.antlr.symtab.VariableSymbol;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeleteExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionList;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumerator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPField;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;

import demo.entity.AliasEntity;
import demo.entity.ClassEntity;
import demo.entity.Entity;
import demo.entity.EntityRepo;
import demo.entity.EnumEntity;
import demo.entity.EnumeratorEntity;
import demo.entity.Expression;
import demo.entity.FileEntity;
import demo.entity.FunctionEntity;
import demo.entity.FunctionEntityDecl;
import demo.entity.FunctionEntityDefine;
import demo.entity.Location;
import demo.entity.NamespaceEntity;
import demo.entity.StructEntity;
import demo.entity.TemplateEntity;
import demo.entity.UnionEntity;
import demo.entity.VarEntity;
import demo.entityType.Type;
import demo.symtab.EnumScope;
import demo.symtab.NamespaceScope;
import demo.symtab.TemplateScope;
import demo.symtab.UnionScope;
import demo.entity.DataAggregateEntity;

public class HandlerContext {
	protected EntityRepo entityRepo;
	protected FileEntity currentFileEntity;
	protected Stack<Entity> entityStack = new Stack<Entity>();
	Scope currentScope;
	
	public HandlerContext(EntityRepo entityrepo) {
		this.entityRepo = entityrepo;
		entityStack = new Stack<Entity>();	
	}
	public FileEntity makeFile(String filefullpath) {
		String[] name = filefullpath.split("\\\\");
		GlobalScope scope = new GlobalScope(null);
		currentFileEntity = new FileEntity(name[name.length-1], filefullpath, 
				null, entityRepo.generateId(),filefullpath,scope, new Location(filefullpath));
		
		entityRepo.add(currentFileEntity);
		pushScope(scope);
		entityStack.push(currentFileEntity);
		return currentFileEntity;
	}

	public Entity foundNamespace(String namespaceName, int startingLineNumber, Location location) {
		NamespaceEntity nsEntity = new NamespaceEntity(namespaceName,
				this.latestValidContainer().getQualifiedName()+"."+ namespaceName, 
				currentFileEntity,entityRepo.generateId(), location);
		nsEntity.setLine(startingLineNumber);
		entityRepo.add(nsEntity);
		entityStack.push(nsEntity);
		
		NamespaceScope symbol = new NamespaceScope(namespaceName+"_namespace");
		if(this.currentScope.getSymbol(namespaceName+"_namespace")==null) {
			this.currentScope.define(symbol);
		}
		else {
			symbol = (NamespaceScope) this.currentScope.getSymbol(namespaceName+"_namespace");
		}
		this.pushScope(symbol);
		
		return nsEntity;
	}
	

	public FunctionEntity foundMethodDeclaratorDeclaration(String methodName, String returnType, Location location){
		methodName = methodName.replace("::", ".");
		int id = entityRepo.generateId();
		// 分析构造作用域
		MethodSymbol symbol = new MethodSymbol(methodName+"_method");
		if(this.currentScope.getSymbol(methodName+"_method")==null) {
			this.currentScope.define(symbol);
		}
		else {
			symbol = (MethodSymbol) this.currentScope.getSymbol(methodName+"_method");
		}
		// 创建函数实体
		FunctionEntity functionEntity = new FunctionEntityDecl(methodName,
				this.latestValidContainer().getQualifiedName()+"."+methodName,  
				this.latestValidContainer(),id, symbol, location);
		functionEntity.setReturn(returnType);
		entityRepo.add(functionEntity);
		
		// 作用域维护和数据聚合实体栈更新
		pushScope(symbol);
		entityStack.push(functionEntity);
		
		return functionEntity;
	}
	

	public String getFunctionReturn(IASTDeclSpecifier declSpeci) {
		String type = null;
		if (declSpeci instanceof IASTCompositeTypeSpecifier) {
			final IASTCompositeTypeSpecifier compositeTypeSpec = (IASTCompositeTypeSpecifier) declSpeci;
			
		} else if (declSpeci instanceof IASTElaboratedTypeSpecifier) {
			final IASTElaboratedTypeSpecifier elaboratedTypeSpec = (IASTElaboratedTypeSpecifier) declSpeci;

		} else if (declSpeci instanceof IASTEnumerationSpecifier) {
			final IASTEnumerationSpecifier enumerationSpec = (IASTEnumerationSpecifier) declSpeci;

		} else if (declSpeci instanceof IASTSimpleDeclSpecifier) {
			//无返回值或者返回值非常的显而易见类似：int，float
			final IASTSimpleDeclSpecifier simple = (IASTSimpleDeclSpecifier) declSpeci;
			type = simple.getRawSignature();
			
		} else if (declSpeci instanceof IASTNamedTypeSpecifier) {
			final IASTNamedTypeSpecifier namedTypeSpec = (IASTNamedTypeSpecifier) declSpeci;
			type = namedTypeSpec.getName().toString();
		}
		return type;
	}
	
	// 实现了
	public FunctionEntity foundMethodDeclaratorDefine(String methodName,  String returnType, Location location){
		
		methodName = methodName.replace("::", ".");
		int id = entityRepo.generateId();
		MethodSymbol symbol = new MethodSymbol(methodName+"_method");
		
		if(this.currentScope.getSymbol(methodName+"_method")==null) {
			this.currentScope.define(symbol);
		}
		else if(this.currentScope.getSymbol(methodName+"_method") instanceof MethodSymbol){
			//org.antlr.symtab.Symbol symbol_TYPE =  this.currentScope.getSymbol(methodName);
			symbol = (MethodSymbol) this.currentScope.getSymbol(methodName+"_method");
		}
		else {
			this.currentScope.define(symbol);
		}
		
		FunctionEntity functionEntity = new FunctionEntityDefine(methodName,
				this.latestValidContainer().getQualifiedName()+"."+methodName, 
				this.latestValidContainer(),id,symbol, location);
		functionEntity.setReturn(returnType);
		entityRepo.add(functionEntity);
		pushScope(symbol);
		entityStack.push(functionEntity);
		return functionEntity;
	}
	public EnumEntity foundEnumDefinition(String enumName, Location location) {
		int id = entityRepo.generateId();
		EnumScope symbol = new EnumScope(enumName);
		if(this.currentScope.getSymbol(enumName)==null) {
			this.currentScope.define(symbol);
		}
		else {
			if(this.currentScope.getSymbol(enumName) instanceof EnumScope) {
				symbol = (EnumScope) this.currentScope.getSymbol(enumName);
			}
			else {
				symbol = new EnumScope(enumName+"_default");
				this.currentScope.define(symbol);
			}
		}
		
		EnumEntity enumeration = new EnumEntity(enumName,
				this.latestValidContainer().getQualifiedName()+"."+enumName,
				this.latestValidContainer(),id,symbol, location);
		entityRepo.add(enumeration);
		pushScope(symbol);
		entityStack.push(enumeration);
		return enumeration;
	}
	public ClassEntity foundClassDefinition(String ClassName,List<String> baseClass, Location location){
		int id = entityRepo.generateId();
		ClassSymbol symbol = new ClassSymbol(ClassName);
		if(this.currentScope.getSymbol(ClassName)==null) {
			this.currentScope.define(symbol);
		}
		else {
			symbol = (ClassSymbol) this.currentScope.getSymbol(ClassName);
		}
		ClassEntity classEntity = new ClassEntity(ClassName, 
				this.latestValidContainer().getQualifiedName()+"."+ClassName,
				this.latestValidContainer(),id,symbol, location);
		if(baseClass!= null) {
			classEntity.addBaseClass(baseClass);
		}
		entityRepo.add(classEntity);
		pushScope(symbol);
		entityStack.push(classEntity);
		return classEntity;
	}
	public StructEntity foundStructDefinition(String StructName, List<String> baseStruct, Location location) {
		int id = entityRepo.generateId();
		if(StructName.equals("")) {
			StructName = "Default";
		}
		StructSymbol symbol = new StructSymbol(StructName+"_struct");
		if(this.currentScope.getSymbol(StructName+"_struct")==null) {
			this.currentScope.define(symbol);
		}
		else {
			symbol =  (StructSymbol)this.currentScope.getSymbol(StructName+"_struct");
		}
		
		
		
		StructEntity structEntity = new StructEntity(StructName,
				this.latestValidContainer().getQualifiedName()+"."+StructName,
				this.latestValidContainer(), id, symbol, location);
		if(baseStruct!= null) {
			structEntity.addBaseStruct(baseStruct);
		}
		entityRepo.add(structEntity);
		pushScope(symbol);
		entityStack.push(structEntity);
		return structEntity;
	}
	public UnionEntity foundUnionDefinition(String UnionName, Location location) {
		int id = entityRepo.generateId();
		
		UnionScope symbol = new UnionScope(UnionName);
		if(this.currentScope.getSymbol(UnionName)==null) {
			this.currentScope.define(symbol);
		}
		else {//if(this.currentScope.getSymbol(UnionName) instanceof MethodSymbol)
			//org.antlr.symtab.Symbol symbol_TYPE =  this.currentScope.getSymbol(UnionName);
			symbol = (UnionScope) this.currentScope.getSymbol(UnionName);
		}

		UnionEntity unionEntity = new UnionEntity(UnionName,
				this.latestValidContainer().getQualifiedName()+"."+UnionName,
				this.latestValidContainer(), id, symbol, location);
		
		entityRepo.add(unionEntity);
		pushScope(symbol);
		entityStack.push(unionEntity);
		return unionEntity;
	}
	
	public EnumeratorEntity foundEnumeratorDefinition(String enumeratorName, Location location) {
		int id = entityRepo.generateId();
		EnumeratorEntity enumertorEntity = new EnumeratorEntity(enumeratorName,
				this.latestValidContainer().getQualifiedName()+"."+enumeratorName,
				this.latestValidContainer(),id, location);
		entityRepo.add(enumertorEntity);
		if(this.latestValidContainer() instanceof EnumEntity) {
			((EnumEntity)this.latestValidContainer()).addEnumerator(enumertorEntity);
		}
		return enumertorEntity;
	}
	
	/*
	 * Abstraction handles all expressions.
	 * such as: CPPASTBinaryExpression, CPPASTCastExpression, CPPASTDeleteExpression,
	 * CPPASTUnaryExpression, CPPASTFunctionCallExpression, CPPASTExpressionList
	 */
	public void dealExpression(IASTExpression expression) {
		if(expression instanceof CPPASTBinaryExpression) {
			CPPASTBinaryExpression binaryExp = (CPPASTBinaryExpression)expression;
			this.dealExpressionNode(binaryExp.getOperand1());
			this.dealExpressionNode(binaryExp.getOperand2());
			return;
		}
		if(expression instanceof CPPASTCastExpression) {
			CPPASTCastExpression castExp = (CPPASTCastExpression)expression;
			this.dealExpressionNode(castExp.getOperand());
			return;
		}
		
		if(expression instanceof CPPASTDeleteExpression) {
			CPPASTDeleteExpression deleteExp = (CPPASTDeleteExpression)expression;
			this.dealExpressionNode(deleteExp.getOperand());
			return;
		}
		
		if(expression instanceof CPPASTUnaryExpression) {
			CPPASTUnaryExpression unaryExp = (CPPASTUnaryExpression)expression;
			this.dealExpressionNode(unaryExp.getOperand());
			return;
		}
		if(expression instanceof CPPASTFunctionCallExpression) {
			CPPASTFunctionCallExpression functioncallExp = (CPPASTFunctionCallExpression)expression;
			this.dealExpressionNode(functioncallExp.getFunctionNameExpression());		
		}
		if(expression instanceof CPPASTExpressionList) {
			CPPASTExpressionList expressionList = (CPPASTExpressionList)expression;
			for(IASTExpression exp:expressionList.getExpressions()) {
				this.dealExpressionNode(exp);
			}
			return;
		}
	}
	/*
	 * Intermediate process for processing expressions
	 */
	public void dealExpressionNode(IASTExpression expression) {
		if(expression instanceof CPPASTIdExpression) {
			
			Entity entity = getEntity((CPPASTIdExpression)expression);
			
			if(entity != null) {
				if(entity instanceof DataAggregateEntity) {
					if(entity instanceof FunctionEntity) {
						setCall(entity);
					}
				}
				else {
					setUse(entity);
				}
			}
		}
		else {
			dealExpression(expression);
		}
	}

	public Entity getEntity(CPPASTIdExpression idExpression) {
		idExpression.getName().resolveBinding();
		if(idExpression.getName().getBinding() == null) {
			return null;
		}
		
		IBinding node = idExpression.getName().getBinding();	
		IASTNode definitionNode = null;
		switch(node.getClass().toString()) {
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable":
				definitionNode = ((CPPVariable)node).getDefinition();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter":
				definitionNode = ((CPPParameter)node).getPhysicalNode();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPField":
				definitionNode = ((CPPField)node).getDefinition();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction":
				definitionNode = ((CPPFunction)node).getDefinition();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod":
				definitionNode = ((CPPMethod)node).getDefinition();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumerator":
				definitionNode = ((CPPEnumerator)node).getDefinition();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPScope$CPPScopeProblem":
				break;
			

			
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPDeferredFunction":
				break;
				
			
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunctionInstance":
				
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType":
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.ProblemBinding":
				break;
			default:
				System.out.println(node.getClass().toString());
		}
		if(definitionNode!=null&&definitionNode.getFileLocation()!=null) {
			Entity entity = entityRepo.getEntity(definitionNode.getFileLocation().getFileName(),
					definitionNode.getFileLocation().getStartingLineNumber(), 
					definitionNode.getFileLocation().getNodeOffset());
			if(entity !=null) 	return entity;
		}
		
		return null;
	}
	
	public void setUse(Entity entity) {
		this.latestValidContainer().setUse(entity);
	}
	
	public void setCall(Entity entity) {
		this.latestValidContainer().setCall(entity);
	}
	

	

	
	public VarEntity foundVarDefinition(String varName, Location location) {
		if(location == null) return null;
		VarEntity varEntity = new VarEntity(varName, 
				this.latestValidContainer().getQualifiedName()+"."+varName,  this.latestValidContainer(), entityRepo.generateId(), location);
		entityRepo.add(varEntity);
		if(this.latestValidContainer() instanceof DataAggregateEntity ) {
			if(!(this.currentScope instanceof DataAggregateSymbol)) {
				if(this.currentScope.getSymbol(varName)==null) {
					VariableSymbol v = new VariableSymbol(varName);
					this.currentScope.define(v);
				}
			}
			
			
		}
		return varEntity;
	}
		
	public AliasEntity foundNewAlias(String aliasName, String originalName, Location location) {
		if (aliasName.equals(originalName)) return null; 
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.latestValidContainer().getQualifiedName()+"."+aliasName,this.latestValidContainer(),
				entityRepo.generateId(), originalName, location );
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}
	public AliasEntity foundNewAlias(String aliasName, Entity referToEntity, Location location) {
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.latestValidContainer().getQualifiedName()+"."+aliasName,this.latestValidContainer(),
				entityRepo.generateId(),aliasName, location);
		currentTypeEntity.setReferToEntity(referToEntity);
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}
	
	
	public TemplateEntity foundTemplate(Location location) {
		
		TemplateScope scope = new TemplateScope("template");
		TemplateEntity template = new TemplateEntity("template",
				this.latestValidContainer().getQualifiedName()+".template",
				this.latestValidContainer(),entityRepo.generateId(),scope, location);
		
		return template;
	}
	
	public void addFriendClass(String className) {
		
	}
	public void addFriendFunction(String functionName) {
		
	}
	public void foundCodeScope(IASTStatement statement) {

		BaseScope scope = new LocalScope(this.currentScope);
		pushScope(scope);
	}
	public void foundUsingImport(String usingname) {
		this.currentFileEntity.setUsing(usingname);
	}
	
	public FunctionEntity currentFunction() {
		for (int i = entityStack.size() - 1; i >= 0; i--) {
			Entity t = entityStack.get(i);
			if (t instanceof FunctionEntity)
				return (FunctionEntity) t;
		}
		return null;
	}


	public DataAggregateEntity latestValidContainer() {
		for (int i = entityStack.size() - 1; i >= 0; i--) {
			Entity t = entityStack.get(i);
			if (t instanceof FunctionEntity) 
				return (FunctionEntity)t;
			if (t instanceof FileEntity)	
				return (FileEntity)t;
			if (t instanceof StructEntity)
				return (StructEntity)t;
			if (t instanceof UnionEntity)
				return (UnionEntity)t;
			if (t instanceof ClassEntity)
				return (ClassEntity)t;
		}
		return null;
	}
	public void exitLastedEntity() {
		//we never pop up the lastest one (FileEntity)
		if (entityStack.size()>1) {
			entityStack.pop();
		}
			
	}
	
	
	private void pushScope(Scope s) {
		this.currentScope = s;
		//System.out.println("entering: "+this.currentScope.getName()+":"+s +",     " +this.currentScope.getClass().toString());
	}

	public void popScope() {
		//System.out.println("leaving: "+this.currentScope.getName()+":"+this.currentScope);
		this.currentScope = this.currentScope.getEnclosingScope();
	}
	
	public void showASTNode(IASTNode node,int i) {
		if(node.getChildren()==null) {
			return;
		}
		System.out.println("layerNo"+ i + "  "+node.getClass() + "    " + node.getRawSignature());
		for(IASTNode child:node.getChildren()) {
			System.out.println(child.getClass() + "    " + child.getRawSignature());
		}
		for(IASTNode child:node.getChildren()) {
			showASTNode(child,i+1);
		}
	}
	
}
