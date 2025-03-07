package service;

import dataaccess.DataAccessException;

public class ClearService extends Service {

    public boolean clear() {
        //clear AuthData
        try {
            AUTH_DAO.clearAuth();
            USER_DAO.clearUser();
            GAME_DAO.clearGame();
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

}
