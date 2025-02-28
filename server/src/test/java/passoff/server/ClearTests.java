package passoff.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearService;

public class ClearTests {

    @Test
    public void clearDAO() {
        ClearService service = new ClearService();
        Assertions.assertTrue(service.clear());
    }
}
