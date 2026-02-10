package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class GodClassRule implements Rule {
    @Override
    public String getId() { return "GOD_CLASS"; }

    @Override
    public String getName() { return "God Class"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!profile.enabledRules.contains(getId())) return res;
        if (metrics.totalMethods > profile.godClassMaxMethods || metrics.fields > profile.godClassMaxFields) {
            Violation v = new Violation();
            v.className = metrics.className;
            v.ruleId = getId();
            v.description = "Class with too many methods/fields";
            v.severity = com.tav.progetto.analysis.core.Severity.HIGH;
            res.add(v);
        }
        return res;
    }
}
