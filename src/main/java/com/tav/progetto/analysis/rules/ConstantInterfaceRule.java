package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class ConstantInterfaceRule implements Rule {
    @Override
    public String getId() { return "CONSTANT_INTERFACE"; }

    @Override
    public String getName() { return "Constant Interface / Holder"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;

        boolean hasEnoughConstantFields =
                metrics.publicStaticFinalFieldsCount >= profile.constantInterfaceMinConstantFields;
        int methods = metrics.getEffectiveMethodsCount();
        int fields = metrics.getEffectiveFieldsCount();
        boolean hasFewMethods = methods <= profile.constantInterfaceMaxMethods;
        boolean mostlyConstants = fields > 0
                && metrics.publicStaticFinalFieldsCount * 2 >= fields;

        if (hasEnoughConstantFields && hasFewMethods && mostlyConstants) {
            res.add(newViolation(
                    metrics,
                    Severity.MEDIUM,
                    (metrics.isInterface ? "Constant interface" : "Constant holder class")
                            + ": public static final fields "
                            + metrics.publicStaticFinalFieldsCount + "/" + fields
                            + ", methods " + methods + " <= " + profile.constantInterfaceMaxMethods
            ));
        }
        return res;
    }
}
