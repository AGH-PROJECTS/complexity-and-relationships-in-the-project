import java.util.HashMap;
import java.util.Map;

public class FileData {
    private String name;                                    //nazwa
    private Integer size;                                   //rozmiar
    private Map<String, Map<String, Integer>> dependencies; //mapa połączeń i ich wagi

    public FileData(String n,Integer i,Map<String,Map<String,Integer>> m){
        name=n;
        size=i;
        dependencies=new HashMap<String,Map<String,Integer>>(m);
    }
    public String getName(){
        return name;
    }
    public Integer getSize(){
        return size;
    }
    public Map<String,Map<String,Integer>> getDependencies(){
        return dependencies;
    }
}
