package com.tav.progetto.analysis.loader;

import com.tav.progetto.analysis.classfile.ClassFileInspector;
import com.tav.progetto.analysis.core.TargetDescriptor;
import com.tav.progetto.analysis.core.TargetType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ClassLoadingService {
    public URLClassLoader createClassLoader(TargetDescriptor target) {
        try {
            URL url;
            if (target.getType() == TargetType.DIRECTORY) {
                File root = new File(target.getPath());
                URL raw = root.toURI().toURL();
                // URLClassLoader expects directories to end with '/'.
                url = raw.toExternalForm().endsWith("/") ? raw : new URL(raw.toExternalForm() + "/");
            } else if (target.getType() == TargetType.JAR) {
                File jar = new File(target.getPath());
                url = jar.toURI().toURL();
            } else if (target.getType() == TargetType.CLASS_FILE) {
                url = inferRootDirectoryUrlFromClassFile(Path.of(target.getPath()));
            } else {
                throw new IllegalArgumentException("Unsupported target type: " + target.getType());
            }
            return new URLClassLoader(new URL[]{url}, ClassLoadingService.class.getClassLoader());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid target path: " + target.getPath(), e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to inspect .class file: " + target.getPath(), e);
        }
    }

    public Class<?> loadClass(String fqcn, ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(fqcn, false, loader);
    }

    private URL inferRootDirectoryUrlFromClassFile(Path classFile) throws IOException {
        Path file = classFile.toAbsolutePath().normalize();
        String internalName = ClassFileInspector.readInternalClassName(file);
        Path suffix = Path.of(internalName.replace('/', File.separatorChar) + ".class");

        Path rootDir = file.getParent();
        if (rootDir != null && file.endsWith(suffix)) {
            rootDir = file;
            for (int i = 0; i < suffix.getNameCount(); i++) {
                rootDir = rootDir.getParent();
                if (rootDir == null) break;
            }
        }
        if (rootDir == null) {
            throw new IOException("Cannot infer root directory for class file");
        }

        URL raw = rootDir.toUri().toURL();
        return raw.toExternalForm().endsWith("/") ? raw : new URL(raw.toExternalForm() + "/");
    }
}
