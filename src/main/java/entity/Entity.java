package entity;

import relation.BindingRelation;
import relation.ObjectRelation;
import relation.Relation;
import relation.ScopeRelation;
import util.Tuple;
import symtab.Scope;

import java.util.*;

/**
 * Entity is the root of all entities, including file, package, module, class,
 * method/function etc. Each entity has unique id, name,qualifiedName, parent,
 * children We also use entity to record relations
 */
public abstract class Entity {
	Integer id = -1;
	/*
	 * external id is used to record the id of entity in other system, such as
	 */
	Integer externalId = -1;
	String qualifiedName;
	String name;
	Entity parent;
	Set<Entity> children;
	private Location location;

	private List<BindingRelation> RelationListByBinding = new ArrayList<BindingRelation>();
	private ArrayList<ScopeRelation> RelationListByScope = new ArrayList<ScopeRelation>();
	private ArrayList<ObjectRelation> RelationListByObject = new ArrayList<ObjectRelation>();
	private ArrayList<Relation> relations = new ArrayList<Relation>();

	protected HashMap<String, Entity> visibleNames = new HashMap<>();

	Scope scope = null;
	int visibilityLabel = 1;
	int visibility = -1;
	int storage_class = -1;

	boolean global = false;
	boolean isPointer = false;
	boolean isTemplate = false;


	public Entity(String name, String qualifiedName, Entity parent, Integer id) {
		this.name = name;
		this.qualifiedName = qualifiedName;
		this.parent = parent;
		this.id = id;
		if (parent != null)
			parent.addChild(this);
		this.location = location;
		visibleNames.put(qualifiedName, this);
	}

	public Entity(String name, String qualifiedName, Entity parent, Integer id, Location location) {
		this.name = name;
		this.qualifiedName = qualifiedName;
		this.parent = parent;
		this.id = id;
		if (parent != null)
			parent.addChild(this);
		this.location = location;
		visibleNames.put(qualifiedName, this);
	}

	private Set<Entity> children() {
		if (children == null) children = new HashSet<>();
		return children;
	}

	public Scope getScope() {
		return scope;
	}
	public Integer getId() {
		return id;
	}



	public void addChild(Entity child) {
		children().add(child);
		// visibleNames.put(child.getRawName().getName(), child);
		visibleNames.put(child.getQualifiedName(), child);
	}

	public Entity getParent() {
		return parent;
	}
	public Integer getParentId(){
		if(parent == null) return -1;
		return parent.getId();
	}

	public void setParent(Entity parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	public Collection<Entity> getChildren() {
		if (children == null)
			return new HashSet<>();
		return children;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	public final String getName() {
		return name;
	}
	public final String getQualifiedName() {
		return qualifiedName;
	}
	public String getQualifiedName(boolean overrideFileWithPackage) {
		return qualifiedName;
	}
	public void setLocation(Location location) {this.location = location;}

//	@Override
//	public String toString() {
//		if (parent == null)
//			return "Entity [id=" + id + ", qualifiedName=" + qualifiedName + "]";
//		return "Entity [id=" + id + ", Name=" + name + ", qualifiedName=" + qualifiedName + ",class="
//				+ this.getClass().toString() + ",parent=" + parent.qualifiedName + "]" + ",startline="
//				+ location.getStartLine(); // + ", rawName=" + rawName
//	}

	/**
	 * Get ancestor of type.
	 * 
	 * @return null (if not exist) or the type
	 */
	public Entity getAncestor() {
		Entity fromEntity = this;
		while (fromEntity.getParent() != null) {
			fromEntity = fromEntity.getParent();
		}
		return fromEntity;
	}


	public static void setParent(Entity child, Entity parent) {
		if (parent == null)
			return;
		if (child == null)
			return;
		if (parent.equals(child.getParent()))
			return;
		child.setParent(parent);
		parent.addChild(child);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Entity getByName(String name, HashSet<Entity> searched) {
		if (searched.contains(this))
			return null;
		searched.add(this);
		return visibleNames.get(name);
	}

	public Integer getStartLine() {
		return location.getStartLine();
	}

	public void setStartLine(int lineNumber) {
		this.location.setStartLine(lineNumber);
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLine(int lineNumber) {
		this.location.setStartLine(lineNumber);
	}

	public void setVisibilityLabel(int label) {
		this.visibilityLabel = label;
	}

	public int getVisibilityLabel() {
		return this.visibilityLabel;
	}

	public void setVisiblity(int tag) { this.visibility = tag; }

	public int getVisiblity() {return this.visibility; }

	public void setStorageClass(int tag) { this.storage_class = tag; }

	public int getStorgaeClass() { return this.storage_class; }

	public void setGlobal(){ this.global = true; }

	public boolean getGlobal() { return this.global; }

	public void setPointer(){
		this.isPointer = true;
	}
	public boolean getPointer() {
		return this.isPointer;
	}

	/*
	 * Determines whether this entity is a template
	 */
	public void setTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	public boolean getIsTemplate() {
		return this.isTemplate;
	}


	public List<Entity> getChild() {
		List<Entity> childlist = new ArrayList<Entity>();
		Entity ancestor = this;
		if (ancestor.getChildren() != null) {
			for (Entity child : ancestor.getChildren()) {
				childlist.add(child);
				if (child.getChild() != null) {
					for (Entity child_child : child.getChild())
						childlist.add(child_child);
				}
			}
		}

		return childlist;
	}

	public void addRelation(Relation relation) {
		if (relations == null) relations = new ArrayList<>();
		relations.add(relation);
	}

	public ArrayList<Relation> getRelations() {
		if (relations == null) return new ArrayList<>();
		return relations;
	}
	
	/*
	 * add and get relation by binding or scope resolution
	 * 
	 */
	public void addBindingRelation(int retype,  String eninfor, Integer fileID, Integer line, Integer offset) {
		this.RelationListByBinding.add(new BindingRelation(retype,  eninfor, fileID, line, offset));
	}
	
	public List<BindingRelation> getRelationListByBinding(){
		return this.RelationListByBinding;
	}
	
	public void addScopeRelation(int retype, String entityName, Integer fileID, Integer line, Integer offset) {
		this.RelationListByScope.add(new ScopeRelation(this, entityName, retype, fileID, line, offset));
	}

	public void addRelationByObject(String object, String entityName, int relationType, Integer fileID, Integer line, Integer offset) {
		this.RelationListByObject.add(new ObjectRelation(object, this, entityName, relationType, fileID, line, offset));
	}

	public List<ScopeRelation> getRelationListByScope(){
		return this.RelationListByScope;
	}

	public List<ObjectRelation> getRelationListByObject(){
		return this.RelationListByObject;
	}

	public void setExternalId(Integer id) {
		this.externalId = id;
	}

	public Integer getExternalId() {
		return this.externalId;
	}

}
