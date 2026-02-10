package com.tav.progetto.analysis.rules;

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
        if (!profile.enabledRules.contains(getId())) return res;
        if (metrics.maxParametersPerMethod > profile.longParamListMaxParams) {
            Violation v = new Violation();
            v.className = metrics.className;
            v.ruleId = getId();
            v.description = "Method with too many parameters";
            v.severity = com.tav.progetto.analysis.core.Severity.MEDIUM;
            res.add(v);
        }
        return res;
    }
}
