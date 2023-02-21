package relation;

import entity.*;
import entity.Entity.BindingRelation;
import org.eclipse.cdt.core.model.IFunction;
import symtab.*;
import util.Configure;
import util.Tuple;

import java.util.Iterator;
import java.util.List;

public class RelationContext {
	//private static final Logger LOGGER = LogManager.getLogger(RelationContext.class);
	EntityRepo entityRepo;
	RelationRepo relationRepo;
	List<FileEntity> FileList;
	int entityRepoSize;

	public RelationContext(EntityRepo entityrepo) {
		this.entityRepo = entityrepo;
		entityRepoSize = entityrepo.generateId();
		this.relationRepo = new RelationRepo();
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
				relationRepo.addRelation(re);
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
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			for(BindingRelation bindingre :entity.getRelationListByBinding()) {
				Entity toEntity = entityRepo.getEntityByLocation(bindingre.getLocationInfor());
				if(toEntity == null) {
					toEntity = this.findTheEntity(bindingre.getLocationInfor(), entity);
					if(toEntity == null) continue;
				}
				Relation re = new Relation(entity, toEntity, bindingre.getRelationType());
				relationRepo.addRelation(re);
			}
			for(Tuple scopere :entity.getRelationListByScope()) {
				Entity toEntity = this.findTheEntity((String)scopere.getSecond(), entity);
				if(toEntity == null) toEntity = entityRepo.getEntityByName((String)scopere.getSecond());
				if(toEntity == null) continue;
				Relation re = new Relation(entity, toEntity,(String)scopere.getFirst());
				relationRepo.addRelation(re);
			}
			for(Tuple scopere :entity.getRelationListByObject()) {
				Tuple infor = (Tuple) scopere.getSecond();
				String function_name = (String) infor.getFirst();
				String object_name = (String) infor.getSecond();
				Entity fieldObject = findTheVariable(object_name, entity.getScope());
				if(fieldObject != null){
					if(fieldObject instanceof VarEntity){
						String typeName = ((VarEntity) fieldObject).getType().getTypeName();
						Entity typedEntity = findTheTypedEntity(typeName, entity.getScope());
						if(typedEntity != null){
							if(typedEntity.getScope().getSymbol(function_name) != null){
								Relation re = new Relation(entity,
										entityRepo.getEntity(typedEntity.getScope().getSymbol(function_name).getEntityID()),
										(String)scopere.getFirst());
								relationRepo.addRelation(re);
							}
						}
					}
				}
			}
			for(Relation relation :entity.getRelations()) {
				relationRepo.addRelation(relation);
			}
		}
	}

	public void AggregateDeal() {
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof DataAggregateEntity) {
				List<Entity> useList = ((DataAggregateEntity) entity).getUse();
				if (useList.size() != 0) {
					for (Entity toEntity : useList) {
						Relation re = new Relation(entity, toEntity, "Use");
						relationRepo.addRelation(re);
					}
				}
				List<String> usingList = ((DataAggregateEntity) entity).getUsingImport();
				if (usingList.size() != 0) {
					for (String usingEntity : usingList) {
						Entity toEntity = this.findTheEntity(usingEntity, entity);
						if(toEntity!=null) {
							Relation re = new Relation(entity, toEntity, "Using");
							relationRepo.addRelation(re);
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
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof ClassEntity) {
				List<String> baseclass = ((ClassEntity) entity).getBaseClass();
				for (String base : baseclass) {
					Entity fromEntity = findTheTypedEntity(base, entity.getScope());
					if (fromEntity != null) {
						foundOverride(fromEntity, entity);
						Relation re = new Relation(entity, fromEntity,  "Extend");
						relationRepo.addRelation(re);
					}
				}
				List<String> friendClass = ((ClassEntity) entity).getFriendClass();
				for (String friend_class : friendClass) {

					Entity fromentity = this.findTheEntity(friend_class, entity);
					if (fromentity != null) {
						Relation re = new Relation(entity, fromentity,  "Friend");
						relationRepo.addRelation(re);
					}
				}
				List<String> friendFunction = ((ClassEntity) entity).getFriendFunction();
				for (String friend_function : friendFunction) {
					Entity fromEntity = this.findTheEntity(friend_function, entity);
					if (fromEntity != null) {
						Relation re = new Relation(entity, fromEntity,  "Friend");
						relationRepo.addRelation(re);
					}
				}
				List<Integer> containEntity = ((ClassEntity) entity).getContainEntity();
				for(Integer to_entity:containEntity){
					Entity contain_to_entity = entityRepo.getEntity(to_entity);
					if(contain_to_entity != null){
						Relation re = new Relation(entity, contain_to_entity, "Contain");
						relationRepo.addRelation(re);
					}
				}
			}
			if (entity instanceof StructEntity) {
				List<String> baseStruct = ((StructEntity) entity).getBaseStruct();
				for (String base : baseStruct) {
					Entity fromEntity = this.findTheTypedEntity(base, entity.getScope());
					if (fromEntity != null) {
						Relation re = new Relation(fromEntity, entity, "Extend");
						relationRepo.addRelation(re);
					}
				}
			}
		}
	}

	/**
	 * @methodsName: FunctionDeal()
	 * @description: deal with function
	 * @param: null
	 * @return: void
	 * @throws:
	 */
	public void FunctionDeal() {
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof FunctionEntity) {
				for (Entity parameterEntity : ((FunctionEntity) entity).getParameter()) {
					if (parameterEntity != null) {
						Relation re = new Relation(entity, parameterEntity, "Parameter");
						relationRepo.addRelation(re);
					}
				}
				Entity returnEntity = ((FunctionEntity) entity).getReturnEntity();
				if (returnEntity != null) {
					Relation re = new Relation(entity, returnEntity, "Return");
					relationRepo.addRelation(re);
				}
//				if (entity instanceof FunctionEntity) {
//					Iterator<Entity> iteratorFunction = entityRepo.entityIterator();
//					while (iteratorFunction.hasNext()) {
//						Entity entityfunc = iteratorFunction.next();
//						if (entityfunc instanceof FunctionEntity) {
//							if (entityfunc.getName().equals(entity.getName())) {
//								Relation redefine = new Relation(entityfunc, entity, "Define");
//								relationRepo.addRelation(redefine);
//							}
//						}
//					}
//				}
			}
		}
	}
	public void NamespaceAliasDeal() {
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof NamespaceAliasEntity) {
				if(((NamespaceAliasEntity) entity).getToNamespaceName() != null){
					if(entity.getScope() == null) continue;
					Entity namespace = this.findTheEntity(((NamespaceAliasEntity) entity).getToNamespaceName(), entity);
					if(namespace != null){
						Relation aliasDep= new Relation(entity, namespace, "Alias");
						relationRepo.addRelation(aliasDep);
					}
				}
			}
		}
	}


	public Entity findTheVariable(String name, Scope current){
		String[] scopeManages = name.split("::");
		if(scopeManages.length == 1){
			do{
				if(current.getSymbol(scopeManages[0]) != null){
					if(current.getSymbol(scopeManages[0]) instanceof VariableSymbol){
						return entityRepo.getEntity(current.getSymbol(scopeManages[0]).getEntityID());
					}
				}
				if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
				else return null;
				if(current == current.getEnclosingScope()) return null;
			}while(current != null);
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
				else return null;
				if(current == current.getEnclosingScope()) return null;
			}while(current != null);
		}
		return null;
	}

	public Entity findTheTypedEntity(String name, Scope current){
		String[] scopeManages = name.split("::");
		if(scopeManages.length == 1){
			do{
				if(current.getSymbol(scopeManages[0]) != null){
					if(current.getSymbol(scopeManages[0]) instanceof DataAggregateSymbol){
						return entityRepo.getEntity(current.getSymbol(scopeManages[0]).getEntityID());
					}
				}
				if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
				else return null;
				if(current == current.getEnclosingScope()) return null;
			}while(current != null);
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
				else return null;
				if(current == current.getEnclosingScope()) return null;
			}while(current.getEnclosingScope() != null);
		}
		return null;
	}

	public Entity findTheEntity(String name, Entity entity){
		Scope current = entity.getScope();
		if((current == null) && (entity.getParent()!=null))
			current = entity.getParent().getScope();
		try{
			if(current == null) return null;
			String[] scopeManages = name.split("::");
			if(scopeManages.length == 1){
				do{
					if(current == null) return null;
					if(current.getSymbol(scopeManages[0]) != null){
						return entityRepo.getEntity(current.getSymbol(scopeManages[0]).getEntityID());
					}
					if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
					else return null;
					if(current == current.getEnclosingScope()) return null;
				}while(current != null);
			}
			else if(scopeManages.length == 2){
				do{
					if(current == null)
						return null;
					if(current.getSymbol(scopeManages[0]) != null){
						if(current.getSymbol(scopeManages[0]) instanceof Scope){
							current = (Scope) current.getSymbol(scopeManages[0]);
							if(current.getSymbol(scopeManages[1]) != null){
								return entityRepo.getEntity(current.getSymbol(scopeManages[1]).getEntityID());
							}
						}
						break;
					}
					if(current.getEnclosingScope() != null) current = current.getEnclosingScope();
					else return null;
					if(current == current.getEnclosingScope()) return null;
				}while(current != null);
			}
		}catch (NullPointerException exception){
			return null;
		}

		return null;
	}

	/**
	* @methodsName: foundOverride
	* @description: deal Override dependency
	* @param: Entity baseEntity, Entity entity
	* @return: void
	* @throws: 
	*/
	public void foundOverride(Entity baseEntity, Entity entity) {
		Scope fromScope = baseEntity.getScope();
		Scope toScope = entity.getScope();
		if(fromScope instanceof DataAggregateSymbol){
			for(String function_name: ((DataAggregateSymbol) fromScope).getSymbols().get(Configure.Function).keySet()){
				if(toScope.getSymbolByKind(function_name, Configure.Function) != null){
					Relation re = new Relation(
							entityRepo.getEntity(toScope.getSymbolByKind(function_name, Configure.Function).getEntityID()),
							entityRepo.getEntity(fromScope.getSymbolByKind(function_name, Configure.Function).getEntityID()),
							 "Override");
					relationRepo.addRelation(re);
				}
			}
		}
	}
	
	
