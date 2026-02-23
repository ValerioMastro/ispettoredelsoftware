package com.tav.progetto.analysis.scanner;

import com.tav.progetto.analysis.core.TargetDescriptor;
import com.tav.progetto.analysis.core.TargetType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassPathScannerTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldScanJarAndReturnFqcn() throws IOException {
        Path jarPath = tempDir.resolve("test.jar");
        try (OutputStream os = Files.newOutputStream(jarPath);
             JarOutputStream jos = new JarOutputStream(os)) {
            jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jos.write("Manifest-Version: 1.0\n".getBytes());
            jos.closeEntry();

            jos.putNextEntry(new JarEntry("com/example/A.class"));
            jos.write(new byte[]{0});
            jos.closeEntry();

            jos.putNextEntry(new JarEntry("com/example/B$Inner.class"));
            jos.write(new byte[]{0});
            jos.closeEntry();

            jos.putNextEntry(new JarEntry("module-info.class"));
            jos.write(new byte[]{0});
            jos.closeEntry();
        }

        ClassPathScanner scanner = new ClassPathScanner();
        List<ScannedClass> scanned = scanner.findClasses(new TargetDescriptor(jarPath.toString(), TargetType.JAR));

        List<String> names = scanned.stream().map(ScannedClass::getClassName).sorted().collect(Collectors.toList());
        assertEquals(List.of("com.example.A", "com.example.B$Inner"), names);
    }
}

