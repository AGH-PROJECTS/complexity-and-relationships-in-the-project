import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InformationGeneratorTest.class,
        ExportToXMLTest.class,
        JGraphXDrawTest.class,
        MaintenanceTest.class,
        MainTest.class
})

public class AllTests {

}
