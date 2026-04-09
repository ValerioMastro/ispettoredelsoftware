package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class SwitchManiaRule implements Rule {
    @Override
    public String getId() { return "SWITCH_MANIA"; }

    @Override
    public String getName() { return "Switch Mania"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;
        if (!metrics.switchMetricsAvailable) return res;

        // Bytecode-based heuristic: compiler desugaring can affect the final number of switch instructions.
        List<String> issues = new ArrayList<>();
        if (metrics.switchInstructionsCount > profile.switchManiaMaxSwitchesPerClass) {
            issues.add("switch instructions " + metrics.switchInstructionsCount
                    + " > " + profile.switchManiaMaxSwitchesPerClass);
        }
        if (metrics.maxSwitchCasesInMethod > profile.switchManiaMaxCasesPerMethod) {
            issues.add("max switch cases in one method " + metrics.maxSwitchCasesInMethod
                    + " > " + profile.switchManiaMaxCasesPerMethod);
        }
        if (issues.isEmpty()) return res;

        res.add(newViolation(
                metrics,
                Severity.MEDIUM,
                "Switch mania: " + String.join("; ", issues)
                        + "; methods with switch " + metrics.methodsWithSwitchCount
        ));
        return res;
    }
}
