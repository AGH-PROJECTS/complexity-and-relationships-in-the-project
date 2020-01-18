import main_package.export.XMLCreator;
import main_package.model.InformationGenerator;
import main_package.tools.Maintenance;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class XMLCreatorTest {
    private static XMLCreator obj;
    private static InformationGenerator gen;
    private static Map<String, Map<String, AtomicInteger>> filesRelations;
    private static Map<String, Map<String, AtomicInteger>> methodsRelations;

    @BeforeClass
    public static void init() throws IOException {
        obj = new XMLCreator();
        gen = new InformationGenerator(Maintenance.MAIN_PATH);
        methodsRelations = gen.getMethodsDependency();
        filesRelations = gen.getFilesDependency();
    }
    @Test
    public void getExportedXML_NotNull(){
        Assert.assertNotNull(obj.getExportedXML());
    }
    @Test
    public void getProperties_NotNull(){
        Assert.assertNotNull(obj.getProperties());
    }
    @Test(expected = NullPointerException.class)
    public void addElements1_ThrowNullPointerException() {
        obj.addElements(filesRelations, null);
    }
    @Test(expected = NullPointerException.class)
    public void addElements2_ThrowNullPointerException() {
        obj.addElements(null, gen.getFilesWeights());
    }
    @Test(expected = NullPointerException.class)
    public void addElements3_ThrowNullPointerException() {
        Map<String,Map<String,AtomicInteger>> tmp1=new HashMap<>();
        Map<String,Integer> tmp2=new HashMap<>();
        tmp2.put("a",1);
        tmp1.put("b",new HashMap<>());
        tmp1.get("b").put("c", new AtomicInteger(2));
        obj.addElements(tmp1, tmp2);
    }









}