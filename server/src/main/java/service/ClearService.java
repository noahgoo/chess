package service;

import dataaccess.MemoryAuthDAO;

public class ClearService extends Service {

    public boolean clear() {
        //clear AuthData
        authDao.clearAuth();
        userDao.clearUser();
        gameDao.clearGame();
        return true;
    }

}
