package com.tav.progetto.analysis.core;

import com.tav.progetto.analysis.metrics.ClassMetrics;
import com.tav.progetto.analysis.metrics.MetricCalculator;
import com.tav.progetto.analysis.rules.AnalysisProfile;
import com.tav.progetto.analysis.rules.RuleEngine;
import com.tav.progetto.analysis.scanner.ClassPathScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectAnalyzer {
    private final ClassPathScanner scanner = new ClassPathScanner();
    private final MetricCalculator calculator = new MetricCalculator();
    private final RuleEngine ruleEngine = new RuleEngine();

    public ProjectAnalysisResult analyze(TargetDescriptor target, AnalysisProfile profile) {
        List<File> files = scanner.findClassFiles(target);
        List<ClassAnalysisResult> results = new ArrayList<>();
        int penalty = 0;
        for (File f : files) {
            ClassMetrics m = calculator.computeMetrics(f);
            ClassAnalysisResult car = new ClassAnalysisResult();
            car.metrics = m;
            car.violations = ruleEngine.analyzeClass(m, profile);
            car.overallSeverity = car.violations.stream()
                    .map(v -> v.severity)
                    .max(Comparator.naturalOrder())
                    .orElse(Severity.LOW);
            results.add(car);
            for (Violation v : car.violations) {
                if (v.severity == Severity.HIGH) penalty += 20;
                else if (v.severity == Severity.MEDIUM) penalty += 10;
                else penalty += 2;
            }
        }
        ProjectAnalysisResult par = new ProjectAnalysisResult();
        par.classResults = results;
        par.healthScore = Math.max(0, 100 - penalty);
        return par;
    }
}
