package main_package;

import java.io.IOException;
import java.util.*;
import main_package.file_checking.FileInformator;
import main_package.file_checking.FileVisitor;
import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;

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
            fileUsageMap = fileVisitor.searchFiles();
            InformationGenerator informationGenerator = new InformationGenerator();
            methods =  informationGenerator.getInformationMethods();
            packages = informationGenerator.getInformationPackages();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JGraphXDraw newGraph = new JGraphXDraw();
        newGraph.createGraphX(packages, filesInformation);
    }

}
