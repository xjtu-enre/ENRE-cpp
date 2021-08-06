package demo.cdt;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTImplicitDestructorName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLinkageSpecification;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamespaceAlias;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTProblemDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReferenceOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTStaticAssertionDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateSpecialization;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTVisibilityLabel;

import demo.entity.Entity;
import demo.entity.EntityRepo;
import demo.entity.EnumEntity;
import demo.entity.EnumeratorEntity;
import demo.entity.FileEntity;
import demo.entity.FunctionEntity;
import demo.entity.Pointer;
import demo.entity.StructEntity;
import demo.entity.UnionEntity;
import demo.entity.VarEntity;
import macro.MacroRepo;
import demo.entity.ClassEntity;

public class CppVisitor extends ASTVisitor{
	private static final Logger LOGGER = LogManager.getLogger(CppVisitor.class);
	private EntityRepo entityrepo;
	private FileEntity currentfile;
	private HandlerContext context;
	private MacroRepo macrorepo;
	public CppVisitor(EntityRepo entityrepo,String filefullpath) {
		this.shouldVisitAmbiguousNodes	 = true;
		this.shouldVisitArrayModifiers	 = true;
		this.shouldVisitAttributes = true;	
		this.shouldVisitBaseSpecifiers = true;	
		this.shouldVisitCaptures = true;	
		this.shouldVisitDeclarations = true;	
		this.shouldVisitDeclarators = true;	
		this.shouldVisitDeclSpecifiers = true;	
		this.shouldVisitDecltypeSpecifiers = true;	
		this.shouldVisitDesignators	 = true;
		this.shouldVisitEnumerators	 = true;
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
		//showASTNode(tu,0);
		return super.visit(tu);
	}
	
		
		@Override
		public int visit(IASTProblem problem) {
			LOGGER.error("warning: parse error " + problem.getOriginalNode().getRawSignature() + problem.getMessageWithLocation());
			return super.visit(problem);
		}
		
