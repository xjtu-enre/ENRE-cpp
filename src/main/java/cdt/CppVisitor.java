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
		EnumeratorEntity var = context.foundEnumeratorDefinition(enumeratorName, context.getLocation(enumerator));
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
				Location location = context.getLocation(functionDefinition.getDeclarator());
				this.entityrepo.addEntityByLocation(this.entityrepo.getEntity(location.getFile()).getQualifiedName() + location.getStartOffset(),
						functionEntity);
				functionEntity.setLocation(context.getLocation(functionDefinition.getDeclarator()));
				context.entityStack.push(functionEntity);
			}
			else{
				String rawName = declarator.getName().toString();
				IASTDeclSpecifier declSpeci = functionDefinition.getDeclSpecifier();
				String returnType = context.getType(declSpeci);
				List<ParameterEntity> parameterLists = new ArrayList<ParameterEntity>();
				for (IASTNode node : declarator.getChildren()) {
					if (node instanceof IASTParameterDeclaration) {
						ParameterEntity parameter = context.foundParameterDeclaration(((IASTParameterDeclaration) node));
						if (parameter != null) {
							parameterLists.add(parameter);
							parameter.setIndex(parameterLists.size());
						}
					}
				}
				functionEntity = context.foundFunctionDefine(rawName, returnType, context.getLocation(functionDefinition.getDeclarator()), parameterLists, true);
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
			context.foundNewAlias(alias, originalName, context.getLocation(declaration));
		}
		else if (declaration instanceof CPPASTNamespaceAlias) {
			CPPASTNamespaceAlias namespaceAlias = (CPPASTNamespaceAlias) declaration;
			IASTName name = ((CPPASTNamespaceAlias) declaration).getAlias();
			String alias = name.getRawSignature();
			String originalName = namespaceAlias.getMappingName().getRawSignature();
			NamespaceAliasEntity namespaceAliasEntity = context.foundNewNamespaceAlias(alias, originalName, context.getLocation(declaration));
			namespaceAliasEntity.setToNamespaceName(originalName);
		}
		else if(declaration instanceof CPPASTVisibilityLabel) {
			CPPASTVisibilityLabel visibilityLabel = (CPPASTVisibilityLabel)declaration;
			int temp = visibilityLabel.getVisibility();
			// not be public, private or protected
			if(temp > 3 | temp < 1){
				context.foundLabelDefinition(visibilityLabel.getRawSignature().replace(":", ""),
						context.getLocation(visibilityLabel));
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
			}else if(declSpec instanceof CPPASTElaboratedTypeSpecifier) {
				CPPASTElaboratedTypeSpecifier cppastElaboratedTypeSpecifier = (CPPASTElaboratedTypeSpecifier) declSpec;
				int kind = cppastElaboratedTypeSpecifier.getKind();
				switch (kind) {
					case ICPPASTElaboratedTypeSpecifier.k_struct:
					case ICPPASTElaboratedTypeSpecifier.k_union:
					case ICPPASTElaboratedTypeSpecifier.k_class:
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
		if(expression.getRawSignature().contains("g_f32_io_inte_buf")){
			System.out.println(expression.getRawSignature());
			System.out.println("TEST");
		}
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
		return PROCESS_SKIP;
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
		int storageClass = declSpec.getStorageClass();
		/**
		 * 获取信息：如存储类、类型等
		 */
		if(declSpec.getParent() instanceof CPPASTSimpleDeclaration){
			visibility = this.getVisibility((IASTSimpleDeclaration) declSpec.getParent());
			storageClass = declSpec.getStorageClass();
		}
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
				CPPASTElaboratedTypeSpecifier cppastElaboratedTypeSpecifier = (CPPASTElaboratedTypeSpecifier)declSpec;
				int kind = cppastElaboratedTypeSpecifier.getKind();
				for(IASTDeclarator declarator : simpleDeclaration.getDeclarators()){
					String name = declarator.getName().toString();
					switch (kind) {
						case CPPASTElaboratedTypeSpecifier.k_struct:
						case CPPASTElaboratedTypeSpecifier.k_union:
						case CPPASTElaboratedTypeSpecifier.k_class:
							if(declSpec.getParent() instanceof CPPASTSimpleDeclaration){
								String typeName = this.context.getType(declSpec);
								/**
								 * 此处仅仅考虑的是声明类对象。不考虑是否为extern关系
								 */
								this.specifierEntity = context.foundFieldDefinition(name, context.getLocation(declarator),
										typeName, getVisibility((IASTSimpleDeclaration) declSpec.getParent()),0);
								if(declarator instanceof CPPASTDeclarator){
									if(declarator.getPointerOperators().length > 0 ){
										if(this.specifierEntity != null) this.specifierEntity.setPointer();
									}
								}
							}

					}
				}
				for(IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					if(declarator instanceof CPPASTFunctionDeclarator){
						/*
						 * TODO: 抽取extern关系
						 */
						entity = context.foundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier, storageClass);
					}else if(declarator instanceof CPPASTDeclarator){

					}else{
						System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
					}
				}
				// -----------------------------------------
				String methodName = cppastElaboratedTypeSpecifier.getName().toString();
				if(cppastElaboratedTypeSpecifier.getName() instanceof ICPPASTTemplateId) {
					ICPPASTTemplateId templateId = (ICPPASTTemplateId)(cppastElaboratedTypeSpecifier.getName());
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
				boolean isTemplate = context.isTemplate(declSpec);
				switch (kind) {
					case ICPPASTElaboratedTypeSpecifier.k_struct:
						this.specifierEntity = context.foundStructDefinition(methodName,
								context.getLocation(cppastElaboratedTypeSpecifier), isTemplate);
						break;
					case ICPPASTElaboratedTypeSpecifier.k_union:
						this.specifierEntity = context.foundUnionDefinition(methodName,
								context.getLocation(cppastElaboratedTypeSpecifier));
						break;
					case ICPPASTElaboratedTypeSpecifier.k_class:
						this.specifierEntity = context.foundClassDefinition(methodName,
								context.getLocation(cppastElaboratedTypeSpecifier), isTemplate);
				}
			}
			else if(declSpecifier instanceof CPPASTSimpleDeclSpecifier) {
				// TODO: built-in type
				// TODO: IASTSimpleDeclSpecifier.t_char ((CPPASTSimpleDeclSpecifier)declSpecifier).isLongLong()
				for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					if(declarator instanceof CPPASTFunctionDeclarator){
						/*
						 * TODO: 抽取extern关系
						 */
						entity = context.foundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier, storageClass);
					}else if(declarator instanceof CPPASTDeclarator){
						if(declarator.getName() != null){
							String varName = this.resolveEntityName(declarator.getName());
							String type = this.context.getType(declSpecifier);

							entity = context.foundFieldDefinition(varName, context.getLocation(declarator), type, visibility, storageClass);
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
						String type = this.context.getType(declSpecifier);
						entity = context.foundVarDefinition(varName, context.getLocation(declarator.getName()), type, storageClass);
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
						/*
						 * TODO: 抽取extern关系
						 */
						entity = context.foundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier, storageClass);
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
				entity = context.foundEnumDefinition(methodName, context.getLocation(enumerationSpecifier));
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

				boolean isTemplate = context.isTemplate(declSpec);
				switch (type) {
					case IASTCompositeTypeSpecifier.k_struct:
						this.specifierEntity = context.foundStructDefinition(methodName,
								context.getLocation(typeSpecifier), isTemplate);
						break;
					case IASTCompositeTypeSpecifier.k_union:
						this.specifierEntity = context.foundUnionDefinition(methodName,
								context.getLocation(typeSpecifier));
						break;
					case 3:
						this.specifierEntity = context.foundClassDefinition(methodName,
								context.getLocation(typeSpecifier), isTemplate);
				}
				for (ICPPASTBaseSpecifier baseSpecifier : baseSpecifiers) {
					this.specifierEntity.addScopeRelation(RelationType.EXTEND,
							baseSpecifier.getNameSpecifier().toString(),
							this.currentfile.getId(), baseSpecifier.getFileLocation().getStartingLineNumber(),
							baseSpecifier.getFileLocation().getNodeOffset());
				}

			}
			/**
			 * 遍历simpleDeclaration中的每个声明器，找到新的typedef定义
			 *
			 * @param simpleDeclaration 包含所有声明器的声明列表
			 * @param context 上下文对象
			 */
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
				if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
					String varName = declarator.getName().toString();
					if(!(declarator instanceof CPPASTFunctionDeclarator))
						entity = context.foundTypedefDefinition(varName, declSpecifier, context.getLocation(declarator));
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
			entity.setStorageClass(storageClass);
			if(entity instanceof VarEntity & simpleDeclaration.getParent() instanceof CPPASTTranslationUnit){
				entity.setGlobal();
			}
		}
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

	public FileEntity getfile() {
		return this.context.currentFileEntity;
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
