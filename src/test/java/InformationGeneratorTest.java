import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import main_package.model.InformationGenerator;

public class InformationGeneratorTest {

    private static InformationGenerator gen;
    private static Map<String, Map<String, Integer>> filesRelations;
    private static Map<String, Map<String, Integer>> methodsRelations;
    private static Map<String, Map<String, Integer>> packagesRelations;
    private static Map<String, Integer> filesWeights;
    private static Map<String, Integer> methodsWeights;
    private static Map<String, Integer> packagesWeights;

    @BeforeClass
    public static void init() throws IOException {
        gen = new InformationGenerator();
        methodsRelations =  gen.getMethodsRelations();
        methodsWeights = gen.getMethodsWeights();
        packagesRelations = gen.getPackagesRelations();
        packagesWeights = gen.getPackagesWeights();
        filesRelations = gen.getFilesRelations();
        filesWeights = gen.getFilesWeights();
    }

    @Test
    public void InformationGenerator_NotThrowIOException() {
        try {
            gen = new InformationGenerator();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getMethodsRelations_NotNull() throws IOException {
        Assert.assertNotNull(methodsRelations);
    }

    @Test
    public void getMethodsWeights_NotNull() throws IOException {
        Assert.assertNotNull(methodsWeights);
    }

    @Test
    public void getPackagesRelations_NotNull() throws IOException {
        Assert.assertNotNull(packagesRelations);
    }

    @Test
    public void getPackagesWeights_NotNull() throws IOException {
        Assert.assertNotNull(packagesWeights);
    }

    @Test
    public void getFilesRelations_NotNull() throws IOException {
        Assert.assertNotNull(filesRelations);
    }

    @Test
    public void getFilesWeights_NotNull() throws IOException {
        Assert.assertNotNull(filesWeights);
    }

}
