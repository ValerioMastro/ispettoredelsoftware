package com.tav.progetto.analysis.metrics;

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
    public int maxParametersCount;
    public int interfacesCount;
    public int inheritanceDepth;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