		//namespace
		@Override
		public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
			String namespaceName = namespaceDefinition.getName().toString().replace("::", ".");
			Entity ns = context.foundNamespace(namespaceName,namespaceDefinition.getFileLocation().getStartingLineNumber());
			return super.visit(namespaceDefinition);
		}
		
		@Override
		public int leave(ICPPASTNamespaceDefinition namespaceDefinition) {
			context.exitLastedEntity();
			return super.leave(namespaceDefinition);
		}
		
		
		// enum, struct, union, class
		@Override
		public int visit(IASTDeclSpecifier declSpec)  {
			
			if(declSpec instanceof IASTEnumerationSpecifier) {
				IASTEnumerationSpecifier enumeration = (IASTEnumerationSpecifier)declSpec;
				String methodName = enumeration.getName().toString();
				int startingLineNumber = enumeration.getFileLocation().getStartingLineNumber();
				if(methodName.equals("") ) {
					if(enumeration.getParent() instanceof CPPASTSimpleDeclaration) {
						boolean isNoObject = true;
						for (IASTDeclarator declarator:((CPPASTSimpleDeclaration)declSpec.getParent()).getDeclarators()) {
							String varName = declarator.getRawSignature().toString();
							EnumEntity enumentity = context.foundEnumDefinition(varName, startingLineNumber);
							
							context.foundVarDefinition(varName,enumentity);
							isNoObject = false;
						}
						if(isNoObject) {
							context.foundEnumDefinition("defaultEnumName", startingLineNumber);
						}				
					}
				}
				else context.foundEnumDefinition(methodName, startingLineNumber);
			}
			if(declSpec instanceof CPPASTCompositeTypeSpecifier) {
				CPPASTCompositeTypeSpecifier typeSpecifier = (CPPASTCompositeTypeSpecifier) declSpec;
				int type = typeSpecifier.getKey();
				String methodName = typeSpecifier.getName().toString();
				int startingLineNumber = typeSpecifier.getFileLocation().getStartingLineNumber();
				switch(type){
					case 1 :
						ArrayList<String> baseStruct = new ArrayList<String>();
						ICPPASTBaseSpecifier[] base1 = ((CPPASTCompositeTypeSpecifier)declSpec).getBaseSpecifiers();
						for(ICPPASTBaseSpecifier b:base1) {
							baseStruct.add(b.getName().getRawSignature());
						}
						if(methodName.equals("") ) {
							if(declSpec.getParent() instanceof CPPASTSimpleDeclaration) {
								boolean isNoObject = true;
								for (IASTDeclarator declarator:((CPPASTSimpleDeclaration)declSpec.getParent()).getDeclarators()) {
									String varName = declarator.getRawSignature().toString();
									StructEntity struct = context.foundStructDefinition(varName, startingLineNumber, baseStruct);
									context.foundVarDefinition(varName,struct);
									
									isNoObject = false;
								}
								if(isNoObject) {
									context.foundStructDefinition("defaultName", startingLineNumber, baseStruct);
								}				
							}
						}
						
						else context.foundStructDefinition(methodName, startingLineNumber, baseStruct);
						break; 
					case 2 :
						if(methodName.equals("") ) {
							if(declSpec.getParent() instanceof CPPASTSimpleDeclaration) {
								boolean isNoObject = true;
								for (IASTDeclarator declarator:((CPPASTSimpleDeclaration)declSpec.getParent()).getDeclarators()) {
									String varName = declarator.getRawSignature().toString();
									UnionEntity uniontype = context.foundUnionDefinition(varName, startingLineNumber);
									context.foundVarDefinition(varName,uniontype);
									isNoObject = false;
								}
								if(isNoObject) {
									context.foundUnionDefinition("defaultName", startingLineNumber);
								}
							}
						}
						else context.foundUnionDefinition(methodName, startingLineNumber);
						break; 
					case 3:
						ArrayList<String> baseClass = new ArrayList<String>();
						ICPPASTBaseSpecifier[] base3 = ((CPPASTCompositeTypeSpecifier)declSpec).getBaseSpecifiers();
						for(ICPPASTBaseSpecifier b:base3) {
							baseClass.add(b.getName().getRawSignature());
						}
						if(methodName.equals("")) {
							if(declSpec.getParent() instanceof CPPASTSimpleDeclaration) {
								boolean isNoObject = true;
								for (IASTDeclarator declarator:((CPPASTSimpleDeclaration)declSpec.getParent()).getDeclarators()) {
									String varName = declarator.getRawSignature().toString();
									ClassEntity classtype = context.foundClassDefinition(varName, startingLineNumber, baseClass);
									context.foundVarDefinition(varName,classtype);
									isNoObject = false;
								}
								if(isNoObject) {
									context.foundClassDefinition("defaultName", startingLineNumber, baseClass);
								}
							}
							
						}
						else {
							ClassEntity classEntity = context.foundClassDefinition(methodName, startingLineNumber, baseClass);
							if(isTemplate(declSpec)) classEntity.setTemplate();
						}
						
						break;
					default : 
						LOGGER.info("else type of this situlation");
				}
			}
			
			return super.visit(declSpec);
		}
		@Override
		public int leave(IASTDeclSpecifier declSpec) {
			
			if(declSpec instanceof CPPASTCompositeTypeSpecifier) {
				context.popScope();
				context.exitLastedEntity();
			}
			if(declSpec instanceof IASTEnumerationSpecifier) {
				context.popScope();
				context.exitLastedEntity();
			}
			return super.leave(declSpec);
		}
		

		
		
		@Override
		public int visit(IASTEnumerator enumerator) {
			int startingLineNumber = enumerator.getFileLocation().getStartingLineNumber();
			EnumeratorEntity var = context.foundEnumeratorDefinition(enumerator.getRawSignature(),
					startingLineNumber);
			return super.visit(enumerator);
		}
		
		
		//Try block
		@Override
		public int visit(IASTStatement statement) {

			
			if(statement instanceof  CPPASTExpressionStatement) {
				//System.out.println(statement.getRawSignature());
				//CPPASTExpressionStatement expressionStatement = (CPPASTExpressionStatement)statement;
				
			}
//			 
//			if(statement instanceof  ICPPASTCatchHandler) {

//			}
			if(statement instanceof IASTForStatement||statement instanceof IASTIfStatement||
					statement instanceof IASTWhileStatement||statement instanceof IASTSwitchStatement) {
				context.foundCodeScope(statement);
			}
			if(statement instanceof  IASTReturnStatement) {
				IASTReturnStatement returnstatement = (IASTReturnStatement)statement;
				if(returnstatement.getReturnArgument()!=null) {
					if( context.currentFunction()!=null&&returnstatement.getReturnValue()!=null) {
						VarEntity var = context.foundVarDefinition(returnstatement.getReturnValue().getRawSignature(),
								context.currentFunction().getReturnType());
						if(var != null && context.currentFunction()!=null) {
							context.currentFunction().setReturn(var);
						}
					}
				}	
			}
			if(statement instanceof  IASTProblemStatement) {
					
			}
			return super.visit(statement);
		}
		@Override
		public int leave(IASTStatement statement) {
			if(statement instanceof IASTForStatement||statement instanceof IASTIfStatement||
					statement instanceof IASTWhileStatement||statement instanceof IASTSwitchStatement) {
				context.popScope();
			}
			return super.leave(statement);
		}
		
		// Variables
		@Override
		public int visit(IASTDeclaration declaration) {		
			
			if (declaration instanceof ICPPASTUsingDeclaration) {
				//ICPPASTUsingDeclaration decl = (ICPPASTUsingDeclaration)declaration.getParent();
				String ns = ASTStringUtil.getName((ICPPASTUsingDeclaration)declaration);
				context.foundUsingImport(ns);
			}
			else if (declaration instanceof ICPPASTUsingDirective) {
				String ns = ((ICPPASTUsingDirective)declaration).getQualifiedName().toString().replace("::", ".");
				context.foundUsingImport(ns);
			}
			else if (declaration instanceof IASTSimpleDeclaration ) {
				IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration)declaration;
				if(simpleDeclaration.getDeclSpecifier() instanceof CPPASTElaboratedTypeSpecifier) {
					// friend class
					CPPASTElaboratedTypeSpecifier ela =(CPPASTElaboratedTypeSpecifier)simpleDeclaration.getDeclSpecifier();
					if(ela.isFriend()&&ela.getKind()==CPPASTElaboratedTypeSpecifier.k_class) {
						context.addFriendClass(ela.getName().toString());
					}	
				}
				for (IASTDeclarator declarator:simpleDeclaration.getDeclarators()) {
					IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
					//Found new typedef definition
					if(declarator instanceof IASTFunctionDeclarator) {
						// 仅声明没有实现的部分 function
						String rawName = declarator.getName().toString().replace("::", ".");
						IASTDeclSpecifier declSpeci = simpleDeclaration.getDeclSpecifier();
						String returnType = getFunctionReturn(declSpeci);
						//函数指针的处理（待修正）
						if(declarator.getName().toString().equals("")) {
							for(IASTNode node:declarator.getChildren()) {
								if(node instanceof CPPASTDeclarator) {
									rawName = node.getRawSignature();
								}
							}
						}
						context.foundMethodDeclaratorDeclaration(rawName, declaration.getFileLocation().getStartingLineNumber(), returnType);
						if(((IASTSimpleDeclaration) declaration).getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
							CPPASTSimpleDeclSpecifier sp = (CPPASTSimpleDeclSpecifier)((IASTSimpleDeclaration) declaration).getDeclSpecifier();
							if(sp.isFriend()) {
								
							}
						}
						context.addFriendFunction("");
					}
					if(declSpecifier instanceof CPPASTCompositeTypeSpecifier) {
						// 无类名对象 在运行到CPPASTCompositeTypeSpecifier时判断
						super.visit(declaration);
					}
					if (declSpecifier.getStorageClass()==IASTDeclSpecifier.sc_typedef) {
						String varType = declSpecifier.getRawSignature().toString(); 
						String varName = declarator.getRawSignature().toString();
						context.foundNewAlias(varName, varType);
					}else if (!(declarator instanceof IASTFunctionDeclarator)) {
						String varType = declSpecifier.getRawSignature().toString(); 
						String varName = declarator.getName().toString();
						if(!(declSpecifier instanceof CPPASTCompositeTypeSpecifier)) {
							for(IASTNode node:declarator.getChildren()) {
								if(node instanceof CPPASTPointer) {
									Pointer pointer = context.foundPointerDefinition(varName,varType);
								}
								else {
									VarEntity entity = context.foundVarDefinition(varName,varType);
									if(node instanceof CPPASTReferenceOperator) 
										entity.setReference(true);
								}
							}
						}	
					}
				}
			
			}else if(declaration instanceof IASTFunctionDefinition){
				//function 声明了也实现了的部分
				IASTFunctionDefinition decl = (IASTFunctionDefinition)declaration;
				IASTDeclarator declarator = ((IASTFunctionDefinition) declaration).getDeclarator();
				String rawName = declarator.getName().toString();
				IASTDeclSpecifier declSpeci =  decl.getDeclSpecifier();	
				String returnType = getFunctionReturn(declSpeci);

				FunctionEntity functionEntity = context.foundMethodDeclaratorDefine(rawName, decl.getFileLocation().getStartingLineNumber(), returnType);
				if(isTemplate(declaration)) functionEntity.setTemplate();
				
			}else  if (declaration instanceof CPPASTVisibilityLabel){
				//System.out.println(declaration.getRawSignature());
				//we ignore the visibility in dependency check
			}else if (declaration instanceof CPPASTLinkageSpecification){
					
			}else if (declaration instanceof CPPASTTemplateDeclaration){
				
				
			}else if (declaration instanceof CPPASTProblemDeclaration){
				LOGGER.error("parsing error \n" + declaration.getRawSignature());
			}else if (declaration instanceof ICPPASTAliasDeclaration){
				ICPPASTAliasDeclaration aliasDeclaration = (ICPPASTAliasDeclaration)declaration;
				String alias = aliasDeclaration.getAlias().toString();
				String originalName = aliasDeclaration.getMappingTypeId().getRawSignature();
				context.foundNewAlias(alias, originalName);
			}else if (declaration instanceof CPPASTNamespaceAlias){
				CPPASTNamespaceAlias namespaceAlias = (CPPASTNamespaceAlias)declaration;
				IASTName name = ((CPPASTNamespaceAlias)declaration).getAlias();
				String alias = name.getRawSignature();
				String originalName = namespaceAlias.getMappingName().getRawSignature();
				context.foundNewAlias(alias, originalName);
			}
			else if(declaration instanceof CPPASTStaticAssertionDeclaration)
			{
					
			}else if (declaration instanceof CPPASTTemplateSpecialization) {
			}
			else{
			}
			return super.visit(declaration);
		}	
		@Override
		public int leave(IASTDeclaration declaration) {

			if (declaration instanceof IASTSimpleDeclaration ) {
				for (IASTDeclarator declarator:((IASTSimpleDeclaration)declaration).getDeclarators()) {
					//Found new typedef definition
					if(declarator instanceof IASTFunctionDeclarator) {
						// 仅声明没有实现的部分 function
						context.exitLastedEntity();
						context.popScope();
					}
				}
			}else if(declaration instanceof IASTFunctionDefinition){
				//function 声明了也实现了的部分
				context.exitLastedEntity();
				context.popScope();
			}
			
			return super.leave(declaration);
		}


	    @Override
	    public int visit(IASTExpression expression) {
	        
//	        if(expression instanceof CPPASTIdExpression) {
//	        	CPPASTIdExpression exp = (CPPASTIdExpression)expression;
//	        	System.out.println(exp.getEvaluation());
//		        System.out.println(expression.getClass());
//	        }
	        
	        context.foundExpression(expression);
	        return PROCESS_CONTINUE;
	    }

		@Override
		public int visit(IASTParameterDeclaration parameterDeclaration) {
			VarEntity var = null;
			if(parameterDeclaration.getDeclarator() instanceof CPPASTFunctionDeclarator) {
				CPPASTFunctionDeclarator functionDeclarator = (CPPASTFunctionDeclarator)parameterDeclaration.getDeclarator();
				var =  context.foundFunctionPointerDefinition(functionDeclarator.getName().toString().replace("::", "."),"functionPointer");
				context.currentFunction().setCallbackCall(true);
			}
			else if(parameterDeclaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier){
				String parameterName = parameterDeclaration.getDeclarator().getName().toString().replace("::", ".");
				String parameterType = parameterDeclaration.getDeclSpecifier().getRawSignature().toString().replace("::", ".");
				IASTNode[] declaratorChild = parameterDeclaration.getDeclarator().getChildren();
				if(declaratorChild.length == 1 ) {
					var = context.foundVarDefinition(parameterName,parameterType);
				}
				if(declaratorChild.length <= 3) {
					for(IASTNode node:declaratorChild) {
						if(node instanceof CPPASTPointer) {
							var = context.foundPointerDefinition(parameterName, parameterType);
							break;
						}
						if(node instanceof CPPASTReferenceOperator) {
							var = context.foundVarDefinition(parameterName, parameterType);
							var.setReference(true);
							break;
						}
					}
				}
				
			}
			if(var != null && context.currentFunction()!=null) {
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
				//无返回值或者返回值非常的显而易见类似：int，float
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
			if(node instanceof CPPASTTemplateDeclaration) return true;
			if(node.getParent() == null) return false;
			else return isTemplate(node.getParent());
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
