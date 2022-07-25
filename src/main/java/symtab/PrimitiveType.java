package symtab;

public class PrimitiveType extends BaseSymbol implements Type {
	protected int typeIndex;

	public PrimitiveType(String name, int id) {
		super(name, id);
	}

	@Override
	public int getTypeIndex() { return typeIndex; }

	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}

	@Override
	public String getName() {
		return name;
	}
}
