import file_checking.FileDirections;
import file_checking.FileInformator;
import file_checking.FileVisitor;
import model.JGraphXDraw;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final String ROOT_PATH = System.getProperty("user.dir")+"/src/main/java";// scieżka, w której program szuka plików
    private static Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();
    public static void main(String[] args) {
        FileInformator fileInformator = new FileInformator(ROOT_PATH);
        Map<String,Long> filesInformation = fileInformator.getInformation();
        try {
            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(ROOT_PATH);
            fileVisitor.makeMethodsMap();
            fileUsageMap = fileVisitor.searchFiles();

            FileDirections fileDirections = new FileDirections(fileVisitor.getFilesMap(),fileVisitor.getNameList());

            System.out.println("ClassAndMethodsList");
            
            //Poniżej test: wypisanie wszytkich nazw klas z metodami
            for(String name: fileVisitor.getClassList())
            {
                System.out.println("\t"+name);
                for(Method m:fileVisitor.getMethodsMap().get(name))
                    System.out.println("\t\t"+m);
                System.out.println("\n");
            }
            
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
