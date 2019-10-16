import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private Map<String, String> filesMap = new HashMap<>();
    //Klucz - nazwa pliku, wartość - ścieżka do pliku//

    public void findFiles(String rootPath) throws IOException {
        Files.walk(Paths.get(rootPath)).filter((path -> path.toFile().isFile())).forEach(this::createFileMap);
    }

    private void createFileMap(Path path) {
        if(path.toString().endsWith(".java")){
            String []parts = path.toString().split("\\\\");
            String fileName = parts[parts.length - 1];
            String []parts2 = fileName.split("\\.");
            nameList.add(parts2[0]);
            filesMap.put(parts2[0], path.toString());
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
                    for(String name: nameList){
                        if(line.contains(name)){
                            usageMap.put(name, usageMap.get(name) + 1);
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


}