//	/**
//	* @methodsName: foundExtendEntity
//	* @description: deal Extend dependency
//	* @param: Entity entity, String toentity
//	* @return: Entity
//	* @throws:
//	*/
//	public Entity foundEntityByName(Entity entity, String toentity) {
//		if(entity.getScope() != null){
//			if (((BaseScope) entity.getScope()).getEnclosingScope() != null) {
//				if(entity.getParent() instanceof FileEntity) {
//					return entityRepo.getEntityByName(toentity);
//				}
//				else {
//					return entityRepo.getEntityByName(entity.getParent().getQualifiedName() + "::" + toentity);
//				}
//			}
//		}
//		if(entity.getParent() != null){
//			if(entity.getParent() instanceof FileEntity) {
//				return entityRepo.getEntityByName(toentity);
//			}
//			else {
//				return entityRepo.getEntityByName(entity.getParent().getQualifiedName() + "::" + toentity);
//			}
//		}
//		return null;
//	}


//	public Entity foundEntityByScope(Scope scope, String toentity){
//		if(!toentity.contains("::")){
//			do{
//				if(scope.getSymbol(toentity) != null){
//					Integer toID = scope.getSymbol(toentity).getEntityID();
//					return entityRepo.getEntity(toID);
//				}
//				if(scope.getEnclosingScope() != null) scope = scope.getEnclosingScope();
//			}while(scope.getEnclosingScope() != null);
//		}else{
//			String[] names = toentity.split("::");
//			do{
//				if(scope.getSymbol(names[0]) != null){
//					scope = (Scope) scope.getSymbol(names[0]);
//					break;
//				}
//				if(scope.getEnclosingScope() != null) scope = scope.getEnclosingScope();
//			}while(scope.getEnclosingScope() != null);
//			if(names.length == 2) {
//				if(scope.getSymbol(names[1]) != null) {
//					Integer toID = scope.getSymbol(names[1]).getEntityID();
//					return entityRepo.getEntity(toID);
//				}
//				return null;
//			}
//			for(int i = 1; i < names.length; i++){
//				if(scope.getSymbol(names[i]) != null){
//					scope = (Scope) scope.getSymbol(names[i]);
//				}else{
//					return null;
//				}
//			}
//		}
//		return null;
//	}
	

