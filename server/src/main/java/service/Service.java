package service;

import dataaccess.*;

public class Service {
    public static final UserDAO USER_DAO = new SQLUserDAO();
    public static final AuthDAO AUTH_DAO = new SQLAuthDAO();
    public static final GameDAO GAME_DAO = new SQLGameDAO();
    public static final DAO DAO = new DAO();
}
