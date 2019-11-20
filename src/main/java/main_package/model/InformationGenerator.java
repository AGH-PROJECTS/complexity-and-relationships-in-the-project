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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class InformationGenerator {
    private static final String MAIN_PATH = "src/main/java";
    private CombinedTypeSolver combinedTypeSolver;
    private TypeSolver typeSolver;
    private TypeSolver reflectionTypeSolver;
    private JavaSymbolSolver javaSymbolSolver;
    private File mainDir;
    private Set<File> classes;
    private List<String > classesName;
    private Map<String, Integer> wagesPackagesMap;
    private Map<String, Integer> wagesMethodsMap;

    public InformationGenerator() throws IOException {
        this.wagesPackagesMap = new HashMap<>();
        this.wagesMethodsMap = new HashMap<>();
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

    public Map<String, Map<String, Integer>> getAllInformationPackages() {
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
        addInformation(allPackagesInformationMap, wagesPackagesMap);
        wagesPackagesMap.put("main_package",1);
        return allPackagesInformationMap;
    }

    public Map<String, Map<String, Integer>> getAllInformationMethods() {
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
        addInformation(allMethodsInformationMap, wagesMethodsMap);
        wagesMethodsMap.put("main",1);
        return allMethodsInformationMap;
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

    public Map<String, Integer> getWeightsOfPackages() {
        return wagesPackagesMap;
    }

    public Map<String, Integer> getWeightsOfMethods() {
        return wagesMethodsMap;
    }
}
