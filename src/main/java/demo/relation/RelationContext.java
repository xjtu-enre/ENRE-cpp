package demo.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;

import demo.Main;
import demo.entity.ClassEntity;
import demo.entity.DataAggregateEntity;
import demo.entity.Entity;
import demo.entity.EntityRepo;
import demo.entity.Expression;
import demo.entity.FileEntity;
import demo.entity.FunctionEntity;
import demo.entity.FunctionEntityDecl;
import demo.entity.FunctionEntityDefine;
import demo.entity.StructEntity;


public class RelationContext {
	private static final Logger LOGGER = LogManager.getLogger(RelationContext.class);
	EntityRepo entityrepo;
	RelationRepo relationrepo;
	List<FileEntity> FileList;
	int entityrepoSize;
	int includenum=0;
	int definenum=0;
	int callnum=0;
	int returnnum=0;
	int parameternum=0;
	int extendnum=0;
	int overridenum=0;
	
	public RelationContext(EntityRepo entityrepo) {
		this.entityrepo = entityrepo;
		entityrepoSize = entityrepo.generateId();
		this.relationrepo = new RelationRepo();
		this.FileList = entityrepo.getFileEntities();
	}
	
	/*
	 * deal include dependency
	 */
	public void FileDeal() {
		for(FileEntity File:FileList) {
			//getIncludeRelation(File);
			for(FileEntity includefile:File.getIncludeEntity()) {
				Relation re = new Relation(File, includefile,"Include");
				includenum++;
				relationrepo.addRelation(re);
			}
		}
	}
	
	
	
