package Handler;

import Result.ErrorResult;
import dataaccess.DataAccessException;
import model.AuthData;
import spark.*;
import service.ClearService;

public class ClearHandler extends Handler {


    public String clear(Request request, Response response) {
        if (clearService.clear()) {
            response.status(200);
        } else {
            response.status(500);
            return toJson(new ErrorResult("Error: unable to clear"));
        }
        return "{}";
    }
}
