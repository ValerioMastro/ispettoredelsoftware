package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongParameterListRuleTest {
    private final LongParameterListRule rule = new LongParameterListRule();

    @Test
    void shouldReportViolationWhenParameterThresholdIsExceeded() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "LongParamClass";
        metrics.maxParametersPerMethod = profile.longParamListMaxParams + 1;

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("LONG_PARAM_LIST", violation.ruleId);
        assertEquals(Severity.MEDIUM, violation.severity);
        assertEquals("LongParamClass", violation.className);
    }

    @Test
    void shouldNotReportViolationAtExactThreshold() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.maxParametersPerMethod = profile.longParamListMaxParams;

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
