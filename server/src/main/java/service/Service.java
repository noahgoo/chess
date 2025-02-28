package service;

import dataaccess.*;

public class Service {
    public static final UserDAO userDao = new MemoryUserDAO();
    public static final AuthDAO authDao = new MemoryAuthDAO();
    public static final GameDAO gameDao = new MemoryGameDAO();
}
