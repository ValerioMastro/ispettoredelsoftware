package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VendorLockInRule implements Rule {
    @Override
    public String getId() { return "VENDOR_LOCK_IN"; }

    @Override
    public String getName() { return "Vendor Lock-In"; }

    @Override
    public List<Violation> apply(ClassMetrics metrics, AnalysisProfile profile) {
        List<Violation> res = new ArrayList<>();
        if (!isEnabled(profile) || metrics.hasInspectionError()) return res;

        // Structural heuristic only: we match configured package prefixes against referenced type names.
        List<String> forbiddenPackages = normalizeForbiddenPackages(profile.vendorLockInForbiddenPackages);
        if (forbiddenPackages.isEmpty()) return res;

        Set<String> offendingTypes = new LinkedHashSet<>();
        collectIfForbidden(offendingTypes, metrics.superClassName, forbiddenPackages);
        for (String interfaceName : safeTypeNames(metrics.interfaceNames)) {
            collectIfForbidden(offendingTypes, interfaceName, forbiddenPackages);
        }
        for (String referencedTypeName : safeTypeNames(metrics.referencedTypeNames)) {
            collectIfForbidden(offendingTypes, referencedTypeName, forbiddenPackages);
        }
        if (offendingTypes.isEmpty()) return res;

        Set<String> matchedPackages = new LinkedHashSet<>();
        for (String offendingType : offendingTypes) {
            String matchedPackage = findMatchingPackage(offendingType, forbiddenPackages);
            if (matchedPackage != null) matchedPackages.add(matchedPackage);
        }

        res.add(newViolation(
                metrics,
                Severity.MEDIUM,
                "Vendor lock-in: references forbidden packages "
                        + String.join(", ", matchedPackages)
                        + " via " + String.join(", ", offendingTypes)
        ));
        return res;
    }

    private List<String> normalizeForbiddenPackages(List<String> forbiddenPackages) {
        List<String> normalized = new ArrayList<>();
        if (forbiddenPackages == null) return normalized;
        for (String forbiddenPackage : forbiddenPackages) {
            if (forbiddenPackage == null) continue;
            String trimmed = forbiddenPackage.trim();
            if (!trimmed.isEmpty()) normalized.add(trimmed);
        }
        return normalized;
    }

    private void collectIfForbidden(Set<String> offendingTypes, String typeName, List<String> forbiddenPackages) {
        if (typeName == null || typeName.isBlank()) return;
        if (findMatchingPackage(typeName, forbiddenPackages) != null) offendingTypes.add(typeName);
    }

    private String findMatchingPackage(String typeName, List<String> forbiddenPackages) {
        for (String forbiddenPackage : forbiddenPackages) {
            if (typeName.startsWith(forbiddenPackage)) return forbiddenPackage;
        }
        return null;
    }

    private List<String> safeTypeNames(List<String> typeNames) {
        return typeNames == null ? List.of() : typeNames;
    }
}
