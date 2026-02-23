package com.tav.progetto.analysis.scanner;

import com.tav.progetto.analysis.core.TargetType;

public class ScannedClass {
    private final String className;
    private final String originPath;
    private final TargetType targetType;

    public ScannedClass(String className, String originPath, TargetType targetType) {
        this.className = className;
        this.originPath = originPath;
        this.targetType = targetType;
    }

    public String getClassName() {
        return className;
    }

    public String getOriginPath() {
        return originPath;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return "ScannedClass{" +
                "className='" + className + '\'' +
                ", originPath='" + originPath + '\'' +
                ", targetType=" + targetType +
                '}';
    }
}

