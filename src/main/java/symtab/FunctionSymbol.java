package symtab;

import org.antlr.v4.runtime.ParserRuleContext;
import util.Configure;

import java.util.ArrayList;
import java.util.List;

/** This symbol represents a function ala C, not a method ala Java.
 *  You can associate a node in the parse tree that is responsible
 *  for defining this symbol.
 */
public class FunctionSymbol extends SymbolWithScope implements TypedSymbol {
	protected ParserRuleContext defNode;
	protected Type retType;
	// to be used as an overload function
	protected boolean overload = false;
	// to be used as the base of all overload functions
	protected boolean overloadBase = false;
	protected List<Integer> overload_list = new ArrayList<Integer>();

	public FunctionSymbol(String name, int id) {
		super(name, id);
	}


	public void setDefNode(ParserRuleContext defNode) {
		this.defNode = defNode;
	}

	public ParserRuleContext getDefNode() {
		return defNode;
	}

	@Override
	public Type getType() {
		return retType;
	}

	@Override
	public void setType(Type type) {
		retType = type;
	}

	/** Return the number of VariableSymbols specifically defined in the scope.
	 *  This is useful as either the number of parameters or the number of
	 *  parameters and locals depending on how you build the scope tree.
	 */
	public int getNumberOfVariables() {
		return Utils.filter(symbols.get(Configure.Variable).values(), s -> s instanceof VariableSymbol).size();
	}

	public int getNumberOfParameters() {
		return Utils.filter(symbols.get(Configure.Variable).values(), s -> s instanceof ParameterSymbol).size();
	}

	public String toString() { return name+":"+super.toString(); }

	public boolean isOverload(){ return this.overload; }
	public void setOverload(){
		this.overload = true;
	}

	public boolean isBaseOverload(){ return this.overloadBase; }
	public void setBaseOverload(){
		this.overloadBase = true;
		this.overload_list.add(this.entityID);
	}

	public void addOverload(Integer id){ this.overload_list.add(id); }
	public List<Integer> getOverload_list() { return this.overload_list; }

}
