package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import service.ClearService;
import service.GameService;
import service.Service;
import service.UserService;
import spark.Request;

public class Handler {
    protected final static ClearService clearService = new ClearService();
    protected final static UserService userService = new UserService();
    protected final static GameService gameService = new GameService();
    final static Gson serializer = new Gson();

    public <T> T createObj(Request request, Class<T> clazz) {
        String body = request.body();
        return serializer.fromJson(body, clazz);
    }

    public <T> String toJson(T obj) {
        return serializer.toJson(obj);
    }

    public AuthData checkAuth(Request request) throws DataAccessException {
        AuthData authData = Service.authDao.getAuth(request.headers("authorization"));
        if (authData!=null) {
            return authData;
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
