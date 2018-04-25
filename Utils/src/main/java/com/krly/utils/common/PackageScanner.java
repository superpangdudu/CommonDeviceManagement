package com.krly.utils.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PackageScanner {
    public List<String> scanPackage(String path) {
        path = path.replace(".", File.separator);
        String rootPath = PackageScanner.class.getResource("/").getPath();

        String scanPath = rootPath + path;
        List<String> fileList = scan(scanPath);

        //
        rootPath = rootPath.replaceFirst("/", "")
                .replace("/", "\\");

        List<String> classList = new ArrayList<>();
        for (String filePath : fileList) {
            String className = filePath.replace(rootPath, "").replace("\\", ".").replace(".class", "");
            classList.add(className);
        }

        return classList;
    }

    private List<String> scan(String path) {
        List<String> classList = new ArrayList<>();

        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tmp : files)
                classList.addAll(scan(tmp.getPath()));

        } else {
            if (file.getName().endsWith(".class") == false)
                return new ArrayList<>();

            classList.add(file.getPath());
        }

        return classList;
    }

    //===================================================================================
    public static void main(String[] args) {
        PackageScanner scanner = new PackageScanner();
        List<String> classList = scanner.scanPackage("com.krly.utils");
        for (String name : classList)
            System.out.println(name);
    }
}
