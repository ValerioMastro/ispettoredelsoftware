package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwitchManiaRuleTest {
    private final SwitchManiaRule rule = new SwitchManiaRule();

    @Test
    void shouldReportViolationWhenSwitchCountsExceedThresholds() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "SwitchHeavy";
        metrics.switchMetricsAvailable = true;
        metrics.switchInstructionsCount = profile.switchManiaMaxSwitchesPerClass + 1;
        metrics.methodsWithSwitchCount = 2;
        metrics.maxSwitchCasesInMethod = profile.switchManiaMaxCasesPerMethod + 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("SWITCH_MANIA", violation.ruleId);
        assertEquals(Severity.MEDIUM, violation.severity);
        assertEquals("SwitchHeavy", violation.className);
    }

    @Test
    void shouldNotReportViolationWhenSwitchMetricsAreUnavailable() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.switchInstructionsCount = 10;
        metrics.maxSwitchCasesInMethod = 10;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
