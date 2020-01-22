import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import main_package.model.InformationGenerator;
import main_package.model.JGraphXDraw;
import main_package.tools.Maintenance;

public class JGraphXDrawTest {

    private static JGraphXDraw applet;
    private static InformationGenerator gen;

    @BeforeClass
    public static void init() {
        applet = new JGraphXDraw();
        gen = new InformationGenerator(Maintenance.MAIN_PATH);
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

    @Test(expected = NullPointerException.class)
    public void createGraphX_2_ThrowNullPointerException(){
        applet.createGraphX(null,null);
    }

    @Test(expected = NullPointerException.class)
    public void createGraph_3_ThrowNullPointerException() {
        applet.createGraphX(gen.getFilesMethodsDependency(), null);
    }

    @Test(expected = NullPointerException.class)
    public void createGraph_4_ThrowNullPointerException() {
        List<Map<String, Map<String, AtomicInteger>>> relationsList = new ArrayList<>();
        List<Map<String, Integer>> weightsList = new ArrayList<>();
        relationsList.add(gen.getFilesDependency());
        weightsList.add(gen.getFilesWeights());
        applet.createGraphX(relationsList, weightsList, gen.getMethodsComplexity(), null);
    }
}
