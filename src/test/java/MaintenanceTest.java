import org.junit.Test;

import main_package.tools.Maintenance;

public class MaintenanceTest {

    @Test
    public void getVersionIdentifier_NotThrowIOException(){
        Maintenance.getVersionIdentifier();
    }
}
