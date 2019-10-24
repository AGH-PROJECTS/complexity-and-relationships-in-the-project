import file_checking.FileDirections;
import file_checking.FileInformator;
import file_checking.FileVisitor;
import model.JGraphXDraw;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final String ROOT_PATH = System.getProperty("user.dir")+"\\src\\main\\java";// scieżka, w której program szuka plików
    private static Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();
    public static void main(String[] args) {
        FileInformator fileInformator = new FileInformator(ROOT_PATH);
        Map<String,Long> filesInformation = fileInformator.getInformation();
        try {
            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(ROOT_PATH);
            fileUsageMap = fileVisitor.searchFiles();

            FileDirections fileDirections = new FileDirections(fileVisitor.getFilesMap(),fileVisitor.getNameList());

            for(String name: fileDirections.getNameList())
            {
                System.out.println(name);
            }

            //fileDirections.showMap();

            fileDirections.findConnections();

            fileDirections.showConnections();


        } catch (IOException e) {
            e.printStackTrace();
        }
        JGraphXDraw.createGraphX(fileUsageMap, filesInformation);
    }
}