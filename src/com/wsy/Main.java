package com.wsy;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class Main {


    public static void main(String[] args) {
        System.out.println("1. classToSignature");

        System.out.println(JNIUtil.class2Signature(int.class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(Integer.class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(String.class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(String[].class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(Object[].class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(List[].class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(List.class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.class2Signature(TestClazz.class));

        System.out.println("-------------------------------------------------------");


        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("2. generateMethod2SignatureCode");
        Method[] declaredMethods = File.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.println(JNIUtil.generateMethod2SignatureCode(method));

            System.out.println("-------------------------------------------------------");

        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("3. generateField2SignatureCode");
        Field[] declaredFields = File.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(JNIUtil.generateField2SignatureCode(declaredField));

            System.out.println("-------------------------------------------------------");

        }
        declaredFields = TestClazz.class.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            System.out.println(JNIUtil.generateField2SignatureCode(declaredField));

            System.out.println("-------------------------------------------------------");

        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("4. generateFindClassCode");
        System.out.println(JNIUtil.generateFindClassCode(int.class));
        System.out.println(JNIUtil.generateFindClassCode(File.class));

        System.out.println("-------------------------------------------------------");

        System.out.println(JNIUtil.generateFindClassCode(TestClazz.class));

        System.out.println("-------------------------------------------------------");

    }

    private static class TestClazz {
        int intField;
        static int staticIntField;
        static Object staticObjectField;
        static File staticFileField;
    }
}