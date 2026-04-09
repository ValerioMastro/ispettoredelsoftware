package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilityClassRuleTest {
    private final UtilityClassRule rule = new UtilityClassRule();

    @Test
    void shouldReportViolationWhenClassIsMostlyStaticAndHasNoInstanceState() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "UtilityHelper";
        metrics.totalMethods = 5;
        metrics.staticMethodsCount = 5;
        metrics.instanceMethodsCount = 0;
        metrics.staticFieldsCount = 1;
        metrics.instanceFieldsCount = 0;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("UTILITY_CLASS", violation.ruleId);
        assertEquals(Severity.LOW, violation.severity);
        assertEquals("UtilityHelper", violation.className);
    }

    @Test
    void shouldNotReportViolationWhenStaticMethodRatioIsTooLow() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.totalMethods = 5;
        metrics.staticMethodsCount = 3;
        metrics.instanceMethodsCount = 2;
        metrics.instanceFieldsCount = 0;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotReportViolationWhenMetricsContainInspectionError() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.totalMethods = 2;
        metrics.staticMethodsCount = 2;
        metrics.instanceFieldsCount = 0;
        metrics.errorMessage = "ClassFormatError";

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
