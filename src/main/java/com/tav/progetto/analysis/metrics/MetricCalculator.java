package com.tav.progetto.analysis.metrics;

import com.tav.progetto.analysis.classfile.ClassFileInspector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MetricCalculator {
    public ClassMetrics computeMetrics(File javaFile) {
        ClassMetrics m = new ClassMetrics();
        m.className = javaFile.getName().replaceAll("\\.java$", "");
        try (BufferedReader r = new BufferedReader(new FileReader(javaFile))) {
            String line;
            int methods = 0;
            int publicMethods = 0;
            int staticMethods = 0;
            int instanceMethods = 0;
            int fields = 0;
            int staticFields = 0;
            int instanceFields = 0;
            int publicStaticFinalFields = 0;
            int sameTypeStaticFields = 0;
            int singletonAccessorMethods = 0;
            int mutableInstanceFields = 0;
            int maxParams = 0;
            int imports = 0;
            boolean isInterface = false;
            boolean onlyConstants = true;
            Set<String> referencedTypeNames = new LinkedHashSet<>();
            List<String> interfaceNames = new ArrayList<>();
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.startsWith("import ")) {
                    imports++;
                    if (!t.startsWith("import static ")) {
                        String importedType = t.substring("import ".length()).replace(";", "").trim();
                        if (!importedType.isEmpty()) referencedTypeNames.add(importedType);
                    }
                }
                if (t.contains(" interface ") || t.startsWith("interface ")) isInterface = true;
                if (t.matches(".*(public|protected|private).*\\(.*\\).*\\{?")) {
                    methods++;
                    if (t.contains("public ")) publicMethods++;
                    if (t.contains(" static ")) staticMethods++;
                    else instanceMethods++;
                    if (isSingletonAccessorSignature(t, m.className)) singletonAccessorMethods++;
                    int p = countParams(t);
                    if (p > maxParams) maxParams = p;
                } else if (t.endsWith(";") && !t.contains("(")) {
                    fields++;
                    if (t.contains("public ") && t.contains("static") && t.contains("final")) {
                        publicStaticFinalFields++;
                    }
                    if (t.contains("static") && t.matches(".*\\b" + m.className + "\\b.*;")) {
                        sameTypeStaticFields++;
                    }
                    if (t.contains(" static ")) staticFields++;
                    else {
                        instanceFields++;
                        if (!t.contains(" final ")) mutableInstanceFields++;
                    }
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
            m.inheritanceDepth = 0;
            m.superClassName = null;
            m.methodsCount = methods;
            m.totalFieldsCount = fields;
            m.publicStaticFinalFieldsCount = publicStaticFinalFields;
            m.staticMethodsCount = staticMethods;
            m.instanceMethodsCount = instanceMethods;
            m.staticFieldsCount = staticFields;
            m.instanceFieldsCount = instanceFields;
            m.sameTypeStaticFieldsCount = sameTypeStaticFields;
            m.singletonAccessorMethodsCount = singletonAccessorMethods;
            m.mutableInstanceFieldsCount = mutableInstanceFields;
            m.interfaceNames = interfaceNames;
            m.referencedTypeNames = new ArrayList<>(referencedTypeNames);
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
        Set<String> referencedTypeNames = new LinkedHashSet<>();

        int publicMethods = 0;
        int maxParams = 0;
        int staticMethods = 0;
        int instanceMethods = 0;
        int singletonAccessorMethods = 0;
        for (Method method : declaredMethods) {
            if (Modifier.isPublic(method.getModifiers())) publicMethods++;
            if (Modifier.isStatic(method.getModifiers())) staticMethods++;
            else instanceMethods++;
            if (isSingletonAccessorMethod(method, clazz)) singletonAccessorMethods++;
            for (Class<?> parameterType : method.getParameterTypes()) {
                addTypeName(referencedTypeNames, parameterType);
            }
            int count = method.getParameterCount();
            if (count > maxParams) maxParams = count;
        }

        int staticFields = 0;
        int instanceFields = 0;
        int publicStaticFinalFields = 0;
        int sameTypeStaticFields = 0;
        int mutableInstanceFields = 0;
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            addTypeName(referencedTypeNames, field.getType());
            if (Modifier.isStatic(modifiers)) staticFields++;
            else {
                instanceFields++;
                if (!Modifier.isFinal(modifiers)) mutableInstanceFields++;
            }
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                publicStaticFinalFields++;
            }
            if (Modifier.isStatic(modifiers) && field.getType() == clazz) {
                sameTypeStaticFields++;
            }
        }

        int nonPrivateConstructors = 0;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!Modifier.isPrivate(constructor.getModifiers())) nonPrivateConstructors++;
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
        String superClassName = normalizeTypeName(current);
        addTypeName(referencedTypeNames, current);
        while (current != null && current != Object.class) {
            inheritanceDepth++;
            current = current.getSuperclass();
        }

        Class<?>[] implementedInterfaces = clazz.getInterfaces();
        List<String> interfaceNames = new ArrayList<>();
        for (Class<?> implementedInterface : implementedInterfaces) {
            String interfaceName = normalizeTypeName(implementedInterface);
            if (interfaceName != null) interfaceNames.add(interfaceName);
            addTypeName(referencedTypeNames, implementedInterface);
        }

        m.declaredMethodsCount = declaredMethods.length;
        m.declaredFieldsCount = declaredFields.length;
        m.methodsCount = declaredMethods.length;
        m.totalFieldsCount = declaredFields.length;
        m.publicStaticFinalFieldsCount = publicStaticFinalFields;
        m.maxParametersCount = maxParams;
        m.interfacesCount = implementedInterfaces.length;
        m.inheritanceDepth = inheritanceDepth;
        m.superClassName = superClassName;
        m.staticMethodsCount = staticMethods;
        m.instanceMethodsCount = instanceMethods;
        m.staticFieldsCount = staticFields;
        m.instanceFieldsCount = instanceFields;
        m.sameTypeStaticFieldsCount = sameTypeStaticFields;
        m.singletonAccessorMethodsCount = singletonAccessorMethods;
        m.mutableInstanceFieldsCount = mutableInstanceFields;
        m.nonPrivateConstructorsCount = nonPrivateConstructors;
        m.interfaceNames = interfaceNames;
        m.referencedTypeNames = new ArrayList<>(referencedTypeNames);

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

    // Heuristic only: static no-arg methods returning the same type and named like singleton accessors.
    private boolean isSingletonAccessorMethod(Method method, Class<?> ownerType) {
        if (!Modifier.isStatic(method.getModifiers())) return false;
        if (method.getParameterCount() != 0) return false;
        if (method.getReturnType() != ownerType) return false;
        String name = method.getName().toLowerCase(Locale.ROOT);
        return name.equals("getinstance")
                || name.equals("instance")
                || name.endsWith("instance")
                || name.contains("singleton");
    }

    private boolean isSingletonAccessorSignature(String line, String className) {
        String lower = line.toLowerCase(Locale.ROOT);
        if (!lower.contains("static")) return false;
        if (!line.contains(className + " ")) return false;
        return lower.contains("getinstance(")
                || lower.contains(" instance(")
                || lower.endsWith("instance(){")
                || lower.contains("singleton(");
    }

    private void addTypeName(Set<String> referencedTypeNames, Class<?> type) {
        String typeName = normalizeTypeName(type);
        if (typeName != null) referencedTypeNames.add(typeName);
    }

    private String normalizeTypeName(Class<?> type) {
        if (type == null) return null;
        while (type.isArray()) {
            type = type.getComponentType();
        }
        if (type == null || type.isPrimitive()) return null;
        return type.getName();
    }

    public void applySwitchUsage(ClassMetrics metrics, ClassFileInspector.SwitchUsageSummary switchUsage) {
        if (metrics == null || switchUsage == null || !switchUsage.isAvailable()) return;
        metrics.switchMetricsAvailable = true;
        metrics.switchInstructionsCount = switchUsage.getSwitchInstructionsCount();
        metrics.methodsWithSwitchCount = switchUsage.getMethodsWithSwitchCount();
        metrics.totalSwitchCasesCount = switchUsage.getTotalSwitchCasesCount();
        metrics.maxSwitchCasesInMethod = switchUsage.getMaxSwitchCasesInMethod();
    }
}
