package cdt;

import entity.*;
import macro.MacroRepo;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.cpp.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.parser.ast.ASTAccessVisibility;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.cdt.internal.core.model.MethodDeclaration;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SimpleTimeZone;


public class CppVisitor extends ASTVisitor {
	private EntityRepo entityrepo;
	private FileEntity currentfile;
	private HandlerContext context;
	private MacroRepo macrorepo;
	private Entity specifierEntity = null;

	public CppVisitor(EntityRepo entityrepo, String filefullpath) {
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
		this.shouldVisitImplicitNames = true;
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
		this.context = new HandlerContext(entityrepo);
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

	// namespace
	@Override
	public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
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
		if(namespaceDefinition.getName().toString().length() == 0) return super.leave(namespaceDefinition);
		context.exitLastedEntity();
		context.popScope();
		return super.leave(namespaceDefinition);
	}

	@Override
	public int visit(IASTDeclSpecifier declSpec) {
		if(declSpec instanceof CPPASTCompositeTypeSpecifier) {
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
						CPPASTSimpleDeclaration simpleDeclaration = (CPPASTSimpleDeclaration)(declSpec.getParent());
						if(simpleDeclaration.getDeclarators().length == 1){
							methodName = simpleDeclaration.getDeclarators()[0].getName().toString();
						}
					}else methodName = "[unnamed]";
				}
			}
			ArrayList<String> baseName = new ArrayList<String>();
			ICPPASTBaseSpecifier[] baseSpecifiers = ((CPPASTCompositeTypeSpecifier) declSpec).getBaseSpecifiers();
			for (ICPPASTBaseSpecifier baseSpecifier : baseSpecifiers) {
				baseName.add(baseSpecifier.getNameSpecifier().toString());
			}
			boolean isTemplate = isTemplate(declSpec);
			switch (type) {
				case IASTCompositeTypeSpecifier.k_struct -> {
					this.specifierEntity = context.foundStructDefinition(methodName, baseName,
							getLocation(typeSpecifier), isTemplate);
				}
				case IASTCompositeTypeSpecifier.k_union -> {
					this.specifierEntity = context.foundUnionDefinition(methodName,
							getLocation(typeSpecifier));
				}
				case 3 -> {
					this.specifierEntity = context.foundClassDefinition(methodName, baseName,
							getLocation(typeSpecifier), isTemplate);
				}
			}
			if(declSpec.getParent() instanceof CPPASTSimpleDeclaration){
				if(this.specifierEntity != null){
					int visibility = this.getVisibility((IASTSimpleDeclaration) declSpec.getParent());
					int storage_class = declSpec.getStorageClass();
					this.specifierEntity.setStorage_class(storage_class);
					this.specifierEntity.setVisiblity(visibility);
				}
			}

		}
		return super.visit(declSpec);
	}

	@Override
	public int leave(IASTDeclSpecifier declSpec) {
		if (declSpec instanceof CPPASTCompositeTypeSpecifier) {
			int type = ((CPPASTCompositeTypeSpecifier) declSpec).getKey();
			if (type == 1 || type == 2 || type == 3) {
				context.popScope();
				this.specifierEntity = context.exitLastedEntity();
			}
		}
		return super.leave(declSpec);
	}


	@Override
	public int visit(IASTEnumerator enumerator) {
		String enumeratorName = enumerator.getName().toString();
		EnumeratorEntity var = context.foundEnumeratorDefinition(enumeratorName, getLocation(enumerator));
		return super.visit(enumerator);
	}


	// set, use, try block
	@Override
	public int visit(IASTStatement statement) {
		if (statement instanceof IASTForStatement || statement instanceof IASTIfStatement
				|| statement instanceof IASTWhileStatement || statement instanceof IASTSwitchStatement) {
			context.foundCodeScope(statement);
		}
		if(statement instanceof CPPASTReturnStatement){
			if(((CPPASTReturnStatement) statement).getReturnArgument() instanceof CPPASTIdExpression){
				context.dealExpressionNode((IASTExpression) ((CPPASTReturnStatement) statement).getReturnArgument(),
						"Use");
			}
		}
		return super.visit(statement);
	}

	@Override
	public int leave(IASTStatement statement) {
		if (statement instanceof IASTForStatement || statement instanceof IASTIfStatement
				|| statement instanceof IASTWhileStatement || statement instanceof IASTSwitchStatement) {
			context.popScope();
		}
		return super.leave(statement);
	}

	@Override
	public int visit(IASTDeclaration declaration) {
		if (declaration instanceof ICPPASTUsingDeclaration) {
			String ns = ((ICPPASTUsingDeclaration) declaration).getName().toString();
			context.foundUsingImport(ns);
		}
		else if (declaration instanceof ICPPASTUsingDirective) {
			// using namespace xxx
			String ns = ((ICPPASTUsingDirective) declaration).getQualifiedName().toString();
			context.foundUsingImport(ns);
		}
		else if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
			int visibility = this.getVisibility(simpleDeclaration);
			int storage_class = declSpec.getStorageClass();
			Entity entity = null;
			if(declSpec instanceof ICPPASTDeclSpecifier) {
				ICPPASTDeclSpecifier declSpecifier = (ICPPASTDeclSpecifier) declSpec;
				if(declSpecifier instanceof CPPASTElaboratedTypeSpecifier) {
					if(declSpecifier.isFriend()) {
						context.addFriendClass(((CPPASTElaboratedTypeSpecifier) declSpec).getName().toString());
					}
					CPPASTElaboratedTypeSpecifier elaboratedTypeSpecifier = (CPPASTElaboratedTypeSpecifier)declSpec;
					int type = elaboratedTypeSpecifier.getKind();
					for(IASTDeclarator declarator : simpleDeclaration.getDeclarators()){
						String name = declarator.getName().toString();
						switch (type) {
							case CPPASTElaboratedTypeSpecifier.k_struct, CPPASTElaboratedTypeSpecifier.k_union, CPPASTElaboratedTypeSpecifier.k_class -> {
								if(declSpec.getParent() instanceof CPPASTSimpleDeclaration){
									this.specifierEntity = context.foundFieldDefinition(name, getLocation(elaboratedTypeSpecifier),
											declSpec.getRawSignature(), getVisibility((IASTSimpleDeclaration) declSpec.getParent()));
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

//					System.out.println(((CPPASTSimpleDeclSpecifier) declSpecifier).isLongLong());
//					System.out.println(((CPPASTSimpleDeclSpecifier) declSpecifier).isLong());
//					System.out.println(((CPPASTSimpleDeclSpecifier) declSpecifier).isInline());
//					System.out.println(IASTSimpleDeclSpecifier.t_char);
//					System.out.println(((CPPASTSimpleDeclSpecifier) declSpecifier).getType());
//					System.out.println(IASTSimpleDeclSpecifier.t_int);
//					System.out.println(IASTSimpleDeclSpecifier.t_int);
					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
						if(declarator instanceof CPPASTFunctionDeclarator){
							entity = this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
						}else if(declarator instanceof CPPASTDeclarator){
							if(declarator.getName() != null){
								String varName = this.resolveEntityName(declarator.getName());
								String type = declSpecifier.getRawSignature();
								visibility = getVisibility((IASTSimpleDeclaration) declaration);
								entity = context.foundFieldDefinition(varName, getLocation(declarator), type, visibility);
								if(declarator.getInitializer() instanceof CPPASTEqualsInitializer){
									CPPASTEqualsInitializer equalsInitializer = (CPPASTEqualsInitializer)(declarator.getInitializer());
									context.dealExpressionNode(equalsInitializer.getExpression(), "Use");
								}
							}

						}else{
							System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
						}
					}
					if(declSpecifier.isFriend()) {
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							context.addFriendFunction(declarator.getName().toString());
						}
					}
				}
				else if(declSpecifier instanceof CPPASTNamedTypeSpecifier) {
					if(declSpecifier.isFriend()) {
						context.addFriendClass(((CPPASTNamedTypeSpecifier) declSpec).getName().toString());
					}
					else {
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = this.resolveEntityName(declarator.getName());
							String type = ((CPPASTNamedTypeSpecifier) declSpecifier).getName().toString();
							entity = context.foundVarDefinition(varName, getLocation(declarator.getName()), type);
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
							if (((IASTSimpleDeclaration) declaration).getDeclarators().length > 0) methodName = ((IASTSimpleDeclaration) declaration).getDeclarators()[0].getName().toString();
							else
								methodName = "[unnamed]";
						}else
							methodName = "[unnamed]";
					entity = context.foundEnumDefinition(methodName, getLocation(enumerationSpecifier));

//					try {
//						// enum with scope: enum class/enum struct
//						if(enumerationSpecifier.getName()!= null & !methodName.equals("")) {
//							if(enumerationSpecifier.getName().getLeadingSyntax()!=null) {
//								if(enumerationSpecifier.getName().getLeadingSyntax().getNext()!=null) {
//									enumentity.setScope(true);
//								}
//							}
//						}
//					} catch (UnsupportedOperationException | ExpansionOverlapsBoundaryException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}

				for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					// Found new typedef definition
					if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
						String varType = declSpecifier.getRawSignature().toString();
						String varName = declarator.getName().toString();
						if(declarator instanceof CPPASTFunctionDeclarator){
							if(declarator.getNestedDeclarator() != null) varName = declarator.getNestedDeclarator().getName().toString();
						}
						entity = context.foundTypedefDefinition(varName, varType, getLocation(declarator));
					} else if (!(declarator instanceof IASTFunctionDeclarator)) {
						String varType = declSpecifier.getRawSignature().toString();
						String varName = declarator.getName().toString();
					}
				}
			}
			if(entity!=null){
				entity.setVisiblity(visibility);
				entity.setStorage_class(storage_class);
			}
		}
		else if (declaration instanceof IASTFunctionDefinition) {
			// function definition
			FunctionEntity functionEntity = null;
			IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) declaration;
			IASTDeclarator declarator = functionDefinition.getDeclarator();
			String rawName = declarator.getName().toString();
			IASTDeclSpecifier declSpeci = functionDefinition.getDeclSpecifier();
			String returnType = getType(declSpeci);
			List<ParameterEntity> parameterLists = new ArrayList<ParameterEntity>();
			for(IASTNode node:declarator.getChildren()){
				if(node instanceof IASTParameterDeclaration){
					ParameterEntity parameter = foundParameterDeclaration(((IASTParameterDeclaration) node));
					if (parameter != null) {
						parameterLists.add(parameter);
					}
				}
			}
			System.out.println(declaration.getRawSignature());
			System.out.println(declSpeci.getRawSignature());
			functionEntity = context.foundFunctionDefine(rawName, returnType, getLocation(functionDefinition.getDeclarator()), parameterLists, true);
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

	public FunctionEntity FoundFunctionDeclaration(CPPASTFunctionDeclarator declarator, ICPPASTDeclSpecifier declSpecifier){
		List<ParameterEntity> parameterLists = new ArrayList<ParameterEntity>();
		for(IASTNode node:declarator.getChildren()){
			if(node instanceof  IASTParameterDeclaration){
				ParameterEntity parameter = foundParameterDeclaration(((IASTParameterDeclaration) node));
				if (parameter != null) {
					parameterLists.add(parameter);
				}
			}
		}
		if (declarator instanceof IASTFunctionDeclarator) {
			// function declarator
			String rawName = declarator.getName().toString();
			String returnType = getType(declSpecifier);
			// function pointer
			if (declarator.getName().toString().equals("")) {
				for (IASTNode node : declarator.getChildren()) {
					if (node instanceof CPPASTDeclarator) {
						rawName = ((CPPASTDeclarator) node).getName().toString();
						VarEntity varEntity = context.foundVarDefinition(rawName, getLocation(declarator), "null");
						varEntity.setPoint(true);
					}
				}
			}
			FunctionEntity functionEntity = context.foundFunctionDeclare(rawName, returnType, getLocation(declarator), parameterLists);
			if(declarator.isPureVirtual()) functionEntity.setPureVirtual();
			if(isTemplate(declarator)) functionEntity.setTemplate(true);
			return functionEntity;
		}
		return null;
	}

	@Override
	public int leave(IASTDeclaration declaration) {
		if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();

			if (declSpec instanceof IASTEnumerationSpecifier) {
				context.popScope();
				context.exitLastedEntity();
			}
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
				// Found new typedef definition
				if (declarator instanceof IASTFunctionDeclarator) {
					// function declarator
//					System.out.println(declarator.getRawSignature());
				}
			}
		} else if (declaration instanceof IASTFunctionDefinition) {
			// function definition
			context.exitLastedEntity();
			context.popScope();
		}
		return super.leave(declaration);
	}

	@Override
	public int visit(IASTExpression expression) {
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

	public ParameterEntity foundParameterDeclaration(IASTParameterDeclaration parameterDeclaration) {
		ParameterEntity var = null;
		if (parameterDeclaration.getDeclarator() instanceof CPPASTFunctionDeclarator) {
			CPPASTFunctionDeclarator functionDeclarator = (CPPASTFunctionDeclarator) parameterDeclaration
					.getDeclarator();
			if (this.getLocation(functionDeclarator.getName()) != null)
				var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
					null,  null, context.entityRepo.generateId(),
					getLocation(parameterDeclaration.getDeclarator().getName()), "");
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
								parameterDeclaration.getDeclarator().getName().toString(),  null, context.entityRepo.generateId(),
								getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
						entityrepo.add(var);
					}
				}
				if (declaratorChild.length <= 3) {
					for (IASTNode node : declaratorChild) {
						if (node instanceof CPPASTPointer) {
							if (getLocation(parameterDeclaration.getDeclarator().getName()) != null) {
								var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
										parameterDeclaration.getDeclarator().getName().toString(),  null, context.entityRepo.generateId(),
										getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
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
						}else{
//							System.out.println(parameterDeclaration.getRawSignature());
//							System.err.println("NOT RESOLVE TYPE: " + node.getRawSignature());
						}
					}
				}
			}else{
				String parameterType = this.getType(parameterDeclaration.getDeclSpecifier());
				var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
						parameterDeclaration.getDeclarator().getName().toString(),  null, context.entityRepo.generateId(),
						getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
				entityrepo.add(var);
			}
		}
		return var;
	}

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

	public String getType(IASTDeclSpecifier declSpeci) {
		String type = null;

		if (declSpeci instanceof IASTCompositeTypeSpecifier) {
			final IASTCompositeTypeSpecifier compositeTypeSpec = (IASTCompositeTypeSpecifier) declSpeci;
		} else if (declSpeci instanceof IASTElaboratedTypeSpecifier) {
			final IASTElaboratedTypeSpecifier elaboratedTypeSpec = (IASTElaboratedTypeSpecifier) declSpeci;

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

	public Location getLocation(IASTNode node) {
		if (node.getFileLocation() == null)
			return null;
		return new Location(node.getFileLocation().getNodeLength(), node.getFileLocation().getStartingLineNumber(),
				node.getFileLocation().getEndingLineNumber(), node.getFileLocation().getNodeOffset(),
				node.getFileLocation().getFileName().toString());
	}

	public IASTName getDeclarationName(IASTSimpleDeclaration astNode) {
		IASTDeclarator[] declarators = astNode.getDeclarators();
		if(declarators != null && declarators.length > 0){
			return declarators[0].getName();
		} else {
			return null;
		}
	}

	public int getVisibility(IASTSimpleDeclaration astNode){
		IASTName name = getDeclarationName(astNode);
		if(name == null){
			return 0;
		} else {
			try {
				ICPPMember member = ((ICPPMember)getDeclarationName(astNode).resolveBinding());
				return member.getVisibility();
			} catch (RuntimeException e){
				return 0;
			}
		}
	}
}
