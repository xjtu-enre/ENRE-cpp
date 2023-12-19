package cdt;
import entity.*;
import entity.FieldEntity;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.ProblemType;
import relation.*;
import util.Configure;
import util.Tuple;
import symtab.*;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import util.UnionFind;

import java.util.*;


public class HandlerContext {
	protected EntityRepo entityRepo;
	protected RelationRepo relationRepo;
	protected FileEntity currentFileEntity;
	protected Stack<Entity> entityStack = new Stack<Entity>();
	protected UnionFind overloadSet = new UnionFind();
	Scope currentScope;
	List<Scope> includeScope;
	/*
		currentRelationType 用于处理嵌套表达式里的依赖类型，需要注意在给定值信息后重新设置为空
		currentRelationFromEntityName 用于处理当前最高层表达式所代表的实体名称
	 */
	int currentRelationType = 0;
	String currentRelationFromEntityName = null;
	/*
		nodeRecord 用于记录当前节点是否已经过宏定义展开处理，避免重复处理
	 */
	HashMap<Integer, Boolean> nodeRecord = new HashMap<>();


	public HandlerContext(EntityRepo entityrepo, RelationRepo relationRepo) {
		this.entityRepo = entityrepo;
		this.relationRepo = relationRepo;
		this.entityStack = new Stack<Entity>();
		this.includeScope = new ArrayList<Scope>();
	}

	/**
	 * 用于收集IdExpression的AST访问器
	 */
	private static class IdExpressionCollector extends ASTVisitor {
		private List<IASTIdExpression> idExpressions;

		public IdExpressionCollector() {
			shouldVisitExpressions = true;
			idExpressions = new ArrayList<>();
		}

		@Override
		public int visit(IASTExpression expression) {
			if (expression instanceof IASTIdExpression) {
				idExpressions.add((IASTIdExpression) expression);
			}
			return PROCESS_CONTINUE;
		}

		public List<IASTIdExpression> getIdExpressions() {
			return idExpressions;
		}
	}

	/**
	 *  公共方法，接收一个 IASTExpression 并返回其中的所有 IdExpression 节点
	 *
	 * @param expression 给定的表达式
	 * @return 包含所有ID表达式的列表
	 */
	public List<IASTIdExpression> findAllIdExpressions(IASTExpression expression) {
		IdExpressionCollector collector = new IdExpressionCollector();
		expression.accept(collector);
		return collector.getIdExpressions();
	}

	//
	/**
	 * 用于收集字段引用的AST访问器
	 * 主要类型有：
	 * CPPQualifierType, CPPTypedef, CPPClassType,
	 * CPPASTUnaryExpression, CPPASTIdExpression, CPPASTArraySubscriptExpression,
	 */
	private static class FieldReferenceCollector extends ASTVisitor {
		private List<CPPASTFieldReference> fieldReferences;

		public FieldReferenceCollector() {
			shouldVisitExpressions = true;
			fieldReferences = new ArrayList<>();
		}

		@Override
		public int visit(IASTExpression expression) {
			if (expression instanceof CPPASTFieldReference) {
				CPPASTFieldReference fieldReference = (CPPASTFieldReference)expression;
				IASTName name = fieldReference.getFieldName();
				name.resolveBinding();
//				String bindinginfo1 = getBinding(name);
				ICPPASTExpression expression1 = fieldReference.getFieldOwner();
				IType type = fieldReference.getFieldOwnerType();
				if(expression1 instanceof  CPPASTIdExpression){
					CPPASTIdExpression idExpression2 = (CPPASTIdExpression) expression1;
					idExpression2.getName().resolveBinding();
					String bindinginfo = getBinding(idExpression2.getName());
				}
				fieldReferences.add((CPPASTFieldReference) expression);
			}
			return PROCESS_CONTINUE;
		}

		public List<CPPASTFieldReference> getFieldReferences() {
			return fieldReferences;
		}
	}
	/**
	 * 查找所有字段引用
	 *
	 * @param expression 表达式
	 * @return 返回所有字段引用的列表
	 */
	public List<CPPASTFieldReference> findAllFieldReferences(IASTExpression expression) {
		FieldReferenceCollector collector = new FieldReferenceCollector();
		expression.accept(collector);
		return collector.getFieldReferences();
	}
	/**
	 * 用于收集宏展开信息的私有静态内部类
	 */
	private static class MacroExpansionCollector extends ASTVisitor {

		HashSet<IASTPreprocessorMacroDefinition> macroRecord = new HashSet<>();
		HashMap<Integer, Boolean> nodeRecord = new HashMap<>();

		public MacroExpansionCollector() {
			shouldVisitExpressions = true;
		}

