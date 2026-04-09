package com.tav.progetto.analysis.metrics;

import java.util.ArrayList;
import java.util.List;

public class ClassMetrics {
    public String className;
    public int totalMethods;
    public int publicMethods;
    public int fields;
    public int depthOfInheritance;
    public int maxParametersPerMethod;
    public int outgoingDependencies;
    public boolean isInterface;
    public boolean hasOnlyConstants;

    // Reflection-based metrics (PW4). Kept alongside legacy fields for GUI compatibility.
    public int declaredMethodsCount;
    public int declaredFieldsCount;
    public int methodsCount;
    public int totalFieldsCount;
    public int publicStaticFinalFieldsCount;
    public int maxParametersCount;
    public int interfacesCount;
    public int inheritanceDepth;
    public String superClassName;
    public int staticMethodsCount;
    public int instanceMethodsCount;
    public int staticFieldsCount;
    public int instanceFieldsCount;
    public int sameTypeStaticFieldsCount;
    public int singletonAccessorMethodsCount;
    public int mutableInstanceFieldsCount;
    public int nonPrivateConstructorsCount;
    public List<String> interfaceNames = new ArrayList<>();
    public List<String> referencedTypeNames = new ArrayList<>();
    public boolean switchMetricsAvailable;
    public int switchInstructionsCount;
    public int methodsWithSwitchCount;
    public int totalSwitchCasesCount;
    public int maxSwitchCasesInMethod;
    public String errorMessage;

    public int getDeclaredMethodsCount() {
        return declaredMethodsCount;
    }

    public void setDeclaredMethodsCount(int declaredMethodsCount) {
        this.declaredMethodsCount = declaredMethodsCount;
    }

    public int getDeclaredFieldsCount() {
        return declaredFieldsCount;
    }

    public void setDeclaredFieldsCount(int declaredFieldsCount) {
        this.declaredFieldsCount = declaredFieldsCount;
    }

    public int getMethodsCount() {
        return methodsCount;
    }

    public void setMethodsCount(int methodsCount) {
        this.methodsCount = methodsCount;
    }

    public int getTotalFieldsCount() {
        return totalFieldsCount;
    }

    public void setTotalFieldsCount(int totalFieldsCount) {
        this.totalFieldsCount = totalFieldsCount;
    }

    public int getPublicStaticFinalFieldsCount() {
        return publicStaticFinalFieldsCount;
    }

    public void setPublicStaticFinalFieldsCount(int publicStaticFinalFieldsCount) {
        this.publicStaticFinalFieldsCount = publicStaticFinalFieldsCount;
    }

    public int getMaxParametersCount() {
        return maxParametersCount;
    }

    public void setMaxParametersCount(int maxParametersCount) {
        this.maxParametersCount = maxParametersCount;
    }

    public int getInterfacesCount() {
        return interfacesCount;
    }

    public void setInterfacesCount(int interfacesCount) {
        this.interfacesCount = interfacesCount;
    }

    public int getInheritanceDepth() {
        return inheritanceDepth;
    }

    public void setInheritanceDepth(int inheritanceDepth) {
        this.inheritanceDepth = inheritanceDepth;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public int getStaticMethodsCount() {
        return staticMethodsCount;
    }

    public void setStaticMethodsCount(int staticMethodsCount) {
        this.staticMethodsCount = staticMethodsCount;
    }

    public int getInstanceMethodsCount() {
        return instanceMethodsCount;
    }

    public void setInstanceMethodsCount(int instanceMethodsCount) {
        this.instanceMethodsCount = instanceMethodsCount;
    }

    public int getStaticFieldsCount() {
        return staticFieldsCount;
    }

    public void setStaticFieldsCount(int staticFieldsCount) {
        this.staticFieldsCount = staticFieldsCount;
    }

    public int getInstanceFieldsCount() {
        return instanceFieldsCount;
    }

    public void setInstanceFieldsCount(int instanceFieldsCount) {
        this.instanceFieldsCount = instanceFieldsCount;
    }

    public int getSameTypeStaticFieldsCount() {
        return sameTypeStaticFieldsCount;
    }

    public void setSameTypeStaticFieldsCount(int sameTypeStaticFieldsCount) {
        this.sameTypeStaticFieldsCount = sameTypeStaticFieldsCount;
    }

    public int getSingletonAccessorMethodsCount() {
        return singletonAccessorMethodsCount;
    }

    public void setSingletonAccessorMethodsCount(int singletonAccessorMethodsCount) {
        this.singletonAccessorMethodsCount = singletonAccessorMethodsCount;
    }

    public int getMutableInstanceFieldsCount() {
        return mutableInstanceFieldsCount;
    }

    public void setMutableInstanceFieldsCount(int mutableInstanceFieldsCount) {
        this.mutableInstanceFieldsCount = mutableInstanceFieldsCount;
    }

    public int getNonPrivateConstructorsCount() {
        return nonPrivateConstructorsCount;
    }

    public void setNonPrivateConstructorsCount(int nonPrivateConstructorsCount) {
        this.nonPrivateConstructorsCount = nonPrivateConstructorsCount;
    }

    public List<String> getInterfaceNames() {
        return interfaceNames;
    }

    public void setInterfaceNames(List<String> interfaceNames) {
        this.interfaceNames = interfaceNames == null ? new ArrayList<>() : new ArrayList<>(interfaceNames);
    }

    public List<String> getReferencedTypeNames() {
        return referencedTypeNames;
    }

    public void setReferencedTypeNames(List<String> referencedTypeNames) {
        this.referencedTypeNames = referencedTypeNames == null ? new ArrayList<>() : new ArrayList<>(referencedTypeNames);
    }

    public boolean isSwitchMetricsAvailable() {
        return switchMetricsAvailable;
    }

    public void setSwitchMetricsAvailable(boolean switchMetricsAvailable) {
        this.switchMetricsAvailable = switchMetricsAvailable;
    }

    public int getSwitchInstructionsCount() {
        return switchInstructionsCount;
    }

    public void setSwitchInstructionsCount(int switchInstructionsCount) {
        this.switchInstructionsCount = switchInstructionsCount;
    }

    public int getMethodsWithSwitchCount() {
        return methodsWithSwitchCount;
    }

    public void setMethodsWithSwitchCount(int methodsWithSwitchCount) {
        this.methodsWithSwitchCount = methodsWithSwitchCount;
    }

    public int getTotalSwitchCasesCount() {
        return totalSwitchCasesCount;
    }

    public void setTotalSwitchCasesCount(int totalSwitchCasesCount) {
        this.totalSwitchCasesCount = totalSwitchCasesCount;
    }

    public int getMaxSwitchCasesInMethod() {
        return maxSwitchCasesInMethod;
    }

    public void setMaxSwitchCasesInMethod(int maxSwitchCasesInMethod) {
        this.maxSwitchCasesInMethod = maxSwitchCasesInMethod;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean hasInspectionError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public int getEffectiveMethodsCount() {
        return Math.max(methodsCount, totalMethods);
    }

    public int getEffectiveFieldsCount() {
        return Math.max(totalFieldsCount, fields);
    }

    public int getEffectiveInheritanceDepth() {
        return Math.max(inheritanceDepth, depthOfInheritance);
    }
}
