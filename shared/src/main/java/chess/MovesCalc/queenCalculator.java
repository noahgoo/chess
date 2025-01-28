package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class queenCalculator implements movesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves;
        movesCalculator calc = new bishopCalculator();
        movesCalculator calc2 = new rookCalculator();
        moves = calc.pieceMoves(board, myPosition);
        moves.addAll(calc2.pieceMoves(board, myPosition));
        return moves;
    }
}
