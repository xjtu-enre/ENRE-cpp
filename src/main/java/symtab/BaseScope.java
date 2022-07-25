package symtab;

import util.Configure;

import java.util.*;

/** An abstract base class that houses common functionality for scopes. */
public abstract class BaseScope implements Scope {
	protected Scope enclosingScope; // null if this scope is the root of the scope tree

	/** All symbols defined in this scope; can include classes, functions,
	 *  variables, or anything else that is a Symbol impl. It does NOT
	 *  include non-Symbol-based things like LocalScope. See nestedScopes.
	 */
	protected List<LinkedHashMap<String, Symbol>> symbols = new ArrayList<>();

	/** All directly contained scopes, typically LocalScopes within a
	 *  LocalScope or a LocalScope within a FunctionSymbol. This does not
	 *  include SymbolWithScope objects.
	 */
	protected List<Scope> nestedScopesNotSymbols = new ArrayList<>();

	public BaseScope() {
		for(int i = 0; i< Configure.ENTITY_KIND_NUM; i++){
			this.symbols.add(new LinkedHashMap<String,Symbol>());
		}
	}

	public BaseScope(Scope enclosingScope) {
		setEnclosingScope(enclosingScope);
		for(int i = 0; i< Configure.ENTITY_KIND_NUM; i++){
			this.symbols.add(new LinkedHashMap<String,Symbol>());
		}
	}

//	public Map<String, ? extends Symbol> getMembers() {
//		return symbols;
//	}

	@Override
	public Symbol getSymbolByKind(String name, int kind) {
		return symbols.get(kind).get(name);
	}


	@Override
	public Symbol getSymbol(String name) {
		for(LinkedHashMap<String, Symbol> scope:symbols){
			if(scope.get(name)!=null) return scope.get(name);
		}
		return null;
	}

	@Override
	public void setEnclosingScope(Scope enclosingScope) {
		this.enclosingScope = enclosingScope;
	}

	public List<Scope> getAllNestedScopedSymbols() {
		List<Scope> scopes = new ArrayList<Scope>();
		Utils.getAllNestedScopedSymbols(this, scopes);
		return scopes;
	}

	@Override
	public List<Scope> getNestedScopedSymbols() {
		List<? extends Symbol> scopes = Utils.filter(getSymbols(), s -> s instanceof Scope);
		return (List)scopes; // force it to cast
	}

	@Override
	public List<Scope> getNestedScopes() {
		ArrayList<Scope> all = new ArrayList<>();
		all.addAll(getNestedScopedSymbols());
		all.addAll(nestedScopesNotSymbols);
		return all;
	}

	/** Add a nested scope to this scope; could also be a FunctionSymbol
	 *  if your language allows nested functions.
	 */
	@Override
	public void nest(Scope scope) throws IllegalArgumentException {
		if ( scope instanceof SymbolWithScope ) {
			throw new IllegalArgumentException("Add SymbolWithScope instance "+
												   scope.getName()+" via define()");
		}
		nestedScopesNotSymbols.add(scope);
	}

	@Override
	public Symbol resolve(String name, int kind) {
		Symbol s = symbols.get(kind).get(name);;
		if ( s!=null ) {
			return s;
		}
		// if not here, check any enclosing scope
		Scope parent = getEnclosingScope();
		if ( parent != null ) return parent.resolve(name, kind);
		return null; // not found
	}

	public void define(Symbol sym, int kind) throws IllegalArgumentException {
		if ( symbols.get(kind).containsKey(sym.getName()) ) {
			throw new IllegalArgumentException("duplicate symbol "+sym.getName());
		}
		sym.setScope(this);
		sym.setInsertionOrderNumber(symbols.size()); // set to insertion position from 0
		symbols.get(kind).put(sym.getName(), sym);
	}

	public Scope getEnclosingScope() { return enclosingScope; }

	/** Walk up enclosingScope until we find topmost. Note this is
	 *  enclosing scope not necessarily parent. This will usually be
	 *  a global scope or something, depending on your scope tree.
	 */
	public Scope getOuterMostEnclosingScope() {
		Scope s = this;
		while ( s.getEnclosingScope()!=null ) {
			s = s.getEnclosingScope();
		}
		return s;
	}

	/** Walk up enclosingScope until we find an object of a specific type.
	 *  E.g., if you want to get enclosing method, you would pass in
	 *  MethodSymbol.class, unless of course you have created a subclass for
	 *  your language implementation.
	 */
	public MethodSymbol getEnclosingScopeOfType(Class<?> type) {
		Scope s = this;
		while ( s!=null ) {
			if ( s.getClass()==type ) {
				return (MethodSymbol)s;
			}
			s = s.getEnclosingScope();
		}
		return null;
	}

	@Override
	public List<Scope> getEnclosingPathToRoot() {
		List<Scope> scopes = new ArrayList<>();
		Scope s = this;
		while ( s!=null ) {
			scopes.add(s);
			s = s.getEnclosingScope();
		}
		return scopes;
	}

	@Override
	public List<? extends Symbol> getSymbols() {
		Collection<Symbol> values = null;
		for(int i=0;i<symbols.size();i++){
			if(values == null) values = symbols.get(i).values();
			else values.addAll(symbols.get(i).values());
		}
		if ( values instanceof List ) {
			return (List<Symbol>)values;
		}
		return new ArrayList<>(values);
	}

	public List<? extends Symbol> getAllSymbols() {
		List<Symbol> syms = new ArrayList<>();
		syms.addAll(getSymbols());
		for(LinkedHashMap<String, Symbol> symbolkind:symbols){
			for (Symbol s : symbolkind.values()) {
				if ( s instanceof Scope ) {
					Scope scope = (Scope)s;
					syms.addAll(scope.getAllSymbols());
				}
			}
		}

		return syms;
	}

	@Override
	public int getNumberOfSymbols() {
		return symbols.size();
	}

	@Override
	public Set<String> getSymbolNames() {
		Set<String> key = new HashSet<>();
		for(LinkedHashMap<String, Symbol> symbolkind:symbols){
			key.addAll(symbolkind.keySet());
		}
		return key;
	}

	public String toString() { return this.getSymbolNames().toString(); }

	public String toScopeStackString(String separator) {
		return Utils.toScopeStackString(this, separator);
	}

	public String toQualifierString(String separator) {
		return Utils.toQualifierString(this, separator);
	}

	public String toTestString() {
		return toTestString(", ", ".");
	}

	public String toTestString(String separator, String scopePathSeparator) {
		List<? extends Symbol> allSymbols = this.getAllSymbols();
		List<String> syms = Utils.map(allSymbols, s -> s.getScope().getName() + scopePathSeparator + s.getName());
		return Utils.join(syms, separator);
	}
}
