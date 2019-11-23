package com.wsy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class JNIUtil {
    /**
     * 类转签名，如：
     * 将a.b.C类转换为："La/b/C;"
     * 将int转换为："I"
     *
     * @param clazz Java类
     * @return 签名字符串
     */
    public static String class2Signature(Class clazz) {
        if (clazz.isArray()) {
            return "[" + class2Signature(clazz.getComponentType());
        } else {
            if (clazz.isPrimitive()) {
                return primitiveClassToSignature(clazz);
            } else {
                return "L" + clazz.getName().replace(".", "/") + ";";
            }
        }
    }

    /**
     * 基本类型转为签名，如：
     * 将int转换为："I"
     *
     * @param clazz 基本类型，如 int、float等
     * @return 签名字符串
     */
    private static String primitiveClassToSignature(Class clazz) {
        if (clazz == void.class) {
            return "V";
        }
        if (clazz == boolean.class) {
            return "Z";
        }
        if (clazz == byte.class) {
            return "B";
        }
        if (clazz == char.class) {
            return "C";
        }
        if (clazz == short.class) {
            return "S";
        }
        if (clazz == int.class) {
            return "I";
        }
        if (clazz == long.class) {
            return "J";
        }
        if (clazz == float.class) {
            return "F";
        }
        if (clazz == double.class) {
            return "D";
        }
        throw new IllegalArgumentException("class '" + clazz.getName() + "' is not a primitive class");
    }

    /**
     * 生成FindClass的代码
     *
     * @param clazz java类
     * @return FindClass代码字符串
     */
    public static String generateFindClassCode(Class clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("class " + clazz.getName() + " is array!");
        } else {
            if (clazz.isPrimitive()) {
                System.out.println("// primitive class can be used directly in native, for example: ");
                System.out.println("// int -> jint ");
                System.out.println("// float -> jfloat ");
                System.out.println("// boolean -> jboolean ");
                System.out.println("// ....");
                return "j" + lowerFirst(clazz.getSimpleName());
            } else {
                return "jclass " +
                        lowerFirst(clazz.getSimpleName()) +
                        "Clazz = env->FindClass(\"" +
                        clazz.getName().replace(".", "/") +
                        "\");";
            }
        }
    }

    /**
     * 首字母大写
     *
     * @param s 原字符串
     * @return 将首字母转换为大写后的字符串
     */
    private static String upperFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }


    /**
     * 首字母小写
     *
     * @param s 原字符串
     * @return 将首字母转换为小写后的字符串
     */
    private static String lowerFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * 生成获取字段值的代码
     *
     * @param field 字段
     * @return 获取字段值的代码
     */
    public static String generateField2SignatureCode(Field field) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jfieldID ").append(field.getName()).append("ID = ");
        boolean isStatic = Modifier.isStatic(field.getModifiers());

        stringBuilder.append(isStatic ? "env->GetStaticFieldID(" : "env->GetFieldID(")
                .append(lowerFirst(field.getDeclaringClass().getSimpleName())).append("Clazz,")
                .append("\"").append(field.getName()).append("\"").append(",\"")
                .append(class2Signature(field.getType()))
                .append("\");")
                .append("\n")
                .append("j")
                .append(field.getType().isPrimitive() ? (field.getType().getSimpleName()) :
                        (field.getType() == String.class ? "string" : "object"))
                .append(" ")
                .append(field.getName())
                .append("Value = ")
                .append(isStatic ? "env->GetStatic" : "env->Get")
                .append(field.getType().isPrimitive() ? upperFirst(field.getType().getName()) : "Object")
                .append("Field(")
                .append(field.getClass().getSimpleName())
                .append(isStatic ? "Clazz," : "Obj,")
                .append(field.getName())
                .append("ID);");

        return stringBuilder.toString();
    }

    /**
     * 生成调用java函数的代码
     *
     * @param method 函数
     * @return 调用函数的代码
     */
    public static String generateMethod2SignatureCode(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jmethodID ").append(method.getName()).append("ID = ");
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        stringBuilder
                .append(isStatic ? "env->GetStaticMethodID(" : "env->GetMethodID(")
                .append(lowerFirst(method.getDeclaringClass().getSimpleName()))
                .append("Clazz,")
                .append("\"")
                .append(method.getName())
                .append("\"")
                .append(",\"");
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length == 0) {
            stringBuilder.append("()");
        } else {
            stringBuilder.append("(");
            for (Parameter parameter : parameters) {
                stringBuilder.append(class2Signature(parameter.getType()));
            }
            stringBuilder.append(")");
        }
        Class<?> returnType = method.getReturnType();

        stringBuilder.append(class2Signature(returnType))
                .append("\");")
                .append("\n");

        // get
        if (method.getReturnType() != void.class) {
            stringBuilder
                    .append("j")
                    .append(method.getReturnType().isPrimitive() ? (method.getReturnType().getSimpleName()) :
                            (method.getReturnType() == String.class ? "string" : "object"))
                    .append(" ")
                    .append(method.getName())
                    .append("Value = ")
                    .append(method.getReturnType() == String.class ? "(jstring) " : "");
        }
        // call method
        stringBuilder
                .append(isStatic ? "env->CallStatic" : "env->Call")
                .append(method.getReturnType().isPrimitive() ? upperFirst(method.getReturnType().getSimpleName()) : "Object")
                .append("Method(")
                .append(lowerFirst(method.getDeclaringClass().getSimpleName()))
                .append(isStatic ? "Clazz," : "Obj,")
                .append(method.getName()).append("ID");

        if (parameters != null && parameters.length != 0) {
            stringBuilder.append(",");
            for (Parameter parameter : parameters) {
                stringBuilder.append(lowerFirst(parameter.getName())).append("Value").append(",");
            }
            // delete a comma
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        stringBuilder
                .append(");");

        // notice when exception occurred
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            stringBuilder.append("\n// ").append(exceptionTypes.length == 1 ? "this exception" : "those exceptions").append(" may occur:");
            for (Class<?> exceptionType : exceptionTypes) {
                stringBuilder.append("\n// ").append(exceptionType.getName());
            }
            stringBuilder.append("\n");
            stringBuilder.append("jthrowable error = env->ExceptionOccurred();\n")
                    .append("if (error != NULL) {\n")
                    .append("    // WARNING: YOU CAN NOT USE SOME JNI FUNCTIONS AFTER EXCEPTION OCCURRED AND 'env->ExceptionClear()' IS NOT CALLED\n")
                    .append("    // see \n")
                    .append("    // https://developer.androID.google.cn/training/articles/perf-jni#exceptions_1\n")
                    .append("    // or\n")
                    .append("    // https://developer.androID.com/training/articles/perf-jni#exceptions_1\n")
                    .append("    // for more information\n")
                    .append("    // \n")
                    .append("    // print exception:\n")
                    .append("    // env->ExceptionDescribe();\n")
                    .append("    // \n")
                    .append("    // clear exception:\n")
                    .append("    // env->ExceptionClear();\n")
                    .append("    // \n")
                    .append("    // TODO: write your code here to solve this exception\n")
                    .append("    // like \n    // jclass ").append(exceptionTypes[0].getSimpleName()).append("Clazz = env->FindClass(\"").append(exceptionTypes[0].getName().replace(".", "/")).append("\");\n")
                    .append("    // if(env->IsInstanceOf(error,").append(exceptionTypes[0].getSimpleName()).append("Clazz").append(")){\n")
                    .append("    //     //do when this exception occurred\n")
                    .append("    // }\n")
                    .append("}");
        }

        return stringBuilder.toString();
    }

}
