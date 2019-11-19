import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import file_checking.FileInformator;
import file_checking.FileVisitor;
import model.InformationGenerator;
import model.JGraphXDraw;

public class Main {
    public static final String ROOT_PATH = System.getProperty("user.dir")+"\\src\\main\\java";// scieżka, w której program szuka plików
    private static Map<String, Map<String, Integer>> fileUsageMap = new HashMap<>();
    private static Map<String, Map<String, Integer>>  methods;
    private static Map<String, Map<String, Integer>> packages;
    public static void main(String[] args) {
        FileInformator fileInformator = new FileInformator(ROOT_PATH);
        Map<String,Long> filesInformation = fileInformator.getInformation();
        try {
            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(ROOT_PATH);
            fileVisitor.findFiles(ROOT_PATH);
            fileUsageMap = fileVisitor.searchFiles();
            InformationGenerator informationGenerator = new InformationGenerator();
           // InformationGenerator informationGenerator1 = new InformationGenerator();
            methods =  informationGenerator.getInformationMethods();
            packages = informationGenerator.getInformationPackages();
            //informationGenerator1.getInformationMethods();
            //int [][]graph = new int[fileVisitor.getNameList().size()][fileVisitor.getNameList().size()];

            //graph = graph(fileVisitor.getNameList());
           // printList(graph,fileVisitor.getNameList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(packages);
        JGraphXDraw myGraph = new JGraphXDraw();
        myGraph.createGraphX(packages, filesInformation);
        //JGraphXDraw.createGraphX(fileUsageMap, filesInformation);
        //JGraphXDraw.createGraphX(fileUsageMap, filesInformation);
    }
}
