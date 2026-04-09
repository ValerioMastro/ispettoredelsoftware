package com.tav.progetto.analysis.rules;

import com.tav.progetto.analysis.core.Severity;
import com.tav.progetto.analysis.core.Violation;
import com.tav.progetto.analysis.metrics.ClassMetrics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VendorLockInRuleTest {
    private final VendorLockInRule rule = new VendorLockInRule();

    @Test
    void shouldReportViolationWhenClassReferencesForbiddenVendorTypes() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.className = "com.example.VendorLockedService";
        metrics.superClassName = "oracle.platform.BaseComponent";
        metrics.interfaceNames = new ArrayList<>(List.of("com.example.Service"));
        metrics.referencedTypeNames = new ArrayList<>(List.of(
                "oracle.platform.BaseComponent",
                "com.ibm.sdk.LegacySession",
                "java.lang.String"
        ));

        List<Violation> violations = rule.apply(metrics, profile);

        assertEquals(1, violations.size());
        Violation violation = violations.get(0);
        assertEquals("VENDOR_LOCK_IN", violation.ruleId);
        assertEquals(Severity.MEDIUM, violation.severity);
        assertTrue(violation.description.contains("oracle."));
        assertTrue(violation.description.contains("com.ibm."));
    }

    @Test
    void shouldNotReportViolationWhenOnlyPortableTypesAreReferenced() {
        AnalysisProfile profile = new AnalysisProfile();
        ClassMetrics metrics = new ClassMetrics();
        metrics.superClassName = "java.lang.Object";
        metrics.interfaceNames = new ArrayList<>(List.of("java.io.Serializable"));
        metrics.referencedTypeNames = new ArrayList<>(List.of(
                "java.lang.String",
                "java.util.List"
        ));

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotReportViolationWhenForbiddenPackageListIsEmpty() {
        AnalysisProfile profile = new AnalysisProfile();
        profile.vendorLockInForbiddenPackages = new ArrayList<>();
        ClassMetrics metrics = new ClassMetrics();
        metrics.referencedTypeNames = new ArrayList<>(List.of("oracle.platform.BaseComponent"));

        List<Violation> violations = rule.apply(metrics, profile);

        assertTrue(violations.isEmpty());
    }
}
