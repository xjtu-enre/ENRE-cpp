package entityType;

import entity.TypeRecord;

public class FundamentalType  extends TypeRecord {
	
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
