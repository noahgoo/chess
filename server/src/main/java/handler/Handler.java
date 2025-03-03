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
    protected final static ClearService CLEAR_SERVICE = new ClearService();
    protected final static UserService USER_SERVICE = new UserService();
    protected final static GameService GAME_SERVICE = new GameService();
    final static Gson SERIALIZER = new Gson();

    public <T> T createObj(Request request, Class<T> clazz) {
        String body = request.body();
        return SERIALIZER.fromJson(body, clazz);
    }

    public <T> String toJson(T obj) {
        return SERIALIZER.toJson(obj);
    }

    public AuthData checkAuth(Request request) throws DataAccessException {
        AuthData authData = Service.AUTH_DAO.getAuth(request.headers("authorization"));
        if (authData!=null) {
            return authData;
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
