package model;

import java.util.HashMap;
import java.util.Map;

public class FileData {
    private String name;                                    //nazwa
    private long size;                                   //rozmiar
    private Map<String, Map<String, Integer>> dependencies; //mapa połączeń i ich wagi

    public FileData(String n,long i,Map<String,Map<String,Integer>> m){
        name=n;
        size=i;
        dependencies=new HashMap<String,Map<String,Integer>>(m);
    }
    public String getName(){
        return name;
    }
    public long getSize(){
        return size;
    }
    public Map<String,Map<String,Integer>> getDependencies(){
        return dependencies;
    }
}
