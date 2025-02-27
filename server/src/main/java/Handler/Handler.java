package Handler;

import com.google.gson.Gson;
import service.ClearService;
import service.UserService;
import spark.Request;

public class Handler {
    protected final static ClearService clearService = new ClearService();
    protected final static UserService userService = new UserService();
    final static Gson serializer = new Gson();

    public <T> T createObj(Request request, Class<T> clazz) {
        String body = request.body();
        return serializer.fromJson(body, clazz);
    }

    public <T> String toJson(T obj) {
        return serializer.toJson(obj);
    }
}
