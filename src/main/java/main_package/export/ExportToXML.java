package main_package.export;


import com.jamesmurty.utils.XMLBuilder2;

import javax.xml.transform.OutputKeys;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class ExportToXML {
    private XMLBuilder2 exportedXML;
    private Properties xmlFileProperties;
    private Integer graphElementsIds = 1;

    public ExportToXML(List<Map<String, Map<String, AtomicInteger>>> dataRelationsList, List<Map<String, Integer>> dataWeightsList, String name) {
        this.setFileProperties();
        this.setXMLProjectProperties(name);
        for(int k=0; k<dataRelationsList.size(); k++){
            this.addGraphElementsToExportedXML(dataRelationsList.get(k), dataWeightsList.get(k));
        }
        this.exportToFile(name);
    }

    public ExportToXML(Map<String, String> relations, String name) {
        this.setFileProperties();
        this.setXMLProjectProperties(name);
        this.addGraphElementsToExportedXML(relations);
        this.exportToFile(name);
    }

    private void setFileProperties(){
        xmlFileProperties = new Properties();
        xmlFileProperties.put(OutputKeys.METHOD, "xml");
        xmlFileProperties.put(OutputKeys.INDENT, "yes");
        xmlFileProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
    }

    private void setXMLProjectProperties(String name){
        exportedXML = XMLBuilder2.create("Project")
                .a("DocumentationType", "html")
                .a("ExporterVersion", "12.2")
                .a("Name", name)
                .a("UmlVersion", "2.x")
                .a("Xml_structure", "simple")
                .e("Models");
    }

    private void exportToFile(String name) {
        try {
            PrintWriter writer = new PrintWriter((name+".xml"));
            exportedXML.toWriter(writer, xmlFileProperties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addGraphElementsToExportedXML(Map<String, Map<String, AtomicInteger>>relations, Map<String, Integer> weights){

        Map<String, Integer> graphElementsWithIds = new HashMap();

        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            graphElementsWithIds.put(entry.getKey(), graphElementsIds);
            graphElementsIds++;
        }

        for (Map.Entry<String, Integer> entry: weights.entrySet()) {
            exportedXML.e("Class")
                    .a("Id", Integer.toString(graphElementsWithIds.get(entry.getKey())))
                    .a("Name", entry.getKey())
                    .up();
        }

        relations.forEach((k, v) -> {
            for (Map.Entry<String, AtomicInteger> entry : v.entrySet()) {
                Integer tmp = graphElementsWithIds.get(entry.getKey());
                System.out.println(tmp+ " "+entry.getValue());
                if(tmp==null)
                    continue;
                exportedXML.e("Usage")
                        .a("From", Integer.toString(graphElementsWithIds.get(k)))
                        .a("Id", Integer.toString(graphElementsIds))
                        .a("To", Integer.toString(tmp))
                        .up();
                graphElementsIds++;
            }
        });
    }
    private void addGraphElementsToExportedXML(Map<String, String>relations){

        Map<String, Integer> graphElementsWithIds = new HashMap();

        for (String entry : relations.values()) {
            if (graphElementsWithIds.containsKey(entry))
                continue;
            graphElementsWithIds.put(entry, graphElementsIds);
            graphElementsIds++;
        }

        for (String entry: relations.keySet()){
            graphElementsWithIds.put(entry, graphElementsIds);
            graphElementsIds++;
        }

        for (Map.Entry<String, Integer> entry: graphElementsWithIds.entrySet()) {
            exportedXML.e("Class")
                    .a("Id", Integer.toString(entry.getValue()))
                    .a("Name", entry.getKey())
                    .up();
        }

        for (Map.Entry<String, String> entry: relations.entrySet()){
            exportedXML.e("Usage")
                    .a("From", Integer.toString(graphElementsWithIds.get(entry.getValue())))
                    .a("Id", Integer.toString(graphElementsIds))
                    .a("To", Integer.toString(graphElementsWithIds.get(entry.getKey())))
                    .up();
            graphElementsIds++;
        }

    }
}


