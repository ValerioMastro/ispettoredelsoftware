package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class BrokenUtilityClassRule implements Rule {
    @Override
    public String getId() { return "BROKEN_UTILITY_CLASS"; }

    @Override
    public String getName() { return "Broken Utility Class"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;

        boolean hasOnlyStaticMembers = metrics.instanceMethodsCount == 0 && metrics.instanceFieldsCount == 0;
        boolean hasMembers = metrics.getEffectiveMethodsCount() > 0 || metrics.getEffectiveFieldsCount() > 0;
        if (!metrics.isInterface && hasMembers && hasOnlyStaticMembers && metrics.nonPrivateConstructorsCount > 0) {
            res.add(newViolation(
                    metrics,
                    Severity.LOW,
                    "Broken utility class: non-private constructors "
                            + metrics.nonPrivateConstructorsCount + " > 0"
            ));
        }
        return res;
    }
}
