package cdt;

import entity.*;
import macro.MacroRepo;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;


public class CppVisitor extends ASTVisitor {
	//private static final Logger LOGGER = LogManager.getLogger(CppVisitor.class);
	private EntityRepo entityrepo;
	private FileEntity currentfile;
	private HandlerContext context;
	private MacroRepo macrorepo;

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
		// TODO: should be try and throw exception
		// this.shouldVisitImplicitDestructorNames = true;
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
		this.context.dealIncludeScope();
		return super.visit(tu);
	}

	@Override
	public int visit(IASTProblem problem) {
		// LOGGER.error("warning: parse error " +
		// problem.getOriginalNode().getRawSignature() +
		// problem.getMessageWithLocation());
		return super.visit(problem);
	}

	// namespace
	@Override
	public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
		String namespaceName = namespaceDefinition.getName().toString();
		Entity ns = context.foundNamespace(namespaceName);
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
		return super.visit(declSpec);
	}

	@Override
	public int leave(IASTDeclSpecifier declSpec) {
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
		if(statement instanceof CPPASTDeclarationStatement){
//			CPPASTDeclarationStatement declarationStatement = (CPPASTDeclarationStatement)statement;
		}
		if (statement instanceof IASTForStatement || statement instanceof IASTIfStatement
				|| statement instanceof IASTWhileStatement || statement instanceof IASTSwitchStatement) {
			context.foundCodeScope(statement);
		}

//		if (statement instanceof IASTReturnStatement) {
//			IASTReturnStatement returnstatement = (IASTReturnStatement) statement;
//			if (returnstatement.getReturnArgument() != null) {
//				if (context.currentFunction() != null && returnstatement.getReturnValue() != null) {
//					VarEntity var = context.foundVarDefinition(returnstatement.getReturnValue().getRawSignature(),
//							getLocation(returnstatement));
//					if (var != null && context.currentFunction() != null) {
//						context.currentFunction().setReturn(var);
//					}
//				}
//			}
//		}
		if (statement instanceof IASTProblemStatement) {
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

		if(declaration instanceof IASTParameterDeclaration){
			System.out.println(declaration.getRawSignature());
			System.out.println("test");
		}
		if (declaration instanceof ICPPASTUsingDeclaration) {
			String ns = ASTStringUtil.getName((ICPPASTUsingDeclaration) declaration);
			context.foundUsingImport(ns);
		}
		else if (declaration instanceof ICPPASTUsingDirective) {
			// using namespace xxx
			String ns = ((ICPPASTUsingDirective) declaration).getQualifiedName().toString().replace("::", ".");
			context.foundUsingImport(ns);
		}
		else if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
			if(declSpec instanceof ICPPASTDeclSpecifier) {
				ICPPASTDeclSpecifier declSpecifier = (ICPPASTDeclSpecifier) declSpec;
				if(declSpecifier instanceof CPPASTElaboratedTypeSpecifier) {
					if(declSpecifier.isFriend()) {
						context.addFriendClass(((CPPASTElaboratedTypeSpecifier) declSpec).getName().toString());
					}
					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
						if(declarator instanceof CPPASTFunctionDeclarator){
							this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
						}else if(declarator instanceof CPPASTDeclarator){

						}else{
							System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
						}
					}
				}
				else if(declSpecifier instanceof CPPASTSimpleDeclSpecifier) {
					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
						if(declarator instanceof CPPASTFunctionDeclarator){
							this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
						}else if(declarator instanceof CPPASTDeclarator){

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
						// TODO: 绑定到相应的类型
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = this.resolveEntityName(declarator.getName());
							context.foundVarDefinition(varName, getLocation(declarator.getName()), "null");
						}
					}

					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
						if(declarator instanceof CPPASTFunctionDeclarator){
							this.FoundFunctionDeclaration((CPPASTFunctionDeclarator) declarator, declSpecifier);
						}else if(declarator instanceof CPPASTDeclarator){

						}else{
							System.out.println( "INFO: Haven't deal with " + declarator.getClass().toString() + " in " + declSpec.getClass().toString());
						}
					}
				}
				else if(declSpecifier instanceof CPPASTEnumerationSpecifier) {
					// enum
					IASTEnumerationSpecifier enumerationSpecifier = (IASTEnumerationSpecifier) declSpec;
					EnumEntity enumentity = null;
					String methodName = enumerationSpecifier.getName().toString();
					if (methodName.equals("")) {
						boolean isNoObject = true;
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = declarator.getName().toString();
							enumentity = context.foundEnumDefinition(varName, getLocation(enumerationSpecifier));
							context.foundVarDefinition(varName, getLocation(declarator.getName()), "null");
							isNoObject = false;
						}
						if (isNoObject) {
							enumentity = context.foundEnumDefinition("defaultEnumName", getLocation(enumerationSpecifier));
						}

					} else {
						enumentity = context.foundEnumDefinition(methodName, getLocation(enumerationSpecifier));
					}
					try {
						// enum with scope: enum class/enum struct
						if(enumerationSpecifier.getName()!= null & !methodName.equals("")) {
							if(enumerationSpecifier.getName().getLeadingSyntax()!=null) {
								if(enumerationSpecifier.getName().getLeadingSyntax().getNext()!=null) {
									enumentity.setScope(true);
								}
							}
						}
					} catch (UnsupportedOperationException | ExpansionOverlapsBoundaryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(declSpecifier instanceof CPPASTCompositeTypeSpecifier) {
					CPPASTCompositeTypeSpecifier typeSpecifier = (CPPASTCompositeTypeSpecifier) declSpec;
					int type = typeSpecifier.getKey();
					String methodName = typeSpecifier.getName().toString();
					if(typeSpecifier.getName() instanceof ICPPASTTemplateId) {
						ICPPASTTemplateId templateId = (ICPPASTTemplateId)(typeSpecifier.getName());
						methodName = templateId.getTemplateName().toString();
					}
					switch (type) {
					case 1:
						StructEntity structEntity = null;
						ArrayList<String> baseStruct = new ArrayList<String>();
						ICPPASTBaseSpecifier[] base1 = ((CPPASTCompositeTypeSpecifier) declSpec).getBaseSpecifiers();
						for (ICPPASTBaseSpecifier b : base1) {
							baseStruct.add(b.getName().getRawSignature());
						}
						if (methodName.equals("")) {
							structEntity = context.foundStructDefinition("defaultName", baseStruct, getLocation(typeSpecifier));
							boolean isNoObject = true;
							for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
								String varName = declarator.getName().toString();
								context.foundVarDefinition(varName, getLocation(typeSpecifier), "null");
								isNoObject = false;
							}
							if (isNoObject) {
								structEntity = context.foundStructDefinition("defaultName", baseStruct, getLocation(typeSpecifier));
							}
						}

						else {
							structEntity = context.foundStructDefinition(methodName, baseStruct, getLocation(typeSpecifier));
							for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
								String varName = declarator.getName().toString();
								context.foundVarDefinition(varName, getLocation(typeSpecifier), "null");
							}
							if (declaration.getParent() instanceof CPPASTTemplateDeclaration) {
								structEntity.setTemplate(true);
								if(declaration.getParent() instanceof ICPPASTTemplateSpecialization) {
									structEntity.setSpecializationTemplate(true);
								}
							}
						}

						break;
					case 2:
						if (methodName.equals("")) {
							boolean isNoObject = true;
							for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
								String varName = declarator.getName().toString();
								UnionEntity uniontype = context.foundUnionDefinition(varName, getLocation(typeSpecifier));
								context.foundVarDefinition(varName, getLocation(typeSpecifier), "null");
								isNoObject = false;
							}
							if (isNoObject) {
								context.foundUnionDefinition("defaultName", getLocation(typeSpecifier));
							}
						} else {
							context.foundUnionDefinition(methodName, getLocation(typeSpecifier));
						}

						break;
					case 3:
						ClassEntity classEntity = null;
						ArrayList<String> baseClass = new ArrayList<String>();
						ICPPASTBaseSpecifier[] base3 = ((CPPASTCompositeTypeSpecifier) declSpec).getBaseSpecifiers();
						for (ICPPASTBaseSpecifier b : base3) {
							baseClass.add(b.getName().getRawSignature());
						}
						if (methodName.equals("")) {
							boolean isNoObject = true;
							for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
								String varName = declarator.getName().toString();
								classEntity = context.foundClassDefinition(varName, baseClass, getLocation(typeSpecifier));
								context.foundVarDefinition(varName, getLocation(typeSpecifier), "null");
								isNoObject = false;
							}
							if (isNoObject) {
								classEntity = context.foundClassDefinition("defaultName", baseClass, getLocation(typeSpecifier));
							}

						} else {
							classEntity = context.foundClassDefinition(methodName, baseClass, getLocation(typeSpecifier));
							if (declaration.getParent() instanceof ICPPASTInternalTemplateDeclaration) {
								classEntity.setTemplate(true);
								if(declaration.getParent() instanceof ICPPASTTemplateSpecialization) {
									classEntity.setSpecializationTemplate(true);
								}
							}
						}

						break;
					default:
						//LOGGER.info("else type of this situlation");
					}
				}
				for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
					// Found new typedef definition
					if (declSpecifier instanceof CPPASTCompositeTypeSpecifier) {
						// default class, When running to CPPASTCompositeTypeSpecifier judgment
						super.visit(declaration);
					}
					if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
						String varType = declSpecifier.getRawSignature().toString();
						String varName = declarator.getName().toString();
						context.foundTypedefDefinition(varName, varType, getLocation(declarator));

					} else if (!(declarator instanceof IASTFunctionDeclarator)) {
						String varType = declSpecifier.getRawSignature().toString();
						String varName = declarator.getName().toString();
						if (!(declSpecifier instanceof CPPASTCompositeTypeSpecifier)) {
							if (getLocation(declarator.getName()) != null) {
								VarEntity entity = context.foundVarDefinition(varName, getLocation(declarator.getName()), "null");
							}
//							for (IASTNode node : declarator.getChildren()) {
//								if (node instanceof CPPASTPointer) {
//									// entity = context.foundPointerDefinition(varName,varType);
//								}
//								if (node instanceof CPPASTReferenceOperator) {
//									// entity.setReference(true);
//								}
//							}
						}
					}
				}
			}
		}
		else if (declaration instanceof IASTFunctionDefinition) {

			// function definition
			FunctionEntity functionEntity = null;
			IASTFunctionDefinition decl = (IASTFunctionDefinition) declaration;
			IASTDeclarator declarator = decl.getDeclarator();
			String rawName = this.resolveEntityName(declarator.getName());

			IASTDeclSpecifier declSpeci = decl.getDeclSpecifier();
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
			functionEntity = context.foundFunctionDefine(rawName, returnType, getLocation(decl.getDeclarator()), parameterLists);
			if (declaration.getParent() instanceof CPPASTTemplateDeclaration) {
				functionEntity.setTemplate(true);
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

	public void FoundFunctionDeclaration(CPPASTFunctionDeclarator declarator, ICPPASTDeclSpecifier declSpecifier){
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
//			context.foundFunctionDeclaration(rawName, returnType, getLocation(declarator), parameterLists);
			context.foundFunctionDefine(rawName, returnType, getLocation(declarator), parameterLists);
		}
	}

	@Override
	public int leave(IASTDeclaration declaration) {

		if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
			if (declSpec instanceof CPPASTCompositeTypeSpecifier) {

				int type = ((CPPASTCompositeTypeSpecifier) declSpec).getKey();
				if (type == 1 || type == 2 || type == 3) {
					context.popScope();
					context.exitLastedEntity();
				}
			}
			if (declSpec instanceof IASTEnumerationSpecifier) {
				context.popScope();
				context.exitLastedEntity();
			}
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
				// Found new typedef definition
				if (declarator instanceof IASTFunctionDeclarator) {
					// function declarator
					context.exitLastedEntity();
					context.popScope();

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
			if (parameterDeclaration.getParent() instanceof IASTStandardFunctionDeclarator) {
				if(context.currentFunction()!=null)
					context.currentFunction().setCallbackCall();
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
								null,  null, context.entityRepo.generateId(),
								getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);

					}
				}
				if (declaratorChild.length <= 3) {
					for (IASTNode node : declaratorChild) {
						if (node instanceof CPPASTPointer) {
							if (getLocation(parameterDeclaration.getDeclarator().getName()) != null) {
								var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
										null,  null, context.entityRepo.generateId(),
										getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
							}
							break;
						} else if (node instanceof CPPASTReferenceOperator) {
							var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
									null,  null, context.entityRepo.generateId(),
									getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
							break;
						}else if(node instanceof CPPASTName){
							var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
									parameterDeclaration.getDeclarator().getName().toString(),
									null, context.entityRepo.generateId(),
									getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
						}else{
							System.err.println("NOT RESOLVE TYPE: " + node.getRawSignature());
						}
					}
				}
			}else{
				String parameterType = this.getType(parameterDeclaration.getDeclSpecifier());
				var = new ParameterEntity(parameterDeclaration.getDeclarator().getName().toString(),
						null,  null, context.entityRepo.generateId(),
						getLocation(parameterDeclaration.getDeclarator().getName()), parameterType);
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
		return false;
	}

	public void showASTNode(IASTNode node, int i) {
		if (node.getChildren() == null) {
			return;
		}
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
}
