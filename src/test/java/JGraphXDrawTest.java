import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;
import main_package.tools.Maintenance;

public class JGraphXDrawTest {

    private static JGraphXDraw applet;
    private static InformationGenerator gen;
    private static Map<String, Map<String, AtomicInteger>> filesRelations;
    private static Map<String, Map<String, AtomicInteger>> methodsRelations;

    @BeforeClass
    public static void init() throws IOException {
        applet = new JGraphXDraw();
        gen = new InformationGenerator(Maintenance.MAIN_PATH);
        methodsRelations = gen.getMethodsDependency();
        filesRelations = gen.getFilesDependency();
    }

    @Test
    public void JGraphXDrawTest_NotThrowHeadlessException () {
        try{
        JGraphXDraw applet = new JGraphXDraw();
        }catch (HeadlessException e){
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void createGraphX_1_ThrowNullPointerException(){
        applet.createGraphX(null,null ,null, null);
    }

    /*@Test(expected = NullPointerException.class)
    public void createGraphX_2_ThrowNullPointerException(){
        applet.createGraphX(null,null ,null,null ,null);
    }

    @Test(expected = NullPointerException.class)
    public void createGraphX_3_ThrowNullPointerException(){
        applet.createGraphX(null,null ,null,null ,null ,null ,null);
    }*/

   /* @Test(expected = NullPointerException.class)
    public void createGraph_4_ThrowNullPointerException() {
        applet.createGraphX(methodsRelations, gen.getMethodsWeights(), null);
    }*/

    /*@Test(expected = NullPointerException.class)
    public void createGraph_5_ThrowNullPointerException() {
        applet.createGraphX(methodsRelations, filesRelations,gen.getMethodsWeights(), gen.getFilesWeights(), null);
    }

    @Test(expected = NullPointerException.class)
    public void createGraph_6_ThrowNullPointerException() {
        applet.createGraphX(methodsRelations, filesRelations, gen.getPackagesDependency(), gen.getMethodsWeights(),gen.getFilesWeights(), gen.getPackagesWeights(), null);
    }*/
}
