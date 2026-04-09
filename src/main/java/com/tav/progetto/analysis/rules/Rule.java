package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.List;

public interface Rule {
    String getId();
    String getName();
    List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile);

    default boolean isEnabled(AnalysisProfile profile) {
        return profile.enabledRules.contains(getId());
    }

    default Violation newViolation(ClassMetrics metrics, Severity severity, String description) {
        Violation violation = new Violation();
        violation.className = metrics.className;
        violation.ruleId = getId();
        violation.description = description;
        violation.severity = severity;
        return violation;
    }
}
