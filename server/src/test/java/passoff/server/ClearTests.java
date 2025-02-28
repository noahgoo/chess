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

//    @Test
//    public void validResponse() {
//        ClearHandler clearHandler = new ClearHandler();
//        Response res = clearHandler.clear();
//        Assertions.assertEquals();
//    }

}
