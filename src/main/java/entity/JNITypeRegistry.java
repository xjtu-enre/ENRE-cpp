package entity;

import java.util.*;

public class JNITypeRegistry {
    // 全静态 Map
    private static final Map<String, JNITypeInfo> typeMap = new HashMap<>();

    // 静态代码块：类加载时自动执行一次
    static {
        loadDefaultTypes();
    }

    // 私有构造函数，防止被实例化
    private JNITypeRegistry() {}

    private static void loadDefaultTypes() {
        // 基本类型
        addType(new JNITypeInfo("jboolean", "boolean", "jboolean", false, "8-bit boolean"));
        addType(new JNITypeInfo("jbyte", "byte", "jbyte", false, "8-bit signed integer"));
        addType(new JNITypeInfo("jchar", "char", "jchar", false, "16-bit unsigned Unicode character"));
        addType(new JNITypeInfo("jshort", "short", "jshort", false, "16-bit signed integer"));
        addType(new JNITypeInfo("jint", "int", "jint", false, "32-bit signed integer"));
        addType(new JNITypeInfo("jlong", "long", "jlong", false, "64-bit signed integer"));
        addType(new JNITypeInfo("jfloat", "float", "jfloat", false, "32-bit floating point"));
        addType(new JNITypeInfo("jdouble", "double", "jdouble", false, "64-bit floating point"));
        addType(new JNITypeInfo("jvoid", "void", "void", false, "void type"));

        // 基本类型数组
        addType(new JNITypeInfo("jbooleanArray", "boolean[]", "jbooleanArray", true, "Array of boolean"));
        addType(new JNITypeInfo("jbyteArray", "byte[]", "jbyteArray", true, "Array of bytes"));
        addType(new JNITypeInfo("jcharArray", "char[]", "jcharArray", true, "Array of chars"));
        addType(new JNITypeInfo("jshortArray", "short[]", "jshortArray", true, "Array of shorts"));
        addType(new JNITypeInfo("jintArray", "int[]", "jintArray", true, "Array of ints"));
        addType(new JNITypeInfo("jlongArray", "long[]", "jlongArray", true, "Array of longs"));
        addType(new JNITypeInfo("jfloatArray", "float[]", "jfloatArray", true, "Array of floats"));
        addType(new JNITypeInfo("jdoubleArray", "double[]", "jdoubleArray", true, "Array of doubles"));

        // 引用类型（Java 对象）
        addType(new JNITypeInfo("jobject", "Object", "jobject", false, "Generic Java object"));
        addType(new JNITypeInfo("jclass", "Class<?>", "jclass", false, "Java class reference"));
        addType(new JNITypeInfo("jstring", "String", "jstring", false, "UTF-16 Java string"));
        addType(new JNITypeInfo("jthrowable", "Throwable", "jthrowable", false, "Java exception"));
        addType(new JNITypeInfo("jobjectArray", "Object[]", "jobjectArray", true, "Array of Java objects"));

        // 特殊引用类型
        addType(new JNITypeInfo("jweak", "WeakReference<?>", "jweak", false, "Weak global reference"));
        addType(new JNITypeInfo("jvalue", "Object (union)", "jvalue", false, "Union for JNI argument values"));

        // JNI 结构体与函数上下文
        addType(new JNITypeInfo("JNIEnv", "JNIEnv*", "JNIEnv*", false, "JNI environment pointer"));
        addType(new JNITypeInfo("JavaVM", "JavaVM*", "JavaVM*", false, "Java Virtual Machine interface pointer"));
        addType(new JNITypeInfo("jmethodID", "long", "jmethodID", false, "Method identifier"));
        addType(new JNITypeInfo("jfieldID", "long", "jfieldID", false, "Field identifier"));

        // 常见宏（可用于识别 JNIEXPORT / JNICALL）
        addType(new JNITypeInfo("JNIEXPORT", "", "JNIEXPORT", false, "JNI export specifier (macro)"));
        addType(new JNITypeInfo("JNICALL", "", "JNICALL", false, "JNI call specifier (macro)"));

        // 额外类型（扩展 JNI）
        addType(new JNITypeInfo("jsize", "int", "jsize", false, "Size type used by JNI (32-bit signed integer)"));
        addType(new JNITypeInfo("jbooleanRef", "boolean*", "jboolean*", false, "Pointer to boolean value"));
    }

    // 添加类型
    public static void addType(JNITypeInfo info) {
        typeMap.put(info.getJniName(), info);
    }

    // 获取类型信息
    public static JNITypeInfo getType(String jniName) {
        return typeMap.get(jniName);
    }

    // 判断是否为 JNI 类型
    public static boolean isJNIType(String name) {
        return typeMap.containsKey(name);
    }

    // 获取所有类型
    public static Collection<JNITypeInfo> getAllTypes() {
        return typeMap.values();
    }
}
