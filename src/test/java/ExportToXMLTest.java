//import main_package.export.ExportToXML;
//import main_package.model.InformationGenerator;
//import main_package.tools.Maintenance;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class ExportToXMLTest {
//    private static InformationGenerator gen;
//    private static Map<String, Map<String, AtomicInteger>> filesRelations;
//    private static Map<String, Integer> filesWeights;
//
//    @BeforeClass
//    public static void init() throws IOException {
//        gen = new InformationGenerator(Maintenance.MAIN_PATH);
//        filesWeights = gen.getFilesWeights();
//        filesRelations = gen.getFilesDependency();
//
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void exportToXML_ThrowNullPointerException() {
//        ExportToXML exportToXML = new ExportToXML(null, null, null);
//    }
//
//    @Test
//    public void exportToXML_NotThrowNullPointerException() {
//        List<Map<String, Map<String, AtomicInteger>>> dataRelationsList = new ArrayList<>();
//        dataRelationsList.add(filesRelations);
//        List<Map<String, Integer>> dataWeightsList = new ArrayList<>();
//        dataWeightsList.add(filesWeights);
//        ExportToXML exportToXML = new ExportToXML(dataRelationsList, dataWeightsList, "name");
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void exportToXML1_ThrowNullPointerException() {
//        ExportToXML exportToXML = new ExportToXML(null, "name");
//    }
//
//    @Test
//    public void exportToXML1_NotThrowNullPointerException() {
//        ExportToXML exportToXML = new ExportToXML(gen.getFilesMethodsDependency(), "name");
//    }
//}