
import java.io.IOException;
import java.util.Map;

public class Main {
    public static final String ROOT_PATH = System.getProperty("user.dir");// scieżka, w której program szuka plików
    public static void main(String[] args) {
        Map<String, Map<String, Integer>> fileUsageMap = null;
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
