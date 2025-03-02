package chess;

import java.util.Collection;
import java.util.Objects;
import chess.MovesCalc.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {
    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (ChessPiece) super.clone();
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType piece = board.getPiece(myPosition).getPieceType();
        MovesCalculator calculator = null;
        switch (piece) {
            case BISHOP:
                calculator = new BishopCalculator();
                break;
            case ROOK:
                calculator = new RookCalculator();
                break;
            case QUEEN:
                calculator = new QueenCalculator();
                break;
            case KNIGHT:
                calculator = new KnightCalculator();
                break;
            case KING:
                calculator = new KingCalculator();
                break;
            case PAWN:
                calculator = new PawnCalculator();
                break;
        }
        assert calculator != null;
        return calculator.pieceMoves(board, myPosition);

    }
}
