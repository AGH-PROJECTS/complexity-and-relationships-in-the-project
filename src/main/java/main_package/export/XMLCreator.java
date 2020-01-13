package main_package.export;


import com.jamesmurty.utils.XMLBuilder2;

import javax.xml.transform.OutputKeys;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class XMLCreator {
    private XMLBuilder2 exportedXML;
    private Properties properties;
    private static Integer ids = 1;

    public XMLBuilder2 getExportedXML() {
        return exportedXML;
    }
    public Properties getProperties(){
        return properties;
    }
    public XMLCreator() {
        exportedXML = XMLBuilder2.create("Project")
                .a("DocumentationType", "html")
                .a("ExporterVersion", "12.2")
                .a("Name", "untitled")
                .a("UmlVersion", "2.x")
                .a("Xml_structure", "simple")
                .e("Models");
        properties = new Properties();
        properties.put(OutputKeys.METHOD, "xml");
        properties.put(OutputKeys.INDENT, "yes");
        properties.put("{http://xml.apache.org/xslt}indent-amount", "2");
    }

    public void addElements(Map<String, Map<String, AtomicInteger>> structure, Map<String, Integer> info) {

        //Loop for creating ID for every class on graph
        Map<String, Integer> tmpIDMap = new HashMap();

        for (Map.Entry<String, Integer> entry : info.entrySet()) {
            tmpIDMap.put(entry.getKey(), ids);
            ids++;
        }


        for (Map.Entry<String, Integer> entry: info.entrySet()) {
            exportedXML.e("Class")
                    .a("Id", Integer.toString(tmpIDMap.get(entry.getKey())))
                    .a("Name", entry.getKey())
                    .up();
        }

        structure.forEach((k, v) -> {
            for (Map.Entry<String, AtomicInteger> entry : v.entrySet()) {
                Integer tmp = tmpIDMap.get(entry.getKey());
                System.out.println(tmp);
                exportedXML.e("Usage")
                        .a("From", Integer.toString(tmpIDMap.get(k)))
                        .a("Id", Integer.toString(ids))
                        .a("To", Integer.toString(tmp))
                        .up();
                ids++;
            }
        });
    }
}


