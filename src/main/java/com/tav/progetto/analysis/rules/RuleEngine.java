package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {
    private final List<Rule> rules = new ArrayList<>();

    public RuleEngine() {
        this(createDefaultRules());
    }

    public RuleEngine(List<Rule> rules) {
        this.rules.addAll(rules);
    }

    public List<Violation> analyzeClass(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        for (Rule r : rules) {
            res.addAll(r.apply(metrics, profile));
        }
        return res;
    }

    private static List<Rule> createDefaultRules() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new GodClassRule());
        rules.add(new LongParameterListRule());
        rules.add(new LazyClassRule());
        rules.add(new UtilityClassRule());
        rules.add(new BadSingletonRule());
        rules.add(new SwitchManiaRule());
        rules.add(new VendorLockInRule());
        rules.add(new YoyoRule());
        rules.add(new ConstantInterfaceRule());
        rules.add(new BrokenUtilityClassRule());
        return rules;
    }
}
