package main_package.model;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static main_package.tools.Constants.MAIN_PATH;

public class InformationGenerator {
    private Set<File> classes;
    private List<String> classesName;
    private Set<String> packagesName;
    private Map<String, Integer> packagesWeights;
    private Map<String, Integer> methodsWeights;
    private Map<String, Integer> filesWeights;
    private Map<String, Map<String, Integer>> methodsRelations;

    public InformationGenerator() {
        this.packagesWeights = new HashMap<>();
        this.methodsWeights = new HashMap<>();
        this.filesWeights = new HashMap<>();

        try {
            specifySymbolsSolver();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.classes = getClasses();
        this.classesName = getClassesName(classes);
        this.packagesName = getPackagesName(classes);

        getMethodsRelations();
        getPackagesRelations();
        getFilesRelations();
    }

    public void test() {
        System.out.println("Test");
    }

    //konfiguracja typeSolvera
    private void specifySymbolsSolver() throws IOException {
        TypeSolver typeSolver = new JavaParserTypeSolver(MAIN_PATH);
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        JavaSymbolSolver javaSymbolSolver;
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(typeSolver);

        List<String> listJars = getJarsToResolver();

        listJars.forEach(jar -> {
            try {
                combinedTypeSolver.add(new JarTypeSolver(jar));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        javaSymbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSymbolSolver);
    }

    //szukam jary bibliotek, itp ktore wystepuja w projekcie
    private List<String> getJarsToResolver() {
        List<String > listJars;
        String path = System.getProperty("java.class.path");
        listJars = Arrays.asList(path.split(";"));
        listJars = listJars.stream().filter(jar -> jar.endsWith(".jar")).collect(Collectors.toList());
        return listJars;
    }

    //set klas
    private Set<File> getClasses() {
        Set<File> searchedClasses = new HashSet<>();
        File mainDir = new File(MAIN_PATH); // tutaj szukam plikow naszego projektu
        Arrays.stream(mainDir.listFiles()).forEach(file -> {
            checkDirectory(file, searchedClasses);
        }); //tworze liste klas
        return searchedClasses;
    }

    private List<String> getClassesName(Set<File> classesToSearch) {
        List<String> searchedClassesName = new ArrayList<>();
        classesToSearch.forEach(file -> searchedClassesName.add(file.getName().substring(0, file.getName().lastIndexOf(".java"))));
        //wybieram z listy klas tylko nazwy bez .java
        return searchedClassesName;
    }

    private Set<String> getPackagesName(Set<File> classesToSearch) {
        Set<String> packagesName = new HashSet<>();

        classesToSearch.forEach(
                file -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(file);
                        String packageName = cu.getPackageDeclaration().get().getName().asString();
                        packagesName.add(packageName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );

        return packagesName;
    }

    //sprawdzam co zawiera folder, jesli inny folder to przechodze do niego
    private void checkDirectory(File file, Set<File> fileSet) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(cFile -> {
                if (cFile.isDirectory()) {
                    checkDirectory(cFile, fileSet);
                } else {
                    fileSet.add(cFile);
                }
            });
        } else {
            fileSet.add(file);
        }
    }

    //relacje miedzy package
    public Map<String, Map<String, AtomicInteger>> getPackagesRelations() {
        Map<String, Map<String, AtomicInteger>> packagesRelationsMap = new HashMap<>();
        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                PackageNameSearching packageNameSearching = new PackageNameSearching(packagesName);
                cu.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                    methodDeclaration.accept(packageNameSearching, null);
                });

                if (packageNameSearching.getMapSize() > 0) {
                    System.out.println(methodDeclaration.resolve().getPackageName() + " "+ methodDeclaration.getName() + " wywulujaca");
                    packagesRelationsMap.put(cu.getPackageDeclaration().get().getName().asString(), packageNameSearching.getPackageMap());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        addSizeInformation(packagesRelationsMap, packagesWeights);

        return packagesRelationsMap;
    }

    //relacje miedzy metodami
    public Map<String, Map<String, AtomicInteger>> getMethodsRelations() {
        Map<String, Map<String, AtomicInteger>> methodsRelationsMap = new HashMap<>();

        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(mcd -> {
                    MethodNameSearching methodNameSearching = new MethodNameSearching(packagesName);
                    mcd.accept(methodNameSearching, null);

                    if (methodNameSearching.getMapSize() > 0) {
                        methodsRelationsMap.put(mcd.resolve().getName(), methodNameSearching.getMethodMap());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        addSizeInformation(methodsRelationsMap, methodsWeights);
        return methodsRelationsMap;
    }

    //relacje miedzy plikami
    public Map<String, Map<String, AtomicInteger>> getFilesRelations() {
        Map<String, Map<String, AtomicInteger>> filesRelationsMap = new HashMap<>();

        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                String fileName = file.getName();
                cu.findAll(MethodDeclaration.class).forEach(mcd -> {
                    FileNameSearching fileNameSearching = new FileNameSearching(packagesName);
                    mcd.accept(fileNameSearching, null);
                    if (fileNameSearching.getMapSize() > 0) {
                        filesRelationsMap.put(fileName, fileNameSearching.getMethodMap());
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });

        addSizeInformation(filesRelationsMap,filesWeights);
        return filesRelationsMap;
    }

    private static class PackageNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, AtomicInteger> packageMap = new HashMap<String, AtomicInteger>();
        private Set<String > packagesName;
        PackageNameSearching(Set<String > packagesName ) {
            this.packagesName = packagesName;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            String packagePath = n.resolve().getPackageName();
            packagesName.stream()
                    .filter(pN -> pN.equals(packagePath))
                    .forEach(pN ->
                    {
                        System.out.println(packagePath+ " " + n.resolve().getClassName() + " " + n.getName().asString() + " wywolywana");
                        packageMap.putIfAbsent(packagePath,new AtomicInteger(0));
                        packageMap.get(packagePath).incrementAndGet();
                    }
            );
        }

        private Map<String, AtomicInteger> getPackageMap() {
            return packageMap;
        }

        private int getMapSize() {
            return packageMap.size();
        }
    }

    private static class MethodNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, AtomicInteger> methodMap = new HashMap<String, AtomicInteger>();
        private Set<String > packagesName;
        MethodNameSearching(Set<String > packagesName ) {
            this.packagesName = packagesName;
        }
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            String packagePath = n.resolve().getPackageName();
            String methodName = n.resolve().getName();
            packagesName.stream()
                    .filter(mN -> mN.equals(packagePath))
                    .forEach(mN ->
                    {
                        //System.out.println(n.resolve().getClassName());
                        methodMap.putIfAbsent(methodName,new AtomicInteger(0));
                        methodMap.get(methodName).incrementAndGet();
                    });
        }

        private Map<String, AtomicInteger> getMethodMap() {
            return methodMap;
        }

        private int getMapSize() {
            return methodMap.size();
        }
    }

    private static class FileNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, AtomicInteger> methodMap = new HashMap<String, AtomicInteger>();
        private Set<String > packagesName;

        FileNameSearching(Set<String > packagesName ) {
            this.packagesName = packagesName;
        }
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            String packagePath = n.resolve().getPackageName();
            String fileName = n.resolve().getClassName() + ".java";
            packagesName.stream()
                    .filter(mN -> mN.equals(packagePath))
                    .forEach(mN ->
                    {
                        methodMap.putIfAbsent(fileName,new AtomicInteger(0));
                        methodMap.get(fileName).incrementAndGet();
                    });
        }

        private Map<String, AtomicInteger> getMethodMap() {
            return methodMap;
        }

        private int getMapSize() {
            return methodMap.size();
        }
    }


    private void addSizeInformation(Map<String, Map<String, AtomicInteger>> mainMap, Map<String, Integer> valueMap) {
        for (Map.Entry<String, Map<String, AtomicInteger>> entry : mainMap.entrySet()) {
            Map<String, AtomicInteger> mapValue = entry.getValue();

            for (Map.Entry<String, AtomicInteger> entryMap : mapValue.entrySet()) {
                String name = entryMap.getKey();
                if (valueMap.containsKey(name)) {
                    valueMap.put(name, valueMap.get(name) + 1);
                } else {
                    valueMap.put(name, 1);
                }
            }
        }
    }

    public Map<String, Integer> getPackagesWeights() {
        return packagesWeights;
    }

    public Map<String, Integer> getMethodsWeights() {
        return methodsWeights;
    }

    public Map<String, Integer> getFilesWeights() {
        return filesWeights;
    }
}
