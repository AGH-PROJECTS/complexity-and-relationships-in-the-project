package main_package.export;

public class Package extends Entry{
    int id;
    int model;
    String name;
    public Package(int id,int model,String name){
        this.id=id;
        this.model=model;
        this.name=name;
    }
    public String write(){
        return("Diagram,ID,Model ID,Name,Visibility,Abstract,Leaf,Root,Delete?\n" +
                ','+String.valueOf(id)+','+String.valueOf(model)+','+name+",public,No,No,No,No\n\n");
    }
    public String getName(){
        return name;
    }
}
