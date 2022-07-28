package relation;

import entity.*;
import entity.Entity.BindingRelation;
import util.Configure;
import util.Tuple;
import symtab.BaseScope;
import symtab.Scope;

import java.util.Iterator;
import java.util.List;

public class RelationContext {
	//private static final Logger LOGGER = LogManager.getLogger(RelationContext.class);
	EntityRepo entityrepo;
	RelationRepo relationrepo;
	List<FileEntity> FileList;
	int entityrepoSize;

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
				Entity toEntity = entityrepo.getEntityByLocation(bindingre.getLocationInfor());
				if(toEntity == null) {
					continue; 
				}
				Relation re = new Relation(entity, toEntity, bindingre.getRelationType());
				relationrepo.addRelation(re);				
			}
			for(Tuple scopere :entity.getRelationListByScope()) {
				Entity toEntity = entityrepo.getEntityByName((String)scopere.getSecond());
				if(toEntity == null) continue; 
				Relation re = new Relation(entity, toEntity,(String)scopere.getFirst());
				relationrepo.addRelation(re);				
			}
			for(Relation relation :entity.getRelations()) {
				relationrepo.addRelation(relation);
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
				if (getToFile(file) != null) {
					Relation re = new Relation(fromEntity, getToFile(file), "Include");
					fromEntity.addincludeEntity(getToFile(file));
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

	public void AggregateDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof DataAggregateEntity) {
				List<Entity> useList = ((DataAggregateEntity) entity).getUse();
				if (useList.size() != 0) {
					for (Entity toEntity : useList) {
						Relation re = new Relation(entity, toEntity, "Use");
						relationrepo.addRelation(re);
					}
				}
				List<String> usingList = ((DataAggregateEntity) entity).getUsingImport();
				if (usingList.size() != 0) {
					for (String usingEntity : usingList) {
						Entity toEntity = this.foundEntity(entity, usingEntity);
						if(toEntity!=null) {
							Relation re = new Relation(entity, toEntity, "Using");
							relationrepo.addRelation(re);
						}
					}
				}
			}
		}
	}
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
					Entity fromentity = foundEntity(entity, base);
					if (fromentity != null) {
						foundOverride(fromentity, entity);
						Relation re = new Relation(entity, fromentity,  "Extend");
						relationrepo.addRelation(re);
					}
				}
				List<String> friendClass = ((ClassEntity) entity).getFriendClass();
				for (String friend_class : friendClass) {
					Entity fromentity = foundEntity(entity, friend_class);
					if (fromentity != null) {
						Relation re = new Relation(entity, fromentity,  "Friend");
						relationrepo.addRelation(re);
					}
				}
				List<String> friendFunction = ((ClassEntity) entity).getFriendFunction();
				for (String friend : friendFunction) {
					Entity fromentity = foundEntity(entity, friend);
					if (fromentity != null) {
						Relation re = new Relation(entity, fromentity,  "Friend");
						relationrepo.addRelation(re);
					}
				}
				List<Integer> containEntity = ((ClassEntity) entity).getContainEntity();
				for(Integer to_entity:containEntity){
					Entity contain_to_entity = entityrepo.getEntity(to_entity);
					if(contain_to_entity != null){
						Relation re = new Relation(entity, contain_to_entity, "Contain");
						relationrepo.addRelation(re);
					}
				}
			}
			if (entity instanceof StructEntity) {
				List<String> baseStruct = ((StructEntity) entity).getBaseStruct();
				for (String base : baseStruct) {
					Entity fromentity = foundEntity(entity, base);
					if (fromentity != null) {
						Relation re = new Relation(fromentity, entity, "Extend");
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
			if (child instanceof FunctionEntity) {
				if (((BaseScope) entity.getScope()).getSymbol(child.getName()+"_method") != null) {
					Entity OverrideEntity = entityrepo.getEntityByName(entity.getQualifiedName() + "::" + child.getName());
					if(OverrideEntity != null){
						Relation re = new Relation(child, OverrideEntity, "Override");
						relationrepo.addRelation(re);
					}
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
	public Entity foundEntity(Entity entity, String toentity) {
		if(entity.getScope() != null){
			if (((BaseScope) entity.getScope()).getEnclosingScope() != null) {
				if(entity.getParent() instanceof FileEntity) {
					return entityrepo.getEntityByName(toentity);
				}
				else {
					return entityrepo.getEntityByName(entity.getParent().getQualifiedName() + "::" + toentity);
				}
			}
		}
		if(entity.getParent() != null){
			if(entity.getParent() instanceof FileEntity) {
				return entityrepo.getEntityByName(toentity);
			}
			else {
				return entityrepo.getEntityByName(entity.getParent().getQualifiedName() + "::" + toentity);
			}
		}
		return null;
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
						relationrepo.addRelation(re);
					}
				}
				Entity returnEntity = ((FunctionEntity) entity).getReturnEntity();
				if (returnEntity != null) {
					Relation re = new Relation(entity, returnEntity, "Return");
					relationrepo.addRelation(re);
				}
				if (entity instanceof FunctionEntity) {
					Iterator<Entity> iteratorFunction = entityrepo.entityIterator();
					while (iteratorFunction.hasNext()) {
						Entity entityfunc = iteratorFunction.next();
						if (entityfunc instanceof FunctionEntity) {
							if (entityfunc.getName().equals(entity.getName())) {
								Relation redefine = new Relation(entityfunc, entity, "Define");
								relationrepo.addRelation(redefine);
							}
						}
					}
				}
			}
		}
	}
	public void NamespaceAliasDeal() {
		Iterator<Entity> iterator = entityrepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof NamespaceAliasEntity) {
				if(((NamespaceAliasEntity) entity).getToNamespaceName() != null){
					Entity namespace = this.foundEntity(entity, ((NamespaceAliasEntity) entity).getToNamespaceName());
					if(namespace != null){
						Relation aliasDep= new Relation(entity, namespace, "Alias");
						relationrepo.addRelation(aliasDep);
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

		if (((BaseScope) entity.getScope()).getSymbol(pureName) != null) {
			for (Entity en : entity.getParent().getChild()) {
				if (en instanceof FunctionEntity && en.getName().equals(pureName))
					return en;
			}
		} else {
			// (2)
			while (entity.getParent() != null) {
				entity = entity.getParent();
				if (((BaseScope) entity.getScope()).getSymbol(pureName) != null) {
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
					if (file.getScope().resolve(pureName, Configure.File) != null) {
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

	public RelationRepo getRelationRepo() {
		return relationrepo;
	}

}
