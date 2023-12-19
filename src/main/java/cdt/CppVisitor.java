package cdt;

import entity.*;
import macro.MacroRepo;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.cpp.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.cdt.internal.core.model.CElement;
import org.eclipse.cdt.internal.core.model.CElementInfo;
import org.eclipse.cdt.internal.core.model.TranslationUnit;
import relation.Relation;
import relation.RelationRepo;
import relation.RelationType;
import relation.ScopeRelation;
import util.BuiltInDeal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CppVisitor extends ASTVisitor {
	/**
	 * 实体仓库
	 */
	private EntityRepo entityrepo;
	/**
	 * 私有成员变量relationRepo，表示关系仓库
	 */
	private RelationRepo relationRepo;
	/**
	 * 当前文件实体
	 */
	private FileEntity currentfile;
	/**
	 * 私有成员变量，HandlerContext类型的上下文对象
	 */
	private HandlerContext context;
	private MacroRepo macrorepo;
	private Entity specifierEntity = null;

	public CppVisitor(EntityRepo entityrepo, RelationRepo relationrepo, String filefullpath) {
		this.shouldVisitAmbiguousNodes = true;
		this.shouldVisitArrayModifiers = true;
		this.shouldVisitAttributes = true;
		this.shouldVisitBaseSpecifiers = true;
		this.shouldVisitCaptures = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitDeclarators = true;
		this.shouldVisitDeclSpecifiers = true;
		this.shouldVisitDecltypeSpecifiers = true;
		this.shouldVisitDesignators = true;
		this.shouldVisitEnumerators = true;
		this.shouldVisitExpressions = true;
//		// TODO: should be try and throw exception
//		// this.shouldVisitImplicitDestructorNames = true;
		this.shouldVisitImplicitNameAlternates = true;
//		this.shouldVisitImplicitNames = true;
		this.shouldVisitInitializers = true;
		this.shouldVisitNames = true;
		this.shouldVisitNamespaces = true;
		this.shouldVisitParameterDeclarations = true;
		this.shouldVisitPointerOperators = true;
		this.shouldVisitProblems = true;
		this.shouldVisitStatements = true;
		this.shouldVisitTemplateParameters = true;
		this.shouldVisitTokens = true;
		this.shouldVisitTranslationUnit = true;
		this.shouldVisitTypeIds = true;
		this.shouldVisitVirtSpecifiers = true;
		this.entityrepo = entityrepo;
		this.relationRepo = relationrepo;
		this.context = new HandlerContext(entityrepo, relationrepo);
		this.currentfile = context.makeFile(filefullpath);
	}

	@Override
	public int visit(IASTTranslationUnit tu) {
		try {
			this.context.dealIncludeScope();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return super.visit(tu);
	}

	@Override
	public int visit(IASTProblem problem) {
		return super.visit(problem);
	}

	/**
	 * 访问ICPPASTNamespaceDefinition节点
	 *
	 * @param namespaceDefinition 要访问的节点
	 * @return 访问结果
	 */
	@Override
	public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
		if(!namespaceDefinition.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.visit(namespaceDefinition);
		String namespaceName = namespaceDefinition.getName().toString();
		if(namespaceName.equals("")) namespaceName = "[unnamed]";
		try{
			context.foundNamespace(namespaceName,
					namespaceDefinition.getFileLocation().getStartingLineNumber(),
					namespaceDefinition.getFileLocation().getEndingLineNumber());
		}catch(NullPointerException exception){

		}

		return super.visit(namespaceDefinition);
	}

	@Override
	public int leave(ICPPASTNamespaceDefinition namespaceDefinition) {
		if(!namespaceDefinition.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.leave(namespaceDefinition);
		if(namespaceDefinition.getName().toString().length() == 0) return super.leave(namespaceDefinition);
		context.exitLastedEntity();
		context.popScope();
		return super.leave(namespaceDefinition);
	}

	/**
	 * 访问枚举器节点
	 *
	 * @param enumerator 枚举器节点
	 * @return 访问结果
	 */
	@Override
	public int visit(IASTEnumerator enumerator) {
		if(!enumerator.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.visit(enumerator);
		String enumeratorName = enumerator.getName().toString();
		EnumeratorEntity var = context.foundEnumeratorDefinition(enumeratorName, getLocation(enumerator));
		return super.visit(enumerator);
	}


	/**
	 * 访问AST语句节点
	 * 主要处理函数定义、if语句、for语句、while语句、switch语句、return语句
	 * 获取set, use, try block等依赖关系
	 * @param statement 要访问的AST语句节点
	 * @return 访问结果
	 */
	@Override
	public int visit(IASTStatement statement) {
		if(!statement.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.visit(statement);
		if (statement instanceof IASTForStatement || statement instanceof IASTIfStatement
				|| statement instanceof IASTWhileStatement || statement instanceof IASTSwitchStatement) {
			context.foundCodeScope(statement);
		}
		if(statement instanceof CPPASTReturnStatement){
			if(((CPPASTReturnStatement) statement).getReturnArgument() instanceof CPPASTIdExpression){
				context.dealExpressionNode((IASTExpression) ((CPPASTReturnStatement) statement).getReturnArgument(),
						RelationType.USE);
			}
		}
		return super.visit(statement);
	}

	@Override
	public int leave(IASTStatement statement) {
		if(!statement.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.leave(statement);
		if (statement instanceof IASTForStatement || statement instanceof IASTIfStatement
				|| statement instanceof IASTWhileStatement || statement instanceof IASTSwitchStatement) {
			context.popScope();
		}
		return super.leave(statement);
	}


	/**
	 * 访问IASTDeclaration类型的节点
	 * 主要处理using声明、函数定义、变量声明、结构体定义等
	 * @param declaration IASTDeclaration类型的节点
	 * @return 返回int类型的值
	 */
	@Override
	public int visit(IASTDeclaration declaration) {
		if(!declaration.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.visit(declaration);
		if (declaration instanceof ICPPASTUsingDeclaration) {
			String ns = ((ICPPASTUsingDeclaration) declaration).getName().toString();
			context.latestValidContainer().setUsing(ns, this.currentfile.getId(), declaration.getFileLocation().getStartingLineNumber(),
					declaration.getFileLocation().getNodeOffset());
		}
		else if (declaration instanceof ICPPASTUsingDirective) {
			// using namespace xxx
			String ns = ((ICPPASTUsingDirective) declaration).getQualifiedName().toString();
			context.latestValidContainer().setUsing(ns, this.currentfile.getId(), declaration.getFileLocation().getStartingLineNumber(),
					declaration.getFileLocation().getNodeOffset());
		}
		else if (declaration instanceof IASTSimpleDeclaration) {
			dealWithSimpleDeclaration((IASTSimpleDeclaration) declaration);
		}
		else if (declaration instanceof IASTFunctionDefinition) {
			FunctionEntity functionEntity = null;
			IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) declaration;
			IASTDeclarator declarator = functionDefinition.getDeclarator();

			IASTName cppastName = declarator.getName();
			cppastName.resolveBinding();
			IBinding iBinding = cppastName.getBinding();
			IASTDeclarator[] declarators = null;
			if(iBinding instanceof  CPPFunction){
				CPPFunction cppFunction = (CPPFunction)iBinding;
				cppFunction.isStatic();
				declarators = cppFunction.getDeclarations();
			}
			if(declarators != null){
				for(IASTDeclarator declarator1:declarators){
					String name = declarator1.getFileLocation().getFileName() +
							declarator1.getFileLocation().getNodeOffset();
					Entity entityByLocation = this.entityrepo.getEntityByLocation(name);
					if(entityByLocation instanceof FunctionEntity)
						functionEntity = (FunctionEntity) entityByLocation;
				}
			}
			if(functionEntity != null){
				functionEntity.setLocation(getLocation(functionDefinition.getDeclarator()));
				context.entityStack.push(functionEntity);
			}
			else{
				String rawName = declarator.getName().toString();
				IASTDeclSpecifier declSpeci = functionDefinition.getDeclSpecifier();
				String returnType = getType(declSpeci);
				List<ParameterEntity> parameterLists = new ArrayList<ParameterEntity>();
				for (IASTNode node : declarator.getChildren()) {
					if (node instanceof IASTParameterDeclaration) {
						ParameterEntity parameter = foundParameterDeclaration(((IASTParameterDeclaration) node));
						if (parameter != null) {
							parameterLists.add(parameter);
							parameter.setIndex(parameterLists.size());
						}
					}
				}
				functionEntity = context.foundFunctionDefine(rawName, returnType, getLocation(functionDefinition.getDeclarator()), parameterLists, true);
			}
			int visibility = this.getVisibility(functionDefinition);
			if(functionEntity != null) functionEntity.setVisiblity(visibility);
			if(declaration != null){
				if (declaration.getParent() instanceof CPPASTTemplateDeclaration) {
					if(functionEntity != null) functionEntity.setTemplate(true);
				}
			}
		}
		else if (declaration instanceof ICPPASTAliasDeclaration) {
			ICPPASTAliasDeclaration aliasDeclaration = (ICPPASTAliasDeclaration) declaration;
			String alias = aliasDeclaration.getAlias().toString();
			String originalName = aliasDeclaration.getMappingTypeId().getRawSignature();
			context.foundNewAlias(alias, originalName, getLocation(declaration));
		}
		else if (declaration instanceof CPPASTNamespaceAlias) {
			CPPASTNamespaceAlias namespaceAlias = (CPPASTNamespaceAlias) declaration;
			IASTName name = ((CPPASTNamespaceAlias) declaration).getAlias();
			String alias = name.getRawSignature();
			String originalName = namespaceAlias.getMappingName().getRawSignature();
			NamespaceAliasEntity namespaceAliasEntity = context.foundNewNamespaceAlias(alias, originalName, getLocation(declaration));
			namespaceAliasEntity.setToNamespaceName(originalName);
		}
		else if(declaration instanceof CPPASTVisibilityLabel) {
			CPPASTVisibilityLabel visibilityLabel = (CPPASTVisibilityLabel)declaration;
			int temp = visibilityLabel.getVisibility();
			// not be public, private or protected
			if(temp > 3 | temp < 1){
				context.foundLabelDefinition(visibilityLabel.getRawSignature().replace(":", ""),
						getLocation(visibilityLabel));
			}
		}
		else if (declaration instanceof CPPASTLinkageSpecification) {
		}
		else if (declaration instanceof CPPASTProblemDeclaration) {

		}else if (declaration instanceof CPPASTStaticAssertionDeclaration) {

		}else if (declaration instanceof CPPASTTemplateSpecialization){
		}else if(declaration instanceof CPPASTTemplateDeclaration){
		}
		return super.visit(declaration);
	}

	/**
	 * 根据给定的函数声明器和函数声明，找到相应的函数实体。
	 *
	 * @param declarator 函数声明器
	 * @param declSpecifier 函数声明
	 * @return 找到的函数实体
	 */
	public FunctionEntity FoundFunctionDeclaration(CPPASTFunctionDeclarator declarator, ICPPASTDeclSpecifier declSpecifier){
		FunctionEntity functionEntity = null;
		IASTName functionName = declarator.getName();
		functionName.resolveBinding();
		IBinding iBinding = functionName.getBinding();
		IASTDeclarator[] declarators = null;
		if(iBinding instanceof CPPFunction){
			CPPFunction cppFunction = (CPPFunction)iBinding;
			cppFunction.isStatic();
			declarators = cppFunction.getDeclarations();
		}
		if(declarators != null){
			for(IASTDeclarator declarator1:declarators){
				String name = declarator1.getFileLocation().getFileName() +
						declarator1.getFileLocation().getNodeOffset();
				Entity entityByLocation = this.entityrepo.getEntityByLocation(name);
				if(entityByLocation instanceof FunctionEntity){
					functionEntity = (FunctionEntity) entityByLocation;
					functionEntity.addRelation(new Relation(context.latestValidContainer(), functionEntity, RelationType.DECLARE, context.currentFileEntity.getId(),
							declarator1.getFileLocation().getStartingLineNumber(), declarator1.getFileLocation().getNodeOffset()));
					return functionEntity;
				}
			}
		}

		List<ParameterEntity> parameterLists = new ArrayList<ParameterEntity>();
		for(IASTNode node:declarator.getChildren()){
			if(node instanceof  IASTParameterDeclaration){
				ParameterEntity parameter = foundParameterDeclaration(((IASTParameterDeclaration) node));
				if (parameter != null) {
					parameterLists.add(parameter);
					parameter.setIndex(parameterLists.size());
				}
			}
		}
		if (declarator instanceof IASTFunctionDeclarator) {
			String rawName = declarator.getName().toString();
			String returnType = getType(declSpecifier);
			// function pointer
			if (declarator.getName().toString().equals("")) {
				for (IASTNode node : declarator.getChildren()) {
					if (node instanceof CPPASTDeclarator) {
						rawName = ((CPPASTDeclarator) node).getName().toString();
						functionEntity = context.foundFunctionDeclare(rawName, returnType, getLocation(declarator), parameterLists);
						IASTDeclarator declarator1 = declarator.getNestedDeclarator();
						if(declarator1.getPointerOperators().length > 0 ){
							functionEntity.setPointer();
						}
					}
				}
			}else{
				functionEntity = context.foundFunctionDeclare(rawName, returnType, getLocation(declarator), parameterLists);
				if(declarator instanceof CPPASTDeclarator){
//					if(declarator.getPointerOperators().length > 0 ){
//						functionEntity.setPointer();
//					}
				}
			}
			if(functionEntity != null){
				if(declarator.isPureVirtual()) functionEntity.setPureVirtual();
				if(isTemplate(declarator)) functionEntity.setTemplate(true);
				functionEntity.setStorage_class(declSpecifier.getStorageClass());
				return functionEntity;
			}
		}

		return null;
	}

	@Override
	public int leave(IASTDeclaration declaration) {
		if(!declaration.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
			return super.leave(declaration);
		if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();

			if (declSpec instanceof IASTEnumerationSpecifier) {
				context.popScope();
				context.exitLastedEntity();
			}
			else if (declSpec instanceof CPPASTCompositeTypeSpecifier) {
				int type = ((CPPASTCompositeTypeSpecifier) declSpec).getKey();
				if (type == 1 || type == 2 || type == 3) {
					context.popScope();
					context.exitLastedEntity();
				}
			}
		} else if (declaration instanceof IASTFunctionDefinition) {
			// function definition
			context.exitLastedEntity();
			context.popScope();
		}
		return super.leave(declaration);
	}

	/**
	 * 访问IASTExpression表达式
	 * 调用context的dealExpression方法进行进一步处理
	 * @param expression 要访问的表达式
	 * @return 访问结果，PROCESS_CONTINUE表示继续访问
	 */
	@Override
	public int visit(IASTExpression expression) {
		if(expression.getFileLocation()!=null)
			if(!expression.getFileLocation().getFileName().equals(this.currentfile.getQualifiedName()))
				return super.visit(expression);
		if(expression instanceof CPPASTLambdaExpression){
			CPPASTLambdaExpression lambdaExpression = (CPPASTLambdaExpression) expression;
		}
		else{
			if(expression instanceof CPPASTFunctionCallExpression){
//				System.out.println(expression.getRawSignature());
//				this.showASTNode(expression, 0);
			}
			context.dealExpression(expression);
		}
		return PROCESS_CONTINUE;
	}

	/**
	 * 处理简单声明
	 * 分为两个部分：Specifier 和 Declarator
	 * 1. Specifier
	 * 		1.1 访问Specifier，获取存储类、类型
	 * 		1.2 根据Specifier的类型区分实体类型
	 * 	2 访问Declarator，获取变量名
	 * @param simpleDeclaration 简单声明
	 */
	public void dealWithSimpleDeclaration(IASTSimpleDeclaration simpleDeclaration){
		IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
		int visibility = this.getVisibility(simpleDeclaration);
		int storage_class = declSpec.getStorageClass();
		Entity entity = null;
		if(declSpec instanceof ICPPASTDeclSpecifier) {
			ICPPASTDeclSpecifier declSpecifier = (ICPPASTDeclSpecifier) declSpec;
			if(declSpecifier instanceof CPPASTElaboratedTypeSpecifier) {
				if(declSpecifier.isFriend()) {
					String className = ((CPPASTElaboratedTypeSpecifier) declSpec).getName().toString();
					if(context.latestValidContainer() instanceof ClassEntity) {
						((ClassEntity) context.latestValidContainer()).addFriendClass(new ScopeRelation(
								context.latestValidContainer(), className, RelationType.FRIEND, this.currentfile.getId(),
								declSpec.getFileLocation().getStartingLineNumber(), declSpec.getFileLocation().getNodeOffset()
						));
					}
				}
				CPPASTElaboratedTypeSpecifier elaboratedTypeSpecifier = (CPPASTElaboratedTypeSpecifier)declSpec;
				int type = elaboratedTypeSpecifier.getKind();
				for(IASTDeclarator declarator : simpleDeclaration.getDeclarators()){
					String name = declarator.getName().toString();
					switch (type) {
						case CPPASTElaboratedTypeSpecifier.k_struct:
						case CPPASTElaboratedTypeSpecifier.k_union:
						case CPPASTElaboratedTypeSpecifier.k_class:
							if(declSpec.getParent() instanceof CPPASTSimpleDeclaration){
								String typeName = this.getType(declSpec);
								this.specifierEntity = context.foundFieldDefinition(name, getLocation(declarator),
										typeName, getVisibility((IASTSimpleDeclaration) declSpec.getParent()));
								if(declarator instanceof CPPASTDeclarator){
									if(declarator.getPointerOperators().length > 0 ){
										if(this.specifierEntity != null) this.specifierEntity.setPointer();
									}
								}
							}

					}
				}
				for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					if(declarator instanceof CPPASTFunctionDeclarator){
						entity = this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
					}else if(declarator instanceof CPPASTDeclarator){

					}else{
						System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
					}
				}
			}
			else if(declSpecifier instanceof CPPASTSimpleDeclSpecifier) {
				// TODO: built-in type
				// TODO: IASTSimpleDeclSpecifier.t_char ((CPPASTSimpleDeclSpecifier)declSpecifier).isLongLong()
				for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					if(declarator instanceof CPPASTFunctionDeclarator){
						entity = this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
					}else if(declarator instanceof CPPASTDeclarator){
						if(declarator.getName() != null){
							String varName = this.resolveEntityName(declarator.getName());
							String type = this.getType(declSpecifier);
							entity = context.foundFieldDefinition(varName, getLocation(declarator), type, visibility);
							if(declarator.getInitializer() instanceof CPPASTEqualsInitializer){
								entity.addRelation(new Relation(entity.getParent(), entity, RelationType.SET, currentfile.getId(), entity.getStartLine(), entity.getLocation().getStartOffset()));
							}
						}
					}else{
						System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
					}
				}
				if(declSpecifier.isFriend()) {
					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
						String className = declarator.getName().toString();
						if(context.latestValidContainer() instanceof ClassEntity) {
							((ClassEntity) context.latestValidContainer()).addFriendFunction(new ScopeRelation(
									context.latestValidContainer(), className, RelationType.FRIEND, this.currentfile.getId(),
									declSpec.getFileLocation().getStartingLineNumber(), declSpec.getFileLocation().getNodeOffset()
							));
						}
					}
				}
			}
			else if(declSpecifier instanceof CPPASTNamedTypeSpecifier) {
				if(declSpecifier.isFriend()) {
					String className = ((CPPASTNamedTypeSpecifier) declSpec).getName().toString();
					if(context.latestValidContainer() instanceof ClassEntity) {
						((ClassEntity) context.latestValidContainer()).addFriendClass(new ScopeRelation(
								context.latestValidContainer(), className, RelationType.FRIEND, this.currentfile.getId(),
								declSpec.getFileLocation().getStartingLineNumber(), declSpec.getFileLocation().getNodeOffset()
						));
					}
				}
				else {
					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
						if(declarator instanceof CPPASTFunctionDeclarator){
							continue;
						}
						String varName = this.resolveEntityName(declarator.getName());
						String type = this.getType(declSpecifier);
						entity = context.foundVarDefinition(varName, getLocation(declarator.getName()), type);
						if(declSpecifier instanceof CPPASTNamedTypeSpecifier){
							IBinding[] bindings = ((CPPASTNamedTypeSpecifier) declSpecifier).findBindings(((CPPASTNamedTypeSpecifier) declSpecifier).getName(), true);
							for(IBinding binding:bindings){
								if(binding instanceof CPPTypedef){
									IASTNode definition = ((CPPTypedef) binding).getDefinition();
									if(definition instanceof CPPASTName){
										if(entity != null) entity.addScopeRelation(RelationType.TYPE, definition.toString(),
												this.currentfile.getId(), declarator.getFileLocation().getStartingLineNumber(),
												declarator.getFileLocation().getNodeOffset());
									}
								}
							}
						}
					}
				}
				for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					if(declarator instanceof CPPASTFunctionDeclarator){
						entity = this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
					}else if(declarator instanceof CPPASTDeclarator){

					}else{
						System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
					}
				}
			}
			else if(declSpecifier instanceof CPPASTEnumerationSpecifier) {
				IASTEnumerationSpecifier enumerationSpecifier = (IASTEnumerationSpecifier) declSpec;
				String methodName = enumerationSpecifier.getName().toString();
				if (methodName.equals(""))
					if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
						if (simpleDeclaration.getDeclarators().length > 0) methodName = simpleDeclaration.getDeclarators()[0].getName().toString();
						else
							methodName = "[unnamed]";
					}else
						methodName = "[unnamed]";
				entity = context.foundEnumDefinition(methodName, getLocation(enumerationSpecifier));
			}
			else if(declSpecifier instanceof CPPASTCompositeTypeSpecifier) {
				// Extract Class/Struct/Union Entities
				CPPASTCompositeTypeSpecifier typeSpecifier = (CPPASTCompositeTypeSpecifier) declSpec;
				int type = typeSpecifier.getKey();
				String methodName = typeSpecifier.getName().toString();
				if(typeSpecifier.getName() instanceof ICPPASTTemplateId) {
					ICPPASTTemplateId templateId = (ICPPASTTemplateId)(typeSpecifier.getName());
					methodName = templateId.getTemplateName().toString();
				}
				if(methodName.equals("")){
					if(declSpec != null){
						if(declSpec.getParent() instanceof CPPASTSimpleDeclaration && declSpec.getStorageClass() == IASTDeclSpecifier.sc_typedef){
							if(simpleDeclaration.getDeclarators().length == 1){
								methodName = simpleDeclaration.getDeclarators()[0].getName().toString();
							}
						}else methodName = "[unnamed]";
					}
				}
				ArrayList<String> baseName = new ArrayList<String>();
				ICPPASTBaseSpecifier[] baseSpecifiers = ((CPPASTCompositeTypeSpecifier) declSpec).getBaseSpecifiers();

				boolean isTemplate = isTemplate(declSpec);
				switch (type) {
					case IASTCompositeTypeSpecifier.k_struct:
						this.specifierEntity = context.foundStructDefinition(methodName,
								getLocation(typeSpecifier), isTemplate);
						break;
					case IASTCompositeTypeSpecifier.k_union:
						this.specifierEntity = context.foundUnionDefinition(methodName,
								getLocation(typeSpecifier));
						break;
					case 3:
						this.specifierEntity = context.foundClassDefinition(methodName,
								getLocation(typeSpecifier), isTemplate);
				}
				for (ICPPASTBaseSpecifier baseSpecifier : baseSpecifiers) {
					this.specifierEntity.addScopeRelation(RelationType.EXTEND,
							baseSpecifier.getNameSpecifier().toString(),
							 this.currentfile.getId(), baseSpecifier.getFileLocation().getStartingLineNumber(),
							baseSpecifier.getFileLocation().getNodeOffset());
				}
				if(declSpec.getParent() instanceof CPPASTSimpleDeclaration){
					if(this.specifierEntity != null){
						visibility = this.getVisibility((IASTSimpleDeclaration) declSpec.getParent());
						storage_class = declSpec.getStorageClass();
						this.specifierEntity.setStorage_class(storage_class);
						this.specifierEntity.setVisiblity(visibility);
					}
				}
			}
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
				// Found new typedef definition
				if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
//					String varType = declSpecifier.getRawSignature().toString();
					String varName = declarator.getName().toString();
					if(!(declarator instanceof CPPASTFunctionDeclarator))
						entity = context.foundTypedefDefinition(varName, declSpecifier, getLocation(declarator));
				} else if (!(declarator instanceof IASTFunctionDeclarator)) {
					String varType = declSpecifier.getRawSignature().toString();
					String varName = declarator.getName().toString();
				}
				if(declarator instanceof CPPASTDeclarator){
					if(declarator.getPointerOperators().length > 0 ){
						if(entity != null && !(entity instanceof FunctionEntity)) entity.setPointer();
					}
				}
			}
		}
		if(entity!=null){
			entity.setVisiblity(visibility);
			entity.setStorage_class(storage_class);
			if(entity instanceof VarEntity & simpleDeclaration.getParent() instanceof CPPASTTranslationUnit){
				entity.setGlobal();
			}
		}
	}

	/**
	 * 找到参数声明，返回ParameterEntity对象
	 *
	 * @param parameterDeclaration 参数声明
	 * @return 返回ParameterEntity对象
	 */
	public ParameterEntity foundParameterDeclaration(IASTParameterDeclaration parameterDeclaration) {
		ParameterEntity var = null;
		if (parameterDeclaration.getDeclarator() instanceof CPPASTFunctionDeclarator) {
			CPPASTFunctionDeclarator functionDeclarator = (CPPASTFunctionDeclarator) parameterDeclaration.getDeclarator();
			String parameterType = this.getType(parameterDeclaration.getDeclSpecifier());
			if (this.getLocation(functionDeclarator.getName()) != null)
				var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
					null, context.latestValidContainer() , context.entityRepo.generateId(),
					getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
			if(parameterDeclaration != null){
				if (parameterDeclaration.getParent() instanceof IASTStandardFunctionDeclarator) {
					if(context.currentFunction()!=null)
						context.currentFunction().setCallbackCall();
				}
			}

		}
		else {
			if (parameterDeclaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
				String parameterName = parameterDeclaration.getDeclarator().getName().toString();
				String parameterType = this.getType(parameterDeclaration.getDeclSpecifier());
				IASTNode[] declaratorChild = parameterDeclaration.getDeclarator().getChildren();
				if (declaratorChild.length == 1) {
					if (getLocation(parameterDeclaration.getDeclarator().getName()) != null) {
						var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
								parameterDeclaration.getDeclarator().getName().toString(),  context.latestValidContainer(), context.entityRepo.generateId(),
								getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
						entityrepo.add(var);
					}
				}
				else if (declaratorChild.length <= 3) {
					for (IASTNode node : declaratorChild) {
						if (node instanceof CPPASTPointer) {
							if (getLocation(parameterDeclaration.getDeclarator().getName()) != null) {
								var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
										parameterDeclaration.getDeclarator().getName().toString(),  null, context.entityRepo.generateId(),
										getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
								entityrepo.add(var);
								var.setPointer();
							}
							break;
						} else if (node instanceof CPPASTReferenceOperator) {
							var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
									parameterDeclaration.getDeclarator().getName().toString(),  null, context.entityRepo.generateId(),
									getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
							break;
						}else if(node instanceof CPPASTName){
							var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
									parameterDeclaration.getDeclarator().getName().toString(),
									null, context.entityRepo.generateId(),
									getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
						}
					}
				}
			}else{
				String parameterType = this.getType(parameterDeclaration.getDeclSpecifier());
				var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
						parameterDeclaration.getDeclarator().getName().toString(),  null, context.entityRepo.generateId(),
						getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
				if(parameterDeclaration.getDeclarator() instanceof CPPASTDeclarator){
					if(parameterDeclaration.getDeclarator().getPointerOperators().length > 0 ){
						if(var != null) var.setPointer();
					}
				}
				entityrepo.add(var);
			}
		}
		return var;
	}

	/**
	 * 解析实体名称
	 *
	 * @param name 实体名称
	 * @return 解析后的实体名称
	 */
	public String resolveEntityName(IASTName name) {
		String formatName = "";
		String rawName = name.toString();
		if(name instanceof CPPASTName) {
			return name.toString();
		}
		else {
			if(name instanceof CPPASTQualifiedName) {
				CPPASTQualifiedName qualifiedName = (CPPASTQualifiedName)name;
				for(ICPPASTNameSpecifier nameSpecifier:qualifiedName.getAllSegments()) {
					if(nameSpecifier instanceof ICPPASTTemplateId) {
						ICPPASTTemplateId templateId = (ICPPASTTemplateId)nameSpecifier;
						formatName = formatName + "::" + templateId.getTemplateName().toString();
					}
					else if(nameSpecifier instanceof IASTName) {
						formatName = formatName + "::" + nameSpecifier.getRawSignature();
					}

				}
			}
		}
		formatName = formatName.replaceFirst("::", "");
		return formatName;
	}

	/**
	 * 获取传入的IASTDeclSpecifier对象的类型
	 *
	 * @param declSpeci 传入的IASTDeclSpecifier对象
	 * @return 返回对象的类型
	 */
	public String getType(IASTDeclSpecifier declSpeci) {
		String type = null;

		if (declSpeci instanceof IASTCompositeTypeSpecifier) {
			final IASTCompositeTypeSpecifier compositeTypeSpec = (IASTCompositeTypeSpecifier) declSpeci;
		} else if (declSpeci instanceof IASTElaboratedTypeSpecifier) {
			final IASTElaboratedTypeSpecifier elaboratedTypeSpec = (IASTElaboratedTypeSpecifier) declSpeci;
			type = elaboratedTypeSpec.getName().toString();
		} else if (declSpeci instanceof IASTEnumerationSpecifier) {
			final IASTEnumerationSpecifier enumerationSpec = (IASTEnumerationSpecifier) declSpeci;

		} else if (declSpeci instanceof IASTSimpleDeclSpecifier) {
			// No return value or the return value is built-in
			final IASTSimpleDeclSpecifier simple = (IASTSimpleDeclSpecifier) declSpeci;
			type = simple.getRawSignature();
		} else if (declSpeci instanceof IASTNamedTypeSpecifier) {
			final IASTNamedTypeSpecifier namedTypeSpec = (IASTNamedTypeSpecifier) declSpeci;
			type = namedTypeSpec.getName().toString();
		}
		return type;
	}

	public FileEntity getfile() {
		return this.context.currentFileEntity;
	}

	/**
	 * 判断给定的节点是否为模板
	 *
	 * @param node 给定的节点
	 * @return 如果节点是模板，返回true；否则返回false
	 */
	public boolean isTemplate(IASTNode node) {
		if (node instanceof CPPASTTemplateDeclaration)
			return true;
		if(node instanceof CPPASTCompositeTypeSpecifier){
			CPPASTCompositeTypeSpecifier compositeTypeSpecifier = (CPPASTCompositeTypeSpecifier)node;
			if(compositeTypeSpecifier.getParent().getParent() != null){
				if(compositeTypeSpecifier.getParent().getParent() instanceof CPPASTTemplateDeclaration){
					return true;
				}
			}
		}
		if(node instanceof CPPASTDeclarator){
			CPPASTDeclarator declarator = (CPPASTDeclarator)node;
			if(declarator.getParent().getParent() != null){
				if(declarator.getParent().getParent() instanceof CPPASTTemplateDeclaration){
					return true;
				}
			}
		}
		return false;
	}

	public void showASTNode(IASTNode node, int i) {
		System.out.println("layerNo" + i + "  " + node.getClass() + "    " + node.getRawSignature());
		for (IASTNode child : node.getChildren()) {
			System.out.println(child.getClass() + "    " + child.getRawSignature());
		}
		for (IASTNode child : node.getChildren()) {
			showASTNode(child, i + 1);
		}
	}

	/**
	 * 获取给定节点的位置信息
	 *
	 * @param node 给定的节点
	 * @return 返回节点的位置信息，如果节点没有位置信息则返回null
	 */
	public Location getLocation(IASTNode node) {
		if (node.getFileLocation() == null)
			return null;
		return new Location(node.getFileLocation().getNodeLength(), node.getFileLocation().getStartingLineNumber(),
				node.getFileLocation().getEndingLineNumber(), node.getFileLocation().getNodeOffset(),
				this.currentfile.getId());
	}

	/**
	 * 从给定的IASTSimpleDeclaration节点中获取声明的名称
	 *
	 * @param astNode 给定的IASTSimpleDeclaration节点
	 * @return 声明的名称，如果节点为空则返回null
	 */
	public IASTName getDeclarationName(IASTSimpleDeclaration astNode) {
		IASTDeclarator[] declarators = astNode.getDeclarators();
		if(declarators != null && declarators.length > 0){
			return declarators[0].getName();
		} else {
			return null;
		}
	}

	/**
	 * 获取AST节点中声明的可见性
	 *
	 * @param astNode AST节点
	 * @return 返回声明的可见性，若无法获取则返回0
	 */
	public int getVisibility(IASTDeclaration astNode){
		IASTName name = null;
		if(astNode instanceof IASTSimpleDeclaration){
			name = getDeclarationName((IASTSimpleDeclaration) astNode);

		}else if(astNode instanceof CPPASTFunctionDefinition) {
			name = ((CPPASTFunctionDefinition) astNode).getDeclarator().getName();
			CPPASTFunctionDefinition icppastFunctionDefinition = (CPPASTFunctionDefinition) astNode;
		}
		if(name == null){
			return 0;
		} else {
			try {
				IBinding binding = name.resolveBinding();
				if(binding instanceof ICPPMember){
					ICPPMember member = (ICPPMember)binding;
					return member.getVisibility();
				}
//				else if(binding instanceof CPPMethod){
//					System.out.println("CPPMethod");
//				}
				else if(binding instanceof CPPFunction){
					CPPFunction cppFunction = (CPPFunction)binding;
					ICPPFunctionType icppFunctionType = cppFunction.getType();
					if(icppFunctionType instanceof CPPFunctionType){}
				}
			} catch (RuntimeException e){
				return 0;
			}
		}
		return 0;
	}

}
