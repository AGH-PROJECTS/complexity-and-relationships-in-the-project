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
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.google.common.graph.ImmutableNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import main_package.Main;

public class InformationGenerator {
    private static final String MAIN_PATH = "src/main/java";
    private CombinedTypeSolver combinedTypeSolver;
    private TypeSolver typeSolver;
    private TypeSolver reflectionTypeSolver;
    private JavaSymbolSolver javaSymbolSolver;
    private File mainDir;
    private Set<File> classes;
    private List<String> classesName;
    private Map<String, Integer> packagesWeights;
    private Map<String, Integer> methodsWeights;
    private Map<String, Integer> filesWeights;

    public InformationGenerator() throws IOException {
        this.packagesWeights = new HashMap<>();
        this.methodsWeights = new HashMap<>();
        this.filesWeights = new HashMap<>();
        this.combinedTypeSolver = new CombinedTypeSolver();
        this.typeSolver = new JavaParserTypeSolver(MAIN_PATH);
        this.reflectionTypeSolver = new ReflectionTypeSolver();

        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(typeSolver);
        SymbolSolverCollectionStrategy symbolSolverCollectionStrategy = new SymbolSolverCollectionStrategy();
        String path = System.getProperty("java.class.path");
        String[] p;
        p = path.split(";");
        for(int i=1;i<p.length;i++) {
            combinedTypeSolver.add(new JarTypeSolver(p[i]));
        }

        this.javaSymbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSymbolSolver);

        mainDir = new File(MAIN_PATH);
        classes = new HashSet<>();

        Arrays.stream(mainDir.listFiles()).forEach(file -> {
            checkDirectory(file,classes);
        });

