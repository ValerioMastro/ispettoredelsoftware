package com.tav.progetto.analysis.scanner;

import com.tav.progetto.analysis.core.TargetDescriptor;
import com.tav.progetto.analysis.core.TargetType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ClassPathScanner {
    // Default PW4: ignore inner/anonymous classes (e.g. A$Inner, A$1).
    private static final boolean EXCLUDE_DOLLAR_CLASSES = true;
    private static final String MODULE_INFO_CLASS = "module-info.class";
    private static final String PACKAGE_INFO_CLASS = "package-info.class";

    public List<ScannedClass> findClasses(TargetDescriptor target) {
        List<ScannedClass> result = new ArrayList<>();
        if (target.getType() == TargetType.DIRECTORY) {
            scanDirectory(target, result);
        } else if (target.getType() == TargetType.JAR) {
            scanJar(target, result);
        }
        return result;
    }

    private void scanDirectory(TargetDescriptor target, List<ScannedClass> out) {
        Path root = Path.of(target.getPath());
        if (!Files.exists(root)) return;
        try (Stream<Path> paths = Files.walk(root)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".class"))
                    .filter(p -> !isIgnoredClassFileName(p.getFileName().toString()))
                    .filter(p -> !shouldExcludeDollarClass(p.getFileName().toString()))
                    .forEach(p -> {
                        String fqcn = toFqcnFromRelativePath(root.relativize(p));
                        if (fqcn == null) return;
                        if (shouldExcludeDollarClass(fqcn)) return;
                        out.add(new ScannedClass(fqcn, p.toString(), target.getType()));
                    });
        } catch (IOException e) {
            // ignore for demo/prototype
        }
    }

    private void scanJar(TargetDescriptor target, List<ScannedClass> out) {
        File jarPath = new File(target.getPath());
        if (!jarPath.exists() || !jarPath.isFile()) return;
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                if (name == null) continue;
                if (name.startsWith("META-INF/")) continue;
                if (!name.endsWith(".class")) continue;
                if (isIgnoredClassFileName(basename(name))) continue;
                if (shouldExcludeDollarClass(name)) continue;

                String fqcn = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                if (fqcn.isEmpty()) continue;
                if (shouldExcludeDollarClass(fqcn)) continue;
                out.add(new ScannedClass(fqcn, jarPath.getPath() + "!/" + name, target.getType()));
            }
        } catch (IOException e) {
            // ignore for demo/prototype
        }
    }

    private String toFqcnFromRelativePath(Path relativeClassPath) {
        if (relativeClassPath == null) return null;
        String rel = relativeClassPath.toString();
        if (!rel.endsWith(".class")) return null;
        String noExt = rel.substring(0, rel.length() - ".class".length());
        if (noExt.isEmpty()) return null;
        return noExt.replace(File.separatorChar, '.').replace('/', '.');
    }

    private boolean isIgnoredClassFileName(String fileName) {
        return MODULE_INFO_CLASS.equals(fileName) || PACKAGE_INFO_CLASS.equals(fileName);
    }

    private boolean shouldExcludeDollarClass(String nameOrFqcn) {
        return EXCLUDE_DOLLAR_CLASSES && nameOrFqcn != null && nameOrFqcn.contains("$");
    }

    private String basename(String path) {
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
}
