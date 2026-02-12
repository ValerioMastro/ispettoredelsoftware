package com.tav.progetto.analysis.core;

import com.tav.progetto.analysis.rules.AnalysisProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectAnalyzerIntegrationTest {
    @Test
    void shouldDetectExpectedViolationsInSampleProject() {
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        AnalysisProfile profile = new AnalysisProfile();

        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor("sample-project", TargetType.DIRECTORY),
                profile
        );

        assertEquals(4, result.classResults.size());
        assertTrue(result.healthScore >= 0 && result.healthScore <= 100);

        Map<String, ClassAnalysisResult> byClass = result.classResults.stream()
                .collect(Collectors.toMap(cr -> cr.metrics.className, Function.identity()));

        assertTrue(hasViolation(byClass.get("GodClass"), "GOD_CLASS"));
        assertTrue(hasViolation(byClass.get("LongParamClass"), "LONG_PARAM_LIST"));
        assertTrue(byClass.get("UtilityClass").violations.isEmpty());
        assertTrue(byClass.get("ConstantInterface").violations.isEmpty());
    }

    private boolean hasViolation(ClassAnalysisResult result, String ruleId) {
        return result.violations.stream().anyMatch(v -> ruleId.equals(v.ruleId));
    }
}
