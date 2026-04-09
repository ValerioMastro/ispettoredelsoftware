package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrokenUtilityClassRuleTest {
    private final BrokenUtilityClassRule rule = new BrokenUtilityClassRule();

    @Test
    void shouldReportViolationWhenUtilityClassHasAccessibleConstructor() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "UtilityClass";
        metrics.totalMethods = 2;
        metrics.fields = 0;
        metrics.instanceMethodsCount = 0;
        metrics.instanceFieldsCount = 0;
        metrics.nonPrivateConstructorsCount = 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("BROKEN_UTILITY_CLASS", violation.ruleId);
        assertEquals(Severity.LOW, violation.severity);
        assertEquals("UtilityClass", violation.className);
    }

    @Test
    void shouldNotReportViolationWhenUtilityClassCanNotBeInstantiated() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.totalMethods = 2;
        metrics.instanceMethodsCount = 0;
        metrics.instanceFieldsCount = 0;
        metrics.nonPrivateConstructorsCount = 0;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
