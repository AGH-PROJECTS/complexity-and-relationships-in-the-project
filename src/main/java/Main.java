import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Main {
    public static void main(String[] args) {

        String rootPath = "C:\\src";            // scieżka, w której program szuka plików

        try {

            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(rootPath);
            Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();
            fileUsageMap = fileVisitor.searchFiles();
            printUsageMap(fileUsageMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void printUsageMap(Map<String, Map<String, Integer>> fileUsageMap) {

        Set<Entry<String, Map<String, Integer>>> entrySet = fileUsageMap.entrySet();
        for(Entry<String, Map<String, Integer>> entry: entrySet){
            System.out.println(entry.getKey());
            Set<Entry<String, Integer>> littleEntrySet = entry.getValue().entrySet();
            for(Entry<String, Integer> littleEntry: littleEntrySet){
                System.out.println("\t" + littleEntry.getKey() + ": " + littleEntry.getValue());
            }
        }

    }
}