        classesName = new ArrayList<>();
        classes.forEach(file-> classesName.add(file.getName().substring(0,file.getName().lastIndexOf(".java"))));
    }

    public Map<String, Map<String, Integer>> getPackagesRelations() {
        Map<String, Map<String, Integer>> allPackagesInformationMap = new HashMap<>();

        classes.forEach(file -> {
            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);
                CompilationUnit finalCu = cu;
                cu.findAll(MethodDeclaration.class)
                        .stream()
                        .forEach(mcd-> {
                            PackageNameSearching packageNameSearching = new PackageNameSearching();
                            mcd.accept(packageNameSearching, null);

                            if(packageNameSearching.getMapSize() > 0) {
                                allPackagesInformationMap.put(finalCu.getPackageDeclaration().get().getName().asString(),packageNameSearching.getPackageMap());
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        addInformation(allPackagesInformationMap, packagesWeights);
        packagesWeights.put("main_package",1);
        return allPackagesInformationMap;
    }

    public Map<String, Map<String, Integer>> getMethodsRelations() {
        Map<String, Map<String, Integer>> allMethodsInformationMap = new HashMap<>();

        classes.forEach(file -> {
            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class)
                        .stream()
                        .forEach(mcd-> {
                            MethodNameSearching methodNameSearching = new MethodNameSearching();
                            mcd.accept(methodNameSearching, null);

                            if(methodNameSearching.getMapSize() > 0) {
                                allMethodsInformationMap.put(mcd.resolve().getName(),methodNameSearching.getMethodMap());
                            }

                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        addInformation(allMethodsInformationMap, methodsWeights);
        methodsWeights.put("main",1);
        return allMethodsInformationMap;
    }

    public Map<String, Map<String, Integer>> getFilesRelations() {
        Map<String, Map<String, Integer>> methodsRelations = Main.getMethodsRelations();
        Map<String, Map<String, Integer>> filesRelations = new HashMap<>();
        Map<String, String> filesMethods = new HashMap<>();
        List<Integer> weights = new ArrayList<>();

        classes.forEach(file -> {
            String fileName = file.getName().substring(0,file.getName().lastIndexOf(".java"));
            filesWeights.put(fileName, (int)file.length());
            weights.add((int)file.length());
            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);
                CompilationUnit finalCu = cu;

                cu.findAll(MethodDeclaration.class)
                        .stream()
                        .forEach(mcd-> {

                            filesMethods.put(mcd.resolve().getName(), fileName);
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });


        for(String i: classesName){
            Map<String, Integer> usageMap = new HashMap<>();
            filesRelations.put(i, usageMap);
        }

        //Set default relation value to 0
        Set<Map.Entry<String, Map<String, Integer>>> filesEntrySet = filesRelations.entrySet();
        for(Map.Entry<String, Map<String, Integer>> entry: filesEntrySet){
            Map<String, Integer> usageMap = entry.getValue();
            for(String i: classesName){
                usageMap.put(i, 0);
            }
        }

        Set<Map.Entry<String, Map<String, Integer>>> methodsEntrySet = methodsRelations.entrySet();
        for(Map.Entry<String, Map<String, Integer>> entry: methodsEntrySet){

            Set<Map.Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for(Map.Entry<String, Integer> littleEntry: littleEntrySet) {

                String fileName = filesMethods.get(entry.getKey());
                Map<String, Integer> usageMap = filesRelations.get(fileName);
                String methodFileName = filesMethods.get(littleEntry.getKey());
                int valueToPut = littleEntry.getValue() + usageMap.get(methodFileName);
                if(!fileName.equals(methodFileName)){
                    usageMap.put(methodFileName, valueToPut);
                }
                filesRelations.put(fileName, usageMap);
            }
        }
        //Normalize filesWeights
        double max = (double)Collections.max(weights);
        double min = (double)Collections.min(weights);
        Set<Map.Entry<String, Integer>> weightsEntrySet = filesWeights.entrySet();
        for (Map.Entry<String, Integer> entry: weightsEntrySet){
            double val = ((double)entry.getValue() - min)/(max - min);
            val = (val*7 + 1);
            val *= 10;
            val = Math.round(val);
            val /= 10;
            filesWeights.put(entry.getKey(), (int)val);
        }

        return filesRelations;
    }

    private void checkDirectory(File file, Set<File> list) {
        if(file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(cFile -> {
                if(cFile.isDirectory()) {
                    checkDirectory(cFile,list);
                } else {
                    list.add(cFile);
                }
            });
        } else {
            list.add(file);
        }
    }

    private static class PackageNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, Integer> packageMap = new HashMap<>();

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n,arg);
            String methodQualifiedName = n.resolve().getQualifiedName();

            if(methodQualifiedName.contains("main_package")){
                String packageName = n.resolve().getPackageName();
                if(packageMap.containsKey(packageName)) {
                    packageMap.put(packageName,packageMap.get(packageName) + 1);
                } else {
                    packageMap.put(packageName,1);
                }
            }
        }

        private Map<String, Integer> getPackageMap() {
            return packageMap;
        }

        private int getMapSize() {
            return packageMap.size();
        }
    }

    private static class MethodNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, Integer> methodMap = new HashMap<>();

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n,arg);
            String methodQualifiedName = n.resolve().getQualifiedName();

            if(methodQualifiedName.contains("main_package")){
                String methodName = n.resolve().getName();
                if(methodMap.containsKey(methodName)) {
                    methodMap.put(methodName,methodMap.get(methodName) + 1);
                } else {
                    methodMap.put(methodName,1);
                }
            }
        }

        private Map<String, Integer> getMethodMap() {
            return methodMap;
        }

        private int getMapSize() {
            return methodMap.size();
        }
    }

    private void addInformation(Map<String, Map<String, Integer>> mainMap, Map<String, Integer> valueMap) {
        for(Map.Entry<String, Map<String, Integer>> entry : mainMap.entrySet()) {
            Map<String, Integer> mapValue = entry.getValue();

            for(Map.Entry<String,Integer> entryMap : mapValue.entrySet()) {
                String name = entryMap.getKey();
                if(valueMap.containsKey(name)) {
                    valueMap.put(name,valueMap.get(name)+ 1);
                } else {
                    valueMap.put(name,1);
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
