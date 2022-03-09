package cdt;

import entity.*;
import macro.MacroRepo;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.ArrayList;

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
		this.shouldVisitImplicitDestructorNames = true;
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
		//showASTNode(tu, 0);
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
		context.exitLastedEntity();
		context.popScope();
		return super.leave(namespaceDefinition);
	}

	// enum, struct, union, class
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
		int startingLineNumber = enumerator.getFileLocation().getStartingLineNumber();
		EnumeratorEntity var = context.foundEnumeratorDefinition(enumerator.getRawSignature(), getLocation(enumerator));
		return super.visit(enumerator);
	}


	// set, use, try block
	@Override
	public int visit(IASTStatement statement) {
		if(statement instanceof CPPASTDeclarationStatement){
//			System.out.println("*******************");
//			System.out.println(statement.getRawSignature());
//			System.out.println(statement.getParent().getClass());
//			System.out.println(statement.getParent().getRawSignature());
//			CPPASTDeclarationStatement declarationStatement = (CPPASTDeclarationStatement)statement;
//			System.out.println(declarationStatement.getDeclaration().getRawSignature());
//			System.out.println("*******************");
		}
		if (statement instanceof CPPASTExpressionStatement) {
			CPPASTExpressionStatement expressionStatement = (CPPASTExpressionStatement) statement;
			IASTExpression expression = expressionStatement.getExpression();
			context.dealExpression(expression);
		}

		if (statement instanceof IASTForStatement || statement instanceof IASTIfStatement
				|| statement instanceof IASTWhileStatement || statement instanceof IASTSwitchStatement) {
			context.foundCodeScope(statement);
		}

		if (statement instanceof IASTReturnStatement) {
			IASTReturnStatement returnstatement = (IASTReturnStatement) statement;
			if (returnstatement.getReturnArgument() != null) {
				if (context.currentFunction() != null && returnstatement.getReturnValue() != null) {
					VarEntity var = context.foundVarDefinition(returnstatement.getReturnValue().getRawSignature(),
							getLocation(returnstatement));
					if (var != null && context.currentFunction() != null) {
						context.currentFunction().setReturn(var);
					}
				}
			}
		}
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

	// Variables
	@Override
	public int visit(IASTDeclaration declaration) {
		if (declaration instanceof ICPPASTUsingDeclaration) {
			String ns = ASTStringUtil.getName((ICPPASTUsingDeclaration) declaration);
			context.foundUsingImport(ns);
		} 
		else if (declaration instanceof ICPPASTUsingDirective) {
			String ns = ((ICPPASTUsingDirective) declaration).getQualifiedName().toString().replace("::", ".");
			context.foundUsingImport(ns);
		} 
		else if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
			if (declSpec instanceof CPPASTElaboratedTypeSpecifier) {
				// friend class
				CPPASTElaboratedTypeSpecifier ela = (CPPASTElaboratedTypeSpecifier) simpleDeclaration
						.getDeclSpecifier();
				if (ela.isFriend() && ela.getKind() == CPPASTElaboratedTypeSpecifier.k_class) {
					context.addFriendClass(ela.getName().toString());
				}
			}
			if (declSpec instanceof IASTEnumerationSpecifier) {
				// enum

				IASTEnumerationSpecifier enumeration = (IASTEnumerationSpecifier) declSpec;
				String methodName = enumeration.getName().toString();
				if (methodName.equals("")) {
					boolean isNoObject = true;
					for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = declarator.getRawSignature().toString();
							EnumEntity enumentity = context.foundEnumDefinition(varName, getLocation(enumeration));
							context.foundVarDefinition(varName, getLocation(declarator.getName()));
							isNoObject = false;
					}
					if (isNoObject) {
						context.foundEnumDefinition("defaultEnumName", getLocation(enumeration));
					}
					
				} else
					context.foundEnumDefinition(methodName, getLocation(enumeration));
			}
			if (declSpec instanceof CPPASTCompositeTypeSpecifier) {
				CPPASTCompositeTypeSpecifier typeSpecifier = (CPPASTCompositeTypeSpecifier) declSpec;
				int type = typeSpecifier.getKey();
				String methodName = typeSpecifier.getName().toString();
				switch (type) {
				case 1:
					ArrayList<String> baseStruct = new ArrayList<String>();
					ICPPASTBaseSpecifier[] base1 = ((CPPASTCompositeTypeSpecifier) declSpec).getBaseSpecifiers();
					for (ICPPASTBaseSpecifier b : base1) {
						baseStruct.add(b.getName().getRawSignature());
					}
					if (methodName.equals("")) {
						boolean isNoObject = true;
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = declarator.getRawSignature().toString();
							StructEntity struct = context.foundStructDefinition(varName, baseStruct, getLocation(typeSpecifier));
							context.foundVarDefinition(varName, getLocation(typeSpecifier));
							isNoObject = false;
						}
						if (isNoObject) {
							context.foundStructDefinition("defaultName", baseStruct, getLocation(typeSpecifier));
						}
					}

					else
						context.foundStructDefinition(methodName, baseStruct, getLocation(typeSpecifier));
					break;
				case 2:
					if (methodName.equals("")) {
						boolean isNoObject = true;
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = declarator.getRawSignature().toString();
							UnionEntity uniontype = context.foundUnionDefinition(varName, getLocation(typeSpecifier));
							context.foundVarDefinition(varName, getLocation(typeSpecifier));
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

					ArrayList<String> baseClass = new ArrayList<String>();
					ICPPASTBaseSpecifier[] base3 = ((CPPASTCompositeTypeSpecifier) declSpec).getBaseSpecifiers();
					for (ICPPASTBaseSpecifier b : base3) {
						baseClass.add(b.getName().getRawSignature());
					}
					if (methodName.equals("")) {
						boolean isNoObject = true;
						for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
							String varName = declarator.getRawSignature().toString();
							ClassEntity classtype = context.foundClassDefinition(varName, baseClass, getLocation(typeSpecifier));
							context.foundVarDefinition(varName, getLocation(typeSpecifier));
							isNoObject = false;
						}
						if (isNoObject) {
							context.foundClassDefinition("defaultName", baseClass, getLocation(typeSpecifier));
						}

					} else {
						
						if (isTemplate(declSpec)) {
							ClassTemplateEntity classTemplateEntity = context.foundClassTemplateDefinition(methodName, baseClass,
									getLocation(typeSpecifier));
						}
						else {
							ClassEntity classEntity = context.foundClassDefinition(methodName, baseClass,
									getLocation(typeSpecifier));
						}
							
					}

					break;
				default:
					//LOGGER.info("else type of this situlation");
				}
			}
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {

				IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
				// Found new typedef definition
				if (declarator instanceof IASTFunctionDeclarator) {
					// function declarator
					String rawName = declarator.getName().toString().replace("::", ".");
					IASTDeclSpecifier declSpeci = simpleDeclaration.getDeclSpecifier();
					String returnType = getFunctionReturn(declSpeci);
					// function pointer
					if (declarator.getName().toString().equals("")) {
						for (IASTNode node : declarator.getChildren()) {
							if (node instanceof CPPASTDeclarator) {
								rawName = node.getRawSignature();
							}
						}
					}
					context.foundMethodDeclaratorDeclaration(rawName, returnType, getLocation(declarator));
//					if (((IASTSimpleDeclaration) declaration).getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
//						CPPASTSimpleDeclSpecifier simpleDeclSpecifier= (CPPASTSimpleDeclSpecifier) ((IASTSimpleDeclaration) declaration)
//								.getDeclSpecifier();
//						if (simpleDeclSpecifier.isFriend()) {
//						}
//					}
//					context.addFriendFunction("");
				}
				if (declSpecifier instanceof CPPASTCompositeTypeSpecifier) {
					// default class, When running to CPPASTCompositeTypeSpecifier judgment
					super.visit(declaration);
				}
				if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
					String varType = declSpecifier.getRawSignature().toString();
					String varName = declarator.getRawSignature().toString();
					context.foundTypedefDefinition(varName, varType, getLocation(declarator));
					
				} else if (!(declarator instanceof IASTFunctionDeclarator)) {
					String varType = declSpecifier.getRawSignature().toString();
					String varName = declarator.getName().toString();
					if (!(declSpecifier instanceof CPPASTCompositeTypeSpecifier)) {
						if (getLocation(declarator.getName()) != null) {
							VarEntity entity = context.foundVarDefinition(varName, getLocation(declarator.getName()));
						}
//						for (IASTNode node : declarator.getChildren()) {
//							if (node instanceof CPPASTPointer) {
//								// entity = context.foundPointerDefinition(varName,varType);
//							}
//							if (node instanceof CPPASTReferenceOperator) {
//								// entity.setReference(true);
//							}
//						}
					}
				}
			}

		} else if (declaration instanceof IASTFunctionDefinition) {
			// function definition
			//System.out.println(declaration.getRawSignature());
			IASTFunctionDefinition decl = (IASTFunctionDefinition) declaration;
			IASTDeclarator declarator = decl.getDeclarator();
			String rawName = declarator.getName().toString();
			IASTDeclSpecifier declSpeci = decl.getDeclSpecifier();
			String returnType = getFunctionReturn(declSpeci);
			if (isTemplate(declaration)) {
				FunctionTemplateEntity functionTemplateEntity = context.foundFunctionTemplateDefine(rawName, returnType,
						getLocation(decl.getDeclarator()));
			}
			else{
				FunctionEntity functionEntity = context.foundFunctionDeclaratorDefine(rawName, returnType,
						getLocation(decl.getDeclarator()));
			}
					
			
		} else if (declaration instanceof ICPPASTAliasDeclaration) {
			ICPPASTAliasDeclaration aliasDeclaration = (ICPPASTAliasDeclaration) declaration;
			String alias = aliasDeclaration.getAlias().toString();
			String originalName = aliasDeclaration.getMappingTypeId().getRawSignature();
			context.foundNewAlias(alias, originalName, getLocation(declaration));
		} else if (declaration instanceof CPPASTNamespaceAlias) {
			CPPASTNamespaceAlias namespaceAlias = (CPPASTNamespaceAlias) declaration;
			IASTName name = ((CPPASTNamespaceAlias) declaration).getAlias();
			String alias = name.getRawSignature();
			String originalName = namespaceAlias.getMappingName().getRawSignature();
			context.foundNewAlias(alias, originalName, getLocation(declaration));
		}
		
