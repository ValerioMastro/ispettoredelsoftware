package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BadSingletonRuleTest {
    private final BadSingletonRule rule = new BadSingletonRule();

    @Test
    void shouldReportViolationWhenSingletonLikeClassHasNonPrivateConstructors() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "BadSingleton";
        metrics.singletonAccessorMethodsCount = 1;
        metrics.sameTypeStaticFieldsCount = 1;
        metrics.nonPrivateConstructorsCount = 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("BAD_SINGLETON", violation.ruleId);
        assertEquals(Severity.MEDIUM, violation.severity);
        assertEquals("BadSingleton", violation.className);
    }

    @Test
    void shouldReportViolationWhenSingletonLikeClassHasMutableSharedState() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.sameTypeStaticFieldsCount = 1;
        metrics.mutableInstanceFieldsCount = profile.badSingletonMaxMutableInstanceFields + 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
    }

    @Test
    void shouldNotReportViolationWhenSingletonLooksStructurallySound() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.sameTypeStaticFieldsCount = 1;
        metrics.singletonAccessorMethodsCount = 1;
        metrics.nonPrivateConstructorsCount = 0;
        metrics.mutableInstanceFieldsCount = 0;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
