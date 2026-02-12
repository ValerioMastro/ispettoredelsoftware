package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GodClassRuleTest {
    private final GodClassRule rule = new GodClassRule();

    @Test
    void shouldReportViolationWhenMethodThresholdIsExceeded() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "GodClass";
        metrics.totalMethods = 25;
        metrics.fields = 5;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("GOD_CLASS", violation.ruleId);
        assertEquals(Severity.HIGH, violation.severity);
        assertEquals("GodClass", violation.className);
    }

    @Test
    void shouldNotReportViolationAtExactThreshold() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.totalMethods = profile.godClassMaxMethods;
        metrics.fields = profile.godClassMaxFields;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
