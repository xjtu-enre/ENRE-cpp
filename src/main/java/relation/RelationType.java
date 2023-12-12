package relation;

public class RelationType {
	/*
         The category of Relation
    */
	public static final int DEFINE = 1;
	public static final int DECLARE = 2;
	public static final int RETURN = 3;
	public static final int SET = 4;
	public static final int INCLUDE = 5;
	public static final int USE = 6;
	public static final int CALL = 7;
	public static final int UNRESOLVED = 8;
	public static final int CAST = 9;
	public static final int DELETE = 10;
	public static final int MODIFY = 11;
	public static final int USING = 12;
	public static final int FRIEND = 13;
	public static final int PARAMETER = 14;
	public static final int ALIAS = 15;
	public static final int OVERRIDE = 16;
	public static final int EMBED = 17;
	public static final int TYPEUSE = 18;
	public static final int ADDR_PARAMETER_USE = 19;
	public static final int PARAMETER_USE = 20;
	public static final int PARAMETER_USE_FIELD_REFERENCE = 21;
	public static final int TYPE = 22;
	public static final int EXTEND = 23;

//	public static String getRelationCategory(int typeID){
//		switch (typeID){
//			case DEFINE:
//				return "Define";
//			case DECLARE:
//				return "Declare";
//			case RETURN:
//				return "Return";
//			case SET:
//				return "Set";
//			case INCLUDE:
//				return "Include";
//			case USE:
//				return "Use";
//			case CALL:
//				return "Call";
//			case UNRESOLVED:
//				return "Unresolved";
//			case CAST:
//				return "Cast";
//			case DELETE:
//				return "Delete";
//			case MODIFY:
//				return "Modify";
//			case USING:
//				return "Using";
//			case FRIEND:
//				return "Friend";
//			case PARAMETER:
//				return "Parameter";
//			case ALIAS:
//				return "Alias";
//			case OVERRIDE:
//				return "Override";
//			case EMBED:
//				return "Embed";
//			case TYPEUSE:
//				return "TypeUse";
//			case ADDR_PARAMETER_USE:
//				return "Addr Parameter Use";
//			case PARAMETER_USE:
//				return "Parameter Use";
//			case PARAMETER_USE_FIELD_REFERENCE:
//				return "Parameter Use Field Reference";
//			case TYPE:
//				return "Type";
//			case EXTEND:
//				return "Extend";
//		}
//		// If none of the above cases matched, return null. You may want to return a default value or throw an exception instead.
//		return null;
//	}
public static String getRelationCategory(int typeID){
	switch (typeID){
		case DEFINE:
			return "Contains";
		case DECLARE:
			return "Declares";
		case RETURN:
			return "Return";
		case SET:
			return "Sets";
		case INCLUDE:
			return "Includes";
		case USE:
			return "Reads";
		case CALL:
			return "Calls";
		case UNRESOLVED:
			return "Unresolved";
		case CAST:
			return "Cast";
		case DELETE:
			return "Delete";
		case MODIFY:
			return "Modify";
		case USING:
			return "Using";
		case FRIEND:
			return "Friend";
		case PARAMETER:
			return "Parameter";
		case ALIAS:
			return "Alias";
		case OVERRIDE:
			return "Override";
		case EMBED:
			return "Embed";
		case TYPEUSE:
			return "Has Type";
		case ADDR_PARAMETER_USE:
			return "Addr Parameter Use";
		case PARAMETER_USE:
			return "Parameter Use";
		case PARAMETER_USE_FIELD_REFERENCE:
			return "Parameter Use Field Reference";
		case TYPE:
			return "Type";
		case EXTEND:
			return "Extend";
	}
	// If none of the above cases matched, return null. You may want to return a default value or throw an exception instead.
	return null;
}

}
