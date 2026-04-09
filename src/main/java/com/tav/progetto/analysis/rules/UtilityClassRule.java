package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UtilityClassRule implements Rule {
    @Override
    public String getId() { return "UTILITY_CLASS"; }

    @Override
    public String getName() { return "Utility Class"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;
        if (metrics.isInterface) return res;
        if (metrics.getEffectiveMethodsCount() == 0 || metrics.staticMethodsCount == 0) return res;

        int methods = metrics.getEffectiveMethodsCount();
        double staticMethodRatio = (double) metrics.staticMethodsCount / methods;
        if (staticMethodRatio >= profile.utilityMinStaticMethodRatio
                && metrics.instanceFieldsCount <= profile.utilityMaxInstanceFields) {
            res.add(newViolation(
                    metrics,
                    Severity.LOW,
                    "Utility class: static method ratio "
                            + formatRatio(staticMethodRatio) + " >= "
                            + formatRatio(profile.utilityMinStaticMethodRatio)
                            + ", instance fields " + metrics.instanceFieldsCount
                            + " <= " + profile.utilityMaxInstanceFields
            ));
        }
        return res;
    }

    private String formatRatio(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
