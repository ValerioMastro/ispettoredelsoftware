package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.metrics.ClassMetrics;
import com.tav.progetto.analysis.core.Violation;

import java.util.List;

public interface Rule {
    String getId();
    String getName();
    List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile);
}
