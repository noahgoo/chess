package ui;

import java.io.PrintStream;

public class Help {

    public void printChessMenuHelp(PrintStream out) {
        out.println(""" 
                This is the Chess Menu!
                Enter "1" to create a new game with given GAMENAME
                Enter "2" to list all existing games
                Enter "3" to join a game with a given GAMEID
                Enter "4" to observe a game with a given GAMEID
                Enter "5" to logout
                Enter "7" to quit
                """);
    }

    public void printGameHelp(PrintStream out) {
        out.println("""
                This is the Game Menu!
                Enter "2" to redraw the Chessboard on your screen.
                Enter "3" to leave the game.
                Enter "4" to make a move with a piece.
                Enter "5" to resign and lose the game.
                Enter "6" to highlight legal moves for a piece.
                """);
    }

    public void printPreHelp(PrintStream out) {
        out.println(""" 
                This is the Chess Pre-Login Menu!
                Enter "1" to register a new user with a given USERNAME, PASSWORD, and EMAIL
                Enter "2" to login an existing user with a given USERNAME and PASSWORD
                Enter "4" to quit
                """);
    }
}
