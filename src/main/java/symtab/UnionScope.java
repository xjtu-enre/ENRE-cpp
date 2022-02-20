package symtab;

import org.antlr.symtab.DataAggregateSymbol;
import org.antlr.symtab.FieldSymbol;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;

public class UnionScope extends DataAggregateSymbol{

	public UnionScope(String name) {
		super(name);
	}
	

	@Override
	public Symbol resolve(String name) {
		Symbol s = resolveMember(name);
		if ( s!=null ) {
			return s;
		}
		// if not a member, check any enclosing scope. it might be a global variable for example
		Scope parent = getEnclosingScope();
		if ( parent != null ) return parent.resolve(name);
		return null; // not found
	}

	

	/** Look for a field with this name in this scope or any super class.
	 *  Return null if no field found.
	 */
	@Override
	public Symbol resolveField(String name) {
		Symbol s = resolveMember(name);
		if ( s instanceof FieldSymbol ) {
			return s;
		}
		return null;
	}

}
