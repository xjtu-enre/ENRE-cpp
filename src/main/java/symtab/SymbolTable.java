package symtab;

/** A marginally useful object to track predefined and global scopes. */
public class SymbolTable {
	public static final Type INVALID_TYPE = new InvalidType();

	public BaseScope PREDEFINED = new PredefinedScope();
	public GlobalScope GLOBALS = new GlobalScope(PREDEFINED);

	public SymbolTable() {
	}

	public void initTypeSystem() {
	}

	public void definePredefinedSymbol(Symbol s, int kind) {
		PREDEFINED.define(s, kind);
	}

	public void defineGlobalSymbol(Symbol s, int kind) {
		GLOBALS.define(s, kind);
	}
}
