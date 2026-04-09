package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class BadSingletonRule implements Rule {
    @Override
    public String getId() { return "BAD_SINGLETON"; }

    @Override
    public String getName() { return "Bad Singleton"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;
        if (metrics.isInterface) return res;

        // Structural heuristic only: we flag classes that look singleton-like and show clear design issues.
        boolean hasSingletonField = metrics.sameTypeStaticFieldsCount > 0;
        boolean hasSingletonAccessor = metrics.singletonAccessorMethodsCount > 0;
        boolean singletonLike = hasSingletonField || hasSingletonAccessor;
        if (!singletonLike) return res;

        List<String> issues = new ArrayList<>();
        if (metrics.nonPrivateConstructorsCount > 0) {
            issues.add("non-private constructors " + metrics.nonPrivateConstructorsCount + " > 0");
        }
        if (metrics.mutableInstanceFieldsCount > profile.badSingletonMaxMutableInstanceFields) {
            issues.add("mutable instance fields " + metrics.mutableInstanceFieldsCount
                    + " > " + profile.badSingletonMaxMutableInstanceFields);
        }
        if (issues.isEmpty()) return res;

        res.add(newViolation(
                metrics,
                Severity.MEDIUM,
                "Bad singleton (heuristic): "
                        + "same-type static fields=" + metrics.sameTypeStaticFieldsCount
                        + ", accessor methods=" + metrics.singletonAccessorMethodsCount
                        + "; " + String.join("; ", issues)
        ));
        return res;
    }
}
