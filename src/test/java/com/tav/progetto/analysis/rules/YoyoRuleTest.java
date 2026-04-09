package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YoyoRuleTest {
    private final YoyoRule rule = new YoyoRule();

    @Test
    void shouldReportViolationWhenInheritanceDepthExceedsThreshold() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "DeepHierarchy";
        metrics.inheritanceDepth = profile.yoyoMaxInheritanceDepth + 1;
        metrics.superClassName = "com.example.BaseLevel";

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("YOYO", violation.ruleId);
        assertEquals(Severity.MEDIUM, violation.severity);
        assertEquals("DeepHierarchy", violation.className);
    }

    @Test
    void shouldNotReportViolationAtExactThreshold() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.inheritanceDepth = profile.yoyoMaxInheritanceDepth;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
