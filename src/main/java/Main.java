import java.io.IOException;
import java.util.*;

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

        } catch (IOException e) {
            e.printStackTrace();
        }
        JGraphXDraw.createGraphX(fileUsageMap, filesInformation);
    }

}
