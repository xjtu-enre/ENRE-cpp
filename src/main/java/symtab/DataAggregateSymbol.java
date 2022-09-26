package symtab;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/** A symbol representing a collection of data like a struct or class.
 *  Each member has a slot number indexed from 0 and we track data fields
 *  and methods with different slot sequences. A DataAggregateSymbol
 *  can also be a member of an aggregate itself (nested structs, ...).
 */
public class DataAggregateSymbol extends SymbolWithScope implements MemberSymbol, Type {
	protected ParserRuleContext defNode;
	protected int nextFreeFieldSlot = 0;  // next slot to allocate
	protected int typeIndex;

	public DataAggregateSymbol(String name, int id) {
		super(name, id);
	}

	public void setDefNode(ParserRuleContext defNode) {
		this.defNode = defNode;
	}

	public ParserRuleContext getDefNode() {
		return defNode;
	}

	@Override
	public void define(Symbol sym, int kind) throws IllegalArgumentException {
//		if ( !(sym instanceof MemberSymbol) ) {
//			throw new IllegalArgumentException(
//				"sym is "+sym.getClass().getSimpleName()+" not MemberSymbol"
//			);
//		}
		super.define(sym, kind);
		setSlotNumber(sym);
	}

	@Override
	public List<LinkedHashMap<String, Symbol>> getSymbols() {
		return super.getSymbols();
	}

//	@Override
//	public Map<String, ? extends MemberSymbol> getMembers() {
//		return (Map<String, ? extends MemberSymbol>)super.getMembers();
//	}

	/** Look up name within this scope only. Return any kind of MemberSymbol found
	 *  or null if nothing with this name found as MemberSymbol.
	 */
	public Symbol resolveMember(String name, int kind) {
		Symbol s = symbols.get(kind).get(name);
		if ( s instanceof MemberSymbol ) {
			return s;
		}
		return null;
	}

	/** Look for a field with this name in this scope only.
	 *  Return null if no field found.
	 */
	public Symbol resolveField(String name, int kind) {
		Symbol s = resolveMember(name, kind);
		if ( s instanceof FieldSymbol ) {
			return s;
		}
		return null;
	}

	public void setSlotNumber(Symbol sym) {
		if ( sym instanceof FieldSymbol) {
			FieldSymbol fsym = (FieldSymbol)sym;
			fsym.slot = nextFreeFieldSlot++;
		}
	}

	@Override
	public int getSlotNumber() {
		return -1; // class definitions do not yield either field or method slots; they are just nested
	}

	@Override
	public int getTypeIndex() { return typeIndex; }

	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}
}
