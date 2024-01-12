package entity;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

import java.util.*;
import java.util.Map.Entry;

public class EntityRepo {

	public class EntityＭapIterator implements Iterator<Entity> {

		private Iterator<Entry<Integer, Entity>> entryIterator;

		public EntityＭapIterator(Set<Entry<Integer, Entity>> entries) {
			this.entryIterator = entries.iterator();
		}

		@Override
		public boolean hasNext() {
			return entryIterator.hasNext();
		}

		@Override
		public Entity next() {
			return entryIterator.next().getValue();
		}

	}

	private Map<String, Entity> allEntieisByName;
	private Map<Integer, Entity> allEntitiesById;
	private Map<String, Entity> allEntitiesByLocation;
	private List<FileEntity> fileEntities;
	private Map<String, Integer> namespaceEntities;
	private int nextAvaliableIndex;

	/**
	 * 初始化externVarList和externFuncList两个ArrayList，分别存储extern storage类型的VarEntity和FunctionEntity对象。
	 */
	ArrayList<VarEntity> externVarList = new ArrayList<>();
	ArrayList<FunctionEntity> externFuncList = new ArrayList<>();


	/**
	 * Generate a global unique ID for entity
	 *
	 * @return the unique id
	 */

	public Integer generateId() {
		return nextAvaliableIndex++;
	}

	public EntityRepo() {
		allEntieisByName = new TreeMap<>();
		allEntitiesById = new TreeMap<>();
		allEntitiesByLocation = new TreeMap<>();
		fileEntities = new ArrayList<FileEntity>();
		namespaceEntities = new TreeMap<>();
	}

	public Entity getEntityByName(String entityQualifiedName) {
		return allEntieisByName.get(entityQualifiedName);
	}

	public Entity getEntity(Integer entityId) {
		return allEntitiesById.get(entityId);
	}

	public Entity getEntityByLocation(String information) {
		return allEntitiesByLocation.get(information);
	}

	/*
	 * 仅用于声明和实现分离时，给定新的位置信息, locationInfo: file.getQualifiedName() + startOffset()
	 */
	public void addEntityByLocation(String locationInfo, Entity entity){
		allEntitiesByLocation.put(locationInfo, entity);
	}

	public void add(Entity entity) {
		allEntitiesById.put(entity.getId(), entity);
		if(entity.getLocation() == null) {
			allEntitiesByLocation.put(entity.getQualifiedName(), entity);
		}
		else{
			allEntitiesByLocation.put(allEntitiesById.get(entity.getLocation().getFile()).getQualifiedName()
					+ entity.getLocation().getStartOffset(), entity);
		}
		String Qualifiedname = entity.getQualifiedName();
		if (entity.getQualifiedName() != null && !(entity.getQualifiedName().isEmpty())) {
			Qualifiedname = entity.getQualifiedName();
		}
		if (entity instanceof FileEntity) {
			fileEntities.add((FileEntity) entity);
		}
		if (entity.getStorgaeClass() == IASTDeclSpecifier.sc_extern){

		}else if (allEntieisByName.containsKey(Qualifiedname)) {
			Entity existedEntity = allEntieisByName.get(Qualifiedname);
		} else {
			allEntieisByName.put(Qualifiedname, entity);
		}
		if (entity.getParent() != null)
			Entity.setParent(entity, entity.getParent());
		if(entity instanceof NamespaceEntity) namespaceEntities.put(entity.getQualifiedName(), entity.getId());
	}

	public Iterator<Entity> entityIterator() {
		return new EntityＭapIterator(allEntitiesById.entrySet());
	}

	public List<FileEntity> getFileEntities() {
		return fileEntities;
	}

	public Map<Integer, Entity> getEntities() {
		return allEntitiesById;
	}

	public Integer getNamespace(String name){
		if(namespaceEntities.get(name) != null)
			return namespaceEntities.get(name);
		return -1;
	}

	public void externVarListAdd(VarEntity var) {
		externVarList.add(var);
	}

	public ArrayList<VarEntity> getExternVarList() {
		return externVarList;
	}

	public void externFuncListAdd(FunctionEntity func) {
		externFuncList.add(func);
	}

	public ArrayList<FunctionEntity> getExternFuncList() {
		return externFuncList;
	}
}