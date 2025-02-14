package Service;

import spark.*;
import DAO.*;

public class LoginService {
    private static final UserDAO userDao = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();


    public Response login(String loginRequest) {
        // get user
        String user = userDao.getUser(loginRequest);
        // create AuthData
        String authToken = authDAO.createAuth();
        Response loginResult = new Response()
    }
}
