package main_package.export;

public class Dependency extends Entry{
    int id;
    int model;
    int name;
    int supplier;
    int client;
    int parent;
    public Dependency(int id,int model,int name,int supplier,int client,int parent){
        this.id=id;
        this.model=model;
        this.name=name;
        this.supplier=supplier;
        this.client=client;
        this.parent=parent;
    }
    public String write(){
        return("Diagram,ID,Model ID,Name,,Supplier,Client,Visibility,Parent ID,Parent Name,Delete?\n" +
                ','+String.valueOf(id)+','+String.valueOf(model)+','+String.valueOf(name)+','+String.valueOf(supplier)+','+String.valueOf(client)+",Unspecified,"+String.valueOf(parent)+",Dependency,No\n\n");
    }
    public String getName(){
        return String.valueOf(name);
    }
}
