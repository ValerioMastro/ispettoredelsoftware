package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {
    private final List<Rule> rules = new ArrayList<>();

    public RuleEngine() {
        rules.add(new GodClassRule());
        rules.add(new LongParameterListRule());
    }

    public List<Violation> analyzeClass(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        for (Rule r : rules) {
            res.addAll(r.apply(metrics, profile));
        }
        return res;
    }
}
