package service;

public class ClearService extends Service {

    public boolean clear() {
        //clear AuthData
        AUTH_DAO.clearAuth();
        USER_DAO.clearUser();
        GAME_DAO.clearGame();
        return true;
    }

}
