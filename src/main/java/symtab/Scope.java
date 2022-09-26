package symtab;

import java.util.List;
import java.util.Set;

/** A scope is a dictionary of symbols that are grouped together by some
 *  lexical construct in the input language. Examples include structs,
 *  functions, {...} code blocks, argument lists, etc...
 *
 *  Scopes all have an enclosing scope that encloses them lexically.
 *  In other words, am I wrapped in a class? a function? a {...} code block?
 *
 *  This is distinguished from the parent scope. The parent scope is usually
 *  the enclosing scope, but in the case of inheritance, it is the superclass
 *  rather than the enclosing scope. Otherwise, the global scope would be
 *  considered the parent scope of a class. When resolving symbols, we look
 *  up the parent scope chain not the enclosing scope chain.
 *
 *  For convenience of code using this library, I have added a bunch of
 *  methods one can use to get lots of useful information from a scope, but
 *  they don't necessarily define what a scope is.
 */
public interface Scope {
	/** Often scopes have names like function or class names. For
	 *  unnamed scopes like code blocks, you can just return "local" or something.
	 */
	String getName();

	/** Scope in which this scope defined. null if no enclosing scope */
	Scope getEnclosingScope();

	/** What scope encloses this scope. E.g., if this scope is a function,
	 *  the enclosing scope could be a class. The BaseScope class automatically
	 *  adds this to nested scope list of s.
	 */
	void setEnclosingScope(Scope s);

	/** Define a symbol in this scope, throw IllegalArgumentException
	 *  if sym already defined in this scope. This alters sym:
	 *
	 *  1. Set insertion order number of sym
	 *  2. Set sym's scope to be the scope.
	 *
	 *  The order in which symbols are defined must be preserved so that
	 *  returns the list in definition order.
	 */
	void define(Symbol sym, int kind) throws IllegalArgumentException;

	/** Look up name in this scope or recursively in parent scope if not here */
	Symbol resolve(String name, int kind);

	/** Get symbol if name defined within this specific scope */
	Symbol getSymbolByKind(String name, int kind);

	Symbol getSymbol(String name);

	/** Add a nested local scope to this scope; it's like define() but
	 *  for non SymbolWithScope objects. E.g., a FunctionSymbol will
	 *  add a LocalScope for its block via this method.
	 *
	 *  @throws IllegalArgumentException if you pass in a SymbolWithScope.
	 */
	void nest(Scope scope) throws IllegalArgumentException;


	// ------------ Convenience methods --------------------------------

	/** Return (inclusive) list of all scopes on path to root scope.
	 *  The first element is the current scope and the last is the root scope.
	 */
	List<Scope> getEnclosingPathToRoot();

	/** Return the set of names associated with all symbols in the scope. */
	Set<String> getSymbolNames();

	/** Number of symbols in this specific scope */
	int getNumberOfSymbols();

	/** Return scopes from to current with separator in between */
	public String toQualifierString(String separator);

	void union(Scope scope);
}
