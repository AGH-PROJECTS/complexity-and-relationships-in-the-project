import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Main {
    public static final String ROOT_PATH = System.getProperty("user.dir")+"\\src\\main\\java";// scieżka, w której program szuka plików
    public static void main(String[] args) {
        JGraphXDraw graph1 = new JGraphXDraw();
        //graph1.createGraphX();
        FileInformator fileInformator = new FileInformator(ROOT_PATH);
        Map<String,Long> filesInformation = fileInformator.getInformation();
        try {
            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(ROOT_PATH);
            Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();
            fileUsageMap = fileVisitor.searchFiles();
            printUsageMap(fileUsageMap);

            /*List<FileData> fileDataList=new LinkedList<>();
            Set<Entry<String, Map<String, Integer>>> entrySet = fileUsageMap.entrySet();
            for(Entry<String, Map<String, Integer>> entry: entrySet){
                fileDataList.add(new FileData(entry.getKey(),filesInformation.get(entry.getKey()),entry.getValue());
            }
            */


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
