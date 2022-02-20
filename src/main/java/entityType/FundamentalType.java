package entityType;

public class FundamentalType  extends Type{
	
	private static final String VOID = "VOID";
    private static final String NULLPTR = "NULLPTR";
    private static final String ARITHMETIC = "ARITHETIC";

	public FundamentalType(String typeName) {
		super(typeName);
	}
	
	public FundamentalType(int i) {
		super(VOID);
	}
	

}
