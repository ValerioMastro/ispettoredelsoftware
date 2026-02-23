package com.tav.progetto.analysis.core;

import com.tav.progetto.analysis.rules.AnalysisProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectAnalyzerIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldDetectExpectedViolationsInSampleProject() {
        Path root = tempDir.resolve("classes");
        try {
            Files.createDirectories(root.resolve("com/example/sample"));
            Files.createFile(root.resolve("com/example/sample/GodClass.class"));
            Files.createFile(root.resolve("com/example/sample/LongParamClass.class"));
            Files.createFile(root.resolve("com/example/sample/UtilityClass.class"));
            Files.createFile(root.resolve("com/example/sample/ConstantInterface.class"));
            Files.createFile(root.resolve("module-info.class"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        AnalysisProfile profile = new AnalysisProfile();

        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(root.toString(), TargetType.DIRECTORY),
                profile
        );

        assertEquals(4, result.classResults.size());
        assertTrue(result.healthScore >= 0 && result.healthScore <= 100);

        Map<String, ClassAnalysisResult> byClass = result.classResults.stream()
                .collect(Collectors.toMap(cr -> cr.metrics.className, Function.identity()));

        assertTrue(byClass.get("com.example.sample.GodClass").violations.isEmpty());
        assertTrue(byClass.get("com.example.sample.LongParamClass").violations.isEmpty());
        assertTrue(byClass.get("com.example.sample.UtilityClass").violations.isEmpty());
        assertTrue(byClass.get("com.example.sample.ConstantInterface").violations.isEmpty());
    }
}
