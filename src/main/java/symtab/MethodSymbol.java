package symtab;

/** A method symbol is a function that lives within an aggregate/class and has a slot number. */
public class MethodSymbol extends FunctionSymbol implements MemberSymbol {
	protected int slot = -1;

	public MethodSymbol(String name, int id) {
		super(name, id);
	}

	@Override
	public int getSlotNumber() { return slot; }
}
