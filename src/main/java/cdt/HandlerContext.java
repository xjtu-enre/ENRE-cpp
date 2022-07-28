package cdt;
import entity.*;
import relation.Relation;
import util.Configure;
import util.Tuple;
import symtab.*;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HandlerContext {
	protected EntityRepo entityRepo;
	protected FileEntity currentFileEntity;
	protected Stack<Entity> entityStack = new Stack<Entity>();
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
		String[] name = filefullpath.split(File.separator);
		GlobalScope scope = new GlobalScope(null);
		currentFileEntity = new FileEntity(name[name.length-1], filefullpath, 
				null, entityRepo.generateId(),filefullpath, scope, new Location(filefullpath));
		
		entityRepo.add(currentFileEntity);
		pushScope(scope);
		entityStack.push(currentFileEntity);
		return currentFileEntity;
	}

	public void dealIncludeScope(){
		for(FileEntity fileEntity:currentFileEntity.getIncludeEntity()){
			includeScope.add(fileEntity.getScope());
		}
	}
	
	public String resolveName(String Name) {
		if(this.latestValidContainer() instanceof FileEntity)
			return Name;
		if(this.latestValidContainer().getQualifiedName() == "")
			return Name;
		return this.latestValidContainer().getQualifiedName() + "::" + Name;
	}

	/**
	 * @methodsName: findFunctionEntity
	 * @param:  String qualifiedName, int kind
	 * @return: int[]:
	 */
	public int[] findFunction(String functionName, List<String> parameterLists){
		String[] names = functionName.split("::");
		Scope scope = this.currentFileEntity.getScope();
		for(int i=0; i < names.length; i++){
			String name = names[i];
			if(scope.getSymbol(name) != null){
				if(i == names.length - 1){
					if(scope.getSymbolByKind(name, Configure.Function) != null){
						FunctionSymbol symbol = (FunctionSymbol)scope.getSymbolByKind(name, Configure.Function);
						if(symbol.isOverload()){
							for(Integer id:symbol.getOverload_list()){
								FunctionEntity getEntity = (FunctionEntity) entityRepo.getEntity(id);
								if(getEntity.equals(name, parameterLists)) return new int[]{Configure.FOUNDENTITY, getEntity.getId()};
							}
						}else{
							FunctionEntity getEntity = (FunctionEntity) entityRepo.getEntity(symbol.getEntityID());
							if(getEntity.equals(name, parameterLists)) return new int[]{Configure.FOUNDENTITY, getEntity.getId()};
						}
					}
				}
				if(scope.getSymbol(name) instanceof Scope) scope = (Scope)scope.getSymbol(name);
			}
			else break;
		}
		for(Scope includeScope: this.includeScope){
			scope = includeScope;
			for(int i=0; i<names.length; i++){
				String name = names[i];
				if(scope.getSymbol(name) != null){
					if(i == names.length - 1){
						FunctionSymbol symbol = (FunctionSymbol)scope.getSymbolByKind(name, Configure.Function);
						if(symbol.isOverload()){
							for(Integer id:symbol.getOverload_list()){
								FunctionEntity getEntity = (FunctionEntity) entityRepo.getEntity(id);
								if(getEntity.equals(name, parameterLists)) return new int[]{Configure.FOUNDENTITYINCLUDE, getEntity.getId()};
							}
						}else{
							FunctionEntity getEntity = (FunctionEntity) entityRepo.getEntity(symbol.getEntityID());
							if(getEntity.equals(name, parameterLists)) {
								return new int[]{Configure.FOUNDENTITYINCLUDE, getEntity.getId()};
							}
						}
					}
					if(scope.getSymbol(name) instanceof Scope) scope = (Scope)scope.getSymbol(name);
				}
				else break;
			}
		}
		return new int[]{Configure.NOTFOUNDENTITY, -1};
	}

	public Scope findScope(String qualifiedName){
		Scope returnScope = null;
		String[] names = qualifiedName.split("::");
		if(names.length == 1) return null;
		returnScope = this.currentFileEntity.getScope();
		for(int i=0;i<names.length-1;i++){
			if(returnScope.getSymbol(names[i])==null){
				return null;
			}
			returnScope = (Scope) returnScope.getSymbol(names[i]);
		}
		return returnScope;
	}
	/**
	* @methodsName: foundNamespace
	* @description: Build Namespace entity
	* @param:  String namespaceName, int startingLineNumber, Location location
	* @return: Entity
	*/
	public Entity foundNamespace(String namespaceName) {
		int id = entityRepo.generateId();
		if(namespaceName.length() == 0) return null;
		NamespaceScope symbol = new NamespaceScope(namespaceName, id);
		if(this.currentScope.getSymbolByKind(namespaceName, Configure.Namespace)==null) {
			this.currentScope.define(symbol, Configure.Namespace);
		}
		else {
			symbol = (NamespaceScope) this.currentScope.getSymbolByKind(namespaceName, Configure.Namespace);
		}
		this.pushScope(symbol);
		NamespaceEntity nsEntity = new NamespaceEntity(namespaceName,
				resolveName(namespaceName), currentFileEntity, id, symbol);
		entityRepo.add(nsEntity);
		entityStack.push(nsEntity);
		return nsEntity;
	}

	/**
	* @methodsName: foundMethodDeclaratorDeclaration
	* @param:  String methodName, String returnType, Location location
	* @return: FunctionEntity
	*/
	public FunctionEntity foundFunctionDefine(String name, String returnType, Location location, List<ParameterEntity> parameterList, boolean isDefine){
		// TODO 需要明确当前的function entity 是哪一个（不需要知道其虚 overload base scope）
		// TODO 这里的symbol是明确的对应于function entity的作用域， 但是它根据实际情况指向上层作用域或者是Overload base作用域
		// TODO 需要给定functionEntity一个函数签名
		if(name.contains("::")){
			String[] realName = name.split("::");
			for(String text:realName){
				if(this.currentScope.getSymbol(text) != null){

				}
			}
		}
		FunctionEntity functionEntity = null;
		FunctionSymbol symbol = null;
		List<String> parameterTypeLists = new ArrayList<String>();
		for(ParameterEntity entity:parameterList) parameterTypeLists.add(entity.getType().getTypeName());
		int[] findFunctionID = findFunction(name, parameterTypeLists);

		if(findFunctionID[0] != Configure.NOTFOUNDENTITY){
			// TODO 情况1 存在完全一致的实体
			functionEntity = (FunctionEntity) entityRepo.getEntity(findFunctionID[1]);
			if(isDefine) functionEntity.setLocation(location);
			if(this.currentScope.getSymbolByKind(functionEntity.getName(), Configure.Function) == null){
				// TODO 情况1.1 存在完全一致的实体且当前作用域无相同名称实体
				// TODO 这里需要替换成作用域解析符 处理过后的方法内容 这里直接用了API返回的名字 很有可能是ASTObject::ASTObject这种类型的
				symbol = new FunctionSymbol(name, findFunctionID[1]);
				this.currentScope.define(symbol, Configure.Function);
			}else{
				// TODO 情况1.2 存在完全一致的实体 当前作用域内存在相同名称的函数，检测是否为Overload基类
				FunctionSymbol functionSymbol = (FunctionSymbol) this.currentScope.getSymbolByKind(functionEntity.getName(), Configure.Function);
				if(functionSymbol.isBaseOverload()){
					// TODO 情况1.2.1 存在完全一致的实体 当前作用域内存在相同名称的函数, 是Overload基类
					// TODO 检查是否有完全一致的函数实体存在
					for(int overloadID:functionSymbol.getOverload_list()){
						if(overloadID == findFunctionID[1]){
							// TODO 存在完全一致的实体 需要使用该作用域
							symbol = (FunctionSymbol) functionSymbol.getSymbolByKind(functionEntity.getNameWithSignature(), Configure.Function);
						}
					}
				}else{
					// TODO 情况1.2.2 存在完全一致的实体 当前作用域内存在相同名称的函数, 非overload基类，检测是否为同一个函数
					if(functionSymbol.getEntityID() == findFunctionID[1]){
						// TODO 情况1.2.2.1 存在完全一致的实体 当前作用域内存在相同名称的函数, 非overload基类，是同一个函数，
						// TODO 直接把当前作用域设置为该作用域
						symbol = functionSymbol;
					}else{
						// TODO 情况1.2.2.2 存在完全一致的实体 当前作用域内存在相同名称的函数, 非overload基类，非同一个函数，
						// 当前找到的这个symbol没有被设置为是一个overload函数的基类(Overload Base)
						// TODO 生成新的作用域, 使用函数签名
						// TODO 需要在出栈的时候确定一下 是否为baseOverload，
						// TODO baseOverload function和Overload function可以被视作是一个整体 绑定起来
						functionSymbol.setBaseOverload();
						symbol = new FunctionSymbol(functionEntity.getNameWithSignature(), findFunctionID[1]);
						symbol.setOverload();
						functionSymbol.define(symbol, Configure.Function);
						functionSymbol.addOverload(findFunctionID[1]);
					}
				}
			}

		}else{
			// 不存在已被声明过的函数实体
			int id = entityRepo.generateId();
			symbol = new FunctionSymbol(name, id);
			functionEntity = new FunctionEntity(name, resolveName(name), this.latestValidContainer(), id, symbol, location);
			if(parameterList.size()>0){
				for(ParameterEntity parameter:parameterList){
					//TODO 没有把parameter加入到entityrepo里
					functionEntity.addParameter(parameter);
				}
			}
			if(null == this.currentScope.getSymbolByKind(name, Configure.Function)) {
				this.currentScope.define(symbol, Configure.Function);
			}
			else {
				FunctionSymbol functionSymbol = (FunctionSymbol) this.currentScope.getSymbolByKind(functionEntity.getName(), Configure.Function);
				if(!functionSymbol.isBaseOverload()){
					functionSymbol.setBaseOverload();
				}
				symbol = new FunctionSymbol(functionEntity.getNameWithSignature(), findFunctionID[1]);
				symbol.setOverload();
				functionSymbol.define(symbol, Configure.Function);
				functionSymbol.addOverload(id);
			}
			if(this.latestValidContainer() instanceof ClassEntity){
				((ClassEntity) this.latestValidContainer()).addContainEntity(id);
			}
			if(!isDefine) this.latestValidContainer().addRelation(
					new Relation(this.latestValidContainer(), functionEntity, "Declare"));
			functionEntity.setReturn(returnType);
			entityRepo.add(functionEntity);
		}

		pushScope(symbol);
		entityStack.push(functionEntity);
		return functionEntity;
	}
	
	/**
	* @methodsName: foundClassDefinition
	* @description: build Class entity
	* @param:  String ClassName,List<String> baseClass, Location location
	* @return: ClassEntity
	*/
	public ClassEntity foundClassDefinition(String ClassName,List<String> baseClass, Location location){
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
	*/
	public StructEntity foundStructDefinition(String StructName, List<String> baseStruct, Location location) {
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
		pushScope(symbol);
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
		pushScope(symbol);
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
		pushScope(symbol);
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
			this.dealExpressionNode(unaryExp.getOperand(), "Modify");
		}
		if(expression instanceof CPPASTFunctionCallExpression) {
			CPPASTFunctionCallExpression functionCallExpression = (CPPASTFunctionCallExpression)expression;
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

	/**
	* @methodsName: foundVarDefinition
	* @description: build var entity
	* @param: String varName, Location location
	* @return: VarEntity
	*/
	public VarEntity foundVarDefinition(String varName, Location location, String type) {
		if(location == null) return null;
		Integer id = entityRepo.generateId();
		VarEntity varEntity = new VarEntity(varName, resolveName(varName),  this.latestValidContainer(), id, location, type);
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
		if (aliasName.equals(originalName)) return null; 
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.resolveName(originalName), this.latestValidContainer(),
				entityRepo.generateId(), originalName, location );
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}

	public NamespaceAliasEntity foundNewNamespaceAlias(String aliasName, String originalName, Location location) {
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
		pushScope(scope);
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
				EnumEntity enumEntity = (EnumEntity)t;
				if(enumEntity.getisScope()) {
					return enumEntity;
				}
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
	*/
	private void pushScope(Scope s) {
//		if(s != null) System.out.println("push scope: " + s.getClass().toString() + " " + s.getName()+ " in " +this.currentFileEntity.getQualifiedName());
		this.currentScope = s;
	}
	
	
	/**
	* @methodsName: popScope
	* @description: pop scope from stack
	* @param: null
	* @return: void
	*/
	public void popScope() {
//		if(this.currentScope != null) System.out.println("pop scope:" + this.currentScope.getClass().toString() + " " + this.currentScope.getName() + " in " +this.currentFileEntity.getQualifiedName());
		this.currentScope = this.currentScope.getEnclosingScope();
		if(this.currentScope instanceof FunctionSymbol){
			if(((FunctionSymbol)(this.currentScope)).isBaseOverload()){
				popScope();
			}
		}
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
