package com.tav.progetto.analysis.scanner;

import com.tav.progetto.analysis.core.TargetDescriptor;
import com.tav.progetto.analysis.core.TargetType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassPathScanner {
    public List<File> findClassFiles(TargetDescriptor target) {
        List<File> result = new ArrayList<>();
        if (target.getType() == TargetType.DIRECTORY) {
            File root = new File(target.getPath());
            scanDir(root, result);
        }
        return result;
    }

    private void scanDir(File dir, List<File> out) {
        if (dir == null || !dir.exists()) return;
        if (dir.isFile() && dir.getName().endsWith(".java")) {
            out.add(dir);
            return;
        }
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File f : children) scanDir(f, out);
    }
}
