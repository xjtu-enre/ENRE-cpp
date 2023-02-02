package cdt;
import entity.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.ProblemType;
import relation.Relation;
import util.Configure;
import util.Tuple;
import symtab.*;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import util.UnionFind;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class HandlerContext {
	protected EntityRepo entityRepo;
	protected FileEntity currentFileEntity;
	protected Stack<Entity> entityStack = new Stack<Entity>();
	protected UnionFind overloadSet = new UnionFind();
	Scope currentScope;
	List<Scope> includeScope;

	public HandlerContext(EntityRepo entityrepo) {
		this.entityRepo = entityrepo;
		this.entityStack = new Stack<Entity>();
		this.includeScope = new ArrayList<Scope>();
	}
	
	/**
	* @methodsName: makeFile
	* @param:  String filefullpath
	* @return: FileEntity
	*/
	public FileEntity makeFile(String filefullpath) {
		String[] name = filefullpath.split("[/|\\\\]");
		GlobalScope scope = new GlobalScope(null);

		currentFileEntity = new FileEntity(name[name.length-1], filefullpath, 
				null, entityRepo.generateId(),filefullpath, scope, new Location(filefullpath));
		entityRepo.add(currentFileEntity);
		entityStack.push(currentFileEntity);
		this.currentScope = scope;
		return currentFileEntity;
	}

	public void dealIncludeScope() throws CloneNotSupportedException {
		for(FileEntity fileEntity:currentFileEntity.getIncludeEntity()){
			includeScope.add(fileEntity.getScope());
			this.currentScope.union(fileEntity.getScope());
		}
	}

	public String resolveName(String Name) {
		this.currentScope = this.entityStack.peek().getScope();
		if(this.currentScope instanceof Symbol){
			Entity en = entityRepo.getEntity(((Symbol)this.currentScope).getEntityID());
			if(en == null)
				return Name;
			if(en instanceof FileEntity)
				return Name;
			if(en.getQualifiedName().equals(""))
				return Name;
			if(en.getQualifiedName().equals("[unnamed]")) {
				return Name;
			}
			return en.getQualifiedName() + "::" + Name;
		}
		return Name;
	}


	public FunctionEntity foundFunctionDeclare(String name, String returnType, Location location, List<ParameterEntity> parameterList){
		this.currentScope = this.entityStack.peek().getScope();
		this.currentScope = this.findTheScope(name);
		int id = entityRepo.generateId();

		String[] scopeManages = name.split("::");
		name = scopeManages[scopeManages.length - 1];
		FunctionSymbol symbol = new FunctionSymbol(name, id);
		FunctionEntity functionEntity = new FunctionEntity(name, resolveName(name), this.latestValidContainer(), id, symbol, location);
		if(parameterList.size()>0){
			for(ParameterEntity parameter:parameterList){
				functionEntity.addParameter(parameter);
			}
		}
		if(this.currentScope.getSymbolByKind(symbol.getName(), Configure.Function) != null){
			overloadSet.union(id, symbol.getEntityID());
		}
		else{
			this.currentScope.define(symbol, Configure.Function);
		}
		entityRepo.add(functionEntity);
		return functionEntity;
	}

	public Scope findTheScope(String name){
		String[] scopeManages = name.split("::");
		if(scopeManages.length == 1) return this.currentScope;
		Scope current = this.currentScope;
		do{
			if(current.getSymbol(scopeManages[0]) != null){
				current = (Scope) current.getSymbol(scopeManages[0]);
				break;
			}
			if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
		}while(current.getEnclosingScope() != null);
		if(scopeManages.length == 2) return current;
		for(int i = 1; i < scopeManages.length - 1; i++){
			if(current.getSymbol(scopeManages[i]) != null){
				current = (Scope) current.getSymbol(scopeManages[i]);
			}else{
				BlockScope blockScope = new BlockScope(scopeManages[i],  -1);
				current.define( blockScope, Configure.Default);
				current = blockScope;
			}
		}
		return current;
	}
	public FunctionEntity foundFunctionDefine(String name, String returnType, Location location, List<ParameterEntity> parameterList, boolean isDefine){
		Entity en = this.entityStack.peek();
		this.currentScope = this.entityStack.peek().getScope();
		this.currentScope = findTheScope(name);
		String[] scopeManages = name.split("::");
		FunctionEntity functionEntity = null;
		FunctionSymbol symbol = null;
		name = scopeManages[scopeManages.length - 1];
		boolean shouldNewEntity = true;
		if(this.currentScope.getSymbolByKind(scopeManages[scopeManages.length -1], Configure.Function) != null){
			Integer scopeID = this.currentScope.getSymbolByKind(scopeManages[scopeManages.length -1], Configure.Function).getEntityID();
			FunctionEntity scopeFunctionEntity = (FunctionEntity) entityRepo.getEntity(scopeID);
			List<String> parameterLists = new ArrayList<String>();
			for(ParameterEntity parameterEntity:parameterList){
				parameterLists.add(parameterEntity.getType().getTypeName());
			}
			if(scopeFunctionEntity.equals(name, parameterLists)){
				shouldNewEntity = false;
				scopeFunctionEntity.setLocation(location);
				this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), scopeFunctionEntity, "Define"));
				entityStack.push(scopeFunctionEntity);
			}else if(this.overloadSet.isInSet(scopeID)){
				for(Integer id: this.overloadSet.getNodes(scopeID)){
					scopeFunctionEntity = (FunctionEntity) entityRepo.getEntity(id);
					if(scopeFunctionEntity.equals(name, parameterLists)) {
						shouldNewEntity = false;
						scopeFunctionEntity.setLocation(location);
						this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), scopeFunctionEntity, "Define"));
						entityStack.push(scopeFunctionEntity);
						break;
					}
				}
			}
		}
		if(shouldNewEntity){
			int id = entityRepo.generateId();
			symbol = new FunctionSymbol(name, id);
			functionEntity = new FunctionEntity(name, resolveName(name), this.latestValidContainer(), id, symbol, location);
			if(parameterList.size()>0){
				for(ParameterEntity parameter:parameterList){
					functionEntity.addParameter(parameter);
				}
			}
			if(this.currentScope.getSymbolByKind(symbol.getName(), Configure.Function) != null){
				overloadSet.union(id, symbol.getEntityID());
			}else{
				this.currentScope.define(symbol, Configure.Function);
			}
			if(this.latestValidContainer() instanceof ClassEntity){
				((ClassEntity) this.latestValidContainer()).addContainEntity(id);
			}
			this.latestValidContainer().addRelation(
					new Relation(this.latestValidContainer(), functionEntity, "Define"));
			functionEntity.setReturn(returnType);
			entityRepo.add(functionEntity);
			entityStack.push(functionEntity);
		}
		return functionEntity;
	}
	/**
	* @methodsName: foundNamespace
	* @description: Build Namespace entity
	* @param:  String namespaceName, int startingLineNumber, Location location
	* @return: Entity
	*/
	public Entity foundNamespace(String namespaceName, int startLine, int endLine) {
		this.currentScope = this.entityStack.peek().getScope();
		NamespaceEntity nsEntity = null;
		NamespaceScope symbol = null;

		String resolve_name = this.resolveName(namespaceName);
		int id = entityRepo.getNamespace(resolve_name);

		if(id != -1){
			nsEntity = (NamespaceEntity) entityRepo.getEntity(id);
			symbol = (NamespaceScope) nsEntity.getScope();
		}else{
			id = entityRepo.generateId();
			symbol = new NamespaceScope(namespaceName, id);
			nsEntity = new NamespaceEntity(namespaceName, resolveName(namespaceName), currentFileEntity, id, symbol);
			entityRepo.add(nsEntity);
		}
		if(this.currentScope.getSymbolByKind(symbol.getName(), Configure.Namespace) == null){
			this.currentScope.define(symbol, Configure.Namespace);
		}
		nsEntity.addScale(endLine - startLine);
		entityStack.push(nsEntity);
		return nsEntity;
	}



	
	/**
	* @methodsName: foundClassDefinition
	* @description: build Class entity
	* @param:  String ClassName,List<String> baseClass, Location location
	* @return: ClassEntity
	*/
	public ClassEntity foundClassDefinition(String ClassName,List<String> baseClass, Location location){
		this.currentScope = this.entityStack.peek().getScope();
		int id = entityRepo.generateId();
		ClassSymbol symbol = new ClassSymbol(ClassName, id);
		if(this.currentScope.getSymbolByKind(ClassName, Configure.Class)==null) {
			this.currentScope.define(symbol, Configure.Class);
		}
		else {
			symbol = (ClassSymbol) this.currentScope.getSymbolByKind(ClassName, Configure.Class);
		}
		ClassEntity classEntity = new ClassEntity(ClassName, resolveName(ClassName),
				this.latestValidContainer(),id,symbol, location);
		classEntity.addRelation(new Relation(this.currentFileEntity, classEntity,"Contain"));
		if(baseClass!= null) {
			classEntity.addBaseClass(baseClass);
			
		}
		entityRepo.add(classEntity);
		entityStack.push(classEntity);
		return classEntity;
	}
	
	
	/**
	* @methodsName: foundStructDefinition
	* @description: build Struct entity
	* @param:  String StructName, List<String> baseStruct, Location location
	* @return: StructEntity
	*/
	public StructEntity foundStructDefinition(String StructName, List<String> baseStruct, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		int id = entityRepo.generateId();
		if(StructName.equals("")) {
			StructName = "Default";
		}
		StructSymbol symbol = new StructSymbol(StructName, id);
		if(this.currentScope.getSymbolByKind(StructName, Configure.Struct)==null) {
			this.currentScope.define(symbol, Configure.Struct);
		}
		else {
			symbol =  (StructSymbol)this.currentScope.getSymbolByKind(StructName, Configure.Struct);
		}

		StructEntity structEntity = new StructEntity(StructName, resolveName(StructName),
				this.latestValidContainer(), id, symbol, location);
		if(baseStruct!= null) {
			structEntity.addBaseStruct(baseStruct);
		}
		entityRepo.add(structEntity);
		entityStack.push(structEntity);
		return structEntity;
	}
	
	
	/**
	* @methodsName: foundUnionDefinition
	* @description: build Union entity
	* @param:  String UnionName, Location location
	* @return: UnionEntity
	*/
	public UnionEntity foundUnionDefinition(String UnionName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		int id = entityRepo.generateId();
		
		UnionScope symbol = new UnionScope(UnionName, id);
		if(this.currentScope.getSymbolByKind(UnionName, Configure.Union)==null) {
			this.currentScope.define(symbol, Configure.Union);
		}
		else {
			symbol = (UnionScope) this.currentScope.getSymbolByKind(UnionName, Configure.Union);
		}

		UnionEntity unionEntity = new UnionEntity(UnionName, resolveName(UnionName),
				this.latestValidContainer(), id, symbol, location);
		
		entityRepo.add(unionEntity);
		entityStack.push(unionEntity);
		return unionEntity;
	}
	
	/**
	* @methodsName: foundEnumDefinition
	* @description: build Enum entity
	* @param:  String enumName, Location location
	* @return: EnumEntity
	*/
	public EnumEntity foundEnumDefinition(String enumName, Location location) {
		int id = entityRepo.generateId();
		EnumScope symbol = new EnumScope(enumName, id);
		if(this.currentScope.getSymbolByKind(enumName, Configure.Enum)==null) {
			this.currentScope.define(symbol, Configure.Enum);
		}
		else {
			if(this.currentScope.getSymbolByKind(enumName, Configure.Enum) instanceof EnumScope) {
				symbol = (EnumScope) this.currentScope.getSymbolByKind(enumName, Configure.Enum);
			}
			else {
				symbol = new EnumScope(enumName, id);
				this.currentScope.define(symbol, Configure.Enum);
			}
		}
		
		EnumEntity enumeration = new EnumEntity(enumName, resolveName(enumName),
				this.latestValidContainer(), id, symbol, location);
		entityRepo.add(enumeration);
		entityStack.push(enumeration);
		return enumeration;
	}
	
	
	/**
	* @methodsName: foundEnumeratorDefinition
	* @description: build Enumerator entity
	* @param:  String enumeratorName, Location location
	* @return: EnumeratorEntity
	*/
	public EnumeratorEntity foundEnumeratorDefinition(String enumeratorName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		int id = entityRepo.generateId();
		EnumeratorEntity enumertorEntity = new EnumeratorEntity(enumeratorName, resolveName(enumeratorName),
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
	*/
	public void dealExpression(IASTExpression expression) {
		if(expression instanceof CPPASTBinaryExpression) {
			CPPASTBinaryExpression binaryExp = (CPPASTBinaryExpression)expression;
			this.dealExpressionNode(binaryExp.getOperand1(), "Set");
			this.dealExpressionNode(binaryExp.getOperand2(), "Use");
		}
		if(expression instanceof CPPASTCastExpression) {
			CPPASTCastExpression castExp = (CPPASTCastExpression) expression;
			this.dealExpressionNode(castExp.getOperand(), "Cast");
		}
		if(expression instanceof CPPASTDeleteExpression) {
			CPPASTDeleteExpression deleteExp = (CPPASTDeleteExpression)expression;
			this.dealExpressionNode(deleteExp.getOperand(), "Delete");
		}

		if(expression instanceof CPPASTUnaryExpression) {
			CPPASTUnaryExpression unaryExp = (CPPASTUnaryExpression)expression;
			String unaryExpText = unaryExp.getRawSignature();
			if(unaryExp.getOperator() == 9 || unaryExp.getOperator() == 10){
				this.dealExpressionNode(unaryExp.getOperand(), "Modify");
			}
		}
		if(expression instanceof CPPASTFunctionCallExpression) {
			CPPASTFunctionCallExpression functionCallExpression = (CPPASTFunctionCallExpression)expression;
			IASTExpression functionNameExpression = functionCallExpression.getFunctionNameExpression();
			if(functionNameExpression instanceof CPPASTUnaryExpression){}
			else if(functionNameExpression instanceof CPPASTFieldReference) {
				String rawSignature = expression.getRawSignature();
				String callFunctionName = ((CPPASTFieldReference) functionNameExpression).getFieldName().toString();
				ICPPASTExpression fieldExpression = ((CPPASTFieldReference) functionNameExpression).getFieldOwner();
				if(fieldExpression instanceof CPPASTIdExpression){
					String fieldName = ((CPPASTIdExpression) fieldExpression).getName().toString();
					this.latestValidContainer().addRelationByObject("call", callFunctionName, fieldName);
				}else if(fieldExpression instanceof CPPASTFieldReference){
					String text = expression.getRawSignature();
				}
			}
			else if(functionNameExpression instanceof CPPASTFunctionCallExpression){}
			else if(functionNameExpression instanceof CPPASTIdExpression){}
			this.dealExpressionNode(functionCallExpression.getFunctionNameExpression(), "Call");
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
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef":
				definitionNode = ((CPPTypedef)node).getDefinition();
				break;
			//case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPLambdaExpressionParameter":

			default:
//				System.out.println(idExpression.getRawSignature());
//				System.out.println(node.getClass().toString());
		}
		if(definitionNode != null && definitionNode.getFileLocation() != null) {
			return new Tuple(definitionNode.getFileLocation().getFileName()+
					definitionNode.getFileLocation().getStartingLineNumber()+
					definitionNode.getFileLocation().getNodeOffset(), type);
		}
		
		return null;
	}

	public Entity findTheTypedEntity(String name){
		String[] scopeManages = name.split("::");
		Scope current = this.currentScope;
		if(scopeManages.length == 1){
			do{
				if(current.getSymbol(scopeManages[0]) != null){
					if(current.getSymbol(scopeManages[0]) instanceof DataAggregateSymbol){
						return entityRepo.getEntity(current.getSymbol(scopeManages[0]).getEntityID());
					}
				}
				if(current.getEnclosingScope() == current) return null;
				if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
			}while(current.getEnclosingScope() != null);
		}
		else if(scopeManages.length == 2){
			do{
				if(current.getSymbol(scopeManages[0]) != null){
					if(current.getSymbol(scopeManages[0]) instanceof Scope){
						current = (Scope) current.getSymbol(scopeManages[0]);
						if(current.getSymbol(scopeManages[1]) != null){
							if(current.getSymbol(scopeManages[1]) instanceof DataAggregateSymbol){
								return entityRepo.getEntity(current.getSymbol(scopeManages[1]).getEntityID());
							}
						}
					}
					break;
				}
				if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
				if(current.getEnclosingScope() == current) return null;
			}while(current.getEnclosingScope() != null);
		}
		return null;
	}

	/**
	* @methodsName: foundVarDefinition
	* @description: build var entity
	* @param: String varName, Location location
	* @return: VarEntity
	*/
	public VarEntity foundVarDefinition(String varName, Location location, String type) {
		this.currentScope = this.entityStack.peek().getScope();
		if(location == null) return null;
		Integer id = entityRepo.generateId();
		String qualifiedName = varName;
		if(this.latestValidContainer() instanceof NamespaceEntity) qualifiedName = resolveName(varName);
		Entity typeEntity = this.findTheTypedEntity(type);
		if(typeEntity != null){
			typeEntity = this.findTheTypedEntity(type);
		}
		VarEntity varEntity = new VarEntity(varName, qualifiedName,  this.latestValidContainer(), id, location, type);
		entityRepo.add(varEntity);
		if(this.latestValidContainer() instanceof DataAggregateEntity ) {
			if(!(this.currentScope instanceof DataAggregateSymbol)) {
				if(this.currentScope.getSymbolByKind(varName, Configure.Variable)==null) {
					VariableSymbol v = new VariableSymbol(varName, id);
					this.currentScope.define(v, Configure.Variable);
				}
			}
		}
		if(this.latestValidContainer() instanceof ClassEntity){
			((ClassEntity) this.latestValidContainer()).addContainEntity(id);
		}
		return varEntity;
	}

	public LabelEntity foundLabelDefinition(String labelName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		if(location == null) return null;
		LabelEntity labelEntity = new LabelEntity(labelName,  resolveName(labelName),  this.latestValidContainer(), entityRepo.generateId(), location, "null");
		entityRepo.add(labelEntity);
		if(this.latestValidContainer() instanceof DataAggregateEntity ) {
			if(!(this.currentScope instanceof DataAggregateSymbol)) {
				if(this.currentScope.getSymbolByKind(labelName, Configure.Variable)==null) {
					VariableSymbol v = new VariableSymbol(labelName, -1);
					this.currentScope.define(v, Configure.Variable);
				}
			}
		}
		return labelEntity;
	}
	
	/**
	* @methodsName: foundTypedefDefinition
	* @description: build Typedef entity
	* @param: String Name, Location location
	* @return: TypedefEntity
	*/
	public TypedefEntity foundTypedefDefinition(String Name, String originalName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		TypedefEntity typedefEntity = new TypedefEntity(Name, resolveName(Name),this.latestValidContainer(),
				entityRepo.generateId(),  location );
		entityRepo.add(typedefEntity);
		return typedefEntity;		
	}
	
	
	/**
	* @methodsName: foundNewAlias
	* @description: build alias entity
	* @param: String aliasName, String originalName, Location location
	* @return: AliasEntity
	*/
	public AliasEntity foundNewAlias(String aliasName, String originalName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		if (aliasName.equals(originalName)) return null;
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.resolveName(aliasName), this.latestValidContainer(),
				entityRepo.generateId(), originalName, location );
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}

	public NamespaceAliasEntity foundNewNamespaceAlias(String aliasName, String originalName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		if (aliasName.equals(originalName)) return null;
		NamespaceAliasEntity currentTypeEntity = new NamespaceAliasEntity(aliasName,
				this.resolveName(aliasName), this.latestValidContainer(),
				entityRepo.generateId(), originalName, location );
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;
	}
	
	/**
	* @methodsName: foundNewAlias
	* @description: build alias entity
	* @param: String aliasName, Entity referToEntity, Location location
	* @return: AliasEntity
	*/
	public AliasEntity foundNewAlias(String aliasName, Entity referToEntity, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, resolveName(aliasName),this.latestValidContainer(),
				entityRepo.generateId(),aliasName, location);
		currentTypeEntity.setReferToEntity(referToEntity);
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}
	


	public void addFriendClass(String className) {
		if(this.latestValidContainer() instanceof ClassEntity) {
			((ClassEntity) this.latestValidContainer()).addFriendClass(className);
		}
	}
	public void addFriendFunction(String functionName) {
		if(this.latestValidContainer() instanceof ClassEntity) {
			((ClassEntity) this.latestValidContainer()).addFriendFunction(functionName);
		}
	}
	
	/**
	* @methodsName: foundCodeScope
	* @description: push scope into stack
	* @param: IASTStatement statement
	* @return: void
	*/
	public void foundCodeScope(IASTStatement statement) {
		BaseScope scope = new LocalScope(this.currentScope);
		BlockEntity entity = new BlockEntity("", "", this.latestValidContainer(),
				-1, scope, new Location(-1, -1, -1, -1,
				this.currentFileEntity.getQualifiedName()));
	}
	
	
	public void foundUsingImport(String usingname) {
		this.latestValidContainer().setUsing(usingname);
	}
	
	/**
	* @methodsName: currentFunction()
	* @description: Maintains an up-to-date function entity
	* @param: null
	* @return: FunctionEntity
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
			if (t instanceof NamespaceEntity)
				return (NamespaceEntity)t;
			if (t instanceof EnumEntity) {
				return (EnumEntity)t;
			}
				
		}
		return null;
	}
	
	
	/**
	* @methodsName: exitLastedEntity()
	* @description: Maintains an up-to-date Entity
	* @param: null
	* @return: void
	*/
	public Entity exitLastedEntity() {
		//we never pop up the lastest one (FileEntity)
		if (entityStack.size() > 1) {
			return entityStack.pop();
		}
		return null;
	}
	
	
	/**
	* @methodsName: popScope
	* @description: pop scope from stack
	* @param: null
	* @return: void
	*/
	public void popScope() {
//		this.currentScope = this.currentScope.getEnclosingScope();
//		if(this.currentScope instanceof FunctionSymbol){
//			if(((FunctionSymbol)(this.currentScope)).isBaseOverload()){
//				popScope();
//			}
//		}
	}
	
	public void showASTNode(IASTNode node, int i) {
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
