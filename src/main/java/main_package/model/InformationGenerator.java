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





//to do zmiany
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
                    packagesRelationsMap.put(cu.getPackageDeclaration().get().getName().asString(), packageNameSearching.getPackageMap());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        //addInformation(allRelations, packagesWeights);
        //packagesWeights.put("main_package", 1);

        return packagesRelationsMap;
    }

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

        //addInformation(methodsRelationsMap, methodsWeights);
       // methodsWeights.put("main", 1);
       // this.methodsRelations = allMethodsInformationMap;
        return methodsRelationsMap;
    }

    public Map<String, Map<String, Integer>> getFilesRelations() {
        Map<String, Map<String, Integer>> methodsRelations = this.methodsRelations;
        Map<String, Map<String, Integer>> filesRelations = new HashMap<>();
        Map<String, String> filesMethods = new HashMap<>();
        List<Integer> weights = new ArrayList<>();

        classes.forEach(file -> {
            String fileName = file.getName().substring(0, file.getName().lastIndexOf(".java"));
            filesWeights.put(fileName, (int) file.length());
            weights.add((int) file.length());
            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);
                CompilationUnit finalCu = cu;

                cu.findAll(MethodDeclaration.class)
                        .stream()
                        .forEach(mcd -> {

                            filesMethods.put(mcd.resolve().getName(), fileName);
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });


        for (String i : classesName) {
            Map<String, Integer> usageMap = new HashMap<>();
            filesRelations.put(i, usageMap);
        }

        //Set default relation value to 0
        Set<Map.Entry<String, Map<String, Integer>>> filesEntrySet = filesRelations.entrySet();
        for (Map.Entry<String, Map<String, Integer>> entry : filesEntrySet) {
            Map<String, Integer> usageMap = entry.getValue();
            for (String i : classesName) {
                usageMap.put(i, 0);
            }
        }

        Set<Map.Entry<String, Map<String, Integer>>> methodsEntrySet = methodsRelations.entrySet();
        for (Map.Entry<String, Map<String, Integer>> entry : methodsEntrySet) {

            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for (Map.Entry<String, Integer> littleEntry : littleEntrySet) {

                String fileName = filesMethods.get(entry.getKey());
                Map<String, Integer> usageMap = filesRelations.get(fileName);
                String methodFileName = filesMethods.get(littleEntry.getKey());
                int valueToPut = littleEntry.getValue() + usageMap.get(methodFileName);
                if (!fileName.equals(methodFileName)) {
                    usageMap.put(methodFileName, valueToPut);
                }
                filesRelations.put(fileName, usageMap);
            }
        }
        //Normalize filesWeights
        double max = (double) Collections.max(weights);
        double min = (double) Collections.min(weights);
        Set<Map.Entry<String, Integer>> weightsEntrySet = filesWeights.entrySet();
        for (Map.Entry<String, Integer> entry : weightsEntrySet) {
            double val = ((double) entry.getValue() - min) / (max - min);
            val = (val * 7 + 1);
            val *= 10;
            val = Math.round(val);
            val /= 10;
            filesWeights.put(entry.getKey(), (int) val);
        }

        return filesRelations;
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
            String packageName = n.resolve().getName();
            packagesName.stream()
                    .filter(pN -> pN.equals(packagePath))
                    .forEach(pN ->
                    {
                        packageMap.putIfAbsent(packageName,new AtomicInteger(0));
                        packageMap.get(packageName).incrementAndGet();
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

    private void addInformation(Map<String, Map<String, Integer>> mainMap, Map<String, Integer> valueMap) {
        for (Map.Entry<String, Map<String, Integer>> entry : mainMap.entrySet()) {
            Map<String, Integer> mapValue = entry.getValue();

            for (Map.Entry<String, Integer> entryMap : mapValue.entrySet()) {
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
