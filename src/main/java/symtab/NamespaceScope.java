package symtab;

public class NamespaceScope extends DataAggregateSymbol{

    public NamespaceScope(String name, int id) {
        super(name, id);
    }

    @Override
    public Symbol resolve(String name, int kind) {
        Symbol s = resolveMember(name, kind);
        if ( s!=null ) {
            return s;
        }
        // if not a member, check any enclosing scope. it might be a global variable for example
        Scope parent = getEnclosingScope();
        if ( parent != null ) return parent.resolve(name, kind);
        return null; // not found
    }

    /** Look for a field with this name in this scope or any super class.
     *  Return null if no field found.
     */
    @Override
    public Symbol resolveField(String name, int kind) {
        Symbol s = resolveMember(name, kind);
        if ( s instanceof FieldSymbol ) {
            return s;
        }
        return null;
    }

}