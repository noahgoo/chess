package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalculator implements MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++) {
                if (i!=0||j!=0) {
                    ChessPosition potential = new ChessPosition(row+i,col+j);
                    if (checkSquare(board, potential, color)) {
                        moves.add(new ChessMove(myPosition, potential, null));
                    }
                }
            }
        }

        return moves;
    }

}
