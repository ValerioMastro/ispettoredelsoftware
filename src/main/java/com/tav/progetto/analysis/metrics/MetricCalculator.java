package com.tav.progetto.analysis.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
