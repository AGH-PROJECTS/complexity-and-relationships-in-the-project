package model;

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

public class InformationGenerator {
    private static final String MAIN_PATH = "src/main/java";
    private CombinedTypeSolver combinedTypeSolver;
    private TypeSolver typeSolver;
    private TypeSolver reflectionTypeSolver;
    private JavaSymbolSolver javaSymbolSolver;
    private File mainDir;
    private Set<File> classes;
    private  List<String > classesName;

    public InformationGenerator() {
        this.combinedTypeSolver = new CombinedTypeSolver();
        this.typeSolver = new JavaParserTypeSolver(MAIN_PATH);
        reflectionTypeSolver = new ReflectionTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(typeSolver);
        try {
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\com\\github\\javaparser\\javaparser-core\\3.15.5\\javaparser-core-3.15.5.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\com\\github\\javaparser\\javaparser-symbol-solver-core\\3.15.5\\javaparser-symbol-solver-core-3.15.5.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\com\\github\\javaparser\\javaparser-symbol-solver-logic\\3.15.5\\javaparser-symbol-solver-logic-3.15.5.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\com\\github\\javaparser\\javaparser-symbol-solver-model\\3.15.5\\javaparser-symbol-solver-model-3.15.5.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\org\\jgrapht\\jgrapht-core\\1.3.2-SNAPSHOT\\jgrapht-core-1.3.2-20191110.130638-30.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\org\\jgrapht\\jgrapht-ext\\1.3.2-SNAPSHOT\\jgrapht-ext-1.3.2-20191110.130651-31.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\org\\jgrapht\\jgrapht-io\\1.3.2-SNAPSHOT\\jgrapht-io-1.3.2-20191110.130647-31.jar"));
            combinedTypeSolver.add(new JarTypeSolver("C:\\Users\\dawid\\.m2\\repository\\com\\github\\vlsi\\mxgraph\\jgraphx\\3.9.8.1\\jgraphx-3.9.8.1.jar"));
        } catch (IOException e) {
            e.printStackTrace();
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

    public Map<String, Map<String, Integer>> getInformationPackages() {
        Map<String, Map<String, Integer>> packagesInformation = new HashMap<>();

        classes.forEach(file -> {
            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);
                CompilationUnit finalCu = cu;
                cu.findAll(MethodDeclaration.class).stream().forEach(mcd-> {
                    PackageNameSearching packageNameSearching = new PackageNameSearching();
                    classesName.forEach(name -> {
                        packageNameSearching.setName(name);
                        mcd.accept(packageNameSearching, null);
                    });
                    if(packageNameSearching.getMapSize() > 0) {
                        if(finalCu.getPackageDeclaration().isEmpty()) {
                            packagesInformation.put("main",packageNameSearching.getPackageMap());
                        } else {
                            packagesInformation.put(finalCu.getPackageDeclaration().get().getName().asString(),packageNameSearching.getPackageMap());
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        return packagesInformation;
    }

    public Map<String, Map<String, Integer>> getInformationMethods() {
        Map<String, Map<String, Integer>> methodsInformation = new HashMap<>();

        classes.forEach(file -> {
            CompilationUnit cu = null;
            try {
                cu = StaticJavaParser.parse(file);

                cu.findAll(MethodDeclaration.class).stream().forEach(mcd-> {
                   MethodNameSearching methodNameSearching = new MethodNameSearching();
                    classesName.forEach(name -> {
                        methodNameSearching.setName(name);
                        mcd.accept(methodNameSearching, null);
                    });
                    if(methodNameSearching.getMapSize() > 0) {
                        methodsInformation.put(mcd.resolve().getName(),methodNameSearching.getMethodMap());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });


        return methodsInformation;
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
        private String name = null;

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n,arg);
            String methodClass = n.resolve().getClassName();

            if(methodClass.equals(name)){
                String packageName = n.resolve().getPackageName();
                if(packageMap.containsKey(packageName)) {
                    packageMap.put(packageName,packageMap.get(packageName).intValue() + 1);
                } else {
                    packageMap.put(packageName,1);
                }

            }
        }

        public Map<String, Integer> getPackageMap() {
            return packageMap;
        }

        public int getMapSize() {
            return packageMap.size();
        }
    }

    private static class MethodNameSearching extends VoidVisitorAdapter<Void> {
        private String name = null;
        private Map<String, Integer> methodMap = new HashMap<>();
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n,arg);
            String methodClass = n.resolve().getClassName();

            if(methodClass.equals(name)){
                String methodName = n.resolve().getName();
                if(methodMap.containsKey(methodName)) {
                    methodMap.put(methodName,methodMap.get(methodName).intValue() + 1);
                } else {
                    methodMap.put(methodName,1);
                }

            }
        }

        public Map<String, Integer> getMethodMap() {
            return methodMap;
        }

        public int getMapSize() {
            return methodMap.size();
        }
    }
}
