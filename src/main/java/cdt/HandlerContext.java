package cdt;

import entity.*;
import symtab.EnumScope;
import symtab.NamespaceScope;
import symtab.TemplateScope;
import symtab.UnionScope;
import util.Tuple;
import org.antlr.symtab.*;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.List;
import java.util.Stack;

public class HandlerContext {
	protected EntityRepo entityRepo;
	protected FileEntity currentFileEntity;
	protected Stack<Entity> entityStack = new Stack<Entity>();
	Scope currentScope;
	
	public HandlerContext(EntityRepo entityrepo) {
		this.entityRepo = entityrepo;
		entityStack = new Stack<Entity>();	
	}
	
	/**
	* @methodsName: makeFile
	* @description: Build file entity
	* @param:  String filefullpath
	* @return: FileEntity
	* @throws: 
	*/
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
	
	/**
	* @methodsName: foundNamespace
	* @description: Build Namespace entity
	* @param:  String namespaceName, int startingLineNumber, Location location
	* @return: Entity
	* @throws: 
	*/
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
	
	/**
	* @methodsName: foundMethodDeclaratorDeclaration
	* @description: Build MethodDeclarator entity
	* @param:  String methodName, String returnType, Location location
	* @return: FunctionEntity
	* @throws: 
	*/
	public FunctionEntity foundMethodDeclaratorDeclaration(String methodName, String returnType, Location location){
		methodName = methodName.replace("::", ".");
		int id = entityRepo.generateId();
		MethodSymbol symbol = new MethodSymbol(methodName+"_method");
		if(this.currentScope.getSymbol(methodName+"_method")==null) {
			this.currentScope.define(symbol);
		}
		else {
			symbol = (MethodSymbol) this.currentScope.getSymbol(methodName+"_method");
		}
		FunctionEntity functionEntity = new FunctionEntityDecl(methodName,
				this.latestValidContainer().getQualifiedName()+"."+methodName,  
				this.latestValidContainer(),id, symbol, location);
		functionEntity.setReturn(returnType);
		entityRepo.add(functionEntity);
		
		pushScope(symbol);
		entityStack.push(functionEntity);
		
		return functionEntity;
	}
	
	/**
	* @methodsName: getFunctionReturn
	* @description: get the function return
	* @param:  IASTDeclSpecifier declSpeci
	* @return: String
	* @throws: 
	*/
	public String getFunctionReturn(IASTDeclSpecifier declSpeci) {
		String type = null;
		if (declSpeci instanceof IASTCompositeTypeSpecifier) {
			final IASTCompositeTypeSpecifier compositeTypeSpec = (IASTCompositeTypeSpecifier) declSpeci;
			
		} else if (declSpeci instanceof IASTElaboratedTypeSpecifier) {
			final IASTElaboratedTypeSpecifier elaboratedTypeSpec = (IASTElaboratedTypeSpecifier) declSpeci;

		} else if (declSpeci instanceof IASTEnumerationSpecifier) {
			final IASTEnumerationSpecifier enumerationSpec = (IASTEnumerationSpecifier) declSpeci;

		} else if (declSpeci instanceof IASTSimpleDeclSpecifier) {
			final IASTSimpleDeclSpecifier simple = (IASTSimpleDeclSpecifier) declSpeci;
			type = simple.getRawSignature();
			
		} else if (declSpeci instanceof IASTNamedTypeSpecifier) {
			final IASTNamedTypeSpecifier namedTypeSpec = (IASTNamedTypeSpecifier) declSpeci;
			type = namedTypeSpec.getName().toString();
		}
		return type;
	}
	