		@Override
		public int visit(IASTExpression expression) {
			int hashcode = expression.hashCode();
			if (nodeRecord.containsKey(hashcode)) {
				return PROCESS_CONTINUE;
			}
			if (expression instanceof IASTIdExpression) {
				IASTIdExpression idExpression = (IASTIdExpression)expression;
				IASTName name = idExpression.getName();
				name.resolveBinding();
				IASTNodeLocation[] iastFileLocation = idExpression.getNodeLocations();
				for(IASTNodeLocation location:iastFileLocation) {
					if (location instanceof IASTMacroExpansionLocation) {
						IASTMacroExpansionLocation macro = (IASTMacroExpansionLocation) location;
						IASTPreprocessorMacroExpansion iastPreprocessorMacroExpansion = macro.getExpansion();
						IASTPreprocessorMacroDefinition iastPreprocessorMacroDefinition = iastPreprocessorMacroExpansion.getMacroDefinition();
						macroRecord.add(iastPreprocessorMacroDefinition);
					}
				}
			}

			return PROCESS_CONTINUE;
		}
		public IASTExpression getParentExpression(IASTExpression expression) {
			IASTNode parent = expression.getParent();
			while(parent.getParent() != null) {
				if (parent instanceof IASTExpression) {
					expression = (IASTExpression) parent;
				}
				parent = parent.getParent();
			}
			return expression;
		}
		public HashSet<IASTPreprocessorMacroDefinition> getMacroRecord() {
			return macroRecord;
		}
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
				null, entityRepo.generateId(),filefullpath, scope);
		entityRepo.add(currentFileEntity);
		entityStack.push(currentFileEntity);
		this.currentScope = scope;
		return currentFileEntity;
	}

	/**
	 * 处理包含范围
	 *
	 * @throws CloneNotSupportedException 如果克隆不支持
	 */
	public void dealIncludeScope() throws CloneNotSupportedException {
		for(FileEntity fileEntity:currentFileEntity.getIncludeEntity()){
			includeScope.add(fileEntity.getScope());
			this.currentScope.union(fileEntity.getScope());
		}
	}

	/**
	 * 将传入的名称解析为完整的名称
	 *
	 * @param Name 待解析的名称
	 * @return 返回解析后的完整名称
	 */
	public String resolveName(String Name) {
		this.currentScope = this.entityStack.peek().getScope();

		if(this.currentScope instanceof Symbol){
			Entity en = this.entityStack.peek();
//			Entity en = entityRepo.getEntity(((Symbol)this.currentScope).getEntityID());
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


	/**
	 * 查找并返回一个函数声明
	 *
	 * @param name 函数名称
	 * @param returnType 返回类型
	 * @param location 函数位置
	 * @param parameterList 参数列表
	 * @return 返回函数实体
	 */
	public FunctionEntity foundFunctionDeclare(String name, String returnType, Location location, List<ParameterEntity> parameterList){
		this.currentScope = this.entityStack.peek().getScope();
		this.currentScope = this.findTheScope(name);
		int id = entityRepo.generateId();

		String[] scopeManages = name.split("::");
		name = scopeManages[scopeManages.length - 1];
		FunctionSymbol symbol = new FunctionSymbol(name, id);
		FunctionEntity functionEntity = new FunctionEntity(name, resolveName(name), this.latestValidContainer(), id, symbol, location);
		functionEntity.addRelation(new Relation(this.latestValidContainer(), functionEntity, RelationType.DECLARE, this.currentFileEntity.getId(),
				location.getStartLine(), location.getStartOffset()));
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
		for(ParameterEntity parameterEntity:parameterList){
			parameterEntity.setParent(functionEntity);
		}
		entityRepo.add(functionEntity);
		return functionEntity;
	}

	/**
	 * 根据给定的名称查找作用域
	 *
	 * @param name 作用域名称
	 * @return 找到的作用域
	 */
	public Scope findTheScope(String name){
		String[] scopeManages = name.split("::");
		if(scopeManages.length == 1) return this.currentScope;
		Scope current = this.currentScope;
		do{
			if( (current.getSymbol(scopeManages[0]) != null) &&
					(current.getSymbol(scopeManages[0]) instanceof Scope)){
				current = (Scope) current.getSymbol(scopeManages[0]);
				break;
			}
			if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
			if(current == current.getEnclosingScope()) break;
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
	/**
	 * 查找函数定义
	 *
	 * @param name 函数名
	 * @param returnType 返回类型
	 * @param location 函数位置
	 * @param parameterList 参数列表
	 * @param isDefine 是否是定义
	 * @return 函数实体
	 */
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
				parameterLists.add(parameterEntity.getType());
			}
			if(scopeFunctionEntity.equals(name, parameterLists)){
				shouldNewEntity = false;
				scopeFunctionEntity.setLocation(location);
				this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), scopeFunctionEntity,
						RelationType.DEFINE, this.currentFileEntity.getId(), scopeFunctionEntity.getStartLine(),
						scopeFunctionEntity.getLocation().getStartOffset()));
				entityStack.push(scopeFunctionEntity);
			}else if(this.overloadSet.isInSet(scopeID)){
				for(Integer id: this.overloadSet.getNodes(scopeID)){
					scopeFunctionEntity = (FunctionEntity) entityRepo.getEntity(id);
					if(scopeFunctionEntity.equals(name, parameterLists)) {
						shouldNewEntity = false;
						scopeFunctionEntity.setLocation(location);
						this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(),
								scopeFunctionEntity, RelationType.DEFINE, this.currentFileEntity.getId(),
								scopeFunctionEntity.getLocation().getStartLine(), scopeFunctionEntity.getLocation().getStartOffset()));
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
					new Relation(this.latestValidContainer(), functionEntity, RelationType.DEFINE,
							this.currentFileEntity.getId(), functionEntity.getLocation().getStartLine(),
							functionEntity.getLocation().getStartOffset()));
			functionEntity.setReturn(returnType);
			entityRepo.add(functionEntity);
			entityStack.push(functionEntity);
		}
		for(ParameterEntity parameterEntity:parameterList){
			parameterEntity.setParent(functionEntity);
		}
		return functionEntity;
	}

	/**
	 * 查找命名空间
	 *
	 * @param namespaceName 命名空间名称
	 * @param startLine     起始行
	 * @param endLine        结束行
	 * @return 返回找到的命名空间实体
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

		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), nsEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), startLine,  -1));
		nsEntity.addScale(endLine - startLine);
		entityStack.push(nsEntity);
		return nsEntity;
	}



	/**
	 * 查找类定义，若找不到则创建一个新的类定义。
	 *
	 * @param ClassName 待查找或创建的类的名称
	 * @param location 类定义所在的位置
	 * @param isTemplate 是否为模板类
	 * @return 返回查找到或创建的类实体
	 */
	public ClassEntity foundClassDefinition(String ClassName, Location location, boolean isTemplate){
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
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), classEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), classEntity.getLocation().getStartLine(), classEntity.getLocation().getStartOffset()));

		classEntity.setTemplate(isTemplate);
		entityRepo.add(classEntity);
		entityStack.push(classEntity);
		return classEntity;
	}

	/**
	 * 根据结构体名称和位置以及是否是模板类型，查找并返回一个结构体实体
	 *
	 * @param StructName 结构体名称
	 * @param location 结构体位置
	 * @param isTemplate 是否是模板类型
	 * @return 返回结构体实体
	 */
	public StructEntity foundStructDefinition(String StructName, Location location, boolean isTemplate) {
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
		structEntity.addRelation(new Relation(this.latestValidContainer(), structEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), structEntity.getLocation().getStartLine(), structEntity.getLocation().getStartOffset()));
		structEntity.setTemplate(isTemplate);
		entityRepo.add(structEntity);
		entityStack.push(structEntity);
		return structEntity;
	}
	
	
	/**
	 * 查找Union定义
	 *
	 * @param UnionName Union名称
	 * @param location 位置
	 * @return UnionEntity对象
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
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), unionEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), unionEntity.getLocation().getStartLine(), unionEntity.getLocation().getStartOffset()));
		entityRepo.add(unionEntity);
		entityStack.push(unionEntity);
		return unionEntity;
	}
	

	/**
	 * 根据枚举名称和位置查找枚举定义
	 *
	 * @param enumName 枚举名称
	 * @param location 位置信息
	 * @return 返回找到的枚举定义
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
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), enumeration, RelationType.DEFINE,
				this.currentFileEntity.getId(), enumeration.getLocation().getStartLine(), enumeration.getLocation().getStartOffset()));
		entityRepo.add(enumeration);
		entityStack.push(enumeration);
		return enumeration;
	}
	
	

	/**
	 * 查找枚举定义
	 *
	 * @param enumeratorName 枚举名称
	 * @param location 位置
	 * @return 枚举实体
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
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), enumertorEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), enumertorEntity.getLocation().getStartLine(), enumertorEntity.getLocation().getStartOffset()));
		return enumertorEntity;
	}


	/**
	 * 处理表达式节点
	 *
	 * @param expression 待处理的表达式节点
	 */
	public void dealExpression(IASTExpression expression) {
		/**
		 * 判断给定的表达式是否已经被处理过，如果未处理过，则对其进行宏展开并添加宏定义和使用的关联关系，并将表达式对应的哈希码添加到nodeRecord中
		 *
		 * @param expression 待处理的表达式
		 */
		int hashcode = expression.hashCode();
		DealMacro:
		if(!nodeRecord.containsKey(hashcode)) {
			IASTNode parent = expression.getParent();
			while(parent != null){
				if(nodeRecord.containsKey(parent.hashCode())){
					nodeRecord.put(hashcode, true);
					break DealMacro;
				}
				parent = parent.getParent();
			}
			MacroExpansionCollector macroExpansion = new MacroExpansionCollector();
			expression.accept(macroExpansion);
			if(macroExpansion.getMacroRecord().size() > 0) {
				HashSet<IASTPreprocessorMacroDefinition> macroSet = macroExpansion.getMacroRecord();
				for(IASTPreprocessorMacroDefinition record : macroSet) {
					record.getFileLocation().getNodeOffset();
					String entityInformation = record.getFileLocation().getFileName() + record.getFileLocation().getNodeOffset();
					int relationType = RelationType.MACRO_USE;
					if(expression.getFileLocation() != null)
						this.latestValidContainer().addBindingRelation(relationType,
								entityInformation, this.currentFileEntity.getId(), expression.getFileLocation().getStartingLineNumber(),
								expression.getFileLocation().getNodeOffset());
				}
			}
			nodeRecord.put(hashcode, true);
		}

		/**
		 * 如果expression是CPPASTBinaryExpression的实例，则执行以下操作：
		 * 如果操作符为op_assign，则处理表达式节点并设置关系为SET。
		 * 如果操作符为op_multiplyAssign、op_divideAssign、op_moduloAssign、op_plusAssign、op_minusAssign、op_shiftLeftAssign、op_shiftRightAssign、op_binaryAndAssign、op_binaryXorAssign、op_binaryOrAssign，则处理表达式节点并分别设置关系为SET和USE。
		 * 如果操作符为op_pmdot或op_pmarrow，则处理表达式节点并设置为UNRESOLVED。
		 * 其他情况下，处理表达式节点并设置为USE。
		 *
		 * @param expression 需要处理的表达式节点
		 * @param relationType 关系类型
		 */
		if(expression instanceof CPPASTBinaryExpression) {
			CPPASTBinaryExpression binaryExp = (CPPASTBinaryExpression)expression;
			if(binaryExp.getOperator() == IASTBinaryExpression.op_assign){
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.SET);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.USE);
			}else if(binaryExp.getOperator() <= IASTBinaryExpression.op_binaryOrAssign &
					binaryExp.getOperator() >= IASTBinaryExpression.op_multiplyAssign){
				// op_multiplyAssign = 18, op_divideAssign = 19, op_moduloAssign = 20, op_plusAssign = 21,
				// op_minusAssign = 22, op_shiftLeftAssign = 23, op_shiftRightAssign = 24,
				// op_binaryAndAssign = 25, op_binaryXorAssign = 26, op_binaryOrAssign = 27
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.SET);
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.USE);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.USE);
			}else if(binaryExp.getOperator() == IASTBinaryExpression.op_pmdot){
				// TODO: operand1.operand2
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.UNRESOLVED);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.UNRESOLVED);
			}else if(binaryExp.getOperator() == IASTBinaryExpression.op_pmarrow){
				// TODO: operand1 -> operand2
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.UNRESOLVED);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.UNRESOLVED);
			}else{
				// *， /， %， +， -， <<， >>， <， >， <=， >=，
				// &， ^， |， &&， ||， ==， !=， .，
				// ->， max()， min()， ...
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.USE);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.USE);
			}
		}
		if(expression instanceof CPPASTCastExpression) {
			CPPASTCastExpression castExp = (CPPASTCastExpression) expression;
			this.dealExpressionNode(castExp.getOperand(), RelationType.CAST);
		}
		if(expression instanceof CPPASTDeleteExpression) {
			CPPASTDeleteExpression deleteExp = (CPPASTDeleteExpression)expression;
			this.dealExpressionNode(deleteExp.getOperand(), RelationType.DELETE);
		}
		if(expression instanceof CPPASTUnaryExpression) {
			CPPASTUnaryExpression unaryExp = (CPPASTUnaryExpression)expression;
			if(unaryExp.getOperator() == IASTUnaryExpression.op_postFixIncr ||     // i++
					unaryExp.getOperator() == IASTUnaryExpression.op_postFixDecr ||   //--
					unaryExp.getOperator() == IASTUnaryExpression.op_tilde || //～
					unaryExp.getOperator() == IASTUnaryExpression.op_prefixIncr || // ++i
					unaryExp.getOperator() == IASTUnaryExpression.op_bracketedPrimary //
			){
				this.dealExpressionNode(unaryExp.getOperand(), RelationType.USE);
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
					this.latestValidContainer().addRelationByObject(fieldName, callFunctionName, RelationType.CALL, this.currentFileEntity.getId(),
							expression.getFileLocation().getStartingLineNumber(), ((CPPASTFunctionCallExpression) expression).getOffset());
				}else if(fieldExpression instanceof CPPASTFieldReference){
					String text = expression.getRawSignature();
				}
			}
			else if(functionNameExpression instanceof CPPASTFunctionCallExpression){}
			else if(functionNameExpression instanceof CPPASTIdExpression){}
			IASTInitializerClause[] iastInitializerClauses = functionCallExpression.getArguments();
			this.dealFunctionExpressionNode(functionCallExpression.getFunctionNameExpression(), RelationType.CALL, iastInitializerClauses);
		}
		if(expression instanceof CPPASTExpressionList) {
			CPPASTExpressionList expressionList = (CPPASTExpressionList)expression;
			for(IASTExpression exp:expressionList.getExpressions()) {
				this.dealExpression(exp);
			}
		}
		if(expression instanceof CPPASTLiteralExpression){}
		if(expression instanceof CPPASTFieldReference){
			CPPASTFieldReference fieldReference = (CPPASTFieldReference)expression;
			IASTExpression expression1 = fieldReference.getFieldOwner();
			IType type = fieldReference.getFieldOwnerType();
			IASTName fieldName = fieldReference.getFieldName();
			String entityInformation = getBinding(fieldName);
			int relationType = RelationType.USE;
			// TODO : 待处理依赖类型
			if(expression.getFileLocation() != null)
				this.latestValidContainer().addBindingRelation(relationType,
					entityInformation, this.currentFileEntity.getId(), expression.getFileLocation().getStartingLineNumber(),
					expression.getFileLocation().getNodeOffset());
		}
		/**
		 * 如果expression是CPPASTConditionalExpression的实例，则处理条件表达式节点
		 *
		 * @param expression 要处理的表达式
		 * @param relationType 关系类型
		 */
		if(expression instanceof CPPASTConditionalExpression) {
			CPPASTConditionalExpression conditionalExpression = (CPPASTConditionalExpression)expression;
			IASTExpression logicalConditionExpression = conditionalExpression.getLogicalConditionExpression();
			this.dealExpressionNode(logicalConditionExpression, RelationType.USE);
			IASTExpression positiveResultExpression = conditionalExpression.getPositiveResultExpression();
			this.dealExpressionNode(positiveResultExpression, RelationType.USE);
			IASTExpression negativeResultExpression = conditionalExpression.getNegativeResultExpression();
			this.dealExpressionNode(negativeResultExpression, RelationType.USE);
		}

	}

	/**
	 * 处理函数表达式节点
	 *
	 * @param expression 表达式
	 * @param expressionType 表达式类型
	 * @param iastInitializerClauses 初始化语句
	 */
	public void dealFunctionExpressionNode(IASTExpression expression,int expressionType, IASTInitializerClause[] iastInitializerClauses){

		if(expression instanceof CPPASTIdExpression) {
			// Begin of arguments processing
			ArrayList<String> arguementsInformation = new ArrayList<>();
			ArrayList<Integer> argReCatInfor = new ArrayList<>();
			ArrayList<Integer> argIndex = new ArrayList<>();

			for(int i=0;i<iastInitializerClauses.length;i++){
				IASTInitializerClause initializerClause = iastInitializerClauses[i];
				List<CPPASTFieldReference> fieldReferences = findAllFieldReferences((IASTExpression) initializerClause);

				if(initializerClause instanceof CPPASTUnaryExpression){
					if(((CPPASTUnaryExpression) initializerClause).getOperator() == IASTUnaryExpression.op_amper){
						if(((CPPASTUnaryExpression) initializerClause).getOperand() instanceof CPPASTIdExpression) {
							arguementsInformation.add( this.getBinding(((CPPASTIdExpression) ((CPPASTUnaryExpression) initializerClause).getOperand()).getName()) );
							argReCatInfor.add( RelationType.ADDR_PARAMETER_USE);
							argIndex.add(i+1);
						}
					}
				}else if(initializerClause instanceof CPPASTIdExpression){
					arguementsInformation.add( this.getBinding(((CPPASTIdExpression)initializerClause).getName()) );
					argReCatInfor.add(RelationType.PARAMETER_USE);
					argIndex.add(i+1);
				}else if(initializerClause instanceof CPPASTFieldReference){
					// 对类或结构体成员变量的访问
					CPPASTFieldReference fieldReference = (CPPASTFieldReference)initializerClause;
					IASTName name = fieldReference.getFieldName();
					name.resolveBinding();
					String binding = this.getBinding(name);
					arguementsInformation.add(binding);
					argReCatInfor.add(RelationType.PARAMETER_USE_FIELD_REFERENCE);
					argIndex.add(i+1);

				}else if(initializerClause instanceof  CPPASTLiteralExpression){
					// 常数
				}else if(initializerClause instanceof CPPASTBinaryExpression){
					// 双目运算符处理，获取内部全部的IDExpression
					List<IASTIdExpression> idExpressions = findAllIdExpressions((IASTExpression) initializerClause);
					for(IASTIdExpression idExpression:idExpressions){
						String binding = this.getBinding(idExpression.getName());
						arguementsInformation.add(binding);
						argReCatInfor.add( RelationType.PARAMETER_USE);
						argIndex.add(i+1);
					}
				}else{
					// System.out.println("Haven't processing to \" " + initializerClause.getClass().toString() + "\" type parameters");
				}
			}
			// End of arguments processing
			String entityInformation = this.getBinding(((CPPASTIdExpression) expression).getName());
			if(expression.getFileLocation() != null){
				if(entityInformation == null) {
					this.latestValidContainer().addScopeRelation(expressionType, expression.getRawSignature(),
							this.currentFileEntity.getId(), expression.getFileLocation().getStartingLineNumber(),
							expression.getFileLocation().getNodeOffset());
					for(int i=0;i<arguementsInformation.size();i++){
					// for(int i=0;i<iastInitializerClauses.length;i++){
						if(arguementsInformation.get(i) != null){
							this.relationRepo.addScopeBindingRelation(new ScopeBindingRelation(
									argReCatInfor.get(i),
									expression.getRawSignature(),
									arguementsInformation.get(i),
									this.currentFileEntity.getId(),
									expression.getFileLocation().getStartingLineNumber(),
									expression.getFileLocation().getNodeOffset(),
									this.latestValidContainer(),
									argIndex.get(i)
									));
						}
					}
				}
				else {
					this.latestValidContainer().addBindingRelation(expressionType,
							entityInformation,
							this.currentFileEntity.getId(), expression.getFileLocation().getStartingLineNumber(),
							expression.getFileLocation().getNodeOffset());
					for(int i=0;i<arguementsInformation.size();i++){
					// for(int i=0;i<iastInitializerClauses.length;i++){
						if(arguementsInformation.get(i) != null){
							this.relationRepo.addBindingRelation(
									new BindingRelation(argReCatInfor.get(i),
										entityInformation, arguementsInformation.get(i),
										this.currentFileEntity.getId(),
										iastInitializerClauses[i].getFileLocation().getStartingLineNumber(),
										iastInitializerClauses[i].getFileLocation().getNodeOffset(),
										argIndex.get(i)
									)
							);
						}
					}
				}

			}

		}
		else if(expression instanceof CPPASTBinaryExpression) {
			CPPASTBinaryExpression binaryExp = (CPPASTBinaryExpression)expression;
			if(binaryExp.getOperator() == IASTBinaryExpression.op_assign){
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.SET);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.USE);
			}else if(binaryExp.getOperator() <= IASTBinaryExpression.op_binaryOrAssign &
					binaryExp.getOperator() >= IASTBinaryExpression.op_multiplyAssign){
				// op_multiplyAssign = 18, op_divideAssign = 19, op_moduloAssign = 20, op_plusAssign = 21,
				// op_minusAssign = 22, op_shiftLeftAssign = 23, op_shiftRightAssign = 24,
				// op_binaryAndAssign = 25, op_binaryXorAssign = 26, op_binaryOrAssign = 27
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.SET);
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.USE);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.USE);
			}else if(binaryExp.getOperator() == IASTBinaryExpression.op_pmdot){
				// TODO: operand1.operand2
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.UNRESOLVED);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.UNRESOLVED);
			}else if(binaryExp.getOperator() == IASTBinaryExpression.op_pmarrow){
				// TODO: operand1 -> operand2
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.UNRESOLVED);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.UNRESOLVED);
			}else{
				// *， /， %， +， -， <<， >>， <， >， <=， >=，
				// &， ^， |， &&， ||， ==， !=， .，
				// ->， max()， min()， ...
				this.dealExpressionNode(binaryExp.getOperand1(), RelationType.USE);
				this.dealExpressionNode(binaryExp.getOperand2(), RelationType.USE);
			}
		}else {
			dealExpression(expression);
		}
	}
	

	/**
	 * 处理表达式节点
	 *
	 * @param expression 表达式
	 * @param expressionType 表达式类型
	 */
	public void dealExpressionNode(IASTExpression expression, int expressionType) {
		/*
			使用currentRelationType进行设置依赖类型
		 */
		if(this.currentRelationType == 0){
			expressionType = currentRelationType;
		}

		if(expression instanceof CPPASTIdExpression) {
			String entityInformation = this.getBinding(((CPPASTIdExpression) expression).getName());
			IASTFileLocation location = expression.getFileLocation();
			if(expression.getFileLocation() != null){
				if(this.currentRelationType == 0){
					this.relationRepo.addBindingRelation(new BindingRelation(expressionType,
							this.currentRelationFromEntityName, entityInformation,
							this.currentFileEntity.getId(),
							expression.getFileLocation().getStartingLineNumber(),
							expression.getFileLocation().getNodeOffset()));
				}
				else if(entityInformation == null) {
					this.latestValidContainer().addScopeRelation(expressionType, expression.getRawSignature(),
							this.currentFileEntity.getId(), expression.getFileLocation().getStartingLineNumber(),
							expression.getFileLocation().getNodeOffset());
				}
				else {
					this.latestValidContainer().addBindingRelation(expressionType,
							entityInformation,
							this.currentFileEntity.getId(), expression.getFileLocation().getStartingLineNumber(),
							expression.getFileLocation().getNodeOffset());
				}
			}
		}else if(expression instanceof CPPASTLiteralExpression){
			/*   自然数值  */

		}else {
			dealExpression(expression);
		}
	}
	
	
	/**
	 * 通过binding获取实体信息
	 *
	 * @param name 需要获取信息的CPPASTIdExpression对象
	 * @return 返回字符串，包含文件名和节点偏移量，如果获取失败则返回null
	 */
	public static String getBinding(IASTName name){
		name.resolveBinding();
		IBinding node = name.getBinding();
		if(node == null) {
			return null;
		}
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
			case "class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef":
				definitionNode = ((CPPTypedef)node).getDefinition();
				break;
			case "class org.eclipse.cdt.internal.core.dom.parser.ProblemBinding":
				break;
			default:
				// System.out.println("Haven't resolve binding type: " + node.getClass().toString());
		}
		if(definitionNode != null && definitionNode.getFileLocation() != null) {
			return definitionNode.getFileLocation().getFileName() + definitionNode.getFileLocation().getNodeOffset();
		}
		return null;
	}

	/**
	 * 根据名称查找特定类型的实体
	 *
	 * @param name 实体名称
	 * @return 查找到的实体，若不存在则返回null
	 */
	public Entity findTheTypedEntity(String name){
		if(name == null) return null;
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
	 * 查找变量定义
	 * @description: build var entity
	 * @param varName 变量名
	 * @param location 变量位置
	 * @param type 变量类型
	 * @return 返回VarEntity类型的变量定义
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
			if(this.currentScope instanceof DataAggregateSymbol) {
				if(this.currentScope.getSymbolByKind(varName, Configure.Variable)==null) {
					VariableSymbol v = new VariableSymbol(varName, id);
					this.currentScope.define(v, Configure.Variable);
				}
			}
		}
		if(this.latestValidContainer() instanceof ClassEntity){
			((ClassEntity) this.latestValidContainer()).addContainEntity(id);
		}
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), varEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), varEntity.getLocation().getStartLine(), varEntity.getLocation().getStartOffset()));
		return varEntity;
	}

	/**
	 * 在当前范围内查找给定名称、类型和可见性的字段定义。
	 *
	 * @param varName 字段名称
	 * @param location 字段位置
	 * @param type 字段类型
	 * @param visibility 字段可见性
	 * @return 返回找到的字段实体，如果找不到则返回null
	 */
	public FieldEntity foundFieldDefinition(String varName, Location location, String type, int visibility) {
		this.currentScope = this.entityStack.peek().getScope();
		if(location == null) return null;
		Integer id = entityRepo.generateId();
		String qualifiedName = resolveName(varName);

		FieldEntity entity = new FieldEntity(varName, qualifiedName,  this.latestValidContainer(), id, location, type);
		Entity typeEntity = this.findTheTypedEntity(type);
		if(typeEntity != null){
			entity.setTypeID(typeEntity.getId());
		}
		entity.setVisiblity(visibility);
		entityRepo.add(entity);
		if(this.latestValidContainer() != null) {
			if(this.currentScope instanceof DataAggregateSymbol) {
				if(this.currentScope.getSymbolByKind(varName, Configure.Variable)==null) {
					VariableSymbol v = new VariableSymbol(varName, id);
					this.currentScope.define(v, Configure.Variable);
				}
			}
			this.latestValidContainer().addContainEntity(id);
		}
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), entity, RelationType.DEFINE,
				this.currentFileEntity.getId(), entity.getLocation().getStartLine(), entity.getLocation().getStartOffset()));
		return entity;
	}

	/**
	 * 查找label定义
	 *
	 * @param labelName label名称
	 * @param location label位置
	 * @return 返回label实体，如果找不到则返回null
	 */
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
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), labelEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), labelEntity.getLocation().getStartLine(), labelEntity.getLocation().getStartOffset()));
		return labelEntity;
	}
	
	/**
	 * 构建Typedef实体
	 *
	 * @description: build Typedef entity
	 * @param Name 名称
	 * @param declSpecifier 声明规范
	 * @param location 位置
	 * @return TypedefEntity
	 */
	public TypedefEntity foundTypedefDefinition(String Name, ICPPASTDeclSpecifier declSpecifier, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		Entity parent = this.latestValidContainer();
		String qualifiedName = resolveName(Name);
		if(declSpecifier instanceof CPPASTCompositeTypeSpecifier){
			parent = parent.getParent();
			this.currentScope = this.entityStack.peek().getScope();
			if(this.currentScope instanceof Symbol){
				Entity en = this.entityStack.peek().getParent();
				if(en == null || en instanceof FileEntity|| en.getQualifiedName().equals("")
						|| en.getQualifiedName().equals("[unnamed]")) qualifiedName = Name;
				else qualifiedName =  en.getQualifiedName() + "::" + Name;
			}
		}
		TypedefEntity typedefEntity = new TypedefEntity(Name, qualifiedName, parent,
				entityRepo.generateId(), location);
		entityRepo.add(typedefEntity);
		Integer startLine = -1;
		Integer startOffset = -1;
		if(typedefEntity.getLocation() != null){
			startLine = typedefEntity.getLocation().getStartLine();
			startOffset = typedefEntity.getLocation().getStartOffset();
		}
		this.latestValidContainer().addRelation(new Relation(parent, typedefEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), startLine, startOffset));
		return typedefEntity;		
	}
	

	/**
	 * 查找新别名
	 *
	 * @param aliasName 别名名称
	 * @param originalName 原始名称
	 * @param location 位置
	 * @return 返回找到的别名实体，如果找不到则返回null
	 */
	public AliasEntity foundNewAlias(String aliasName, String originalName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		if (aliasName.equals(originalName)) return null;
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, 
				this.resolveName(aliasName), this.latestValidContainer(),
				entityRepo.generateId(), originalName, location );
		currentTypeEntity.addBindingRelation(RelationType.ALIAS,  originalName,
				this.currentFileEntity.getId(), location.getStartLine(), location.getStartOffset());
		this.latestValidContainer().addRelation(new Relation(this.latestValidContainer(), currentTypeEntity, RelationType.DEFINE,
				this.currentFileEntity.getId(), currentTypeEntity.getLocation().getStartLine(), currentTypeEntity.getLocation().getStartOffset()));
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}

	/**
	 * 创建新的命名空间别名的实体
	 *
	 * @param aliasName 别名
	 * @param originalName 原始名称
	 * @param location 位置信息
	 * @return 返回新创建的命名空间别名的实体，如果别名和原始名称相同则返回null
	 */
	public NamespaceAliasEntity foundNewNamespaceAlias(String aliasName, String originalName, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		if (aliasName.equals(originalName)) return null;
		NamespaceAliasEntity currentTypeEntity = new NamespaceAliasEntity(aliasName,
				this.resolveName(aliasName), this.latestValidContainer(),
				entityRepo.generateId(), originalName, location);
		currentTypeEntity.addBindingRelation(RelationType.ALIAS,  originalName,
				this.currentFileEntity.getId(), location.getStartLine(), location.getStartOffset());
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;
	}
	

	/**
	 * 创建新的别名并返回一个AliasEntity对象
	 *
	 * @param aliasName 别名名称
	 * @param referToEntity 别名所指向的实体
	 * @param location 别名的位置信息
	 * @return 返回一个AliasEntity对象，表示新创建的别名
	 */
	public AliasEntity foundNewAlias(String aliasName, Entity referToEntity, Location location) {
		this.currentScope = this.entityStack.peek().getScope();
		AliasEntity currentTypeEntity = new AliasEntity(aliasName, resolveName(aliasName),this.latestValidContainer(),
				entityRepo.generateId(),aliasName, location);
		currentTypeEntity.setReferToEntity(referToEntity);
		entityRepo.add(currentTypeEntity);
		return currentTypeEntity;		
	}


	/**
	 * 找到代码作用域
	 *
	 * @param statement 包含代码范围的语句
	 */
	public void foundCodeScope(IASTStatement statement) {
		BaseScope scope = new LocalScope(this.currentScope);
		BlockEntity entity = new BlockEntity("", "", this.latestValidContainer(),
				-1, scope, new Location(-1, -1, -1, -1,
				this.currentFileEntity.getId()));
	}

	/**
	 * 返回最新的FunctionEntity实体
	 *
	 * @return 如果实体栈中有FunctionEntity，则返回该实体；否则返回null
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
	 * 返回最新有效的容器实体
	 *
	 * @return 返回最新有效的容器实体，如果栈中没有有效的容器实体，则返回null
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
	 * 返回栈顶的实体对象，并从栈中弹出。
	 *
	 * @return 返回栈顶的实体对象，如果栈为空则返回null。
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

	
}
