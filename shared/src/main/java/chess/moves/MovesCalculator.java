package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
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

    default Collection<ChessMove> fourDirectionHelper(ChessBoard board, ChessPosition myPosition, int r, int c) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        Collection<ChessMove> directionMoves = new ArrayList<>();
        for (int i=1; row+r*i<9&&row+r*i>0&&col+c*i<9&&col+c*i>0; i++) {
            ChessPosition potential = new ChessPosition(row+r*i, col+c*i);
            if (board.getPiece(potential)==null) {
                directionMoves.add(new ChessMove(myPosition, potential, null));
            } else if (board.getPiece(potential).getTeamColor()!=board.getPiece(myPosition).getTeamColor()) {
                directionMoves.add(new ChessMove(myPosition, potential, null));
                break;
            } else {break;}
        }
        return directionMoves;
    }
}
