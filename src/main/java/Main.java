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
    public static void main(String[] args) {
        FileInformator fileInformator = new FileInformator(ROOT_PATH);
        Map<String,Long> filesInformation = fileInformator.getInformation();
        try {
            FileVisitor fileVisitor = new FileVisitor();
            fileVisitor.findFiles(ROOT_PATH);
            fileVisitor.findFiles(ROOT_PATH);
            fileUsageMap = fileVisitor.searchFiles();
            InformationGenerator informationGenerator = new InformationGenerator();
            InformationGenerator informationGenerator1 = new InformationGenerator();
            Map<String, Map<String, Integer>>  methods =  informationGenerator.getInformationMethods();
            Map<String, Map<String, Integer>> packages = informationGenerator.getInformationPackages();
            //informationGenerator1.getInformationMethods();
            //int [][]graph = new int[fileVisitor.getNameList().size()][fileVisitor.getNameList().size()];

            //graph = graph(fileVisitor.getNameList());
           // printList(graph,fileVisitor.getNameList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        //JGraphXDraw.createGraphX(fileUsageMap, filesInformation);
    }

    private static int [][]graph (List<String> nameList)
    {
        int listsize = nameList.size();
        int connections[][] = new int[listsize][listsize]; // tworzymy tablice ktora okresili nam kierunki polaczen
        for(int g = 0; g <listsize; g++)
        {
            for(int h = 0; h < listsize; h++)
            {
                connections[g][h] = -1;
            }
        }
        String rootPath = "C:\\Users\\KuroiAkuma\\Desktop\\projectIO-faza_1\\projectIO-faza_1\\src\\main\\java"; //moja sciezka dla folderu projektu
        String filepath;
        int index;

        for(int i = 0; i < listsize; i++)
        {
            index = 0;
            filepath = rootPath + "\\" + nameList.get(i) + ".java"; // ustalamy sciezke pliku w ktorym sprawdzamy polaczenia
            //System.out.println(filepath + "\n");
            File file = new File(filepath);
            try {
                Scanner scanner = new Scanner(file);
                String line;
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    for (int j = 0; j < listsize; j++) {
                        //System.out.println(j);
                        if (line.contains(nameList.get(j)) && i != j) {
                            //System.out.println(i + "\t" + j);
                            connections[i][j] = j;
                            index++;
                            break;
                        }
                        /*else
                        {
                            connections[i][j] = -1;
                            //index++;
                        }*/
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connections;

    }

    private static void printList(int [][] graph, List<String> nameList)
    {
        /*
        reprezentacja kierunkowości połączeń w grafie
        element graph[i][j] jest potrzebny elementowi i
        i -----> graph[i][j]
        */

        for(int i = 0; i < nameList.size(); i++)
        {
            for(int j = 0; j < nameList.size(); j++)
            {
                if(graph[i][j] >= 0)
                    //System.out.println(i + " " + j + " " +  graph[i][j]);
                    System.out.println(nameList.get(i) + " -----> " + nameList.get(j));
            }

        }

    }

}
