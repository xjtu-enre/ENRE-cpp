package util;

public class BuiltInDeal {
    protected int TYPEDEF_SIGNED_CHAR = -1;
    protected int TYPEDEF_UNSIGNED_CHAR = -2;
    protected int TYPEDEF_SIGNED_SHORT = -3;
    protected int TYPEDEF_UNSIGNED_SHORT = -4;
    protected int TYPEDEF_SIGNED_INT = -5;
    protected int TYPEDEF_UNSIGNED_INT = -6;
    protected int TYPEDEF_SIGNED_LONG_LONG = -7;
    protected int TYPEDEF_UNSIGNED_LONGLONG = -8;
    protected int DOUBLE = -9;
    protected int INT = -10;
    protected int FLOAT = -11;
    protected int FLOAT_T = -12;
    protected int UNSIGNED = -13;
    protected int BOOL = -14;
    protected int UNSIGNED_LONG = -15;
    protected int UNSIGNED_CHAR = -16;
    protected int STRING = -17;

    public Integer DealBuiltInType(String typeName){
        if(typeName == null) return -1;
        typeName = typeName.replace("extern", "");
        typeName = typeName.replace("const", "");
        typeName = typeName.replace("typedef", "");
        typeName = typeName.strip();
        switch (typeName){
            case  "int8_t":
                return this.TYPEDEF_SIGNED_CHAR;
            case  "uint8_t":
                return this.TYPEDEF_UNSIGNED_CHAR;
            case  "int16_t":
                return this.TYPEDEF_SIGNED_SHORT;
            case  "uint16_t":
                return this.TYPEDEF_UNSIGNED_SHORT;
            case  "int32_t":
                return this.TYPEDEF_SIGNED_INT;
            case  "uint32_t":
                return this.TYPEDEF_UNSIGNED_INT;
            case  "int64_t":
                return this.TYPEDEF_SIGNED_LONG_LONG;
            case  "uint64_t":
                return this.TYPEDEF_UNSIGNED_LONGLONG;
            case  "double":
                return this.DOUBLE;
            case  "int":
                return this.INT;
            case  "float":
                return this.FLOAT;
            case  "float_t":
                return this.FLOAT_T;
            case  "unsigned":
                return this.UNSIGNED;
            case  "bool":
                return this.BOOL;
            case  "unsigned long":
                return this.UNSIGNED_LONG;
            case "unsigned char":
                return this.UNSIGNED_CHAR;
            case "std::string":
                return this.STRING;
        }
        return -1;
    }

    public int DealSimpleBuiltInType(){
        return -1;
    }
}
