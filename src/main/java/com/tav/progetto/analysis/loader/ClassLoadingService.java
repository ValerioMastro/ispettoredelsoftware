package com.tav.progetto.analysis.loader;

import com.tav.progetto.analysis.core.TargetDescriptor;
import com.tav.progetto.analysis.core.TargetType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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
            } else {
                throw new IllegalArgumentException("Unsupported target type: " + target.getType());
            }
            return new URLClassLoader(new URL[]{url}, ClassLoadingService.class.getClassLoader());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid target path: " + target.getPath(), e);
        }
    }

    public Class<?> loadClass(String fqcn, ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(fqcn, false, loader);
    }
}

