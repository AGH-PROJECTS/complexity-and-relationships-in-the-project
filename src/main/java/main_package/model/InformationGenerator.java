package main_package.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.contexts.ClassOrInterfaceDeclarationContext;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.module.ResolvedModule;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static main_package.tools.Maintenance.MAIN_PATH;

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
        this.classes = searchClasses();
        this.classesName = searchClassesName(classes);
        this.packagesName = searchPackagesName(classes);

        /*File file = new File("C:\\Users\\Comarch\\Downloads\\Evolution-Generator-master\\Evolution-Generator-master\\src\\mainPackage");
        SourceRoot sourceRoot = new SourceRoot(file.toPath());
        List<ParseResult<CompilationUnit>> compilationUnits = null;
        try {
            compilationUnits = sourceRoot.tryToParse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        compilationUnits.forEach(result->{
            CompilationUnit cu = result.getResult().get();
            if(cu.getPackageDeclaration().isPresent()) {
                System.out.println(cu.getPackageDeclaration().get().getName().asString());
                cu.findAll(MethodDeclaration.class).forEach(m-> );
                System.out.println();
            }
        });*/

        getMethodsRelations();
        //getPackagesRelations();
        //getFilesRelations();
        //getMethodsFilesRelations();
    }

    public void test() {
        System.out.println("Test");
    }

    public void test2() {
        System.out.println("Test");
    }
    public void test3() {
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
    private Set<File> searchClasses() {
        Set<File> searchedClasses = new HashSet<>();
        File mainDir = new File(MAIN_PATH); // tutaj szukam plikow naszego projektu
        Arrays.stream(mainDir.listFiles()).forEach(file -> {
            checkDirectory(file, searchedClasses);
        }); //tworze liste klas
        return searchedClasses;
    }

    private List<String> searchClassesName(Set<File> classesToSearch) {
        List<String> searchedClassesName = new ArrayList<>();
        classesToSearch.forEach(file -> searchedClassesName.add(file.getName().substring(0, file.getName().lastIndexOf(".java"))));
        //wybieram z listy klas tylko nazwy bez .java
        return searchedClassesName;
    }

    private Set<String> searchPackagesName(Set<File> classesToSearch) {
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
                    if(cFile.getName().contains(".java"))
                    fileSet.add(cFile);
                }
            });
        } else {
            if(file.getName().contains(".java"))
            fileSet.add(file);
        }
    }

    //relacje miedzy package
    public Map<String, Map<String, AtomicInteger>> getPackagesRelations() {
        Map<String, Map<String, AtomicInteger>> packagesRelationsMap = new HashMap<>(); //mapa relacji miedzy paczkami
        List<Map<String, Map<String, AtomicInteger>>> packagesCalledMethodsRelationsMap = new ArrayList<>(); //lista map relacji miedzy paczka a metodami ktore sa wolane z innej metody
        List<Map<String, Map<String, AtomicInteger>>> packagesCallingMethodsRelationsMap = new ArrayList<>(); //lista map relacji miedzy paczkami a metodami ktore wolaja metody z innych paczek
        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                    Map<String, AtomicInteger> callingMethodsMap = new HashMap<>(); //mapa ilosci wystapien metod wołajacych
                    Map<String,Map<String,AtomicInteger>> packagesMethodsRelation = new HashMap<>(); //mapa relacji miedzy paczka a iloscia metod wolajacych

                    MethodInPackageNameSearching packageNameSearching = new MethodInPackageNameSearching(packagesName);
                    methodDeclaration.accept(packageNameSearching, null);

                    if (packageNameSearching.getMapSize() > 0) {
                        String callingMethodName = methodDeclaration.getName().asString(); //nazwa metody ktora wola inne
                        String callingPackageLongName = methodDeclaration.getName().asString(); //nazwa paczki w ktorejjest metoda ktora wola inne

                        callingMethodsMap.putIfAbsent(callingMethodName,new AtomicInteger(0));
                        callingMethodsMap.get(callingMethodName).incrementAndGet();
                        packagesMethodsRelation.put(callingPackageLongName, callingMethodsMap);

                        packagesRelationsMap.put(callingPackageLongName, packageNameSearching.getPackagesMap());
                        packagesCalledMethodsRelationsMap.add(packageNameSearching.getPackagesMethodsRelation());
                        packagesCallingMethodsRelationsMap.add(packagesMethodsRelation);
                    }

                });
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
                        methodsRelationsMap.put(mcd.getName().asString(), methodNameSearching.getMethodMap());
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

    //relacje miedzy metodami a plikami
    public Map<String, String> getMethodsFilesRelations() {// nazwa pliku i metody
        Map<String, String> methodsFilesRelationsMap = new HashMap<>(); //mapa relacji definicji metod miedzy plikami
        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(mcd -> {
                    String methodName =  mcd.getName().asString();
                    methodsFilesRelationsMap.put(methodName, file.getName());
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        return methodsFilesRelationsMap;
    }

    private static class MethodInPackageNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, AtomicInteger> packagesMap = new HashMap<String, AtomicInteger>(); // mapa ilosci wystapien danej paczki w metodzie
        private Set<String > packagesName;
        Map<String,Map<String,AtomicInteger>> packagesMethodsRelation = new HashMap<>(); //mapa relacji miedzy paczka a metoda i ilosc jej wystapien
        Map<String, AtomicInteger> methodsCallingMap = new HashMap<>(); //mapa ilosci wystapien danej metody

        MethodInPackageNameSearching(Set<String > packagesName ) {
            this.packagesName = packagesName;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            CompilationUnit cu = n.findCompilationUnit().get();
            //System.out.println(  n.findCompilationUnit().get().getPackageDeclaration().get().getName().asString() + " Paczka");
            String packageLongName = n.findCompilationUnit().get().getPackageDeclaration().get().getNameAsString(); //dluga nazwa paczki
            String methodName = n.getNameAsString(); //nazwa metody
            packagesName.stream()
                    .filter(pN -> pN.equals(packageLongName))
                    .forEach(pN ->
                    {
                        //informace o ilosci wystapien metody
                        methodsCallingMap.putIfAbsent(methodName,new AtomicInteger(0));
                        methodsCallingMap.get(methodName).incrementAndGet();

                        //relacja paczka -> metody
                        packagesMethodsRelation.put(packageLongName,methodsCallingMap);

                        //informacja o ilosci wystapien paczki
                        packagesMap.putIfAbsent(packageLongName,new AtomicInteger(0));
                        packagesMap.get(packageLongName).incrementAndGet();
                    }
            );

        }

        private Map<String, AtomicInteger> getPackagesMap() {
            return packagesMap;
        }

        public Map<String, Map<String, AtomicInteger>> getPackagesMethodsRelation() {
            return packagesMethodsRelation;
        }

        private int getMapSize() {
            return packagesMap.size();
        }
    }

    private static class MethodNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, AtomicInteger> methodMap = new HashMap<String, AtomicInteger>();
        private Set<String > packagesName; //paczki w ktorych mamy szukac
        MethodNameSearching(Set<String > packagesName ) {
            this.packagesName = packagesName;
        }
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            String packageLongName = n.findCompilationUnit().get().getPackageDeclaration().get().getNameAsString(); //cala nazwa paczki
            String methodName = n.getNameAsString(); //nazwa metody
            packagesName.stream()
                    .filter(mN -> mN.equals(packageLongName))
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

    private static class FileNameSearching extends VoidVisitorAdapter<Void> {
        private Map<String, AtomicInteger> methodMap = new HashMap<String, AtomicInteger>();
        private Set<String > packagesName;

        FileNameSearching(Set<String > packagesName ) {
            this.packagesName = packagesName;
        }
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            String packagePath = n.findCompilationUnit().get().getPackageDeclaration().get().getNameAsString();
            System.out.println();
            String fileName = n.getParentNode().get().findCompilationUnit().get().getPrimaryType().get().getNameAsString() + ".java";
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
