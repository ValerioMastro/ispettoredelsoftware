package com.tav.progetto.analysis.core;

import com.tav.progetto.analysis.rules.AnalysisProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassFileTargetIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldAnalyzeSingleClassFile_GodClass() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.GodClass", godClassSource());
        Path classFile = classesRoot.resolve("com/example/sample/GodClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.GodClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "GOD_CLASS".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_LongParamList() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.LongParamClass", longParamClassSource());
        Path classFile = classesRoot.resolve("com/example/sample/LongParamClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.LongParamClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "LONG_PARAM_LIST".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_LazyClass() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.LazyClass", lazyClassSource());
        Path classFile = classesRoot.resolve("com/example/sample/LazyClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.LazyClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "LAZY_CLASS".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_UtilityClass() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.RealUtilityClass", utilityClassSource());
        Path classFile = classesRoot.resolve("com/example/sample/RealUtilityClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.RealUtilityClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "UTILITY_CLASS".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_BadSingleton() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.BadSingleton", badSingletonSource());
        Path classFile = classesRoot.resolve("com/example/sample/BadSingleton.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.BadSingleton", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "BAD_SINGLETON".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_SwitchMania() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.SwitchHeavyClass", switchHeavySource());
        Path classFile = classesRoot.resolve("com/example/sample/SwitchHeavyClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.SwitchHeavyClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "SWITCH_MANIA".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_Yoyo() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.DeepLeaf", yoyoClassSource());
        Path classFile = classesRoot.resolve("com/example/sample/DeepLeaf.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.DeepLeaf", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "YOYO".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_ConstantInterface() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.ConstantInterface", constantInterfaceSource());
        Path classFile = classesRoot.resolve("com/example/sample/ConstantInterface.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.ConstantInterface", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "CONSTANT_INTERFACE".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_ConstantHolderClass() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.ConstantsHolder", constantHolderSource());
        Path classFile = classesRoot.resolve("com/example/sample/ConstantsHolder.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.ConstantsHolder", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "CONSTANT_INTERFACE".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_BrokenUtilityClass() throws IOException {
        Path classesRoot = compile(tempDir, "com.example.sample.UtilityClass", brokenUtilityClassSource());
        Path classFile = classesRoot.resolve("com/example/sample/UtilityClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.UtilityClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "BROKEN_UTILITY_CLASS".equals(v.ruleId)));
    }

    @Test
    void shouldAnalyzeSingleClassFile_VendorLockIn() throws IOException {
        Path classesRoot = compile(tempDir, vendorLockedSources());
        Path classFile = classesRoot.resolve("com/example/sample/VendorLockedClass.class");

        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        ProjectAnalysisResult result = analyzer.analyze(
                new TargetDescriptor(classFile.toString(), TargetType.CLASS_FILE),
                new AnalysisProfile()
        );

        assertEquals(1, result.classResults.size());
        var cr = result.classResults.get(0);
        assertEquals("com.example.sample.VendorLockedClass", cr.metrics.className);
        assertTrue(cr.violations.stream().anyMatch(v -> "VENDOR_LOCK_IN".equals(v.ruleId)));
    }

    private Path compile(Path root, String fqcn, String source) throws IOException {
        return compile(root, Map.of(fqcn, source));
    }

    private Path compile(Path root, Map<String, String> sources) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("JDK compiler not available");

        Path srcRoot = root.resolve("src");
        Path classesRoot = root.resolve("classes");
        Files.createDirectories(srcRoot);
        Files.createDirectories(classesRoot);

        List<java.io.File> javaFiles = new ArrayList<>();
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String rel = entry.getKey().replace('.', '/') + ".java";
            Path javaFile = srcRoot.resolve(rel);
            Files.createDirectories(javaFile.getParent());
            Files.writeString(javaFile, entry.getValue(), StandardCharsets.UTF_8);
            javaFiles.add(javaFile.toFile());
        }

        try (StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {
            var units = fm.getJavaFileObjectsFromFiles(javaFiles);
            boolean ok = compiler.getTask(
                    null,
                    fm,
                    null,
                    java.util.List.of("-d", classesRoot.toString()),
                    null,
                    units
            ).call();
            if (!ok) throw new IllegalStateException("Compilation failed for " + sources.keySet());
        }
        return classesRoot;
    }

    private String godClassSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class GodClass {\n"
                + "    private int f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16;\n"
                + "\n"
                + "    public void m1() {}\n"
                + "    public void m2() {}\n"
                + "    public void m3() {}\n"
                + "    public void m4() {}\n"
                + "    public void m5() {}\n"
                + "    public void m6() {}\n"
                + "    public void m7() {}\n"
                + "    public void m8() {}\n"
                + "    public void m9() {}\n"
                + "    public void m10() {}\n"
                + "    public void m11() {}\n"
                + "    public void m12() {}\n"
                + "    public void m13() {}\n"
                + "    public void m14() {}\n"
                + "    public void m15() {}\n"
                + "    public void m16() {}\n"
                + "    public void m17() {}\n"
                + "    public void m18() {}\n"
                + "    public void m19() {}\n"
                + "    public void m20() {}\n"
                + "    public void m21() {}\n"
                + "}\n";
    }

    private String longParamClassSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class LongParamClass {\n"
                + "    public void longMethod(int a, int b, int c, int d, int e) {}\n"
                + "}\n";
    }

    private String constantInterfaceSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public interface ConstantInterface {\n"
                + "    int A = 1;\n"
                + "    String B = \"X\";\n"
                + "}\n";
    }

    private String constantHolderSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class ConstantsHolder {\n"
                + "    public static final int A = 1;\n"
                + "    public static final int B = 2;\n"
                + "    private int ignored;\n"
                + "\n"
                + "    public int getIgnored() { return ignored; }\n"
                + "}\n";
    }

    private String lazyClassSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class LazyClass {\n"
                + "    private String name;\n"
                + "\n"
                + "    public String getName() { return name; }\n"
                + "}\n";
    }

    private String utilityClassSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class RealUtilityClass {\n"
                + "    private static final String PREFIX = \"util\";\n"
                + "\n"
                + "    private RealUtilityClass() {}\n"
                + "\n"
                + "    public static String upper(String value) { return value.toUpperCase(); }\n"
                + "    public static String prefix(String value) { return PREFIX + value; }\n"
                + "}\n";
    }

    private String badSingletonSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class BadSingleton {\n"
                + "    private static final BadSingleton INSTANCE = new BadSingleton();\n"
                + "    private int counter;\n"
                + "\n"
                + "    public BadSingleton() {}\n"
                + "\n"
                + "    public static BadSingleton getInstance() { return INSTANCE; }\n"
                + "    public void increment() { counter++; }\n"
                + "}\n";
    }

    private String yoyoClassSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "class Level1 {}\n"
                + "class Level2 extends Level1 {}\n"
                + "class Level3 extends Level2 {}\n"
                + "class Level4 extends Level3 {}\n"
                + "public class DeepLeaf extends Level4 {}\n";
    }

    private String switchHeavySource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class SwitchHeavyClass {\n"
                + "    public int code(int value) {\n"
                + "        switch (value) {\n"
                + "            case 0: return 0;\n"
                + "            case 1: return 10;\n"
                + "            case 2: return 20;\n"
                + "            case 3: return 30;\n"
                + "            case 4: return 40;\n"
                + "            case 5: return 50;\n"
                + "            default: return -1;\n"
                + "        }\n"
                + "    }\n"
                + "}\n";
    }

    private String brokenUtilityClassSource() {
        return ""
                + "package com.example.sample;\n"
                + "\n"
                + "public class UtilityClass {\n"
                + "    public static String upper(String value) { return value.toUpperCase(); }\n"
                + "    public static int sum(int left, int right) { return left + right; }\n"
                + "}\n";
    }

    private Map<String, String> vendorLockedSources() {
        Map<String, String> sources = new LinkedHashMap<>();
        sources.put(
                "oracle.platform.BaseComponent",
                ""
                        + "package oracle.platform;\n"
                        + "\n"
                        + "public class BaseComponent {\n"
                        + "}\n"
        );
        sources.put(
                "com.ibm.sdk.LegacyContract",
                ""
                        + "package com.ibm.sdk;\n"
                        + "\n"
                        + "public interface LegacyContract {\n"
                        + "    void sync();\n"
                        + "}\n"
        );
        sources.put(
                "com.example.sample.VendorLockedClass",
                ""
                        + "package com.example.sample;\n"
                        + "\n"
                        + "import com.ibm.sdk.LegacyContract;\n"
                        + "import oracle.platform.BaseComponent;\n"
                        + "\n"
                        + "public class VendorLockedClass extends BaseComponent implements LegacyContract {\n"
                        + "    private BaseComponent cachedBase;\n"
                        + "\n"
                        + "    @Override\n"
                        + "    public void sync() {}\n"
                        + "\n"
                        + "    public void connect(BaseComponent value) {\n"
                        + "        this.cachedBase = value;\n"
                        + "    }\n"
                        + "}\n"
        );
        return sources;
    }
}
