package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalculator implements MovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = helper(board, myPosition, 1, 1);
        moves.addAll(helper(board, myPosition, -1, 1));
        moves.addAll(helper(board, myPosition, 1, -1));
        moves.addAll(helper(board, myPosition, -1, -1));
        return moves;
    }


    private Collection<ChessMove> helper(ChessBoard board, ChessPosition myPosition, int r, int c) {
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
