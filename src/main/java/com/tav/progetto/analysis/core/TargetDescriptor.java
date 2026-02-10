package com.tav.progetto.analysis.core;

public class TargetDescriptor {
    private final String path;
    private final TargetType type;

    public TargetDescriptor(String path, TargetType type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() { return path; }
    public TargetType getType() { return type; }
}
