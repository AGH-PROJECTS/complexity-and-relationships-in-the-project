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

    public void exportGraphToXML(List<Map<String, Map<String, AtomicInteger>>> dataRelationsList, List<Map<String, Integer>> dataWeightsList, String name) {
        if (dataRelationsList == null || dataWeightsList == null || name == null) {
            throw new NullPointerException();
        }
        this.setFileProperties();
        this.setXMLProjectProperties(name);
        for(int k=0; k<dataRelationsList.size(); k++){
            this.addGraphElementsToExportedXML(dataRelationsList.get(k), dataWeightsList.get(k));
        }
        this.exportToFile(name);
    }

    public void exportGraphToXML(Map<String, String> relations, String name) {
        if (relations == null || name == null) {
            throw new NullPointerException();
        }
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

        for (Map.Entry<String, Map<String, AtomicInteger>> relation: relations.entrySet()) {
            for (Map.Entry<String, AtomicInteger> entry : relation.getValue().entrySet()) {
                if (graphElementsWithIds.containsKey(entry.getKey()))
                    continue;
                graphElementsWithIds.put(entry.getKey(), graphElementsIds);
                graphElementsIds++;
            }
            if (graphElementsWithIds.containsKey(relation.getKey()))
                continue;
            graphElementsWithIds.put(relation.getKey(), graphElementsIds);
            graphElementsIds++;
        }

        for (Map.Entry<String, Integer> entry: graphElementsWithIds.entrySet()) {
            exportedXML.e("Class")
                    .a("Id", Integer.toString(entry.getValue()))
                    .a("Name", entry.getKey())
                    .up();
        }

        relations.forEach((k, v) -> {
            for (Map.Entry<String, AtomicInteger> entry : v.entrySet()) {
                exportedXML.e("Usage")
                        .a("From", Integer.toString(graphElementsWithIds.get(k)))
                        .a("Id", Integer.toString(graphElementsIds))
                        .a("To", Integer.toString(graphElementsWithIds.get(entry.getKey())))
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


