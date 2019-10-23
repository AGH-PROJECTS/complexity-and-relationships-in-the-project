package file_checking;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileVisitor {
    private List<String> nameList = new LinkedList<>();
    private Map<String, Method[]> methodsMap = new HashMap<>();
    //Klucz - nazwa klasy, wartość - tablica metod

    public void findClasses(String rootPath) throws IOException {
        Files.walk(Paths.get(rootPath)).filter((path -> path.toFile().isFile())).forEach(this::createClassList);
        makeMethodsMap();
    }

    private void createClassList(Path path) {
        if(path.toString().endsWith(".java")){
            String []parts = path.toString().split("/");
            List<String> parts2=new LinkedList<>();
            boolean flag=false;
            for (String p: parts){
                if(flag==true)
                    parts2.add(p);
                if(p.equals("java"))
                    flag=true;
            }
            String className="";
            for(String p:parts2)
                className+=p+".";
            className=className.substring(0,className.length()-6);
            nameList.add(className);
        }
    }
    private void makeMethodsMap(){
        for(String n: nameList){
            try{
                Class c=Class.forName(n);
                Method[] methods=c.getDeclaredMethods();
                methodsMap.put(n,methods);
            }
            catch(ClassNotFoundException e){e.printStackTrace();}
        }
    }
}
