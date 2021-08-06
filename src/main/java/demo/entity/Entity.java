package demo.entity;

import java.util.*;
import org.antlr.symtab.BaseScope;
import org.antlr.symtab.Scope;

/**
 * Entity is the root of all entities, including file, package, module, 
 * class, method/function etc.
 * Each entity has unique id, name,qualifiedName, parent, children
 * We also use entity to record relations 
 */
public abstract class Entity {
	
    Integer id = -1;
	String qualifiedName;
	String name;
	String codesnippet;
	Entity parent;
	Set<Entity> children;
	
	private Location location = new Location();
	private ArrayList<Expression> expressionList;
	protected HashMap<String, Entity> visibleNames = new HashMap<>();
	ArrayList<Relation> relations;
	Scope scope = null;
	int visibilityLabel = 1;
	

	 public Entity() {};
	 public Entity(String name, String qualifiedName, Entity parent, Integer id) {//GenericName rawName,
		 this.name = name;
		 this.qualifiedName = qualifiedName;
		 this.parent = parent;
		 this.id = id;
		 if (parent!=null)
			 parent.addChild(this);
		 visibleNames.put(qualifiedName, this);
		}

	    private Set<Entity> children() {
	    	if (children==null)
	    		children = new HashSet<>();
			return children;
		}
	    
	    public Scope getScope() {
	    	return scope;
	    }

		public Integer getId() {
	        return id;
	    }

	    public void addRelation(Relation relation) {
	    	if (relations==null)
	    		relations = new ArrayList<>();
	    	//if (relation.getEntity()==null) return;
	        relations.add(relation);
	    }

	    public ArrayList<Relation> getRelations() {
	    	if (relations==null)
	    		return new ArrayList<>();
	        return relations;
	    }

	    public void addChild(Entity child) {
	    	children().add(child);
			//visibleNames.put(child.getRawName().getName(), child);
			visibleNames.put(child.getQualifiedName(), child);
	    }

		public Entity getParent() {
			return parent;
		}

		public void setParent(Entity parent) {
			this.parent = parent;
			parent.addChild(this);
		}
		
		public Collection<Entity> getChildren() {
			if (children==null)
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

		@Override
		public String toString() {
			if(parent == null) return "Entity [id=" + id + ", qualifiedName=" + qualifiedName  + "]";
			//if(this.getStartLine() == null) return "Entity [id=" + id + ", Name=" + name  +", qualifiedName=" + qualifiedName  +",class="+this.getClass().toString()+ ",parent="+ parent.qualifiedName+"]";   //+ ", rawName=" + rawName
			return "Entity [id=" + id + ", Name=" + name  +", qualifiedName=" + qualifiedName  +",class="+this.getClass().toString()+ ",parent="+ parent.qualifiedName+"]" + ",startline=" + location.getStartLine();   //+ ", rawName=" + rawName
		}

		/**
		 * Get ancestor of type.  
		 * @param classType
		 * @return null (if not exist) or the type
		 */
		public Entity getAncestor() {
			Entity fromEntity = this;
			while(fromEntity.getParent()!=null) {
				fromEntity = fromEntity.getParent();
			}
			return fromEntity;
		}

		/**
		 * Invoke inferer to resolve the entity type etc. 
		
		public void inferEntities(Inferer inferer) {
			inferLocalLevelEntities(inferer);
			for (Entity child:this.getChildren()) {
				child.inferEntities(inferer);
			}
		}
		public abstract void inferLocalLevelEntities(Inferer inferer);
		
		 * */

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
			if (searched.contains(this)) return null;
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
		
		public void addExpresion(Expression expression) {

			this.expressionList().add(expression);
		}
		public List<Expression> expressionList() {
			if (expressionList==null) 
				expressionList = new ArrayList<>();
			return expressionList;
		}
		public void setVisibilityLabel(int label) {
			this.visibilityLabel = label;
		}
		public int getVisibilityLabel() {
			return this.visibilityLabel;
		}
		public List<Entity> resolveExpression(){
			if(expressionList == null) return null;
			List<Entity> entitylist = new ArrayList<Entity>();
			for(Expression expression:expressionList) {
				if(expression.isCall()) {
					Entity ancestor = this.getAncestor();
								
					for(Entity child:ancestor.getChild()) {
						if(child instanceof FunctionEntity && child.getQualifiedName().equals(expression.getExpression())) {
							expression.addReferredEntity(child);
						}
					}
				}
			}
			return entitylist;
		}
		public List<Entity> getChild(){
			List<Entity> childlist = new ArrayList<Entity>();
			Entity ancestor = this;
			if(ancestor.getChildren()!=null) {
				for(Entity child:ancestor.getChildren()) {
					childlist.add(child);
					if(child.getChild()!=null) {
						for(Entity child_child:child.getChild())
							childlist.add(child_child);
					}
				}
			}
				
			
			return childlist;
		}
	
}
