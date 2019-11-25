package main_package.export;

public class Diagram extends Entry{
    int id;
    String name;
    String type;
    String delete;
    public Diagram(int id,String name,String type){
        this.id=id;
        this.name=name;
        this.type=type;
        this.delete="No";
    }
    public String write(){
        return("Diagram,ID,Name,Type,Delete?\n" +
                ','+String.valueOf(id)+','+name+','+type+",No\n\n");
    }
    public String getName(){
        return name;
    }
}
