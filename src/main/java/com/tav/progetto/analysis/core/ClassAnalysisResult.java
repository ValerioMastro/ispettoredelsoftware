package com.tav.progetto.analysis.core;

import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.List;

public class ClassAnalysisResult {
    public ClassMetrics metrics;
    public List<Violation> violations;
    public Severity overallSeverity;
}
