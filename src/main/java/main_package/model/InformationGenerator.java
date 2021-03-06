package main_package.model;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InformationGenerator {
    private String MAIN_PATH;
    private Set<File> classes;
    private List<String> classesName;
    private Set<String> packagesName;
    private Map<String, Integer> packagesWeights;
    private Map<String, Integer> methodsWeights;
    private Map<String, Integer> filesWeights;
    private Map<String, Map<String, Integer>> methodsRelations;

    public Map<String, Integer> getMethodsComplexity() {
        return methodsComplexity;
    }

    private Map<String, Integer> methodsComplexity;

    private Map<String, Map<String, AtomicInteger>> filesDependency;
    private Map<String, Map<String, AtomicInteger>> methodsDependency;
    private Map<String, Map<String, AtomicInteger>> packagesDependency;
    private Map<String, String> filesMethodsDependency;

    public InformationGenerator(String path) {
        this.MAIN_PATH = path;
        this.packagesWeights = new HashMap<>();
        this.methodsWeights = new HashMap<>();
        this.filesWeights = new HashMap<>();
        this.methodsComplexity = new HashMap<>();

        try {
            specifySymbolsSolver();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.classes = searchClasses();
        this.classesName = searchClassesName(classes);
        this.packagesName = searchPackagesName(classes);
        createRelationsMaps();
    }

    private void createRelationsMaps() {
        this.filesDependency = getFilesRelations();
        this.methodsDependency = getMethodsRelations();
        this.packagesDependency = getPackagesRelations();
        this.filesMethodsDependency = getMethodsFilesRelations();
        this.methodsComplexity = returnMethodsComplexity();
    }
    //konfiguracja typeSolvera
    private void specifySymbolsSolver() throws IOException {
        TypeSolver typeSolver = new JavaParserTypeSolver(MAIN_PATH);
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        JavaSymbolSolver javaSymbolSolver;
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(typeSolver);
        combinedTypeSolver.add(reflectionTypeSolver);
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
                        if(cu.getPackageDeclaration().isPresent()) {
                            String packageName = cu.getPackageDeclaration().get().getName().asString();
                            packagesName.add(packageName);
                        }
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

    List<Map<String, Map<String, AtomicInteger>>> packagesCalledMethodsRelationsMap = new ArrayList<>(); //lista map relacji miedzy paczka a metodami ktore sa wolane z innej metody
    List<Map<String, Map<String, AtomicInteger>>> packagesCallingMethodsRelationsMap = new ArrayList<>(); //lista map relacji miedzy paczkami a metodami ktore wolaja metody z innych paczek
    List<Map<String, Integer>> packagesCalledMethodsWagesMap = new ArrayList<>();
    List<Map<String, Integer>> packagesCallingMethodsWagesMap = new ArrayList<>();
    //relacje miedzy package
    private Map<String, Map<String, AtomicInteger>> getPackagesRelations() {
        Map<String, Map<String, AtomicInteger>> packagesRelationsMap = new HashMap<>(); //mapa relacji miedzy paczkami

        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                    Map<String, AtomicInteger> callingMethodsMap = new HashMap<>(); //mapa ilosci wystapien metod wołajacych
                    Map<String,Map<String,AtomicInteger>> packagesMethodsRelation = new HashMap<>(); //mapa relacji miedzy paczka a iloscia metod wolajacych

                    MethodInPackageNameSearching packageNameSearching = new MethodInPackageNameSearching(packagesName);
                    methodDeclaration.accept(packageNameSearching, null);

                    if (packageNameSearching.getMapSize() > 0) {
                        //String callingMethodName = methodDeclaration.getName().asString(); //nazwa metody ktora wola inne
                         String callingPackageLongName = methodDeclaration.resolve().getPackageName(); //nazwa paczki w ktorejjest metoda ktora wola inne
                        if(packagesRelationsMap.containsKey(packagesName)) {
                            packagesRelationsMap.computeIfPresent(callingPackageLongName,(k,v)-> {
                                v.putAll(packageNameSearching.getPackagesMap());
                                return v;
                            });
                        } else {
                            packagesRelationsMap.put(callingPackageLongName,packageNameSearching.getPackagesMap());
                        }
                        /*

                        callingMethodsMap.putIfAbsent(callingMethodName,new AtomicInteger(0));
                        callingMethodsMap.get(callingMethodName).incrementAndGet();
                        packagesMethodsRelation.put(callingPackageLongName, callingMethodsMap);

                        packagesRelationsMap.put(callingPackageLongName, packageNameSearching.getPackagesMap());
                        packagesCalledMethodsRelationsMap.add(packageNameSearching.getPackagesMethodsRelation());
                        packagesCallingMethodsRelationsMap.add(packagesMethodsRelation);*/
                    }

                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        packagesCalledMethodsRelationsMap.forEach(map->{
            Map<String, Integer> mapToSave = new HashMap<>();
            addSizeInformation(map, mapToSave);
            packagesCalledMethodsWagesMap.add(mapToSave);
        });

        packagesCallingMethodsRelationsMap.forEach(map->{
            Map<String, Integer> mapToSave = new HashMap<>();
            addSizeInformation(map, mapToSave);
            packagesCallingMethodsWagesMap.add(mapToSave);
        });

        addSizeInformation(packagesRelationsMap, packagesWeights);

        return packagesRelationsMap;
    }

    //relacje miedzy metodami
    private Map<String, Map<String, AtomicInteger>> getMethodsRelations() {
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
    public Map<String,Integer> returnMethodsComplexity() {
        Map<String,Integer> methodsComplexityMap=new HashMap<>();
        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(mcd -> {
                    Integer complexity = 1;
                    if (mcd.getBody().isPresent()) {
                        complexity += mcd.getBody().get().findAll(IfStmt.class).size();
                        Integer returns = mcd.getBody().get().findAll(ReturnStmt.class).size();
                        if (returns > 0) returns -= 1;
                        complexity += returns;
                        complexity += mcd.getBody().get().findAll(SwitchEntry.class).size();
                        complexity += mcd.getBody().get().findAll(ForStmt.class).size();
                        complexity += mcd.getBody().get().findAll(ForEachStmt.class).size();
                        complexity += mcd.getBody().get().findAll(WhileStmt.class).size();
                        complexity += mcd.getBody().get().findAll(DoStmt.class).size();
                        complexity += mcd.getBody().get().findAll(ConditionalExpr.class).size();
                        complexity += mcd.getBody().get().findAll(CatchClause.class).size();
                        complexity += mcd.getBody().get().findAll(ThrowStmt.class).size();
                        List<BinaryExpr> expressionList = mcd.getBody().get().findAll(BinaryExpr.class);
                        for (BinaryExpr expression : expressionList)
                            if (expression.getOperator() == BinaryExpr.Operator.AND ||
                                    expression.getOperator() == BinaryExpr.Operator.OR ||
                                    expression.getOperator() == BinaryExpr.Operator.BINARY_AND ||
                                    expression.getOperator() == BinaryExpr.Operator.BINARY_OR ||
                                    expression.getOperator() == BinaryExpr.Operator.XOR)
                                complexity++;

                    }
                    if (complexity == 0) complexity = 1;
                    //System.out.println(mcd.resolve().getName() + ' ' + complexity.toString());
                    methodsComplexityMap.put(mcd.resolve().getName(), complexity);
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        return methodsComplexityMap;
    }
    //relacje miedzy plikami
    private Map<String, Map<String, AtomicInteger>> getFilesRelations() {
        Map<String, Map<String, AtomicInteger>> filesRelationsMap = new HashMap<>();

        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                String fileName = file.getName();
                cu.findAll(MethodDeclaration.class).forEach(mcd -> {
                    FileNameSearching fileNameSearching = new FileNameSearching(packagesName);
                    mcd.accept(fileNameSearching, null);
                    if (fileNameSearching.getMapSize() > 0) {
                        if(filesRelationsMap.containsKey(fileName)) {
                            filesRelationsMap.computeIfPresent(fileName,(k,v)-> {
                                v.putAll(fileNameSearching.getMethodMap());
                                return v;
                            });
                        } else {
                            filesRelationsMap.put(fileName,fileNameSearching.getMethodMap());
                        }

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
    private Map<String, String> getMethodsFilesRelations() {// nazwa pliku i metody
        Map<String, String> methodsFilesRelationsMap = new HashMap<>(); //mapa relacji definicji metod miedzy plikami
        classes.forEach(file -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(mcd -> {
                    String methodName =  mcd.resolve().getName();
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
            String packageLongName = n.resolve().getPackageName(); //dluga nazwa paczki
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
            String packageLongName = n.resolve().getPackageName(); //cala nazwa paczki
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

            if(!valueMap.containsKey(entry.getKey())) {
                valueMap.put(entry.getKey(),1);
            }

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

    public Map<String, Map<String, AtomicInteger>> getFilesDependency() {
        return filesDependency;
    }

    public Map<String, Map<String, AtomicInteger>> getMethodsDependency() {
        return methodsDependency;
    }

    public Map<String, Map<String, AtomicInteger>> getPackagesDependency() {
        return packagesDependency;
    }

    public Map<String, String> getFilesMethodsDependency() {
        return filesMethodsDependency;
    }

    public List<Map<String, Map<String, AtomicInteger>>> getPackagesCalledMethodsRelationsMap() {
        return packagesCalledMethodsRelationsMap;
    }

    public List<Map<String, Map<String, AtomicInteger>>> getPackagesCallingMethodsRelationsMap() {
        return packagesCallingMethodsRelationsMap;
    }

    public List<Map<String, Integer>> getPackagesCalledMethodsWagesMap() {
        return packagesCalledMethodsWagesMap;
    }

    public List<Map<String, Integer>> getPackagesCallingMethodsWagesMap() {
        return packagesCallingMethodsWagesMap;
    }


    public void printRelations()
    {
        Set<Map.Entry<String,Map<String,AtomicInteger>>> entrySet = methodsDependency.entrySet();

        for(Map.Entry<String,Map<String,AtomicInteger>> entry: entrySet)
        {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    public void printWeigths()
    {
        Set<Map.Entry<String,Integer>>entrySet = methodsWeights.entrySet();

        for(Map.Entry<String,Integer> entry: entrySet)
        {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    public void partition(int numberOfPartitions)
    {
        ArrayList<String> methodsName = new ArrayList<String>();
        Set<Map.Entry<String,Map<String,AtomicInteger>>> entrySet = methodsDependency.entrySet();
        Set<Map.Entry<String,Integer>> entrySet2 = methodsWeights.entrySet();
        for(Map.Entry<String,Integer> entry: entrySet2)
        {
            methodsName.add(entry.getKey());
        }
        System.out.println("przerwa");
        for(String entry: methodsName)
        {
            System.out.println(entry);
        }


        // wyznaczenie maksymalnej ilości wierzchołków w klastrze
        int maxNumberOfVertexes = (methodsName.size()/numberOfPartitions) + 1;
        int[] colorAmount = new int[numberOfPartitions];
        ArrayList<Integer> vertexColors = new ArrayList<>();
        for(int i = 0; i < methodsName.size(); i++)
        {
            //ustawienie wszystkich kolorów na non-colour
            vertexColors.add(0);
        }
        ArrayList<Pair<String,Integer>> neighboursAmount = new ArrayList<>();
        for(Map.Entry<String,Map<String,AtomicInteger>> entry: entrySet)
        {
            Pair<String,Integer> tmpPair = new Pair<>(entry.getKey(),entry.getValue().size());
            neighboursAmount.add(tmpPair);
        }
        //dodanie wierzchołków bez sąsiadów
//        for(String entry: methodsName)
//        {
//            for(Pair<String, Integer> pair :neighboursAmount)
//                if(pair.a != )
//                {
//                    Pair<String,Integer> tmpPair = new Pair<>(entry,0);
//                    neighboursAmount.add(tmpPair);
//                }
//        }
        Collections.sort(neighboursAmount, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o1.b.compareTo(o2.b);
            }
        });
        System.out.println("przerwa");
        for(Pair<String,Integer> element : neighboursAmount)
        {
            System.out.println(element.a + " " + element.b);
        }



//        // wyznaczenie minimalnej ilości wierzchołków w każdym klastrze
//        int minimalNumberOfVertexes = methodsName.size()/numberOfPartitions;
//        Collections.shuffle(methodsName);
//        //kolorowanie na losowy kolor
//        ArrayList<Integer> vertexColours = new ArrayList<Integer>();
//        for(int i = 0; i < methodsName.size(); i++) {
//            vertexColours.add(i % numberOfPartitions);
//        }
//        boolean change = true;
//        int[][] numberOfConnections = new int[4][4];
//        // początkowa ilośc ustawioa na 0
//        for(int i = 0; i < numberOfPartitions; i++)
//        {
//            for(int j = 0; j < numberOfPartitions; j++) numberOfConnections[i][j] = 0;
//        }
//
//        while(change)
//        {
//            change = false;
//            int colour1, colour2;
//            for(Map.Entry<String,Map<String,AtomicInteger>> entry: entrySet)
//            {
//                colour1 = vertexColours.get(methodsName.indexOf(entry.getKey()));
//
//            }
//
//        }


    }
}
