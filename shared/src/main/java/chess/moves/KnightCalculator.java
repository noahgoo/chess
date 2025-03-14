package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalculator implements MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        for (int i=-2;i<3;i+=4) {
            for (int j=-1;j<2;j+=2) {
                ChessPosition potential1 = new ChessPosition(row+i,col+j);
                ChessPosition potential2 = new ChessPosition(row+j, col+i);
                if (checkSquare(board, potential1, color)) {
                    moves.add(new ChessMove(myPosition, potential1, null));
                }
                if (checkSquare(board, potential2, color)) {
                    moves.add(new ChessMove(myPosition, potential2, null));
                }
            }
        }

        return moves;
    }


}
