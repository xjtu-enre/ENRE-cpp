package relation;

import entity.*;
import entity.Entity.BindingRelation;
import util.Tuple;
import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;

import java.util.Iterator;
import java.util.List;

public class RelationContext {
	//private static final Logger LOGGER = LogManager.getLogger(RelationContext.class);
	EntityRepo entityrepo;
	RelationRepo relationrepo;
	List<FileEntity> FileList;
	int entityrepoSize;
	int includenum = 0;
	int definenum = 0;
	int callnum = 0;
	int returnnum = 0;
	int parameternum = 0;
	int extendnum = 0;
	int overridenum = 0;

	public RelationContext(EntityRepo entityrepo) {
		this.entityrepo = entityrepo;
		entityrepoSize = entityrepo.generateId();
		this.relationrepo = new RelationRepo();
		this.FileList = entityrepo.getFileEntities();
	}

	/**
	* @methodsName: FileDeal
	* @description: deal include dependency
	* @param: null
	* @return: void
	* @throws: 
	*/
	public void FileDeal() {
		for (FileEntity File : FileList) {
			for (FileEntity includefile : File.getIncludeEntity()) {
				Relation re = new Relation(File, includefile, "Include");
				includenum++;
				relationrepo.addRelation(re);
			}
		}
	}
	
	/**
	* @methodsName: relationListDeal() 
	* @description: deal with relation list
	* @param: null
	* @return: void
	* @throws: 
	*/
	public void relationListDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			
			for(BindingRelation bindingre :entity.getRelationListByBinding()) {
				//System.out.println(bindingre.getLocationInfor());
				Entity toEntity = entityrepo.getEntityByLocation(bindingre.getLocationInfor());
				if(toEntity == null) {
					continue; 
				}
				
				Relation re = new Relation(entity, toEntity, bindingre.getRelationType());
				relationrepo.addRelation(re);				
			}
			for(Tuple scopere :entity.getRelationListByScope()) {
				Entity toEntity = entityrepo.getEntityByName((String)scopere.getFirst());
				if(toEntity == null) continue; 
				Relation re = new Relation(entity, toEntity,(String)scopere.getSecond());
				relationrepo.addRelation(re);				
			}
		}
	}
	
	
	
	/**
	* @methodsName: getIncludeRelation() 
	* @description: deal with include relation list
	* @param: FileEntity fromEntity
	* @return: void
	* @throws: 
	*/
	public void getIncludeRelation(FileEntity fromEntity) {

		List<String> includeFile = fromEntity.getInclude();
		if (includeFile != null) {
			for (String file : includeFile) {
				// System.out.println(file);
				if (getToFile(file) != null) {
					Relation re = new Relation(fromEntity, getToFile(file), "Include");
					fromEntity.addincludeEntity(getToFile(file));
					includenum++;
					relationrepo.addRelation(re);

				}

			}
		}

	}
	
	/**
	* @methodsName: getToFile() 
	* @description: get file entity by file path
	* @param: String file
	* @return: FileEntity
	* @throws: 
	*/
	public FileEntity getToFile(String file) {
		for (FileEntity fileentity : FileList) {
			if (fileentity.getPath().equals(file)) {
				return fileentity;
			}
		}
		return null;
	}

