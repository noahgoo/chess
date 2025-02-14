package Handler;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;
import Service.LoginService;

public class LoginHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final LoginService loginService = new LoginService();

    public Response login(Request request, Response response) {
        String loginRequest = getBody(request);
        // perform login request
        response = loginService.login(loginRequest);
    }

    // read the Json
    private String getBody(Request request) {
        var body = new Gson().fromJson(request.body(), String.class);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }

    // return login result in a Json
}
