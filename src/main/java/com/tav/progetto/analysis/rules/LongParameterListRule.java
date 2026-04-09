package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class LongParameterListRule implements Rule {
    @Override
    public String getId() { return "LONG_PARAM_LIST"; }

    @Override
    public String getName() { return "Long Parameter List"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;
        if (metrics.maxParametersPerMethod > profile.longParamListMaxParams) {
            res.add(newViolation(
                    metrics,
                    Severity.MEDIUM,
                    "Long parameter list: max parameters " + metrics.maxParametersPerMethod
                            + " > " + profile.longParamListMaxParams
            ));
        }
        return res;
    }
}
