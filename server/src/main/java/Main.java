import chess.*;
import server.Server;
import spark.Spark;
import spark.Request;
import spark.Response;
import spark.Route;


public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
        new Server().run(8080);
    }
}