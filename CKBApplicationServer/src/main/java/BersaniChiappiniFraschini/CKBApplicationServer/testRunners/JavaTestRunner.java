package BersaniChiappiniFraschini.CKBApplicationServer.testRunners;

import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Opcodes;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaTestRunner implements TestRunner {
    public Map<String, TestStatus> launchUnitTests(String compiledProjectDirectory, String testsFileName) throws Exception {
        if (compiledProjectDirectory == null || testsFileName == null) return null;
        Class<?> testClass = loadTestClassFromJar(compiledProjectDirectory, testsFileName);
        return runTests(testClass);
    }

    /**
     * Loads the test class as an internal Class.
     * @param jarFilePath path to jar file.
     * @param className name of the class containing tests.
     * @return the loaded Class object.
     * @throws Exception when the class is not found.
     */
    private Class<?> loadTestClassFromJar(String jarFilePath, String className) throws Exception {
        File jarFile = new File(jarFilePath);

        // Convert the JAR file path to URL with the "file" protocol
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
        System.out.println("JAR FILE PATH: " + jarFilePath);
        var canonicalName = findCanonicalNameInJar(jarFilePath, className);
        System.out.printf("CANONICAL NAME FOR %s: %s%n", className, canonicalName);

        // Load the class dynamically
        var aClass = Class.forName(canonicalName, true, classLoader);
        System.out.println("TEST CLASS: " + aClass);
        classLoader.close();
        return aClass;
    }

    /**
     * Runs the tests by calling JUnit on the loaded class.
     * @param testClass class containing tests.
     * @return the results of the tests as (Name of testcase, Status).
     */
    private Map<String, TestStatus> runTests(Class<?> testClass) {
        Map<String, TestStatus> results = new HashMap<>();

        LauncherDiscoveryRequest discoveryRequest = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(testClass))
                .build();

        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(summaryListener);
        launcher.execute(discoveryRequest);
        TestExecutionSummary summary = summaryListener.getSummary();

        Method[] methods = testClass.getDeclaredMethods();
        System.out.println("METHODS: "+ Arrays.toString(methods));
        // This is not particularly good but seems to work.
        for (var method : methods) {
            System.out.println("CHECKING METHOD: "+method.getName());
            System.out.println(Arrays.toString(method.getAnnotations()));
            if (method.isAnnotationPresent(Test.class)) {
                System.out.println("REGISTERED METHOD: " + method.getName());
                DisplayName displayNameAnnotation = method.getAnnotation(DisplayName.class);
                // Initialize all as passed
                if (displayNameAnnotation == null) {
                    results.put(method.getName() + "()", TestStatus.PASSED);
                } else {
                    results.put(displayNameAnnotation.value(), TestStatus.PASSED);
                }
            }
        }
        System.out.println("SUMMARY:");
        System.out.println("# FOUND:"+summary.getTestsFoundCount());
        System.out.println("# ABORTED:"+summary.getTestsAbortedCount());
        System.out.println("# SKIPPED:"+summary.getTestsSkippedCount());

        for (var failure : summary.getFailures()) {
            failure.getException().printStackTrace();
            results.put(failure.getTestIdentifier().getDisplayName(), TestStatus.FAILED);
        }

        return results;
    }

    /**
     * Finds the name of the package where the test-file is located in the jar file.
     * @param jarFilePath path to the jar file.
     * @param targetClassName name of the class containing tests.
     * @return the canonical name of the test class inside the jar.
     * @throws IOException when the jar is not found.
     */
    public String findCanonicalNameInJar(String jarFilePath, String targetClassName) throws IOException {
        File file = new File(jarFilePath);
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                var entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    var className = entryName.substring(entry.getName().lastIndexOf("/") + 1, entryName.lastIndexOf("."));
                    if (className.equals(targetClassName)) {
                        // Transform path to package name (Canonical name)
                        ClassReader classReader = new ClassReader(jarFile.getInputStream(entry));
                        ClassNameVisitor classNameVisitor = new ClassNameVisitor();
                        classReader.accept(classNameVisitor, 0);
                        return classNameVisitor.getClassName();
                    }
                }
            }
        }

        return null; // Class not found in the JAR
    }

    /**
     * Visitor used to convert the path to the test file into a package path.
     */
    @Getter
    private static class ClassNameVisitor extends ClassVisitor {
        private String className;

        public ClassNameVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            className = name.replace('/', '.');
        }
    }
}
