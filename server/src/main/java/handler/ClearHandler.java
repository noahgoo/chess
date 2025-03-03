package handler;

import result.ErrorResult;
import spark.*;

public class ClearHandler extends Handler {


    public String clear(Request request, Response response) {
        if (CLEAR_SERVICE.clear()) {
            response.status(200);
        } else {
            response.status(500);
            return toJson(new ErrorResult("Error: unable to clear"));
        }
        return "{}";
    }
}
