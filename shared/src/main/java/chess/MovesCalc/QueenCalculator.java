package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class QueenCalculator implements MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves;
        MovesCalculator calc = new BishopCalculator();
        MovesCalculator calc2 = new RookCalculator();
        moves = calc.pieceMoves(board, myPosition);
        moves.addAll(calc2.pieceMoves(board, myPosition));
        return moves;
    }
}