//	public void AggregateDeal() {
//		Iterator<Entity> iterator = entityrepo.entityIterator();
//		while (iterator.hasNext()) {
//			Entity entity = iterator.next();
//			if (entity instanceof DataAggregateEntity) {
//				List<Entity> useList = ((DataAggregateEntity) entity).getUse();
//				if (useList.size() != 0) {
//					for (Entity toEntity : useList) {
//						Relation re = new Relation(entity, toEntity, "Use");
//						relationrepo.addRelation(re);
//					}
//				}
//				List<Entity> callList = ((DataAggregateEntity) entity).getCall();
//				if (callList.size() != 0) {
//					for (Entity toEntity : callList) {
//						Relation re = new Relation(entity, toEntity, "Call");
//						relationrepo.addRelation(re);
//					}
//				}
//			}
//		}
//	}
	/**
	* @methodsName: ClassDeal()
	* @description: deal extend dependency
	* @param: null
	* @return: void
	* @throws: 
	*/
	public void ClassDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof ClassEntity) {
				List<String> baseclass = ((ClassEntity) entity).getBaseClass();
				for (String base : baseclass) {
					Entity fromentity = foundExtendEntity(entity, base);
					if (fromentity != null) {
						foundOverride(fromentity, entity);
						Relation re = new Relation(fromentity, entity, "Extend");
						extendnum++;
						relationrepo.addRelation(re);
					}
				}

			}
			if (entity instanceof StructEntity) {
				List<String> baseStruct = ((StructEntity) entity).getBaseStruct();
				for (String base : baseStruct) {
					Entity fromentity = foundExtendEntity(entity, base);
					if (fromentity != null) {
						Relation re = new Relation(fromentity, entity, "Extend");
						extendnum++;
						relationrepo.addRelation(re);
					}
				}
			}
		}
	}
	
	/**
	* @methodsName: foundOverride
	* @description: deal Override dependency
	* @param: Entity baseEntity, Entity entity
	* @return: void
	* @throws: 
	*/
	public void foundOverride(Entity baseEntity, Entity entity) {
		for (Entity child : baseEntity.getChild()) {
			if (child instanceof FunctionEntityDefine) {
				if (((BaseScope) entity.getScope()).getSymbol(child.getName()) != null) {
					Entity OverrideEntity = entityrepo.getEntityByName(entity.getQualifiedName() + "." + child.getName());
					Relation re = new Relation(child, OverrideEntity, "Override");
					overridenum++;
					relationrepo.addRelation(re);
				}
			}
		}
	}
	
	
	/**
	* @methodsName: foundExtendEntity
	* @description: deal Extend dependency
	* @param: Entity entity, String toentity
	* @return: Entity
	* @throws: 
	*/
	public Entity foundExtendEntity(Entity entity, String toentity) {
		if (((BaseScope) entity.getScope()).getEnclosingScope() != null) {
			BaseScope scope = ((BaseScope) ((BaseScope) entity.getScope()).getEnclosingScope());
			if (scope.getMembers().get(toentity) != null) {
				return entityrepo.getEntityByName(entity.getParent().getQualifiedName() + "." + toentity);
			}
		}

		return null;
	}
	
	
	/**
	* @methodsName: EntityScopeDeal
	* @description: pre-deal with source entity dependencies based on scope
	* @param: sourceEntity
	*/
	public void EntityScopeDeal(Entity en) {
		
	}
	
	public void EntityInformationDeal() {
		
	
	}
	
	/**
	* @methodsName: FunctionDeal() 
	* @description: deal with function
	* @param: null
	* @return: void 
	* @throws: 
	*/
	public void FunctionDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof FunctionEntity) {
				for (Entity parameterEntity : ((FunctionEntity) entity).getParameter()) {
					if (parameterEntity != null) {
						Relation re = new Relation(entity, parameterEntity, "Parameter");
						parameternum++;
						relationrepo.addRelation(re);
					}
				}
				Entity returnEntity = ((FunctionEntity) entity).getReturnEntity();
				if (returnEntity != null) {
					Relation re = new Relation(entity, returnEntity, "Return");
					returnnum++;
					relationrepo.addRelation(re);
				}
				if (entity instanceof FunctionEntityDecl) {
					Iterator<Entity> iteratorFunction = entityrepo.entityIterator();
					while (iteratorFunction.hasNext()) {
						Entity entityfunc = iteratorFunction.next();
						if (entityfunc instanceof FunctionEntityDefine) {
							if (entityfunc.getName().equals(entity.getName())) {
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
	
	
	/**
	* @methodsName: getEntity()
	* @description: get entity by methodName and fromEntity
	* @param: Entity entity, String methodName
	* @return: Entity
	* @throws: 
	*/
	public Entity getEntity(Entity entity, String methodName) {
		String pureName = methodName;

		if (((BaseScope) entity.getScope()).getMembers().get(pureName) != null) {
			for (Entity en : entity.getParent().getChild()) {
				if (en instanceof FunctionEntity && en.getName().equals(pureName))
					return en;
			}
		} else {
			// (2)
			while (entity.getParent() != null) {
				entity = entity.getParent();
				if (((BaseScope) entity.getScope()).getMembers().get(pureName) != null) {
					for (Entity en : entity.getChild()) {
						if (en instanceof FunctionEntity && en.getName().equals(pureName))
							return en;
					}
				}
			}
			// (3)
			if (entity instanceof FileEntity) {
				if (((FileEntity) entity).getIncludeEntity() == null)
					return null;
				for (FileEntity file : ((FileEntity) entity).getIncludeEntity()) {
					if (file.getScope().resolve(pureName) != null) {
						for (Entity en : file.getChild()) {
							if (en instanceof FunctionEntity && en.getName().equals(pureName))
								return en;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	* @methodsName: getEntity()
	* @description: get entity by methodName and scope
	* @param: Scope scope, String functionName
	* @return: boolean
	* @throws: 
	*/
	public boolean foundEntity(Scope scope, String functionName) {
		if (functionName.contains("::")) {
			String[] splitName = functionName.split("::");
			if (scope.resolve(splitName[0]) != null) {

			}
		} else {
			if (scope != null) {
				if (scope.resolve(functionName) != null)
					return true;
				else {
					while (scope.getEnclosingScope() != null) {
						scope = scope.getEnclosingScope();
						if (scope.resolve(functionName) != null)
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
		//LOGGER.info("includenum:" + includenum);
		//LOGGER.info("definenum:" + definenum);
		//LOGGER.info("callnum:" + callnum);
		//LOGGER.info("returnnum:" + returnnum);
		//LOGGER.info("parameternum:" + parameternum);
		//LOGGER.info("extendnum:" + extendnum);
		//LOGGER.info("overridenum:" + overridenum);
	}
}