	public void getIncludeRelation(FileEntity fromEntity) {
		
		List<String> includeFile = fromEntity.getInclude();
		if(includeFile!=null) {
			for(String file:includeFile) {
				//System.out.println(file);
				if(getToFile(file)!=null) {
					
					Relation re = new Relation(fromEntity, getToFile(file),"Include");
					fromEntity.addincludeEntity(getToFile(file));
					includenum++;
					relationrepo.addRelation(re);
					
				}
				
			}
		}
		
	}
	public FileEntity getToFile(String file) {
		for(FileEntity fileentity:FileList) {
			if(fileentity.getPath().equals(file)) {
				return fileentity;
			}
		}
		return null;
	}
	
	
	public void AggregateDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while(iterator.hasNext()) {
			Entity entity = iterator.next();
			if(entity instanceof DataAggregateEntity) {
				List<Entity> useList = ((DataAggregateEntity) entity).getUse();
				if(useList.size()!= 0) {
					for(Entity toEntity:useList) {
						Relation re = new Relation(entity, toEntity,"Use");
						relationrepo.addRelation(re);
					}
				}
				List<Entity> callList = ((DataAggregateEntity) entity).getCall();
				if(callList.size()!= 0) {
					for(Entity toEntity:callList) {
						Relation re = new Relation(entity, toEntity,"Call");
						relationrepo.addRelation(re);
					}
				}
			}
		}
	}

	/*
	 * deal extend dependency
	 */
	public void ClassDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while(iterator.hasNext()) {
			Entity entity = iterator.next();
			if(entity instanceof ClassEntity) {
				List<String> baseclass = ((ClassEntity) entity).getBaseClass();
				for(String base:baseclass) {
					Entity fromentity = foundExtendEntity(entity, base);
					if(fromentity != null) {
						foundOverride(fromentity,entity);
						Relation re = new Relation(fromentity, entity,"Extend");
						extendnum++;
						relationrepo.addRelation(re);
					}
				}
				
			}
			if(entity instanceof StructEntity) {
				List<String> baseStruct = ((StructEntity) entity).getBaseStruct();
				for(String base:baseStruct) {
					Entity fromentity = foundExtendEntity(entity, base);
					if(fromentity != null) {
						Relation re = new Relation(fromentity, entity,"Extend");
						extendnum++;
						relationrepo.addRelation(re);
					}
				}
			}
		}
	}
	
	
	public void foundOverride(Entity baseEntity, Entity entity) {
		for(Entity child:baseEntity.getChild()) {
			if(child instanceof FunctionEntityDefine) {
				if(((BaseScope)entity.getScope()).getSymbol(child.getName())!=null) {
					Entity OverrideEntity = entityrepo.getEntity(entity.getQualifiedName()+"."+child.getName());
					Relation re = new Relation(child, OverrideEntity,"Override");
					overridenum++;
					relationrepo.addRelation(re);
				}
			}
		}
	}
	public Entity foundExtendEntity(Entity entity, String toentity) {
		if(((BaseScope) entity.getScope()).getEnclosingScope()!=null){
			BaseScope scope = ((BaseScope)((BaseScope) entity.getScope()).getEnclosingScope());
			if(scope.getMembers().get(toentity)!=null) {
				return entityrepo.getEntity(entity.getParent().getQualifiedName()+"."+toentity);
			}
		}
		
		return null;
	}

	
	
	public void FunctionDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while(iterator.hasNext()) {
			Entity entity = iterator.next();
			if(entity instanceof FunctionEntity) {
				for(Entity parameterEntity:((FunctionEntity)entity).getParameter()) {
					if(parameterEntity!=null) {
						Relation re = new Relation(entity, parameterEntity,"Parameter");
						parameternum++;
						relationrepo.addRelation(re);
					}
				}
				Entity returnEntity = ((FunctionEntity)entity).getReturnEntity();
				if(returnEntity!=null) {
					Relation re = new Relation(entity, returnEntity,"Return");
					returnnum++;
					relationrepo.addRelation(re);
				}
				((FunctionEntity)entity).resolveExpression();
				List<Expression> expressions = ((FunctionEntity) entity).expressionList();
				for(Expression exp:expressions) {
					String methodName = exp.getExpression();
					if(methodName.contains("::")) {
						Entity methodEntity = getEntity(entity, methodName.split("::")[0]);
						if(methodEntity!=null) {
							exp.setToEntity(getEntity(methodEntity, methodName.split("::")[1]));
							Relation re = new Relation(entity, exp.getToEntity(),"Call");
							relationrepo.addRelation(re);
							callnum++;
						}
					}
					else {
						exp.setToEntity(getEntity(entity, methodName));
						if(exp.getToEntity()!=null) {
							Relation re = new Relation(entity, exp.getToEntity(),"Call");
							relationrepo.addRelation(re);
							callnum++;
						}
						if(exp.getToEntity() instanceof FunctionEntity && ((FunctionEntity)(exp.getToEntity())).isCallbackCall()) {
							FunctionEntity callbackFunction = (FunctionEntity)(exp.getToEntity());
							for(IASTInitializerClause test:((IASTFunctionCallExpression)(exp.getNode())).getArguments()) {
								if(getEntity(callbackFunction, test.getRawSignature())!=null) {
									Relation re = new Relation(callbackFunction, getEntity(callbackFunction, test.getRawSignature()),"CallBack");
									relationrepo.addRelation(re);
									callnum++;
								}
							}
							
						}
						
					}
				}
				if(entity instanceof FunctionEntityDecl) {
					Iterator<Entity> iteratorFunction = entityrepo.entityIterator();
					while(iteratorFunction.hasNext()) {
						Entity entityfunc = iteratorFunction.next();
						if(entityfunc instanceof FunctionEntityDefine) {
							if(entityfunc.getName().equals(entity.getName())) {
								Relation redefine = new Relation(entityfunc, entity, "Define");
								definenum++;
								relationrepo.addRelation(redefine);
							}							
						}
					}
				}
			}
		}
	}
	public Entity getEntity(Entity entity, String methodName) {
		String pureName = methodName;

		
		if(((BaseScope) entity.getScope()).getMembers().get(pureName)!=null) {
			for(Entity en:entity.getParent().getChild()) {
				if(en instanceof FunctionEntity && en.getName().equals(pureName)) return en;
			}
		}
		else {
			//(2)
			while(entity.getParent()!=null) {
				entity = entity.getParent();
				if(((BaseScope) entity.getScope()).getMembers().get(pureName)!=null) {
					for(Entity en:entity.getChild()) {
						if(en instanceof FunctionEntity && en.getName().equals(pureName))	return en;
					}
				}
			}
			//(3)
			if(entity instanceof FileEntity) {
				if(((FileEntity)entity).getIncludeEntity()==null) return null;
				for(FileEntity file:((FileEntity)entity).getIncludeEntity()) {
//					System.out.println(file.getName());
//					System.out.println(file.getScope().getNumberOfSymbols());
//					for(String str:file.getScope().getSymbolNames()) {
//						System.out.println(str);
//					}
					
//					if(file.getScope().resolve("ph")!=null) {
//						System.out.println("yes");
//						if(file.getScope().resolve("Context")!=null) {
//							System.out.println("yes");
//							if(file.getScope().resolve(pureName)!=null) {
//								System.out.println("yes");
//							}
//						}
//					}
					if(file.getScope().resolve(pureName)!=null) {
						for(Entity en:file.getChild()) {
							if(en instanceof FunctionEntity && en.getName().equals(pureName))	return en;
						}
					}
				}
			}
		}
		return null;
	}
	public boolean foundEntity(Scope scope, String functionName) {
		if(functionName.contains("::")) {
			String[] splitName = functionName.split("::");
			if(scope.resolve(splitName[0]) != null) {
				
			}
		}
		else {
			if(scope != null) {
				if(scope.resolve(functionName) != null)
					return true;
				else {
					while(scope.getEnclosingScope()!= null) {
						scope = scope.getEnclosingScope();
						if(scope.resolve(functionName)!=null)
							return true;
					}
				}
			}
				
		}
		return false;
	}
	public RelationRepo getRelationRepo() {
		return relationrepo;
	}
	public void stastics() {
		LOGGER.info("includenum:"+includenum);
		LOGGER.info("definenum:"+definenum);
		LOGGER.info("callnum:"+callnum);
		LOGGER.info("returnnum:"+returnnum);
		LOGGER.info("parameternum:"+parameternum);
		LOGGER.info("extendnum:"+extendnum);
		LOGGER.info("overridenum:"+overridenum);
	}
}
