package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class BishopCalculator implements MovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = fourDirectionHelper(board, myPosition, 1, 1);
        moves.addAll(fourDirectionHelper(board, myPosition, -1, 1));
        moves.addAll(fourDirectionHelper(board, myPosition, 1, -1));
        moves.addAll(fourDirectionHelper(board, myPosition, -1, -1));
        return moves;
    }

}
