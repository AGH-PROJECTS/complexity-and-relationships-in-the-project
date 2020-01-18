import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import main_package.model.InformationGenerator;
import main_package.tools.Maintenance;

public class InformationGeneratorTest {

    private static InformationGenerator gen;
    private static Map<String, Map<String, AtomicInteger>> filesRelations;
    private static Map<String, Map<String, AtomicInteger>> methodsRelations;

    @BeforeClass
    public static void init() throws IOException {
        gen = new InformationGenerator(Maintenance.MAIN_PATH);
        methodsRelations = gen.getMethodsDependency();
        filesRelations = gen.getFilesDependency();
    }

    @Test
    public void InformationGenerator_NotThrowIOException() {
        gen = new InformationGenerator(Maintenance.MAIN_PATH);
    }

    @Test
    public void getMethodsRelations_NotNull() throws IOException {
        Assert.assertNotNull(methodsRelations);
    }

    @Test
    public void getMethodsWeights_NotNull() throws IOException {
        Assert.assertNotNull(gen.getMethodsWeights());
    }

    @Test
    public void getPackagesRelations_NotNull() throws IOException {
        Assert.assertNotNull(gen.getPackagesDependency());
    }

    @Test
    public void getPackagesWeights_NotNull() throws IOException {
        Assert.assertNotNull(gen.getPackagesWeights());
    }

    @Test
    public void getFilesRelations_NotNull() throws IOException {
        Assert.assertNotNull(filesRelations);
    }

    @Test
    public void getFilesWeights_NotNull() throws IOException {
        Assert.assertNotNull(gen.getFilesWeights());
    }

}