//	/**
//	* @methodsName: getEntity()
//	* @description: get entity by methodName and fromEntity
//	* @param: Entity entity, String methodName
//	* @return: Entity
//	* @throws:
//	*/
//	public Entity getEntity(Entity entity, String methodName) {
//		String pureName = methodName;
//
//		if (((BaseScope) entity.getScope()).getSymbol(pureName) != null) {
//			for (Entity en : entity.getParent().getChild()) {
//				if (en instanceof FunctionEntity && en.getName().equals(pureName))
//					return en;
//			}
//		} else {
//			// (2)
//			while (entity.getParent() != null) {
//				entity = entity.getParent();
//				if (((BaseScope) entity.getScope()).getSymbol(pureName) != null) {
//					for (Entity en : entity.getChild()) {
//						if (en instanceof FunctionEntity && en.getName().equals(pureName))
//							return en;
//					}
//				}
//			}
//			// (3)
//			if (entity instanceof FileEntity) {
//				if (((FileEntity) entity).getIncludeEntity() == null)
//					return null;
//				for (FileEntity file : ((FileEntity) entity).getIncludeEntity()) {
//					if (file.getScope().resolve(pureName, Configure.File) != null) {
//						for (Entity en : file.getChild()) {
//							if (en instanceof FunctionEntity && en.getName().equals(pureName))
//								return en;
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}

	public RelationRepo getRelationRepo() {
		return relationRepo;
	}

}