//		} else if (declaration instanceof CPPASTVisibilityLabel) {
//			// we ignore the visibility in dependency check
//		} else if (declaration instanceof CPPASTLinkageSpecification) {
//		} else if (declaration instanceof CPPASTProblemDeclaration) {
//		 LOGGER.error("parsing error \n" + declaration.getRawSignature());
//		} else if (declaration instanceof CPPASTStaticAssertionDeclaration) {
//
//		} else if (declaration instanceof CPPASTTemplateSpecialization) {
//		} else {
//		}
		return super.visit(declaration);
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
			//System.out.println(lambdaExpression.getRawSignature());
			//System.out.println(lambdaExpression.getCaptureDefault());
		}
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IASTParameterDeclaration parameterDeclaration) {
		VarEntity var = null;
		if (parameterDeclaration.getDeclarator() instanceof CPPASTFunctionDeclarator) {
			CPPASTFunctionDeclarator functionDeclarator = (CPPASTFunctionDeclarator) parameterDeclaration
					.getDeclarator();
			if (this.getLocation(functionDeclarator.getName()) != null)
				var = context.foundVarDefinition(functionDeclarator.getName().toString().replace("::", "."),
						getLocation(functionDeclarator.getName()));
			if (parameterDeclaration.getParent() instanceof IASTStandardFunctionDeclarator) {
				context.currentFunction().setCallbackCall(true);
			}

		} else if (parameterDeclaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
			String parameterName = parameterDeclaration.getDeclarator().getName().toString().replace("::", ".");
			String parameterType = parameterDeclaration.getDeclSpecifier().getRawSignature().toString().replace("::",
					".");
			IASTNode[] declaratorChild = parameterDeclaration.getDeclarator().getChildren();
			if (declaratorChild.length == 1) {
				if (getLocation(parameterDeclaration.getDeclarator().getName()) != null) {
					var = context.foundVarDefinition(parameterName,
							getLocation(parameterDeclaration.getDeclarator().getName()));
				}
			}
			if (declaratorChild.length <= 3) {
				for (IASTNode node : declaratorChild) {
					if (node instanceof CPPASTPointer) {
						if (getLocation(parameterDeclaration.getDeclarator().getName()) != null) {
							var = context.foundVarDefinition(parameterName,
									getLocation(parameterDeclaration.getDeclarator().getName()));
						}

						break;
					}
					if (node instanceof CPPASTReferenceOperator) {
						var = context.foundVarDefinition(parameterName,
								getLocation(parameterDeclaration.getDeclarator().getName()));
						break;
					}
				}
			}

		}
		if (var != null && context.currentFunction() != null) {
			context.currentFunction().addParameter(var);
		}

		return super.visit(parameterDeclaration);
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
		if (node.getParent() == null)
			return false;
		else
			return isTemplate(node.getParent());
	}

	public void showASTNode(IASTNode node, int i) {
		if (node.getChildren() == null) {
			return;
		}
		System.out.println("layerNo" + i + "  " + node.getClass() + "    " + node.getRawSignature());
		for (IASTNode child : node.getChildren()) {
			System.out.println(child.getClass() + "    " + child.getRawSignature());
			//System.out.println(child.getFileLocation().getStartingLineNumber());
			//System.out.println(child.getFileLocation().getEndingLineNumber());
		}
		for (IASTNode child : node.getChildren()) {
			showASTNode(child, i + 1);
		}
	}

	public void removeLeafNode(IASTNode node, String[] text) {
		if (node.getChildren().length == 0) {
			System.out.println(node.getClass() + "    " + node.getRawSignature());
			System.out.println(node.getFileLocation().getStartingLineNumber());
			System.out.println(node.getFileLocation().getEndingLineNumber());
			System.out.println(node.getFileLocation().getNodeOffset());
			System.out.println(node.getFileLocation().getNodeLength());
			IASTFileLocation location = node.getFileLocation();
			text[location.getStartingLineNumber() - 1] = text[location.getStartingLineNumber() - 1]
					.replace(node.getRawSignature(), " ");

		} else {
			for (IASTNode child : node.getChildren()) {
				removeLeafNode(child, text);
			}
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
