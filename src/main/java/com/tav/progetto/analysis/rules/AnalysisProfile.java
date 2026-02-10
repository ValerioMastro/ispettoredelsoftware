package com.tav.progetto.analysis.rules;

import java.util.HashSet;
import java.util.Set;

public class AnalysisProfile {
    public int godClassMaxMethods = 20;
    public int godClassMaxFields = 15;
    public int longParamListMaxParams = 4;
    public Set<String> enabledRules = new HashSet<>();

    public AnalysisProfile() {
        enabledRules.add("GOD_CLASS");
        enabledRules.add("LONG_PARAM_LIST");
    }
}
