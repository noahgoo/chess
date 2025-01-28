package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface movesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
