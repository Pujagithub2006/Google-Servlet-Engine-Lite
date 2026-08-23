package org.gse_lite.scanner;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClassScanner {
    public static Set<Class<?>> scan(String packageName) throws Exception {
        Set<Class<?>> controllerClasses = new HashSet<>();

        String packagePath = packageName.replace('.', '/');

        URL resource = Thread.currentThread().getContextClassLoader().getResource(packagePath);

        if (resource == null) return controllerClasses; // empty set

        File directory = new File(resource.getFile());

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().endsWith(".class")) {
                String controllerClassName = packageName + "." + file.getName().replace(".class", "");
                controllerClasses.add(Class.forName(controllerClassName));
            }
        }

        return controllerClasses;
    }
}
