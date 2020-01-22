import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import main_package.model.InformationGenerator;
import main_package.tools.Maintenance;
import main_package.tools.RevisionDifference;

public class RevisionDifferenceTest {
    private static RevisionDifference revisionDifference;
    private static InformationGenerator gen;

    @BeforeClass
    public static void init() {
        gen = new InformationGenerator(Maintenance.MAIN_PATH);
    }

    @Test(expected = NullPointerException.class)
    public void RevisionDifference_NotThrowNullPointerException() {
        RevisionDifference rd = new RevisionDifference(null);
    }

    @Test
    public void RevisionDifference_Test() {
        revisionDifference = new RevisionDifference("https://github.com/dawidkruczek/projectIO.git");
        gen = new InformationGenerator(revisionDifference.PATH);
    }

    @Test
    public void getRepository_Test() {
        try {
            revisionDifference.getRepository();
            revisionDifference.goToPreviousMerge();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void findDifferences_NotThrowNullPointerException() {
        revisionDifference.findDifferences(gen.getPackagesWeights(), gen.getPackagesWeights());
    }

    @Test
    public void findDifferences2_NotThrowNullPointerException() {
        revisionDifference.findDifferences2(gen.getFilesDependency(), gen.getMethodsDependency());
    }

    @Test
    public void findDifferences3_NotThrowNullPointerException() {
        revisionDifference.findDifferences3(gen.getFilesMethodsDependency(), gen.getFilesMethodsDependency());
    }

    @Test(expected = NullPointerException.class)
    public void findDifferences_ThrowNullPointerException() {
        revisionDifference.findDifferences(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void findDifferences2_ThrowNullPointerException() {
        revisionDifference.findDifferences2(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void findDifferences3_ThrowNullPointerException() {
        revisionDifference.findDifferences3(null, null);
    }
}
