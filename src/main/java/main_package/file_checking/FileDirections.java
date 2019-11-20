package main_package.file_checking;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class FileDirections {
    private Map<String,String> fileMap = new HashMap<>(); // Klucz - nazwa, wartość - ścieżka
    private List<String> nameList = new LinkedList<>();
    private static int listSize;
    private Map<String,Map<Integer,String>> connections = new HashMap<>(); // Klucz - nazwa pliku źródłowego, wartość - klucz - nr połączenia w grafie, wartość - nazwa pliku z którym ma połączenie

    public FileDirections(Map<String,String> map,List<String> name)
    {
        fileMap = map;
        nameList = name;
    }

    public void findConnections()
    {
        Set<Map.Entry<String,String>> entrySet = fileMap.entrySet();

        for(Map.Entry<String,String> entry: entrySet)
        {
            int i = 0;
            Map<Integer,String> connection = new HashMap<>();
            //System.out.println(entry.getKey() + "\t" + entry.getValue());
            File file = new File(entry.getValue());
            try {
                Scanner scanner = new Scanner(file);
                String line;
                while (scanner.hasNextLine())
                {
                    line = scanner.nextLine();
                    if(line.contains("import"))
                    {
                        for(String name: nameList)
                        {

                            if(line.contains(name))
                            {
                                //System.out.println(line + "\t" + name);
                                connection.put(i,name);
                                i++;
                                connections.put(entry.getKey(),connection);
                            }

                        }
                    }
                }

            }catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }


        }
    }

    public void showMap()
    {
        Set<Map.Entry<String, String>> entrySet = fileMap.entrySet();
        for(Map.Entry<String,String> entry: entrySet)
        {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }


    public void showConnections(){
        Set<Map.Entry<String,Map<Integer,String>>> entrySet1 = connections.entrySet();
        for(Map.Entry<String,Map<Integer,String>> entry: entrySet1)
        {
            System.out.println(entry.getKey() + " --------- >" + entry.getValue());
        }
    }


    public List<String> getNameList() {
        return nameList;
    }
}