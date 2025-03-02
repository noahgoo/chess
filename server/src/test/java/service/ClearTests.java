package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearTests {

    @Test
    public void clearDAO() {
        ClearService service = new ClearService();
        Assertions.assertTrue(service.clear());
    }
}
