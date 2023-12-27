package relation;

import entity.*;

import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.cdt.internal.core.model.LibraryReferenceArchive;
import symtab.*;
import util.Configure;
import util.Tuple;

import java.util.*;

public class RelationContext {
	EntityRepo entityRepo;
	RelationRepo relationRepo;
	List<FileEntity> FileList;
	int entityRepoSize;

	public RelationContext(EntityRepo entityrepo, RelationRepo relationRepo) {
		this.entityRepo = entityrepo;
		entityRepoSize = entityrepo.generateId();
		this.relationRepo = relationRepo;
		this.FileList = entityrepo.getFileEntities();
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
				if(bindingre.getLocationInfor() != null){
					Entity toEntity = entityRepo.getEntityByLocation(bindingre.getLocationInfor());
					if(toEntity == null) {
						toEntity = this.findTheEntity(bindingre.getLocationInfor(), entity);
						if(toEntity == null) continue;
					}
					Relation re = new Relation(entity, toEntity, bindingre.getRelationType(), bindingre.getFileID(),
							bindingre.getStartLine(), bindingre.getStartOffset(), bindingre.getParameterIndex());
					relationRepo.addRelation(re);
				}
			}
			for(ScopeRelation relation :entity.getRelationListByScope()) {
				Entity toEntity = this.findTheEntity(relation.getToEntity(), entity);
				if(toEntity == null) toEntity = entityRepo.getEntityByName(relation.getToEntity());
				if(toEntity == null) continue;
				Relation re;
				if(Objects.equals(relation.getType(), "Type")) re = new Relation(toEntity, entity, relation.getType(),
						relation.getFileID(), relation.getStartLine(), relation.getStartOffset());
				else re = new Relation(entity, toEntity, relation.getType(),
						relation.getFileID(), relation.getStartLine(), relation.getStartOffset());
				relationRepo.addRelation(re);
			}
			for(ObjectRelation relation:entity.getRelationListByObject()) {
				String function_name = relation.getToEntity();
				String object_name = relation.getObject();
				Entity fieldObject = findTheVariable(object_name, entity.getScope());
				if(fieldObject != null){
					if(fieldObject instanceof VarEntity){
						String typeName = ((VarEntity) fieldObject).getType();
						Entity typedEntity = findTheTypedEntity(typeName, entity.getScope());
						if(typedEntity != null){
							if(typedEntity.getScope().getSymbol(function_name) != null){
								Relation re = new Relation(entity,
										entityRepo.getEntity(typedEntity.getScope().getSymbol(function_name).getEntityID()),
										relation.getType(), relation.getFileID(), relation.getStartLine(), relation.getStartOffset());
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
		for(BindingRelation bindingRelation: relationRepo.getBindingList()){
			if(bindingRelation.getFromEntityInfo() != null && bindingRelation.getLocationInfor()!=null){
				Entity FromEntity = entityRepo.getEntityByLocation(bindingRelation.getFromEntityInfo());
				Entity toEntity = entityRepo.getEntityByLocation(bindingRelation.getLocationInfor());
				if(FromEntity!= null && toEntity != null) {
					Relation re = new Relation(FromEntity, toEntity, bindingRelation.getRelationType(), bindingRelation.getFileID(),
							bindingRelation.getStartLine(),bindingRelation.getStartOffset(), bindingRelation.getParameterIndex());
					relationRepo.addRelation(re);
				}
			}
		}
		for(ScopeBindingRelation scopeBindingRelation: relationRepo.getScopeBindingList()){
			Entity fromEntity = this.findTheEntity(scopeBindingRelation.getFromEntityInfo(), scopeBindingRelation.getDataAggregateEntity());
			if(fromEntity == null) fromEntity = entityRepo.getEntityByName(scopeBindingRelation.getFromEntityInfo());
			if(fromEntity == null) continue;
			Entity toEntity = entityRepo.getEntityByLocation(scopeBindingRelation.getLocationInfor());
			if(toEntity != null) {
				Relation re = new Relation(fromEntity, toEntity, scopeBindingRelation.getRelationType(), scopeBindingRelation.getFileID(),
						scopeBindingRelation.getStartLine(),scopeBindingRelation.getStartOffset(), scopeBindingRelation.getParameterIndex());
				relationRepo.addRelation(re);
			}
		}
	}

	public void AggregateDeal() {
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (entity instanceof DataAggregateEntity) {
//				List<Entity> useList = ((DataAggregateEntity) entity).getUse();
//				if (useList.size() != 0) {
//					for (Entity toEntity : useList) {
//						System.out.println("Use relation");
////						Relation re = new Relation(entity, toEntity, "Use");
////						relationRepo.addRelation(re);
//					}
//				}
				List<ScopeRelation> usingList = ((DataAggregateEntity) entity).getUsingImport();
				if (usingList.size() != 0) {
					for (ScopeRelation relation : usingList) {
						String usingEntity = relation.getToEntity();
						Entity toEntity = this.findTheEntity(usingEntity, entity);
						if(toEntity!=null) {
							Relation re = new Relation(entity, toEntity, RelationType.USING, relation.getFileID(),
									relation.getStartLine(), relation.getStartOffset());
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
				List<ScopeRelation> friend_relations = ((ClassEntity) entity).getFriendClass();
				for (ScopeRelation friend_relation : friend_relations) {
					Entity fromentity = this.findTheEntity(friend_relation.getToEntity(), entity);
					if (fromentity != null) {
						Relation re = new Relation(entity, fromentity,  RelationType.FRIEND,
								friend_relation.getFileID(), friend_relation.getStartLine(), friend_relation.getStartOffset());
						relationRepo.addRelation(re);
					}
				}
				List<ScopeRelation> friend_Functions = ((ClassEntity) entity).getFriendFunction();
				for (ScopeRelation friend_relation : friend_Functions) {
					Entity fromentity = this.findTheEntity(friend_relation.getToEntity(), entity);
					if (fromentity != null) {
						Relation re = new Relation(entity, fromentity,  RelationType.FRIEND,
								friend_relation.getFileID(), friend_relation.getStartLine(), friend_relation.getStartOffset());
						relationRepo.addRelation(re);
					}
				}
//				List<Integer> containEntity = ((ClassEntity) entity).getContainEntity();
//				for(Integer to_entity:containEntity){
//					Entity contain_to_entity = entityRepo.getEntity(to_entity);
//					if(contain_to_entity != null){
//						Relation re = new Relation(entity, contain_to_entity, "Contain");
//						relationRepo.addRelation(re);
//					}
//				}
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
						if(parameterEntity.getLocation() != null){
							Relation re = new Relation(entity, parameterEntity, RelationType.PARAMETER,
									parameterEntity.getLocation().getFile(),
									parameterEntity.getLocation().getStartLine(), parameterEntity.getLocation().getStartOffset());
							relationRepo.addRelation(re);
						}
					}
				}
				Entity returnEntity = ((FunctionEntity) entity).getReturnEntity();
				if (returnEntity != null) {
					Relation re = new Relation(entity, returnEntity, RelationType.RETURN);
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
						Relation aliasDep= new Relation(entity, namespace, RelationType.ALIAS, entity.getLocation().getFile(),
								entity.getLocation().getStartLine(), entity.getLocation().getStartOffset());
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
					Entity toEntity = entityRepo.getEntity(toScope.getSymbolByKind(function_name, Configure.Function).getEntityID());
					Relation re = new Relation(
							toEntity, entityRepo.getEntity(fromScope.getSymbolByKind(function_name, Configure.Function).getEntityID()),
							 RelationType.OVERRIDE, toEntity.getLocation().getFile(), toEntity.getLocation().getStartLine(),
							toEntity.getLocation().getStartOffset());
					relationRepo.addRelation(re);
				}
			}
		}
	}

	public void relationListDealAfter() {
		Iterator<Entity> iterator = entityRepo.entityIterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if(entity instanceof VarEntity){
				if(((VarEntity) entity).getTypeID() != -1){
					if(entity.getParentId() != -1){
						Entity parent = entityRepo.getEntity(entity.getParentId());
						Entity type = entityRepo.getEntity(((VarEntity) entity).getTypeID());
						if(parent instanceof StructEntity & type instanceof StructEntity){
							relationRepo.addRelation(new Relation(parent, type, RelationType.EMBED,
									entity.getLocation().getFile(), entity.getLocation().getStartLine(),
									entity.getLocation().getStartOffset()));
						}else if(parent instanceof FunctionEntity & type instanceof StructEntity){
							if(entity.getLocation() != null)
								relationRepo.addRelation(new Relation(parent, type, RelationType.TYPEUSE,
										entity.getLocation().getFile(), entity.getLocation().getStartLine(),
										entity.getLocation().getStartOffset()));
						}
					}
				}
			}
		}
		/*
			Add dependencies on arguments and parameters
		 */
		Map<Integer, Map<Integer, Integer>> function_parameter = new HashMap<>();
		List<Relation> relationList = this.getRelationRepo().getrelationrepo();
		for (Relation relation: relationList) {
			if(relation.getType() == RelationType.PARAMETER){
				Entity from_entity = relation.getFromEntity();
				Entity to_entity =relation.getToEntity();
				if(to_entity instanceof ParameterEntity){
					if(function_parameter.get(from_entity.getId()) == null){
						function_parameter.put(from_entity.getId(), new HashMap<Integer, Integer>());
					}
					function_parameter.get(from_entity.getId()).put(((ParameterEntity) to_entity).getIndex(), to_entity.getId());
				}

			}
		}
		List<Relation> flowToRelation = new ArrayList<>();
		for(Relation relation: relationList) {
			if (relation.getType() == RelationType.PARAMETER_USE ||
					relation.getType() == RelationType.ADDR_PARAMETER_USE ||
					relation.getType() == RelationType.PARAMETER_USE_FIELD_REFERENCE) {
				Entity from_entity = relation.getFromEntity();
				Entity to_entity = relation.getToEntity();
				if(from_entity instanceof FunctionEntity){
					Integer parameterIndex = relation.getParameterIndex();
					if(parameterIndex != -1 &&
							function_parameter.containsKey(from_entity.getId()) &&
							function_parameter.get(from_entity.getId()).containsKey(parameterIndex)){
						flowToRelation.add(new Relation(to_entity,
								entityRepo.getEntity(function_parameter.get(from_entity.getId()).get(parameterIndex)),
								RelationType.FLOWTO,
								relation.getFileID(),
								relation.getStartLine(),
								relation.getStartOffset()));
					}
				}
			}
		}
		for(Relation relation : flowToRelation){
			this.relationRepo.addRelation(relation);
		}
	}

	public void externRelationDeal(){
		for(Relation re: relationRepo.getrelationrepo()){
			if(re.getType() == RelationType.DECLARE) continue;
			if(re.getFromEntity().getExternalId() != -1){
				re.reSetFromEntity(entityRepo.getEntity(re.getFromEntity().getExternalId()));
			}
			if(re.getToEntity().getExternalId() != -1){
				re.reSetToEntity(entityRepo.getEntity(re.getToEntity().getExternalId()));
			}
		}
	}

	public RelationRepo getRelationRepo() {
		return relationRepo;
	}

}
