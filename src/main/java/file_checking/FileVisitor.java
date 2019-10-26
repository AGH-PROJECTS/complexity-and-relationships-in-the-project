package file_checking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileVisitor {
    private List<String> nameList = new LinkedList<>();
    private List<String> classList = new LinkedList<>();
    private Map<String, String> filesMap = new HashMap<>();
    //Klucz - nazwa pliku, wartość - ścieżka do pliku//
    private Map<String, Method[]> methodsMap = new HashMap<>();
    //Klucz - nazwa klasy, wartość - tablica metod


    public void findFiles(String rootPath) throws IOException {
        Files.walk(Paths.get(rootPath)).filter((path -> path.toFile().isFile())).forEach(this::createFileMap);
    }

    private void createFileMap(Path path) {
        if(path.toString().endsWith(".java")){
            String []parts = path.toString().split("/");
            String fileName = parts[parts.length - 1];
            String []parts2 = fileName.split("\\.");
            nameList.add(parts2[0]);
            filesMap.put(parts2[0], path.toString());

            List<String> parts3=new LinkedList<>();
            boolean flag=false;
            for (String p: parts){
                if(flag==true)
                    parts3.add(p);
                if(p.equals("java"))
                    flag=true;
            }
            String className="";
            for(String p:parts3)
                className+=p+".";
            className=className.substring(0,className.length()-6);
            classList.add(className);
        }
    }
    public void makeMethodsMap(){
        for(String n: classList){
            try{
                Class c=Class.forName(n);
                Method[] methods=c.getDeclaredMethods();
                methodsMap.put(n,methods);
            }
            catch(ClassNotFoundException e){e.printStackTrace();}
        }
    }

    public Map<String, Map<String, Integer>> searchFiles(){
        /*
         * Zwraca mapę której kluczami są nazwy plików,
         * a wartościami mapy z nazwami plików do których istnieją odwołania
         * oraz liczbą tych odwołań
         */
        Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();

        Set<Entry<String, String>> entrySet = filesMap.entrySet();
        for(Entry<String, String> entry: entrySet){
            Map<String,Integer> usageMap = new HashMap<>();
            for(String name: nameList){
                if(entry.getKey().equals(name)){
                    usageMap.put(name, -1);
                }else{
                    usageMap.put(name, 0);
                }
            }

            File file = new File(entry.getValue());
            try {
                Scanner scanner = new Scanner(file);
                String line;
                while(scanner.hasNextLine()){
                    line = scanner.nextLine();
                    if(line.contains("import")){
                        for(String name: nameList){
                            if(line.contains(name)){
                                usageMap.put(name, usageMap.get(name) + 1);
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            fileUsageMap.put(entry.getKey(), usageMap);
        }
        return fileUsageMap;
    }

    public List<String> getNameList() {
        return nameList;
    }
    public List<String> getClassList() {
        return classList;
    }

    public Map<String, Method[]> getMethodsMap() {
        return methodsMap;
    }
    public Map<String, String> getFilesMap() {
        return filesMap;
    }
}
