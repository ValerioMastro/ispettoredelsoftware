package com.tav.progetto.analysis.core;

import com.tav.progetto.analysis.loader.ClassLoadingService;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import com.tav.progetto.analysis.metrics.MetricCalculator;
import com.tav.progetto.analysis.rules.AnalysisProfile;
import com.tav.progetto.analysis.rules.RuleEngine;
import com.tav.progetto.analysis.scanner.ClassPathScanner;
import com.tav.progetto.analysis.scanner.ScannedClass;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectAnalyzer {
    private final ClassPathScanner scanner = new ClassPathScanner();
    private final MetricCalculator calculator = new MetricCalculator();
    private final ClassLoadingService loadingService = new ClassLoadingService();
    private final RuleEngine ruleEngine = new RuleEngine();

    public ProjectAnalysisResult analyze(TargetDescriptor target, AnalysisProfile profile) {
        List<ScannedClass> classes = scanner.findClasses(target);
        List<ClassAnalysisResult> results = new ArrayList<>();
        int penalty = 0;
        URLClassLoader loader;
        try {
            loader = loadingService.createClassLoader(target);
        } catch (RuntimeException e) {
            System.err.println("[ProjectAnalyzer] Failed to create classloader for target " + target.getPath() + ": " + e);
            for (ScannedClass scanned : classes) {
                ClassMetrics m = new ClassMetrics();
                m.className = scanned.getClassName();
                m.errorMessage = formatErrorMessage(e);
                penalty = addResultAndPenalty(results, penalty, m, profile);
            }
            ProjectAnalysisResult par = new ProjectAnalysisResult();
            par.classResults = results;
            par.healthScore = Math.max(0, 100 - penalty);
            return par;
        }

        try {
            for (ScannedClass scanned : classes) {
                ClassMetrics m;
                try {
                    Class<?> clazz = loadingService.loadClass(scanned.getClassName(), loader);
                    m = calculator.computeMetrics(clazz);
                } catch (Throwable e) {
                    m = new ClassMetrics();
                    m.className = scanned.getClassName();
                    m.errorMessage = formatErrorMessage(e);
                    System.err.println("[ProjectAnalyzer] Failed to load/inspect class " + scanned.getClassName() +
                            " from " + scanned.getOriginPath() + ": " + e);
                }
                penalty = addResultAndPenalty(results, penalty, m, profile);
            }
        } finally {
            try {
                loader.close();
            } catch (IOException e) {
                System.err.println("[ProjectAnalyzer] Failed to close classloader: " + e);
            }
        }

        ProjectAnalysisResult par = new ProjectAnalysisResult();
        par.classResults = results;
        par.healthScore = Math.max(0, 100 - penalty);
        return par;
    }

    private String formatErrorMessage(Throwable e) {
        String msg = e.getMessage();
        if (msg == null) msg = "";
        msg = msg.replace('\n', ' ').replace('\r', ' ').trim();
        if (msg.length() > 160) msg = msg.substring(0, 160);
        return e.getClass().getSimpleName() + (msg.isEmpty() ? "" : ": " + msg);
    }

    private int addResultAndPenalty(List<ClassAnalysisResult> results, int penalty, ClassMetrics m, AnalysisProfile profile) {
        ClassAnalysisResult car = new ClassAnalysisResult();
        car.metrics = m;
        car.violations = ruleEngine.analyzeClass(m, profile);
        car.overallSeverity = car.violations.stream()
                .map(v -> v.severity)
                .max(Comparator.naturalOrder())
                .orElse(Severity.LOW);
        results.add(car);

        int updatedPenalty = penalty;
        for (Violation v : car.violations) {
            if (v.severity == Severity.HIGH) updatedPenalty += 20;
            else if (v.severity == Severity.MEDIUM) updatedPenalty += 10;
            else updatedPenalty += 2;
        }
        return updatedPenalty;
    }
}
