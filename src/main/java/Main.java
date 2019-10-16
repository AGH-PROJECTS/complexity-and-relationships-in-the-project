import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Main {
    public static final String ROOT_PATH = System.getProperty("user.dir");// scieżka, w której program szuka plików
    private static Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();
    public static void main(String[] args) {
        try {
            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(ROOT_PATH);
            fileUsageMap = fileVisitor.searchFiles();


        } catch (IOException e) {
            e.printStackTrace();
        }
        JGraphXDraw.createGraphX(fileUsageMap);

    }

}
