package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface MovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean checkSquare(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        int row = position.getRow();
        int col = position.getColumn();
        if (row<9&&row>0&&col>0&&col<9) {
            if (board.getPiece(position)==null) {
                return true;
            } else {
                return board.getPiece(position).getTeamColor()!=color;
            }
        }
        return false;
    }
}
