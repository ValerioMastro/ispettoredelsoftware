package com.tav.progetto.analysis.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MetricCalculator {
    public ClassMetrics computeMetrics(File javaFile) {
        ClassMetrics m = new ClassMetrics();
        m.className = javaFile.getName().replaceAll("\\.java$", "");
        try (BufferedReader r = new BufferedReader(new FileReader(javaFile))) {
            String line;
            int methods = 0;
            int publicMethods = 0;
            int fields = 0;
            int maxParams = 0;
            int imports = 0;
            boolean isInterface = false;
            boolean onlyConstants = true;
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.startsWith("import ")) imports++;
                if (t.contains(" interface ") || t.startsWith("interface ")) isInterface = true;
                if (t.matches(".*(public|protected|private).*\\(.*\\).*\\{?")) {
                    methods++;
                    if (t.contains("public ")) publicMethods++;
                    int p = countParams(t);
                    if (p > maxParams) maxParams = p;
                } else if (t.endsWith(";") && !t.contains("(")) {
                    fields++;
                    if (!t.contains("static") && !t.contains("final")) onlyConstants = false;
                }
            }
            m.totalMethods = methods;
            m.publicMethods = publicMethods;
            m.fields = fields;
            m.maxParametersPerMethod = maxParams;
            m.outgoingDependencies = imports;
            m.isInterface = isInterface;
            m.hasOnlyConstants = isInterface && onlyConstants;
            m.depthOfInheritance = 0; // placeholder for demo
        } catch (IOException e) {
            // keep defaults
        }
        return m;
    }

    public ClassMetrics computeMetrics(Class<?> clazz) {
        ClassMetrics m = new ClassMetrics();
        m.className = clazz.getName();

        Method[] declaredMethods = clazz.getDeclaredMethods();
        Field[] declaredFields = clazz.getDeclaredFields();

        int publicMethods = 0;
        int maxParams = 0;
        for (Method method : declaredMethods) {
            if (Modifier.isPublic(method.getModifiers())) publicMethods++;
            int count = method.getParameterCount();
            if (count > maxParams) maxParams = count;
        }

        boolean isInterface = clazz.isInterface();
        boolean hasOnlyConstants = false;
        if (isInterface) {
            boolean onlyConstants = true;
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                if (!(Modifier.isStatic(mod) && Modifier.isFinal(mod))) {
                    onlyConstants = false;
                    break;
                }
            }
            hasOnlyConstants = onlyConstants;
        }

        int inheritanceDepth = 0;
        // Chosen convention: exclude java.lang.Object (a class directly extending Object has depth 0).
        Class<?> current = clazz.getSuperclass();
        while (current != null && current != Object.class) {
            inheritanceDepth++;
            current = current.getSuperclass();
        }

        m.declaredMethodsCount = declaredMethods.length;
        m.declaredFieldsCount = declaredFields.length;
        m.maxParametersCount = maxParams;
        m.interfacesCount = clazz.getInterfaces().length;
        m.inheritanceDepth = inheritanceDepth;

        // Map reflection metrics to legacy fields used by the existing GUI and rules.
        m.totalMethods = m.declaredMethodsCount;
        m.publicMethods = publicMethods;
        m.fields = m.declaredFieldsCount;
        m.maxParametersPerMethod = m.maxParametersCount;
        m.isInterface = isInterface;
        m.hasOnlyConstants = hasOnlyConstants;
        m.depthOfInheritance = m.inheritanceDepth;
        m.outgoingDependencies = 0;

        return m;
    }

    private int countParams(String line) {
        int p = 0;
        int i1 = line.indexOf('(');
        int i2 = line.indexOf(')');
        if (i1 >= 0 && i2 > i1) {
            String inside = line.substring(i1 + 1, i2).trim();
            if (inside.isEmpty()) return 0;
            // count commas
            p = inside.split(",").length;
        }
        return p;
    }
}
