package service;

import dataaccess.*;

public class Service {
    public static final UserDAO USER_DAO = new MemoryUserDAO();
    public static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    public static final GameDAO GAME_DAO = new MemoryGameDAO();
}
