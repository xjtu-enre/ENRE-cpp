package entity;

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
	private int nextAvaliableIndex;

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

	public void add(Entity entity) {
		allEntitiesById.put(entity.getId(), entity);
		if(entity.getLocation() == null) {
			allEntitiesByLocation.put(entity.getQualifiedName(), entity);
		}
		else{
			allEntitiesByLocation.put(entity.getLocation().getFileName() + entity.getLocation().getStartLine()
					+ entity.getLocation().getStartColumn(), entity);
		}
				

		String Qualifiedname = entity.getQualifiedName();
		if (entity.getQualifiedName() != null && !(entity.getQualifiedName().isEmpty())) {
			Qualifiedname = entity.getQualifiedName();
		}
		if (entity instanceof FileEntity) {
			fileEntities.add((FileEntity) entity);
		}
		if (allEntieisByName.containsKey(Qualifiedname)) {
			Entity existedEntity = allEntieisByName.get(Qualifiedname);

		} else {
			allEntieisByName.put(Qualifiedname, entity);
		}
		if (entity.getParent() != null)
			Entity.setParent(entity, entity.getParent());
	}

	public Iterator<Entity> entityIterator() {
		return new EntityＭapIterator(allEntitiesById.entrySet());
	}

	public void update(Entity entity) {
	}

	public List<FileEntity> getFileEntities() {
		return fileEntities;
	}

	public Map<Integer, Entity> getEntities() {
		return allEntitiesById;
	}

	public void printAllEntities() {
		for (Integer entityId : allEntitiesById.keySet()) {
			Entity entity = allEntitiesById.get(entityId);
			System.out.println(entity.getName() + entity.getLocation().getFileName()
					+ entity.getLocation().getStartLine() + entity.getLocation().getStartColumn());
		}

	}

}
