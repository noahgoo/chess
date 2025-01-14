package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
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
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
         return pieceType;
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
        if (piece.equals(PieceType.PAWN)) {
            Collection<ChessPosition> potentialMoves = PawnMoves(board, myPosition);
            Collection<ChessMove> validMoves = new ArrayList<>();
            for (ChessPosition potentialMove : potentialMoves) {
                validMoves.add(new ChessMove(myPosition, potentialMove, null));
            }
            return validMoves;
        }
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessPosition> PawnMoves(ChessBoard board, ChessPosition myPosition) {
        // create an array for possible moves and determine if piece is white or black
        Collection<ChessPosition> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        int movement;
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {

        }
        if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) {
            moves.add(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()));

        } else if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1 )) != null) {
            moves.add(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1 ));
        }
        return moves;
    }

    {

    }
}
