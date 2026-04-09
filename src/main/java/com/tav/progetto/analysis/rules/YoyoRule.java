package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class YoyoRule implements Rule {
    @Override
    public String getId() { return "YOYO"; }

    @Override
    public String getName() { return "Yo-yo Inheritance"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;

        int inheritanceDepth = metrics.getEffectiveInheritanceDepth();
        if (inheritanceDepth > profile.yoyoMaxInheritanceDepth) {
            res.add(newViolation(
                    metrics,
                    Severity.MEDIUM,
                    "Yo-yo inheritance: depth " + inheritanceDepth
                            + " > " + profile.yoyoMaxInheritanceDepth
                            + (metrics.superClassName == null ? "" : " (direct superclass: " + metrics.superClassName + ")")
            ));
        }
        return res;
    }
}
