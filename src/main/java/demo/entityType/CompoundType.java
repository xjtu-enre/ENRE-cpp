package demo.entityType;

public class CompoundType extends Type{
	
	private static final String VOID = "VOID";
    private static final String NULLPTR = "NULLPTR";
    private static final String ARITHMETIC = "ARITHETIC";

	public CompoundType(String typeName) {
		super(typeName);
	}
	
	public CompoundType(int i) {
		super(VOID);
	}
	

}
