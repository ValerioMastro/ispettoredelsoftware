package com.tav.progetto.analysis.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalysisProfile {
    public int godClassMaxMethods = 20;
    public int godClassMaxFields = 15;
    public int longParamListMaxParams = 4;
    public int lazyClassMaxMethods = 2;
    public int lazyClassMaxFields = 1;
    public double utilityMinStaticMethodRatio = 0.80;
    public int utilityMaxInstanceFields = 1;
    public int yoyoMaxInheritanceDepth = 3;
    public int constantInterfaceMinConstantFields = 2;
    public int constantInterfaceMaxMethods = 1;
    public int badSingletonMaxMutableInstanceFields = 0;
    public int switchManiaMaxSwitchesPerClass = 2;
    public int switchManiaMaxCasesPerMethod = 5;
    public List<String> vendorLockInForbiddenPackages = new ArrayList<>(
            List.of("oracle.", "com.ibm.", "com.vendor.")
    );
    public Set<String> enabledRules = new HashSet<>();

    public AnalysisProfile() {
        Collections.addAll(
                enabledRules,
                "GOD_CLASS",
                "LONG_PARAM_LIST",
                "LAZY_CLASS",
                "UTILITY_CLASS",
                "BAD_SINGLETON",
                "SWITCH_MANIA",
                "VENDOR_LOCK_IN",
                "YOYO",
                "CONSTANT_INTERFACE",
                "BROKEN_UTILITY_CLASS"
        );
    }
}
