package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstantInterfaceRuleTest {
    private final ConstantInterfaceRule rule = new ConstantInterfaceRule();

    @Test
    void shouldReportViolationWhenInterfaceContainsOnlyConstants() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "ConstantInterface";
        metrics.isInterface = true;
        metrics.totalFieldsCount = 2;
        metrics.publicStaticFinalFieldsCount = 2;
        metrics.methodsCount = 0;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("CONSTANT_INTERFACE", violation.ruleId);
        assertEquals(Severity.MEDIUM, violation.severity);
        assertEquals("ConstantInterface", violation.className);
    }

    @Test
    void shouldReportViolationWhenClassMostlyExposesConstants() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "ConstantsHolder";
        metrics.totalFieldsCount = 3;
        metrics.publicStaticFinalFieldsCount = 2;
        metrics.methodsCount = 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        assertEquals("CONSTANT_INTERFACE", violations.get(0).ruleId);
    }

    @Test
    void shouldNotReportViolationWhenInterfaceHasBehavior() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.isInterface = true;
        metrics.totalFieldsCount = 2;
        metrics.publicStaticFinalFieldsCount = 2;
        metrics.methodsCount = profile.constantInterfaceMaxMethods + 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
