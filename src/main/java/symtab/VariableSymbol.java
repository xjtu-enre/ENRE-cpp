package symtab;

public class VariableSymbol extends BaseSymbol implements TypedSymbol {
	public VariableSymbol(String name, int id) {
		super(name, id);
	}

	@Override
	public void setType(Type type) {
		super.setType(type);
	}
}