	/**
	* @methodsName: foundMethodDeclaratorDefine
	* @description: build function entity
	* @param:  String methodName,  String returnType, Location location
	* @return: FunctionEntity
	* @throws: 
	*/
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
//		System.out.println(functionEntity.getName()+functionEntity.getLocation().getFileName() + functionEntity.getLocation().getStartLine()
//				+ " "+functionEntity.getLocation().getStartColumn());
		pushScope(symbol);
		entityStack.push(functionEntity);
		return functionEntity;
	}
	
	
	/**
	* @methodsName: foundEnumDefinition
	* @description: build Enum entity
	* @param:  String enumName, Location location
	* @return: EnumEntity
	* @throws: 
	*/
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
	
	/**
	* @methodsName: foundClassDefinition
	* @description: build Class entity
	* @param:  String ClassName,List<String> baseClass, Location location
	* @return: ClassEntity
	* @throws: 
	*/
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
	
	
	/**
	* @methodsName: foundStructDefinition
	* @description: build Struct entity
	* @param:  String StructName, List<String> baseStruct, Location location
	* @return: StructEntity
	* @throws: 
	*/
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
	
	
	/**
	* @methodsName: foundUnionDefinition
	* @description: build Union entity
	* @param:  String UnionName, Location location
	* @return: UnionEntity
	* @throws: 
	*/
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
	
	
	/**
	* @methodsName: foundEnumeratorDefinition
	* @description: build Enumerator entity
	* @param:  String enumeratorName, Location location
	* @return: EnumeratorEntity
	* @throws: 
	*/
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
	
	
	/**
	* @methodsName: dealExpression
	* @description: Abstraction handles all expressions.
	*  such as: CPPASTBinaryExpression, CPPASTCastExpression, CPPASTDeleteExpression,
	*   CPPASTUnaryExpression, CPPASTFunctionCallExpression, CPPASTExpressionList
	* @param:  IASTExpression expression
	* @return: void
	* @throws: 
	*/
	public void dealExpression(IASTExpression expression) {
		if(expression instanceof CPPASTBinaryExpression) {
			CPPASTBinaryExpression binaryExp = (CPPASTBinaryExpression)expression;
			this.dealExpressionNode(binaryExp.getOperand1(), "set");
			this.dealExpressionNode(binaryExp.getOperand2(), "use");
		}
		if(expression instanceof CPPASTCastExpression) {
			CPPASTCastExpression castExp = (CPPASTCastExpression)expression;
			this.dealExpressionNode(castExp.getOperand(), "cast");
		}
		
		if(expression instanceof CPPASTDeleteExpression) {
			CPPASTDeleteExpression deleteExp = (CPPASTDeleteExpression)expression;
			this.dealExpressionNode(deleteExp.getOperand(), "delete");
		}
		
		if(expression instanceof CPPASTUnaryExpression) {
			CPPASTUnaryExpression unaryExp = (CPPASTUnaryExpression)expression;
			this.dealExpressionNode(unaryExp.getOperand(), "modify");
		}
		if(expression instanceof CPPASTFunctionCallExpression) {
			CPPASTFunctionCallExpression functioncallExp = (CPPASTFunctionCallExpression)expression;
			//System.out.println(((CPPASTFunctionCallExpression)expression).getRawSignature());
			this.dealExpressionNode(functioncallExp.getFunctionNameExpression(), "call");
		}
		if(expression instanceof CPPASTExpressionList) {
			CPPASTExpressionList expressionList = (CPPASTExpressionList)expression;
			for(IASTExpression exp:expressionList.getExpressions()) {
				this.dealExpression(exp);
			}
		}
	}
	
	
	/**
	* @methodsName: dealExpressionNode
	* @description: Intermediate process for processing expressions
	* @param: IASTExpression expression, String expressionType
	* @return: void
	* @throws: 
	*/
	public void dealExpressionNode(IASTExpression expression, String expressionType) {
		if(expression instanceof CPPASTIdExpression) {
			Tuple entityInformation = this.getEntityInforbyBinding((CPPASTIdExpression)expression);
			if(entityInformation == null) {
				this.latestValidContainer().addScopeRelation(expressionType, expression.getRawSignature());
			}
			else {
				this.latestValidContainer().addBindingRelation(expressionType, 
						(String)entityInformation.getSecond(), (String)entityInformation.getFirst());
			}
		}
		else {
			dealExpression(expression);
		}
	}
	
	
	/**
	* @methodsName: getEntityInforbyBinding
	* @description: get binding information to obtaine Entity information
	* @param: CPPASTIdExpression idExpression
	* @return: Tuple
	* @throws: 
	*/
	public Tuple getEntityInforbyBinding(CPPASTIdExpression idExpression) {
		idExpression.getName().resolveBinding();
		if(idExpression.getName().getBinding() == null) {
			return null;
		}
		
		IBinding node = idExpression.getName().getBinding();	
		IASTNode definitionNode = null;
		String type = "unknown";
		switch(node.getClass().toString()) {
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable":
				definitionNode = ((CPPVariable)node).getDefinition();
				type = "var";
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter":
				definitionNode = ((CPPParameter)node).getPhysicalNode();
				type = "parameter";
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPField":
				definitionNode = ((CPPField)node).getDefinition();
				type = "field";
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction":
				definitionNode = ((CPPFunction)node).getDefinition();
				// class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator
				type = "function";
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod":
				definitionNode = ((CPPMethod)node).getDefinition();
				type = "method";
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumerator":
				definitionNode = ((CPPEnumerator)node).getDefinition();
				type = "enumerator";
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPScope$CPPScopeProblem":
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef":
				definitionNode = ((CPPTypedef)node).getDefinition();
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
			return new Tuple(definitionNode.getFileLocation().getFileName()+
					definitionNode.getFileLocation().getStartingLineNumber()+
					definitionNode.getFileLocation().getNodeOffset(), type);
		}
		
		return null;
	}

	/**
	* @methodsName: foundVarDefinition
	* @description: build var entity
	* @param: String varName, Location location
	* @return: VarEntity
	* @throws: 
	*/
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
	
	
	/**
	* @methodsName: foundNewAlias
	* @description: build alias entity
	* @param: String aliasName, String originalName, Location location
	* @return: AliasEntity
	* @throws: 
	*/
	public AliasEntity foundNewAlias(String aliasName, String originalName, Location location) {
		if (aliasName.equals(originalName)) return null; 
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.latestValidContainer().getQualifiedName()+"."+aliasName,this.latestValidContainer(),
				entityRepo.generateId(), originalName, location );
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}
	
	/**
	* @methodsName: foundNewAlias
	* @description: build alias entity
	* @param:String aliasName, Entity referToEntity, Location location
	* @return: AliasEntity
	* @throws: 
	*/
	public AliasEntity foundNewAlias(String aliasName, Entity referToEntity, Location location) {
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.latestValidContainer().getQualifiedName()+"."+aliasName,this.latestValidContainer(),
				entityRepo.generateId(),aliasName, location);
		currentTypeEntity.setReferToEntity(referToEntity);
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}
	
	/**
	* @methodsName: foundTemplate
	* @description: build Template entity
	* @param: Location location
	* @return: TemplateEntity
	* @throws: 
	*/
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
	
	/**
	* @methodsName: foundCodeScope
	* @description: push scope into stack
	* @param: IASTStatement statement
	* @return: void
	* @throws: 
	*/
	public void foundCodeScope(IASTStatement statement) {
		BaseScope scope = new LocalScope(this.currentScope);
		pushScope(scope);
	}
	
	
	public void foundUsingImport(String usingname) {
		this.currentFileEntity.setUsing(usingname);
	}
	
	/**
	* @methodsName: currentFunction()
	* @description: Maintains an up-to-date function entity
	* @param: null
	* @return: FunctionEntity
	* @throws: 
	*/
	public FunctionEntity currentFunction() {
		for (int i = entityStack.size() - 1; i >= 0; i--) {
			Entity t = entityStack.get(i);
			if (t instanceof FunctionEntity)
				return (FunctionEntity) t;
		}
		return null;
	}

	/**
	* @methodsName: latestValidContainer()
	* @description: Maintains an up-to-date DataAggregateEntity
	* @param: null
	* @return: DataAggregateEntity
	* @throws: 
	*/
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
	
	
	/**
	* @methodsName: exitLastedEntity()
	* @description: Maintains an up-to-date Entity
	* @param: null
	* @return: void
	* @throws: 
	*/
	public void exitLastedEntity() {
		//we never pop up the lastest one (FileEntity)
		if (entityStack.size()>1) {
			entityStack.pop();
		}	
	}
	
	/**
	* @methodsName: pushScope
	* @description: push scope into stack
	* @param: Scope s
	* @return: void
	* @throws: 
	*/
	private void pushScope(Scope s) {
		this.currentScope = s;
	}
	
	
	/**
	* @methodsName: popScope
	* @description: pop scope from stack
	* @param: null
	* @return: void
	* @throws: 
	*/
	public void popScope() {
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
