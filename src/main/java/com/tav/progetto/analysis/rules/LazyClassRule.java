package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class LazyClassRule implements Rule {
    @Override
    public String getId() { return "LAZY_CLASS"; }

    @Override
    public String getName() { return "Lazy Class"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;

        int methods = metrics.getEffectiveMethodsCount();
        int fields = metrics.getEffectiveFieldsCount();
        if (!metrics.isInterface
                && methods <= profile.lazyClassMaxMethods
                && fields <= profile.lazyClassMaxFields) {
            res.add(newViolation(
                    metrics,
                    Severity.LOW,
                    "Lazy class: methods " + methods + " <= " + profile.lazyClassMaxMethods
                            + " and fields " + fields + " <= " + profile.lazyClassMaxFields
            ));
        }
        return res;
    }
}
