package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LazyClassRuleTest {
    private final LazyClassRule rule = new LazyClassRule();

    @Test
    void shouldReportViolationWhenClassIsBelowThresholds() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "LazyClass";
        metrics.totalMethods = profile.lazyClassMaxMethods;
        metrics.fields = profile.lazyClassMaxFields;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("LAZY_CLASS", violation.ruleId);
        assertEquals(Severity.LOW, violation.severity);
        assertEquals("LazyClass", violation.className);
    }

    @Test
    void shouldNotReportViolationWhenMethodThresholdIsExceeded() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.totalMethods = profile.lazyClassMaxMethods + 1;
        metrics.fields = profile.lazyClassMaxFields;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotReportViolationWhenMetricsContainInspectionError() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.totalMethods = 0;
        metrics.fields = 0;
        metrics.errorMessage = "ClassFormatError";

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
