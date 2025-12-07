package entity;

import java.io.Serializable;

public class JNITypeInfo implements Serializable {
    private String jniName;        // JNI 类型名称，例如 jbyteArray
    private String javaType;       // 对应的 Java 类型，例如 byte[]
    private String cppType;        // 对应的 C++ 类型，例如 jbyteArray
    private boolean isArray;       // 是否是数组类型
    private String description;    // 类型说明，可选

    public JNITypeInfo(String jniName, String javaType, String cppType, boolean isArray, String description) {
        this.jniName = jniName;
        this.javaType = javaType;
        this.cppType = cppType;
        this.isArray = isArray;
        this.description = description;
    }

    // Getters / Setters
    public String getJniName() { return jniName; }
    public String getJavaType() { return javaType; }
    public String getCppType() { return cppType; }
    public boolean isArray() { return isArray; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("JNITypeInfo[%s -> Java:%s, C++:%s, Array:%b]",
                jniName, javaType, cppType, isArray);
    }
}
